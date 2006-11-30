/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/PagedStore.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;

import org.enerj.core.CorruptDatabaseException;
import org.enerj.server.logentry.StoreObjectLogEntry;
import org.enerj.util.RequestProcessor;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;

/** 
 * Deals with the OID list and object storage for PagedObjectServer.
 * This class is thread-safe.<p>
 *
 * Objects are stored on a page in the following format:
 * <pre>
 *     OID[N]---+
 *              |
 *              v
 * | Page Free  | OID | Obj Length | Overflow | Obj Segment |Object    /  /  | 0xFF | 0xFF | ... |
 * | Length (2) | (8) |    (4)     |  Ptr (8) | Length (2)  |Data (N) /  /   |  (1) |  (1) | ... | Page[X]
 * +------------+-----+------------+----------+-------------+---------  -----+------+------+.....+
 *                                        |
 *                    +-------------------+
 *                    |                                       
 *                    v                                        
 * | Page Free  | ... | OID | Obj Length | Overflow | Obj Segment |Object    /  /  | ... |
 * | Length (2) | ... | (8) |    (4)     |  Ptr (8) | Length (2)  |Data (N) /  /   | ... | Page[Y]
 * +------------+.....+-----+------------+----------+-------------+---------  -----+.....+
 *
 * <dl>
 * <dt>Page Free Length</dt> <dd>The number of free bytes on the page. Unsigned short.</dd>
 * <dt>OID</dt> <dd>The Object ID of the object pointed to by OID[N]. This must match the requested OID. 
 *      Because the length is a signed value, but always positive, the first byte is never 0xFF (the free
 *      byte indicator).  
 *      Signed long.</dd>
 * <dt>Obj Length</dt> <dd>The entire
 *      length of this object, not just the length on this page. 
 *      Signed int. Note this limits the size of a single object to 2GB. </dd>
 *
 * <dt>Overflow Ptr</dt> <dd>Offset into an overflow page where the data for the object continues. 
 *      If the object does not overflow to another page, this is PageServer.NULL_OFFSET. Signed long. </dd>
 * <dt>Obj Segment Length</dt> <dd>Length of the segment of the object on this page. Unsigned short.</dd>
 * <dt>Object Data</dt> <dd>The variable length object data.  Variable number of bytes.</dd>
 * <dt>0xFF</dt> <dd>A free byte on the page.</dd>
 * </dl>
 * </pre>
 *
 * On the overflow page, the same fields are repeated.
 *
 * @version $Id: PagedStore.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see PagedObjectServer
 */
class PagedStore
{
    private static final int PAGE_FREE_LENGTH_SIZE = 2;
    private static final int OID_SIZE = 8;
    private static final int OBJ_LENGTH_SIZE = 4;
    private static final int OVERFLOW_PTR_SIZE = 8;
    private static final int OBJ_SEGMENT_LENGTH_SIZE = 2;
    private static final int HEADER_SIZE = OID_SIZE + OBJ_LENGTH_SIZE + 
            OVERFLOW_PTR_SIZE + OBJ_SEGMENT_LENGTH_SIZE;
        
    private PageServer mPageServer;
    /** The StorageProcessor which processes queued requests for the PageServer. */
    private RequestProcessor mStorageProcessor;
    /** The OID list. Only manipulated by the StorageProcessor. */
    private OIDList mOIDList;
    /** The RedoLogServer from which to load serialized objects, if necessary. */
    private RedoLogServer mRedoLogServer;
    /** PagedObjectServer - used to report storage errors and compelete store requests. */
    private PagedObjectServer mObjectServer;

    /** Page size from mPageServer. */
    private int mPageSize;

    /** A page which has been partially used . Used while storing and freeing objects. */
    private long mRemainingPagePtr = PageServer.NULL_OFFSET;
    /** Offset into the page at mRemainingPagePtr where the rest of the page is free. */
    private int mRemainingPageOffset = 0;
    /** A page buffer for the StorageThread to work with. */
    private ByteBuffer mStoragePageBuffer;
    /** A page buffer full of 0xff bytes. Used for free space within a page. */
    private ByteBuffer mFreeBuffer;

    

    /**
     * Construct a PagedStore.
     *
     * @param someProperties the properties that will be used to connect to the PageServer.<br>
     * <i>PagedObjectServer.PageServerClass</i> - class name of the PageServer plug-in.
     * 
     * @param aRedoLogServer the RedoLogServer from which to load serialized objects,
     *  if necessary.
     * @param anObjectServer the PagedObjectServer using this PagedStore.
     * @param shouldForceOpen if true, volume will be opened even if it was not previously closed
     *  properly. This is primarily used for recovery. The volume will be marked as
     *  "closed properly" when it is closed.
     *
     * @throws ODMGException if an error occurs.
     */
    PagedStore(Properties someProperties, RedoLogServer aRedoLogServer, PagedObjectServer anObjectServer, 
                boolean shouldForceOpen) throws org.odmg.ODMGException
    {
        if (shouldForceOpen) {
            someProperties.put("PageServer.forceOpen", "");
        }
        
        String pageServerClassName = someProperties.getProperty("PagedObjectServer.PageServerClass");
        if (pageServerClassName == null) {
            throw new ODMGException("Missing required parameter: PagedObjectServer.PageServerClass");
        }
        
        mPageServer = (PageServer)PluginHelper.connect(pageServerClassName, someProperties);
        mPageSize = mPageServer.getPageSize();

        mStorageProcessor = new RequestProcessor("PagedStore:StorageProcessor:" + pageServerClassName);
        
        mOIDList = new OIDList(mPageServer, mPageServer.getLogicalFirstPageOffset() );
        mStoragePageBuffer = ByteBuffer.allocate(mPageSize);
        mFreeBuffer = ByteBuffer.allocate(mPageSize);
        Arrays.fill(mFreeBuffer.array(), (byte)0xff);
        
        mRedoLogServer = aRedoLogServer;
        mObjectServer = anObjectServer;
    }
    

    /**
     * Cleans up and disconects from the PageServer.
     *
     * @throws PageServerException if an error occurs.
     */
    void disconnect() throws PageServerException
    {
        if (mStorageProcessor != null) {
            mStorageProcessor.shutdown();
            mStorageProcessor = null;
        }

        if (mOIDList != null) {
            mOIDList.writeHeader();
            mOIDList = null;
        }

        if (mPageServer != null) {
            mPageServer.disconnect();
            mPageServer = null;
        }
    }
    

    /**
     * Helper for storage requests - RequestProcessor.queueRequestAndWait(). Traps non-ODMGExceptions
     * and rewraps them as ODMGException. All exceptions from the request are thrown.
     *
     * @throws ODMGException if an error occurs.
     * @throws ODMGRuntimeException  if an error occurs.
     */
    void queueStorageRequestAndWait(RequestProcessor.Request aRequest) throws ODMGException
    {
        queueStorageRequest(aRequest);
        waitForStorageRequest(aRequest);
    }
    

    /**
     * Helper for storage requests - RequestProcessor.queueRequest(). Traps non-ODMGExceptions
     * and rewraps them as ODMGException. All exceptions from the request are thrown.
     * Does not wait for the request to complete.
     *
     * @throws ODMGException if an error occurs.
     * @throws ODMGRuntimeException  if an error occurs.
     */
    void queueStorageRequest(RequestProcessor.Request aRequest) throws ODMGException
    {
        try {
            mStorageProcessor.queueRequest(aRequest);
        }
        catch (Exception e) {
            throw new ODMGException("Request failed while queuing:" + e.toString(), e);
        }
    }
    

    /**
     * Helper for storage requests - RequestProcessor.waitForRequest(). Traps non-ODMGExceptions
     * and rewraps them as ODMGException. All exceptions from the request are thrown.
     *
     * @throws ODMGException if an error occurs.
     * @throws ODMGRuntimeException  if an error occurs.
     */
    void waitForStorageRequest(RequestProcessor.Request aRequest) throws ODMGException
    {
        try {
            mStorageProcessor.waitForRequest(aRequest);
        }
        catch (Exception e) {
            throw new ODMGException("Request failed while waiting for completion:" + e.toString(), e);
        }
        
        Exception requestException = aRequest.getException();
        if (requestException != null) {
            if (requestException instanceof ODMGException) {
                throw (ODMGException)requestException;
            }
            else if (requestException instanceof ODMGRuntimeException) {
                throw (ODMGRuntimeException)requestException;
            }
            
            throw new ODMGException(requestException.toString(), requestException);
        }
    }


    /**
     * Helper for ByteBuffer to get a two byte unsigned short.
     * 
     * @param aByteBuffer a ByteBuffer to read from.
     * @param aPosition the position in aByteBuffer.
     *
     * @return an int representing the unsigned short value.
     */
    private static int getUnsignedShort(ByteBuffer aByteBuffer, int aPosition)
    {
        int highByte = aByteBuffer.get(aPosition) & 0xff;
        int lowByte = aByteBuffer.get(aPosition + 1) & 0xff;
        return (highByte << 8) | lowByte;
    }
    

    /**
     * Helper for ByteBuffer to put a two byte unsigned short.
     * 
     * @param aByteBuffer a ByteBuffer to write to.
     * @param aPosition the position in aByteBuffer.
     * @param aValue an int representing the unsigned short value.
     */
    private static void putUnsignedShort(ByteBuffer aByteBuffer, int aPosition, int aValue)
    {
        aByteBuffer.put(aPosition, (byte)((aValue >> 8) & 0xff));
        aByteBuffer.put(aPosition + 1, (byte)(aValue & 0xff));
    }
    

    /**
     * Given a storage pointer, get the offset to the start of the page.
     *
     * @param aPtr the storage pointer.
     * 
     * @return an offset to the start of the page.
     */
    private final long getPageFromPtr(long aPtr)
    {
        return (aPtr / mPageSize) * mPageSize;
    }
    

    /**
     * Given a storage pointer, get the offset within the page.
     *
     * @param aPtr the storage pointer.
     * 
     * @return an offset within the page.
     */
    private final int getOffsetFromPtr(long aPtr)
    {
        return (int)(aPtr % mPageSize);
    }
    

    /**
     * Gets the "Page Free Length" field.
     * Note: This uses mStoragePageBuffer.
     *
     * @param aPagePtr the pointer to the beginning of the page.
     *
     * @throws PageServerException if a storage error occurs.
     * @throws ODMGRuntimeException if a database storage integrity error is encountered
     */
    private int getPageFreeLength(long aPagePtr) throws PageServerException
    {
        mStoragePageBuffer.position(0);
        mStoragePageBuffer.limit(PAGE_FREE_LENGTH_SIZE);
        mPageServer.loadPage(mStoragePageBuffer, aPagePtr, 0);

        int freeLength = getUnsignedShort(mStoragePageBuffer, 0);
        if (freeLength < 0 || freeLength >= (mPageSize - PAGE_FREE_LENGTH_SIZE)) {
            throw new CorruptDatabaseException("Invalid page free length " + freeLength + 
                " at position " + aPagePtr);
        }
        
        return freeLength;
    }
        

    /**
     * Sets the "Page Free Length" field. If page free length would result in the
     * entire page being free, the page is freed from the PageServer.
     * Page may be compacted if it reaches a certain free space threshold.
     * Note: This uses mStoragePageBuffer.
     *
     * @param aPagePtr the pointer to the beginning of the page.
     * @param aFreeLength the new page free length.
     *
     * @throws PageServerException if a storage error occurs.
     * @throws ODMGRuntimeException if a database storage integrity error is encountered
     */
    private void setPageFreeLength(long aPagePtr, int aFreeLength) throws PageServerException
    {
        if (aFreeLength == (mPageSize - PAGE_FREE_LENGTH_SIZE)) {
            // Page is now empty - free it from the PageServer.
            mPageServer.freePage(aPagePtr);
            return;
        }
        
        if (aFreeLength < 0 || aFreeLength > (mPageSize - PAGE_FREE_LENGTH_SIZE)) {
            throw new ODMGRuntimeException("New Page Free Length would cause free length to go out of range: " + 
                aFreeLength + " at position " + aPagePtr);
        }
        
        mStoragePageBuffer.position(0);
        mStoragePageBuffer.limit(PAGE_FREE_LENGTH_SIZE);
        putUnsignedShort(mStoragePageBuffer, 0, aFreeLength);
        mPageServer.storePage(mStoragePageBuffer, aPagePtr, 0);
        //  TODO  compact if threshold reached.
        //  TODO  set mRemainingPagePtr if not set? -- do this in compact.
    }
    

    /**
     * Change the "Page Free Length" for the specified page by a delta amount.
     * Note: This uses mStoragePageBuffer.
     *
     * @param aPagePtr the pointer to the beginning of the page.
     * @param aDeltaValue the amount by which to increase or decrease the free length.
     *
     * @throws PageServerException if a storage error occurs.
     * @throws ODMGRuntimeException if a database storage integrity error is encountered
     */
    private void changePageFreeLength(long aPagePtr, int aDeltaValue) throws PageServerException
    {
        // Get current free length.
        int freeLength = getPageFreeLength(aPagePtr);
        setPageFreeLength(aPagePtr, freeLength + aDeltaValue);
    }
    

    /**
     * Frees the object pointed to by anObjectPtr. All segments starting 
     * from anObjectPtr to the end of the object are freed. This does <em>not</em>
     * change mOIDList.
     * Note: This uses mStoragePageBuffer.
     *
     * @param anOID the OID for the object. This is used to perform integrity checks.
     * @param anObjectPtr a pointer to an ObjectHeader. This does not have to
     *  point to the first segment of the object. However, all segments, starting with this
     *  one, in the overflow chain will be freed.
     *
     * @throws PageServerException if a storage error occurs.
     * @throws ODMGRuntimeException if a database storage integrity error is encountered
     */
    private void freeObject(long anOID, long anObjectPtr) throws PageServerException
    {
        ObjectHeader hdr = new ObjectHeader();
        int objectLength = -1;
        while (anObjectPtr != PageServer.NULL_OFFSET) {
            hdr.read(anObjectPtr);
            if (objectLength == -1) {
                objectLength = hdr.mObjectLength;
            }

            hdr.validateOIDAndLength(anOID, objectLength);
            
            // Freeing the object and its header.
            int freeLength = hdr.mObjectSegmentLength + HEADER_SIZE;
            mFreeBuffer.position(0);
            mFreeBuffer.limit(freeLength);
            mPageServer.storePage(mFreeBuffer, hdr.mPagePtr, hdr.mHeaderOffset);
            
            // Bump the page free length.
            changePageFreeLength(hdr.mPagePtr, freeLength);
            
            anObjectPtr = hdr.mOverflowPtr;
        }
    }
    

    /**
     * Called from StorageThread to store an object.
     *
     * @param aCID a Class Id.
     * @param anOID an Object ID.
     * @param aSerializedObject the serialized bytes for the object.
     *
     * @throws PageServerException if a storage error occurs.
     * @throws ODMGRuntimeException if a database storage integrity error is encountered
     */
    private synchronized void processStoreRequest(long aCID, long anOID, byte[] aSerializedObject) 
        throws PageServerException
    {
        long currentObjectPtr = mOIDList.getObjectOffsetForOID(anOID);
        ByteBuffer objectBuffer = ByteBuffer.wrap(aSerializedObject);
        
        // Are we replacing the object?
        if (currentObjectPtr != PageServer.NULL_OFFSET) {
            ObjectHeader hdr = new ObjectHeader(currentObjectPtr);
            if (hdr.mOID != anOID) {
                throw new CorruptDatabaseException("Possible media failure. Expected OID " + 
                    anOID + " but found OID " + hdr.mOID + " at position " + currentObjectPtr);
            }

            // Can we replace the current object on the same page or pages?
            // Three conditions - 1) exact same size so just replace the bytes; 2) smaller size so we need to 
            // replace and mark remaining bytes free; or 3) the object is now larger so we
            // need to free and allocate object as new. Note that in any of these
            // cases the object might span multiple pages.
            if (aSerializedObject.length <= hdr.mObjectLength) {
                // Case 1: Same length - simply replace the bytes.
                // Case 2: New object is smaller. Reuse space and free the remainder.
                // Same code handles both cases.
                
                // Set OID info now in case we compact the page later.
                mOIDList.setOIDInfo(anOID, currentObjectPtr, aCID);

                int lengthWritten = 0;
                int lengthLeft = aSerializedObject.length;
                int lastSegmentLengthWritten = 0;
                int prevLengthLeft = hdr.mObjectLength;
                int prevObjectLength = hdr.mObjectLength;
                // We use do..while (lengthLeft > 0) because we at least want the object
                // header, even if the length of the serialized object is zero.
                do {
                    int length = (hdr.mObjectSegmentLength > lengthLeft ? lengthLeft : hdr.mObjectSegmentLength);
                    
                    // Update header if necessary.
                    if (aSerializedObject.length != hdr.mObjectLength) {
                        hdr.mObjectLength = aSerializedObject.length;
                        // If this is the last segment, clear the overflow pointer.
                        if (length == lengthLeft) {
                            // Free any extra overflow segments before clearing the pointer.
                            if (hdr.mOverflowPtr != PageServer.NULL_OFFSET) {
                                freeObject(anOID, hdr.mOverflowPtr);
                            }

                            hdr.mOverflowPtr = 0;
                            hdr.mObjectSegmentLength = length;
                        }
                        
                        hdr.write(hdr.mPagePtr + hdr.mHeaderOffset);
                    }
                    
                    objectBuffer.position(lengthWritten);
                    objectBuffer.limit(lengthWritten + length);
                    mPageServer.storePage(objectBuffer, hdr.mPagePtr, hdr.mObjectOffset);
                    lastSegmentLengthWritten = length;
                    lengthWritten += length;
                    lengthLeft -= length;
                    prevLengthLeft -= length;

                    // Only read header for next segment.
                    if (hdr.mOverflowPtr != PageServer.NULL_OFFSET) {
                        hdr.read(hdr.mOverflowPtr);
                        hdr.validateOIDAndLength(anOID, prevObjectLength);
                    }
                }
                while (lengthLeft > 0);
                
                // Did we have some remaining space? If so, free it.
                if (prevLengthLeft > 0) {
                    // Calculate the number of bytes to free at the end of this segment.
                    int remainingSegmentLength = hdr.mObjectSegmentLength - lastSegmentLengthWritten;
                    
                    // Free remainder on this page.
                    mFreeBuffer.position(0);
                    mFreeBuffer.limit(remainingSegmentLength);
                    mPageServer.storePage(mFreeBuffer, hdr.mPagePtr, hdr.mObjectOffset + lastSegmentLengthWritten);
                    changePageFreeLength(hdr.mPagePtr, remainingSegmentLength);
                }

                return;
            }
            else {   // aSerializedObject.length > hdr.mObjectLength
                // Case 3: Object is growing. Just free the current object and allocate as if it
                // were new.
                freeObject(anOID, currentObjectPtr);
            }
        }
        // else this is a new object.

        currentObjectPtr = PageServer.NULL_OFFSET;
        int lengthWritten = 0;
        int lengthLeft = aSerializedObject.length;
        long previousObjectPtr = PageServer.NULL_OFFSET;
        ObjectHeader hdr = new ObjectHeader();
        hdr.mOID = anOID;
        hdr.mObjectLength = aSerializedObject.length;
        
        // We use do..while (lengthLeft > 0) because we at least want the object
        // header, even if the length of the serialized object is zero.
        do {
            // Need to allocate a new page if we don't have a partial page left over
            // or if the remaining part of the partial page does not have enough space.
            // HEADER_SIZE * 2 = don't just allocate enough space for the header and
            // a couple of bytes and then overflow. If that would happen, just allocate
            // a fresh page.
            int lengthNeeded = HEADER_SIZE + lengthLeft;
            int minSpaceNeeded = Math.min(HEADER_SIZE * 2, lengthNeeded);
            int spaceLeft = mPageSize - mRemainingPageOffset;
            if (mRemainingPagePtr == PageServer.NULL_OFFSET || spaceLeft < minSpaceNeeded) {
                mRemainingPagePtr = mPageServer.allocatePage();
                mRemainingPageOffset = PAGE_FREE_LENGTH_SIZE;
                spaceLeft = mPageSize - mRemainingPageOffset;
            }
            
            if (currentObjectPtr == PageServer.NULL_OFFSET) {
                currentObjectPtr = mRemainingPagePtr + mRemainingPageOffset;
            }
            else {
                // Update overflow pointer on previous segment.
                hdr.mOverflowPtr = mRemainingPagePtr + mRemainingPageOffset;
                hdr.write(previousObjectPtr);
            }

            previousObjectPtr = mRemainingPagePtr + mRemainingPageOffset;
            
            // Init the header now, but don't write it yet. It gets written with the
            // overflow ptr on the next iteration, or after we break out of the loop.
            hdr.mOverflowPtr = PageServer.NULL_OFFSET;
            hdr.mObjectSegmentLength = (spaceLeft > lengthNeeded ? lengthNeeded : spaceLeft) - HEADER_SIZE;

            objectBuffer.position(lengthWritten);
            objectBuffer.limit(lengthWritten + hdr.mObjectSegmentLength);
            mPageServer.storePage(objectBuffer, mRemainingPagePtr, mRemainingPageOffset + HEADER_SIZE);
            lengthWritten += hdr.mObjectSegmentLength;
            lengthLeft -= hdr.mObjectSegmentLength;

            // Mark rest of page as free and set the free length.
            mRemainingPageOffset += HEADER_SIZE + hdr.mObjectSegmentLength;
            spaceLeft = mPageSize - mRemainingPageOffset;
            if (spaceLeft > 0) {
                mStoragePageBuffer.position(0);
                mStoragePageBuffer.limit(spaceLeft);
                Arrays.fill(mStoragePageBuffer.array(), 0, spaceLeft, (byte)0xff);
                mPageServer.storePage(mStoragePageBuffer, mRemainingPagePtr, mRemainingPageOffset);
            }
            
            setPageFreeLength(mRemainingPagePtr, spaceLeft);
        }
        while (lengthLeft > 0);

        // Update overflow pointer on previous segment.
        hdr.mOverflowPtr = PageServer.NULL_OFFSET;
        hdr.write(previousObjectPtr);

        mOIDList.setOIDInfo(anOID, currentObjectPtr, aCID);
    }


    /**
     * Load an object.
     *
     * @param anOID an Object ID.
     *
     * @return the serialized bytes for the object.
     *
     * @throws ODMGRuntimeException if a database storage integrity error is encountered
     */
    public synchronized byte[] loadObject(long anOID) 
    {
        try {
            long objectPtr = mOIDList.getObjectOffsetForOID(anOID);

            if (objectPtr == PageServer.NULL_OFFSET) {
                // This could be a problem with the API, not necessarily a corrupt database.
                throw new ODMGRuntimeException("Requested to load OID " + anOID + ", but it points to nothing.");
            }

            if (objectPtr < 0) {
                throw new CorruptDatabaseException("Possible media failure. Object pointer for OID " + anOID + " is negative.");
            }

            byte[] serializedObject = null;
            int lengthRead = 0;
            int objectLength = 0;
            ObjectHeader hdr = new ObjectHeader();
            ByteBuffer objectBuffer = null;

            while (objectPtr != PageServer.NULL_OFFSET) {
                // Read the header.
                hdr.read(objectPtr);
                if (serializedObject == null) {
                    objectLength = hdr.mObjectLength;
                    hdr.validateOIDAndLength(anOID, objectLength);
                    serializedObject = new byte[objectLength];
                    objectBuffer = ByteBuffer.wrap(serializedObject);
                }
                else {
                    hdr.validateOIDAndLength(anOID, objectLength);
                }

                // Do some basic integrity checks.
                if (hdr.mObjectSegmentLength > objectLength || hdr.mObjectSegmentLength < 0) {
                    throw new CorruptDatabaseException("Possible media failure. Expected object segment length to be less than object length of " + objectLength +
                        " at position " + objectPtr + ", but got a segment length of " + hdr.mObjectSegmentLength);
                }

                if ((lengthRead + hdr.mObjectSegmentLength) > objectLength) {
                    throw new CorruptDatabaseException("Possible media failure. Expected object segment length of " + hdr.mObjectSegmentLength + "would exceed object length of " + objectLength +
                        " at position " + objectPtr);
                }

                if (hdr.mOverflowPtr < 0) {
                    throw new CorruptDatabaseException("Possible media failure. Negative overflow pointer at position " + objectPtr);
                }

                // Load this segment.
                objectBuffer.position(lengthRead);
                objectBuffer.limit(lengthRead + hdr.mObjectSegmentLength);
                mPageServer.loadPage(objectBuffer, hdr.mPagePtr, hdr.mObjectOffset);

                lengthRead += hdr.mObjectSegmentLength;
                objectPtr = hdr.mOverflowPtr;
            }

            return serializedObject;
        }
        catch (ODMGRuntimeException e) {
            throw e;    // Don't wrap.
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Error loading object for OID " + anOID + ": " + e, e);
        }
    }
    

    /**
     * Get the CID corresponding to anOID.
     *
     * @param anOID an Object ID.
     *
     * @return the CID, or ObjectServer.NULL_CID if the object does not exist.
     *
     * @throws ODMGRuntimeException if a database storage integrity error is encountered
     */
    public synchronized long getCIDForOID(long anOID)
    {
        try {
            return mOIDList.getCIDforOID(anOID);
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Error getting CID for OID " + anOID + ": " + e, e);
        }
    }
    

    /**
     * Get a block of new OIDs.
     * 
     * @param anOIDCount the number of OIDs to get.
     *
     * @return an array of OIDs.
     *
     * @throws ODMGRuntimeException if a database storage integrity error is encountered
     */
    public synchronized long[] getNewOIDBlock(int anOIDCount)
    {
        try {
            return mOIDList.allocateOIDBlock(anOIDCount);
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Error allocating OID Block: " + e, e);
        }
    }
    

    /**
     * Ensures that a OID has been allocated. If it has not been allocated,
     * it will be upon return. This is used primarily for log-based recovery
     * when we can't be certain if the OID header was rewritten after an allocateOIDBlock.
     *
     * @param anOID the OID.
     *
     * @throws ODMGRuntimeException if a database storage integrity error is encountered
     */
    public synchronized void ensureOIDAllocated(long anOID)
    {
        try {
            mOIDList.ensureOIDAllocated(anOID);
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Error ensuring allocation of OID " + anOID + ':' + e, e);
        }
    }
    


    /**
     * Object header as stored in a page.
     */
    private final class ObjectHeader
    {
        /** OID as stored on the header. */
        long mOID;
        /** The full length of the object. */
        int mObjectLength;
        /** The length of this segment on this page. */
        int mObjectSegmentLength;
        /** If this is not PageServer.NULL_OFFSET, this is the pointer to the next segment. */
        long mOverflowPtr;

        /** Pointer to beginning of page. */
        long mPagePtr;
        /** Offset in page to start of header. */
        int mHeaderOffset;
        /** Offset in page to start of serialized object. */
        int mObjectOffset;


        /** 
         * Constructs an empty. All values are zero.
         */
        ObjectHeader()
        {
        }
        

        /** 
         * Constructs a new header that has never been stored.
         */
        ObjectHeader(long anOID, int anObjectLength, long anOverflowPtr, int anObjectSegmentLength)
        {
            mOID = anOID;
            mObjectLength = anObjectLength;
            mOverflowPtr = anOverflowPtr;
            mObjectSegmentLength = anObjectSegmentLength;
        }


        /** 
         * Constructs a new header from a storage pointer to an Object. Header is
         * read from storage.
         * Note: Uses mStoragePageBuffer.
         */
        ObjectHeader(long anObjectPtr) throws PageServerException
        {
            read(anObjectPtr);
        }


        /**
         * Reads an Object header given a storage pointer.
         * Note: Uses mStoragePageBuffer.
         */
        void read(long anObjectPtr)  throws PageServerException
        {
            mPagePtr = getPageFromPtr(anObjectPtr);
            mHeaderOffset  = getOffsetFromPtr(anObjectPtr);

            mStoragePageBuffer.position(0);
            mStoragePageBuffer.limit(HEADER_SIZE);
            mPageServer.loadPage(mStoragePageBuffer, mPagePtr, mHeaderOffset);

            mOID = mStoragePageBuffer.getLong(0);
            mObjectLength = mStoragePageBuffer.getInt(OID_SIZE);
            mOverflowPtr = mStoragePageBuffer.getLong(OID_SIZE + OBJ_LENGTH_SIZE);
            mObjectSegmentLength = getUnsignedShort(mStoragePageBuffer, OID_SIZE + OBJ_LENGTH_SIZE + OVERFLOW_PTR_SIZE);

            mObjectOffset = mHeaderOffset + HEADER_SIZE;
        }


        /**
         * Writes an Object header given a storage pointer.
         * Note: Uses mStoragePageBuffer.
         */
        void write(long anObjectPtr)  throws PageServerException
        {
            mPagePtr = getPageFromPtr(anObjectPtr);
            mHeaderOffset  = getOffsetFromPtr(anObjectPtr);

            mStoragePageBuffer.position(0);
            mStoragePageBuffer.limit(HEADER_SIZE);
            mStoragePageBuffer.putLong(0, mOID);
            mStoragePageBuffer.putInt(OID_SIZE, mObjectLength);
            mStoragePageBuffer.putLong(OID_SIZE + OBJ_LENGTH_SIZE, mOverflowPtr);
            putUnsignedShort(mStoragePageBuffer, OID_SIZE + OBJ_LENGTH_SIZE + OVERFLOW_PTR_SIZE, mObjectSegmentLength);
            
            mPageServer.storePage(mStoragePageBuffer, mPagePtr, mHeaderOffset);
        }


        /**
         * Validates an ObjectHeader's OID and Object Length against the given values.
         * Returns normally if they match, otherwise throws.
         *
         * @throws CorruptDatabaseException if the values don't match.
         */
        void validateOIDAndLength(long anOID, int anObjectLength) throws CorruptDatabaseException
        {
            if (mOID != anOID) {
                throw new CorruptDatabaseException("Possible media failure. Expected OID " + 
                    anOID + " but found OID " + mOID + " at position " + mPagePtr);
            }

            if (mObjectLength != anObjectLength || mObjectLength < 0) {
                throw new CorruptDatabaseException("Possible media failure. Expected object length to be " + anObjectLength +
                    " at position " + mPagePtr + ", but got length " + mObjectLength);
            }
        }
    }
    


    /**
     * Abstract Request that represents updates managed a transaction.
     * It supports a double-linked list capability.
     */
    abstract class UpdateRequest extends RequestProcessor.Request
    {
        UpdateRequest mNext = null;
        UpdateRequest mPrev = null;
        /** Position of the log entry for this update. */
        long mLogEntryPosition = -1L;
        long mOID;


        UpdateRequest(long anOID)
        {
            mOID = anOID;
            // We don't usually wait on update requests.
            setMonitored(false);
        }


        public void complete(Exception e) 
        {
            super.complete(e);
            if (e != null && !isMonitored()) {
                mObjectServer.handleStorageException(this, e);
            }
        }
    }
    


    /**
     * Request to store an Object.
     */
    final class StoreObjectRequest extends UpdateRequest
    {
        long mCID; 
        byte[] mSerializedObject;


        StoreObjectRequest(long aCID, long anOID, byte[] aSerializedObject)
        {
            super(anOID);
            mCID = aCID;
            mSerializedObject = aSerializedObject;
        }


        public void run()
        {
            try {
                processStoreRequest(mCID, mOID, resolveSerializedObject() );
                complete(null);
            }
            catch (Exception e) {
                complete(e);
            }
        }
        

        public void complete(Exception e) 
        {
            super.complete(e);
            if (e == null) {
                // Remove store request from update cache.
                mObjectServer.completeStoreObjectRequest(this);
            }
        }
        

        /** 
         * Resolves the bytes for the serialized object, loading them from 
         * the RedoLogServer if necessary.
         *
         * @return the serialized object bytes. The mSerializedObject attribute
         *  is not affected.
         *
         * @throws ODMGException if an error occurs.
         */
        byte[] resolveSerializedObject() throws ODMGException
        {
            // Found an updated version of the object. Use the updated serialized object.
            if (mSerializedObject == null) {
                // Don't have the bytes anymore, resolve serialized bytes from the log.
                StoreObjectLogEntry logEntry = (StoreObjectLogEntry)mRedoLogServer.read(mLogEntryPosition);
                return logEntry.getObjectValue();
            }

            return mSerializedObject;
        }
    }



    /**
     * Request to delete an Object.
     */
    final class DeleteObjectRequest extends UpdateRequest
    {

        DeleteObjectRequest(long anOID)
        {
            super(anOID);
        }


        public void run()
        {
            try {
                //  TODO  - processDeleteRequest(mCID, mOID, mSerializedObject);
                complete(null);
                throw new Exception("Not implemented");
            }
            catch (Exception e) {
                complete(e);
            }
        }
    }



    /**
     * Request to perform the work of a database "end checkpoint". This
     * is put in the queue after committed requests. It causes the
     * dirty pages to be flushed to the database and then calls
     * the object server's "endDatabaseCheckpoint" method (which writes the log entry).
     */
    class EndDatabaseCheckpointRequest extends RequestProcessor.Request
    {

        EndDatabaseCheckpointRequest()
        {
            // We wait for this request to complete.
        }


        public void run()
        {
            try {
                mPageServer.syncAllPages();
                mObjectServer.endDatabaseCheckpoint();
                complete(null);
            }
            catch (Exception e) {
                complete(e);
            }
        }


        public void complete(Exception e) 
        {
            super.complete(e);
            if (e != null && !isMonitored()) {
                mObjectServer.handleStorageException(this, e);
            }
        }
    }
    
}



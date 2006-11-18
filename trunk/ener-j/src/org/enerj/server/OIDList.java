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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/OIDList.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.server;

import java.io.*;
import java.nio.*;
import java.util.*;

import org.odmg.*;

import org.enerj.core.*;

/** 
 * Manages the list of OIDs for PagedObjectServer. Could be used in other ObjectServers as well
 * since it is only dependent on a PageServer. The client must call writeHeader prior'
 * to ending the use of the list.
 * This class is not thread-safe.
 * <p>
 * The PagedObjectServer header page contains:
 * <p><pre>
 * | Num OIDs (8) |  Head Page of OID list (8) |
 * +--------------+----------------------------+
 * </pre><p>
 * The OID list is a linked list of pages formatted as follows:
 * <p><pre>
 * |Next OID | ---       OID[k]           --- | ... OID[k+n] --- |Slack |
 * |Page (8) | Offset to Object (8) | CID (8) |      ....        | (<16)|
 * +---------+----------------------+---------+------------------+------+
 * </pre><p>
 *
 * Next OID Page points to the next page in the OID list, or zero if this is
 * the last node in the list. 
 * If Offset to Object is zero, then this OID is not used. 
 * Otherwise, Offset to Object points to the
 * start of the actual object inside a page.
 * Once allocated, a OID page is never freed (OIDs aren't reused).
 * OID 0 is never used (it's the null OID).
 * Slack is possible unused space on the page, in the range of 0..15 bytes.
 * The amount of slack depends on how evenly we can divide the page for OIDs.
 * <p>
 * Note: If we used a 2K page size, we could store 127 OIDs per page.
 * For one billion OIDs, we need to maintain an in-memory OID page array
 * of 7,874,016 entries, times 8 bytes per entry consumes 62,992,128 bytes
 * of memory (62MB). If the page size were 8K, we could store 511 OIDs per page.
 * For one billion OIDs, the array would be 1,956,948 entries, or 15,655,584 bytes (15MB).
 * The lesson is: for larger databases, use larger page sizes.
 * <p>
 *
 * @version $Id: OIDList.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see PagedObjectServer
 *
 *  TODO  Refcnt may go in the OID list someday, making the number of OIDs per page
 *  even smaller.
 */
public class OIDList
{
    private static final int PTR_SIZE = 8;
    private static final int CID_SIZE = 8;
    private static final int OID_SIZE = PTR_SIZE + CID_SIZE;
    private static final int HDR_SIZE = PTR_SIZE + PTR_SIZE;

    private PageServer mPageServer;
    private int mPageSize;
    /** Offset to the header. */
    private long mHeaderOffset;
    /** The size of the OID list. */
    private long mNumOIDs;
    /** Pointers to each OID page. */
    private long[] mOIDPages;
    /** Number of OIDs that can fit on a page. Maintained as a long to avoid type promotions. */
    private long mOIDsPerPage;
    /** A working buffer that we can use. */
    private ByteBuffer mBuffer;

    //----------------------------------------------------------------------
    /**
     * Constructs an OIDList from a PageServer.
     *
     * @param aPageServer the PageServer where the list is stored.
     * @param aHeaderOffset the offset to the OIDList header.
     *
     * @throws PageServerException if an error occurs.
     */
    public OIDList(PageServer aPageServer, long aHeaderOffset) throws PageServerException
    {
        mPageServer = aPageServer;
        mPageSize = mPageServer.getPageSize();
        mHeaderOffset = aHeaderOffset;
        mBuffer = ByteBuffer.allocate(mPageSize);
        mOIDsPerPage = (mPageSize - PTR_SIZE) / OID_SIZE;
        readHeader();
    }

    //----------------------------------------------------------------------
    /**
     * Reads the list header.
     *
     * @throws PageServerException if an error occurs.
     */
    protected void readHeader() throws PageServerException
    {
        mBuffer.position(0);
        mBuffer.limit(HDR_SIZE);
        mPageServer.loadPage(mBuffer, mHeaderOffset, 0);
        mNumOIDs = mBuffer.getLong(0);
        long head = mBuffer.getLong(PTR_SIZE);

        // We always have at least one OID which is the null OID.
        // We also don't want to return the System OIDs. E.g., SCHEMA_OID (1). So start this 
        // at the first user OID.
        if (mNumOIDs == 0) {
            mNumOIDs = ObjectSerializer.FIRST_USER_OID;
        }

        int numOIDPages = (int)((mNumOIDs + mOIDsPerPage - 1) / mOIDsPerPage);
        // Allocate a few more pages so we don't have to grow the array constantly.
        mOIDPages = new long[numOIDPages + 1000];

        // Load the page offsets into memory.
        long pageOffset = head;
        mBuffer.position(0);
        mBuffer.limit(PTR_SIZE);
        for (int i= 0; pageOffset != 0; ++i) {
            mOIDPages[i] = pageOffset;
            mPageServer.loadPage(mBuffer, pageOffset, 0);
            pageOffset = mBuffer.getLong(0);
        }
    }

    //----------------------------------------------------------------------
    /** 
     * Writes the OIDList header information back to the PageServer. Until
     * this is called, the header information is cached in memory.
     *
     * @throws PageServerException if an error occurs.
     */
    public void writeHeader() throws PageServerException
    {
        mBuffer.limit(HDR_SIZE);
        mBuffer.putLong(0, mNumOIDs);
        mBuffer.putLong(PTR_SIZE, mOIDPages[0]);
        mBuffer.position(0);
        mPageServer.storePage(mBuffer, mHeaderOffset, 0);
    }

    //----------------------------------------------------------------------
    /**
     * Gets the size of the OID list. Some OIDs in the list may not be in use.
     * Unused OIDs can be detected by calling getObjectOffsetForOID() on an OID.
     * If the method returns PageServer.NULL_OFFSET, the OID is not in use.
     *
     * @return the size of the OID list.
     */
    public long getListSize()
    {
        return mNumOIDs;
    }
    
    //----------------------------------------------------------------------
    /**
     * Allocates a block of OIDs.
     *
     * @param anOIDCount the number of OIDs to allocate.
     *
     * @return a block of OIDs of length anOIDCount.
     *
     * @throws PageServerException if an error occurs.
     */
    public long[] allocateOIDBlock(int anOIDCount) throws PageServerException
    {
        long[] oids = new long[anOIDCount];
        for (int i = 0; i < oids.length; i++) {
            oids[i] = mNumOIDs++;
        }

        return oids;
    }

    //----------------------------------------------------------------------
    /**
     * Ensures that a OID has been allocated. If it has not been allocated,
     * it will be upon return. This is used primarily for log-based recovery
     * when we can't be certain if the OID header was rewritten after an allocateOIDBlock.
     *
     * @param anOID the OID.
     *
     * @throws PageServerException if an error occurs.
     */
    public void ensureOIDAllocated(long anOID) throws PageServerException
    {
        if (anOID >= mNumOIDs) {
            mNumOIDs = anOID + 1;
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the index for mOIDPages given anOID. The array will grow to 
     * accomodate the returned index. A new page is allocated if
     * necessary and linked to the list.
     *
     * @param anOID the OID.
     *
     * @return the index.
     *
     * @throws PageServerException if an error occurs.
     */
    protected long getPageIndexForOID(long anOID) throws PageServerException
    {
        long pageIndex = (int)(anOID / mOIDsPerPage);
        if (pageIndex >= mOIDPages.length) {
            long[] newOIDPages = new long[(int)pageIndex + 1000];
            System.arraycopy(mOIDPages, 0, newOIDPages, 0, mOIDPages.length);
            mOIDPages = newOIDPages;
        }

        // If this array entry and/or its predecessors were zero, allocate empty pages 
        // and link them together.
        if (mOIDPages[(int)pageIndex] == PageServer.NULL_OFFSET) {
            int idx = (int)pageIndex;
            long nextPage = PageServer.NULL_OFFSET;
            mBuffer.position(0);
            mBuffer.limit(mPageSize);
            Arrays.fill( mBuffer.array(), (byte)0);
            for (; idx >= 0 && mOIDPages[idx] == PageServer.NULL_OFFSET; --idx) {
                long pageOffset = mPageServer.allocatePage();
                mBuffer.putLong(0, nextPage);
                mPageServer.storePage(mBuffer, pageOffset, 0);
                mOIDPages[idx] = pageOffset;
                nextPage = pageOffset;
            }
            
            // Link last full page in list with the last one we allocated.
            if (idx >= 0) {
                mBuffer.putLong(0, nextPage);
                mBuffer.position(0);
                mBuffer.limit(PTR_SIZE);
                mPageServer.storePage(mBuffer, mOIDPages[idx], 0);
            }
        }

        return pageIndex;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the offset within the page of the OID. 
     *
     * @param aPageIndex the page index returned by getPageIndexForOID().
     * @param anOID the OID.
     *
     * @return the offset within the page.
     *
     * @throws PageServerException if an error occurs.
     */
    protected int getOffsetWithinPage(long aPageIndex, long anOID)
    {
        return PTR_SIZE + ((int)(anOID - (aPageIndex * mOIDsPerPage)) * OID_SIZE);
    }

    //----------------------------------------------------------------------
    /**
     * Gets the CID associated with the specified OID.
     *
     * @param anOID the OID to get the CID for.
     *
     * @return the CID.
     *
     * @throws PageServerException if an error occurs.
     */
    public long getCIDforOID(long anOID) throws PageServerException
    {
        long pageIndex = getPageIndexForOID(anOID);
        long pageOffset = mOIDPages[(int)pageIndex];
        int offsetWithinPage = getOffsetWithinPage(pageIndex, anOID);
        mBuffer.position(0);
        mBuffer.limit(CID_SIZE);
        mPageServer.loadPage(mBuffer, pageOffset, offsetWithinPage + PTR_SIZE);
        return mBuffer.getLong(0);
    }

    //----------------------------------------------------------------------
    /**
     * Gets the pointer to the object (object offset) for anOID.
     *
     * @param anOID the OID to get the CID for.
     *
     * @return the object's offset, or PageServer.NULL_OFFSET if anOID is
     *  not in use.
     *
     * @throws PageServerException if an error occurs.
     */
    public long getObjectOffsetForOID(long anOID) throws PageServerException
    {
        long pageIndex = getPageIndexForOID(anOID);
        long pageOffset = mOIDPages[(int)pageIndex];
        int offsetWithinPage = getOffsetWithinPage(pageIndex, anOID);
        mBuffer.position(0);
        mBuffer.limit(PTR_SIZE);
        mPageServer.loadPage(mBuffer, pageOffset, offsetWithinPage);
        return mBuffer.getLong(0);
    }

    //----------------------------------------------------------------------
    /**
     * Sets the OID information for anOID. Information is only updated if 
     * it is different from what is currently stored.
     *
     * @param anOID the OID.
     * @param anOffset the object offset.
     * @param aCID the CID.
     *
     * @throws PageServerException if an error occurs.
     */
    public void setOIDInfo(long anOID, long anOffset, long aCID) throws PageServerException
    {
        // only update if there is a change so we don't dirty the page.
        long pageIndex = getPageIndexForOID(anOID);
        long pageOffset = mOIDPages[(int)pageIndex];
        int offsetWithinPage = getOffsetWithinPage(pageIndex, anOID);
        mBuffer.position(0);
        mBuffer.limit(PTR_SIZE + CID_SIZE);
        mPageServer.loadPage(mBuffer, pageOffset, offsetWithinPage);
        long currentOffset = mBuffer.getLong(0);
        long currentCID = mBuffer.getLong(PTR_SIZE);
        if (anOffset != currentOffset || aCID != currentCID) {
            mBuffer.putLong(0, anOffset);
            mBuffer.putLong(PTR_SIZE, aCID);
            mPageServer.storePage(mBuffer, pageOffset, offsetWithinPage);
        }
    }
}

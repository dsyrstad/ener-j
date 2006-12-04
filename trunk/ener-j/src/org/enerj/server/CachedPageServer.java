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
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/CachedPageServer.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.server;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.odmg.ODMGException;

/**
 * Ener-J Cached Page Server. Caches pages served by another PageServer.
 * This class is <em>not</em>thread-safe. 
 *
 * @version $Id: CachedPageServer.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class CachedPageServer implements PageServer
{
    private static final Logger sLogger = Logger.getLogger(CachedPageServer.class.getName());

    /** The delegate PageServer being cached. */
    private PageServer mDelegate = null;
    /** A LRU to MRU ordered HashMap which represents the Cache. Key is logical page offset as a Long.
     * The value is a Page.
     */
    private LinkedHashCache mCache = null;
    /** Maximum number of pages to cache. */
    private int mMaxPages = 0;
    /** Cached page size from delegate. */
    private int mPageSize;
    /** True if stored pages should be written through to the delegate. They are always cached. */
    private boolean mWriteThru = false;
    /** Cached copy of delegate's read only flag. */
    private boolean mReadOnly = false;
    /** When a page is added to the cache, this may be set to the LRU dirty page that is being
     * dropped from the cache. 
     */
    private Page mDirtyLRUPage = null;
    /** Pool of reusable pages. We don't let this grow larger than mMaxPages. */
    private LinkedList mPagePool = new LinkedList();


    /**
     * Constructs a new CachedPageServer.
     *
     * @param aMaxNumPages the maximum number of pages to cache. This should be at least 2.
     * @param shouldWriteThru true if stored pages should be synchronously written
     *  to the delegate PageServer. If false, stored pages are only written to the
     *  delegate when syncAllPages is invoked or when the last session disconnects
     *  from this server.
     * @param aDelegateServer the delegate PageServer.
     *
     * @throws PageServerException if an error occurs.
     */
    private CachedPageServer(int aMaxNumPages, boolean shouldWriteThru, PageServer aDelegateServer) throws PageServerException
    {
        if (aMaxNumPages < 2) {
            aMaxNumPages = 2;
        }
        
        mMaxPages = aMaxNumPages;
        mWriteThru = shouldWriteThru;
        mDelegate = aDelegateServer;
        
        // Cache this flag so we don't call the delegate on each store.
        mReadOnly = mDelegate.isReadOnly();

        // Cache the page size. It doesn't change.
        mPageSize = mDelegate.getPageSize();

        // Create cache.
        mCache = new LinkedHashCache(mMaxPages);
    }
    

    /**
     * Allocates a new page at the given offset.
     *
     * @param aLogicalPageOffset The logical offset to the page. 
     */
    private Page allocatePage(long aLogicalPageOffset)
    {
        Page page;
        if (mPagePool.isEmpty()) {
            page = new Page(aLogicalPageOffset);
        }
        else {
            page = (Page)mPagePool.removeFirst();
            page.initialize(aLogicalPageOffset);
        }

        return page;
    }
    

    /**
     * Frees an allocated page. The page may be added to the free page pool, or dumped.
     *
     * @param aPage the page to free.
     */
    private void freePage(Page aPage)
    {
        // Don't let the pool grow larger than cache.
        if (mPagePool.size() < mMaxPages) {
            mPagePool.add(aPage);
        }
    }
    

    /**
     * Adds page to the cache and make it the MRU. The LRU page will drop off the cache if the cache
     * is full.
     *
     * @param aPage a Page object.
     *
     * @throws PageServerException if an error occurs.
     */
    private void addCachedPage(Page aPage) throws PageServerException
    {
        Long logicalOffset = aPage.mLogicalPageOffset;
        // This put will cause removeEldestEntry to be called, which may cause mDirtyLRUPage to
        // be set. We have to store the LRU page here, rather than in removeEldestEntry, because the
        // store may throw an exception.
        mDirtyLRUPage = null;
        Page oldPage = (Page)mCache.put(logicalOffset, aPage);
        if (mDirtyLRUPage != null) {
            mDelegate.storePage(mDirtyLRUPage.mContents, mDirtyLRUPage.mLogicalPageOffset, 0);
            freePage(mDirtyLRUPage);
        }
        
        if (oldPage != null) {
            throw new PageServerException("INTERNAL: Didn't expect a page in the cache already!");
        }
    }
    

    /**
     * Gets a cached page and make it the MRU.
     *
     * @param aLogicalPageOffset the logical page offset of the desired page.
     *
     * @return the Page, or null if the page is not cached.
     */
    private Page getCachedPage(long aLogicalPageOffset)
    {
        return (Page)mCache.get(aLogicalPageOffset);
    }
    

    // Start of PageServer Interface...



    /**
     * Connects to a CachedPageServer.
     *
     * @param someProperties properties which specify the connect parameters.
     * The properties can contain the following keys:<br>
     * <ul>
     * <li><i>CachedPageServer.delegatePageServerClass</i> - the class name of the delegate</li>   
     * <li><i>CachedPageServer.numberOfCachedPages</i> - maximum number of pages to cache.</li>   
     * <li><i>CachedPageServer.writeThru</i> - if set to "true", stored pages are immediately stored to 
     *   the delegate page server. Otherwise, pages are stored to the delegate either when they fall off of the cache or
     *   when syncAllPages() is called.</li>   
     * <li><i>PageServer.forceOpen</i> - forces the page server to open the volume if it wasn't closed properly. Optional.</li>
     * </ul>
     * 
     * @return a PageServer. 
     *
     * @throws ODMGException if an error occurs. This includes, but is not limited to:
     *   PageServerNotFoundException if the PageServer cannot be found; VolumeNeedsRecoveryException
     *  if the header "open" flag is set and shouldForceOpen is false.
     */
    public static PageServer connect(Properties someProperties) throws org.odmg.ODMGException
    {
        String numPagesStr = someProperties.getProperty("CachedPageServer.numberOfCachedPages");
        int maxPages;
        try {
            maxPages = Integer.parseInt(numPagesStr);
        }
        catch (NumberFormatException e) {
            throw new PageServerException("Invalid number of CachedPageServer.numberOfCachedPages: " + numPagesStr);
        }
        
        boolean writeThru = (someProperties.getProperty("CachedPageServer.writeThru") != null);
        boolean forceOpen = (someProperties.getProperty("PageServer.forceOpen") != null);

        String delegateClassName = someProperties.getProperty("CachedPageServer.delegatePageServerClass");
            
        PageServer delegate = (PageServer)PluginHelper.connect(delegateClassName, someProperties);
        return new CachedPageServer(maxPages, writeThru, delegate);
    }


    public void disconnect() throws PageServerException
    {
        try {
            syncAllPages();
            mDelegate.disconnect();
        }
        finally {
            mDelegate = null;
            mCache = null;
        }
        
        sLogger.fine("CahcedPageServer is disconnected/shutdown");
    }


    public long getLogicalFirstPageOffset()
    {
        return mDelegate.getLogicalFirstPageOffset();
    }


    public long getLogicalLastPageOffset()
    {
        return mDelegate.getLogicalLastPageOffset();
    }


    public int getPageSize()
    {
        return mPageSize;
    }


    public long getVolumeCreationDate()
    {
        return mDelegate.getVolumeCreationDate();
    }


    public long getDatabaseID()
    {
        return mDelegate.getDatabaseID();
    }
    

    public boolean isReadOnly()
    {
        return mReadOnly;
    }


    public void loadPage(ByteBuffer aBuffer, long aLogicalPageOffset, int anOffset) throws PageServerException
    {
        Page page = getCachedPage(aLogicalPageOffset);
        if (page == null) {
            page = allocatePage(aLogicalPageOffset);
            addCachedPage(page);
            mDelegate.loadPage(page.mContents, aLogicalPageOffset, 0);
        }

        ByteBuffer pageBuf = page.mContents;
        int currPosition = aBuffer.position();
        try {
            pageBuf.position(anOffset);
            pageBuf.limit(anOffset + aBuffer.remaining() );
            aBuffer.put(pageBuf);
        }
        finally {
            pageBuf.position(0);
            pageBuf.limit(mPageSize);
            aBuffer.position(currPosition);
        }
    }


    public void storePage(ByteBuffer aBuffer, long aLogicalPageOffset, int anOffset) throws PageServerException
    {
        //  TODO  we should track the union of anOffset/aLength ranges in the cached page
        //  TODO  and use them when storing to the delegate

        // We can't wait until the page is flushed to the delegate to find out that the
        // volume is read-only and the page can't be stored. So we check the delegate's 
        // read-only flag now.
        if (mReadOnly) {
            throw new PageServerException("Volume is read only");
        }

        Page page = getCachedPage(aLogicalPageOffset);
        if (page == null) {
            // Add new page to cache.
            page = allocatePage(aLogicalPageOffset);
            addCachedPage(page);
        }
        // else page is already in cache, just update.

        ByteBuffer pageBuf = page.mContents;
        int currPosition = aBuffer.position();
        try {
            pageBuf.position(anOffset);
            pageBuf.put(aBuffer);
        }
        finally {
            pageBuf.position(0);
            aBuffer.position(currPosition);
        }
        
        if (mWriteThru) {
            pageBuf.position(anOffset);
            mDelegate.storePage(pageBuf, aLogicalPageOffset, anOffset);
            // Page is no longer dirty.
            page.mModified = false;
        }
        else {
            // Make sure that the page is marked dirty when write-thru is not set. 
            // The intent of the caller is to store the page.
            page.mModified = true;
        }
    }


    public long allocatePage() throws PageServerException
    {
        return mDelegate.allocatePage();
    }


    public void freePage(long aLogicalPageOffset) throws PageServerException
    {
        // We don't care if the cached page is dirty or not if we're freeing it.
        mDelegate.freePage(aLogicalPageOffset);
        Page page = (Page)mCache.remove(aLogicalPageOffset);
        if (page != null) {
            freePage(page);
        }            
    }


    public void syncAllPages() throws PageServerException
    {
        // Flush all dirty pages. Note that iterating does not change the access order.
        Iterator iterator = mCache.values().iterator();
        while (iterator.hasNext()) {
            Page page = (Page)iterator.next();
            if (page.mModified) {
                mDelegate.storePage(page.mContents, page.mLogicalPageOffset, 0);
                page.mModified = false;
            }
        }
        
        mDelegate.syncAllPages();
    }


    // ...End of PageServer Interface.




    /**
     * Overrides removeEldestEntry from LinkedHashMap so that LRU Pages can
     * be dropped from the cache.
     */
    private final class LinkedHashCache extends LinkedHashMap
    {

        LinkedHashCache(int aMaxNumPages)
        {
            super((aMaxNumPages * 3) / 4 + 1, .75F, true);
        }
        

        protected boolean removeEldestEntry(Map.Entry anEldestEntry)
        {
            if (size() < mMaxPages) {
                return false;
            }
            
            Page page = (Page)anEldestEntry.getValue();
            if (page.mModified) {
                mDirtyLRUPage = page;
            }
            else {
                mDirtyLRUPage = null;
            }
            
            return true;
        }
    }
    


    /**
     * Definition of a Page served up by a CachedPageServer. 
     *
     * @version $Id: CachedPageServer.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $
     * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
     */
    private final class Page
    {
        /** The logical offset to the page. */
        long mLogicalPageOffset;
        /** Current page contents. The position must be left at zero and the limit at mPageSize.
         * after any operation.
         */
        ByteBuffer mContents =  ByteBuffer.allocate(mPageSize);
        /** Modified page flag. */
        boolean mModified;


        /**
         * Constructs a new Page.
         *
         * @param aLogicalPageOffset The logical offset to the page. 
         */
        Page(long aLogicalPageOffset)
        {
            initialize(aLogicalPageOffset);
        }


        /** 
         * Initializes the page to the specified offset and sets mModified to false.
         *
         * @param aLogicalPageOffset The logical offset to the page. 
         */
        void initialize(long aLogicalPageOffset)
        {
            mLogicalPageOffset = aLogicalPageOffset;
            mModified = false;
            mContents.position(0);
            mContents.limit( mContents.capacity() );
        }
    }

}


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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/PageServer.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import java.io.*;
import java.nio.*;
import java.util.*;


/**
 * Ener-J Page Server interface. All things that serve pages to ObjectServers
 * implement this interface. 
 * A page server serves up fixed-size "pages" (blocks) of data from a storage device 
 * or file (a "volume").
 * It also keeps track of allocated and free pages. <p>
 *
 * Multiple PageServers, each
 * serving up different volumes, can be simultaneously open by a single
 * ObjectServer. This allows the database to be comprised of multiple 
 * physical volumes on multiple servers. In other words, a cluster of PageServers. <p>
 *
 * Beyond this interface, the following additional requirement
 * is placed on a PageServer implementation:<p>
 *
 * A static factory method "connect" must be defined. The definition
 *    for the method is:<p>
 *
 * public static PageServer connect(String aPageServerURI) throws PageServerException;<p>
 *
 * Parameters:<br> aPageServerURI - a URI identifying the PageServer to open. By the time 
 *  this method is called, the PageServerHelper has already determined the proper
 *  PageServer implementation to use based on the protocol. The URI has the form: <br>
 * vops://{page-server-class-name}[,args...]<p>
 *
 * Throws: PageServerException in the event of an error. These errors include, but are not limited to:
 *  PageServerNotFoundException if the PageServer doesn't exist.<p>
 *
 * Returns: a PageServer which references the same PageServer object 
 * for a given URI that refers to the same PageServer. <p>
 *
 * A PageServer will always be used in a single-threaded mode, hence it does not need to be
 * thread-safe. <p>
 *
 * @version $Id: PageServer.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface PageServer
{
    /** Null Page Offset. 0 is invalid. */
    public static final long NULL_OFFSET = 0L;
    /** Maximum page size. */
    public static final int MAX_PAGE_SIZE = 65536;
    /** Minimum page size. */
    public static final int MIN_PAGE_SIZE = 512;

    

    /**
     * Disconnects from the PageServer. This properly closes down the volume.
     *
     * @throws PageServerException in the event of an error. E.g., 
     * PageServerNotConnectedException. 
     */
    public void disconnect() throws PageServerException;


    /**
     * Gets the offset of the first page offset managed by this PageServer.
     * This value may point to an allocated or free page.
     *
     * @return the offset to the first page managed by this server.
     */
    public long getLogicalFirstPageOffset();


    /**
     * Gets the offset of the last page offset managed by this PageServer.
     * This value may point to an allocated or free page.
     *
     * @return the offset to the last page managed by this server. This may be
     *  NULL_OFFSET if the volume can grow unbounded.
     */
    public long getLogicalLastPageOffset();


    /**
     * Gets the page size managed by this PageServer. This is 
     * the page size specified when the volume was created.
     *
     * @return the size of pages managed by this server.
     */
    public int getPageSize();


    /**
     * Gets the date in which the volume was created.
     *
     * @return the date in milliseconds since Jan 1, 1970 GMT. Suitable for use
     *  with "new Date(millis)".
     */
    public long getVolumeCreationDate();


    /**
     * Gets the unique database ID assigned to this volume. If the database
     * is represented by multiple volumes, all volumes must have the same
     * database ID.
     *
     * @return the database ID.
     */
    public long getDatabaseID();
    

    /**
     * Determines if the volume is read-only.
     *
     * @return true if the volume is read-only, else false.
     */
    public boolean isReadOnly();


    /**
     * Loads partial contents of a page from the volume.
     * aBuffer.remaining() bytes are loaded.
     * The entire page can be loaded by specifying anOffset = 0 and 
     * aBuffer.remaining() of getAvailablePageSize().
     * anOffset + aBuffer.remaining() must not exceed getAvailablePageSize().
     * On return, aBuffer's position and limit will not be affected.
     *
     * @param aBuffer a ByteBuffer which will be loaded upon return.
     * @param aLogicalPageOffset the logical offset to the beginning of the page.
     * @param anOffset the offset within the Page contents from which to start loading
     *  aBuffer.
     *
     * @throws PageServerException if an error occurs.
     */
    public void loadPage(ByteBuffer aBuffer, long aLogicalPageOffset, int anOffset) throws PageServerException;


    /**
     * Stores a partial page to the volume. The bytes from aBuffer.position()
     * to aBuffer.limit() are stored to the page.
     * anOffset + aBuffer.remaining() must not exceed getAvailablePageSize().
     * On return, aBuffer's position and limit will not be affected.
     *
     * @param aBuffer a ByteBuffer to be stored.
     * @param aLogicalPageOffset the logical offset to the beginning of the page.
     * @param anOffset the offset within the Page contents at which to start storing
     *  aBuffer.
     *
     * @throws PageServerException if an error occurs.
     */
    public void storePage(ByteBuffer aBuffer, long aLogicalPageOffset, int anOffset) throws PageServerException;


    /**
     * Allocates a new/free page in the volume.
     *
     * @return a long representing the logical offset to the page.
     *
     * @throws PageServerException if an error occurs. One of the PageServerExceptions
     *  thrown may be PageServerNoMoreSpaceException if there is no more
     *  space available on a volume.
     */
    public long allocatePage() throws PageServerException;


    /**
     * Frees an allocated page in the volume.
     *
     * @param aLogicalPageOffset the logical offset to the page.
     *
     * @throws PageServerException if an error occurs.
     */
    public void freePage(long aLogicalPageOffset) throws PageServerException;


    /**
     * Ensures that all pages have been physically written to the volume.
     *
     * @throws PageServerException if an error occurs.
     */
    public void syncAllPages() throws PageServerException;
}


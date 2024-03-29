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
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/OIDListTest.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.server;

import java.io.File;
import java.util.Properties;

import org.enerj.core.ObjectSerializer;
import org.enerj.server.pageserver.CachedPageServer;
import org.enerj.server.pageserver.FilePageServer;
import org.enerj.server.pageserver.OIDList;
import org.enerj.server.pageserver.PageServer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests OIDList. 
 *
 * @version $Id: OIDListTest.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class OIDListTest extends TestCase
{
    // Page size of 512 gives 31 OIDs per page.
    private static final int PAGE_SIZE = 512;
    private static final int OIDS_PER_PAGE = (PAGE_SIZE - 8) / 16;
    
    private File mTmpPageFile = null;
    private PageServer mPageServer = null;
    

    public OIDListTest(String aTestName) 
    {
        super(aTestName);
    }

    public static Test suite() 
    {
        return new TestSuite(OIDListTest.class);
    }


    /**
     * Creates a new page server volume.
     */
    private void createPageVolume() throws Exception
    {
        String tmpdir = System.getProperty("java.io.tmpdir");
        mTmpPageFile = new File(tmpdir + File.separatorChar + "OIDListTest-volume");
        mTmpPageFile.delete();

        // Pre-allocate first page so it's zeros.
        FilePageServer.createVolume(mTmpPageFile.getAbsolutePath(), PAGE_SIZE, 0x1234L, 0L, 300000000L, (long)PAGE_SIZE);

        // Make sure page server knows that first page is allocated.
        PageServer pageServer = connectToPageVolume();
        long allocatedPage = pageServer.allocatePage();
        long logicalFirstPage = pageServer.getLogicalFirstPageOffset();
        pageServer.disconnect();
        assertTrue( allocatedPage == logicalFirstPage );
    }
    

    /**
     * Connects to the PageServer volume created by createPageVolume().
     */
    private PageServer connectToPageVolume() throws Exception
    {
        Properties props = new Properties( System.getProperties() );
        props.setProperty("CachedPageServer.delegatePageServerClass", FilePageServer.class.getName() );
        props.setProperty("CachedPageServer.numberOfCachedPages", "1000");
        props.setProperty("FilePageServer.volume", mTmpPageFile.getAbsolutePath() );
        return (PageServer)PluginHelper.connect(CachedPageServer.class.getName(), props); 
    }
    

    protected void setUp() throws Exception
    {
        createPageVolume();
        mPageServer = connectToPageVolume();
    }


    protected void tearDown() throws Exception
    {
        if (mPageServer != null) {
            mPageServer.disconnect();
            mPageServer = null;
        }

        if (mTmpPageFile != null) {
            mTmpPageFile.delete();
            mTmpPageFile = null;
        }
    }
    

    /**
     * Tests allocation of OIDs.
     */
    public void testallocateOIDBlock() throws Exception
    {
        OIDList list = new OIDList(mPageServer, mPageServer.getLogicalFirstPageOffset() );
        
        assertEquals(ObjectSerializer.FIRST_USER_OID, list.getListSize());

        long[] oids1 = list.allocateOIDXBlock(40);
        assertNotNull( oids1 );
        assertEquals(40, oids1.length);
        assertEquals((oids1.length + ObjectSerializer.FIRST_USER_OID), list.getListSize());
        
        // Make sure new oids are returned each time and that they're not the null OID.
        long[] oids2 = list.allocateOIDXBlock(10);
        assertEquals(10, oids2.length);
        assertEquals((oids1.length + oids2.length + ObjectSerializer.FIRST_USER_OID), list.getListSize());
        
        for (int i1 = 0; i1 < oids1.length; i1++) {
            assertTrue( oids1[i1] != 0L );
            for (int i2 = 0; i2 < oids2.length; i2++) {
                assertTrue( oids1[i1] != oids2[i2] );
                assertTrue( oids2[i2] != 0L );
            }
        }
        
        list.writeHeader();
    }


    /**
     * Helper for testSetGetOIDInfo.
     */
    private void setGetOIDs(int aNumOIDs) throws Exception
    {
        OIDList list = new OIDList(mPageServer, mPageServer.getLogicalFirstPageOffset() );

        long[] oids = list.allocateOIDXBlock(aNumOIDs);
        assertTrue(oids.length == aNumOIDs);
        
        for (int i = 0; i < oids.length; i++) {
            list.setOIDInfo(oids[i], oids[i] * 1000L, oids[i] * 1234567890L);
        }

        assertTrue( list.getListSize() == (oids.length + ObjectSerializer.FIRST_USER_OID) );

        for (int i = 0; i < oids.length; i++) {
            long cid = list.getCIDforOID(oids[i]);
            assertTrue("CID mismatch at index " + i + " oids[i]=" + oids[i], cid == (oids[i] * 1234567890L) );

            long offset = list.getObjectOffsetForOID(oids[i]);
            assertTrue("Offset mismatch at index " + i + " oids[i]=" + oids[i], offset == (oids[i] * 1000L) );
        }

        list.writeHeader();
        
        // Disconnect, reconnect, and try to read them back again.
        mPageServer.disconnect();

        mPageServer = connectToPageVolume();
        list = new OIDList(mPageServer, mPageServer.getLogicalFirstPageOffset() );

        assertTrue( list.getListSize() == (oids.length + ObjectSerializer.FIRST_USER_OID) );

        for (int i = 0; i < oids.length; i++) {
            long cid = list.getCIDforOID(oids[i]);
            assertTrue("CID mismatch at index " + i + " oids[i]=" + oids[i], cid == (oids[i] * 1234567890L) );

            long offset = list.getObjectOffsetForOID(oids[i]);
            assertTrue("Offset mismatch at index " + i + " oids[i]=" + oids[i], offset == (oids[i] * 1000L) );
        }

        list.writeHeader();
    }
    

    /**
     * Tests setting and getting of OID info on one page only.
     */
    public void testSetGetOIDInfoOnePage() throws Exception
    {
        setGetOIDs(OIDS_PER_PAGE - 2);
    }


    /**
     * Tests setting and getting of OID info on multiple pages.
     */
    public void testSetGetOIDInfoMultiplePages() throws Exception
    {
        setGetOIDs(OIDS_PER_PAGE * 130 + (OIDS_PER_PAGE / 2));
    }


    /**
     * Helper for tests skipping over allocated OIDs so that multiple pages are allocated on
     * first setOIDInfo.
     *
     * @param shouldAllocateFirstOID if true, the first OID is allocated.
     * @param aNumOIDs number of OIDs to allocate.
     */
    private void skipOIDs(boolean shouldAllocateFirstOID, int aNumOIDs) throws Exception
    {
        OIDList list = new OIDList(mPageServer, mPageServer.getLogicalFirstPageOffset() );

        long[] oids = list.allocateOIDXBlock(aNumOIDs);
        assertTrue(oids.length == aNumOIDs);
        
        long firstOID = oids[0];
        long lastOID = oids[ oids.length - 1 ];
        if (shouldAllocateFirstOID) {
            // Allocate first page
            list.setOIDInfo(firstOID, 912345L, 967890L);
        }
        
        // Allocate last page (129th) and blank pages in between.
        list.setOIDInfo(lastOID, 12345L, 67890L);  

        assertTrue( list.getListSize() == (oids.length + ObjectSerializer.FIRST_USER_OID) );
        
        long cid;
        long offset;

        if (shouldAllocateFirstOID) {
            cid = list.getCIDforOID(firstOID);
            assertTrue( cid == 967890L);

            offset = list.getObjectOffsetForOID(firstOID);
            assertTrue( offset == 912345L );
        }

        cid = list.getCIDforOID(lastOID);
        assertTrue( cid == 67890L);

        offset = list.getObjectOffsetForOID(lastOID);
        assertTrue( offset == 12345L );

        list.writeHeader();
        
        // Disconnect, reconnect, and try to read it back again.
        mPageServer.disconnect();

        mPageServer = connectToPageVolume();
        list = new OIDList(mPageServer, mPageServer.getLogicalFirstPageOffset() );

        assertTrue( list.getListSize() == (oids.length + ObjectSerializer.FIRST_USER_OID) );

        if (shouldAllocateFirstOID) {
            cid = list.getCIDforOID(firstOID);
            assertTrue( cid == 967890L);

            offset = list.getObjectOffsetForOID(firstOID);
            assertTrue( offset == 912345L );
        }

        cid = list.getCIDforOID(lastOID);
        assertTrue( cid == 67890L);

        offset = list.getObjectOffsetForOID(lastOID);
        assertTrue( offset == 12345L );

        list.writeHeader();
    }


    /**
     * Tests skipping over allocated OIDs so that multiple pages are allocated on
     * first setOIDInfo. Allocate the first and last OIDs.
     */
    public void testSkipOIDsWithFirstAllocated() throws Exception
    {
        skipOIDs(true, 4000);
    }


    /**
     * Tests skipping over allocated OIDs so that multiple pages are allocated on
     * first setOIDInfo. Allocate only the last OID.
     */
    public void testSkipOIDsOnlyLastAllocated() throws Exception
    {
        skipOIDs(false, 4000);
    }


    /**
     * Tests skipping over allocated OIDs so that multiple pages are allocated on
     * first setOIDInfo. Also cause the OID page array to grow. The assumption 
     * is that the OID page array is initially allocated at 1001, so we allocate
     * 2010 * OIDS_PER_PAGE OIDs.
     */
    public void testSkipOIDsAndCauseOIDPageArrayToGrow() throws Exception
    {
        skipOIDs(false, 2010 * OIDS_PER_PAGE);
    }
}

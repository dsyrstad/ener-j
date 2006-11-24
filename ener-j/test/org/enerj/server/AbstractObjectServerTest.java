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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/AbstractObjectServerTest.java,v 1.2 2006/05/05 13:47:37 dsyrstad Exp $

package org.enerj.server;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;

import org.enerj.core.ObjectSerializer;

import junit.framework.TestCase;

/**
 * Tests ObjectServer. <p>
 *
 * @version $Id: AbstractObjectServerTest.java,v 1.2 2006/05/05 13:47:37 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractObjectServerTest extends TestCase
{
    private static final int PAGE_SIZE = 8192;
    
    private ObjectServerSession mSession = null;

    

    public AbstractObjectServerTest(String aTestName) 
    {
        super(aTestName);
    }
    

    /**
     * Gets the properties to connect to an ObjectServer to be tested.
     */
    abstract protected Properties getObjectServerProperties();
        

    /**
     * Gets the class name of the ObjectServer to be tested.
     */
    abstract protected String getObjectServerClassName();
    
    abstract protected void createDB() throws Exception;


    /**
     * Connects to the ObjectServer.
     */
    private ObjectServerSession connectToObjectServer() throws Exception
    {
        return (ObjectServerSession)PluginHelper.connect(getObjectServerClassName(), getObjectServerProperties());
    }
    

    protected void setUp() throws Exception
    {
        createDB();
        mSession = connectToObjectServer();
    }


    protected void tearDown() throws Exception
    {
        if (mSession != null) {
            mSession.shutdown();
            mSession = null;
        }
    }
    

    /** 
     * Helper for testOIDs.
     */
    private void verifyOIDs(long[] someOIDs) throws Exception
    {
        assertNotNull( someOIDs );
        assertTrue( someOIDs.length >= 1 );
        ClassInfo[] classInfos = mSession.getClassInfoForOIDs(someOIDs);
        for (int i = 0; i < someOIDs.length; i++) {
            assertTrue( someOIDs[i] > ObjectSerializer.NULL_OID );
            
            assertTrue( classInfos[i].getCID() == ObjectSerializer.NULL_CID );
        }
        
        // Verify no duplicates within the block
        for (int i = 0; i < someOIDs.length; i++) {
            for (int j = i + 1; j < someOIDs.length; j++) {
                assertTrue( someOIDs[i] != someOIDs[j] );
            }
        }
    }
    

    /**
     * Allocates a certain number of OIDs.
     */
    private long[] allocateOIDs(int anOIDCount) throws Exception
    {
        long[] oids = new long[anOIDCount];
        int idx = 0;
        int numToAlloc = anOIDCount;
        while (numToAlloc > 0) {
            long[] oidBlock = mSession.getNewOIDBlock(10);
            int copyLength = (numToAlloc < oidBlock.length ? numToAlloc : oidBlock.length);
            System.arraycopy(oidBlock, 0, oids, idx, copyLength);
            idx += copyLength;
            numToAlloc -= copyLength;
        }
        
        return oids;
    }
    

    /**
     * Generates a patterned byte array of aLength bytes using anOID and
     * aCID.
     */
    private byte[] generateBytes(int aLength, long anOID, long aCID)
    {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(aCID);
        buffer.putLong(anOID);
        byte[] sig = buffer.array();
        byte[] bytes = new byte[aLength];
        int idx = 0;
        int lengthLeft = aLength;
        while (lengthLeft > 0) {
            int length = (sig.length > lengthLeft ? lengthLeft : sig.length);
            System.arraycopy(sig, 0, bytes, idx, length);
            idx += length;
            lengthLeft -= length;
        }
        
        return bytes;
    }
    

    /**
     * Stores anObjectCount objects of length aLength and verifies the store.
     */
    private void storeObjects(long[] someOIDs, int aLength) throws Exception
    {
        long cid = System.currentTimeMillis();
        SerializedObject[] objects = new SerializedObject[someOIDs.length];
        for (int i = 0; i < someOIDs.length; i++) {
            objects[i] = new SerializedObject(someOIDs[i], cid + i, generateBytes(aLength, someOIDs[i], cid + i), true);
        }

        mSession.storeObjects(objects);

        byte[][] loadedObjects = mSession.loadObjects(someOIDs);
        assertNotNull(objects);
        ClassInfo[] testClassInfos = mSession.getClassInfoForOIDs(someOIDs);
        for (int i = 0; i < someOIDs.length; i++) {
            byte[] obj = loadedObjects[i];
            assertTrue( obj.length == aLength );

            long testCID = testClassInfos[i].getCID();
            assertTrue( testCID == (cid + i) );

            byte[] testBytes = generateBytes(aLength, someOIDs[i], testCID);
            assertTrue( Arrays.equals(testBytes, obj) );
        }
    }
    

    /**
     * Stores anObjectCount objects of length aLength and verifies the store.
     */
    private void storeObjects(int anObjectCount, int aLength) throws Exception
    {
        storeObjects( allocateOIDs(anObjectCount), aLength);
    }
    

    /**
     * Tests getNewOIDBlock and getCIDForOID.
     */
    public void testOIDs() throws Exception
    {
        mSession.beginTransaction();
        long[] oids1 = mSession.getNewOIDBlock(10);
        verifyOIDs(oids1);

        long[] oids2 = mSession.getNewOIDBlock(10);
        verifyOIDs(oids2);

        for (int i = 0; i < oids1.length; i++) {
            for (int j = 0; j < oids2.length; j++) {
                if (i != j) {
                    assertTrue( oids1[i] != oids2[j] );
                }
            }
        }

        mSession.commitTransaction();
    }


    /**
     * Tests storeObject with a zero length object.
     */
    public void testStoreObjectZeroLength() throws Exception
    {
        mSession.beginTransaction();

        // This should be plenty to span more than one 8K page using just object headers.
        storeObjects(2000, 0);

        mSession.commitTransaction();
    }


    /**
     * Tests storeObject with an object that can fit within one page.
     */
    public void testStoreObjectSingleSegment() throws Exception
    {
        mSession.beginTransaction();

        storeObjects(2000, 100);

        mSession.commitTransaction();
    }


    /**
     * Tests storeObject with an object that spans multiple pages.
     */
    public void testStoreObjectMultipleSegments() throws Exception
    {
        mSession.beginTransaction();

        storeObjects(100, PAGE_SIZE * 3);

        mSession.commitTransaction();
    }


    /**
     * Tests storeObject replacing one object with a larger object in a single segment.
     */
    public void testReplaceWithLargerObjectSingleSegment() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(1000);
        storeObjects(oids, 100);
        storeObjects(oids, 110);    // Larger object.

        mSession.commitTransaction();
    }
    

    /**
     * Tests storeObject replacing one object with a larger object in a Multiple segments.
     */
    public void testReplaceWithLargerObjectMultipleSegments() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(100);
        storeObjects(oids, PAGE_SIZE + 10);
        storeObjects(oids, PAGE_SIZE + 20);    // Larger object.

        mSession.commitTransaction();
    }
    

    /**
     * Tests storeObject replacing one object with the same size object in a single segment.
     */
    public void testReplaceWithSameSizeObjectSingleSegment() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(1000);
        storeObjects(oids, 100);
        storeObjects(oids, 100);

        mSession.commitTransaction();
    }
    

    /**
     * Tests storeObject replacing one zero-length object with another zero-length object.
     */
    public void testReplaceWithSameSizeObjectZeroLength() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(1000);
        storeObjects(oids, 0);
        storeObjects(oids, 0);

        mSession.commitTransaction();
    }
    

    /**
     * Tests storeObject replacing one object with the same size object in a Multiple segments.
     */
    public void testReplaceWithSameSizeObjectMultipleSegments() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(100);
        storeObjects(oids, PAGE_SIZE + 10);
        storeObjects(oids, PAGE_SIZE + 10);

        mSession.commitTransaction();
    }
    

    /**
     * Tests storeObject replacing one object with a smaller object in a single segment.
     */
    public void testReplaceWithSmallerObjectSingleSegment() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(1000);
        storeObjects(oids, 100);
        storeObjects(oids, 10);  // Smaller object

        mSession.commitTransaction();
    }
    

    /**
     * Tests storeObject replacing one object with a smaller object in same number of multiple segments.
     */
    public void testReplaceWithSmallerObjectSameNumberOfSegments() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(100);
        storeObjects(oids, (PAGE_SIZE * 3) + 10);
        storeObjects(oids, (PAGE_SIZE * 3) + 9);  // Smaller object

        mSession.commitTransaction();
    }
    

    /**
     * Tests storeObject replacing one object spanning multiple segments 
     * with a smaller object spanning a fewer number of segments, but more than 1.
     */
    public void testReplaceWithSmallerObjectFewerNumberOfSegments() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(100);
        storeObjects(oids, (PAGE_SIZE * 3) + 10);
        storeObjects(oids, PAGE_SIZE + 9);  // Smaller object

        mSession.commitTransaction();
    }
    

    /**
     * Tests storeObject replacing one object spanning multiple segments 
     * with a smaller object spanning one segment.
     */
    public void testReplaceWithSmallerObjectMultipleToOneSegment() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(100);
        storeObjects(oids, (PAGE_SIZE * 3) + 10);
        storeObjects(oids, 10);  // Smaller object

        mSession.commitTransaction();
    }
    

    /**
     * Tests storeObject replacing one object in one segment
     * with a zero-length object with one segment.
     */
    public void testReplaceWithZeroLengthObjectSingleSegment() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(1000);
        storeObjects(oids, 100);
        storeObjects(oids, 0);

        mSession.commitTransaction();
    }
    

    /**
     * Tests storeObject replacing one object spanning multiple segments
     * with a zero-length object with one segment.
     */
    public void testReplaceWithZeroLengthObjectMultipleSegments() throws Exception
    {
        mSession.beginTransaction();

        long[] oids = allocateOIDs(100);
        storeObjects(oids, PAGE_SIZE * 3);
        storeObjects(oids, 0);

        mSession.commitTransaction();
    }
    

    /**
     * Tests non-transactional reads.
     */
    public void testNontransactionalReads() throws Exception
    {
        // Create some objects.
        mSession.beginTransaction();

        long[] oids = allocateOIDs(2);
        storeObjects(oids, 10);

        mSession.commitTransaction();

        mSession.setAllowNontransactionalReads(true);
        assertTrue( mSession.getAllowNontransactionalReads() );

        // Should succeed
        mSession.loadObjects(new long[] { oids[0] });
        mSession.loadObjects(new long[] { oids[1] });

        mSession.setAllowNontransactionalReads(false);
        assertFalse( mSession.getAllowNontransactionalReads() );
        
        try {
            // Should fail
            mSession.loadObjects( new long[] { oids[0] });
            fail("Expected failure");
        }
        catch (Exception e) {
            // Expected
        }
    }
}

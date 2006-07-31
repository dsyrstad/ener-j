// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/AbstractObjectServerTest.java,v 1.2 2006/05/05 13:47:37 dsyrstad Exp $

package org.enerj.server;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;

import junit.framework.TestCase;

import org.odmg.Transaction;

/**
 * Tests ObjectServer. <p>
 *
 * @version $Id: AbstractObjectServerTest.java,v 1.2 2006/05/05 13:47:37 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractObjectServerTest extends TestCase
{
    private static final int PAGE_SIZE = 8192;
    
    private File mTmpPageFile = null;
    private File mTmpLogFile = null;
    private ObjectServerSession mSession = null;

    
    //----------------------------------------------------------------------
    public AbstractObjectServerTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the properties to connect to an ObjectServer to be tested.
     */
    abstract protected Properties getObjectServerProperties();
        
    //----------------------------------------------------------------------
    /**
     * Gets the class name of the ObjectServer to be tested.
     */
    abstract protected String getObjectServerClassName();

    //----------------------------------------------------------------------
    /**
     * Creates a new page server volume.
     */
    private void createPageVolume() throws Exception
    {
        //String tmpdir = System.getProperty("java.io.tmpdir");
        //  TODO  this currently has to be in sync with object server properties file, which will become a problem
        //  TODO  /tmp/ may not work on non *nix systems.
        mTmpPageFile = new File("/tmp/ObjectServer-volume");
        mTmpPageFile.delete();
        if (mTmpPageFile.exists()) {
            throw new Exception("Huh? " + mTmpPageFile.getAbsolutePath() + " still exists. Possibly last instance of PageServer didn't shutdown.");
        }
        
        // Delete the log file
        mTmpLogFile = new File("/tmp/PagedObjectServerTest.log");
        mTmpLogFile.delete();

        // Pre-allocate first page so it's zeros.
        FilePageServer.createVolume(mTmpPageFile.getAbsolutePath(), PAGE_SIZE, 0x1234L, 0L, 300000000L, (long)PAGE_SIZE);

        // Make sure page server knows that first page is allocated.
        Properties props = new Properties( System.getProperties() );
        props.setProperty("FilePageServer.volume", mTmpPageFile.getAbsolutePath() );
        PageServer pageServer = (PageServer)PluginHelper.connect(FilePageServer.class.getName(), props);
        long allocatedPage = pageServer.allocatePage();
        long logicalFirstPage = pageServer.getLogicalFirstPageOffset();
        pageServer.disconnect();
        assertTrue( allocatedPage == logicalFirstPage );
    }
    
    //----------------------------------------------------------------------
    /**
     * Connects to the ObjectServer.
     */
    private ObjectServerSession connectToObjectServer() throws Exception
    {
        return (ObjectServerSession)PluginHelper.connect(getObjectServerClassName(), getObjectServerProperties());
    }
    
    //----------------------------------------------------------------------
    protected void setUp() throws Exception
    {
        createPageVolume();
        mSession = connectToObjectServer();
    }

    //----------------------------------------------------------------------
    protected void tearDown() throws Exception
    {
        if (mSession != null) {
            mSession.shutdown();
            mSession = null;
        }

        if (mTmpPageFile != null) {
            mTmpPageFile.delete();
            mTmpLogFile.delete();
            mTmpPageFile = null;
        }
    }
    
    //----------------------------------------------------------------------
    /** 
     * Helper for testOIDs.
     */
    private void verifyOIDs(long[] someOIDs) throws Exception
    {
        assertNotNull( someOIDs );
        assertTrue( someOIDs.length >= 1 );
        for (int i = 0; i < someOIDs.length; i++) {
            assertTrue( someOIDs[i] > ObjectServer.NULL_OID );
            
            long cid = mSession.getCIDForOID(someOIDs[i]);
            assertTrue( cid == ObjectServer.NULL_CID );
        }
        
        // Verify no duplicates within the block
        for (int i = 0; i < someOIDs.length; i++) {
            for (int j = i + 1; j < someOIDs.length; j++) {
                assertTrue( someOIDs[i] != someOIDs[j] );
            }
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Allocates a certain number of OIDs.
     */
    private long[] allocateOIDs(int anOIDCount) throws Exception
    {
        long[] oids = new long[anOIDCount];
        int idx = 0;
        int numToAlloc = anOIDCount;
        while (numToAlloc > 0) {
            long[] oidBlock = mSession.getNewOIDBlock();
            int copyLength = (numToAlloc < oidBlock.length ? numToAlloc : oidBlock.length);
            System.arraycopy(oidBlock, 0, oids, idx, copyLength);
            idx += copyLength;
            numToAlloc -= copyLength;
        }
        
        return oids;
    }
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
    /**
     * Stores anObjectCount objects of length aLength and verifies the store.
     */
    private void storeObjects(long[] someOIDs, int aLength) throws Exception
    {
        long cid = System.currentTimeMillis();
        for (int i = 0; i < someOIDs.length; i++) {
            mSession.storeObject(cid + i, someOIDs[i], generateBytes(aLength, someOIDs[i], cid + i), true);
        }


        for (int i = 0; i < someOIDs.length; i++) {
            byte[] obj = mSession.loadObject(someOIDs[i]);
            assertNotNull( obj );
            assertTrue( obj.length == aLength );

            long testCID = mSession.getCIDForOID(someOIDs[i]);
            assertTrue( testCID == (cid + i) );

            byte[] testBytes = generateBytes(aLength, someOIDs[i], testCID);
            assertTrue( Arrays.equals(testBytes, obj) );
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Stores anObjectCount objects of length aLength and verifies the store.
     */
    private void storeObjects(int anObjectCount, int aLength) throws Exception
    {
        storeObjects( allocateOIDs(anObjectCount), aLength);
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests getNewOIDBlock and getCIDForOID.
     */
    public void testOIDs() throws Exception
    {
        mSession.beginTransaction();
        long[] oids1 = mSession.getNewOIDBlock();
        verifyOIDs(oids1);

        long[] oids2 = mSession.getNewOIDBlock();
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

    //----------------------------------------------------------------------
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

    //----------------------------------------------------------------------
    /**
     * Tests storeObject with an object that can fit within one page.
     */
    public void testStoreObjectSingleSegment() throws Exception
    {
        mSession.beginTransaction();

        storeObjects(2000, 100);

        mSession.commitTransaction();
    }

    //----------------------------------------------------------------------
    /**
     * Tests storeObject with an object that spans multiple pages.
     */
    public void testStoreObjectMultipleSegments() throws Exception
    {
        mSession.beginTransaction();

        storeObjects(100, PAGE_SIZE * 3);

        mSession.commitTransaction();
    }

    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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
        mSession.loadObject(oids[0]);
        mSession.loadObject(oids[1]);

        mSession.setAllowNontransactionalReads(false);
        assertFalse( mSession.getAllowNontransactionalReads() );
        
        try {
            // Should fail
            mSession.loadObject(oids[0]);
            fail("Expected failure");
        }
        catch (Exception e) {
            // Expected
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * A timed test. Temporary.
     */
    public void xxtestTimeTest() throws Exception
    {
        int objSize = 400;
        
        long start = System.currentTimeMillis();
        mSession.beginTransaction();

        long[] oids = allocateOIDs(18000);

        long cid = 0x1234567890ABCDEFL;
        for (int i = 0; i < oids.length; i++) {
            byte[] origObj = generateBytes(objSize, 0x987654ABCL, cid + i);
            mSession.getLock(oids[i], Transaction.WRITE, -1L);
            mSession.storeObject(cid + i, oids[i],  origObj, true);
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Store " + oids.length + " objects: " + elapsed);

        mSession.commitTransaction();

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Store " + oids.length + " objects with commit: " + elapsed);

        // -- Read back
        start = System.currentTimeMillis();
        mSession.beginTransaction();

        for (int i = 0; i < oids.length; i++) {
            mSession.getLock(oids[i], Transaction.READ, -1L);
            long testCID = mSession.getCIDForOID(oids[i]);
            byte[] obj = mSession.loadObject(oids[i]);
        }

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Load " + oids.length + " objects: " + elapsed);

        mSession.commitTransaction();

        elapsed = System.currentTimeMillis() - start;
        System.out.println("Load " + oids.length + " objects with commit: " + elapsed);

        mSession.beginTransaction();

        // Read again to check values.
        for (int i = 0; i < oids.length; i++) {
            byte[] origObj = generateBytes(objSize, 0x987654ABCL, cid + i);
            byte[] obj = mSession.loadObject(oids[i]);
            assertTrue( Arrays.equals(obj, origObj) );
        }

        mSession.commitTransaction();
    }    
}

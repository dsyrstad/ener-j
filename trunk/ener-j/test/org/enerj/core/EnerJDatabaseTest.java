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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/EnerJDatabaseTest.java,v 1.6 2006/06/09 02:39:23 dsyrstad Exp $

package org.enerj.core;

import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.ODMGRuntimeException;
import org.odmg.Transaction;
import org.enerj.annotations.Persist;

/**
 * Tests EnerJDatabase. This class does not repeat the basic ODMG Database tests
 * performed in BasicODMGTest.
 *
 * @version $Id: EnerJDatabaseTest.java,v 1.6 2006/06/09 02:39:23 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class EnerJDatabaseTest extends TestCase
{
    private static final String DATABASE_URI = "enerj://root:root@-/EnerJDatabaseTestDB?DefaultMetaObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";
    private static final String DATABASE2_URI = "enerj://root:root@-/EnerJDatabaseTestDB2?DefaultMetaObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";

    private static boolean sEnhanced = false;

    private Exception mThreadException;
    
    //----------------------------------------------------------------------
    public EnerJDatabaseTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(EnerJDatabaseTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(EnerJDatabaseTest.class);
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests that an objected created for one database cannot be reused in 
     * another.
     */
    public void testNoReuse() throws Exception
    {
        TestClass1 reuseObj;

        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        // Try sequentially.
        //  TODO  maybe later try in parallel (sending object to another thread)
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        EnerJTransaction txn = (EnerJTransaction)impl.newTransaction();
        txn.begin(db);

        try {
            reuseObj = new TestClass1(33);
            // Database ownership occurs here.
            db.bind(reuseObj, "Reuse");
        }
        finally {
            txn.commit();
            db.close();
        }
        
        // Now try to bind it to another database.
        db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE2_URI, Database.OPEN_READ_WRITE);

        txn = (EnerJTransaction)impl.newTransaction();
        txn.begin(db);

        // Should throw
        try {
            db.bind(reuseObj, "ReuseAgain");
            txn.commit();
            fail("Expected Exception");
        }
        catch (ODMGRuntimeException e) {
            // Expected
            System.out.println("Message: " + e);
        }
        finally {
            if (txn.isOpen()) {
                txn.abort();
            }

            db.close();
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests that a Database instance cannot be used by another Thread concurrently
     * while a transaction is active.
     */
    public void testNoThreadSharing() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);
        
        // Transaction not currently active. Another thread _should_ be able to
        // use the 'db' instance.
        final EnerJDatabase dbref = db;
        Thread testThread = new Thread("Test") {
            public void run() {
                EnerJTransaction txn = (EnerJTransaction)EnerJImplementation.getInstance().newTransaction();
                try {
                    txn.begin(dbref);
                    txn.commit();
                }
                catch (Exception e) {
                    // Not expected
                    mThreadException = e;
                    e.printStackTrace();
                }
            }
        };

        mThreadException = null;
        testThread.start();
        testThread.join();
        assertNull("Thread Exception was not expected", mThreadException);

        // Ok. Now open a transaction on the database 
        EnerJTransaction txn = (EnerJTransaction)impl.newTransaction();
        txn.begin(db);

        // Try to access in another thread, should get an exception
        try {
            testThread = new Thread("Test") {
                public void run() {
                    EnerJTransaction txn = (EnerJTransaction)EnerJImplementation.getInstance().newTransaction();
                    try {
                        txn.begin(dbref);
                        txn.commit();
                    }
                    catch (Exception e) {
                        // Expected
                        mThreadException = e;
                    }
                }
            };

            mThreadException = null;
            testThread.start();
            testThread.join();
            assertNotNull("Thread Exception was expected", mThreadException);
            System.out.println("Message: " + mThreadException);
        }
        finally {
            txn.commit();
        }
        
        // Transaction closed - not currently active. Another thread _should_ be able to
        // use the 'db' instance.
        testThread = new Thread("Test") {
            public void run() {
                EnerJTransaction txn = (EnerJTransaction)EnerJImplementation.getInstance().newTransaction();
                try {
                    txn.begin(dbref);
                    txn.commit();
                }
                catch (Exception e) {
                    // Not expected
                    mThreadException = e;
                    e.printStackTrace();
                }
            }
        };

        try {
            mThreadException = null;
            testThread.start();
            testThread.join();
            assertNull("Thread Exception was not expected", mThreadException);
        }
        finally {
            db.close();
        }
    }

    //----------------------------------------------------------------------
    /**
     * Test that JVM instances of the same OID are preserved between transactions, as long as the
     * instance is not GCed, as defined by JDO 5.5.4. 
     */
    public void testPreserveIdentity() throws Exception
    {
        TestClass1 obj1;
        TestClass1 obj2;

        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        try {
            obj1 = new TestClass1(33);
            db.bind(obj1, "IdObj");
        }
        finally {
            txn.commit();
        }
        

        // With the Database still open, retrieve the object in a new txn.
        // It should have the same JVM identity as the original object.
        txn = impl.newTransaction();
        txn.begin();

        try {
            obj2 = (TestClass1)db.lookup("IdObj");
            assertTrue("Two objects should be identical", obj1 == obj2);
        }
        finally {
            txn.commit();
            db.close();
        }
    }

    //----------------------------------------------------------------------
    /**
     * Test JDO 5.5.1:  a transient (but enhanced) instance referenced by persistent instances 
     * causes the transient object to get _persisted_ as if it were a new instance. This means that
     * objects created outside of a transaction can later be referenced inside a transaction and they become
     * new instances. 
     */
    public void testTransientToPersistentAutoStateChange() throws Exception
    {
        TestClass1 testClass1Obj = new TestClass1(3423);

        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        try {
            // Reference the transient instance indirectly here.
            db.bind( new TestClass2(testClass1Obj), "StateChgObj");
        }
        finally {
            txn.commit();
            db.close();
        }
        
        testClass1Obj = null;

        // With the Database still open, retrieve the object in a new txn.
        // It the previously transient object should have been persisted.
        
        // Use a new Database instance to ensure cache is not shared.
        db = (EnerJDatabase)impl.newDatabase();
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            TestClass2 testClass2Obj = (TestClass2)db.lookup("StateChgObj");
            testClass1Obj = testClass2Obj.getValue();
            assertNotNull("Reference to TestClass1 object should not be null", testClass1Obj);
            assertTrue("The value should be correct", testClass1Obj.getValue() == 3423);
        }
        finally {
            txn.commit();
            db.close();
        }
    }
    
    //----------------------------------------------------------------------
    /**
     *  Test non-transactional reads.
     */
    public void testNontransactionalRead() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        long oid;
        try {
            // Reference the transient instance indirectly here.
            TestClass1 testClass1Obj = new TestClass1(3423);
            db.bind(testClass1Obj, "namedobj");
            oid = db.getOID(testClass1Obj);
        }
        finally {
            txn.commit();
        }
        
        assertFalse( db.getAllowNontransactionalReads() );
        db.setAllowNontransactionalReads(true);
        assertTrue( db.getAllowNontransactionalReads() );
        
        // Should succeed
        TestClass1 obj1 = (TestClass1)db.lookup("namedobj");
        assertNotNull(obj1);
        
        TestClass1 obj2 = (TestClass1)db.getObjectForOID(oid);
        assertSame(obj1, obj2);
        // Test loading of obj1 and obj2
        assertEquals(obj1.getValue(), obj2.getValue());

        boolean found = false;
        for (TestClass1 obj3 : (Collection<TestClass1>)db.getExtent(obj1.getClass(), false)) {
            if (obj3 == obj1) {
                // Test loading of obj3
                assertEquals(obj1.getValue(), obj3.getValue());
                found = true;
                break;
            }
        }
        
        assertTrue(found);
        
        db.setAllowNontransactionalReads(false);
        assertFalse( db.getAllowNontransactionalReads() );
        
        try {
            // Should fail
            obj1 = (TestClass1)db.lookup("namedobj");
            fail("Expected exception");
        }
        catch (Exception e) {
            // Expected
        }

        try {
            // Should fail
            obj2 = (TestClass1)db.getObjectForOID(oid);
            fail("Expected exception");
        }
        catch (Exception e) {
            // Expected
        }

    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
 
    //----------------------------------------------------------------------
    @Persist
    private static class TestClass1
    {
        private int mValue;
        
        //----------------------------------------------------------------------
        TestClass1(int aValue)
        {
            mValue = aValue;
        }
            
        //----------------------------------------------------------------------
        int getValue()
        {
            return mValue;
        }

        //----------------------------------------------------------------------
        void setValue(int aValue)
        {
            mValue = aValue;
        }
    }

    //----------------------------------------------------------------------
    @Persist
    private static class TestClass2
    {
        private TestClass1 mValue;
        
        //----------------------------------------------------------------------
        TestClass2(TestClass1 aValue)
        {
            mValue = aValue;
        }
            
        //----------------------------------------------------------------------
        TestClass1 getValue()
        {
            return mValue;
        }

        //----------------------------------------------------------------------
        void setValue(TestClass1 aValue)
        {
            mValue = aValue;
        }
    }
}

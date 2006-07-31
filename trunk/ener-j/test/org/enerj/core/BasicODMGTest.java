// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/BasicODMGTest.java,v 1.2 2006/06/05 01:17:03 dsyrstad Exp $

package org.enerj.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.Transaction;
import org.enerj.annotations.Persist;

/**
 * Tests Basic ODMG functionality on Database and Transaction.
 *
 * @version $Id: BasicODMGTest.java,v 1.2 2006/06/05 01:17:03 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class BasicODMGTest extends TestCase
{
    private static final String DATABASE_URI = "enerj://root:root@-/BasicODMGTestDB?DefaultMetaObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";
    
    //----------------------------------------------------------------------
    public BasicODMGTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(BasicODMGTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(BasicODMGTest.class);
    }

    //----------------------------------------------------------------------
    /**
     * Tests Database.bind(), unbind(), lookup().
     */
    public void testBind() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        Database db = impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        try {
            for (int i = 0; i < 10; i++) {
                TestClass1 test = new TestClass1(i);
                db.bind(test, "Link" + i);
            }
        }
        finally {
            txn.commit();
            db.close();
        }

        //------------------------------------------------------------------------------------
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            for (int i = 0; i < 10; i++) {
                TestClass1 test = (TestClass1)db.lookup("Link" + i);
                assertTrue("Values should be the same", test.getValue() == i);
            }
        }
        finally {
            txn.commit();
            db.close();
        }

        //------------------------------------------------------------------------------------
        Database db2 = impl.newDatabase();
        db2.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            // unbind odd numbered items
            for (int i = 1; i < 10; i += 2) {
                db2.unbind("Link" + i);
            }
        }
        finally {
            txn.commit();
            db2.close();
        }

        //------------------------------------------------------------------------------------
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            // Attempt to retrieve odd numbered items
            for (int i = 1; i < 10; i += 2) {
                // Should throw exception
                try {
                    TestClass1 test = (TestClass1)db.lookup("Link" + i);
                    fail("Expected ObjectNameNotFoundException");
                }
                catch (ObjectNameNotFoundException e) {
                    // Expected.
                }
            }
        }
        finally {
            txn.commit();
            db.close();
        }

        //------------------------------------------------------------------------------------
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            // Retrieve even numbered items
            for (int i = 0; i < 10; i += 2) {
                TestClass1 test = (TestClass1)db.lookup("Link" + i);
                assertTrue("Values should be the same", test.getValue() == i);
            }
        }
        finally {
            txn.commit();
            db.close();
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests a do-nothing transaction open/begin/commit(or abort)/close.
     * Tests empty interal commit/abort object lists.
     */
    public void testDoNothingTxn() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        Database db = impl.newDatabase();
        
        // open/begin/commit/close.
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();
        
        txn.commit();
        db.close();

        // open/begin/abort/close.
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();
        
        txn.abort();
        db.close();
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests starting a transaction with an unopened Database.
     */
    public void testTxnWithUnopenDatabase() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        Database db = impl.newDatabase();
        
        // Test not opened yet.
        Transaction txn = impl.newTransaction();
        try {
            txn.begin();
            fail("Expected Exception");
        }
        catch (org.odmg.DatabaseClosedException e) {
            // Expected
        }

        // test open/begin/commit/close followed by new txn
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);
        txn.begin();
        txn.commit();
        db.close();
        
        try {
            txn.begin();
            fail("Expected Exception");
        }
        catch (org.odmg.DatabaseClosedException e) {
            // Expected
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests closing a Database with an open transaction.
     */
    public void testCloseDatabaseWithOpenTxn() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        Database db = impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();
        try {
            db.close();
            fail("Expected Exception");
        }
        catch (org.odmg.TransactionInProgressException e) {
            // Expected
        }
        finally {
            txn.commit();
            db.close();
        }
    }
    
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
}
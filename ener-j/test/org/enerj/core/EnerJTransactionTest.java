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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/EnerJTransactionTest.java,v 1.3 2006/06/09 02:39:23 dsyrstad Exp $

package org.enerj.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.odmg.Database;
import org.odmg.Implementation;
import org.enerj.annotations.Persist;

/**
 * Tests EnerJTransaction. This class does not repeat the basic ODMG Database tests
 * performed in BasicODMGTest.
 *
 * @version $Id: EnerJTransactionTest.java,v 1.3 2006/06/09 02:39:23 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class EnerJTransactionTest extends TestCase
{
    private static final String DATABASE_URI = "enerj://root:root@-/EnerJTransactionTestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";

    private Exception mThreadException;
    
    //----------------------------------------------------------------------
    public EnerJTransactionTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(EnerJTransactionTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(EnerJTransactionTest.class);
    }
    
    //----------------------------------------------------------------------
    /**
     * Test that a Transaction cannot be simulatenously shared between threads while it is open.
     */
    public void testNotSharableWhileOpen() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        EnerJTransaction txn = (EnerJTransaction)impl.newTransaction();
        txn.begin(db);

        final EnerJTransaction txnRef = txn;
        Thread testThread = new Thread("Test") {
            public void run() {
                try {
                    txnRef.checkpoint();
                    // Should not reach here
                }
                catch (org.odmg.TransactionNotInProgressException e) {
                    // Expected
                    mThreadException = e;
                }
            }
        };


        try {
            mThreadException = null;
            testThread.start();
            testThread.join();
            assertNotNull("Thread Exception was expected", mThreadException);
        }
        finally {
            txn.commit();
            db.close();
        }
    }

    //----------------------------------------------------------------------
    /**
     * Test leave/join thread (and implicit leave via join).
     */
    public void testLeaveJoin() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        EnerJTransaction txn = (EnerJTransaction)impl.newTransaction();
        txn.begin(db);

        final EnerJTransaction txnRef = txn;
        Thread testThread = new Thread("Test") {
            public void run() {
                try {
                    txnRef.join(); // implicit leave
                    txnRef.checkpoint();
                    txnRef.leave();
                }
                catch (Exception e) {
                    // Not Expected
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
            
            // Now join the transaction - it was explicitly left via leave().
            txn.join();
            txn.checkpoint();
        }
        finally {
            txn.commit();
            db.close();
        }
    }

    //----------------------------------------------------------------------
    /**
     * Test that abort() rolls back in database.
     */
    public void testAbort() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        EnerJTransaction txn = (EnerJTransaction)impl.newTransaction();

        // Regular object modification should rollback.
        txn.begin(db);
        db.bind(new TestClass2(null), "AbortNull");
        txn.commit();
        
        txn.begin(db);
        try {
            // This change should not be saved.
            TestClass2 testClass2Obj = (TestClass2)db.lookup("AbortNull");
            testClass2Obj.setValue( new TestClass1(383) );
            txn.abort();

            txn.begin(db);
            testClass2Obj = (TestClass2)db.lookup("AbortNull");
            System.out.println("value=" + testClass2Obj.getValue());
            assertNull("Referenece should be null, as originally committed", testClass2Obj.getValue() );
        }
        finally {
            if (txn.isOpen()) {
                txn.commit();
            }
        }

        // bind() should rollback
        txn.begin(db);
        try {
            // This should not be saved.
            for (int i = 0; i < 100; i++) {
                db.bind(new TestClass1(22), "Abort" + i);
            }
            
            txn.abort();
            
            txn.begin(db);
            for (int i = 0; i < 100; i++) {
                try {
                    db.lookup("Abort" + i);
                    fail("Exception Expected: " + i);
                }
                catch (org.odmg.ObjectNameNotFoundException e) {
                    // Expected
                }
            }
        }
        finally {
            txn.commit();
            db.close();
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

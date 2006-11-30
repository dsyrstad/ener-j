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
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/ExtentTest.java,v 1.2 2006/06/09 02:39:24 dsyrstad Exp $

package org.enerj.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.annotations.Persist;
import org.odmg.DList;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.ODMGRuntimeException;
import org.odmg.Transaction;

/**
 * Tests Extent functionality on EnerJDatabase.
 *
 * @version $Id: ExtentTest.java,v 1.2 2006/06/09 02:39:24 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class ExtentTest extends AbstractDatabaseTestCase
{
    public ExtentTest(String aTestName) 
    {
        super(aTestName);
    }

    public static Test suite() 
    {
        return new TestSuite(ExtentTest.class);
    }

    /**
     * Tests addition to extent via reachability.
     */
    public void testViaReachability() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        try {
            db.bind(new TestClass1( new TestClass2(10) ), "obj");
        }
        finally {
            txn.commit();
            db.close();
        }

        db = (EnerJDatabase)impl.newDatabase();
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            Extent extent = db.getExtent(TestClass2.class, true);

            assertEquals(extent.getCandidateClass(), TestClass2.class);
            assertTrue( extent.hasSubclasses() );
            Iterator iterator = extent.iterator();

            assertTrue( iterator.hasNext() );
            TestClass2 obj = (TestClass2)iterator.next();
            assertEquals(obj.getValue(), 10);
            assertFalse( iterator.hasNext() );
            
            extent.close(iterator);
        }
        finally {
            txn.commit();
            db.close();
        }

    }

    /**
     * Tests addition to extent via bind().
     */
    public void testViaBind() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        try {
            db.bind(new TestClass1(5), "obj");
        }
        finally {
            txn.commit();
            db.close();
        }

        db = (EnerJDatabase)impl.newDatabase();
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            Extent extent = db.getExtent(TestClass1.class, false);

            assertEquals(extent.getCandidateClass(), TestClass1.class);
            assertFalse( extent.hasSubclasses() );
            Iterator iterator = extent.iterator();

            assertTrue( iterator.hasNext() );
            TestClass1 obj = (TestClass1)iterator.next();
            assertEquals(obj.getValue(), 5);
            assertFalse( iterator.hasNext() );
            
            extent.close(iterator);
        }
        finally {
            txn.commit();
            db.close();
        }

    }


    /**
     * Tests addition to extent via makePersistent().
     */
    public void testViaMakePersistent() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        try {
            db.makePersistent( new TestClass1( new TestClass2(12) ) );
        }
        finally {
            txn.commit();
            db.close();
        }

        db = (EnerJDatabase)impl.newDatabase();
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            // Extent on TestClass1
            Extent extent = db.getExtent(TestClass1.class, false);

            assertEquals(extent.getCandidateClass(), TestClass1.class);
            assertFalse( extent.hasSubclasses() );
            Iterator iterator = extent.iterator();

            assertTrue( iterator.hasNext() );
            TestClass1 testClass1 = (TestClass1)iterator.next();
            TestClass2 testClass2 = (TestClass2)testClass1.getObj();
            assertEquals( testClass2.getValue(), 12);
            assertFalse( iterator.hasNext() );

            extent.close(iterator);

            // Extent on TestClass2
            extent = db.getExtent(TestClass2.class, true);

            assertEquals(extent.getCandidateClass(), TestClass2.class);
            assertTrue( extent.hasSubclasses() );
            iterator = extent.iterator();

            assertTrue( iterator.hasNext() );
            testClass2 = (TestClass2)iterator.next();
            assertEquals(testClass2.getValue(), 12);
            assertFalse( iterator.hasNext() );

            extent.close(iterator);

            // For grins, test that deletePersistent deletes from extent.
            db.deletePersistent(testClass1);
            extent = db.getExtent(TestClass1.class, false);
            iterator = extent.iterator();
            assertFalse( iterator.hasNext() );
            extent.close(iterator);

            // But TestClass2 object should still be in its extent.
            extent = db.getExtent(TestClass2.class, false);
            iterator = extent.iterator();
            assertTrue( iterator.hasNext() );
            testClass2 = (TestClass2)iterator.next();
            extent.close(iterator);

            // deletePersistent on TestClass2 object, *without* getting a new Extent object.
            db.deletePersistent(testClass2);
            iterator = extent.iterator();
            assertFalse( iterator.hasNext() );
        }
        finally {
            txn.commit();
            db.close();
        }

    }


    /**
     * Tests close() and closeAll().
     */
    public void testClose() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        DList list = impl.newDList();
        list.add(new TestClass1(5));
        list.add(new TestClass1(6));
        try {
            db.bind(list, "obj");
        }
        finally {
            txn.commit();
            db.close();
        }

        db = (EnerJDatabase)impl.newDatabase();
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            Extent extent = db.getExtent(TestClass1.class, false);
            Iterator iterator = extent.iterator();

            assertTrue( iterator.hasNext() );
            extent.close(iterator);
            try {
                iterator.hasNext();
                fail("Expected exception");
            }
            catch (ODMGRuntimeException e) {
                // Expected.
            }

            try {
                iterator.next();
                fail("Expected exception");
            }
            catch (ODMGRuntimeException e) {
                // Expected.
            }

            // Try two open with closeAll()
            iterator = extent.iterator();
            Iterator iterator2 = extent.iterator();

            assertTrue( iterator.hasNext() );
            assertTrue( iterator2.hasNext() );
            extent.closeAll();
            try {
                iterator.hasNext();
                fail("Expected exception");
            }
            catch (ODMGRuntimeException e) {
                // Expected.
            }

            try {
                iterator.next();
                fail("Expected exception");
            }
            catch (ODMGRuntimeException e) {
                // Expected.
            }

            try {
                iterator2.hasNext();
                fail("Expected exception");
            }
            catch (ODMGRuntimeException e) {
                // Expected.
            }

            try {
                iterator2.next();
                fail("Expected exception");
            }
            catch (ODMGRuntimeException e) {
                // Expected.
            }
        }
        finally {
            txn.commit();
            db.close();
        }

    }


    /**
     * Tests extents with subclasses.
     */
    public void testSubclasses() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        TestClass1 testClass1;
        TestClass3 testClass3;

        DList list = impl.newDList();
        list.add(new TestClass1(1));
        list.add(new TestClass1(2));
        list.add(new TestClass3(3, 10));
        list.add(new TestClass3(4, 20));

        HashSet expectedValues = new HashSet();
        HashSet expectedValues2 = new HashSet();
        int expectedTestClass1Instances = 0;
        int expectedTestClass3Instances = 0;
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            testClass1 = (TestClass1)iter.next();
            if (testClass1 instanceof TestClass3) {
                ++expectedTestClass3Instances;
                testClass3 = (TestClass3)testClass1;
                expectedValues2.add( new Long(testClass3.getValue2()) );
            }
            else {
                ++expectedTestClass1Instances;
                expectedValues.add( new Long(testClass1.getValue()) );
            }
        }

        try {
            db.bind(list, "obj");
        }
        finally {
            txn.commit();
            db.close();
        }

        db = (EnerJDatabase)impl.newDatabase();
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            // Test with subclass instances.
            Extent extent = db.getExtent(TestClass1.class, true);
            Iterator iterator = extent.iterator();

            HashSet actualValues = (HashSet)expectedValues.clone();
            HashSet actualValues2 = (HashSet)expectedValues2.clone();
            int actualTestClass1Instances = 0;
            int actualTestClass3Instances = 0;
            while (iterator.hasNext()) {
                testClass1 = (TestClass1)iterator.next();
                if (testClass1 instanceof TestClass3) {
                    ++actualTestClass3Instances;
                    testClass3 = (TestClass3)testClass1;
                    actualValues2.remove( new Long(testClass3.getValue2()) );
                }
                else {
                    ++actualTestClass1Instances;
                    actualValues.remove( new Long(testClass1.getValue()) );
                }
            }

            assertEquals(expectedTestClass1Instances, actualTestClass1Instances);
            assertEquals(expectedTestClass3Instances, actualTestClass3Instances);
            assertTrue("Didn't find all TestClass1 instances", actualValues.isEmpty());
            assertTrue("Didn't find all TestClass3 instances", actualValues2.isEmpty());
            extent.close(iterator);

            // Test without subclass instances.
            extent = db.getExtent(TestClass1.class, false);
            iterator = extent.iterator();

            actualValues = (HashSet)expectedValues.clone();
            actualValues2 = (HashSet)expectedValues2.clone();
            actualTestClass1Instances = 0;
            actualTestClass3Instances = 0;
            while (iterator.hasNext()) {
                testClass1 = (TestClass1)iterator.next();
                if (testClass1 instanceof TestClass3) {
                    ++actualTestClass3Instances;
                    testClass3 = (TestClass3)testClass1;
                    actualValues2.remove( new Long(testClass3.getValue2()) );
                }
                else {
                    ++actualTestClass1Instances;
                    actualValues.remove( new Long(testClass1.getValue()) );
                }
            }

            assertEquals(expectedTestClass1Instances, actualTestClass1Instances);
            assertEquals(0, actualTestClass3Instances);
            assertTrue("Didn't find all TestClass1 instances", actualValues.isEmpty());
            assertEquals(expectedValues2.size(), actualValues2.size());
            extent.close(iterator);
        }
        finally {
            txn.commit();
            db.close();
        }
    }


    /**
     * Tests extents with many objects to verify the integrity of the objects returned.
     */
    public void xtestLargeExtents() throws Exception
    {
        final int NUM_OBJS = 100000;

        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        TestClass1 testClass1;
        TestClass3 testClass3;

        DList list = impl.newDList();

        int expectedTestClass1Instances = 0;
        int expectedTestClass3Instances = 0;
        long expectedTestClass1Sum = 0;
        long expectedTestClass3Sum = 0;
        long start = System.currentTimeMillis();
        Random random = new Random();
        for (int i = 0; i < NUM_OBJS; i++) {
            if (random.nextInt(2) == 0) {
                list.add( new TestClass1(i) );
                expectedTestClass1Sum += i;
                ++expectedTestClass1Instances;
            }
            else {
                int secondValue = i * random.nextInt(10);
                list.add( new TestClass3(i, secondValue) );
                expectedTestClass3Sum += secondValue;
                ++expectedTestClass3Instances;
            }
        }

        try {
            db.bind(list, "obj");
        }
        finally {
            txn.commit();
            System.out.println("Load of " + NUM_OBJS + " objects took " + (System.currentTimeMillis() - start) + " ms");
            db.close();
        }

        db = (EnerJDatabase)impl.newDatabase();
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            // Test - make sure all of the sums match.
            start = System.currentTimeMillis();
            Extent extent = db.getExtent(TestClass1.class, true);
            Iterator iterator = extent.iterator();

            int actualTestClass1Instances = 0;
            int actualTestClass3Instances = 0;
            long actualTestClass1Sum = 0;
            long actualTestClass3Sum = 0;
            while (iterator.hasNext()) {
                testClass1 = (TestClass1)iterator.next();
                if (testClass1 instanceof TestClass3) {
                    ++actualTestClass3Instances;
                    testClass3 = (TestClass3)testClass1;
                    actualTestClass3Sum += testClass3.getValue2();
                }
                else {
                    ++actualTestClass1Instances;
                    actualTestClass1Sum += testClass1.getValue();
                }
            }

            assertEquals(expectedTestClass1Instances, actualTestClass1Instances);
            assertEquals(expectedTestClass3Instances, actualTestClass3Instances);
            assertEquals(expectedTestClass1Sum, actualTestClass1Sum);
            assertEquals(expectedTestClass3Sum, actualTestClass3Sum);
            extent.close(iterator);
            System.out.println("Read of " + NUM_OBJS + " objects took " + (System.currentTimeMillis() - start) + " ms");
        }
        finally {
            txn.commit();
            db.close();
        }
    }


    /**
     * Tests that two iterators can maintain separate positions on same extent.
     * Note that testClose() also indirectly tests two iterators for closing purposes.
     */
    public void testTwoIterators() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        DList list = impl.newDList();
        list.add(new TestClass1(5));
        list.add(new TestClass1(6));
        try {
            db.bind(list, "obj");
        }
        finally {
            txn.commit();
            db.close();
        }

        db = (EnerJDatabase)impl.newDatabase();
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            Extent extent = db.getExtent(TestClass1.class, false);
            Iterator iterator = extent.iterator();
            Iterator iterator2 = extent.iterator();

            assertTrue( iterator.hasNext() );
            assertTrue( iterator2.hasNext() );

            iterator.next();
            iterator.next();
            assertFalse( iterator.hasNext() );
            assertTrue( iterator2.hasNext() );

            iterator2.next();
            assertTrue( iterator2.hasNext() );

            iterator2.next();
            assertFalse( iterator2.hasNext() );

            extent.closeAll();
        }
        finally {
            txn.commit();
            db.close();
        }
    }


    /**
     * Tests that next() throws the proper exception when hasNext() returns false.
     * Also tests that remove() throws UnsupportedOperationException.
     */
    public void testNextRemoveException() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();

        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        DList list = impl.newDList();
        list.add(new TestClass1(5));
        try {
            db.bind(list, "obj");
        }
        finally {
            txn.commit();
            db.close();
        }

        db = (EnerJDatabase)impl.newDatabase();
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            Extent extent = db.getExtent(TestClass1.class, false);
            Iterator iterator = extent.iterator();

            assertTrue( iterator.hasNext() );
            iterator.next();

            // remove() should throw
            try {
                iterator.remove();
                fail("Expected an exception");
            }
            catch (UnsupportedOperationException e) {
                // Expected
            }

            assertFalse( iterator.hasNext() );

            // next() should throw
            try {
                iterator.next();
                fail("Expected an exception");
            }
            catch (NoSuchElementException e) {
                // Expected
            }

            extent.closeAll();
        }
        finally {
            txn.commit();
            db.close();
        }
    }



    @Persist
    private static class TestClass1
    {
        private int mValue;
        private Object mObj;
        

        TestClass1(int aValue)
        {
            mValue = aValue;
        }


        TestClass1(Object anObj)
        {
            mObj = anObj;
        }
            

        int getValue()
        {
            return mValue;
        }


        void setValue(int aValue)
        {
            mValue = aValue;
        }


        Object getObj()
        {
            return mObj;
        }
    }



    @Persist
    private static class TestClass2
    {
        private int mValue;
        

        TestClass2(int aValue)
        {
            mValue = aValue;
        }
            

        int getValue()
        {
            return mValue;
        }


        void setValue(int aValue)
        {
            mValue = aValue;
        }
    }



    @Persist
    private static class TestClass3 extends TestClass1
    {
        private int mValue2;


        TestClass3(int aValue, int aValue2)
        {
            super(aValue);
            mValue2 = aValue2;
        }


        int getValue2()
        {
            return mValue2;
        }


        void setValue2(int aValue2)
        {
            mValue2 = aValue2;
        }
    }
}

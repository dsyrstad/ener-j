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

package org.enerj.core;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.annotations.Index;
import org.enerj.annotations.Indexes;
import org.enerj.annotations.Persist;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.Transaction;

/**
 * Tests Index functionality.
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class IndexTest extends DatabaseTestCase
{
    public IndexTest(String aTestName) 
    {
        super(aTestName);
    }
    

    public static Test suite() 
    {
        return new TestSuite(IndexTest.class);
    }


    /**
     * Tests 
     */
    public void testIterator() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        String[] strValues = { "Orange", "Red", "Brown", "Green", "Blue", "Black", "Yellow" };
        final int numObjs = 1000;
        long colorRangeSize = 0;
        long le12Size = 0;
        long ge10Le12Size = 0;
        long ge10Size = 0;
        Random rand = new Random();
        // Track values to ensure they're all found later.
        List<Integer> values = new ArrayList<Integer>(numObjs);
        try {
            for (int i = 0; i < numObjs; i++) {
                int value = rand.nextInt(5000);
                if (value <= 12) {
                    ++le12Size;
                    if (value >= 10) {
                        ++ge10Le12Size;
                    }
                }

                if (value >= 10) {
                    ++ge10Size;
                }
                
                values.add((Integer)value);
                String string = strValues[i % strValues.length];
                if (string.equals("Green") || string.equals("Orange") || string.equals("Red")) {
                    ++colorRangeSize;
                }
                
                TestClass1 test = new TestClass1(value, string);
                db.makePersistent(test);
            }
        }
        finally {
            txn.commit();
            db.close();
        }

        // Re-open and read from value index iterator.
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            // Check size.
            long size = db.getIndexKeyRangeSize(TestClass1.class, "valueIndex", null, null);
            assertEquals((long)numObjs, size);
            
            IndexIterator<TestClass1> iter = db.getIndexIterator(TestClass1.class, "valueIndex", null, null);
            int lastValue = -1;
            while (iter.hasNext()) {
                TestClass1 obj = iter.next();
                int value = obj.getValue();
                if (lastValue >= 0) {
                    assertTrue(value >= lastValue);
                }
             
                // Remove values from set 
                values.remove((Integer)value);
                lastValue = value;
            }
            
            iter.close();
            
            assertTrue("Not all values found", values.isEmpty());
        }
        finally {
            txn.commit();
            db.close();
        }

        // Re-open and read from string index iterator.
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            // Check size.
            long size = db.getIndexKeyRangeSize(TestClass1.class, "stringIndex", null, null);
            assertEquals((long)numObjs, size);
            
            IndexIterator<TestClass1> iter = db.getIndexIterator(TestClass1.class, "stringIndex", null, null);
            String lastValue = null;
            while (iter.hasNext()) {
                TestClass1 obj = iter.next();
                String value = obj.getString();
                if (lastValue != null) {
                    assertTrue(value.compareTo(lastValue) >= 0);
                }
             
                lastValue = value;
            }
            
            iter.close();
            
            // Try ranges on full keys. Green, Orange, Red
            GenericKey greenStartKey = new GenericKey( new Object[] { "Green"} );
            GenericKey redEndKey = new GenericKey( new Object[] { "Red"} );
            size = db.getIndexKeyRangeSize(TestClass1.class, "stringIndex", greenStartKey, redEndKey);
            assertEquals(colorRangeSize, size);

            iter = db.getIndexIterator(TestClass1.class, "stringIndex", greenStartKey, redEndKey);
            long iterRangeSize = 0;
            while (iter.hasNext()) {
                TestClass1 obj = iter.next();
                ++iterRangeSize;
                String value = obj.getString();
                assertTrue(value.equals("Green") || value.equals("Orange") || value.equals("Red"));
            }

            assertEquals(iterRangeSize, size);
            
            iter.close();
            
            // Try open start and bounded end key
            GenericKey end12Key = new GenericKey( new Object[] { 12 } );
            size = db.getIndexKeyRangeSize(TestClass1.class, "valueIndex", null, end12Key);
            assertEquals(le12Size, size);

            iter = db.getIndexIterator(TestClass1.class, "valueIndex", null, end12Key);
            iterRangeSize = 0;
            while (iter.hasNext()) {
                TestClass1 obj = iter.next();
                ++iterRangeSize;
                assertTrue( obj.getValue() <= 12 );
            }

            assertEquals(le12Size, iterRangeSize);
            iter.close();

            // Try a fully bounded search
            GenericKey start10Key = new GenericKey( new Object[] { 10 } );
            size = db.getIndexKeyRangeSize(TestClass1.class, "valueIndex", start10Key, end12Key);
            assertEquals(ge10Le12Size, size);

            iter = db.getIndexIterator(TestClass1.class, "valueIndex", start10Key, end12Key);
            iterRangeSize = 0;
            while (iter.hasNext()) {
                TestClass1 obj = iter.next();
                ++iterRangeSize;
                assertTrue( obj.getValue() <= 12 && obj.getValue() >= 10 );
            }

            assertEquals(ge10Le12Size, iterRangeSize);
            iter.close();
            
            // Try a bounded start and open end search
            size = db.getIndexKeyRangeSize(TestClass1.class, "valueIndex", start10Key, null);
            assertEquals(ge10Size, size);

            iter = db.getIndexIterator(TestClass1.class, "valueIndex", start10Key, null);
            iterRangeSize = 0;
            while (iter.hasNext()) {
                TestClass1 obj = iter.next();
                ++iterRangeSize;
                assertTrue( obj.getValue() >= 10 );
            }
            
            // Trying next() should get a NoSuchElementException
            try {
                iter.next();
                fail("Expected NoSuchElementException");
            }
            catch (NoSuchElementException e) {
                // Expected
            }

            assertEquals(ge10Size, iterRangeSize);
            iter.close();
            
        }
        finally {
            txn.commit();
            db.close();
        }

    }

    public void testUpdateKey() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        EnerJTransaction txn = (EnerJTransaction)impl.newTransaction();
        txn.begin();

        try {
            db.makePersistent( new TestClass1(1, "one") );
            db.makePersistent( new TestClass1(2, "two") );
            db.makePersistent( new TestClass1(3, "three") );
        }
        finally {
            txn.commit();
            db.close();
        }

        // Re-open and update keys
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = (EnerJTransaction)impl.newTransaction();
        txn.begin();

        try {
            IndexIterator<TestClass1> iter = db.getIndexIterator(TestClass1.class, "valueIndex", null, null);
            while (iter.hasNext()) {
                TestClass1 obj = iter.next();
                obj.setValue( 10 - obj.getValue() ); // Cause reversal of ordering
            }
            
            iter.close();
        }
        finally {
            txn.commit();
            db.close();
        }

        // Read keys after commit. Make sure the values 7, 8, 9 are there, in the new order.
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = (EnerJTransaction)impl.newTransaction();
        txn.begin();
        try {
            IndexIterator<TestClass1> iter = db.getIndexIterator(TestClass1.class, "valueIndex", null, null);

            TestClass1 obj = iter.next();
            assertEquals(7, obj.getValue() );
            assertEquals("three", obj.getString() );

            obj = iter.next();
            assertEquals(8, obj.getValue() );
            assertEquals("two", obj.getString() );
            
            obj = iter.next();
            assertEquals(9, obj.getValue() );
            assertEquals("one", obj.getString() );

            assertFalse(iter.hasNext());
            
            iter.close();
        }
        finally {
            txn.commit();
            db.close();
        }
    }

    @Persist
    @Indexes( {
        @Index(name="stringIndex", properties={ "string" }),
        @Index(name="valueIndex", properties={ "value" }),
        @Index(name="valueStringIndex", properties={ "value", "string" })
    } )
    public static class TestClass1
    {
        private int mValue;
        private String mString;
        

        TestClass1(int aValue, String aString)
        {
            mValue = aValue;
            mString = aString;
        }
            

        public int getValue()
        {
            return mValue;
        }


        public void setValue(int aValue)
        {
            mValue = aValue;
        }


        public String getString()
        {
            return mString;
        }


        public void setString(String someString)
        {
            mString = someString;
        }
        
        public String toString()
        {
            return "value=" + mValue + " String=" + mString;
        }
    }
}

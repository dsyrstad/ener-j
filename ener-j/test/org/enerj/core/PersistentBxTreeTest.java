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
//$Header: $

package org.enerj.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.annotations.Persist;
import org.enerj.annotations.PersistenceAware;
import org.enerj.apache.commons.collections.comparators.NullComparator;
import org.enerj.apache.commons.collections.map.AbstractTestSortedMap;
import org.enerj.util.StringUtil;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.QueryableCollection;
import org.odmg.Transaction;

/**
 * Tests PersistentBxTree. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@PersistenceAware
public class PersistentBxTreeTest extends AbstractDatabaseTestCase
{
    private static final String[] FIRST_NAMES = { "Dan", "Tina", "Bob", "Sue", "Emily", "Cole", "Mike", "Borusik", "Ole", "Lena", };
    private static final String[] LAST_NAMES = { "Smith", "Jones", "Funkmeister", "Johnson", "Anderson", "Syrstad", "Robinson",  };
    private static final String[] CITIES = { "Burnsville", "Bloomington", "Minneapolis", "St Paul", "Washington", "Seattle", "Phoenix", "New York", "Clevland", "San Jose", };

    /**
     * Construct a PersistentBxTreeTest. 
     *
     * @param arg0
     */
    public PersistentBxTreeTest(String arg0)
    {
        super(arg0);
    }


    public static Test suite() 
    {
        TestSuite suite = new TestSuite(PersistentBxTreeTest.class);
        
        suite.addTestSuite( PersistentBxTreeTest.InternalSortedMapTest.class );
        suite.addTestSuite( PersistentBxTreeTest.InternalQueryableCollectionTest.class );
        suite.addTestSuite( PersistentBxTreeTest.ApacheCollectionsSortedMapTest.class );

        return suite;
    }
    
    /**
     * Test method for {@link org.enerj.core.PersistentBxTree#put(java.lang.Object, org.enerj.core.Persistable)}.
     */
    public void xtestLargeRandomPut() throws Exception // TODO Uncomment
    {
        // Create an array of objects and the shuffle them.
        TestClass1[] objs = new TestClass1[100000];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = new TestClass1(i,
                            FIRST_NAMES[ i % FIRST_NAMES.length ],
                            LAST_NAMES[ i % LAST_NAMES.length ],
                            CITIES[ i % CITIES.length ]);
        }
        
        // Shuffle array using a consistent seed
        Collections.shuffle(Arrays.asList(objs), new Random(1L));

        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = new EnerJDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        long start = System.currentTimeMillis();
        try {
            PersistentBxTree<Integer, TestClass1> tree = new PersistentBxTree<Integer, TestClass1>(10, null, false, false, true);
            db.bind(tree, "BTree");
            for (int i = 0; i < objs.length; i++) {
                TestClass1 obj = objs[i];
                tree.insert(obj.getId(), obj);

                try {
                }
                catch (IllegalStateException e) {
                    System.out.println("While inserting " + obj.getId() + " on iteration " + i);
                    tree.dumpTree();
                    throw e;
                }
            }
        }
        finally {
            txn.commit();
            db.close();
        }

        long end = System.currentTimeMillis();
        System.out.println("Insert time " + (end-start) + "ms");

        db = new EnerJDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);
        db.setAllowNontransactionalReads(true);

        start = System.currentTimeMillis();
        try {
            PersistentBxTree<Integer, TestClass1> tree = (PersistentBxTree<Integer, TestClass1>)db.lookup("BTree");

            // Make sure that the tree is valid.
            tree.validateTree();

            for (TestClass1 obj : objs) {
                assertTrue("Id does not exist " + obj.getId(), tree.containsKey(obj.getId()) );
                TestClass1 getObj = tree.get(obj.getId());
                assertNotNull(getObj);
                assertEquals(obj.getId(), getObj.getId());
                if (obj.getLastName() == null) {
                    System.out.println(StringUtil.toString(obj, false, true));
                }
                
                assertEquals(obj.getLastName(), getObj.getLastName() );
            }
        }
        finally {
            db.close();
        }

        end = System.currentTimeMillis();
        System.out.println("ContainsKey/Get time " + (end-start) + "ms");
    }

    // TODO Test subtests with large tree, null keys, dupl keys, dynamic/static resize, reverswed order
    @Persist
    private static final class TestClass1
    {
        private int mId;
        private String mFirstName;
        private String mLastName;
        private String mCity;
        
        TestClass1(int anId, String aFirstName, String aLastName, String aCity)
        {
            mId = anId;
            mFirstName = aFirstName;
            mLastName = aLastName;
            mCity = aCity;
        }

        String getCity()
        {
            return mCity;
        }

        String getFirstName()
        {
            return mFirstName;
        }

        int getId()
        {
            return mId;
        }

        String getLastName()
        {
            return mLastName;
        }
    }

    // Inner classes used to test interfaces and abstract classes.

    /**
     * Tests SortedMap interface of SortedMap.
     */
    public static final class InternalSortedMapTest extends AbstractSortedMapTest
    {
        private EnerJDatabase mDB;
        private EnerJTransaction mTxn;
        
        public InternalSortedMapTest(String aName)
        {
            super(aName);
        }

        public void setUp() throws Exception
        {
            System.gc(); System.out.println("Mem1 before open=" + Runtime.getRuntime().freeMemory());
            super.setUp();
            AbstractDatabaseTestCase.createDatabase1();
            mDB = new EnerJDatabase();
            mDB.open(AbstractDatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
            mTxn = new EnerJTransaction();
            mTxn.begin(mDB);
            System.gc(); System.out.println("Mem1 after opent=" + Runtime.getRuntime().freeMemory());
        }


        public void tearDown() throws Exception
        {
            System.gc(); System.out.println("Mem1 before commit=" + Runtime.getRuntime().freeMemory());
            mTxn.commit();
            mDB.close();
            AbstractDatabaseTestCase.clearDBFiles();
            super.tearDown();
            System.gc(); System.out.println("Mem1 after commit=" + Runtime.getRuntime().freeMemory());
        }

        public Map createMap() throws Exception
        {
            return new PersistentBxTree<Integer, TestClass1>(10, null, false, false, true);
        }


        public boolean allowsNullKeys()
        {
            return true;
        }
    }
    
    
    /**
     * Uses Apache Collections Tests.
     */
    public static final class ApacheCollectionsSortedMapTest extends AbstractTestSortedMap
    {
        private EnerJDatabase mDB;
        private EnerJTransaction mTxn;
        
        public ApacheCollectionsSortedMapTest(String aName)
        {
            super(aName);
        }

        public void setUp() throws Exception
        {
            super.setUp();
            AbstractDatabaseTestCase.createDatabase1();
            mDB = new EnerJDatabase();
            mDB.open(AbstractDatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
            mTxn = new EnerJTransaction();
            mTxn.begin(mDB);
        }


        public void tearDown() throws Exception
        {
            mTxn.commit();
            mDB.close();
            AbstractDatabaseTestCase.clearDBFiles();
            super.tearDown();
        }

        @Override
        public void testSimpleSerialization() throws Exception
        {
            if (isTestSerialization()) {
                super.testSimpleSerialization();
            }
            // Else don't test this. We don't need to support it.
        }

        @Override
        public void testSerializeDeserializeThenCompare() throws Exception
        {
            if (isTestSerialization()) {
                super.testSerializeDeserializeThenCompare();
            }
            // Else don't test this. We don't need to support it.
        }

        // TODO Larger collections via getSample...
        @Override
        public Map makeEmptyMap()
        {
            return new PersistentBxTree<Integer, TestClass1>(10, null, false, false, true);
        }

        @Override
        public Map makeConfirmedMap() 
        {
            return new TreeMap( new NullComparator() );
        }

        @Override
        public boolean isAllowNullKey()
        {
            return true;
        }

        @Override
        public boolean isAllowNullValue()
        {
            return true;
        }

        @Override
        public boolean isTestSerialization()
        {
            return false; // We don't need to support this
        }
    }
    
    /**
     * Tests QueryableCollection interface.
     */
    public static final class InternalQueryableCollectionTest extends AbstractQueryableCollectionTest
    {
        private EnerJDatabase mDB;
        private EnerJTransaction mTxn;
        

        public InternalQueryableCollectionTest(String aName)
        {
            super(aName);
        }


        public void setUp() throws Exception
        {
            super.setUp();
            AbstractDatabaseTestCase.createDatabase1();
            mDB = new EnerJDatabase();
            mDB.open(AbstractDatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
            mTxn = new EnerJTransaction();
            mTxn.begin(mDB);
        }


        public void tearDown() throws Exception
        {
            mTxn.commit();
            mDB.close();
            AbstractDatabaseTestCase.clearDBFiles();
            super.tearDown();
        }

        public QueryableCollection createQueryableCollection() throws Exception
        {
            return new PersistentBxTree<Integer, TestClass1>(10, null, false, false, true);
        }
    }

}

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
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.annotations.Persist;
import org.enerj.annotations.PersistenceAware;
import org.enerj.apache.commons.collections.BulkTest;
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
public class PersistentBxTreeTest extends BulkTest
{
    private static final String[] FIRST_NAMES = { "Dan", "Tina", "Bob", "Sue", "Emily", "Cole", "Mike", "Borusik", "Ole", "Lena", };
    private static final String[] LAST_NAMES = { "Smith", "Jones", "Funkmeister", "Johnson", "Anderson", "Syrstad", "Robinson",  };
    private static final String[] CITIES = { "Burnsville", "Bloomington", "Minneapolis", "St Paul", "Washington", "Seattle", "Phoenix", "New York", "Cleveland", "San Jose", };

    private EnerJDatabase mDB;
    private EnerJTransaction mTxn;
    
    /**
     * Construct a PersistentBxTreeTest. 
     *
     * @param arg0
     */
    public PersistentBxTreeTest(String arg0)
    {
        super(arg0);
    }


    public static Test suite() throws Exception 
    {
        //return makeSuite(PersistentBxTreeTest.class);
        TestSuite suite = new TestSuite(PersistentBxTreeTest.class);
        
        suite.addTestSuite( InternalSortedMapTest.class );
        suite.addTestSuite( InternalQueryableCollectionTest.class );
        suite.addTestSuite( ApacheHarmonySortedMapTest.class ); 
        suite.addTestSuite( ApacheCollectionsSortedMapTest.class ); // Note: this misses some tests because of BulkTest problems 

        return suite;
    }
    
    public void setUp() throws Exception
    {
        DatabaseTestCase.createDatabase1();
        mDB = new EnerJDatabase();
        mDB.open(DatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
        mTxn = new EnerJTransaction();
        mTxn.begin(mDB);
        super.setUp();
    }
    
    public void tearDown() throws Exception
    {
        mTxn.commit();
        mDB.close();
        DatabaseTestCase.clearDBFiles();
        super.tearDown();
    }
    
    
    public BulkTest xbulkTestInternalSortedMapTest() throws Exception
    {
        return new InternalSortedMapTest(InternalSortedMapTest.class.getName());
    }
    
    public BulkTest xbulkTestApacheCollectionsSortedMapTest() throws Exception 
    {
        return new ApacheCollectionsSortedMapTest(ApacheCollectionsSortedMapTest.class.getName());
    }
    
    public BulkTest xbulkTestInternalQueryableCollectionTest()
    {
        return new InternalQueryableCollectionTest(InternalQueryableCollectionTest.class.getName());
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

        long start = System.currentTimeMillis();
        PersistentBxTree<Integer, TestClass1> tree = new PersistentBxTree<Integer, TestClass1>(10, null, false, false, true);
        mDB.bind(tree, "BTree");
        for (int i = 0; i < objs.length; i++) {
            TestClass1 obj = objs[i];
            tree.insert(obj.getId(), obj);
        }

        mTxn.commit();
        mDB.close();
        mDB = null;
        mTxn = null;

        long end = System.currentTimeMillis();
        System.out.println("Insert time " + (end-start) + "ms");

        mDB = new EnerJDatabase();
        
        mDB.open(DatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
        mDB.setAllowNontransactionalReads(true);

        start = System.currentTimeMillis();
        tree = (PersistentBxTree<Integer, TestClass1>)mDB.lookup("BTree");

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

        end = System.currentTimeMillis();
        System.out.println("ContainsKey/Get time " + (end-start) + "ms");
    }

    /**
     * Tests duplicate keys across more than 2 leaf nodes.
     */
    public void testDuplicateKeys() throws Exception 
    {
        // We need at least 20 keys to fill 2 leaf nodes, so do 31.
        final int numDups = 31;
        final int dupKey = 3;
        final String dupKeyStr = String.valueOf(dupKey);
        
        PersistentBxTree<Integer, String> tree = new PersistentBxTree<Integer, String>(10, null, true, false, true);
        // Insert a couple of non-dups before the dups.
        tree.insert(1, "1");
        tree.insert(2, "2");
        for (int i = 0; i < numDups; i++) {
            tree.insert(dupKey, dupKeyStr + "-" + i);
        }
        
        // Insert a couple non-dups after...
        tree.insert(4, "4");
        tree.insert(5, "5");
        
        assertEquals(numDups + 4, tree.size());
        
        // Now count the dups using a regular iterator.
        int dupCnt = 0;
        boolean[] slotFound = new boolean[numDups];
        for (Map.Entry<Integer, String> entry : tree.entrySet()) {
            if (entry.getKey() == dupKey) {
                ++dupCnt;
                String value = entry.getValue();
                String[] c = value.split("-");
                assertEquals(dupKeyStr, c[0]);
                int idx = Integer.valueOf(c[1]); // The original index on "3-#"
                slotFound[idx] = true;
            }
        }
        
        assertEquals(numDups, dupCnt);
        for (int i = 0; i < numDups; i++) {
            if (!slotFound[i]) {
                fail("Didn't find slot " + slotFound[i]);
            }
        }
        
        // Now use a submap restricted by the duplicate key.
        SortedMap<Integer, String> subMap = tree.subMap(dupKey, 4);
        assertEquals(numDups, subMap.size());
        // TODO check iterator as above
    }

    // TODO Test subtests with large tree, dupl keys, dynamic/static resize, reverswed order
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
            //System.gc(); System.out.println("Mem1 before open=" + Runtime.getRuntime().freeMemory());
            DatabaseTestCase.createDatabase1();
            mDB = new EnerJDatabase();
            mDB.open(DatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
            mTxn = new EnerJTransaction();
            mTxn.begin(mDB);
            super.setUp();
        }


        public void tearDown() throws Exception
        {
            mTxn.commit();
            mDB.close();
            DatabaseTestCase.clearDBFiles();
            super.tearDown();
            //System.gc(); System.out.println("Mem1 after commit=" + Runtime.getRuntime().freeMemory());
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
     * Uses Apache Harmony Tests.
     */
    public static final class ApacheHarmonySortedMapTest extends AbstractApacheHarmonySortedMapTest
    {
        private EnerJDatabase mDB;
        private EnerJTransaction mTxn;
        
        public ApacheHarmonySortedMapTest(String aName)
        {
            super(aName);
        }

        public void setUp() throws Exception
        {
            DatabaseTestCase.createDatabase1();
            mDB = new EnerJDatabase();
            mDB.open(DatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
            mTxn = new EnerJTransaction();
            mTxn.begin(mDB);
            super.setUp();
        }


        public void tearDown() throws Exception
        {
            mTxn.commit();
            mDB.close();
            DatabaseTestCase.clearDBFiles();
            super.tearDown();
        }

        @Override
        public SortedMap createSortedMap(Comparator comparator)
        {
            return new PersistentBxTree(10, comparator, false, false, true);
        }
    }
    
    /**
     * Uses Apache Collections Tests.
     */
    // Note: this misses some tests because of BulkTest problems interacting with the database.    
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
            DatabaseTestCase.createDatabase1();
            mDB = new EnerJDatabase();
            mDB.open(DatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
            mTxn = new EnerJTransaction();
            mTxn.begin(mDB);
            super.setUp();
        }


        public void tearDown() throws Exception
        {
            mTxn.commit();
            mDB.close();
            DatabaseTestCase.clearDBFiles();
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

        @Override
        public Object[] getSampleValues()
        {
            Object[] result = new Object[] {
                            "blahv", "foov", "barv", "bazv", "tmpv", "goshv", "gollyv", "geev",
                            "hellov", "goodbyev", "we'llv", "seev", "youv", "allv", "againv",
                            (isAllowNullValue() ? null : "nonnullvalue"),
                            "value",
                            (isAllowDuplicateValues()) ? "value" : "value2",
                        };
            return result;
        }

        @Override
        public Object[] getNewSampleValues() 
        {
            Object[] result = new Object[] {
                            (isAllowNullValue() && isAllowDuplicateValues()) ? null : "newnonnullvalue",
                            "newvalue",
                            (isAllowDuplicateValues()) ? "newvalue" : "newvalue2",
                            "newblahv", "newfoov", "newbarv", "newbazv", "newtmpv", "newgoshv", 
                            "newgollyv", "newgeev", "newhellov", "newgoodbyev", "newwe'llv", 
                            "newseev", "newyouv", "newallv", "newagainv",
                        };
            return result;
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
            DatabaseTestCase.createDatabase1();
            mDB = new EnerJDatabase();
            mDB.open(DatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
            mTxn = new EnerJTransaction();
            mTxn.begin(mDB);
            super.setUp();
        }


        public void tearDown() throws Exception
        {
            mTxn.commit();
            mDB.close();
            DatabaseTestCase.clearDBFiles();
            super.tearDown();
        }

        public QueryableCollection createQueryableCollection() throws Exception
        {
            return new PersistentBxTree<Integer, TestClass1>(10, null, false, false, true);
        }
    }

}

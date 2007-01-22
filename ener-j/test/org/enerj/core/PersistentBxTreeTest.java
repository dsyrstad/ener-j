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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.odmg.QueryableCollection;

/**
 * Tests PersistentBxTree. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@PersistenceAware
public class PersistentBxTreeTest extends BulkTest
{
    // TODO Test removal from leftmost and rightmost leaves, leaving leaves empty, then iterate over all keys
    
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
        if (mTxn != null && mTxn.isOpen()) {
            mTxn.commit();
        }
        
        if (mDB != null && mDB.isOpen()) {
            mDB.close();
        }
        
        DatabaseTestCase.clearDBFiles();
        super.tearDown();
    }
    
    
    // Bulk tests don't work with database setup/cleanup 
    /*
    public BulkTest xbulkTestInternalSortedMapTest() throws Exception
    {
        return new InternalSortedMapTest(InternalSortedMapTest.class.getName());
    }
    
    // Bulk tests don't work with database setup/cleanup 
    public BulkTest xbulkTestApacheCollectionsSortedMapTest() throws Exception 
    {
        return new ApacheCollectionsSortedMapTest(ApacheCollectionsSortedMapTest.class.getName());
    }
    
    // Bulk tests don't work with database setup/cleanup 
    public BulkTest xbulkTestInternalQueryableCollectionTest()
    {
        return new InternalQueryableCollectionTest(InternalQueryableCollectionTest.class.getName());
    }
    */ 
    
    /**
     * Test method for {@link org.enerj.core.PersistentBxTree#put(java.lang.Object, org.enerj.core.Persistable)}.
     */
    public void testLargeRandomPut() throws Exception
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
        mTxn = new EnerJTransaction();
        mTxn.begin(mDB);

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
        
        // Test reorganize
        int sizeBefore = tree.size(); 
        start = System.currentTimeMillis();

        tree.reorganize();

        end = System.currentTimeMillis();
        System.out.println("reorganize time " + (end-start) + "ms");
        
        //tree.dumpTree();
        tree.validateTree();

        assertEquals(sizeBefore, tree.size());
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

    /**
     * Tests duplicate keys across more than 2 leaf nodes.
     */
    public void testDuplicateKeys() throws Exception 
    {
        // We need to fill at least 2 leaf nodes, but we also want at least two levels of 
        // interior nodes with more than one duplicate keys.
        final int numDups = 4001;
        final int numBeforeAfter = numDups / 4;
        final int dupKey = numBeforeAfter + (numDups/2);
        final int numKeys = numDups + numBeforeAfter + numBeforeAfter;

        List<Integer> keys = new ArrayList<Integer>(numKeys);
        int dupEndIdx = numBeforeAfter + numDups;
        for (int i = 0; i < numKeys; i++) {
            if (i < numBeforeAfter || i >= dupEndIdx) {
                keys.add(i);
            }
            else {
                keys.add(dupKey); 
            }
        }
        
        // Shuffle array using a consistent seed
        List<Integer> shuffledKeys = new ArrayList<Integer>(keys);
        Collections.shuffle(shuffledKeys, new Random(1L));
        
        PersistentBxTree<Integer, String> tree = new PersistentBxTree<Integer, String>(10, null, true, false, true);
        for (int i = 0; i < shuffledKeys.size(); i++) {
            Integer key = shuffledKeys.get(i);
            tree.insert(key, key + "-" + i);
            //System.out.println("Inserted " + key); tree.dumpTree(); 
        }
        
        tree.validateTree();
        //tree.dumpTree();
        
        // Now count the dups using a regular iterator.
        assertTreeMatches(tree, keys);
        
        // Now use a subMap restricted by the duplicate key.
        assertTreeMatches(tree.subMap(dupKey, dupKey + 1), keys.subList(numBeforeAfter, numBeforeAfter + numDups));

        // Now use a headMap restricted by the duplicate key.
        assertTreeMatches(tree.headMap(dupKey + 1), keys.subList(0, numDups + numBeforeAfter));

        // Now use a tailMap restricted by the duplicate key.
        assertTreeMatches(tree.tailMap(dupKey), keys.subList(numBeforeAfter, numKeys));

        // Test get() with duplicates
        for (int i = 0; i < shuffledKeys.size(); i++) {
            Integer key = shuffledKeys.get(i);
            String lookupValue = tree.get(key);
            assertTrue(lookupValue.startsWith(key + "-"));
        }
        
        // Test reorganize() against dups.
        tree.reorganize();

        tree.validateTree();
        
        // Count the dups using a regular iterator.
        assertTreeMatches(tree, keys);
    }

    /**
     * Tests deletes across more than 2 leaf nodes, which leaves them empty. Also reinserts in the 
     * empty leafs and checks the results. Duplicate keys are tested at the same time. 
     */
    public void testDeleteKeys() throws Exception 
    {
        // We need at least 20 keys to empty 2 leaf nodes, plus at least 5 on each side of the
        // range (40) to account for splits, so do more.
        final int numToDelete = 420;
        final int startDeleteIdx = 92;
        final int endDeleteIdx = startDeleteIdx + numToDelete;
        // There will actually be twice as many entries as this due to duplicates.
        final int numKeys = startDeleteIdx + endDeleteIdx + 1000;  
        
        List<Integer> unshuffledKeys = new ArrayList<Integer>(numKeys * 2);
        for (int i = 0; i < numKeys; i++) {
            unshuffledKeys.add(i);
            unshuffledKeys.add(i); // Add a duplicate
        }

        List<Integer> shuffledKeys = new ArrayList<Integer>(unshuffledKeys);

        // Shuffle array using a consistent seed
        Collections.shuffle(shuffledKeys, new Random(1L));
        
        PersistentBxTree<Integer, String> tree = new PersistentBxTree<Integer, String>(10, null, true, false, true);
        for (int i = 0; i < shuffledKeys.size(); i++) {
            int key = shuffledKeys.get(i);
            tree.insert(key, key + "-" + i);
        }
        
        tree.validateTree();
        //tree.dumpTree();

        // Before deleting, make sure the tree is correct.
        assertTreeMatches(tree, unshuffledKeys);
        
        // Delete keys
        List<Integer> deletedKeys = new ArrayList<Integer>(numToDelete);
        for (int i = startDeleteIdx; i < endDeleteIdx; i++) {
            Integer key = unshuffledKeys.get(i);
            tree.delete(key);

            // Remove from the master list for later comparison
            unshuffledKeys.remove(key);
            deletedKeys.add(key);
        }

        assertTrue(unshuffledKeys.size() > 0);

        // After deleting, make sure the tree is correct.
        tree.validateTree();
        assertTreeMatches(tree, unshuffledKeys);
        
        // Re-insert the deleted keys
        for (Integer key : deletedKeys) {
            tree.insert(key, key + "-value");
            unshuffledKeys.add(key);
        }

        Collections.sort(unshuffledKeys);
        // After re-inserting, make sure the tree is correct.
        tree.validateTree();
        assertTreeMatches(tree, unshuffledKeys);
        
        // Test reorganize after deletes.
        for (Integer key : deletedKeys) {
            tree.delete(key);
            unshuffledKeys.remove(key);
        }
        
        tree.reorganize();
        tree.validateTree();
        assertTreeMatches(tree, unshuffledKeys);
    }
    
    /**
     * Asserts that the keys exist in the tree.
     *
     * @param sortedMap
     * @param keys
     */
    private void assertTreeMatches(SortedMap<Integer, String> sortedMap, List<Integer> keys)
    {
        assertEquals(keys.size(), sortedMap.size());
        
        // Duplicate keys may be in random order, so we have to mark each value as we encounter it.
        HashSet<String> valuesFound = new HashSet<String>(keys.size());
        Iterator<Map.Entry<Integer,String>> iter = sortedMap.entrySet().iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            Map.Entry<Integer, String> entry = iter.next(); 
            Integer key = entry.getKey();
            
            Integer matchKey = keys.get(i); 
            assertEquals(matchKey, key);

            String value = entry.getValue();
            valuesFound.add(value);

            assertTrue("Value is not correct: " + value + "does not match key " + key, 
                            value.startsWith( String.valueOf(matchKey) + "-" ) );
        }

        // Should have found keys.length unique values.
        assertEquals(valuesFound.size(), keys.size());
    }

    // TODO Test subtests with dynamic/static resize, reversed order
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
            // TODO Implement Larger collections via getSample...
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

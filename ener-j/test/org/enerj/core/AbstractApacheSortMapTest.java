package org.enerj.core;

/*
 * Originally from Apache Harmony:
 * 
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

abstract public class AbstractApacheSortMapTest extends TestCase
{
    public AbstractApacheSortMapTest(String name)
    {
        super(name);
    }

    public static class ReversedComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            return -(((Comparable)o1).compareTo(o2));
        }

        public boolean equals(Object o1, Object o2)
        {
            return (((Comparable)o1).compareTo(o2)) == 0;
        }
    }


    // Regression for Harmony-1026
    public static class MockComparator<T extends Comparable<T>> implements Comparator<T>, Serializable
    {

        public int compare(T o1, T o2)
        {
            if (o1 == o2) {
                return 0;
            }
            if (null == o1) {
                return -1;
            }
            if (null == o2) {
                return 1;
            }
            T c1 = o1;
            T c2 = o2;
            return c1.compareTo(c2);
        }
    }


    // Regression for Harmony-1161
    class MockComparatorNullTolerable implements Comparator<String>
    {

        public int compare(String o1, String o2)
        {
            if (o1 == o2) {
                return 0;
            }
            if (null == o1) {
                return -1;
            }
            return o1.compareTo(o2);
        }
    }

    SortedMap tm;

    Object objArray[] = new Object[1000];

    /**
     * Creates an empty sorted map with an optional comparator.
     *
     * @param comparator the Comparator, may be null.
     * 
     * @return an empty SortedMap.
     */
    abstract public SortedMap createSortedMap(Comparator comparator);

    /**
     * Creates an empty sorted map that has natural sort order.
     * 
     * @return an empty SortedMap.
     */
    public SortedMap createSortedMap()
    {
        return createSortedMap(null);
    }

    /**
     * @tests java.util.SortedMap#SortedMap()
     */
    public void test_Constructor()
    {
        // Test for method java.util.SortedMap()
        new Support_MapTest2(createSortedMap()).runTest();

        assertTrue("New treeMap non-empty", createSortedMap().isEmpty());
    }

    /**
     * @tests java.util.SortedMap#SortedMap(java.util.Comparator)
     */
    public void test_ConstructorLjava_util_Comparator()
    {
        // Test for method java.util.SortedMap(java.util.Comparator)
        Comparator comp = new ReversedComparator();
        SortedMap reversedSortedMap = createSortedMap(comp);
        assertTrue("SortedMap answered incorrect comparator", reversedSortedMap.comparator() == comp);
        reversedSortedMap.put(new Integer(1).toString(), new Integer(1));
        reversedSortedMap.put(new Integer(2).toString(), new Integer(2));
        assertTrue("SortedMap does not use comparator (firstKey was incorrect)", reversedSortedMap.firstKey().equals(
                        new Integer(2).toString()));
        assertTrue("SortedMap does not use comparator (lastKey was incorrect)", reversedSortedMap.lastKey().equals(
                        new Integer(1).toString()));

    }

    /**
     * @tests java.util.SortedMap#SortedMap(java.util.Map)
     */
    public void test_ConstructorLjava_util_Map()
    {
        // Test for method java.util.SortedMap(java.util.Map)
        SortedMap mySortedMap = createSortedMap();
        mySortedMap.putAll(new HashMap(tm));
        assertTrue("Map is incorrect size", mySortedMap.size() == objArray.length);
        for (Object element : objArray) {
            assertTrue("Map has incorrect mappings", mySortedMap.get(element.toString()).equals(element));
        }
    }

    /**
     * @tests java.util.SortedMap#SortedMap(java.util.SortedMap)
     */
    public void test_ConstructorLjava_util_SortedMap()
    {
        // Test for method java.util.SortedMap(java.util.SortedMap)
        Comparator comp = new ReversedComparator();
        SortedMap reversedSortedMap = createSortedMap(comp);
        reversedSortedMap.put(new Integer(1).toString(), new Integer(1));
        reversedSortedMap.put(new Integer(2).toString(), new Integer(2));
        SortedMap anotherSortedMap = createSortedMap(comp);
        anotherSortedMap.putAll(reversedSortedMap);
        assertTrue("New tree map does not answer correct comparator", anotherSortedMap.comparator() == comp);
        assertTrue("SortedMap does not use comparator (firstKey was incorrect)", anotherSortedMap.firstKey().equals(
                        new Integer(2).toString()));
        assertTrue("SortedMap does not use comparator (lastKey was incorrect)", anotherSortedMap.lastKey().equals(
                        new Integer(1).toString()));

    }

    /**
     * @tests java.util.SortedMap#clear()
     */
    public void test_clear()
    {
        // Test for method void java.util.SortedMap.clear()
        tm.clear();
        assertEquals("Cleared map returned non-zero size", 0, tm.size());
    }

    /**
     * @tests java.util.SortedMap#comparator()
     */
    public void test_comparator()
    {
        // Test for method java.util.Comparator java.util.SortedMap.comparator()\
        Comparator comp = new ReversedComparator();
        SortedMap reversedSortedMap = createSortedMap(comp);
        assertTrue("SortedMap answered incorrect comparator", reversedSortedMap.comparator() == comp);
        reversedSortedMap.put(new Integer(1).toString(), new Integer(1));
        reversedSortedMap.put(new Integer(2).toString(), new Integer(2));
        assertTrue("SortedMap does not use comparator (firstKey was incorrect)", reversedSortedMap.firstKey().equals(
                        new Integer(2).toString()));
        assertTrue("SortedMap does not use comparator (lastKey was incorrect)", reversedSortedMap.lastKey().equals(
                        new Integer(1).toString()));
    }

    /**
     * @tests java.util.SortedMap#containsKey(java.lang.Object)
     */
    public void test_containsKeyLjava_lang_Object()
    {
        // Test for method boolean
        // java.util.SortedMap.containsKey(java.lang.Object)
        assertTrue("Returned false for valid key", tm.containsKey("95"));
        assertTrue("Returned true for invalid key", !tm.containsKey("XXXXX"));
    }

    /**
     * @tests java.util.SortedMap#containsValue(java.lang.Object)
     */
    public void test_containsValueLjava_lang_Object()
    {
        // Test for method boolean
        // java.util.SortedMap.containsValue(java.lang.Object)
        assertTrue("Returned false for valid value", tm.containsValue(objArray[986]));
        assertTrue("Returned true for invalid value", !tm.containsValue(new Object()));
    }

    /**
     * @tests java.util.SortedMap#entrySet()
     */
    public void test_entrySet()
    {
        // Test for method java.util.Set java.util.SortedMap.entrySet()
        Set anEntrySet = tm.entrySet();
        Iterator entrySetIterator = anEntrySet.iterator();
        assertTrue("EntrySet is incorrect size", anEntrySet.size() == objArray.length);
        Map.Entry entry;
        while (entrySetIterator.hasNext()) {
            entry = (Map.Entry)entrySetIterator.next();
            assertTrue("EntrySet does not contain correct mappings", tm.get(entry.getKey()) == entry.getValue());
        }
    }

    /**
     * @tests java.util.SortedMap#firstKey()
     */
    public void test_firstKey()
    {
        // Test for method java.lang.Object java.util.SortedMap.firstKey()
        assertEquals("Returned incorrect first key", "0", tm.firstKey());
    }

    /**
     * @tests java.util.SortedMap#get(java.lang.Object)
     */
    public void test_getLjava_lang_Object()
    {
        // Test for method java.lang.Object
        // java.util.SortedMap.get(java.lang.Object)
        Object o = new Integer(1);
        tm.put("Hello", o);
        assertTrue("Failed to get mapping", tm.get("Hello") == o);

    }

    /**
     * @tests java.util.SortedMap#headMap(java.lang.Object)
     */
    public void test_headMapLjava_lang_Object()
    {
        // Test for method java.util.SortedMap
        // java.util.SortedMap.headMap(java.lang.Object)
        Map head = tm.headMap("100");
        assertEquals("Returned map of incorrect size", 3, head.size());
        assertTrue("Returned incorrect elements", head.containsKey("0") && head.containsValue(new Integer("1"))
                        && head.containsKey("10"));

        // Regression for Harmony-1026
        SortedMap<Integer, Double> map = (SortedMap<Integer, Double>)createSortedMap(new MockComparator());
        map.put(1, 2.1);
        map.put(2, 3.1);
        map.put(3, 4.5);
        map.put(7, 21.3);
        map.put(null, null);

        SortedMap<Integer, Double> smap = map.headMap(null);
        assertEquals(0, smap.size());

        Set<Integer> keySet = smap.keySet();
        assertEquals(0, keySet.size());

        Set<Map.Entry<Integer, Double>> entrySet = smap.entrySet();
        assertEquals(0, entrySet.size());

        Collection<Double> valueCollection = smap.values();
        assertEquals(0, valueCollection.size());

        // Regression for Harmony-1066
        assertTrue(head instanceof Serializable);

        // Regression for ill-behaved collator
        Collator c = new Collator() {
            @Override
            public int compare(String o1, String o2)
            {
                if (o1 == o2) {
                    return 0;
                }
                
                if (o1 == null) {
                    return -1;
                }
                
                if (o2 == null) {
                    return 1;
                }
                return o1.compareTo(o2);
            }

            @Override
            public CollationKey getCollationKey(String string)
            {
                return null;
            }

            @Override
            public int hashCode()
            {
                return 0;
            }
        };

        SortedMap<String, String> treemap = (SortedMap<String, String>)createSortedMap(c);
        treemap.put("key", "value"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(0, treemap.headMap(null).size());
    }

    /**
     * @tests java.util.SortedMap#keySet()
     */
    public void test_keySet()
    {
        // Test for method java.util.Set java.util.SortedMap.keySet()
        Set ks = tm.keySet();
        assertTrue("Returned set of incorrect size", ks.size() == objArray.length);
        for (int i = 0; i < tm.size(); i++) {
            assertTrue("Returned set is missing keys", ks.contains(new Integer(i).toString()));
        }
    }

    /**
     * @tests java.util.SortedMap#lastKey()
     */
    public void test_lastKey()
    {
        // Test for method java.lang.Object java.util.SortedMap.lastKey()
        assertTrue("Returned incorrect last key", tm.lastKey().equals(objArray[objArray.length - 1].toString()));
    }

    /**
     * @tests java.util.SortedMap#put(java.lang.Object, java.lang.Object)
     */
    public void test_putLjava_lang_ObjectLjava_lang_Object()
    {
        // Test for method java.lang.Object
        // java.util.SortedMap.put(java.lang.Object, java.lang.Object)
        Object o = new String();
        tm.put("Hello", o);
        assertTrue("Failed to put mapping", tm.get("Hello") == o);

        // regression for Harmony-780
        tm = createSortedMap();
        assertNull(tm.put(new String(), new String()));
        try {
            tm.put(new Integer(1), new String());
            fail("should throw ClassCastException");
        }
        catch (ClassCastException e) {
            // expected
        }

        tm = createSortedMap();
        assertNull(tm.put(new Integer(1), new String()));
    }

    /**
     * @tests java.util.SortedMap#putAll(java.util.Map)
     */
    public void test_putAllLjava_util_Map()
    {
        // Test for method void java.util.SortedMap.putAll(java.util.Map)
        SortedMap x = createSortedMap();
        x.putAll(tm);
        assertTrue("Map incorrect size after put", x.size() == tm.size());
        for (Object element : objArray) {
            assertTrue("Failed to put all elements", x.get(element.toString()).equals(element));
        }
    }

    /**
     * @tests java.util.SortedMap#remove(java.lang.Object)
     */
    public void test_removeLjava_lang_Object()
    {
        // Test for method java.lang.Object
        // java.util.SortedMap.remove(java.lang.Object)
        tm.remove("990");
        assertTrue("Failed to remove mapping", !tm.containsKey("990"));

    }

    /**
     * @tests java.util.SortedMap#size()
     */
    public void test_size()
    {
        // Test for method int java.util.SortedMap.size()
        assertEquals("Returned incorrect size", 1000, tm.size());
    }

    /**
     * @tests java.util.SortedMap#subMap(java.lang.Object, java.lang.Object)
     */
    public void test_subMapLjava_lang_ObjectLjava_lang_Object()
    {
        // Test for method java.util.SortedMap
        // java.util.SortedMap.subMap(java.lang.Object, java.lang.Object)
        SortedMap subMap = tm.subMap(objArray[100].toString(), objArray[109].toString());
        assertEquals("subMap is of incorrect size", 9, subMap.size());
        for (int counter = 100; counter < 109; counter++) {
            assertTrue("SubMap contains incorrect elements", subMap.get(objArray[counter].toString()).equals(
                            objArray[counter]));
        }

        try {
            tm.subMap(objArray[9].toString(), objArray[1].toString());
            fail("end key less than start key should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // Expected
        }

        // Regression for Harmony-1161
        SortedMap<String, String> treeMapWithNull = (SortedMap<String, String>)createSortedMap(new MockComparatorNullTolerable());
        treeMapWithNull.put("key1", "value1"); //$NON-NLS-1$ //$NON-NLS-2$
        treeMapWithNull.put(null, "value2"); //$NON-NLS-1$
        SortedMap<String, String> subMapWithNull = treeMapWithNull.subMap(null, "key1"); //$NON-NLS-1$
        assertEquals("Size of subMap should be 1:", 1, subMapWithNull.size()); //$NON-NLS-1$

        // Regression test for typo in lastKey method
        SortedMap<String, String> map = (SortedMap<String, String>)createSortedMap();
        map.put("1", "one"); //$NON-NLS-1$ //$NON-NLS-2$
        map.put("2", "two"); //$NON-NLS-1$ //$NON-NLS-2$
        map.put("3", "three"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("3", map.lastKey());
        SortedMap<String, String> sub = map.subMap("1", "3"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("2", sub.lastKey()); //$NON-NLS-1$
    }

    /**
     * @tests java.util.SortedMap#tailMap(java.lang.Object)
     */
    public void test_tailMapLjava_lang_Object()
    {
        // Test for method java.util.SortedMap
        // java.util.SortedMap.tailMap(java.lang.Object)
        Map tail = tm.tailMap(objArray[900].toString());
        assertTrue("Returned map of incorrect size : " + tail.size(), tail.size() == (objArray.length - 900) + 9);
        for (int i = 900; i < objArray.length; i++) {
            assertTrue("Map contains incorrect entries", tail.containsValue(objArray[i]));
        }

        // Regression for Harmony-1066
        assertTrue(tail instanceof Serializable);
    }

    /**
     * @tests java.util.SortedMap#values()
     */
    public void test_values()
    {
        // Test for method java.util.Collection java.util.SortedMap.values()
        Collection vals = tm.values();
        vals.iterator();
        assertTrue("Returned collection of incorrect size", vals.size() == objArray.length);
        for (Object element : objArray) {
            assertTrue("Collection contains incorrect elements", vals.contains(element));
        }

        SortedMap mySortedMap = createSortedMap();
        for (int i = 0; i < 100; i++) {
            mySortedMap.put(objArray[i], objArray[i]);
        }
        Collection values = mySortedMap.values();
        new Support_UnmodifiableCollectionTest("Test Returned Collection From SortedMap.values()", values).runTest();
        values.remove(new Integer(0));
        assertTrue("Removing from the values collection should remove from the original map", !mySortedMap
                        .containsValue(new Integer(0)));
    }

    /**
     * @tests java.util.SortedMap#SerializationTest()
     */
    // Regression for Harmony-1066
    public void test_SubMap_Serializable() throws Exception
    {
        SortedMap<Integer, Double> map = (SortedMap<Integer, Double>)createSortedMap();
        map.put(1, 2.1);
        map.put(2, 3.1);
        map.put(3, 4.5);
        map.put(7, 21.3);
        SortedMap<Integer, Double> headMap = map.headMap(3);
        assertTrue(headMap instanceof Serializable);
        assertTrue(headMap instanceof SortedMap);

        assertFalse(headMap.entrySet() instanceof Serializable);
        assertFalse(headMap.keySet() instanceof Serializable);
        assertFalse(headMap.values() instanceof Serializable);
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    @Override
    protected void setUp() throws Exception
    {
        tm = createSortedMap();
        for (int i = 0; i < objArray.length; i++) {
            Object x = objArray[i] = new Integer(i);
            tm.put(x.toString(), x);
        }
    }


    private static final class Support_MapTest2 extends TestCase
    {

        Map<String, String> map;

        public Support_MapTest2(Map<String, String> m)
        {
            super();
            map = m;
            if (!map.isEmpty()) {
                fail("Map must be empty");
            }
        }

        @Override
        public void runTest()
        {
            try {
                map.put("one", "1");
                assertEquals("size should be one", 1, map.size());
                map.clear();
                assertEquals("size should be zero", 0, map.size());
                assertTrue("Should not have entries", !map.entrySet().iterator().hasNext());
                assertTrue("Should not have keys", !map.keySet().iterator().hasNext());
                assertTrue("Should not have values", !map.values().iterator().hasNext());
            }
            catch (UnsupportedOperationException e) {
            }

            try {
                map.put("one", "1");
                assertEquals("size should be one", 1, map.size());
                map.remove("one");
                assertEquals("size should be zero", 0, map.size());
                assertTrue("Should not have entries", !map.entrySet().iterator().hasNext());
                assertTrue("Should not have keys", !map.keySet().iterator().hasNext());
                assertTrue("Should not have values", !map.values().iterator().hasNext());
            }
            catch (UnsupportedOperationException e) {
            }
        }
    }


    public class Support_UnmodifiableCollectionTest extends TestCase
    {

        Collection<Integer> col;

        // must be a collection containing the Integers 0 to 99 (which will iterate
        // in order)

        public Support_UnmodifiableCollectionTest(String p1)
        {
            super(p1);
        }

        public Support_UnmodifiableCollectionTest(String p1, Collection<Integer> c)
        {
            super(p1);
            col = c;
        }

        @Override
        public void runTest()
        {

            // contains
            assertTrue("UnmodifiableCollectionTest - should contain 0", col.contains(new Integer(0)));
            assertTrue("UnmodifiableCollectionTest - should contain 50", col.contains(new Integer(50)));
            assertTrue("UnmodifiableCollectionTest - should not contain 100", !col.contains(new Integer(100)));

            // containsAll
            HashSet<Integer> hs = new HashSet<Integer>();
            hs.add(new Integer(0));
            hs.add(new Integer(25));
            hs.add(new Integer(99));
            assertTrue("UnmodifiableCollectionTest - should contain set of 0, 25, and 99", col.containsAll(hs));
            hs.add(new Integer(100));
            assertTrue("UnmodifiableCollectionTest - should not contain set of 0, 25, 99 and 100", !col.containsAll(hs));

            // isEmpty
            assertTrue("UnmodifiableCollectionTest - should not be empty", !col.isEmpty());

            // iterator
            Iterator<Integer> it = col.iterator();
            SortedSet<Integer> ss = new TreeSet<Integer>();
            while (it.hasNext()) {
                ss.add(it.next());
            }
            it = ss.iterator();
            for (int counter = 0; it.hasNext(); counter++) {
                int nextValue = it.next().intValue();
                assertTrue("UnmodifiableCollectionTest - Iterator returned wrong value.  Wanted: " + counter + " got: "
                                + nextValue, nextValue == counter);
            }

            // size
            assertTrue("UnmodifiableCollectionTest - returned wrong size.  Wanted 100, got: " + col.size(),
                            col.size() == 100);

            // toArray
            Object[] objArray;
            objArray = col.toArray();
            for (int counter = 0; it.hasNext(); counter++) {
                assertTrue("UnmodifiableCollectionTest - toArray returned incorrect array", objArray[counter] == it
                                .next());
            }

            // toArray (Object[])
            objArray = new Object[100];
            col.toArray(objArray);
            for (int counter = 0; it.hasNext(); counter++) {
                assertTrue("UnmodifiableCollectionTest - toArray(Object) filled array incorrectly",
                                objArray[counter] == it.next());
            }

        }
    }

}

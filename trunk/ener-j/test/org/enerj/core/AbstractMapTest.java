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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractMapTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement java.util.Map.
 *
 * Test methods are final so that the contract of Map is guaranteed to be tested.
 *
 * @version $Id: AbstractMapTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractMapTest extends TestCase
{

    public AbstractMapTest(String aName) 
    {
        super(aName);
    }
    

    /**
     * Creates an instance implementing Map. Subclass implements this
     * to create a specific type of Map.
     */
    abstract public Map createMap() throws Exception;
    

    /**
     * Should return true only if Map allows null keys.
     */
    abstract public boolean allowsNullKeys();
    

    /**
     * Tests put(Object, Object), get(Object), clear(), size(), 
     * containsKey(Object), containsValue(Object), and isEmpty().
     */
    public final void testPutGetEtc() throws Exception
    {
        Map testMap = createMap();

        final int mapSize = 10;
        for (int i = 0; i < mapSize; i++) {
            Object prevValue = testMap.put( new CollectionTestObject("Key-" + i), "Value-" + i);
            assertNull("Previous value should be null", prevValue);
        }

        assertTrue("Should not be empty", !testMap.isEmpty() );
        assertTrue("Should have the correct number of elements", testMap.size() == mapSize);
        for (int i = 0; i < mapSize; i++) {
            CollectionTestObject key =  new CollectionTestObject("Key-" + i);
            assertTrue("Should contain Key-" + i + " key", testMap.containsKey(key) );
            String value = (String)testMap.get(key);
            String targetValue = "Value-" + i;
            assertTrue("Value should be " + targetValue, value.equals(targetValue) );
            assertTrue("Should contain " + targetValue, testMap.containsValue(targetValue) );
        }

        // Put of duplicate key should replace value
        CollectionTestObject key = new CollectionTestObject("Key-" + 4);
        String newValue = "NEW VALUE";
        String prevValue = (String)testMap.put(key, newValue);

        assertTrue("Previous value should match", prevValue.equals("Value-" + 4));
        assertTrue("Get should return new value", testMap.get(key).equals(newValue) );
        assertTrue("Size should not have changed", testMap.size() == mapSize);
        
        // get of non-existent key
        Object value = testMap.get( new CollectionTestObject("UNKNOWN KEY") );
        assertNull("Value should not be returned", value);
        
        assertTrue("Should not contain key", !testMap.containsKey( new CollectionTestObject("UNKNOWN KEY") ) );
        assertTrue("Should not contain value", !testMap.containsValue("UNKNOWN VALUE") );
        
        // Test the null key - SortedMap doesn't allow null keys
        if (allowsNullKeys()) {
            testMap.put(null, "NULL VALUE");
            assertTrue("Contains null key", testMap.containsKey(null));
            assertTrue("Value for null key matches", testMap.get(null).equals("NULL VALUE"));
        }

        // Clear map - size should be zero
        testMap.clear();
        assertTrue("Should have zero elements", testMap.size() == 0);
        assertTrue("Should be empty", testMap.isEmpty() );
    }


    /**
     * Tests entrySet().
     */
    public final void testEntrySet() throws Exception
    {
        Map testMap = createMap();

        final int mapSize = 10;
        for (int i = 0; i < mapSize; i++) {
            testMap.put( new CollectionTestObject("Key-" + i), "Value-" + i);
        }
        
        Set entrySet = testMap.entrySet();
        assertTrue("Size should be correct", entrySet.size() == mapSize);
        for (int i = 0; i < mapSize; i++) {
            TestMapEntry testEntry = new TestMapEntry( new CollectionTestObject("Key-" + i), "Value-" + i);
            assertTrue("Should contain entry", entrySet.contains(testEntry) );
        }
    }
    

    /**
     * Tests equals().
     */
    public final void testEquals() throws Exception
    {
        Map testMap = createMap();
        
        final int mapSize = 10;
        for (int i = 0; i < mapSize; i++) {
            testMap.put( new CollectionTestObject("Key-" + i), "Value-" + i);
        }
        
        HashMap map2 = new HashMap();
        // Create in reverse, just for grins.
        for (int i = mapSize - 1; i >= 0; i--) {
            map2.put( new CollectionTestObject("Key-" + i), "Value-" + i);
        }
        
        assertTrue("Maps should be equal", testMap.equals(map2) );

        map2.put( new CollectionTestObject("XYZ"), "Value-");
        assertTrue("Maps should not be equal", !testMap.equals(map2) );
    }
    

    /**
     * Tests hashCode().
     */
    public final void testHashCode() throws Exception
    {
        Map testMap = createMap();
        
        final int mapSize = 10;
        for (int i = 0; i < mapSize; i++) {
            testMap.put( new CollectionTestObject("Key-" + i), "Value-" + i);
        }
        
        // Calculation is from the Map javadoc
        int hashCode = 0;
        Set entrySet = testMap.entrySet();
        Iterator iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            hashCode += entry.hashCode();
        }
        
        assertTrue("Hashcodes should match", testMap.hashCode() == hashCode);
    }


    /**
     * Tests keySet().
     */
    public final void testKeySet() throws Exception
    {
        Map testMap = createMap();

        final int mapSize = 10;
        for (int i = 0; i < mapSize; i++) {
            testMap.put( new CollectionTestObject("Key-" + i), "Value-" + i);
        }
        
        Set keySet = testMap.keySet();
        assertTrue("Size should be correct", keySet.size() == mapSize);
        for (int i = 0; i < mapSize; i++) {
            assertTrue("Should contain entry", keySet.contains( new CollectionTestObject("Key-" + i) ) );
        }
    }
    

    /**
     * Tests putAll(Map).
     */
    public final void testPutAll() throws Exception
    {
        Map testMap = createMap();

        final int mapSize = 10;
        HashMap map2 = new HashMap();
        for (int i = 0; i < mapSize; i++) {
            map2.put( new CollectionTestObject("Key-" + i), "Value-" + i);
        }
        
        assertTrue("Map2 should be correct size", map2.size() == mapSize);
        
        testMap.putAll(map2);
        
        assertTrue("Should have the correct number of elements", testMap.size() == mapSize);
        for (int i = 0; i < mapSize; i++) {
            CollectionTestObject key =  new CollectionTestObject("Key-" + i);
            String value = (String)testMap.get(key);
            String targetValue = "Value-" + i;
            assertTrue("Value should be " + targetValue, value.equals(targetValue) );
            assertTrue("Should contain " + targetValue, testMap.containsValue(targetValue) );
        }
    }
    

    /**
     * Tests remove(Object).
     */
    public final void testRemove() throws Exception
    {
        Map testMap = createMap();

        final int mapSize = 10;
        final int removeIdx = 3;
        for (int i = 0; i < mapSize; i++) {
            testMap.put( new CollectionTestObject("Key-" + i), "Value-" + i);
        }
        
        String prevValue = (String)testMap.remove( new CollectionTestObject("Key-" + removeIdx) );
        assertTrue("Size should be correct", testMap.size() == (mapSize - 1));
        assertTrue("Previous value should match", prevValue.equals("Value-" + removeIdx) );
        for (int i = 0; i < mapSize; i++) {
            if (i != removeIdx) {
                CollectionTestObject key =  new CollectionTestObject("Key-" + i);
                String value = (String)testMap.get(key);
                String targetValue = "Value-" + i;
                assertTrue("Value should be " + targetValue, value.equals(targetValue) );
                assertTrue("Should contain " + targetValue, testMap.containsValue(targetValue) );
            }
        }
        
        // Remove of non-existent key (same one) should return null value.
        prevValue = (String)testMap.remove( new CollectionTestObject("Key-" + removeIdx) );
        assertNull("Previous value should be null", prevValue);
        
        // Try removal of null key - SortedMap doesn't allow null keys
        if (allowsNullKeys()) {
            testMap.put(null, "NULL VALUE");
            prevValue = (String)testMap.remove(null);
            assertTrue("Previous value should match", prevValue.equals("NULL VALUE"));
            assertNull("Null key should not exist", testMap.get(null));
        }
    }
    

    /**
     * Tests values().
     */
    public final void testValues() throws Exception
    {
        Map testMap = createMap();

        final int mapSize = 10;
        for (int i = 0; i < mapSize; i++) {
            testMap.put( new CollectionTestObject("Key-" + i), "Value-" + i);
        }
        
        Collection values = testMap.values();
        assertTrue("Size must match", values.size() == mapSize);
        for (int i = 0; i < mapSize; i++) {
            assertTrue("Value must exist", values.contains("Value-" + i) );
        }
    }



    private static final class TestMapEntry implements Map.Entry
    {
        private Object mKey;
        private Object mValue;
        

        TestMapEntry(Object aKey, Object aValue)
        {
            mKey = aKey;
            mValue = aValue;
        }
        

        public Object getKey()
        {
            return mKey;
        }


        public Object getValue()
        {
            return mValue;
        }


        public Object setValue(Object value)
        {
            throw new UnsupportedOperationException();
        }


        public boolean equals(Object o)
        {
            Map.Entry e2 = (Map.Entry)o;
            return (this.getKey()==null ? e2.getKey()==null : this.getKey().equals(e2.getKey()))  &&
                (this.getValue()==null ? e2.getValue()==null : this.getValue().equals(e2.getValue()));
        }


        public int hashCode()
        {
            return (getKey()==null   ? 0 : getKey().hashCode()) ^
                   (getValue()==null ? 0 : getValue().hashCode());
        }
    }
}

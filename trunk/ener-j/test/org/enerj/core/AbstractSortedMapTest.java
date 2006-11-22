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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractSortedMapTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement java.util.SortedMap.
 *
 * Test methods are final so that the contract of SortedMap is guaranteed to be tested.
 *
 * @version $Id: AbstractSortedMapTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractSortedMapTest extends AbstractMapTest
{

    public AbstractSortedMapTest(String aName) 
    {
        super(aName);
    }


    /**
     * Tests comparator().
     */
    public final void testComparator() throws Exception
    {
        SortedMap testSortedMap = (SortedMap)createMap();
        
        // Just test calling it. It may return null or a Comparator, but we can't check 
        // the validitiy of that. This just ensures that it doesn't throw.
        Comparator comparator = testSortedMap.comparator();
    }

    

    /**
     * Tests firstKey() and that the entrySet() iterator is in order.
     */
    public final void testFirstKey() throws Exception
    {
        SortedMap testSortedMap = (SortedMap)createMap();

        // Add items to set in different order, the set should sort them.
        testSortedMap.put( new CollectionTestObject("Obj3"), "Value3");
        testSortedMap.put( new CollectionTestObject("Obj1"), "Value1");
        testSortedMap.put( new CollectionTestObject("Obj2"), "Value2");

        assertTrue("Should be first element", testSortedMap.firstKey().equals( new CollectionTestObject("Obj1") ) );
        
        Iterator iterator = testSortedMap.entrySet().iterator();
        for (int i = 1; i <= 3; i++) {
            Map.Entry entry = (Map.Entry)iterator.next();
            CollectionTestObject object = (CollectionTestObject)entry.getKey();
            assertTrue("Key should match", object.equals( new CollectionTestObject("Obj" + i) ) );
        }
    }
    

    /**
     * Tests headMap().
     */
    public final void testHeadMap() throws Exception
    {
        SortedMap testSortedMap = (SortedMap)createMap();
        
        final int listSize = 10;
        final int stopIdx = 4; // less  than this
        for (int i = 0; i < listSize; i++) {
            testSortedMap.put( new CollectionTestObject("Obj-" + i), "Value-" + i);
        }
        
        SortedMap headMap = testSortedMap.headMap( new CollectionTestObject("Obj-" + stopIdx) );
        assertTrue("Size should be correct", headMap.size() == stopIdx);
        Iterator iterator = headMap.keySet().iterator();
        for (int i = 0; i < stopIdx; i++) {
            CollectionTestObject object = (CollectionTestObject)iterator.next();
            assertTrue("Value should match", object.equals( new CollectionTestObject("Obj-" + i) ) );
        }
        
    }
    

    /**
     * Tests lastKey().
     */
    public final void testLastKey() throws Exception
    {
        SortedMap testSortedMap = (SortedMap)createMap();

        // Add items to set in different order, the set should sort them.
        testSortedMap.put( new CollectionTestObject("Obj3"), "Value3");
        testSortedMap.put( new CollectionTestObject("Obj1"), "Value1");
        testSortedMap.put( new CollectionTestObject("Obj2"), "Value2");

        assertTrue("Should be first element", testSortedMap.lastKey().equals( new CollectionTestObject("Obj3") ) );
    }
    

    /**
     * Tests subMap().
     */
    public final void testSubMap() throws Exception
    {
        SortedMap testSortedMap = (SortedMap)createMap();
        
        final int listSize = 10;
        final int startIdx = 2; // Inclusive
        final int stopIdx = 6;  // Exclusive
        for (int i = 0; i < listSize; i++) {
            testSortedMap.put( new CollectionTestObject("Obj-" + i), "Value-" + i);
        }
        
        SortedMap subMap = testSortedMap.subMap( new CollectionTestObject("Obj-" + startIdx),
                                 new CollectionTestObject("Obj-" + stopIdx));

        assertTrue("Size should be correct", subMap.size() == (stopIdx - startIdx));
        Iterator iterator = subMap.keySet().iterator();
        for (int i = startIdx; i < stopIdx; i++) {
            CollectionTestObject object = (CollectionTestObject)iterator.next();
            assertTrue("Value should match", object.equals( new CollectionTestObject("Obj-" + i) ) );
        }
        
    }
    

    /**
     * Tests tailMap().
     */
    public final void testTailMap() throws Exception
    {
        SortedMap testSortedMap = (SortedMap)createMap();
        
        final int listSize = 10;
        final int startIdx = 4; // >= this
        for (int i = 0; i < listSize; i++) {
            testSortedMap.put( new CollectionTestObject("Obj-" + i), "Value-" + i);
        }
        
        SortedMap tailMap = testSortedMap.tailMap( new CollectionTestObject("Obj-" + startIdx) );
        assertTrue("Size should be correct", tailMap.size() == (listSize - startIdx) );
        Iterator iterator = tailMap.keySet().iterator();
        for (int i = startIdx; i < listSize; i++) {
            CollectionTestObject object = (CollectionTestObject)iterator.next();
            assertTrue("Value should match", object.equals( new CollectionTestObject("Obj-" + i) ) );
        }
        
    }
    
}

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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractCollectionTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement java.util.Collection.
 * We don't test equals() because a strict Collection requires the exact same instance
 * for the Collection to be equal (i.e., the Object.equals semantics). We let
 * the List and Set tests (which have different semantics) test this. The same applies
 * to hashCode().<p>
 *
 * Test methods are final so that the contract of Collection is guaranteed to be tested.
 *
 * @version $Id: AbstractCollectionTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
abstract public class AbstractCollectionTest extends TestCase
{
    //----------------------------------------------------------------------
    public AbstractCollectionTest(String aName) 
    {
        super(aName);
    }
    
    //----------------------------------------------------------------------
    /**
     * Creates an instance implementing Collection. Subclass implements this
     * to create a specific type of Collection.
     */
    abstract public Collection createCollection() throws Exception;
    
    //----------------------------------------------------------------------
    /**
     * Tests add(Object), clear(), size(), and contains().
     */
    public final void testAdd() throws Exception
    {
        Collection testCollection = createCollection();

        final int listSize = 10;
        for (int i = 0; i < listSize; i++) {
            testCollection.add( new CollectionTestObject("Obj-" + i) );
        }
        
        assertTrue("Should have the correct number of elements", testCollection.size() == listSize);
        for (int i = 0; i < listSize; i++) {
            assertTrue("Should contain Obj-" + i + " element", testCollection.contains( new CollectionTestObject("Obj-" + i) ) );
        }
        
        testCollection.clear();
        assertTrue("Should have zero elements", testCollection.size() == 0);
    }

    //----------------------------------------------------------------------
    /**
     * Tests addAll(Collection).
     */
    public final void testAddAll() throws Exception
    {
        Collection testCollection = createCollection();
        
        final int listSize = 10;
        ArrayList list = new ArrayList(listSize);
        for (int i = 0; i < listSize; i++) {
            list.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testCollection.addAll(list);
        
        assertTrue("Should have the correct number of elements", testCollection.size() == listSize);
        for (int i = 0; i < listSize; i++) {
            assertTrue("Should contain Obj-" + i + " element", testCollection.contains( new CollectionTestObject("Obj-" + i) ) );
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests containsAll(Collection).
     */
    public final void testContainsAll() throws Exception
    {
        Collection testCollection = createCollection();
        
        final int listSize = 10;
        int count = 0;
        ArrayList list = new ArrayList(listSize);
        for (int i = 0; i < listSize; i++) {
            list.add( new CollectionTestObject("Obj-" + i) );
            ++count;
            // Add another, different object to mix it up
            testCollection.add( new CollectionTestObject("XObj-" + i) );
            ++count;
        }
        
        testCollection.addAll(list);
        
        assertTrue("Should have the correct number of elements", testCollection.size() == count);
        assertTrue("Should contain proper elements", testCollection.containsAll(list) );
    }

    //----------------------------------------------------------------------
    /**
     * Tests isEmpty().
     */
    public final void testIsEmpty() throws Exception
    {
        Collection testCollection = createCollection();
        
        assertTrue("Should be empty", testCollection.isEmpty() );

        testCollection.add( new CollectionTestObject("Obj") );

        assertTrue("Should not be empty", !testCollection.isEmpty() );
    }

    //----------------------------------------------------------------------
    /**
     * Tests iterator(). 
     */
    public final void testIterator() throws Exception
    {
        try {
            new AbstractCollectionTest.CollectionIteratorTest("runAllTests").runBare();
        }
        catch (RuntimeException e) {
            throw e;    // thrown as RuntimeException
        }
        catch (Exception e) {
            throw e;    // thrown as Exception
        }
        catch (Error e) {
            throw e;    // thrown as Error
        }
        catch (Throwable t) {
            // A Throwable, but NOT an Exception or Error
            throw new Exception(t);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests remove(Object).
     */
    public final void testRemove() throws Exception
    {
        Collection testCollection = createCollection();

        final int listSize = 10;
        int count = 0;
        for (int i = 0; i < listSize; i++) {
            // Don't count this one
            testCollection.add( new CollectionTestObject("REMOVE-Obj-" + i) );

            testCollection.add( new CollectionTestObject("Obj-" + i) );
            ++count;
        }
        
        // Remove the REMOVE-Obj elements
        for (int i = 0; i < listSize; i++) {
            testCollection.remove( new CollectionTestObject("REMOVE-Obj-" + i) );
        }

        assertTrue("Should have the correct number of elements", testCollection.size() == count);
        for (int i = 0; i < listSize; i++) {
            assertTrue("Should not contain element", !testCollection.contains( new CollectionTestObject("REMOVE-Obj-" + i) ) );
            assertTrue("Should contain element", testCollection.contains( new CollectionTestObject("Obj-" + i) ) );
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests removeAll(Collection).
     */
    public final void testRemoveAll() throws Exception
    {
        Collection testCollection = createCollection();

        final int listSize = 10;
        ArrayList list = new ArrayList(listSize);
        int count = 0;
        for (int i = 0; i < listSize; i++) {
            CollectionTestObject removeObj = new CollectionTestObject("REMOVE-Obj-" + i);
            list.add(removeObj);

            // Don't count this one.
            testCollection.add(removeObj);

            // Add another, different object to mix it up
            testCollection.add( new CollectionTestObject("Obj-" + i) );
            ++count;
        }
        
        testCollection.removeAll(list);
        
        assertTrue("Should have the correct number of elements", testCollection.size() == count);
        for (int i = 0; i < listSize; i++) {
            assertTrue("Should not contain element", !testCollection.contains( new CollectionTestObject("REMOVE-Obj-" + i) ) );
            assertTrue("Should contain element", testCollection.contains( new CollectionTestObject("Obj-" + i) ) );
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests retainAll(Collection).
     */
    public final void testRetainAll() throws Exception
    {
        Collection testCollection = createCollection();

        final int listSize = 10;
        ArrayList list = new ArrayList(listSize);
        int count = 0;
        for (int i = 0; i < listSize; i++) {
            CollectionTestObject retainObj = new CollectionTestObject("RETAIN-Obj-" + i);
            list.add(retainObj);

            testCollection.add(retainObj);
            ++count;

            // Don't count this one.
            testCollection.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testCollection.retainAll(list);
        
        assertTrue("Should have the correct number of elements", testCollection.size() == count);
        for (int i = 0; i < listSize; i++) {
            assertTrue("Should not contain element", !testCollection.contains( new CollectionTestObject("Obj-" + i) ) );
            assertTrue("Should contain element", testCollection.contains( new CollectionTestObject("RETAIN-Obj-" + i) ) );
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests toArray() and toArray(Object[]).
     */
    public final void testToArray() throws Exception
    {
        // This is very similar to testIterator.
        Collection testCollection = createCollection();
        
        final int listSize = 53;
        // This is a "checklist" of objects we need to find in the returned array. As
        // we find them, we check them off by nulling the entry.
        CollectionTestObject[] checkList1 = new CollectionTestObject[listSize];
        CollectionTestObject[] checkList2 = new CollectionTestObject[listSize];
        for (int i = 0; i < listSize; i++) {
            checkList1[i] = new CollectionTestObject("Obj-" + i);
            checkList2[i] = checkList1[i];
            testCollection.add(checkList1[i]);
        }

        Object[] array1 = testCollection.toArray();
        checkArray(array1, checkList1);
        
        CollectionTestObject[] array2 = new CollectionTestObject[listSize];
        testCollection.toArray(array2);
        checkArray(array2, checkList2);
    }
    
    //----------------------------------------------------------------------
    /**
     * Helper for testToArray. Checks the result of toArray() against the aCheckList.
     * Elements of aCheckList are nulled on return.
     */
    protected void checkArray(Object[] anArray, CollectionTestObject[] aCheckList) throws Exception
    {
        for (int arrayIdx = 0; arrayIdx < anArray.length; arrayIdx++) {
            CollectionTestObject obj = (CollectionTestObject)anArray[arrayIdx];

            boolean found = false;
            for (int i = 0; i < aCheckList.length; i++) {
                if (aCheckList[i] == obj) {
                    found = true;
                    aCheckList[i] = null;
                    break;
                }
            }

            assertTrue("Should have found object", found);
        }
        
        // Check list should be all nulls now.
        for (int i = 0; i < aCheckList.length; i++) {
            assertNull("Check list entry " + i + " should be null", aCheckList[i]);
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Tests Collection.iterator().
     */
    private class CollectionIteratorTest extends AbstractIteratorTest
    {
        //----------------------------------------------------------------------
        public CollectionIteratorTest(String aName)
        {
            super(aName);
        }

        //----------------------------------------------------------------------
        public Collection createIteratorCollection(Collection aCollection) throws Exception
        {
            Collection collection = createCollection();
            collection.addAll(aCollection);
            return collection;
        }

        //----------------------------------------------------------------------
        public Iterator createIterator(Collection aCollection) throws Exception
        {
            return aCollection.iterator();
        }

        //----------------------------------------------------------------------
        public boolean supportsRemove()
        {
            return true;
        }
    }
}

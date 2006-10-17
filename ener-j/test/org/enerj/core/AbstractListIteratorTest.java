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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractListIteratorTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement java.util.ListIterator.
 * Test methods are final so that the contract of ListIterator is guaranteed to be tested.
 *
 * @version $Id: AbstractListIteratorTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractListIteratorTest extends AbstractIteratorTest
{
    //----------------------------------------------------------------------
    public AbstractListIteratorTest(String aName) 
    {
        super(aName);
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the index into the collection on which createIterator() is based.
     *
     * @return the index. Zero represents a ListIterator over the entire List.
     */
    abstract public int getStartingIndex();
    
    //----------------------------------------------------------------------
    /**
     * Check if iterator supports add(Object).
     *
     * @return true if the ListIterator returned by createIterator supported the
     *  add() operation, else false.
     */
    abstract public boolean supportsAdd();
    
    //----------------------------------------------------------------------
    /**
     * Check if iterator supports set(Object).
     *
     * @return true if the ListIterator returned by createIterator supported the
     *  set() operation, else false.
     */
    abstract public boolean supportsSet();
    
    //----------------------------------------------------------------------
    /**
     * Checks the iterator against a list of known objects. Iterator does
     * not have to be in the order of the list.<p>
     *
     * Overridden from IteratorTest so that indexed ListIterator are tested properly.
     *
     * @param anIterator the Iterator to be tested.
     * @param aList an ArrayList containing the Objects which should be in the iterator.
     *
     * @throws Exception if an error occurs.
     */
    protected void checkIterator(Iterator anIterator, ArrayList aList) throws Exception
    {
        checkListIterator((ListIterator)anIterator, aList, getStartingIndex() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Checks the iterator against a list of known objects. ListIterator does
     * must be in the order of the list.
     * Tests next(), hasNext(), previous(), hasPrevious(), nextIndex(),
     * previousIndex().
     *
     * @param anIterator the ListIterator to be tested.
     * @param aList a List containing the Objects which should be in the iterator.
     * @param aStartingIndex the starting index on the collection for the iterator.
     *
     * @throws Exception if an error occurs.
     */
    protected void checkListIterator(ListIterator anIterator, List aList, int aStartingIndex) throws Exception
    {
        // Although poorly documented on the ListIterator interface javadoc,
        // nextIndex() and previousIndex() return the index into the List, not the 
        // iterator index position.
        for (int i = aStartingIndex; i < aList.size(); i++) {
            assertTrue("hasNext should be true", anIterator.hasNext() );
            assertTrue("nextIndex should be correct", anIterator.nextIndex() == i);

            CollectionTestObject obj = (CollectionTestObject)anIterator.next();
            assertTrue("Object should match", aList.get(i).equals(obj) );
            assertTrue("previousIndex should be correct", anIterator.previousIndex() == i);
        }

        assertTrue("Should be at end of iterator", !anIterator.hasNext() );
        assertTrue("nextIndex should be list size", anIterator.nextIndex() == aList.size());
        
        // previous() should walk backwards, even past aStartingIndex
        for (int i = aList.size() - 1; i >= 0; i--) {
            assertTrue("hasPrevious should be true", anIterator.hasPrevious() );
            assertTrue("previousIndex should be correct", anIterator.previousIndex() == i);

            CollectionTestObject obj = (CollectionTestObject)anIterator.previous();
            assertTrue("Object should match", aList.get(i).equals(obj) );
            assertTrue("nextndex should be correct", anIterator.nextIndex() == i);
        }

        assertTrue("hasPrevious should be false", !anIterator.hasPrevious() );
        assertTrue("previousIndex should be -1", anIterator.previousIndex() == -1);
        assertTrue("Should be at start of iterator", anIterator.hasNext() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Method to run all tests. Can be used with "new ListIteratorTest("runAllTests").runBare()" 
     * to test an ListIterator from another test method.
     */
    public void runAllTests() throws Exception
    {
        super.runAllTests();    

        testListWalk();
        testAdd();
        testSet();
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests next(), hasNext(), previous(), hasPrevious(), nextIndex(),
     * previousIndex().
     */
    public final void testListWalk() throws Exception
    {
        final int listSize = 10;
        ArrayList list = new ArrayList(listSize);
        for (int i = 0; i < listSize; i++) {
            list.add( new CollectionTestObject("Obj-" + i) );
        }

        ListIterator iterator = (ListIterator)createIterator( createIteratorCollection(list) );
        checkListIterator(iterator, list, getStartingIndex());
    }

    //----------------------------------------------------------------------
    /**
     * Tests add(Object).
     */
    public final void testAdd() throws Exception
    {
        if (!supportsAdd()) {
            // ListIterator doesn't support add() - don't test it.
            return;
        }
        
        final int listSize = 10;
        final int addIdx = 7;
        ArrayList list = new ArrayList(listSize);
        CollectionTestObject removeObj = null;
        for (int i = 0; i < listSize; i++) {
            CollectionTestObject obj = new CollectionTestObject("Obj-" + i);
            list.add(obj);
        }

        List backingCollection = (List)createIteratorCollection(list);
        ListIterator iterator = (ListIterator)createIterator(backingCollection);
        // Skip forward to add position
        for (int i = getStartingIndex(); i < addIdx; i++) {
            iterator.next();
        }
        
        iterator.add( new CollectionTestObject("ADDED OBJ") );
        // Sync our list.
        list.add(addIdx, new CollectionTestObject("ADDED OBJ") );
        
        // Backing collection should have element at addIdx
        assertTrue("Backing collection should have been updated", 
            backingCollection.get(addIdx).equals( new CollectionTestObject("ADDED OBJ") ) );
        
        // Rewind the iterator back to the starting index so we can check it.
        // Use <= addIdx because we need to back up one more element due to the added element.
        for (int i = getStartingIndex(); i <= addIdx; i++) {
            iterator.previous();
        }
        
        checkListIterator(iterator, list, getStartingIndex() );
        
        // Get a new ListIterator from the backing Collection and make sure object was added.
        iterator = (ListIterator)createIterator(backingCollection);
        checkListIterator(iterator, list, getStartingIndex() );
    }

    //----------------------------------------------------------------------
    /**
     * Tests set(Object).
     */
    public final void testSet() throws Exception
    {
        if (!supportsSet()) {
            // ListIterator doesn't support set() - don't test it.
            return;
        }
        
        final int listSize = 10;
        final int setIdx = 7;
        ArrayList list = new ArrayList(listSize);
        CollectionTestObject removeObj = null;
        for (int i = 0; i < listSize; i++) {
            CollectionTestObject obj = new CollectionTestObject("Obj-" + i);
            list.add(obj);
        }

        List backingCollection = (List)createIteratorCollection(list);
        ListIterator iterator = (ListIterator)createIterator(backingCollection);
        // Skip forward to set position
        // Must use <= setIdx because set() updates the last element retrieved.
        for (int i = getStartingIndex(); i <= setIdx; i++) {
            iterator.next();
        }
        
        iterator.set( new CollectionTestObject("ADDED OBJ") );
        // Sync our list.
        list.set(setIdx, new CollectionTestObject("ADDED OBJ") );
        
        // Backing collection should have element at setIdx
        assertTrue("Backing collection should have been updated", 
            backingCollection.get(setIdx).equals( new CollectionTestObject("ADDED OBJ") ) );
        
        // Rewind the iterator back to the starting index so we can check it.
        for (int i = getStartingIndex(); i <= setIdx; i++) {
            iterator.previous();
        }
        
        checkListIterator(iterator, list, getStartingIndex() );
        
        // Get a new ListIterator from the backing Collection and make sure object was added.
        iterator = (ListIterator)createIterator(backingCollection);
        checkListIterator(iterator, list, getStartingIndex() );
    }
}

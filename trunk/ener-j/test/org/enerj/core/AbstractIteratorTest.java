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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractIteratorTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement java.util.Iterator.
 * Test methods are final so that the contract of Iterator is guaranteed to be tested.
 *
 * @version $Id: AbstractIteratorTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractIteratorTest extends TestCase
{
    //----------------------------------------------------------------------
    public AbstractIteratorTest(String aName) 
    {
        super(aName);
    }
    
    //----------------------------------------------------------------------
    /**
     * Creates an instance of a Collection that will be used to test the Iterator.
     * The returned collection is passed to createIterator to generate the Iterator
     * backed by the Collection. Subclass implements this
     * to create a specific type of Iterator. 
     *
     * @param aCollection some Objects over which the iterator is expected to
     *  iterate. This Collection is created by the test case. The resulting Collection
     *  does not have to iterate over the objects in the order
     *  of aCollection (as is the case with HashMap.keySet()).
     */
    abstract public Collection createIteratorCollection(Collection aCollection) throws Exception;
    
    //----------------------------------------------------------------------
    /**
     * Creates an Iterator based on aCollection (a Collection instance returned
     * by createIteratorCollection).
     *
     * @param aCollection a Collection returned by createIteratorColllection 
     *  over which the iterator is expected to
     *  iterate. It is expected that mutation methods such as Iterator.remove() 
     *  removes from aCollection (the backing Collection).
     */
    abstract public Iterator createIterator(Collection aCollection) throws Exception;
    
    //----------------------------------------------------------------------
    /**
     * Check if iterator supports remove().
     *
     * @return true if the Iterator returned by createIterator supported the
     *  remove() operation, else false.
     */
    abstract public boolean supportsRemove();
    
    //----------------------------------------------------------------------
    /**
     * Checks the iterator against a list of known objects. Iterator does
     * not have to be in the order of the list.
     *
     * @param anIterator the Iterator to be tested.
     * @param aList an ArrayList containing the Objects which should be in the iterator.
     *
     * @throws Exception if an error occurs.
     */
    protected void checkIterator(Iterator anIterator, ArrayList aList) throws Exception
    {
        boolean[] checkList = new boolean[ aList.size() ];

        while (anIterator.hasNext()) {
            CollectionTestObject obj = (CollectionTestObject)anIterator.next();

            int idx = aList.indexOf(obj);
            assertTrue("Should have found object", idx >= 0);
            checkList[idx] = true;
        }
        
        // Check list should be all true now.
        for (int i = 0; i < checkList.length; i++) {
            assertTrue("Check list entry " + i + " should be true", checkList[i]);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Method to run all tests. Can be used with "new IteratorTest("runAllTests").runBare()" 
     * to test an Iterator from another test method.
     */
    public void runAllTests() throws Exception
    {
        testWalk();
        testRemove();
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests hasNext(), next(). Not final so ListIterator can override.
     */
    public final void testWalk() throws Exception
    {
        final int listSize = 10;
        ArrayList list = new ArrayList(listSize);
        for (int i = 0; i < listSize; i++) {
            list.add( new CollectionTestObject("Obj-" + i) );
        }

        Iterator iterator = createIterator( createIteratorCollection(list) );
        checkIterator(iterator, list);
    }

    //----------------------------------------------------------------------
    /**
     * Tests remove().
     */
    public final void testRemove() throws Exception
    {
        if (!supportsRemove()) {
            // Iterator doesn't support remove() - don't test it.
            return;
        }
        
        final int listSize = 10;
        final int removeIdx = 7;
        ArrayList list = new ArrayList(listSize);
        CollectionTestObject removeObj = null;
        for (int i = 0; i < listSize; i++) {
            CollectionTestObject obj = new CollectionTestObject("Obj-" + i);
            list.add(obj);
            if (i == removeIdx) {
                removeObj = obj;
            }
        }

        Collection backingCollection = createIteratorCollection(list);
        Iterator iterator = createIterator(backingCollection);
        boolean removed = false;
        while (iterator.hasNext()) {
            CollectionTestObject obj = (CollectionTestObject)iterator.next();
            if (obj.equals(removeObj)) {
                iterator.remove();
            }
        }
        
        // Remove from our list to stay in sync with backingCollection.
        list.remove(removeIdx);
        
        // Get a new Iterator from the backing Collection and make sure object was removed.
        iterator = createIterator(backingCollection);
        checkIterator(iterator, list);
    }

}

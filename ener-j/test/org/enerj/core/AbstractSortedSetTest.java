// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractSortedSetTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement java.util.SortedSet.
 *
 * Test methods are final so that the contract of SortedSet is guaranteed to be tested.
 *
 * @version $Id: AbstractSortedSetTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractSortedSetTest extends AbstractSetTest
{
    //----------------------------------------------------------------------
    public AbstractSortedSetTest(String aName) 
    {
        super(aName);
    }

    //----------------------------------------------------------------------
    /**
     * Tests comparator().
     */
    public final void testComparator() throws Exception
    {
        SortedSet testSortedSet = (SortedSet)createCollection();
        
        // Just test calling it. It may return null or a Comparator, but we can't check 
        // the validitiy of that. This just ensures that it doesn't throw.
        Comparator comparator = testSortedSet.comparator();
    }

    //----------------------------------------------------------------------
    /**
     * Tests first().
     */
    public final void testFirst() throws Exception
    {
        SortedSet testSortedSet = (SortedSet)createCollection();

        // Add items to set in different order, the set should sort them.
        testSortedSet.add( new CollectionTestObject("Obj3") );
        testSortedSet.add( new CollectionTestObject("Obj1") );
        testSortedSet.add( new CollectionTestObject("Obj2") );

        assertTrue("Should be first element", testSortedSet.first().equals( new CollectionTestObject("Obj1") ) );
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests headSet().
     */
    public final void testHeadSet() throws Exception
    {
        SortedSet testSortedSet = (SortedSet)createCollection();
        
        final int listSize = 10;
        final int stopIdx = 4; // less  than this
        for (int i = 0; i < listSize; i++) {
            testSortedSet.add( new CollectionTestObject("Obj-" + i) );
        }
        
        SortedSet headSet = testSortedSet.headSet( new CollectionTestObject("Obj-" + stopIdx) );
        assertTrue("Size should be correct", headSet.size() == stopIdx);
        Iterator iterator = headSet.iterator();
        for (int i = 0; i < stopIdx; i++) {
            CollectionTestObject object = (CollectionTestObject)iterator.next();
            assertTrue("Value should match", object.equals( new CollectionTestObject("Obj-" + i) ) );
        }
        
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests last().
     */
    public final void testLast() throws Exception
    {
        SortedSet testSortedSet = (SortedSet)createCollection();

        // Add items to set in different order, the set should sort them.
        testSortedSet.add( new CollectionTestObject("Obj1") );
        testSortedSet.add( new CollectionTestObject("Obj4") );
        testSortedSet.add( new CollectionTestObject("Obj3") );
        testSortedSet.add( new CollectionTestObject("Obj2") );

        assertTrue("Should be last element", testSortedSet.last().equals( new CollectionTestObject("Obj4") ) );
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests subSet().
     */
    public final void testSubSet() throws Exception
    {
        SortedSet testSortedSet = (SortedSet)createCollection();
        
        final int listSize = 10;
        final int startIdx = 2; // Inclusive
        final int stopIdx = 6;  // Exclusive
        for (int i = 0; i < listSize; i++) {
            testSortedSet.add( new CollectionTestObject("Obj-" + i) );
        }
        
        SortedSet subSet = testSortedSet.subSet( new CollectionTestObject("Obj-" + startIdx),
                                 new CollectionTestObject("Obj-" + stopIdx));

        assertTrue("Size should be correct", subSet.size() == (stopIdx - startIdx));
        Iterator iterator = subSet.iterator();
        for (int i = startIdx; i < stopIdx; i++) {
            CollectionTestObject object = (CollectionTestObject)iterator.next();
            assertTrue("Value should match", object.equals( new CollectionTestObject("Obj-" + i) ) );
        }
        
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests tailSet().
     */
    public final void testTailSet() throws Exception
    {
        SortedSet testSortedSet = (SortedSet)createCollection();
        
        final int listSize = 10;
        final int startIdx = 4; // >= this
        for (int i = 0; i < listSize; i++) {
            testSortedSet.add( new CollectionTestObject("Obj-" + i) );
        }
        
        SortedSet tailSet = testSortedSet.tailSet( new CollectionTestObject("Obj-" + startIdx) );
        assertTrue("Size should be correct", tailSet.size() == (listSize - startIdx) );
        Iterator iterator = tailSet.iterator();
        for (int i = startIdx; i < listSize; i++) {
            CollectionTestObject object = (CollectionTestObject)iterator.next();
            assertTrue("Value should match", object.equals( new CollectionTestObject("Obj-" + i) ) );
        }
        
    }
    
}

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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractDSetTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement org.odmg.DSet.
 * Ener-J's org.odmg.QueryableCollection must be tested separately.
 *
 * Test methods are final so that the contract of DSet is guaranteed to be tested.
 *
 * @version $Id: AbstractDSetTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractDSetTest extends AbstractSetTest
{
    //----------------------------------------------------------------------
    public AbstractDSetTest(String aName) 
    {
        super(aName);
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests union(DSet).
     */
    public final void testUnion() throws Exception
    {
        DSet testDSet = (DSet)createCollection();
        
        testDSet.add( new CollectionTestObject("Obj1") );
        testDSet.add( new CollectionTestObject("Obj2") );
        testDSet.add( new CollectionTestObject("Obj3") );
        
        DSet dset2 = (DSet)createCollection();
        dset2.add( new CollectionTestObject("Obj4") );
        dset2.add( new CollectionTestObject("Obj5") );
        dset2.add( new CollectionTestObject("Obj6") );
        dset2.add( new CollectionTestObject("Obj7") );
        
        DSet dset3 = testDSet.union(dset2);

        assertTrue("Size should be correct", dset3.size() == 7);
        assertTrue("Should contain all of first collection", dset3.containsAll(testDSet) );
        assertTrue("Should contain all of second collection", dset3.containsAll(dset2) );
        assertTrue("First DSet should be unaffected", testDSet.size() == 3);
        assertTrue("Second DSet should be unaffected", dset2.size() == 4);
        
        // Test first bag empty
        testDSet.clear();
        dset3 = testDSet.union(dset2);
        assertTrue("Size should be correct", dset3.size() == 4);
        assertTrue("Should contain all of second collection", dset3.containsAll(dset2) );
        
        // Test second bag empty
        dset3 = dset2.union(testDSet);
        assertTrue("Size should be correct", dset3.size() == 4);
        assertTrue("Should contain all of second collection", dset3.containsAll(dset2) );
        
        // Test both bags empty
        testDSet.clear();
        dset2.clear();
        dset3 = dset2.union(testDSet);
        assertTrue("Size should be correct", dset3.size() == 0);
    }

    //----------------------------------------------------------------------
    /**
     * Tests intersection(DSet).
     */
    public final void testIntersection() throws Exception
    {
        DSet testDSet = (DSet)createCollection();
        
        testDSet.add( new CollectionTestObject("Obj1") );
        testDSet.add( new CollectionTestObject("Obj2") );
        testDSet.add( new CollectionTestObject("Obj3") );
        testDSet.add( new CollectionTestObject("Obj4") );
        testDSet.add( new CollectionTestObject("Obj5") );
        
        DSet dset2 = (DSet)createCollection();
        dset2.add( new CollectionTestObject("ObjZ") );
        dset2.add( new CollectionTestObject("Obj2") );
        dset2.add( new CollectionTestObject("Obj4") );
        dset2.add( new CollectionTestObject("ObjY") );
        dset2.add( new CollectionTestObject("Obj1") );
        dset2.add( new CollectionTestObject("Obj9") );
        dset2.add( new CollectionTestObject("ObjX") );
        
        DSet dset3 = testDSet.intersection(dset2);

        assertTrue("Size should be correct", dset3.size() == 3);
        assertTrue("Should contain Obj2", dset3.contains( new CollectionTestObject("Obj2") ) );
        assertTrue("Should contain Obj4", dset3.contains( new CollectionTestObject("Obj4") ) );
        assertTrue("Should contain Obj1", dset3.contains( new CollectionTestObject("Obj1") ) );
        assertTrue("First DSet should be unaffected", testDSet.size() == 5);
        assertTrue("Second DSet should be unaffected", dset2.size() == 7);

        // Test first bag empty
        testDSet.clear();
        dset3 = testDSet.intersection(dset2);
        assertTrue("Size should be correct", dset3.size() == 0);
        
        // Test second bag empty
        dset3 = dset2.intersection(testDSet);
        assertTrue("Size should be correct", dset3.size() == 0);
        
        // Test both bags empty
        testDSet.clear();
        dset2.clear();
        dset3 = dset2.intersection(testDSet);
        assertTrue("Size should be correct", dset3.size() == 0);
    }

    //----------------------------------------------------------------------
    /**
     * Tests difference(DSet).
     */
    public final void testDifference() throws Exception
    {
        DSet testDSet = (DSet)createCollection();
        
        testDSet.add( new CollectionTestObject("Obj1") );
        testDSet.add( new CollectionTestObject("Obj2") );
        testDSet.add( new CollectionTestObject("Obj3") );
        testDSet.add( new CollectionTestObject("Obj4") );
        testDSet.add( new CollectionTestObject("Obj5") );
        
        DSet dset2 = (DSet)createCollection();
        dset2.add( new CollectionTestObject("ObjZ") );
        dset2.add( new CollectionTestObject("Obj2") );
        dset2.add( new CollectionTestObject("Obj4") );
        dset2.add( new CollectionTestObject("ObjY") );
        dset2.add( new CollectionTestObject("Obj1") );
        dset2.add( new CollectionTestObject("Obj9") );
        dset2.add( new CollectionTestObject("ObjX") );
        
        DSet dset3 = testDSet.difference(dset2);

        assertTrue("Size should be correct", dset3.size() == 2);
        assertTrue("Should contain Obj3", dset3.contains( new CollectionTestObject("Obj3") ) );
        assertTrue("Should contain Obj5", dset3.contains( new CollectionTestObject("Obj5") ) );
        assertTrue("First DSet should be unaffected", testDSet.size() == 5);
        assertTrue("Second DSet should be unaffected", dset2.size() == 7);

        // Turn difference around
        dset3 = dset2.difference(testDSet);

        assertTrue("Size should be correct", dset3.size() == 4);
        assertTrue("Should contain ObjZ", dset3.contains( new CollectionTestObject("ObjZ") ) );
        assertTrue("Should contain ObjY", dset3.contains( new CollectionTestObject("ObjY") ) );
        assertTrue("Should contain Obj9", dset3.contains( new CollectionTestObject("Obj9") ) );
        assertTrue("Should contain ObjX", dset3.contains( new CollectionTestObject("ObjX") ) );

        // Test first bag empty
        testDSet.clear();
        dset3 = testDSet.difference(dset2);
        assertTrue("Size should be correct", dset3.size() == 0);
        
        // Test second bag empty
        dset3 = dset2.difference(testDSet);
        assertTrue("Size should be correct", dset3.size() == 7);
        
        // Test both bags empty
        testDSet.clear();
        dset2.clear();
        dset3 = dset2.difference(testDSet);
        assertTrue("Size should be correct", dset3.size() == 0);
    }

    //----------------------------------------------------------------------
    /**
     * Tests subsetOf(DSet).
     */
    public final void testSubsetOf() throws Exception
    {
        DSet testDSet = (DSet)createCollection();
        testDSet.add( new CollectionTestObject("Obj1") );
        testDSet.add( new CollectionTestObject("Obj2") );
        testDSet.add( new CollectionTestObject("Obj3") );
        testDSet.add( new CollectionTestObject("Obj4") );
        testDSet.add( new CollectionTestObject("Obj5") );
        
        DSet dset2 = (DSet)createCollection();
        dset2.add( new CollectionTestObject("Obj0") );
        dset2.add( new CollectionTestObject("Obj2") );
        dset2.add( new CollectionTestObject("Obj4") );
        dset2.add( new CollectionTestObject("Obj3") );
        dset2.add( new CollectionTestObject("Obj1") );
        dset2.add( new CollectionTestObject("Obj5") );
        dset2.add( new CollectionTestObject("Obj6") );
        
        assertTrue("First set is subset of second", testDSet.subsetOf(dset2) );
        assertTrue("Second set is not subset of first", !dset2.subsetOf(testDSet) );
        
        // Same sets
        assertTrue("First set is a subset of itself", testDSet.subsetOf(testDSet) );
        assertTrue("Second set is a subset of itself", dset2.subsetOf(dset2) );

        // First set empty and Second set empty
        testDSet.clear();
        assertTrue("First set is subset of second", testDSet.subsetOf(dset2) );
        assertTrue("Second set is not subset of first", !dset2.subsetOf(testDSet) );
        
        // Both sets empty
        dset2.clear();
        assertTrue("First set is subset of second", testDSet.subsetOf(dset2) );
        assertTrue("Second set is subset of first", dset2.subsetOf(testDSet) );
    }

    //----------------------------------------------------------------------
    /**
     * Tests supersetOf(DSet).
     */
    public final void testSupersetOf() throws Exception
    {
        DSet testDSet = (DSet)createCollection();
        testDSet.add( new CollectionTestObject("Obj0") );
        testDSet.add( new CollectionTestObject("Obj2") );
        testDSet.add( new CollectionTestObject("Obj4") );
        testDSet.add( new CollectionTestObject("Obj3") );
        testDSet.add( new CollectionTestObject("Obj1") );
        testDSet.add( new CollectionTestObject("Obj5") );
        testDSet.add( new CollectionTestObject("Obj6") );
        
        DSet dset2 = (DSet)createCollection();
        dset2.add( new CollectionTestObject("Obj1") );
        dset2.add( new CollectionTestObject("Obj2") );
        dset2.add( new CollectionTestObject("Obj3") );
        dset2.add( new CollectionTestObject("Obj4") );
        dset2.add( new CollectionTestObject("Obj5") );
        
        assertTrue("First set is superset of second", testDSet.supersetOf(dset2) );
        assertTrue("Second set is not superset of first", !dset2.supersetOf(testDSet) );
        
        // Same sets
        assertTrue("First set is a superset of itself", testDSet.supersetOf(testDSet) );
        assertTrue("Second set is a superset of itself", dset2.supersetOf(dset2) );

        // First set empty and Second set empty
        testDSet.clear();
        assertTrue("First set is not superset of second", !testDSet.supersetOf(dset2) );
        assertTrue("Second set is superset of first", dset2.supersetOf(testDSet) );
        
        // Both sets empty
        dset2.clear();
        assertTrue("First set is superset of second", testDSet.supersetOf(dset2) );
        assertTrue("Second set is superset of first", dset2.supersetOf(testDSet) );
    }

    //----------------------------------------------------------------------
    /**
     * Tests properSubsetOf(DSet).
     */
    public final void testProperSubsetOf() throws Exception
    {
        DSet testDSet = (DSet)createCollection();
        testDSet.add( new CollectionTestObject("Obj1") );
        testDSet.add( new CollectionTestObject("Obj2") );
        testDSet.add( new CollectionTestObject("Obj3") );
        testDSet.add( new CollectionTestObject("Obj4") );
        testDSet.add( new CollectionTestObject("Obj5") );
        
        DSet dset2 = (DSet)createCollection();
        dset2.add( new CollectionTestObject("Obj0") );
        dset2.add( new CollectionTestObject("Obj2") );
        dset2.add( new CollectionTestObject("Obj4") );
        dset2.add( new CollectionTestObject("Obj3") );
        dset2.add( new CollectionTestObject("Obj1") );
        dset2.add( new CollectionTestObject("Obj5") );
        dset2.add( new CollectionTestObject("Obj6") );
        
        assertTrue("First set is properSubset of second", testDSet.properSubsetOf(dset2) );
        assertTrue("Second set is not properSubset of first", !dset2.properSubsetOf(testDSet) );
        
        // Same sets
        assertTrue("First set is not a properSubset of itself", !testDSet.properSubsetOf(testDSet) );
        assertTrue("Second set is not a properSubset of itself", !dset2.properSubsetOf(dset2) );

        // First set empty and Second set empty
        testDSet.clear();
        assertTrue("First set is properSubset of second", testDSet.properSubsetOf(dset2) );
        assertTrue("Second set is not properSubset of first", !dset2.properSubsetOf(testDSet) );
        
        // Both sets empty
        dset2.clear();
        assertTrue("First set is not a properSubset of second", !testDSet.properSubsetOf(dset2) );
        assertTrue("Second set is not a properSubset of first", !dset2.properSubsetOf(testDSet) );
    }

    //----------------------------------------------------------------------
    /**
     * Tests properSupersetOf(DSet).
     */
    public final void testProperSupersetOf() throws Exception
    {
        DSet testDSet = (DSet)createCollection();
        testDSet.add( new CollectionTestObject("Obj0") );
        testDSet.add( new CollectionTestObject("Obj2") );
        testDSet.add( new CollectionTestObject("Obj4") );
        testDSet.add( new CollectionTestObject("Obj3") );
        testDSet.add( new CollectionTestObject("Obj1") );
        testDSet.add( new CollectionTestObject("Obj5") );
        testDSet.add( new CollectionTestObject("Obj6") );
        
        DSet dset2 = (DSet)createCollection();
        dset2.add( new CollectionTestObject("Obj1") );
        dset2.add( new CollectionTestObject("Obj2") );
        dset2.add( new CollectionTestObject("Obj3") );
        dset2.add( new CollectionTestObject("Obj4") );
        dset2.add( new CollectionTestObject("Obj5") );
        
        assertTrue("First set is properSuperset of second", testDSet.properSupersetOf(dset2) );
        assertTrue("Second set is not properSuperset of first", !dset2.properSupersetOf(testDSet) );
        
        // Same sets
        assertTrue("First set is not a properSuperset of itself", !testDSet.properSupersetOf(testDSet) );
        assertTrue("Second set is not a properSuperset of itself", !dset2.properSupersetOf(dset2) );

        // First set empty and Second set empty
        testDSet.clear();
        assertTrue("First set is not properSuperset of second", !testDSet.properSupersetOf(dset2) );
        assertTrue("Second set is properSuperset of first", dset2.properSupersetOf(testDSet) );
        
        // Both sets empty
        dset2.clear();
        assertTrue("First set is not a properSuperset of second", !testDSet.properSupersetOf(dset2) );
        assertTrue("Second set is not a properSuperset of first", !dset2.properSupersetOf(testDSet) );
    }
}

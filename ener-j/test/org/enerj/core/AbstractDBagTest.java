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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractDBagTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement org.odmg.DBag.
 * Ener-J's org.odmg.QueryableCollection must be tested separately.
 *
 * Test methods are final so that the contract of List is guaranteed to be tested.
 *
 * @version $Id: AbstractDBagTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractDBagTest extends AbstractCollectionTest
{

    public AbstractDBagTest(String aName) 
    {
        super(aName);
    }
    

    /**
     * Tests union(DBag).
     */
    public final void testUnion() throws Exception
    {
        DBag testDBag = (DBag)createCollection();
        
        testDBag.add( new CollectionTestObject("Obj1") );
        testDBag.add( new CollectionTestObject("Obj2") );
        testDBag.add( new CollectionTestObject("Obj3") );
        
        DBag dbag2 = (DBag)createCollection();
        dbag2.add( new CollectionTestObject("Obj4") );
        dbag2.add( new CollectionTestObject("Obj5") );
        dbag2.add( new CollectionTestObject("Obj6") );
        dbag2.add( new CollectionTestObject("Obj7") );
        
        DBag dbag3 = testDBag.union(dbag2);

        assertTrue("Size should be correct", dbag3.size() == 7);
        assertTrue("Should contain all of first collection", dbag3.containsAll(testDBag) );
        assertTrue("Should contain all of second collection", dbag3.containsAll(dbag2) );
        assertTrue("First DBag should be unaffected", testDBag.size() == 3);
        assertTrue("Second DBag should be unaffected", dbag2.size() == 4);
        
        // Test first bag empty
        testDBag.clear();
        dbag3 = testDBag.union(dbag2);
        assertTrue("Size should be correct", dbag3.size() == 4);
        assertTrue("Should contain all of second collection", dbag3.containsAll(dbag2) );
        
        // Test second bag empty
        dbag3 = dbag2.union(testDBag);
        assertTrue("Size should be correct", dbag3.size() == 4);
        assertTrue("Should contain all of second collection", dbag3.containsAll(dbag2) );
        
        // Test both bags empty
        testDBag.clear();
        dbag2.clear();
        dbag3 = dbag2.union(testDBag);
        assertTrue("Size should be correct", dbag3.size() == 0);
    }


    /**
     * Tests intersection(DBag).
     */
    public final void testIntersection() throws Exception
    {
        DBag testDBag = (DBag)createCollection();
        
        testDBag.add( new CollectionTestObject("Obj1") );
        testDBag.add( new CollectionTestObject("Obj2") );
        testDBag.add( new CollectionTestObject("Obj3") );
        testDBag.add( new CollectionTestObject("Obj4") );
        testDBag.add( new CollectionTestObject("Obj5") );
        
        DBag dbag2 = (DBag)createCollection();
        dbag2.add( new CollectionTestObject("ObjZ") );
        dbag2.add( new CollectionTestObject("Obj2") );
        dbag2.add( new CollectionTestObject("Obj4") );
        dbag2.add( new CollectionTestObject("ObjY") );
        dbag2.add( new CollectionTestObject("Obj1") );
        dbag2.add( new CollectionTestObject("Obj9") );
        dbag2.add( new CollectionTestObject("ObjX") );
        
        DBag dbag3 = testDBag.intersection(dbag2);

        assertTrue("Size should be correct", dbag3.size() == 3);
        assertTrue("Should contain Obj2", dbag3.contains( new CollectionTestObject("Obj2") ) );
        assertTrue("Should contain Obj4", dbag3.contains( new CollectionTestObject("Obj4") ) );
        assertTrue("Should contain Obj1", dbag3.contains( new CollectionTestObject("Obj1") ) );
        assertTrue("First DBag should be unaffected", testDBag.size() == 5);
        assertTrue("Second DBag should be unaffected", dbag2.size() == 7);

        // Test first bag empty
        testDBag.clear();
        dbag3 = testDBag.intersection(dbag2);
        assertTrue("Size should be correct", dbag3.size() == 0);
        
        // Test second bag empty
        dbag3 = dbag2.intersection(testDBag);
        assertTrue("Size should be correct", dbag3.size() == 0);
        
        // Test both bags empty
        testDBag.clear();
        dbag2.clear();
        dbag3 = dbag2.intersection(testDBag);
        assertTrue("Size should be correct", dbag3.size() == 0);
    }


    /**
     * Tests difference(DBag).
     */
    public final void testDifference() throws Exception
    {
        DBag testDBag = (DBag)createCollection();
        
        testDBag.add( new CollectionTestObject("Obj1") );
        testDBag.add( new CollectionTestObject("Obj2") );
        testDBag.add( new CollectionTestObject("Obj3") );
        testDBag.add( new CollectionTestObject("Obj4") );
        testDBag.add( new CollectionTestObject("Obj5") );
        
        DBag dbag2 = (DBag)createCollection();
        dbag2.add( new CollectionTestObject("ObjZ") );
        dbag2.add( new CollectionTestObject("Obj2") );
        dbag2.add( new CollectionTestObject("Obj4") );
        dbag2.add( new CollectionTestObject("ObjY") );
        dbag2.add( new CollectionTestObject("Obj1") );
        dbag2.add( new CollectionTestObject("Obj9") );
        dbag2.add( new CollectionTestObject("ObjX") );
        
        DBag dbag3 = testDBag.difference(dbag2);

        assertTrue("Size should be correct", dbag3.size() == 2);
        assertTrue("Should contain Obj3", dbag3.contains( new CollectionTestObject("Obj3") ) );
        assertTrue("Should contain Obj5", dbag3.contains( new CollectionTestObject("Obj5") ) );
        assertTrue("First DBag should be unaffected", testDBag.size() == 5);
        assertTrue("Second DBag should be unaffected", dbag2.size() == 7);

        // Turn difference around
        dbag3 = dbag2.difference(testDBag);

        assertTrue("Size should be correct", dbag3.size() == 4);
        assertTrue("Should contain ObjZ", dbag3.contains( new CollectionTestObject("ObjZ") ) );
        assertTrue("Should contain ObjY", dbag3.contains( new CollectionTestObject("ObjY") ) );
        assertTrue("Should contain Obj9", dbag3.contains( new CollectionTestObject("Obj9") ) );
        assertTrue("Should contain ObjX", dbag3.contains( new CollectionTestObject("ObjX") ) );

        // Test first bag empty
        testDBag.clear();
        dbag3 = testDBag.difference(dbag2);
        assertTrue("Size should be correct", dbag3.size() == 0);
        
        // Test second bag empty
        dbag3 = dbag2.difference(testDBag);
        assertTrue("Size should be correct", dbag3.size() == 7);
        
        // Test both bags empty
        testDBag.clear();
        dbag2.clear();
        dbag3 = dbag2.difference(testDBag);
        assertTrue("Size should be correct", dbag3.size() == 0);
    }


    /**
     * Tests occurances(Object).
     */
    public final void testOccurances() throws Exception
    {
        DBag testDBag = (DBag)createCollection();
        
        testDBag.add( new CollectionTestObject("Obj5") );
        testDBag.add( new CollectionTestObject("Obj1") );
        testDBag.add( new CollectionTestObject("Obj2") );
        testDBag.add( new CollectionTestObject("Obj3") );
        testDBag.add( new CollectionTestObject("Obj3") );
        testDBag.add( new CollectionTestObject("Obj3") );
        testDBag.add( new CollectionTestObject("Obj4") );
        testDBag.add( new CollectionTestObject("Obj3") );
        testDBag.add( new CollectionTestObject("Obj5") );
        
        assertTrue("Should contain two of Obj5", testDBag.occurrences(  new CollectionTestObject("Obj5") ) == 2);
        assertTrue("Should contain two of Obj3", testDBag.occurrences(  new CollectionTestObject("Obj3") ) == 4 );
        assertTrue("Should contain one of Obj1", testDBag.occurrences(  new CollectionTestObject("Obj1") ) == 1 );
        assertTrue("Should contain none of ObjX", testDBag.occurrences(  new CollectionTestObject("ObjX") ) == 0 );
    }
}

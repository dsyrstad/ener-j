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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractLargeListTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;


/**
 * Generically Tests classes that implement java.util.LargeList.
 *
 * Test methods are final so that the contract of LargeList is guaranteed to be tested.
 *
 * @version $Id: AbstractLargeListTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractLargeListTest extends AbstractLargeCollectionTest
{

    public AbstractLargeListTest(String aName) 
    {
        super(aName);
    }
    

    /**
     * Tests insertElements(long, long) and getAtIndex(long).
     */
    public final void testInsertElementsAndGetAtIndex() throws Exception
    {

        // insert mid-list
        LargeList testLargeList = (LargeList)createLargeCollection();
        
        final long insertIdx = 5;
        final long insertCount = 326;
        final long listSize = 10;
        for (long i = 0; i < listSize; i++) {
            testLargeList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testLargeList.insertElements(insertIdx, insertCount);

        assertTrue("Should have the correct number of elements", testLargeList.sizeAsLong() == (listSize + insertCount));

        int objIdx = 0;
        for (long i = 0; i < (listSize + insertCount); ++i) {
            CollectionTestObject obj = (CollectionTestObject)testLargeList.getAtIndex(i);
            if (i < insertIdx || i >= (insertIdx + insertCount)) {
                assertTrue("LargeList element at " + i + " should be equal", 
                    obj.equals( new CollectionTestObject("Obj-" + objIdx) ) );
                ++objIdx;
            }
            else {
                assertNull("LargeList element at " + i + " should be null.", obj);
            }
        }


        // out of bounds -1 and > sizeAsLong()
        try {
            testLargeList.insertElements(-1L, 1L);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testLargeList.insertElements(0L, -1L);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testLargeList.insertElements( testLargeList.sizeAsLong() + 1, 1L);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }


        // index == sizeAsLong() to append.
        testLargeList.clear();
        for (long i = 0; i < (listSize - 1); i++) {
            testLargeList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testLargeList.insertElements(testLargeList.sizeAsLong(), 1L);

        assertTrue("Should have the correct number of elements", testLargeList.sizeAsLong() == listSize);

        for (long i = 0; i < listSize; ++i) {
            CollectionTestObject obj = (CollectionTestObject)testLargeList.getAtIndex(i);
            if (i < (listSize - 1)) {
                assertTrue("LargeList element at " + i + " should be equal", 
                    obj.equals( new CollectionTestObject("Obj-" + i) ) );
            }
            else {
                assertNull("LargeList element at " + i + " should be null", obj);
            }
        }


        // index == 0 to insert before everything
        testLargeList.clear();
        for (long i = 1; i < listSize; i++) {
            testLargeList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testLargeList.insertElements(0L, 1);

        assertTrue("Should have the correct number of elements", testLargeList.sizeAsLong() == listSize);

        for (long i = 0; i < listSize; ++i) {
            CollectionTestObject obj = (CollectionTestObject)testLargeList.getAtIndex(i);
            if (i == 0) {
                assertNull("LargeList element at " + i + " should be null", obj);
            }
            else {
                assertTrue("LargeList element at " + i + " should be equal", 
                    obj.equals( new CollectionTestObject("Obj-" + i) ) );
            }
        }

        

        // index == 0 on empty list.
        testLargeList.clear();
        testLargeList.insertElements(0L, 1L);

        assertTrue("Should have the correct number of elements", testLargeList.sizeAsLong() == 1L);
        CollectionTestObject obj = (CollectionTestObject)testLargeList.getAtIndex(0L);
        assertNull("First element should be null", obj);
        

        // test index == sizeAsLong() on getAtIndex - exception
        try {
            testLargeList.getAtIndex( testLargeList.sizeAsLong() );
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }
    

    /**
     * Tests indexOfAsLong(Object).
     */
    public final void testIndexOfAsLong() throws Exception
    {
        LargeList testLargeList = (LargeList)createLargeCollection();

        final long listSize = 10;
        for (long i = 0; i < listSize; i++) {
            testLargeList.add( new CollectionTestObject("Obj-" + i) );
        }

        assertTrue("Should have the correct number of elements", testLargeList.sizeAsLong() == listSize);
        for (long i = 0; i < listSize; i++) {
            assertTrue("Index should be correct", testLargeList.indexOfAsLong( new CollectionTestObject("Obj-" + i) ) == i );
        }
        
        // index not found
        assertTrue("Index should not be found", testLargeList.indexOfAsLong( new CollectionTestObject("XYZ") ) == -1 );
        
        // Should return first index of duplicate objects 
        testLargeList.add( new CollectionTestObject("Dup") );
        testLargeList.add( new CollectionTestObject("Dup") );

        assertTrue("Index should be of first duplicate", testLargeList.indexOfAsLong( new CollectionTestObject("Dup") ) == listSize );
    }
    

    /**
     * Tests lastIndexOfAsLong(Object).
     */
    public final void testLastIndexOf() throws Exception
    {
        LargeList testLargeList = (LargeList)createLargeCollection();

        final long listSize = 10;
        for (long i = 0; i < listSize; i++) {
            testLargeList.add( new CollectionTestObject("Obj-" + i) );
        }

        assertTrue("Should have the correct number of elements", testLargeList.sizeAsLong() == listSize);
        for (long i = 0; i < listSize; i++) {
            assertTrue("Index should be correct", testLargeList.lastIndexOfAsLong( new CollectionTestObject("Obj-" + i) ) == i );
        }
        
        // index not found
        assertTrue("Index should not be found", testLargeList.lastIndexOfAsLong( new CollectionTestObject("XYZ") ) == -1 );
        
        // Should return last index of duplicate objects 
        testLargeList.add( new CollectionTestObject("Dup") );
        testLargeList.add( new CollectionTestObject("Dup") );

        assertTrue("Index should be of last duplicate", testLargeList.lastIndexOfAsLong( new CollectionTestObject("Dup") ) == (listSize + 1) );
    }


    /**
     * Tests removeElements(long, long).
     */
    public final void testIndexedRemove() throws Exception
    {
        LargeList testLargeList = (LargeList)createLargeCollection();
        final long listSize = 100;
        final long removeIdx = 5;
        final long removeCount = 34;
        for (long i = 0; i < listSize; i++) {
            if (i == removeIdx) {
                for (long j = 0; j < removeCount; j++) {
                    testLargeList.add( new CollectionTestObject("REMOVE ME") );
                }
            }

            testLargeList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testLargeList.removeElements(removeIdx, removeCount);

        assertTrue("Should have the correct number of elements", testLargeList.sizeAsLong() == listSize);
        for (long i = 0; i < listSize; i++) {
            assertTrue("Value should be correct", testLargeList.getAtIndex(i).equals( new CollectionTestObject("Obj-" + i) ) );
        }


        // out of bounds -1 and >= size()
        try {
            testLargeList.removeElements(-1L, 1L);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testLargeList.removeElements(1L, -1L);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testLargeList.removeElements( testLargeList.sizeAsLong(), 1L);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

    }


    /**
     * Tests setAtIndex(int, Object).
     */
    public final void testIndexedSet() throws Exception
    {
        LargeList testLargeList = (LargeList)createLargeCollection();
        
        final long listSize = 10;
        final long setIdx = 5;
        for (long i = 0; i < listSize; i++) {
            testLargeList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testLargeList.setAtIndex(setIdx, new CollectionTestObject("SET") );

        assertTrue("Should have the correct number of elements", testLargeList.sizeAsLong() == listSize);
        for (long i = 0; i < listSize; i++) {
            if (i == setIdx) {
                assertTrue("Set Value should be correct", testLargeList.getAtIndex(i).equals( 
                    new CollectionTestObject("SET") ) );
            }
            else {
                assertTrue("Value should be correct", testLargeList.getAtIndex(i).equals( 
                    new CollectionTestObject("Obj-" + i) ) );
            }
        }


        // out of bounds -1
        try {
            testLargeList.setAtIndex(-1L, new Object() );
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }


        // Test growing list.
        testLargeList.clear();
        for (long i = 0; i < listSize; i++) {
            testLargeList.add( new CollectionTestObject("Obj-" + i) );
        }

        final long startIndex = testLargeList.sizeAsLong();
        final long growToIndex = startIndex + 56;

        testLargeList.setAtIndex(growToIndex, new CollectionTestObject("SET2") );
        assertTrue("Should have the correct number of elements", testLargeList.sizeAsLong() == (growToIndex + 1));

        for (long i = startIndex; i <= growToIndex; i++) {
            if (i == growToIndex) {
                assertTrue("Set Value should be correct", testLargeList.getAtIndex(i).equals( 
                    new CollectionTestObject("SET2") ) );
            }
            else {
                assertNull("Value should be null", testLargeList.getAtIndex(i) );
            }
        }
    }

}

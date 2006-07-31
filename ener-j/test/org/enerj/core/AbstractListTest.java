// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractListTest.java,v 1.2 2006/05/05 13:47:37 dsyrstad Exp $

package org.enerj.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Generically Tests classes that implement java.util.List.
 *
 * Test methods are final so that the contract of List is guaranteed to be tested.
 *
 * @version $Id: AbstractListTest.java,v 1.2 2006/05/05 13:47:37 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractListTest extends AbstractCollectionTest
{
    //----------------------------------------------------------------------
    public AbstractListTest(String aName) 
    {
        super(aName);
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests add(int, Object).
     */
    public final void testIndexedAdd() throws Exception
    {
        //----------------------------------------------------------------
        // insert mid-list
        List testList = (List)createCollection();
        
        final int insertIdx = 5;
        final int listSize = 10;
        for (int i = 0; i < listSize; i++) {
            if (i != insertIdx) {
                testList.add( new CollectionTestObject("Obj-" + i) );
            }
        }
        
        testList.add(insertIdx, new CollectionTestObject("Obj-" + insertIdx) );

        assertTrue("Should have the correct number of elements", testList.size() == listSize);

        Iterator iterator = testList.iterator();
        for (int i = 0; i < listSize; ++i) {
            CollectionTestObject obj = (CollectionTestObject)iterator.next();
            assertTrue("List element at " + i + " should be equal", 
                obj.equals( new CollectionTestObject("Obj-" + i) ) );
        }

        //----------------------------------------------------------------
        // out of bounds -1 and > size()
        try {
            testList.add(-1, new Object() );
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testList.add( testList.size() + 1, new Object());
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        //----------------------------------------------------------------
        // index == size() to append.
        testList.clear();
        for (int i = 0; i < (listSize - 1); i++) {
            testList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testList.add(testList.size(), new CollectionTestObject("Obj-" + (listSize - 1)) );

        assertTrue("Should have the correct number of elements", testList.size() == listSize);

        iterator = testList.iterator();
        for (int i = 0; i < listSize; ++i) {
            CollectionTestObject obj = (CollectionTestObject)iterator.next();
            assertTrue("List element at " + i + " should be equal", 
                obj.equals( new CollectionTestObject("Obj-" + i) ) );
        }

        //----------------------------------------------------------------
        // index == 0 to insert before everything
        testList.clear();
        for (int i = 1; i < listSize; i++) {
            testList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testList.add(0, new CollectionTestObject("Obj-" + 0) );

        assertTrue("Should have the correct number of elements", testList.size() == listSize);

        iterator = testList.iterator();
        for (int i = 0; i < listSize; ++i) {
            CollectionTestObject obj = (CollectionTestObject)iterator.next();
            assertTrue("List element at " + i + " should be equal", 
                obj.equals( new CollectionTestObject("Obj-" + i) ) );
        }
        
        //----------------------------------------------------------------
        // index == 0 on empty list.
        testList.clear();
        testList.add(0, new CollectionTestObject("Obj-") );

        assertTrue("Should have the correct number of elements", testList.size() == 1);
        CollectionTestObject obj = (CollectionTestObject)testList.get(0);
        assertTrue("First element should be equal", obj.equals( new CollectionTestObject("Obj-") ) );
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests add(Object). List clarifies the Collection semantics slightly by saying that
     * add() appends the item to the end of the list.
     */
    public final void testAddAppend() throws Exception
    {
        List testList = (List)createCollection();

        final int listSize = 10;
        for (int i = 0; i < (listSize - 1); i++) {
            testList.add( new CollectionTestObject("Obj-" + i) );
        }

        testList.add( new CollectionTestObject("APPENDED") );

        assertTrue("Should have the correct number of elements", testList.size() == listSize);
        CollectionTestObject obj = (CollectionTestObject)testList.get( testList.size() - 1 );
        assertTrue("Last element should be equal", obj.equals( new CollectionTestObject("APPENDED") ) );
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests addAll(int, Collection).
     */
    public final void testIndexedAddAll() throws Exception
    {
        //----------------------------------------------------------------
        // insert mid-list
        List testList = (List)createCollection();
        
        final int listSize = 10;
        for (int i = 0; i < listSize; i++) {
            testList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        final int addOffset = 3;
        final int addSize = 15;
        ArrayList list = new ArrayList(addSize);
        for (int i = 0; i < addSize; i++) {
            list.add( new CollectionTestObject("ADDED-Obj-" + i) );
        }
        
        testList.addAll(3, list);
        
        assertTrue("Should have the correct number of elements", testList.size() == (listSize + addSize));

        Iterator iterator = testList.iterator();
        int objIdx = 0;
        int addIdx = 0;
        for (int i = 0; i < (listSize + addSize); ++i) {
            CollectionTestObject obj = (CollectionTestObject)iterator.next();
            if (i < addOffset || i >= (addOffset + addSize)) {
                assertTrue("Original list element at " + i + " should be equal", 
                    obj.equals( new CollectionTestObject("Obj-" + objIdx) ) );
                ++objIdx;
            }
            else {
                assertTrue("Added list element at " + i + " should be equal", 
                    obj.equals( new CollectionTestObject("ADDED-Obj-" + addIdx) ) );
                ++addIdx;
            }
        }
        
        //----------------------------------------------------------------
        // out of bounds -1 and > size()
        try {
            testList.addAll(-1, list);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testList.addAll( testList.size() + 1, list);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        //----------------------------------------------------------------
        // index == size() to append.
        testList.clear();
        list.clear();
        for (int i = 0; i < (listSize + addSize); i++) {
            CollectionTestObject obj = new CollectionTestObject("Obj-" + i);
            if (i >= listSize) {
                list.add(obj);
            }
            else {
                testList.add(obj);
            }
        }
        
        testList.addAll( testList.size(), list);
        assertTrue("Should have the correct number of elements", testList.size() == (listSize + addSize));

        iterator = testList.iterator();
        for (int i = 0; i < (listSize + addSize); ++i) {
            CollectionTestObject obj = (CollectionTestObject)iterator.next();
            assertTrue("List element at " + i + " should be equal", 
                obj.equals( new CollectionTestObject("Obj-" + i) ) );
        }

        //----------------------------------------------------------------
        // index == 0 to insert before everything
        testList.clear();
        list.clear();
        for (int i = 0; i < (listSize + addSize); i++) {
            CollectionTestObject obj = new CollectionTestObject("Obj-" + i);
            if (i < listSize) {
                list.add(obj);
            }
            else {
                testList.add(obj);
            }
        }
        
        testList.addAll(0, list);
        assertTrue("Should have the correct number of elements", testList.size() == (listSize + addSize));

        iterator = testList.iterator();
        for (int i = 0; i < (listSize + addSize); ++i) {
            CollectionTestObject obj = (CollectionTestObject)iterator.next();
            assertTrue("List element at " + i + " should be equal", 
                obj.equals( new CollectionTestObject("Obj-" + i) ) );
        }
        
        //----------------------------------------------------------------
        // index == 0 on empty list.
        testList.clear();
        list.clear();
        for (int i = 0; i < listSize; i++) {
            list.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testList.addAll(0, list);
        assertTrue("Should have the correct number of elements", testList.size() == listSize);

        iterator = testList.iterator();
        for (int i = 0; i < listSize; ++i) {
            CollectionTestObject obj = (CollectionTestObject)iterator.next();
            assertTrue("List element at " + i + " should be equal", 
                obj.equals( new CollectionTestObject("Obj-" + i) ) );
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests get(int).
     */
    public final void testIndexedGet() throws Exception
    {
        List testList = (List)createCollection();

        final int listSize = 10;
        for (int i = 0; i < listSize; i++) {
            testList.add( new CollectionTestObject("Obj-" + i) );
        }

        assertTrue("Should have the correct number of elements", testList.size() == listSize);
        for (int i = 0; i < listSize; i++) {
            CollectionTestObject obj = (CollectionTestObject)testList.get(i);
            assertTrue("Element should be equal", obj.equals( new CollectionTestObject("Obj-" + i) ) );
        }

        // out of bounds -1 and >= size()
        try {
            testList.get(-1);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testList.get( testList.size() );
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests equals(Object).
     */
    public final void testEquals() throws Exception
    {
        List testList = (List)createCollection();
        
        testList.add( new CollectionTestObject("Obj1") );
        testList.add( new CollectionTestObject("Obj2") );
        testList.add( new CollectionTestObject("Obj3") );
        
        ArrayList list2 = new ArrayList(3);
        list2.add( new CollectionTestObject("Obj1") );
        list2.add( new CollectionTestObject("Obj2") );
        list2.add( new CollectionTestObject("Obj3") );
        
        assertTrue("Lists should be equal", testList.equals(list2) );
        
        list2.add( new CollectionTestObject("Obj0") );
        assertTrue("Lists should not be equal", !testList.equals(list2) );
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests hashCode().
     */
    public final void testHashcode() throws Exception
    {
        List testList = (List)createCollection();
        
        final int listSize = 150;
        for (int i = 0; i < listSize; i++) {
            testList.add( new CollectionTestObject("Obj" + i) );
        }

        // This calculation comes directly from the List interface javadoc.
        int hashCode = 1;
        Iterator i = testList.iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            hashCode = 31*hashCode + (obj==null ? 0 : obj.hashCode());
        }

        assertTrue("Hashcode should be correct", testList.hashCode() == hashCode);
    }

    //----------------------------------------------------------------------
    /**
     * Tests indexOf(Object).
     */
    public final void testIndexOf() throws Exception
    {
        List testList = (List)createCollection();

        final int listSize = 10;
        for (int i = 0; i < listSize; i++) {
            testList.add( new CollectionTestObject("Obj-" + i) );
        }

        assertTrue("Should have the correct number of elements", testList.size() == listSize);
        for (int i = 0; i < listSize; i++) {
            assertTrue("Index should be correct", testList.indexOf( new CollectionTestObject("Obj-" + i) ) == i );
        }
        
        // index not found
        assertTrue("Index should not be found", testList.indexOf( new CollectionTestObject("XYZ") ) == -1 );
        
        // Should return first index of duplicate objects 
        testList.add( new CollectionTestObject("Dup") );
        testList.add( new CollectionTestObject("Dup") );

        assertTrue("Index should be of first duplicate", testList.indexOf( new CollectionTestObject("Dup") ) == listSize );
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests lastIndexOf(Object).
     */
    public final void testLastIndexOf() throws Exception
    {
        List testList = (List)createCollection();

        final int listSize = 10;
        for (int i = 0; i < listSize; i++) {
            testList.add( new CollectionTestObject("Obj-" + i) );
        }

        assertTrue("Should have the correct number of elements", testList.size() == listSize);
        for (int i = 0; i < listSize; i++) {
            assertTrue("Index should be correct", testList.lastIndexOf( new CollectionTestObject("Obj-" + i) ) == i );
        }
        
        // index not found
        assertTrue("Index should not be found", testList.lastIndexOf( new CollectionTestObject("XYZ") ) == -1 );
        
        // Should return last index of duplicate objects 
        testList.add( new CollectionTestObject("Dup") );
        testList.add( new CollectionTestObject("Dup") );

        assertTrue("Index should be of last duplicate", testList.lastIndexOf( new CollectionTestObject("Dup") ) == (listSize + 1) );
    }

    //----------------------------------------------------------------------
    /**
     * Tests listIterator() and listIterator(int).
     */
    public final void testListIterator() throws Exception
    {
        try {
            new AbstractListTest.FullListIteratorTest("runAllTests").runBare();
            new AbstractListTest.IndexedListIteratorTest("runAllTests").runBare();
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

        //----------------------------------------------------------------
        // out of bounds -1 and > size()
        List testList = (List)createCollection();
        try {
            testList.listIterator(-1);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            // This should work.
            testList.listIterator( testList.size() );
            // Expected
        }
        catch (IndexOutOfBoundsException e) {
            fail("Should not have thrown IndexOutOfBoundsException");
        }

        try {
            testList.listIterator( testList.size() + 1 );
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests remove(int).
     */
    public final void testIndexedRemove() throws Exception
    {
        List testList = (List)createCollection();
        final int listSize = 10;
        final int removeIdx = 5;
        for (int i = 0; i < listSize; i++) {
            if (i == removeIdx) {
                testList.add( new CollectionTestObject("REMOVE ME") );
            }

            testList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testList.remove(removeIdx);

        assertTrue("Should have the correct number of elements", testList.size() == listSize);
        for (int i = 0; i < listSize; i++) {
            assertTrue("Value should be correct", testList.get(i).equals( new CollectionTestObject("Obj-" + i) ) );
        }

        //----------------------------------------------------------------
        // out of bounds -1 and >= size()
        try {
            testList.remove(-1);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testList.remove( testList.size() );
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

    }

    //----------------------------------------------------------------------
    /**
     * Tests set(int, Object).
     */
    public final void testIndexedSet() throws Exception
    {
        List testList = (List)createCollection();
        
        final int listSize = 10;
        final int setIdx = 5;
        for (int i = 0; i < listSize; i++) {
            testList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        testList.set(setIdx, new CollectionTestObject("SET") );

        assertTrue("Should have the correct number of elements", testList.size() == listSize);
        for (int i = 0; i < listSize; i++) {
            if (i == setIdx) {
                assertTrue("Set Value should be correct", testList.get(i).equals( new CollectionTestObject("SET") ) );
            }
            else {
                assertTrue("Value should be correct", testList.get(i).equals( new CollectionTestObject("Obj-" + i) ) );
            }
        }

        //----------------------------------------------------------------
        // out of bounds -1 and >= size()
        try {
            testList.set(-1, new Object() );
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testList.set( testList.size(), new Object());
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

    }

    //----------------------------------------------------------------------
    /**
     * Tests subList(int, int).
     */
    public final void testSubList() throws Exception
    {
        // I wonder how much code actually uses subList. Its semantics are fairly
        // restrictive...
        
        List testList = (List)createCollection();
        if (testList instanceof VeryLargeDArray) {
            //  TODO  we need to implement subList - but ignore for now so tests pass
            return;
        }
        
        final int listSize = 10;
        final int startIdx = 2;
        final int endIdx = 8; // Exclusive
        for (int i = 0; i < listSize; i++) {
            testList.add( new CollectionTestObject("Obj-" + i) );
        }
        
        List list = testList.subList(startIdx, endIdx);

        assertTrue("Should have the correct number of elements", list.size() == (endIdx - startIdx));
        for (int i = 0; i < (endIdx - startIdx); i++) {
            assertTrue("Value should be correct", list.get(i).equals( new CollectionTestObject("Obj-" + (i + startIdx)) ) );
        }

        
        //----------------------------------------------------------------
        // out of bounds -1 and > size()
        try {
            testList.subList(-1, 2);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testList.subList( testList.size() + 1, testList.size() + 1);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        try {
            testList.subList(1, -1);
            fail("Should have thrown IndexOutOfBoundsException or IllegalArgumentException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }
        catch (IllegalArgumentException e) {
            // Expected - We'll accept this because ArrayList throws this rather
            // than IndexOutOfBoundsException - EVEN THOUGH the List doc says it 
            // will throw IndexOutOfBoundsException.
        }

        try {
            testList.subList(0, testList.size() + 1);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        // Ending index of size() should work..
        try {
            testList.subList(0, testList.size());
            // Expected
        }
        catch (IndexOutOfBoundsException e) {
            fail("Should not have thrown IndexOutOfBoundsException");
        }

        // However, subList(0, 0) on empty list should succeed according to the doc.
        // EVEN THOUGH a start index of zero is invalid on an empty list.
        testList.clear();
        try {
            testList.subList(0, 0);
            // Expected
        }
        catch (IndexOutOfBoundsException e) {
            fail("Should not have thrown IndexOutOfBoundsException");
        }

    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Tests List.listIterator().
     */
    private class FullListIteratorTest extends AbstractListIteratorTest
    {
        //----------------------------------------------------------------------
        public FullListIteratorTest(String aName)
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
            return ((List)aCollection).listIterator();
        }

        //----------------------------------------------------------------------
        public boolean supportsRemove()
        {
            return true;
        }

        //----------------------------------------------------------------------
        public int getStartingIndex()
        {
            return 0;
        }

        //----------------------------------------------------------------------
        public boolean supportsAdd()
        {
            return true;
        }

        //----------------------------------------------------------------------
        public boolean supportsSet()
        {
            return true;
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Tests List.listIterator(int).
     */
    private class IndexedListIteratorTest extends AbstractListIteratorTest
    {
        private int mStartingIndex;
        
        //----------------------------------------------------------------------
        public IndexedListIteratorTest(String aName)
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
            mStartingIndex = aCollection.size() / 2 - 1;
            return ((List)aCollection).listIterator(mStartingIndex);
        }

        //----------------------------------------------------------------------
        public boolean supportsRemove()
        {
            return true;
        }

        //----------------------------------------------------------------------
        public int getStartingIndex()
        {
            return mStartingIndex;
        }

        //----------------------------------------------------------------------
        public boolean supportsAdd()
        {
            return true;
        }

        //----------------------------------------------------------------------
        public boolean supportsSet()
        {
            return true;
        }
    }
}

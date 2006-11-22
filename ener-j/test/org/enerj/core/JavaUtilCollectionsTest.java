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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/JavaUtilCollectionsTest.java,v 1.2 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Tests the java.util collection framework using our tests. This does two things:
 * 1) it performs a test of our tests (a sanity check); 2) it ensures that our
 * understanding of the collections behavior does not change from one JDK release to 
 * the next.
 *
 * @version $Id: JavaUtilCollectionsTest.java,v 1.2 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class JavaUtilCollectionsTest extends TestCase
{

    public JavaUtilCollectionsTest(String aTestName) 
    {
        super(aTestName);
    }
    

    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(JavaUtilCollectionsTest.class);
    }
    

    public static Test suite() 
    {
        TestSuite suite = new TestSuite(JavaUtilCollectionsTest.class);
        
        suite.addTestSuite( JavaUtilCollectionsTest.ArrayListListTest.class );
        suite.addTestSuite( JavaUtilCollectionsTest.LinkedListListTest.class );
        suite.addTestSuite( JavaUtilCollectionsTest.VectorListTest.class );
        suite.addTestSuite( JavaUtilCollectionsTest.StackListTest.class );

        suite.addTestSuite( JavaUtilCollectionsTest.HashSetSetTest.class );
        suite.addTestSuite( JavaUtilCollectionsTest.LinkedHashSetSetTest.class );
        suite.addTestSuite( JavaUtilCollectionsTest.TreeSetSetTest.class );

        suite.addTestSuite( JavaUtilCollectionsTest.HashMapMapTest.class );
        suite.addTestSuite( JavaUtilCollectionsTest.HashtableMapTest.class );
        suite.addTestSuite( JavaUtilCollectionsTest.LinkedHashMapMapTest.class );
        // Don't test IdentityHashMap and WeakHashMap -- special cases of comparison...
        suite.addTestSuite( JavaUtilCollectionsTest.TreeMapMapTest.class );

        return suite;
    }
    

    /*
     * You can have additional methods here..... 
     */
    public void testNothing() throws Exception
    {
        // Placeholder until a specific test is added.
    }


    // Inner classes used to test interfaces and abstract classes.

    

    /**
     * Tests List interface of ArrayList.
     */
    public static final class ArrayListListTest extends AbstractListTest
    {

        public ArrayListListTest(String aName)
        {
            super(aName);
        }


        public Collection createCollection() throws Exception
        {
            return new ArrayList();
        }
    }
    
    

    /**
     * Tests List interface of LinkedList.
     */
    public static final class LinkedListListTest extends AbstractListTest
    {

        public LinkedListListTest(String aName)
        {
            super(aName);
        }


        public Collection createCollection() throws Exception
        {
            return new LinkedList();
        }
    }
    

    /**
     * Tests List interface of Vector.
     */
    public static final class VectorListTest extends AbstractListTest
    {

        public VectorListTest(String aName)
        {
            super(aName);
        }


        public Collection createCollection() throws Exception
        {
            return new Vector();
        }
    }
    

    /**
     * Tests List interface of Stack.
     */
    public static final class StackListTest extends AbstractListTest
    {

        public StackListTest(String aName)
        {
            super(aName);
        }


        public Collection createCollection() throws Exception
        {
            return new Stack();
        }
    }


    /**
     * Tests Set interface of HashSet.
     */
    public static final class HashSetSetTest extends AbstractSetTest
    {

        public HashSetSetTest(String aName)
        {
            super(aName);
        }


        public Collection createCollection() throws Exception
        {
            return new HashSet();
        }
    }
    

    /**
     * Tests Set interface of LinkedHashSet.
     */
    public static final class LinkedHashSetSetTest extends AbstractSetTest
    {

        public LinkedHashSetSetTest(String aName)
        {
            super(aName);
        }


        public Collection createCollection() throws Exception
        {
            return new LinkedHashSet();
        }
    }
    

    /**
     * Tests Set interface of TreeSet.
     */
    public static final class TreeSetSetTest extends AbstractSortedSetTest
    {

        public TreeSetSetTest(String aName)
        {
            super(aName);
        }


        public Collection createCollection() throws Exception
        {
            return new TreeSet();
        }
    }
    

    /**
     * Tests Map interface of HashMap.
     */
    public static final class HashMapMapTest extends AbstractMapTest
    {

        public HashMapMapTest(String aName)
        {
            super(aName);
        }


        public Map createMap() throws Exception
        {
            return new HashMap();
        }


        public boolean allowsNullKeys()
        {
            return true;
        }
    }
    

    /**
     * Tests Map interface of Hashtable.
     */
    public static final class HashtableMapTest extends AbstractMapTest
    {

        public HashtableMapTest(String aName)
        {
            super(aName);
        }


        public Map createMap() throws Exception
        {
            return new Hashtable();
        }


        public boolean allowsNullKeys()
        {
            return false;
        }
    }
    

    /**
     * Tests Map interface of LinkedHashMap.
     */
    public static final class LinkedHashMapMapTest extends AbstractMapTest
    {

        public LinkedHashMapMapTest(String aName)
        {
            super(aName);
        }


        public Map createMap() throws Exception
        {
            return new LinkedHashMap();
        }


        public boolean allowsNullKeys()
        {
            return true;
        }
    }
    

    /**
     * Tests Map interface of TreeMap.
     */
    public static final class TreeMapMapTest extends AbstractSortedMapTest
    {

        public TreeMapMapTest(String aName)
        {
            super(aName);
        }


        public Map createMap() throws Exception
        {
            return new TreeMap();
        }


        public boolean allowsNullKeys()
        {
            return false;
        }
    }
    
}

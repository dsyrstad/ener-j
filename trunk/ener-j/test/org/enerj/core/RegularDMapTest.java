// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/RegularDMapTest.java,v 1.2 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Tests org.enerj.core.RegularDMap.
 *
 * @version $Id: RegularDMapTest.java,v 1.2 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class RegularDMapTest extends TestCase
{
    //----------------------------------------------------------------------
    public RegularDMapTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(RegularDMapTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        TestSuite suite = new TestSuite(RegularDMapTest.class);
        
        suite.addTestSuite( RegularDMapTest.InternalDMapTest.class );
        suite.addTestSuite( RegularDMapTest.InternalQueryableCollectionTest.class );

        return suite;
    }
    
    //----------------------------------------------------------------------
    /*
     * You can have additional methods here..... 
     */
    public void testNothing() throws Exception
    {
        // Placeholder until a specific test is added.
    }

    //----------------------------------------------------------------------
    // Inner classes used to test interfaces and abstract classes.
    //----------------------------------------------------------------------
    
    //----------------------------------------------------------------------
    /**
     * Tests DMap interface of RegularDMap.
     */
    public static final class InternalDMapTest extends AbstractDMapTest
    {
        //----------------------------------------------------------------------
        public InternalDMapTest(String aName)
        {
            super(aName);
        }

        //----------------------------------------------------------------------
        public Map createMap() throws Exception
        {
            return new RegularDMap();
        }

        //----------------------------------------------------------------------
        public boolean allowsNullKeys()
        {
            return true;
        }
    }
    
    
    //----------------------------------------------------------------------
    /**
     * Tests QueryableCollection interface of RegularDMap.
     */
    public static final class InternalQueryableCollectionTest extends AbstractQueryableCollectionTest
    {
        //----------------------------------------------------------------------
        public InternalQueryableCollectionTest(String aName)
        {
            super(aName);
        }

        //----------------------------------------------------------------------
        public QueryableCollection createQueryableCollection() throws Exception
        {
            return new RegularDMap();
        }
    }
}

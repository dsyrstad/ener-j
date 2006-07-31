// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/RegularDArrayTest.java,v 1.2 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Tests org.enerj.core.RegularDArray.
 *
 * @version $Id: RegularDArrayTest.java,v 1.2 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class RegularDArrayTest extends TestCase
{
    //----------------------------------------------------------------------
    public RegularDArrayTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(RegularDArrayTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        TestSuite suite = new TestSuite(RegularDArrayTest.class);
        
        suite.addTestSuite( RegularDArrayTest.InternalDArrayTest.class );
        suite.addTestSuite( RegularDArrayTest.InternalQueryableCollectionTest.class );

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
     * Tests DArray interface of RegularDArray.
     */
    public static final class InternalDArrayTest extends AbstractDArrayTest
    {
        //----------------------------------------------------------------------
        public InternalDArrayTest(String aName)
        {
            super(aName);
        }

        //----------------------------------------------------------------------
        public Collection createCollection() throws Exception
        {
            return new RegularDArray();
        }
    }
    
    
    //----------------------------------------------------------------------
    /**
     * Tests QueryableCollection interface of RegularDArray.
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
            return new RegularDArray();
        }
    }
}

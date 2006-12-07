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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/VeryLargeDArrayTest.java,v 1.3 2006/05/05 13:47:37 dsyrstad Exp $

package org.enerj.core;

import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.odmg.QueryableCollection;

/**
 * Tests org.enerj.core.VeryLargeDArray.
 *
 * @version $Id: VeryLargeDArrayTest.java,v 1.3 2006/05/05 13:47:37 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class LargePersistentArrayTest extends TestCase
{

    public LargePersistentArrayTest(String aTestName) 
    {
        super(aTestName);
    }

    public static Test suite() 
    {
        TestSuite suite = new TestSuite(LargePersistentArrayTest.class);
        
        suite.addTestSuite( LargePersistentArrayTest.InternalDArrayTest.class );
        suite.addTestSuite( LargePersistentArrayTest.InternalQueryableCollectionTest.class );
        suite.addTestSuite( LargePersistentArrayTest.InternalLargeListTest.class );
        return suite;
    }
    

    /*
     * You can have additional methods here..... 
     */
    public void testNothing() throws Exception
    {
        // Placeholder until a specific test is added.
        //  TODO  test large lists to a real database
    }


    // Inner classes used to test interfaces and abstract classes.

    

    /**
     * Tests DArray interface of VeryLargeDArray.
     */
    public static final class InternalDArrayTest extends AbstractDArrayTest
    {

        public InternalDArrayTest(String aName)
        {
            super(aName);
        }


        public Collection createCollection() throws Exception
        {
            // Construct with small node size so multiple branches will be tested.
            return new LargePersistentArrayList(100);  
        }
    }
    

    /**
     * Tests LargeList interface of VeryLargeDArray.
     */
    public static final class InternalLargeListTest extends AbstractLargeListTest
    {

        public InternalLargeListTest(String aName)
        {
            super(aName);
        }


        public LargeCollection createLargeCollection() throws Exception
        {
            // Construct with small node size so multiple branches will be tested.
            return new LargePersistentArrayList(100);  
        }
    }
    
    

    /**
     * Tests QueryableCollection interface of VeryLargeDArray.
     */
    public static final class InternalQueryableCollectionTest extends AbstractQueryableCollectionTest
    {

        public InternalQueryableCollectionTest(String aName)
        {
            super(aName);
        }


        public QueryableCollection createQueryableCollection() throws Exception
        {
            // Construct with small node size so multiple branches will be tested.
            return new LargePersistentArrayList(100);  
        }
    }
}

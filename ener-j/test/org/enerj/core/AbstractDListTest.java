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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractDListTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement org.odmg.DList.
 * Ener-J's org.odmg.QueryableCollection must be tested separately.
 *
 * Test methods are final so that the contract of List is guaranteed to be tested.
 *
 * @version $Id: AbstractDListTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractDListTest extends AbstractListTest
{
    //----------------------------------------------------------------------
    public AbstractDListTest(String aName) 
    {
        super(aName);
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests concat(DList).
     */
    public final void testConcat() throws Exception
    {
        DList testDList = (DList)createCollection();
        
        testDList.add( new CollectionTestObject("Obj1") );
        testDList.add( new CollectionTestObject("Obj2") );
        testDList.add( new CollectionTestObject("Obj3") );
        
        DList dlist2 = (DList)createCollection();
        dlist2.add( new CollectionTestObject("Obj4") );
        dlist2.add( new CollectionTestObject("Obj5") );
        dlist2.add( new CollectionTestObject("Obj7") );
        dlist2.add( new CollectionTestObject("Obj6") );
        
        DList dlist3 = testDList.concat(dlist2);

        assertTrue("Size should be correct", dlist3.size() == 7);
        for (int i = 1; i <= 7; i++) {
            assertTrue("List should contain object " + i, dlist3.contains( new CollectionTestObject("Obj" + i) ) );
        }

        assertTrue("First DList should be unaffected", testDList.size() == 3);
        assertTrue("Second DList should be unaffected", dlist2.size() == 4);

        // test first list empty
        testDList.clear();
        dlist3 = testDList.concat(dlist2);

        assertTrue("Size should be correct", dlist3.size() == 4);
        for (int i = 4; i <= 7; i++) {
            assertTrue("List should contain object " + i, dlist3.contains( new CollectionTestObject("Obj" + i) ) );
        }
        
        // test second list empty
        dlist3 = dlist2.concat(testDList);

        assertTrue("Size should be correct", dlist3.size() == 4);
        for (int i = 4; i <= 7; i++) {
            assertTrue("List should contain object " + i, dlist3.contains( new CollectionTestObject("Obj" + i) ) );
        }
        
        // test both lists empty
        testDList.clear();
        dlist2.clear();
        dlist3 = testDList.concat(dlist2);

        assertTrue("Size should be correct", dlist3.size() == 0);
    }

}

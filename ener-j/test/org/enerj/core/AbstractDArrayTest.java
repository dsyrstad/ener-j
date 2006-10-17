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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractDArrayTest.java,v 1.2 2006/05/05 13:47:37 dsyrstad Exp $

package org.enerj.core;

import org.odmg.DArray;

/**
 * Generically Tests classes that implement org.odmg.DArray.
 * Ener-J's org.odmg.QueryableCollection must be tested separately.
 *
 * Test methods are final so that the contract of List is guaranteed to be tested.
 *
 * @version $Id: AbstractDArrayTest.java,v 1.2 2006/05/05 13:47:37 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractDArrayTest extends AbstractListTest
{
    //----------------------------------------------------------------------
    public AbstractDArrayTest(String aName) 
    {
        super(aName);
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests resize(int).
     */
    public final void testResize() throws Exception
    {
        DArray testArray = (DArray)createCollection();
        
        testArray.add( new CollectionTestObject("Obj1") );
        testArray.add( new CollectionTestObject("Obj2") );
        testArray.add( new CollectionTestObject("Obj3") );
        
        testArray.resize(4);
        assertTrue("Size should be correct", testArray.size() == 4);
        assertNull("Last element should be null", testArray.get(3) );
        
        // Resizing smaller is not allowed, according to ODMG
        try {
            testArray.resize(3);
            fail("Should have thrown Exception");
        }
        catch (Exception e) {
            // Expected
        }
    }

}

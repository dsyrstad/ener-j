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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractLargeCollectionTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement java.util.LargeCollection.
 * We don't test equals() because a strict LargeCollection requires the exact same instance
 * for the LargeCollection to be equal (i.e., the Object.equals semantics). We let
 * the List and Set tests (which have different semantics) test this. The same applies
 * to hashCode().<p>
 *
 * Test methods are final so that the contract of LargeCollection is guaranteed to be tested.
 *
 * @version $Id: AbstractLargeCollectionTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractLargeCollectionTest extends TestCase
{

    public AbstractLargeCollectionTest(String aName) 
    {
        super(aName);
    }
    

    /**
     * Creates an instance implementing LargeCollection. Subclass implements this
     * to create a specific type of LargeCollection.
     */
    abstract public LargeCollection createLargeCollection() throws Exception;
    

    /**
     * Tests sizeAsLong().
     */
    public final void testSizeAsLong() throws Exception
    {
        LargeCollection testLargeCollection = createLargeCollection();

        final int listSize = 10;
        for (int i = 0; i < listSize; i++) {
            testLargeCollection.add( new CollectionTestObject("Obj-" + i) );
        }
        
        assertTrue("Should have the correct number of elements", testLargeCollection.sizeAsLong() == (long)listSize);
    }


    /**
     * Tests getModificationCount().
     */
    public final void testGetModificationCount() throws Exception
    {
        LargeCollection testLargeCollection = createLargeCollection();
        
        long startingModCount = testLargeCollection.getModificationCount();
        final int listSize = 10;
        for (int i = 0; i < listSize; i++) {
            testLargeCollection.add( new CollectionTestObject("Obj-" + i) );
        }
        
        
        long endingModCount = testLargeCollection.getModificationCount();
        assertTrue("Mod count should have changed", endingModCount != startingModCount);
        
        testLargeCollection.contains( new CollectionTestObject("Obj-1") );
        assertTrue("Mod count should not have changed", testLargeCollection.getModificationCount() == endingModCount);
    }

}

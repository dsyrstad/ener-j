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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractSetTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement java.util.Set.
 *
 * Test methods are final so that the contract of Set is guaranteed to be tested.
 *
 * @version $Id: AbstractSetTest.java,v 1.1 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractSetTest extends AbstractCollectionTest
{

    public AbstractSetTest(String aName) 
    {
        super(aName);
    }
    

    /**
     * Tests equals(Object).
     */
    public final void testEquals() throws Exception
    {
        Set testSet = (Set)createCollection();
        
        testSet.add( new CollectionTestObject("Obj1") );
        testSet.add( new CollectionTestObject("Obj2") );
        testSet.add( new CollectionTestObject("Obj3") );
        
        HashSet set2 = new HashSet(3);
        set2.add( new CollectionTestObject("Obj2") );
        set2.add( new CollectionTestObject("Obj1") );
        set2.add( new CollectionTestObject("Obj3") );
        
        assertTrue("Sets should be equal", testSet.equals(set2) );
        
        set2.add( new CollectionTestObject("Obj0") );
        assertTrue("Sets should not be equal", !testSet.equals(set2) );
    }
    

    /**
     * Tests hashCode().
     */
    public final void testHashcode() throws Exception
    {
        Set testSet = (Set)createCollection();
        
        final int listSize = 150;
        for (int i = 0; i < listSize; i++) {
            testSet.add( new CollectionTestObject("Obj" + i) );
        }

        // This calculation comes from the Set interface javadoc.
        int hashCode = 0;
        Iterator i = testSet.iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            hashCode += (obj==null ? 0 : obj.hashCode());
        }

        assertTrue("Hashcode should be correct", testSet.hashCode() == hashCode);
    }

}

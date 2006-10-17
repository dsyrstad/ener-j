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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/AbstractQueryableCollectionTest.java,v 1.2 2006/05/05 13:47:37 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.lang.reflect.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generically Tests classes that implement org.odmg.QueryableCollection.
 *
 * Test methods are final so that the contract of QueryableCollection is guaranteed to be tested.
 *
 * @version $Id: AbstractQueryableCollectionTest.java,v 1.2 2006/05/05 13:47:37 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public abstract class AbstractQueryableCollectionTest extends TestCase
{
    //----------------------------------------------------------------------
    public AbstractQueryableCollectionTest(String aName) 
    {
        super(aName);
    }
    
    //----------------------------------------------------------------------
    /**
     * Creates an instance implementing QueryableCollection. Subclass implements this
     * to create a specific type of QueryableCollection.
     */
    abstract public QueryableCollection createQueryableCollection() throws Exception;
    
    //----------------------------------------------------------------------
    /**
     * Tests selectElement(String predicate).
     */
    public final void testSelectElement() throws Exception
    {
        //  TODO  after OQL parser done.
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests select(String predicate).
     */
    public final void testSelect() throws Exception
    {
        //  TODO  after OQL parser done.
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests query(String predicate).
     */
    public final void testQuery() throws Exception
    {
        //  TODO  after OQL parser done.
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests existsElement(String predicate).
     */
    public final void testExistsElement() throws Exception
    {
        //  TODO  after OQL parser done.
    }

}

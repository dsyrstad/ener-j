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

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
    //----------------------------------------------------------------------
    public AbstractSetTest(String aName) 
    {
        super(aName);
    }
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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

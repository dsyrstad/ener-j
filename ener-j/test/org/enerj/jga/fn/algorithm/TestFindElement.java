// ============================================================================
// $Id: TestFindElement.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
// Copyright (c) 2002  David A. Hall
// ============================================================================
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.fn.algorithm;

import java.util.Vector;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.Visitor;
import org.enerj.jga.fn.comparison.EqualEqual;
import org.enerj.jga.fn.logical.LogicalAnd;
import org.enerj.jga.fn.string.Match;
import org.enerj.jga.util.FindIterator;


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

public class TestFindElement extends FunctorTest<FindElement<String>> {
    
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";

    static public BinaryFunctor<String,String,Boolean> bothStartWith_b =
        new LogicalAnd().distribute(new Match("_b.*"), new Match("_b.*"));

    public TestFindElement (String name){ super(name); }
    
    Vector<String> list1 = new Vector<String>();
    Vector<String> list2 = new Vector<String>();

    public void setUp() {
        list1.add(FOO);
        list1.add(BAR);
        list1.add(BAZ);
        list1.add(FOO);
        list1.add(BAR);
        list1.add(BAZ);

        list2.add(BAZ);
        list2.add(QLX);
    }

    public void tearDown() {
    }

    /**
     * Ensures that searching an empty iteration doesn't fail due to an error
     */
    public void testFindElementEmptyList() {
        FindElement<String> lmof
            = new FindElement<String>(new Vector<String>());
        assertFalse(lmof.fn(list1.iterator()).hasNext());

        lmof = new FindElement<String>(list2);
        assertFalse(lmof.fn(new Vector<String>().iterator()).hasNext());
    }
        
    /**
     * Ensures that the proper instances are found when using the default
     * constructor to build the functor
     */
    public void testFindElementListContains() {
        FindElement<String> lmof = new FindElement<String>(list2);
        FindIterator<? extends String> finder = lmof.fn(list1.iterator());
        assertEquals(BAZ, finder.next());
        assertEquals(FOO, finder.next());
        
        finder = lmof.fn(finder);
        assertEquals(BAZ, finder.next());
        assertFalse(finder.hasNext());        
    }

    /**
     * Ensures that the proper instances are found when using the functor
     * constructor to build the functor.  Also covers Serialization.
     */
    public void testFindElementListFunctor() {
        FindElement<String> lmof = 
            makeSerial(new FindElement<String>(list2, bothStartWith_b));
        
        FindIterator<? extends String> finder = lmof.fn(list1.iterator());
        assertEquals(BAR, finder.next());
        assertEquals(BAZ, finder.next());
        
        finder = lmof.fn(finder);
        assertEquals(BAR, finder.next());
        assertEquals(BAZ, finder.next());
        assertFalse(finder.hasNext());        
    }

    /**
     * Ensures no false positives
     */
    public void testFindElementNotFound() {
        Vector<String> v = new Vector<String>();
        v.add(QLX);
        
        FindElement<String> lmof = new FindElement<String>(v);
        assertFalse(lmof.fn(list1.iterator()).hasNext());
    }
        
    public void testVisitableInterface() {
        FindElement<String> lmof = new FindElement<String>(list1);
        TestVisitor tv = new TestVisitor();
        lmof.accept(tv);
        assertEquals(lmof, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements FindElement.Visitor
    {
        public Object host;
        public void visit(FindElement host) { this.host = host; }
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestFindElement.class);
    }
}

// ============================================================================
// $Id: TestElementOf.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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


/**
 * Exercises ElementOf
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestElementOf extends FunctorTest<ElementOf<String>> {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";

    static public BinaryFunctor<String,String,Boolean> bothStartWith_b =
        new LogicalAnd().distribute(new Match("_b.*"), new Match("_b.*"));

    public TestElementOf (String name){ super(name); }

    Vector<String> list = new Vector<String>();

    public void setUp() {
        list.add(FOO);
        list.add(BAR);
        list.add(FOO);
        list.add(BAR);
    }

    public void tearDown() {
    }

    /**
     * Ensures that searching an empty iteration doesn't fail due to an error
     */
    public void testElementOfEmptyList() {
        ElementOf<String> lmof = new ElementOf<String>();
        assertFalse(lmof.p(BAR, new Vector<String>()));
    }
        
    /**
     * Ensures that the first instance is found by walking the remainder of the
     * list.
     */
    public void testElementOfListContains() {
        ElementOf<String> lmof = new ElementOf<String>();
        assertTrue(lmof.p(BAR, list));
        assertFalse(lmof.p(QLX, list));
    }

    /**
     * Ensures that the first instance is found by walking the remainder of the
     * list.
     */
    public void testElementOfListFunctor() {
        ElementOf<String> lmof = new ElementOf<String>(bothStartWith_b);
        assertTrue(lmof.p(BAR, list));
        assertTrue(lmof.p(BAZ, list));
        assertFalse(lmof.p(QLX, list));
    }

    public void testSerialization() {
        ElementOf<String> lmof =
            makeSerial(new ElementOf<String>(bothStartWith_b));
        assertTrue(lmof.p(BAR, list));
        assertTrue(lmof.p(BAZ, list));
        assertFalse(lmof.p(QLX, list));
    }

    public void testVisitableInterface() {
        ElementOf<String> lmof = new ElementOf<String>();
        TestVisitor tv = new TestVisitor();
        lmof.accept(tv);
        assertEquals(lmof, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ElementOf.Visitor
    {
        public Object host;
        public void visit(ElementOf host) { this.host = host; }
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestElementOf.class);
    }
}

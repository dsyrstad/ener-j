// ============================================================================
// $Id: TestRemoveAll.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.Iterator;
import java.util.Vector;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.Conditional;
import org.enerj.jga.fn.adaptor.Constant;
import org.enerj.jga.fn.comparison.EqualEqual;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.fn.string.Match;
import org.enerj.jga.util.EmptyIterator;

/**
 * Exercises RemoveAll
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestRemoveAll extends FunctorTest<RemoveAll<String>> {
    public TestRemoveAll (String name){ super(name); }

    static private final String FOO = "_foo_";
    static private final String BAR = "_bar_";
    static private final String BAZ = "_baz_";
    static private final String QLX = "_qlx_";
    
    static private final Integer NEG = new Integer(-1);
    static private final Integer ZERO = new Integer(0);
    static private final Integer POS = new Integer(1);

    Vector<String> v = new Vector<String>();
    
    public void setUp() {
        v.add(FOO);
        v.add(BAR);
        v.add(FOO);
        v.add(BAZ);
        v.add(FOO);
    }

    public void tearDown() {
    }
    
    public void testRemoveAllEmptyList() {
        UnaryFunctor<String,Boolean> eqFoo = new EqualTo<String>().bind2nd(FOO);
        RemoveAll<String> xf = new RemoveAll<String>(eqFoo);
        Iterator<String> iter = xf.fn(new EmptyIterator<String>());
        assertFalse(iter.hasNext());
    }
     
    public void testRemoveAllValue() {
        RemoveAll<String> xf1 = new RemoveAll<String>(FOO);
        Iterator<String> iter1 = xf1.fn(v.iterator());
        assertEquals(BAR, iter1.next());
        assertEquals(BAZ, iter1.next());
        assertFalse(iter1.hasNext());
    }

    public void testRemoveAllEq() {
        RemoveAll<String> xf2 =
            new RemoveAll<String>(new EqualEqual<String>(), FOO);
        Iterator<String> iter2 = xf2.fn(v.iterator());
        assertEquals(BAR, iter2.next());
        assertEquals(BAZ, iter2.next());
        assertFalse(iter2.hasNext());
    }
    
    public void testRemoveAllFn() {
        RemoveAll<String> xf3 =
            makeSerial(new RemoveAll<String>(new Match("_b.*")));
        
        Iterator<String> iter3 = xf3.fn(v.iterator());
        assertEquals(FOO, iter3.next());
        assertEquals(FOO, iter3.next());
        assertEquals(FOO, iter3.next());
        assertFalse(iter3.hasNext());
    }

    public void testVisitableInterface() {
        UnaryFunctor<String,Boolean> eqFoo = new EqualTo<String>().bind2nd(FOO);
        RemoveAll<String> xf = new RemoveAll<String>(eqFoo);

        TestVisitor tv = new TestVisitor();
        xf.accept(tv);
        assertEquals(xf, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements RemoveAll.Visitor
    {
        public Object host;
        public void visit(RemoveAll host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestRemoveAll.class);
    }
}

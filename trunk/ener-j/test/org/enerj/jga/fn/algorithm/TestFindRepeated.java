// ============================================================================
// $Id: TestFindRepeated.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.Visitor;
import org.enerj.jga.fn.comparison.EqualEqual;
import org.enerj.jga.fn.string.Match;

/**
 * Exercises FindRepeated
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestFindRepeated extends FunctorTest<FindRepeated<String>> {
    
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    
    static public UnaryFunctor<String,Boolean> startsWith_b = new Match("_b.*");
    public TestFindRepeated (String name){ super(name); }

    Vector<String> list = new Vector<String>();

    public void setUp() {
        list.add(FOO);
        list.add(BAR);
        list.add(BAZ);
        list.add(BAZ);
        list.add(BAZ);
        list.add(FOO);
        list.add(FOO);
        list.add(BAR);
        list.add(BAZ);
    }

    public void tearDown() {
    }

    /**
     * Ensures that searching an empty iteration doesn't fail due to an error
     */
    public void testFindEmptyList() {
        FindRepeated<String> finder = new FindRepeated<String>(1,BAR);
        Iterator<? extends String> iter =
            finder.fn(new Vector<String>().iterator());
        assertFalse(iter.hasNext());
    }
     
    /**
     * Ensures that searching an empty iteration for a run of length 0 returns
     * the appropriate value
     */
    public void testFindRunOfZeroInEmptyList() {
        FindRepeated<String> finder = new FindRepeated<String>(0,BAR);
        Iterator<? extends String> iter =
            finder.fn(new Vector<String>().iterator());
        assertFalse(iter.hasNext());
    }
     
    /**
     * Ensures that searching an iteration with no adjacent duplicates does not
     * turn up a false positive
     */
    public void testFindNoneAdj() {
        FindRepeated<String> finder = new FindRepeated<String>(2,BAR);
        Iterator<? extends String> iter = finder.fn(list.iterator());
        assertFalse(iter.hasNext());
    }

    /**
     * Ensures that searching an iteration for a run of length 0 returns the
     * appropriate value (first element in the input iteration)
     */
    public void testFindRunOfZero() {
        FindRepeated<String> finder = new FindRepeated<String>(0,QLX);
        Iterator<? extends String> iter = finder.fn(list.iterator());
        assertTrue(iter.hasNext());
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(FOO, iter.next());

        iter = finder.fn(iter);
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertFalse(iter.hasNext());
    }

    /**
     * Ensures that searching an iteration for a run of length 1 returns the
     * appropriate value
     */
    public void testFindRunOfOne() {
        FindRepeated<String> finder = new FindRepeated<String>(1,BAR);
        Iterator<? extends String> iter = finder.fn(list.iterator());
        assertTrue(iter.hasNext());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());

        iter = finder.fn(iter);
        assertEquals(BAR, iter.next());
        
        iter = finder.fn(iter);
        assertFalse(iter.hasNext());
    }

    /**
     * Ensures that searching an iteration for a run of length >1 returns the
     * appropriate value
     */
    public void testFindRunOfMany() {
        FindRepeated<String> finder = new FindRepeated<String>(3,BAZ);
        Iterator<? extends String> iter = finder.fn(list.iterator());
        assertTrue(iter.hasNext());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(FOO, iter.next());

        iter = finder.fn(iter);
        assertFalse(iter.hasNext());
    }

    /**
     * Ensures that searching an iteration with a pair of adjacent elements that
     * meet the given predicate are detected and positioned correctly
     */
    public void testFindWithFn() {
        FindRepeated<String> finder =
            makeSerial(new FindRepeated<String>(4,startsWith_b));
        
        Iterator<? extends String> iter = finder.fn(list.iterator());
        assertTrue(iter.hasNext());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());
        
        iter = finder.fn(iter);
        assertFalse(iter.hasNext());
    }
    
    public void testVisitableInterface() {
        FindRepeated<String> finder = new FindRepeated<String>(3,BAZ);
        TestVisitor tv = new TestVisitor();
        finder.accept(tv);
        assertEquals(finder, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements FindRepeated.Visitor
    {
        public Object host;
        public void visit(FindRepeated host) { this.host = host; }
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestFindRepeated.class);
    }
}

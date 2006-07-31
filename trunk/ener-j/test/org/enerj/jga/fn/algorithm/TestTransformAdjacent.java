// ============================================================================
// $Id: TestTransformAdjacent.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.NoSuchElementException;
import java.util.Vector;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.comparison.Min;
import org.enerj.jga.util.EmptyIterator;
import org.enerj.jga.util.SingletonIterator;
import org.enerj.jga.util.TransformAdjacentIterator;


/**
 * Exercises TransformAdjacent
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestTransformAdjacent
        extends FunctorTest<TransformAdjacent<String,String>>
{
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    
    public TestTransformAdjacent (String name){ super(name); }

    BinaryFunctor<String,String,String> fn = new Min.Comparable<String>();
    TransformAdjacent<String,String> xform =
        makeSerial(new TransformAdjacent<String,String>(fn));
    
    static public Vector<String> v1 = new Vector<String>();

    static {
        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAZ);
        v1.add(QLX);
    }

    public void testTransformAdjacentFn() {
        TransformAdjacentIterator<String,String> iter = xform.fn(v1.iterator());
        
        assertTrue(iter.hasNext()); assertEquals(BAR, iter.next());
        assertTrue(iter.hasNext()); assertEquals(BAR, iter.next());
        assertTrue(iter.hasNext()); assertEquals(BAZ, iter.next());
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException when off the end");
        }
        catch(NoSuchElementException x) {
            // success -- this exception is required
        }
    }

    public void testIteratorAbuse() {
        TransformAdjacentIterator<String,String> iter = xform.fn(v1.iterator());
        
        assertTrue(iter.hasNext());
        assertEquals(BAR, iter.next());
        
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(BAR, iter.next());
        
        assertEquals(BAZ, iter.next());
        
        assertFalse(iter.hasNext());
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException when off the end");
        }
        catch(NoSuchElementException x) {
            // success -- this exception is required
        }
    }

    public void testEmpty() {
        EmptyIterator<String> mt = new EmptyIterator<String>();
        TransformAdjacentIterator<String,String> empty = xform.fn(mt);
        assertFalse(empty.hasNext());
    }

    public void testSingle() {
        SingletonIterator<String> s1 = new SingletonIterator<String>(FOO);
        TransformAdjacentIterator<String,String> single = xform.fn(s1);
        assertFalse(single.hasNext());
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        xform.accept(tv);
        assertEquals(xform, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements TransformAdjacent.Visitor
    {
        public Object host;
        public void visit(TransformAdjacent host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestTransformAdjacent.class);
    }
}

// ============================================================================
// $Id: TestMerge.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.util.ComparableComparator;
import org.enerj.jga.util.EmptyIterator;


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

public class TestMerge extends FunctorTest<Merge<String>> {
    
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";

    public TestMerge (String name){ super(name); }

    static public Vector<String> v1 = new Vector<String>();
    static public Vector<String> v2 = new Vector<String>();

    static {
        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAZ);
        v1.add(QLX);

        v2.add(BAR);
        v2.add(BAZ);
        v2.add(FOO);
        v2.add(QLX);
    }

    static Comparator<String> comp = new ComparableComparator<String>();

    public void testDefaultMerge() {
        Merge<String> merger = makeSerial(new Merge<String>(comp));
        Iterator<String> iter = merger.fn(v1.iterator(), v2.iterator());
        
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(QLX, iter.next());
        assertEquals(QLX, iter.next());
        assertFalse(iter.hasNext());
    }

    public void testDefaultMergeReversed() {
        Iterator<String> iter =
            new Merge<String>(comp).fn(v2.iterator(), v1.iterator());
        
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(QLX, iter.next());
        assertEquals(QLX, iter.next());
        assertFalse(iter.hasNext());
    }

    public void testEmpty() {
        EmptyIterator<String> empty = new EmptyIterator<String>();
        Iterator<String> iter = new Merge<String>(comp).fn(empty,empty);
        assertFalse(iter.hasNext());
    }

    // make sure it works with one non-empty iterator
    public void testHalfEmpty() {
        EmptyIterator<String> empty = new EmptyIterator<String>();
        Iterator<String> iter =
            new Merge<String>(comp).fn(empty,v1.iterator());
        
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(QLX, iter.next());
        assertFalse(iter.hasNext());

        iter = new Merge<String>(comp).fn(v2.iterator(),empty);
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(QLX, iter.next());
        assertFalse(iter.hasNext());
    }
    
    public void testVisitableInterface() {
        Merge<String> counter = new Merge<String>(comp);
        TestVisitor tv = new TestVisitor();
        counter.accept(tv);
        assertEquals(counter, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Merge.Visitor
    {
        public Object host;
        public void visit(Merge host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestMerge.class);
    }
}

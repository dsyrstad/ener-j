// ============================================================================
// $Id: TestFindSequence.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.Visitor;
import org.enerj.jga.fn.comparison.EqualEqual;
import org.enerj.jga.fn.logical.LogicalAnd;
import org.enerj.jga.fn.string.Match;

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

public class TestFindSequence extends FunctorTest<FindSequence<String>> {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";

    static public BinaryFunctor<String,String,Boolean> bothStartWith_b =
        new LogicalAnd().distribute(new Match("_b.*"), new Match("_b.*"));

    public TestFindSequence (String name){ super(name); }

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
    public void testFindSequenceInEmptyList() {
        FindSequence<String> finder = new FindSequence<String>(list);
        Iterator<? extends String> iter =
            finder.fn(new Vector<String>().iterator());
        assertFalse(iter.hasNext());
    }
     
    /**
     * Ensures that searching an empty iteration for a run of length 0 returns
     * the appropriate value
     */
    public void testFindEmptySequenceInEmptyList() {
        Vector<String> sequence = new Vector<String>();
        FindSequence<String> finder = new FindSequence<String>(sequence);
        Iterator<? extends String> iter =
            finder.fn(new Vector<String>().iterator());
        assertFalse(iter.hasNext());
    }
     
    /**
     * Ensures that searching for a sequence that appears at the beginning of the
     * iteration succeeds appropriately
     */
    public void testFindLeadingSequence() {
        Vector<String> sequence = new Vector<String>();
        sequence.add(FOO);
        sequence.add(BAR);
        sequence.add(BAZ);
        
        FindSequence<String> finder = new FindSequence<String>(sequence);
        Iterator<? extends String> iter = finder.fn(list.iterator());
        assertTrue(iter.hasNext());
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());

        iter = finder.fn(iter);
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertFalse(iter.hasNext());
    }

    /**
     * Ensures that searching for a sequence that appears at the end of the
     * iteration succeeds appropriately
     */
    public void testFindTrailingSequence() {
        Vector<String> sequence = new Vector<String>();
        sequence.add(FOO);
        sequence.add(FOO);
        sequence.add(BAR);
        sequence.add(BAZ);
            
        FindSequence<String> finder = new FindSequence<String>(sequence);
        Iterator<? extends String> iter = finder.fn(list.iterator());
        assertTrue(iter.hasNext());
        assertEquals(FOO, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertFalse(iter.hasNext());
    }

    /**
     * Ensures that searching for a sequence that appears at the end of the
     * iteration succeeds appropriately
     */
    public void testFindSequence() {
        Vector<String> sequence = new Vector<String>();
        sequence.add(BAZ);
        sequence.add(FOO);
        sequence.add(FOO);
        sequence.add(BAR);
            
        FindSequence<String> finder = new FindSequence<String>(sequence);
        Iterator<? extends String> iter = finder.fn(list.iterator());
        assertTrue(iter.hasNext());
        assertEquals(BAZ, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertFalse(iter.hasNext());
    }

    /**
     * Ensures that searching an iteration with a pair of adjacent elements that
     * meet the given predicate are detected and positioned correctly
     */
    public void testFindWithFn() {
        Vector<String> sequence = new Vector<String>();
        sequence.add(BAZ);
        sequence.add(BAR);
        sequence.add(BAR);
        sequence.add(BAR);
            
        FindSequence<String> finder = 
            makeSerial(new FindSequence<String>(sequence,bothStartWith_b));
        
        Iterator<? extends String> iter = finder.fn(list.iterator());
        assertTrue(iter.hasNext());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(FOO, iter.next());
        
        iter = finder.fn(iter);
        assertFalse(iter.hasNext());
    }
    
    /**
     * Ensures no false positives
     */
    public void testFindNonExistentSequence() {
        Vector<String> sequence = new Vector<String>();
        sequence.add(FOO);
        sequence.add(QLX);
        sequence.add(BAR);
            
        FindSequence<String> finder = new FindSequence<String>(sequence);
        Iterator<? extends String> iter = finder.fn(list.iterator());
        assertFalse(iter.hasNext());
    }

    /**
     * Demonstrates finding multiple sequences
     */
    public void testFindMultipleSequences() {
        Vector<String> v1 = new Vector<String>();
        v1.add(FOO); v1.add(BAR); v1.add(FOO); v1.add(BAR); v1.add(BAR);
        v1.add(FOO); v1.add(BAR); v1.add(FOO); v1.add(BAR); v1.add(BAZ);
        v1.add(FOO); v1.add(BAR); v1.add(FOO);

        Vector<String> seq = new Vector<String>();
        seq.add(BAR); seq.add(FOO);

        FindSequence<String> finder = new FindSequence<String>(seq);

        int count = 0;
        for (Iterator<? extends String> iter = finder.fn(v1.iterator());
             iter.hasNext(); iter = finder.fn(iter))
        {
            ++count;
            assertEquals(BAR, iter.next());
            assertEquals(FOO, iter.next());
        }

        assertEquals(4, count);
    }

    public void testVisitableInterface() {
        FindSequence<String> finder = new FindSequence<String>(list);
        TestVisitor tv = new TestVisitor();
        finder.accept(tv);
        assertEquals(finder, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements FindSequence.Visitor
    {
        public Object host;
        public void visit(FindSequence host) { this.host = host; }
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestFindSequence.class);
    }
}

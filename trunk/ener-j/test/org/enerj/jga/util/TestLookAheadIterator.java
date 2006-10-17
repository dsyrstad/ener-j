// ============================================================================
// $Id: TestLookAheadIterator.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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

package org.enerj.jga.util;

import java.util.NoSuchElementException;
import java.util.Vector;
import junit.framework.TestCase;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.Visitor;
import org.enerj.jga.util.LookAheadIterator;


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

public class TestLookAheadIterator extends TestCase {
    public TestLookAheadIterator (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    
    Vector<String> list = new Vector<String>();

    public void setUp() {
        list.add(FOO);
        list.add(BAR);
        list.add(BAZ);
        list.add(QLX);
    }

    public void tearDown() {
    }
        
    public void testOnePass() {
        LookAheadIterator<String> iter =
            new LookAheadIterator<String>(list.iterator());
        
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(QLX, iter.next());
        assertFalse(iter.hasNext());
    }

    public void testLookAhead1() {
        LookAheadIterator<String> iter =
            new LookAheadIterator<String>(list.iterator(), 1);

        assertTrue(iter.hasNext());
        assertTrue(iter.hasNextPlus(1));
        assertEquals(FOO, iter.peek(1));
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.peek(1));
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.peek(1));
        assertEquals(BAZ, iter.next());
        assertEquals(QLX, iter.peek(1));
        assertEquals(QLX, iter.next());
        assertFalse(iter.hasNext());
        assertFalse(iter.hasNextPlus(1));

        try {
            iter.next();
            fail("Expected NoSuchElementException when next is called when "
                 +"iterator off the end");
        }
        catch (NoSuchElementException x) {}

        iter = new LookAheadIterator<String>(list.iterator(), 1);
        try {
            iter.peek(2);
            fail("Expected IllegalArgumentException when peek arg "
                 +"exceeds constructed size");
        }
        catch (IllegalArgumentException x) {}
    }

    public void testLookAheadN() {
        LookAheadIterator<String> iter =
            new LookAheadIterator<String>(list.iterator(), 3);

        assertTrue(iter.hasNext());
        assertTrue(iter.hasNextPlus(1));
        assertTrue(iter.hasNextPlus(2));
        assertTrue(iter.hasNextPlus(3));
        assertEquals(FOO, iter.peek(1));
        assertEquals(BAR, iter.peek(2));
        assertEquals(BAZ, iter.peek(3));
        assertEquals(FOO, iter.next());
        assertTrue(iter.hasNextPlus(3));
        assertEquals(BAR, iter.peek(1));
        assertEquals(BAZ, iter.peek(2));
        assertEquals(QLX, iter.peek(3));
        assertEquals(BAR, iter.next());
        assertFalse(iter.hasNextPlus(3));
        assertEquals(BAZ, iter.next());
        assertEquals(QLX, iter.next());
        assertFalse(iter.hasNext());
        assertFalse(iter.hasNextPlus(1));
    }

    public void testLookAheadMoreThanSize() {
        LookAheadIterator<String> iter =
            new LookAheadIterator<String>(list.iterator(), 5);

        assertTrue(iter.hasNext());
        assertTrue(iter.hasNextPlus(1));
        assertTrue(iter.hasNextPlus(2));
        assertTrue(iter.hasNextPlus(3));
        assertTrue(iter.hasNextPlus(4));
        assertFalse(iter.hasNextPlus(5));
        assertEquals(FOO, iter.peek(1));
        assertEquals(BAR, iter.peek(2));
        assertEquals(BAZ, iter.peek(3));
        assertEquals(QLX, iter.peek(4));
        assertEquals(FOO, iter.next());
        assertTrue(iter.hasNextPlus(3));
        assertEquals(BAR, iter.peek(1));
        assertEquals(BAZ, iter.peek(2));
        assertEquals(QLX, iter.peek(3));
        assertFalse(iter.hasNextPlus(4));
        assertEquals(BAR, iter.next());
        assertFalse(iter.hasNextPlus(3));
        assertEquals(BAZ, iter.next());
        assertEquals(QLX, iter.next());
        assertFalse(iter.hasNext());
        assertFalse(iter.hasNextPlus(1));
    }

static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestLookAheadIterator.class);
    }
}

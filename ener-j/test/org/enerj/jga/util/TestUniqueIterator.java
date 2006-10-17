// ============================================================================
// $Id: TestUniqueIterator.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import org.enerj.jga.SampleObject;
import org.enerj.jga.SampleObjectComparator;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.Visitor;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.fn.comparison.Equality;
import org.enerj.jga.util.UniqueIterator;


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

public class TestUniqueIterator extends TestCase {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    
    public TestUniqueIterator (String name){ super(name); }

    public void testDefaultUnique() {
        Vector<String> v = new Vector<String>();

        v.add(FOO);
        v.add(BAR);
        v.add(BAR);
        v.add(BAZ);
        v.add(FOO);
        v.add(QLX);
        v.add(QLX);

        UniqueIterator<String> iter = new UniqueIterator<String>(v.iterator());
        assertTrue(iter.hasNext()); assertEquals(FOO, iter.next());
        assertTrue(iter.hasNext()); assertEquals(BAR, iter.next());
        assertTrue(iter.hasNext()); assertEquals(BAZ, iter.next());
        assertTrue(iter.hasNext()); assertEquals(FOO, iter.next());
        assertTrue(iter.hasNext()); assertEquals(QLX, iter.next());
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException when off the end");
        }
        catch(NoSuchElementException x) {
            // success -- this exception is required
        }
    }

    public void testUniqueFn() {
        SampleObjectComparator soComp = new SampleObjectComparator();
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v = new Vector<SampleObject>();
        SampleObject widgets = new SampleObject("widgets",42);
        SampleObject mumbles = new SampleObject("mumbles",65);
        SampleObject gizmos  = new SampleObject("gizmos",0);
        v.add(widgets);
        v.add(mumbles);
        v.add(mumbles);
        v.add(widgets);
        v.add(gizmos);

        UniqueIterator<SampleObject> iter =
            new UniqueIterator<SampleObject>(v.iterator(),eq);
        assertTrue(iter.hasNext()); assertEquals(widgets,iter.next());
        assertTrue(iter.hasNext()); assertEquals(mumbles,iter.next());
        assertTrue(iter.hasNext()); assertEquals(widgets,iter.next());
        assertTrue(iter.hasNext()); assertEquals(gizmos, iter.next());
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
        UniqueIterator<String> iter =
            new UniqueIterator<String>(new EmptyIterator<String>());

        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException when empty");
        }
        catch(NoSuchElementException x) {
            // success -- this exception is required
        }

        // make sure it works w/o call to hasNext()
        iter = new UniqueIterator<String>(new EmptyIterator<String>());
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException when empty");
        }
        catch(NoSuchElementException x) {
            // success -- this exception is required
        }
    }
    
    public void testMisuse() {
        Vector<String> v = new Vector<String>();
        v.add(FOO);
        v.add(BAR);
        v.add(BAR);
        v.add(BAZ);
        v.add(FOO);
        v.add(FOO);
        v.add(QLX);

        UniqueIterator<String> iter = new UniqueIterator<String>(v.iterator());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(FOO, iter.next());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(BAR, iter.next());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(BAZ, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(QLX, iter.next());
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException when off the end");
        }
        catch(NoSuchElementException x) {
            // success -- this exception is required
        }
    }

static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestUniqueIterator.class);
    }
}

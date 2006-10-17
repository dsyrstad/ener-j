// ============================================================================
// $Id: TestTransformBinaryIterator.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.comparison.Min;

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

public class TestTransformBinaryIterator extends TestCase {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    
    public TestTransformBinaryIterator (String name){ super(name); }

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

    public BinaryFunctor<String,String,String> min = new Min.Comparable<String>();
    
    public void testMinimum() {
        TransformBinaryIterator<String,String,String> iter =
            new TransformBinaryIterator<String,String,String>(v1.iterator(),v2.iterator(),min);
        assertTrue(iter.hasNext()); assertEquals(BAR, iter.next());
        assertTrue(iter.hasNext()); assertEquals(BAR, iter.next());
        assertTrue(iter.hasNext()); assertEquals(BAZ, iter.next());
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

    public void testReversed() {
        TransformBinaryIterator<String,String,String> iter =
            new TransformBinaryIterator<String,String,String>(v1.iterator(),v2.iterator(),min);
        assertTrue(iter.hasNext()); assertEquals(BAR, iter.next());
        assertTrue(iter.hasNext()); assertEquals(BAR, iter.next());
        assertTrue(iter.hasNext()); assertEquals(BAZ, iter.next());
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

    public void testEmpty() {
        EmptyIterator<String> empty = new EmptyIterator<String>();
        TransformBinaryIterator<String,String,String> iter =
            new TransformBinaryIterator<String,String,String>(empty,empty,min);

        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException when empty");
        }
        catch(NoSuchElementException x) {
            // success -- this exception is required
        }

        // make sure it works w/o call to hasNext()
        iter = new TransformBinaryIterator<String,String,String>(empty,empty,min);
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException when empty");
        }
        catch(NoSuchElementException x) {
            // success -- this exception is required
        }
    }

// make sure it works with one non-empty iterator
    public void testHalfEmpty() {
        EmptyIterator<String> empty = new EmptyIterator<String>();
        TransformBinaryIterator<String,String,String> iter =
            new TransformBinaryIterator<String,String,String>(v1.iterator(),empty,min);
        
        assertFalse(iter.hasNext());
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException when empty");
        }
        catch(NoSuchElementException x) {
            // success -- this exception is required
        }

        iter = new TransformBinaryIterator<String,String,String>(empty,v2.iterator(),min);
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException when empty");
        }
        catch(NoSuchElementException x) {
            // success -- this exception is required
        }
    }
    
    public void testMisuse() {
        TransformBinaryIterator<String,String,String> iter =
            new TransformBinaryIterator<String,String,String>(v1.iterator(),v2.iterator(),min);
        assertEquals(BAR, iter.next());
        
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(BAR, iter.next());
        
        assertEquals(BAZ, iter.next());
        
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
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
        junit.swingui.TestRunner.run(TestTransformBinaryIterator.class);
    }
}

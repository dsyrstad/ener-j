// ============================================================================
// $Id: TestArrayIterator.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
// Copyright (c) 2005  David A. Hall
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
import junit.framework.TestCase;

/**
 * Exercises ArrayIterator
 * <p>
 * Copyright &copy; 2005  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestArrayIterator extends TestCase {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    
    public TestArrayIterator (String name){ super(name); }

    String[] list = new String[4];;

    public void setUp() {
        list[0] = FOO;
        list[1] = BAR;
        list[2] = BAZ;
        list[3] = QLX;
    }

    public void tearDown() {
    }
        
    public void testStandardUsage() {
        ArrayIterator<String> iter = new ArrayIterator<String>(list);

        assertTrue(iter.hasNext());        
        assertTrue(!iter.hasPrevious());
        assertEquals(FOO, iter.next());
        assertTrue(iter.hasNext());        
        assertTrue(iter.hasPrevious());
        assertEquals(BAR, iter.next());
        assertTrue(iter.hasNext());        
        assertTrue(iter.hasPrevious());
        assertEquals(BAZ, iter.next());
        assertTrue(iter.hasNext());        
        assertTrue(iter.hasPrevious());
        assertEquals(QLX, iter.next());
        assertTrue(!iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertEquals(QLX, iter.previous());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertEquals(BAZ, iter.previous());
        assertTrue(iter.hasNext());        
        assertTrue(iter.hasPrevious());
        assertEquals(BAR, iter.previous());
        assertTrue(iter.hasNext());        
        assertTrue(iter.hasPrevious());
        assertEquals(FOO, iter.previous());
        assertTrue(iter.hasNext());        
        assertTrue(!iter.hasPrevious());
    }

    public void testNoHasHextCalls() {
        ArrayIterator<String> iter = new ArrayIterator<String>(list);

        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(QLX, iter.next());
        assertTrue(!iter.hasNext());
        assertEquals(QLX, iter.previous());
        assertEquals(BAZ, iter.previous());
        assertEquals(BAR, iter.previous());
        assertEquals(FOO, iter.previous());
        assertTrue(!iter.hasPrevious());
    }


    public void testTooManyHasHextCalls() {
        ArrayIterator<String> iter = new ArrayIterator<String>(list);

        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(FOO, iter.next());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(BAR, iter.next());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(BAZ, iter.next());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(QLX, iter.next());
        assertTrue(!iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertTrue(iter.hasPrevious());
        assertEquals(QLX, iter.previous());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertTrue(iter.hasPrevious());
        assertEquals(BAZ, iter.previous());
        assertTrue(iter.hasPrevious());
        assertTrue(iter.hasPrevious());
        assertEquals(BAR, iter.previous());
        assertTrue(iter.hasPrevious());
        assertTrue(iter.hasPrevious());
        assertEquals(FOO, iter.previous());
        assertTrue(!iter.hasPrevious());
    }

    
    public void testEmpty() {
        ArrayIterator<String> iter = new ArrayIterator<String>(new String[0]);
        assertTrue(!iter.hasNext());
        assertTrue(!iter.hasPrevious());
        try {
            iter.next();
            fail("Expecting NoSuchElementException, in keeping with Iterator contract");
        }
        catch (NoSuchElementException x){
            // ArrayIterator throws the correct exception
        }
        try {
            iter.previous();
            fail("Expecting NoSuchElementException, in keeping with Iterator contract");
        }
        catch (NoSuchElementException x){
            // ArrayIterator throws the correct exception
        }
    }

    
    public void testOne() {
        String[] foo = new String[1];
        foo[0] = FOO;
        ArrayIterator<String> iter = new ArrayIterator<String>( foo );
        assertTrue(iter.hasNext());
        assertTrue(!iter.hasPrevious());
        assertEquals(FOO, iter.next());
        assertTrue(!iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertEquals(FOO, iter.previous());
        assertTrue(iter.hasNext());
        assertTrue(!iter.hasPrevious());
    }


    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestArrayIterator.class);
    }
}

// ============================================================================
// $Id: TestFilterIterator.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.Visitor;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.util.FilterIterator;


/**
 * Exercises FilterIterator
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestFilterIterator extends TestCase {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    
    public TestFilterIterator (String name){ super(name); }

    Vector<String> list = new Vector<String>();

    public void setUp() {
        list.add(FOO);
        list.add(BAR);
        list.add(BAZ);
        list.add(FOO);
        list.add(BAR);
        list.add(BAZ);
    }

    public void tearDown() {
    }
        
    public void testStandardUsage() {
        FilterIterator<String> iter =
            new FilterIterator<String>(list.iterator(),
                                       new EqualTo<String>().bind2nd(FOO));

        assertTrue(iter.hasNext());        
        assertEquals(FOO, iter.next());
        assertTrue(iter.hasNext());        
        assertEquals(FOO, iter.next());
        assertTrue(!iter.hasNext());
    }

    public void testNoHasHextCalls() {
        FilterIterator<String> iter =
            new FilterIterator<String>(list.iterator(),
                                       new EqualTo<String>().bind2nd(FOO));

        assertEquals(FOO, iter.next());
        assertEquals(FOO, iter.next());
        assertTrue(!iter.hasNext());
    }


    public void testTooManyHasHextCalls() {
        FilterIterator<String> iter =
            new FilterIterator<String>(list.iterator(),
                                       new EqualTo<String>().bind2nd(FOO));

        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(FOO, iter.next());
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());
        assertEquals(FOO, iter.next());
        assertTrue(!iter.hasNext());
    }
    
    public void testNotFound() {
        FilterIterator<String> iter =
            new FilterIterator<String>(list.iterator(),
                                       new EqualTo<String>().bind2nd(QLX));

        assertTrue(!iter.hasNext());
        try
        {
            iter.next();
            fail("Expected NoSuchElementException when no element found, "
                 +"but next() called anyway");
        }
        catch (NoSuchElementException x) {}
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestFilterIterator.class);
    }
}

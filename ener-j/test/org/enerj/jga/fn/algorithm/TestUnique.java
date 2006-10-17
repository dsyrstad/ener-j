// ============================================================================
// $Id: TestUnique.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.logical.LogicalAnd;
import org.enerj.jga.fn.string.Match;
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

public class TestUnique extends FunctorTest<Unique<String>> {
    public TestUnique (String name){ super(name); }

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
        v.add(FOO);
        v.add(BAR);
        v.add(BAZ);
        v.add(BAZ);
        v.add(BAZ);
        v.add(FOO);
        v.add(BAR);
        v.add(BAR);
    }

    public void tearDown() {
    }
    
    public void testUniqueEmptyList() {
        Unique<String> xf = new Unique<String>();
        Iterator<String> iter = xf.fn(new EmptyIterator<String>());
        assertFalse(iter.hasNext());
    }
     
    public void testUnique() {
        Unique<String> xf = new Unique<String>();
        Iterator<String> iter = xf.fn(v.iterator());
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(BAZ, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertFalse(iter.hasNext());
    }

    public void testUniqueFn() {
        UnaryFunctor<String,Boolean> startsWith_b = new Match("_b.*");
        BinaryFunctor<String,String,Boolean> bothStartWith_b =
            new LogicalAnd().distribute(startsWith_b, startsWith_b);

        Unique<String> xf = makeSerial(new Unique<String>(bothStartWith_b));
        Iterator<String> iter = xf.fn(v.iterator());
        assertEquals(FOO, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertEquals(FOO, iter.next());
        assertEquals(BAR, iter.next());
        assertFalse(iter.hasNext());
    }

    public void testVisitableInterface() {
        Unique<String> xf = new Unique<String>();
        TestVisitor tv = new TestVisitor();
        xf.accept(tv);
        assertEquals(xf, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Unique.Visitor
    {
        public Object host;
        public void visit(Unique host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestUnique.class);
    }
}

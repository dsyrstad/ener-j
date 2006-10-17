// ============================================================================
// $Id: TestMaxValue.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import java.util.NoSuchElementException;
import java.util.Vector;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.util.ComparableComparator;

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

public class TestMaxValue extends FunctorTest<MaxValue<String>> {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";

    public TestMaxValue (String name){ super(name); }

    Vector<String> list = new Vector<String>();

    ComparableComparator<String> comp1 = new ComparableComparator<String>();
    Comparator<String> comp2 = new Samples.TestComparator();

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

    public void testMaxValueEmptyList() {
        try {
            MaxValue<String> tester = new MaxValue<String>(comp1);
            tester.fn(new Vector<String>());
            fail("Expected NoSuchElement when passed an empty list");
        }
        catch (NoSuchElementException x) {}
    }
     
    public void testMaxValueEmptyListWithComp() {
        try {
            MaxValue<String> tester = new MaxValue<String>(comp2);
            tester.fn(new Vector<String>());
            fail("Expected NoSuchElement when passed an empty list");
        }
        catch (NoSuchElementException x) {}
    }
     
    public void testMaxValueList() {
        MaxValue<String> tester = new MaxValue<String>(comp1);
        assertEquals(FOO,tester.fn(list));
    }

    public void testMaxValueListComp() {
        MaxValue<String> tester = new MaxValue<String>(comp2);
        assertEquals(BAR,tester.fn(list));
    }

    public void testSerialization() {
        MaxValue<String> tester = makeSerial(new MaxValue<String>(comp2));
        assertEquals(BAR,tester.fn(list));
    }

    public void testVisitableInterface() {
        MaxValue<String> tester = new MaxValue<String>(comp1);
        TestVisitor tv = new TestVisitor();
        tester.accept(tv);
        assertEquals(tester, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements MaxValue.Visitor
    {
        public Object host;
        public void visit(MaxValue host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestMaxValue.class);
    }
}

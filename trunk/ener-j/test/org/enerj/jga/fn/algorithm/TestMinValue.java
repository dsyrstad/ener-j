// ============================================================================
// $Id: TestMinValue.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.util.ComparableComparator;


/**
 * Exercises MinValue
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestMinValue extends FunctorTest<MinValue<String>> {
    
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";

    public TestMinValue (String name){ super(name); }

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

    public void testMinValueEmptyList() {
        try {
            MinValue<String> tester = new MinValue<String>(comp1);
            tester.fn(new Vector<String>());
            fail("Expected NoSuchElement when passed an empty list");
        }
        catch (NoSuchElementException x) {}
    }
     
    public void testMinValueEmptyListWithComp() {
        try {
            MinValue<String> tester = new MinValue<String>(comp2);
            tester.fn(new Vector<String>());
            fail("Expected NoSuchElement when passed an empty list");
        }
        catch (NoSuchElementException x) {}
    }
     
    public void testMinValueList() {
        MinValue<String> tester = new MinValue<String>(comp1);
        assertEquals(BAR,tester.fn(list));
    }

    public void testMinValueListComp() {
        MinValue<String> tester = new MinValue<String>(comp2);
        assertEquals(FOO,tester.fn(list));
    }

    public void testSerialization() {
        MinValue<String> tester = makeSerial(new MinValue<String>(comp2));
        assertEquals(FOO,tester.fn(list));
    }

    public void testVisitableInterface() {
        MinValue<String> tester = new MinValue<String>(comp1);
        TestVisitor tv = new TestVisitor();
        tester.accept(tv);
        assertEquals(tester, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements MinValue.Visitor
    {
        public Object host;
        public void visit(MinValue host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestMinValue.class);
    }
}

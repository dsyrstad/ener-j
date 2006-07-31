// ============================================================================
// $Id: TestComparatorFn.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.comparison;

import org.enerj.jga.SampleStringComparator;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises ComparatorFn functor
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestComparatorFn extends FunctorTest<ComparatorFn<String>> {
    public TestComparatorFn (String name){ super(name); }

    static private final String ONE = "one";
    static private final String TWO = "two";

    private ComparatorFn<String> func =
        makeSerial(new ComparatorFn<String>(new SampleStringComparator()));
    
    public void testFunctorInterface() {
        assertTrue(func.fn(ONE, TWO).intValue() < 0);
        assertTrue(func.fn(TWO, ONE).intValue() > 0);
        assertTrue(func.fn(ONE, ONE).intValue() == 0);
    }
    
    public void testComparatorInterface() {
        assertTrue(func.compare(ONE, TWO) < 0);
        assertTrue(func.compare(TWO, ONE) > 0);
        assertTrue(func.compare(ONE, ONE) == 0);
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        func.accept(tv);
        assertEquals(func, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ComparatorFn.Visitor
    {
        public Object host;
        public void visit(ComparatorFn host) { this.host = host; }
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestComparatorFn.class);
    }
}

// ============================================================================
// $Id: TestLess.java,v 1.4 2006/01/12 23:36:19 dsyrstad Exp $
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

import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises Less predicate
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestLess extends FunctorTest<Less<Integer>> {
    public TestLess (String name){ super(name); }

    private Integer ZERO = new Integer(0);
    private Integer ONE  = new Integer(1);
    
    private Less<Integer> pred = makeSerial(new Less.Comparable<Integer>());
    
    public void testFunctorInterface() {
        assertEquals(Boolean.TRUE,  pred.fn(ZERO, ONE));
        assertEquals(Boolean.FALSE, pred.fn(ONE,  ZERO));
        assertEquals(Boolean.FALSE, pred.fn(ZERO, ZERO));
        assertEquals(Boolean.FALSE, pred.fn(ZERO, null));
        assertEquals(Boolean.FALSE, pred.fn(null, ZERO));
        assertEquals(Boolean.FALSE, pred.fn(null, null));
    }
    
    public void testPredicateInterface() {
        assertTrue( pred.p(ZERO, ONE));
        assertTrue(!pred.p(ONE,  ZERO));
        assertTrue(!pred.p(ZERO, ZERO));
        assertTrue(!pred.p(ZERO, null));
        assertTrue(!pred.p(null, ZERO));
        assertTrue(!pred.p(null, null));
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        pred.accept(tv);
        assertEquals(pred, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Less.Visitor
    {
        public Object host;
        public void visit(Less host) { this.host = host; }
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestLess.class);
    }
}
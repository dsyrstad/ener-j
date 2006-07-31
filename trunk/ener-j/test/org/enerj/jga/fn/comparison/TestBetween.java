// ============================================================================
// $Id: TestBetween.java,v 1.4 2006/01/12 23:36:19 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises Between
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestBetween extends FunctorTest<Between<Integer>> {
    public TestBetween (String name){ super(name); }

    private Integer ZERO = new Integer(0);
    private Integer ONE  = new Integer(1);
    private Integer FIVE = new Integer(5);
    private Integer TEN  = new Integer(10);
    
    public void testFunctorInterface() {
        Between<Integer> pred = new Between.Comparable<Integer>(ONE,TEN);
        assertEquals(Boolean.FALSE, pred.fn(ZERO));
        assertEquals(Boolean.TRUE,  pred.fn(ONE));
        assertEquals(Boolean.TRUE,  pred.fn(FIVE));
        assertEquals(Boolean.TRUE,  pred.fn(TEN));
        assertEquals(Boolean.FALSE,  pred.fn(null));

    }
    
    public void testExclusive() {
        UnaryFunctor<Integer,Boolean> gr = new Greater.Comparable<Integer>().bind2nd(ONE);
        UnaryFunctor<Integer,Boolean> le = new Less.Comparable<Integer>().bind2nd(TEN);
        
        Between<Integer> pred = new Between<Integer>(gr,le);
        assertEquals(Boolean.FALSE, pred.fn(ZERO));
        assertEquals(Boolean.FALSE, pred.fn(ONE));
        assertEquals(Boolean.TRUE,  pred.fn(FIVE));
        assertEquals(Boolean.FALSE, pred.fn(TEN));
        assertEquals(Boolean.FALSE, pred.fn(null));
    }
    
    public void testPredicateInterface() {
        Between<Integer> pred = makeSerial(new Between.Comparable<Integer>(ONE,TEN));
        assertTrue(!pred.p(ZERO));
        assertTrue( pred.p(ONE));
        assertTrue( pred.p(FIVE));
        assertTrue( pred.p(TEN));
        assertFalse( pred.p(null));
    }

    public void testVisitableInterface() {
        Between<Integer> pred = new Between.Comparable<Integer>(ONE,TEN);
        TestVisitor tv = new TestVisitor();
        pred.accept(tv);
        assertEquals(pred, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Between.Visitor
    {
        public Object host;
        public void visit(Between host) { this.host = host; }
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestBetween.class);
    }
}

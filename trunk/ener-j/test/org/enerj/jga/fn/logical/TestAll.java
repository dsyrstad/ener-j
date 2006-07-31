// ============================================================================
// $Id: TestAll.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

package org.enerj.jga.fn.logical;

import org.enerj.jga.SampleUnaryFunctor;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Exercises All functor
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestAll extends FunctorTest<All<String>> {
    public TestAll (String name){ super(name); }

    static public final String FOO = "_foo_";

    private SampleUnaryFunctor<String,Boolean> truebranch  =
        new SampleUnaryFunctor<String,Boolean>(FOO, Boolean.TRUE);
    private SampleUnaryFunctor<String,Boolean> truebranch2 =
        new SampleUnaryFunctor<String,Boolean>(FOO, Boolean.TRUE);
    private SampleUnaryFunctor<String,Boolean> falsebranch =
        new SampleUnaryFunctor<String,Boolean>(FOO, Boolean.FALSE);

    
    private All<String> makeZero() {
        return new All<String>();
    }

    private All<String> makeOne(UnaryFunctor<String,Boolean> branch) {
        All<String> one = new All<String>();
        one.addBranch(branch);
        return one;
    }

    private All<String> makeMany(UnaryFunctor<String,Boolean> branch1,
                                 UnaryFunctor<String,Boolean> branch2)
    {
        All<String> many = new All<String>();
        many.addBranch(branch1);
        many.addBranch(branch2);
        return many;
    }

    
    public void testNoBranches() {
        All<String> zero = makeZero();
        assertEquals(Boolean.TRUE, zero.fn(FOO));
        assertTrue(zero.p(FOO));
    }

    public void testFunctorOneTrueBranch() {
        All<String> onetrue = makeOne(truebranch);
        assertEquals(Boolean.TRUE, onetrue.fn(FOO));
        assertEquals(FOO, truebranch._got);
    }
        
    public void testPredOneTrueBranch() {
        All<String> onetrue = makeOne(truebranch);
        assertTrue(onetrue.p(FOO));
        assertEquals(FOO, truebranch._got);
    }

    public void testFunctorOneFalseBranch() {
        All<String> onefalse = makeOne(falsebranch);
        assertEquals(Boolean.FALSE, onefalse.fn(FOO));
        assertEquals(FOO, falsebranch._got);
    }
        
    public void testPredOneFalse() {
        All<String> onefalse = makeOne(falsebranch);
        assertTrue(!onefalse.p(FOO));
        assertEquals(FOO, falsebranch._got);
    }

    public void testFunctorManyTrue() {
        All<String> manytrue = makeMany(truebranch, truebranch2);
        assertEquals(Boolean.TRUE, manytrue.fn(FOO));
        assertEquals(FOO, truebranch._got);
        assertEquals(FOO, truebranch2._got);
    }
        
    public void testPredManyTrue() {
        All<String> manytrue = makeMany(truebranch, truebranch2);
        assertTrue(manytrue.p(FOO));
        assertEquals(FOO, truebranch._got);
        assertEquals(FOO, truebranch2._got);
    }

    public void testFunctorTrueFalse() {
        All<String> truefalse = makeMany(truebranch, falsebranch);
        assertEquals(Boolean.FALSE, truefalse.fn(FOO));
        assertEquals(FOO, truebranch._got);
        assertEquals(FOO, falsebranch._got);
    }
        
    public void testPredTrueFalse() {
        All<String> truefalse = makeMany(truebranch, falsebranch);
        assertTrue(!truefalse.p(FOO));
        assertEquals(FOO, truebranch._got);
        assertEquals(FOO, falsebranch._got);
    }

    public void testFunctorFalseTrue() {
        All<String> falsetrue = makeMany(falsebranch, truebranch);
        assertEquals(Boolean.FALSE, falsetrue.fn(FOO));
        assertEquals(FOO, falsebranch._got);
        assertNull(truebranch._got);
    }
        
    public void testPredFalseTrue() {
        All<String> falsetrue = makeMany(falsebranch, truebranch);
        assertTrue(!falsetrue.p(FOO));
        assertEquals(FOO, falsebranch._got);
        assertNull(truebranch._got);
    }

    public void testSerialization() {
        All<String> falsetrue = makeSerial(makeMany(falsebranch, truebranch));
        assertFalse(falsetrue.p(FOO));
        
        All<String> truefalse = makeSerial(makeMany(truebranch, falsebranch));
        assertFalse(truefalse.p(FOO));
        
        All<String> onefalse = makeSerial(makeOne(falsebranch));
        assertFalse(onefalse.p(FOO));
        
        All<String> zero = makeSerial(makeZero());
        assertTrue(zero.p(FOO));
    }


    public void testVisitableInterface() {
        All<String> zero = makeZero();
        TestVisitor tv = new TestVisitor();
        zero.accept(tv);
        assertEquals(zero, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements All.Visitor
    {
        public Object host;
        public void visit(All host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestAll.class);
    }
}

// ============================================================================
// $Id: TestAny.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.FunctorTest;

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

public class TestAny extends FunctorTest<Any<String>> {
    
    static public final String FOO = "_foo_";
    
    public TestAny (String name){ super(name); }

    private SampleUnaryFunctor<String,Boolean> truebranch  =
        new SampleUnaryFunctor<String,Boolean>(FOO, Boolean.TRUE);
    private SampleUnaryFunctor<String,Boolean> truebranch2 =
        new SampleUnaryFunctor<String,Boolean>(FOO, Boolean.TRUE);
    private SampleUnaryFunctor<String,Boolean> falsebranch =
        new SampleUnaryFunctor<String,Boolean>(FOO, Boolean.FALSE);
    
    private Any<String> makeZero() {
        return new Any<String>();
    }

    private Any<String> makeOne(UnaryFunctor<String,Boolean> branch) {
        Any<String> one = new Any<String>();
        one.addBranch(branch);
        return one;
    }

    private Any<String> makeMany(UnaryFunctor<String,Boolean> branch1,
                                 UnaryFunctor<String,Boolean> branch2)
    {
        Any<String> many = new Any<String>();
        many.addBranch(branch1);
        many.addBranch(branch2);
        return many;
    }

    
    public void testNoBranches() {
        Any<String> zero = makeZero();
        assertEquals(Boolean.FALSE, zero.fn(FOO));
        assertTrue(!zero.p(FOO));
    }

    public void testFunctorOneTrueBranch() {
        Any<String> onetrue = makeOne(truebranch);
        assertEquals(Boolean.TRUE, onetrue.fn(FOO));
        assertEquals(FOO, truebranch._got);
    }
        
    public void testPredOneTrueBranch() {
        Any<String> onetrue = makeOne(truebranch);
        assertTrue(onetrue.p(FOO));
        assertEquals(FOO, truebranch._got);
    }

    public void testFunctorOneFalseBranch() {
        Any<String> onefalse = makeOne(falsebranch);
        assertEquals(Boolean.FALSE, onefalse.fn(FOO));
        assertEquals(FOO, falsebranch._got);
    }
        
    public void testPredOneFalse() {
        Any<String> onefalse = makeOne(falsebranch);
        assertTrue(!onefalse.p(FOO));
        assertEquals(FOO, falsebranch._got);
    }

    public void testFunctorManyTrue() {
        Any<String> manytrue = makeMany(truebranch, truebranch2);
        assertEquals(Boolean.TRUE, manytrue.fn(FOO));
        assertEquals(FOO, truebranch._got);
        assertNull(truebranch2._got);
    }
        
    public void testPredManyTrue() {
        Any<String> manytrue = makeMany(truebranch, truebranch2);
        assertTrue(manytrue.p(FOO));
        assertEquals(FOO, truebranch._got);
        assertNull(truebranch2._got);
    }

    public void testFunctorTrueFalse() {
        Any<String> truefalse = makeMany(truebranch, falsebranch);
        assertEquals(Boolean.TRUE, truefalse.fn(FOO));
        assertEquals(FOO, truebranch._got);
        assertNull(falsebranch._got);
    }
        
    public void testPredTrueFalse() {
        Any<String> truefalse = makeMany(truebranch, falsebranch);
        assertTrue(truefalse.p(FOO));
        assertEquals(FOO, truebranch._got);
        assertNull(falsebranch._got);
    }

    public void testFunctorFalseTrue() {
        Any<String> falsetrue = makeMany(falsebranch, truebranch);
        assertEquals(Boolean.TRUE, falsetrue.fn(FOO));
        assertEquals(FOO, falsebranch._got);
        assertEquals(FOO, truebranch._got);
    }
        
    public void testPredFalseTrue() {
        Any<String> falsetrue = makeMany(falsebranch, truebranch);
        assertTrue(falsetrue.p(FOO));
        assertEquals(FOO, truebranch._got);
        assertEquals(FOO, falsebranch._got);
    }

    public void testSerialization() {
        Any<String> falsetrue = makeSerial(makeMany(falsebranch, truebranch));
        assertTrue(falsetrue.p(FOO));
        
        Any<String> truefalse = makeSerial(makeMany(truebranch, falsebranch));
        assertTrue(truefalse.p(FOO));
        
        Any<String> onefalse = makeSerial(makeOne(falsebranch));
        assertFalse(onefalse.p(FOO));
        
        Any<String> zero = makeSerial(makeZero());
        assertFalse(zero.p(FOO));
    }

    public void testVisitableInterface() {
        Any<String> zero = makeZero();
        TestVisitor tv = new TestVisitor();
        zero.accept(tv);
        assertEquals(zero, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Any.Visitor
    {
        public Object host;
        public void visit(Any host) { this.host = host; }
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestAny.class);
    }
}

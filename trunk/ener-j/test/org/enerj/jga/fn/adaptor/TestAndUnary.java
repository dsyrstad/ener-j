// ============================================================================
// $Id: TestAndUnary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.adaptor;

import java.io.IOException;
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

public class TestAndUnary extends FunctorTest<AndUnary> {
    public TestAndUnary (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";

    SampleUnaryFunctor<String,Boolean> ufTrue =
        new SampleUnaryFunctor<String,Boolean>(FOO,Boolean.TRUE);
    SampleUnaryFunctor<String,Boolean> ufFalse =
        new SampleUnaryFunctor<String,Boolean>(FOO,Boolean.FALSE);
    SampleUnaryFunctor<String,Boolean> ufNull =
        new SampleUnaryFunctor<String,Boolean>(FOO,null);
    SampleUnaryFunctor<String,Boolean> ufStillTrue =
        new SampleUnaryFunctor<String,Boolean>(FOO,Boolean.TRUE);
    
    private AndUnary<String>
    makeFunctor(UnaryFunctor<String,Boolean> uf1, UnaryFunctor<String,Boolean> uf2) {
        return new AndUnary<String>(uf1, uf2);
    }

    public void testFunctorInterface0() {
        AndUnary<String> pred = makeFunctor(ufTrue, ufFalse);
        assertEquals(Boolean.FALSE,  pred.fn(FOO));
        assertEquals(FOO, ufTrue._got);
        assertEquals(FOO, ufFalse._got);
    }
        
    public void testFunctorInterface1() {
        AndUnary<String> pred = makeFunctor(ufFalse, ufNull);
        assertEquals(Boolean.FALSE, pred.fn(FOO));
        assertNull(ufNull._got);
        assertEquals(FOO, ufFalse._got);
    }
        
    public void testFunctorInterface2() {
        AndUnary<String> pred = makeFunctor(ufTrue, ufStillTrue);
        assertEquals(Boolean.TRUE, pred.fn(FOO));
        assertEquals(FOO, ufTrue._got);
        assertEquals(FOO, ufStillTrue._got);
    }
        
    public void testPredicateInterface0() {
        AndUnary<String> pred = makeFunctor(ufTrue, ufFalse);
        assertTrue(!pred.p(FOO));
        assertEquals(FOO, ufTrue._got);
        assertEquals(FOO, ufFalse._got);
    }
    
    public void testPredicateInterface1() {
        AndUnary<String> pred = makeFunctor(ufFalse, ufNull);
        assertTrue(!pred.p(FOO));
        assertEquals(FOO, ufFalse._got);
        assertNull(ufNull._got);
    }
    
    public void testPredicateInterface2() {
        AndUnary<String> pred = makeFunctor(ufTrue, ufStillTrue);
        assertTrue(pred.p(FOO));
        assertEquals(FOO, ufTrue._got);
        assertEquals(FOO, ufStillTrue._got);
    }

    public void testSerializedFunctor() {
        AndUnary<String> pred = makeSerial(makeFunctor(ufTrue, ufFalse));
        assertEquals(Boolean.FALSE,  pred.fn(FOO));
    }
        
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        AndUnary<String> pred = makeFunctor(ufTrue, ufFalse);
        pred.accept(tv);
        assertEquals(pred, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements AndUnary.Visitor
    {
        public Object host;
        public void visit(AndUnary host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestAndUnary.class);
    }
}

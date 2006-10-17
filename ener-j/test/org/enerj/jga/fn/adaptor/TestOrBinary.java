// ============================================================================
// $Id: TestOrBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
import org.enerj.jga.SampleBinaryFunctor;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.BinaryFunctor;
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

public class TestOrBinary extends FunctorTest<OrBinary> {
    public TestOrBinary (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";

    SampleBinaryFunctor<String,String,Boolean> bfTrue =
        new SampleBinaryFunctor<String,String,Boolean>(FOO,BAR,Boolean.TRUE);
    SampleBinaryFunctor<String,String,Boolean> bfFalse =
        new SampleBinaryFunctor<String,String,Boolean>(FOO,BAR,Boolean.FALSE);
    SampleBinaryFunctor<String,String,Boolean> bfNull =
        new SampleBinaryFunctor<String,String,Boolean>(FOO,BAR,null);
    SampleBinaryFunctor<String,String,Boolean> bfStillFalse =
        new SampleBinaryFunctor<String,String,Boolean>(FOO,BAR,Boolean.FALSE);
    
    private OrBinary<String,String>
    makeFunctor(BinaryFunctor<String,String,Boolean> bf1, BinaryFunctor<String,String,Boolean> bf2) {
        return new OrBinary<String,String>(bf1, bf2);
    }

    public void testFunctorInterface0() {
        OrBinary<String,String> pred = makeFunctor(bfFalse, bfTrue);
        assertEquals(Boolean.TRUE,  pred.fn(FOO,BAR));
        assertEquals(FOO, bfTrue._gotX);
        assertEquals(BAR, bfTrue._gotY);
        assertEquals(FOO, bfFalse._gotX);
        assertEquals(BAR, bfFalse._gotY);
    }
        
    public void testFunctorInterface1() {
        OrBinary<String,String> pred = makeFunctor(bfTrue, bfNull);
        assertEquals(Boolean.TRUE, pred.fn(FOO,BAR));
        assertNull(bfNull._gotX);
        assertNull(bfNull._gotY);
        assertEquals(FOO, bfTrue._gotX);
        assertEquals(BAR, bfTrue._gotY);
    }
        
    public void testFunctorInterface2() {
        OrBinary<String,String> pred = makeFunctor(bfFalse, bfStillFalse);
        assertEquals(Boolean.FALSE, pred.fn(FOO,BAR));
        assertEquals(FOO, bfFalse._gotX);
        assertEquals(BAR, bfFalse._gotY);
        assertEquals(FOO, bfStillFalse._gotX);
        assertEquals(BAR, bfStillFalse._gotY);
    }
        
    public void testPredicateInterface0() {
        OrBinary<String,String> pred = makeFunctor(bfFalse, bfTrue);
        assertTrue(pred.p(FOO,BAR));
        assertEquals(FOO, bfTrue._gotX);
        assertEquals(BAR, bfTrue._gotY);
        assertEquals(FOO, bfFalse._gotX);
        assertEquals(BAR, bfFalse._gotY);
    }
    
    public void testPredicateInterface1() {
        OrBinary<String,String> pred = makeFunctor(bfTrue, bfNull);
        assertTrue(pred.p(FOO,BAR));
        assertEquals(FOO, bfTrue._gotX);
        assertEquals(BAR, bfTrue._gotY);
        assertNull(bfNull._gotX);
        assertNull(bfNull._gotY);
    }
    
    public void testPredicateInterface2() {
        OrBinary<String,String> pred = makeFunctor(bfFalse, bfStillFalse);
        assertTrue(!pred.p(FOO,BAR));
        assertEquals(FOO, bfFalse._gotX);
        assertEquals(BAR, bfFalse._gotY);
        assertEquals(FOO, bfStillFalse._gotX);
        assertEquals(BAR, bfStillFalse._gotY);
    }

    public void testSerializedFunctor() {
        OrBinary<String,String> pred = makeSerial(makeFunctor(bfTrue, bfFalse));
        assertEquals(Boolean.TRUE,  pred.fn(FOO,BAR));
    }
        
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        OrBinary<String,String> pred = makeFunctor(bfTrue, bfFalse);
        pred.accept(tv);
        assertEquals(pred, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements OrBinary.Visitor
    {
        public Object host;
        public void visit(OrBinary host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestOrBinary.class);
    }
}

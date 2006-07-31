// ============================================================================
// $Id: TestAndBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

/**
 * Exercises TestAndBinary
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestAndBinary extends FunctorTest<AndBinary> {
    public TestAndBinary (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";

    SampleBinaryFunctor<String,String,Boolean> bfTrue =
        new SampleBinaryFunctor<String,String,Boolean>(FOO,BAR,Boolean.TRUE);
    SampleBinaryFunctor<String,String,Boolean> bfFalse =
        new SampleBinaryFunctor<String,String,Boolean>(FOO,BAR,Boolean.FALSE);
    SampleBinaryFunctor<String,String,Boolean> bfNull =
        new SampleBinaryFunctor<String,String,Boolean>(FOO,BAR,null);
    SampleBinaryFunctor<String,String,Boolean> bfStillTrue =
        new SampleBinaryFunctor<String,String,Boolean>(FOO,BAR,Boolean.TRUE);
    
    private AndBinary<String,String>
    makeFunctor(BinaryFunctor<String,String,Boolean> bf1, BinaryFunctor<String,String,Boolean> bf2) {
        return new AndBinary<String,String>(bf1, bf2);
    }

    public void testFunctorInterface0() {
        AndBinary<String,String> pred = makeFunctor(bfTrue, bfFalse);
        assertEquals(Boolean.FALSE,  pred.fn(FOO,BAR));
        assertEquals(FOO, bfTrue._gotX);
        assertEquals(BAR, bfTrue._gotY);
        assertEquals(FOO, bfFalse._gotX);
        assertEquals(BAR, bfFalse._gotY);
    }
        
    public void testFunctorInterface1() {
        AndBinary<String,String> pred = makeFunctor(bfFalse, bfNull);
        assertEquals(Boolean.FALSE, pred.fn(FOO,BAR));
        assertNull(bfNull._gotX);
        assertNull(bfNull._gotY);
        assertEquals(FOO, bfFalse._gotX);
        assertEquals(BAR, bfFalse._gotY);
    }
        
    public void testFunctorInterface2() {
        AndBinary<String,String> pred = makeFunctor(bfTrue, bfStillTrue);
        assertEquals(Boolean.TRUE, pred.fn(FOO,BAR));
        assertEquals(FOO, bfTrue._gotX);
        assertEquals(BAR, bfTrue._gotY);
        assertEquals(FOO, bfStillTrue._gotX);
        assertEquals(BAR, bfStillTrue._gotY);
    }
        
    public void testPredicateInterface0() {
        AndBinary<String,String> pred = makeFunctor(bfTrue, bfFalse);
        assertTrue(!pred.p(FOO,BAR));
        assertEquals(FOO, bfTrue._gotX);
        assertEquals(BAR, bfTrue._gotY);
        assertEquals(FOO, bfFalse._gotX);
        assertEquals(BAR, bfFalse._gotY);
    }
    
    public void testPredicateInterface1() {
        AndBinary<String,String> pred = makeFunctor(bfFalse, bfNull);
        assertTrue(!pred.p(FOO,BAR));
        assertEquals(FOO, bfFalse._gotX);
        assertEquals(BAR, bfFalse._gotY);
        assertNull(bfNull._gotX);
        assertNull(bfNull._gotY);
    }
    
    public void testPredicateInterface2() {
        AndBinary<String,String> pred = makeFunctor(bfTrue, bfStillTrue);
        assertTrue(pred.p(FOO,BAR));
        assertEquals(FOO, bfTrue._gotX);
        assertEquals(BAR, bfTrue._gotY);
        assertEquals(FOO, bfStillTrue._gotX);
        assertEquals(BAR, bfStillTrue._gotY);
    }

    public void testSerializedFunctor() {
        AndBinary<String,String> pred = makeSerial(makeFunctor(bfTrue, bfFalse));
        assertEquals(Boolean.FALSE,  pred.fn(FOO,BAR));
    }
        
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        AndBinary<String,String> pred = makeFunctor(bfTrue, bfFalse);
        pred.accept(tv);
        assertEquals(pred, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements AndBinary.Visitor
    {
        public Object host;
        public void visit(AndBinary host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestAndBinary.class);
    }
}

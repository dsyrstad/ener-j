// ============================================================================
// $Id: TestAndGenerator.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
import org.enerj.jga.SampleGenerator;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.Generator;

/**
 * Exercises TestAndGenerator
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestAndGenerator extends FunctorTest<AndGenerator> {
    public TestAndGenerator (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";

    SampleGenerator<Boolean> genTrue      = new SampleGenerator<Boolean>(Boolean.TRUE);
    SampleGenerator<Boolean> genFalse     = new SampleGenerator<Boolean>(Boolean.FALSE);
    SampleGenerator<Boolean> genNull      = new SampleGenerator<Boolean>(null);
    SampleGenerator<Boolean> genStillTrue = new SampleGenerator<Boolean>(Boolean.TRUE);
    
    private AndGenerator makeFunctor(Generator<Boolean> gen1, Generator<Boolean> gen2) {
        return new AndGenerator(gen1, gen2);
    }

    public void testFunctorInterface0() {
        AndGenerator pred = makeFunctor(genTrue, genFalse);
        assertEquals(Boolean.FALSE,  pred.gen());
        assertEquals(1, genTrue._count);
        assertEquals(1, genFalse._count);
    }
        
    public void testFunctorInterface1() {
        AndGenerator pred = makeFunctor(genFalse, genNull);
        assertEquals(Boolean.FALSE, pred.gen());
        assertEquals(0, genNull._count);
        assertEquals(1, genFalse._count);
    }
        
    public void testFunctorInterface2() {
        AndGenerator pred = makeFunctor(genTrue, genStillTrue);
        assertEquals(Boolean.TRUE,  pred.gen());
        assertEquals(1, genTrue._count);
        assertEquals(1, genStillTrue._count);
    }
        
    public void testSerializedFunctor() {
        AndGenerator pred = makeSerial(makeFunctor(genTrue, genFalse));
        assertEquals(Boolean.FALSE,  pred.gen());
    }
        
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        AndGenerator pred = makeFunctor(genTrue, genFalse);
        pred.accept(tv);
        assertEquals(pred, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements AndGenerator.Visitor
    {
        public Object host;
        public void visit(AndGenerator host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestAndGenerator.class);
    }
}

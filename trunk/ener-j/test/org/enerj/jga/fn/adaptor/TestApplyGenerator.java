// ============================================================================
// $Id: TestApplyGenerator.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import org.enerj.jga.SampleGenerator;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.Generator;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises ApplyGenerator functor
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestApplyGenerator extends FunctorTest<ApplyGenerator>
{
    public TestApplyGenerator (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";

    Generator<String> bf1 = new SampleGenerator<String>(FOO);
    Generator<String> bf2 = new SampleGenerator<String>(BAR);
    Generator<String> bf3 = new SampleGenerator<String>(BAZ);
    
    Generator[] functors = new Generator[]{ bf1,bf2,bf3 };
    
    private ApplyGenerator makeApply = makeSerial(new ApplyGenerator(functors));
    
    public void testFunctorInterface() {
        Object[] result = makeApply.gen();
        assertEquals(FOO, result[0]);
        assertEquals(BAR, result[1]);
        assertEquals(BAZ, result[2]);
        assertEquals(3, result.length);
    }

    public void testEmptyArgList() {
        Generator[] noGens = new Generator[] {};
        ApplyGenerator gen = new ApplyGenerator(noGens);
        Object[] result = gen.gen();
        assertEquals(0, result.length);
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        makeApply.accept(tv);
        assertEquals(makeApply, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ApplyGenerator.Visitor
    {
        public Object host;
        public void visit(ApplyGenerator host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestApplyGenerator.class);
    }
}

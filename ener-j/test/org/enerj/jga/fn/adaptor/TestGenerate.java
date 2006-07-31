// ============================================================================
// $Id: TestGenerate.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.adaptor;

import junit.framework.AssertionFailedError;
import org.enerj.jga.SampleUnaryFunctor;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.Visitable;
import org.enerj.jga.fn.Visitor;

/**
 * Exercises Generate
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestGenerate extends FunctorTest<Generate<String,Integer>> {
    static public final String FOO = "_foo_";
    static public final Integer ONE = new Integer(1);

    public TestGenerate (String name){ super(name); }

    private SampleUnaryFunctor<String,Integer> uf =
        new SampleUnaryFunctor<String,Integer>(FOO, ONE);
    
    private Constant<String> foogen = new Constant<String>(FOO);
    private Generate<String,Integer> func =
        new Generate<String,Integer>(uf, foogen);
    
    public void testFunctorInterface() {
        assertEquals(ONE, func.gen());
        assertEquals(FOO, uf._got);
    }

    public void testSerialization() {
        Generate<String,Integer> fn = makeSerial(func);
        assertEquals(ONE, fn.gen());

        // this part ensures that the construct was deserialized properly
        try {
            assertEquals(ONE,fn.gen());
            fail("This should fail: the inner exception is expecting TRUE");
        }
        catch(AssertionFailedError x) {}
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        func.accept(tv);
        assertEquals(func, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Generate.Visitor
    {
        public Object host;
        public void visit(Generate host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestGenerate.class);
    }
}

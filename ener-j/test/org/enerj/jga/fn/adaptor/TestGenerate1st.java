// ============================================================================
// $Id: TestGenerate1st.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
import org.enerj.jga.SampleBinaryFunctor;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.Visitable;
import org.enerj.jga.fn.Visitor;

/**
 * Exercises Generate1st
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestGenerate1st extends FunctorTest<Generate1st<String,Boolean,Integer>> {
    static public final String FOO = "_foo_";
    static public final Integer ONE = new Integer(1);

    public TestGenerate1st (String name){ super(name); }

    private SampleBinaryFunctor<String,Boolean,Integer> bf =
        new SampleBinaryFunctor<String,Boolean,Integer>(FOO, Boolean.TRUE, ONE);
    
    private Constant<String> foogen = new Constant<String>(FOO);

    private Generate1st<String,Boolean,Integer> func =
        new Generate1st<String,Boolean,Integer>(bf, foogen);
    
    public void testFunctorInterface() {
        assertEquals(ONE,  func.fn(Boolean.TRUE));
        assertEquals(FOO,  bf._gotX);
        assertEquals(Boolean.TRUE, bf._gotY);
    }

    public void testSerialization() {
        Generate1st<String,Boolean,Integer> fn = makeSerial(func);
        assertEquals(ONE, fn.fn(Boolean.TRUE));

        // this part ensures that the construct was deserialized properly
        try {
            assertEquals(ONE,fn.fn(Boolean.FALSE));
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
            implements Generate1st.Visitor
    {
        public Object host;
        public void visit(Generate1st host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestGenerate1st.class);
    }
}

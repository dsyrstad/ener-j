// ============================================================================
// $Id: TestInvokeNoArgMethod.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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

package org.enerj.jga.fn.property;

import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises InvokeNoArgMethod functor
 *
 * <p>Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestInvokeNoArgMethod
    extends FunctorTest<InvokeNoArgMethod<Integer,String>>
{
    public TestInvokeNoArgMethod (String name){ super(name); }

    private InvokeNoArgMethod<Integer,String> toStringMeth =
        new InvokeNoArgMethod<Integer,String>(Integer.class,"toString");
    
    public void testFunctorInterface() {
        Integer i = new Integer(-4321);
        assertEquals("-4321", makeSerial(toStringMeth).fn(i));
    }
    
    public void testMethodNames() {
        assertEquals("toString", toStringMeth.getMethodName());
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        toStringMeth.accept(tv);
        assertEquals(toStringMeth, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements InvokeNoArgMethod.Visitor
    {
        public Object host;
        public void visit(InvokeNoArgMethod host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestInvokeNoArgMethod.class);
    }
}

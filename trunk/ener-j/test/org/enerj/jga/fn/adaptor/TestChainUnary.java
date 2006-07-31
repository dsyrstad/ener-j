// ============================================================================
// $Id: TestChainUnary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import junit.framework.AssertionFailedError;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.SampleUnaryFunctor;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.Visitable;
import org.enerj.jga.fn.Visitor;

/**
 * Exercises ChainUnary
 *
 * <p>Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestChainUnary
    extends FunctorTest<ChainUnary<String,String,String>>
{
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    
    public TestChainUnary (String name){ super(name); }

    private SampleUnaryFunctor<String,String> inner =
            new SampleUnaryFunctor<String,String>(FOO, BAR);
    
    private SampleUnaryFunctor<String,String> outer =
            new SampleUnaryFunctor<String,String>(BAR, BAZ);
    
    private ChainUnary<String,String,String> func
        = new ChainUnary<String,String,String>(outer, inner);

    public void testFunctorInterface() {
        assertEquals(BAZ, func.fn(FOO));
        assertEquals(FOO, inner._got);
        assertEquals(BAR, outer._got);
    }

    public void testSerialization() {
        ChainUnary<String,String,String> f = makeSerial(func);
        assertEquals(BAZ, f.fn(FOO));

        try {
            assertEquals(BAZ, f.fn(BAR));
            String msg = "Expected AssertionFailedError when passed "
                +"\"BAR\" expecting \"FOO\"";
            fail(msg);
        }
        catch(AssertionFailedError x) {}
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        func.accept(tv);
        assertEquals(func, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ChainUnary.Visitor
    {
        public Object host;
        public void visit(ChainUnary host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestChainUnary.class);
    }
}

// ============================================================================
// $Id: TestChainBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import java.util.Date;
import junit.framework.AssertionFailedError;
import org.enerj.jga.SampleBinaryFunctor;
import org.enerj.jga.SampleUnaryFunctor;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.Visitable;
import org.enerj.jga.fn.Visitor;

/**
 * Exercises ChainBinary
 *
 * <p>Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestChainBinary
    extends FunctorTest<ChainBinary<Integer,Date,String,String>>
{
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    static public final Integer ONE = new Integer(1);
    static public final Date NOW = new Date();

    public TestChainBinary (String name){ super(name); }

    private SampleUnaryFunctor<String,String> outer =
            new SampleUnaryFunctor<String,String>(FOO, BAR);
    
    private SampleBinaryFunctor<Integer,Date,String> inner =
            new SampleBinaryFunctor<Integer,Date,String>(ONE, NOW, FOO);
    
    private ChainBinary<Integer,Date,String,String> func =
        new ChainBinary<Integer,Date,String,String>(outer,inner);

    public void testFunctorInterface() {
        assertEquals(BAR, func.fn(ONE, NOW));
        assertEquals(FOO, outer._got);
        assertEquals(ONE, inner._gotX);
        assertEquals(NOW, inner._gotY);
    }

    public void testSerialization() {
        ChainBinary<Integer,Date,String,String> f2 = makeSerial(func);
        assertEquals(BAR, f2.fn(ONE, NOW));

        try {
            assertEquals(BAR, f2.fn(ONE, new Date()));
            String msg = "Expected AssertionFailedError when passed "
                +"EPOCH expecting NOW";
            fail(msg);
        }
        catch (AssertionFailedError x) {}
        
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        func.accept(tv);
        assertEquals(func, tv.host);
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestChainBinary.class);
    }

    private class TestVisitor extends AbstractVisitor
            implements ChainBinary.Visitor
    {
        public Object host;
        public void visit(ChainBinary host) { this.host = host; }
    }
}
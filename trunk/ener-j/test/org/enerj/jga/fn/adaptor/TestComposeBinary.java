// ============================================================================
// $Id: TestComposeBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.Visitable;
import org.enerj.jga.fn.Visitor;


/**
 * Exercises ComposeBinary
 *
 * <p>Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestComposeBinary
    extends FunctorTest<ComposeBinary<String,String,Integer,Date,String>>
{
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    static public final Integer ONE = new Integer(1);
    static public final Date NOW = new Date();
    
    public TestComposeBinary (String name){ super(name); }

    private SampleBinaryFunctor<String,String,Integer> inner1 =
            new SampleBinaryFunctor<String,String,Integer>(FOO, BAR, ONE);
    
    private SampleBinaryFunctor<String,String,Date> inner2 =
            new SampleBinaryFunctor<String,String,Date>(FOO, BAR, NOW);

    private SampleBinaryFunctor<Integer,Date,String> outer =
            new SampleBinaryFunctor<Integer,Date,String>(ONE, NOW, BAZ);
    
    private ComposeBinary<String,String,Integer,Date,String> func =
        new ComposeBinary<String,String,Integer,Date,String>(inner1, inner2, outer);

    public void testFunctorInterface() {
        assertEquals(BAZ, func.fn(FOO, BAR));
        assertEquals(FOO, inner1._gotX);
        assertEquals(FOO, inner2._gotX);
        assertEquals(BAR, inner1._gotY);
        assertEquals(BAR, inner2._gotY);
        assertEquals(ONE, outer._gotX);
        assertEquals(NOW, outer._gotY);
    }

    public void testSerialization() {
        ComposeBinary<String,String,Integer,Date,String> f2 = makeSerial(func);
        assertEquals(BAZ, f2.fn(FOO, BAR));

        try {
            assertEquals(BAZ, f2.fn(BAR,FOO));
            String msg = "Expeced AssertionFailed when given BAR expecting FOO";
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
        junit.swingui.TestRunner.run(TestComposeBinary.class);
    }

    private class TestVisitor extends AbstractVisitor
            implements ComposeBinary.Visitor
    {
        public Object host;
        public void visit(ComposeBinary host) { this.host = host; }
    }
}

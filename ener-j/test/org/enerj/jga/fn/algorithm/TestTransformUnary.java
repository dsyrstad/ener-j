// ============================================================================
// $Id: TestTransformUnary.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

package org.enerj.jga.fn.algorithm;

import java.util.Iterator;
import java.util.Vector;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.Conditional;
import org.enerj.jga.fn.adaptor.ConstantUnary;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.util.EmptyIterator;

/**
 * Exercises TransformUnary
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestTransformUnary
    extends FunctorTest<TransformUnary<String,Integer>>
{
    public TestTransformUnary (String name){ super(name); }

    static private final String FOO = "_foo_";
    static private final String BAR = "_bar_";
    static private final String BAZ = "_baz_";
    static private final String QLX = "_qlx_";
    
    static private final Integer NEG = new Integer(-1);
    static private final Integer ZERO = new Integer(0);
    static private final Integer POS = new Integer(1);
    
    UnaryFunctor<String,Integer> uf =
        new Conditional<String,Integer>(new EqualTo<String>().bind2nd(FOO),
                                        new ConstantUnary<String,Integer>(NEG),
        new Conditional<String,Integer>(new EqualTo<String>().bind2nd(BAR),
                                        new ConstantUnary<String,Integer>(ZERO),
                                        new ConstantUnary<String,Integer>(POS)));
//         new UnaryFunctor<String,Integer>() {
//             public Integer fn(String str) {
//                 return str.equals(FOO) ? NEG : str.equals(BAR) ? ZERO  : POS;
//             }
//         };

    TransformUnary<String,Integer> xf =
        makeSerial(new TransformUnary<String,Integer>(uf));

    public void testTransformUnaryEmptyList() {
        Iterator<Integer> iter = xf.fn(new EmptyIterator<String>());
        assertFalse(iter.hasNext());
    }
     
    public void testTransformUnaryList() {
        Vector<String> v = new Vector<String>();
        v.add(FOO);
        v.add(BAR);
        v.add(BAZ);

        Iterator<Integer> iter = xf.fn(v.iterator());
        assertEquals(NEG, iter.next());
        assertEquals(ZERO,iter.next());
        assertEquals(POS, iter.next());
        assertFalse(iter.hasNext());
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        xf.accept(tv);
        assertEquals(xf, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements TransformUnary.Visitor
    {
        public Object host;
        public void visit(TransformUnary host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestTransformUnary.class);
    }
}

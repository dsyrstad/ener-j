// ============================================================================
// $Id: TestTransformBinary.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.comparison.Max;
import org.enerj.jga.util.EmptyIterator;
import org.enerj.jga.util.SingletonIterator;


/**
 * Exercises TransformBinary
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestTransformBinary
    extends FunctorTest<TransformBinary<Integer,Integer,Integer>>
{
    public TestTransformBinary (String name){ super(name); }

    BinaryFunctor<Integer,Integer,Integer> max = new Max.Comparable<Integer>();
    TransformBinary<Integer,Integer,Integer> xf = 
        makeSerial(new TransformBinary<Integer,Integer,Integer>(max));

    static private final Integer ZERO = new Integer(0);
    static private final Integer ONE  = new Integer(1);
    static private final Integer TWO  = new Integer(2);
    static private final Integer MAX  = new Integer(Integer.MAX_VALUE);
    static private final Integer MIN  = new Integer(Integer.MIN_VALUE);
    static private final Integer NEG  = new Integer(-1);
    
    public void testTransformBinaryEmptyList() {
        Iterator<Integer> iter =
            xf.fn(new EmptyIterator<Integer>(),
                  new SingletonIterator<Integer>(ZERO));
        
        assertFalse(iter.hasNext());
    }
     
    public void testTransformBinaryList() {
        Vector<Integer> v1 = new Vector<Integer>();
        v1.add(ZERO);
        v1.add(ONE);
        v1.add(NEG);

        Vector<Integer> v2 = new Vector<Integer>();
        v2.add(TWO);
        v2.add(MIN);
        v2.add(MAX);

        Iterator<Integer> iter = xf.fn(v1.iterator(),v2.iterator());
        assertEquals(TWO, iter.next());
        assertEquals(ONE,iter.next());
        assertEquals(MAX, iter.next());
        assertFalse(iter.hasNext());
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        xf.accept(tv);
        assertEquals(xf, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements TransformBinary.Visitor
    {
        public Object host;
        public void visit(TransformBinary host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestTransformBinary.class);
    }
}

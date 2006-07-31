// ============================================================================
// $Id: TestValueOf.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
// ============================================================================
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.fn.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises ValueOf
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestValueOf extends FunctorTest<ValueOf<Number,Long>> {
    public TestValueOf (String name){ super(name); }

    private ValueOf<Number,Byte> bfn        = new ValueOf<Number,Byte>(Byte.class);
    private ValueOf<Number,Short> sfn       = new ValueOf<Number,Short>(Short.class);
    private ValueOf<Number,Integer> ifn     = new ValueOf<Number,Integer>(Integer.class);
    private ValueOf<Number,Long> lfn        = new ValueOf<Number,Long>(Long.class);
//     private ValueOf<Number,Float> ffn       = new ValueOf<Number,Float>(Float.class);
//     private ValueOf<Number,Double> dfn      = new ValueOf<Number,Double>(Double.class);
    private ValueOf<Number,BigDecimal> bdfn = new ValueOf<Number,BigDecimal>(BigDecimal.class);
    private ValueOf<Number,BigInteger> bifn = new ValueOf<Number,BigInteger>(BigInteger.class);
    public void testFunctorInterface() {
        byte b0 = 3;
        short s0 = 3;
        Double pi = new Double(Math.PI);
        
        assertEquals(new Byte(b0), bfn.fn(pi));
        assertEquals(new Short(s0), sfn.fn(pi));
        assertEquals(new Integer(3), ifn.fn(pi));
        assertEquals(new Long(3L), lfn.fn(pi));
        assertEquals(new BigDecimal("3.14159"),
                     bdfn.fn(pi).setScale(5, BigDecimal.ROUND_HALF_DOWN));
        assertEquals(new BigInteger("3"),bifn.fn(pi));
//         assert(0.000000001F < Math.abs(1.23F - f0.floatValue()));
//         assert(0.000000001D < Math.abs(1.23D + d0.doubleValue()));

        ValueOf<Number,Long> lfn2 = makeSerial(new ValueOf<Number,Long>(Long.class));
        assertEquals(new Long(-123L), lfn2.fn(new Double(-123.456D)));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ValueOf.Visitor
    {
        public Object host;
        public void visit(ValueOf host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestValueOf.class);
    }
}

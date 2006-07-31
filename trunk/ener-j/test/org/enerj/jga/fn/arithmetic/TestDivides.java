// ============================================================================
// $Id: TestDivides.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
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
 * Exercises Divides
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestDivides extends FunctorTest<Divides<Long>> {
    public TestDivides (String name){ super(name); }

    private Divides<Byte> bfn    = new Divides<Byte>(Byte.class);
    private Divides<Short> sfn   = new Divides<Short>(Short.class);
    private Divides<Integer> ifn = new Divides<Integer>(Integer.class);
    private Divides<Long> lfn    = new Divides<Long>(Long.class);
    private Divides<Float> ffn   = new Divides<Float>(Float.class);
    private Divides<Double> dfn  = new Divides<Double>(Double.class);
    private Divides<BigDecimal> bdfn
                                = new Divides<BigDecimal>(BigDecimal.class);
    private Divides<BigInteger> bifn
                                = new Divides<BigInteger>(BigInteger.class);

    public void testFunctorInterface() {
        byte b0=-2, b1=17, b2=-8;
        Byte bresult = bfn.fn(new Byte(b1), new Byte(b2));
        assertEquals(new Byte(b0), bresult);
        
        short s0=-3, s1=465, s2=-123;
        Short sresult = sfn.fn(new Short(s1), new Short(s2));
        assertEquals(new Short(s0),sresult);

        Integer i0 = ifn.fn(new Integer(-465), new Integer(123));
        assertEquals(new Integer(-3), i0);

        Long l0 = lfn.fn(new Long(465L), new Long(-123L));
        assertEquals(new Long(-3L), l0);

        BigDecimal bd0 = bdfn.fn(new BigDecimal("4.56"),new BigDecimal("1.23"));
        assertEquals(new BigDecimal("3.71"), bd0);

        BigInteger bi0 = bifn.fn(new BigInteger("-465"), new BigInteger("123"));
        assertEquals(new BigInteger("-3"), bi0);
        
        Float f0 = ffn.fn(new Float(4.65F), new Float(1.23F));
        assert(0.000000001F < Math.abs(3.78049F - f0.floatValue()));

        Double d0 = dfn.fn(new Double(4.65D), new Double(1.23D));
        assert(0.000000001D < Math.abs(3.78049D - d0.doubleValue()));

        Divides<Long> lfn2 = makeSerial(new Divides<Long>(Long.class));
        assertEquals(new Long(-3L), lfn2.fn(new Long(465L), new Long(-123L)));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Divides.Visitor
    {
        public Object host;
        public void visit(Divides host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestDivides.class);
    }
}

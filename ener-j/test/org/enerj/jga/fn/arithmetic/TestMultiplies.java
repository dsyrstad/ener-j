// ============================================================================
// $Id: TestMultiplies.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
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
 * Exercises Multiplies
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestMultiplies extends FunctorTest<Multiplies<Long>> {
    public TestMultiplies (String name){ super(name); }

    private Multiplies<Byte> bfn    = new Multiplies<Byte>(Byte.class);
    private Multiplies<Short> sfn   = new Multiplies<Short>(Short.class);
    private Multiplies<Integer> ifn = new Multiplies<Integer>(Integer.class);
    private Multiplies<Long> lfn    = new Multiplies<Long>(Long.class);
    private Multiplies<Float> ffn   = new Multiplies<Float>(Float.class);
    private Multiplies<Double> dfn  = new Multiplies<Double>(Double.class);
    private Multiplies<BigDecimal> bdfn
                                = new Multiplies<BigDecimal>(BigDecimal.class);
    private Multiplies<BigInteger> bifn
                                = new Multiplies<BigInteger>(BigInteger.class);

    public void testFunctorInterface() {
        byte b0=-103, b1=17, b2=9;
        Byte bresult = bfn.fn(new Byte(b1), new Byte(b2));
        assertEquals(new Byte(b0), bresult);
        
        short s0=-9448, s1=123, s2=456;
        Short sresult = sfn.fn(new Short(s1), new Short(s2));
        assertEquals(new Short(s0), sresult);
        
        Integer i0 = ifn.fn(new Integer(123), new Integer(456));
        assertEquals(new Integer(56088), i0);
        
        Long l0 = lfn.fn(new Long(123L), new Long(456L));
        assertEquals(new Long(56088L), l0);

        BigDecimal bd0 = bdfn.fn(new BigDecimal("1.23"),new BigDecimal("4.56"));
        assertEquals(new BigDecimal("5.6088"), bd0);
        
        BigInteger bi0 = bifn.fn(new BigInteger("123"), new BigInteger("456"));
        assertEquals(new BigInteger("56088"), bi0);
        
        Float f0 = ffn.fn(new Float(1.23F), new Float(4.56F));
        assert(0.000000001F < Math.abs(5.6088F - f0.floatValue()));

        Double d0 = dfn.fn(new Double(1.23D), new Double(4.56D));
        assert(0.000000001D < Math.abs(5.6088D - d0.doubleValue()));

        Multiplies<Long> lfn2 = makeSerial(new Multiplies<Long>(Long.class));
        assertEquals(new Long(56088L), lfn.fn(new Long(123L), new Long(456L)));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Multiplies.Visitor
    {
        public Object host;
        public void visit(Multiplies host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestMultiplies.class);
    }
}

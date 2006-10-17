// ============================================================================
// $Id: TestNegate.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
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

/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/

public class TestNegate extends FunctorTest<Negate<Long>> {
    public TestNegate (String name){ super(name); }

    private Negate<Byte> bfn        = new Negate<Byte>(Byte.class);
    private Negate<Short> sfn       = new Negate<Short>(Short.class);
    private Negate<Integer> ifn     = new Negate<Integer>(Integer.class);
    private Negate<Long> lfn        = new Negate<Long>(Long.class);
    private Negate<Float> ffn       = new Negate<Float>(Float.class);
    private Negate<Double> dfn      = new Negate<Double>(Double.class);
    private Negate<BigDecimal> bdfn = new Negate<BigDecimal>(BigDecimal.class);
    private Negate<BigInteger> bifn = new Negate<BigInteger>(BigInteger.class);

    public void testFunctorInterface() {
        byte b0=10, b1=-10;
        Byte bresult = bfn.fn(new Byte(b1));
        assertEquals(new Byte(b0), bresult);
        
        short s0=579, s1=-579;
        Short sresult = sfn.fn(new Short(s1));
        assertEquals(new Short(s0), sresult);
        
        Integer i0 = ifn.fn(new Integer(123));
        assertEquals(new Integer(-123), i0);
        
        Long l0 = lfn.fn(new Long(-123L));
        assertEquals(new Long(123L), l0);
        
        BigDecimal bd0 = bdfn.fn(new BigDecimal("1.23"));
        assertEquals(new BigDecimal("-1.23"), bd0);
        
        BigInteger bi0 = bifn.fn(new BigInteger("-123"));
        assertEquals(new BigInteger("123"),bi0);

        Float f0 = ffn.fn(new Float(-1.23F));
        assert(0.000000001F < Math.abs(1.23F - f0.floatValue()));

        Double d0 = dfn.fn(new Double(1.23D));
        assert(0.000000001D < Math.abs(1.23D + d0.doubleValue()));

        Negate<Long> lfn2 = makeSerial(new Negate<Long>(Long.class));
        assertEquals(new Long(123L), lfn2.fn(new Long(-123L)));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Negate.Visitor
    {
        public Object host;
        public void visit(Negate host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestNegate.class);
    }
}

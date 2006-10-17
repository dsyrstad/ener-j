// ============================================================================
// $Id: TestMinus.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
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

public class TestMinus extends FunctorTest<Minus<Long>> {
    public TestMinus (String name){ super(name); }

    private Minus<Byte> bfn       = new Minus<Byte>(Byte.class);
    private Minus<Short> sfn      = new Minus<Short>(Short.class);
    private Minus<Integer> ifn    = new Minus<Integer>(Integer.class);
    private Minus<Long> lfn       = new Minus<Long>(Long.class);
    private Minus<Float> ffn      = new Minus<Float>(Float.class);
    private Minus<Double> dfn     = new Minus<Double>(Double.class);
    private Minus<BigDecimal>bdfn = new Minus<BigDecimal>(BigDecimal.class);
    private Minus<BigInteger>bifn = new Minus<BigInteger>(BigInteger.class);

    public void testFunctorInterface() {
        byte b0=123, b1=127, b2=4;
        Byte bresult = bfn.fn(new Byte(b1), new Byte(b2));
        assertEquals(new Byte(b0), bresult);
        
        short s0=123, s1=127, s2=4;
        Short sresult = sfn.fn(new Short(s1), new Short(s2));
        assertEquals(new Short(s0), sresult);

        Integer i0 = ifn.fn(new Integer(579), new Integer(456));
        assertEquals(new Integer(123), i0);
        
        Long l0 = lfn.fn(new Long(579L), new Long(456L));
        assertEquals(new Long(123L), l0);

        BigDecimal bd0 = bdfn.fn(new BigDecimal("5.79"),new BigDecimal("4.56"));
        assertEquals(new BigDecimal("1.23"), bd0);
        
        BigInteger bi0 = bifn.fn(new BigInteger("579"), new BigInteger("456"));
        assertEquals(new BigInteger("123"), bi0);

        Float f0 = ffn.fn(new Float(5.79F), new Float(4.56F));
        assert(0.000000001F < Math.abs(f0.floatValue()));

        Double d0 = dfn.fn(new Double(5.79D), new Double(4.56D));
        assert(0.000000001D < Math.abs(d0.doubleValue()));

        Minus<Long> lfn2 = makeSerial(new Minus<Long>(Long.class));
        assertEquals(new Long(123L), lfn.fn(new Long(579L), new Long(456L)));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Minus.Visitor
    {
        public Object host;
        public void visit(Minus host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestMinus.class);
    }
}

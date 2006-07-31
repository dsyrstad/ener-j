// ============================================================================
// $Id: TestBitwiseAnd.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
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
 * Exercises BitwiseAnd
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestBitwiseAnd extends FunctorTest<BitwiseAnd<Long>> {
    public TestBitwiseAnd (String name){ super(name); }

    private BitwiseAnd<Byte> bfn        = new BitwiseAnd<Byte>(Byte.class);
    private BitwiseAnd<Short> sfn       = new BitwiseAnd<Short>(Short.class);
    private BitwiseAnd<Integer> ifn     = new BitwiseAnd<Integer>(Integer.class);
    private BitwiseAnd<Long> lfn        = new BitwiseAnd<Long>(Long.class);
    private BitwiseAnd<BigInteger> bifn = new BitwiseAnd<BigInteger>(BigInteger.class);

    public void testFunctorInterface() {
        byte b0=0x11, b1=0x55, b2=0x33;
        Byte bresult = bfn.fn(new Byte(b1), new Byte(b2));
        assertEquals(new Byte(b0), bresult);
        
        short s0=0x111, s1=0x555, s2=0x333;
        Short sresult = sfn.fn(new Short(s1), new Short(s2));
        assertEquals(new Short(s0), sresult);
        
        Integer i0 = ifn.fn(new Integer(0xFFFF), new Integer(0x3333));
        assertEquals(new Integer(0x3333), i0);
        
        Long l0 = lfn.fn(new Long(0xEEEEEEEEL), new Long(0x76767676L));
        assertEquals(new Long(0x66666666L), l0);
        
        BigInteger bi0 = bifn.fn(new BigInteger("65535"), new BigInteger("456"));
        assertEquals(new BigInteger("456"),bi0);

        BitwiseAnd<Long> lfn2 = makeSerial(new BitwiseAnd<Long>(Long.class));
        assertEquals(new Long(123L & 456L), lfn2.fn(new Long(123L), new Long(456L)));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements BitwiseAnd.Visitor
    {
        public Object host;
        public void visit(BitwiseAnd host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestBitwiseAnd.class);
    }
}

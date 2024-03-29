// ============================================================================
// $Id: TestBitwiseNot.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
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

public class TestBitwiseNot extends FunctorTest<BitwiseNot<Long>> {
    public TestBitwiseNot (String name){ super(name); }

    private BitwiseNot<Byte> bfn        = new BitwiseNot<Byte>(Byte.class);
    private BitwiseNot<Short> sfn       = new BitwiseNot<Short>(Short.class);
    private BitwiseNot<Integer> ifn     = new BitwiseNot<Integer>(Integer.class);
    private BitwiseNot<Long> lfn        = new BitwiseNot<Long>(Long.class);
    private BitwiseNot<BigInteger> bifn = new BitwiseNot<BigInteger>(BigInteger.class);

    public void testFunctorInterface() {
        byte b0=-86, b1=85;
        Byte bresult = bfn.fn(new Byte(b1));
        assertEquals(new Byte(b0), bresult);
        
        short s0=-21846, s1=21845;
        Short sresult = sfn.fn(new Short(s1));
        assertEquals(new Short(s0), sresult);
        
        Integer i0 = ifn.fn(new Integer(0x0F0F0F0F));
        assertEquals(new Integer(0xF0F0F0F0), i0);
        
        Long l0 = lfn.fn(new Long(0x0F0F0F0F0F0F0F0FL));
        assertEquals(new Long(0xF0F0F0F0F0F0F0F0L), l0);
        
        BigInteger bi0 = bifn.fn(new BigInteger("65534"));
        assertEquals(new BigInteger("-65535"),bi0);

        BitwiseNot<Long> lfn2 = makeSerial(new BitwiseNot<Long>(Long.class));
        assertEquals(new Long(~123L), lfn2.fn(new Long(123L)));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements BitwiseNot.Visitor
    {
        public Object host;
        public void visit(BitwiseNot host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestBitwiseNot.class);
    }
}

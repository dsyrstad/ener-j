// ============================================================================
// $Id: TestBitwiseXor.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
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

public class TestBitwiseXor extends FunctorTest<BitwiseXor<Long>> {
    public TestBitwiseXor (String name){ super(name); }

    private BitwiseXor<Byte> bfn        = new BitwiseXor<Byte>(Byte.class);
    private BitwiseXor<Short> sfn       = new BitwiseXor<Short>(Short.class);
    private BitwiseXor<Integer> ifn     = new BitwiseXor<Integer>(Integer.class);
    private BitwiseXor<Long> lfn        = new BitwiseXor<Long>(Long.class);
    private BitwiseXor<BigInteger> bifn = new BitwiseXor<BigInteger>(BigInteger.class);

    public void testFunctorInterface() {
        byte b0=0x66, b1=0x55, b2=0x33;
        short s0=0x666, s1=0x555, s2=0x333;
        
        assertEquals(new Byte(b0), bfn.fn(new Byte(b1), new Byte(b2)));
        assertEquals(new Short(s0), sfn.fn(new Short(s1), new Short(s2)));
        assertEquals(new Integer(0xCCCC), ifn.fn(new Integer(0xFFFF), new Integer(0x3333)));
        assertEquals(new Long(0x98989898L), lfn.fn(new Long(0xEEEEEEEEL), new Long(0x76767676L)));
        assertEquals(new BigInteger("65079"),bifn.fn(new BigInteger("65535"),new BigInteger("456")));

        BitwiseXor<Long> lfn2 = makeSerial(new BitwiseXor<Long>(Long.class));
        assertEquals(new Long(123L ^ 456L), lfn2.fn(new Long(123L), new Long(456L)));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements BitwiseXor.Visitor
    {
        public Object host;
        public void visit(BitwiseXor host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestBitwiseXor.class);
    }
}

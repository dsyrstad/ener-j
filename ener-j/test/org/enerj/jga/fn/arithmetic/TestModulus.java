// ============================================================================
// $Id: TestModulus.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
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

public class TestModulus extends FunctorTest<Modulus<Long>> {
    public TestModulus (String name){ super(name); }

    private Modulus<Byte> bfn    = new Modulus<Byte>(Byte.class);
    private Modulus<Short> sfn   = new Modulus<Short>(Short.class);
    private Modulus<Integer> ifn = new Modulus<Integer>(Integer.class);
    private Modulus<Long> lfn    = new Modulus<Long>(Long.class);
    private Modulus<BigInteger> bifn
                                 = new Modulus<BigInteger>(BigInteger.class);

    public void testFunctorInterface() {
        byte  b0=1, b1=17, b2=-8;
        assertEquals(new Byte(b0),
                     bfn.fn(new Byte(b1), new Byte(b2)));
        
        short s0=96, s1=465, s2=-123;
        assertEquals(new Short(s0),
                     sfn.fn(new Short(s1), new Short(s2)));
        
        assertEquals(new Integer(-96),
                     ifn.fn(new Integer(-465), new Integer(123)));
        
        assertEquals(new Long(-96L),
                     lfn.fn(new Long(-465L), new Long(-123L)));
        
        assertEquals(new BigInteger("96"),
                     bifn.fn(new BigInteger("465"), new BigInteger("123")));

        Modulus<Long> lfn2 = makeSerial(new Modulus<Long>(Long.class));
        assertEquals(new Long(-96L), lfn2.fn(new Long(-465L), new Long(-123L)));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Modulus.Visitor
    {
        public Object host;
        public void visit(Modulus host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestModulus.class);
    }
}

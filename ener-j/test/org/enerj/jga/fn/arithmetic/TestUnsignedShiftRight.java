// ============================================================================
// $Id: TestUnsignedShiftRight.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
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

public class TestUnsignedShiftRight extends FunctorTest<UnsignedShiftRight<Long>> {
    public TestUnsignedShiftRight (String name){ super(name); }

    private UnsignedShiftRight<Byte> bfn        = new UnsignedShiftRight<Byte>(Byte.class);
    private UnsignedShiftRight<Short> sfn       = new UnsignedShiftRight<Short>(Short.class);
    private UnsignedShiftRight<Integer> ifn     = new UnsignedShiftRight<Integer>(Integer.class);
    private UnsignedShiftRight<Long> lfn        = new UnsignedShiftRight<Long>(Long.class);
    private UnsignedShiftRight<BigInteger> bifn = new UnsignedShiftRight<BigInteger>(BigInteger.class);

    public void testFunctorInterface() {
        Integer THREE = new Integer(3);
        byte b0=96, b1=12;
        short s0=1088, s1=136;
        
        assertEquals(new Long(0x9999999999999999L>>>3),lfn.fn(new Long(0x9999999999999999L),THREE));
        assertEquals(new Integer(0xBBBBBBBB >>> 3), ifn.fn(new Integer(0xBBBBBBBB), THREE));
        assertEquals(new Short(s1), sfn.fn(new Short(s0), THREE));
        assertEquals(new Byte(b1), bfn.fn(new Byte(b0), THREE));
        
        UnsignedShiftRight<Long> lfn2 = makeSerial(new UnsignedShiftRight<Long>(Long.class));
        assertEquals(new Long(4456448L >>> 3), lfn.fn(new Long(4456448L), THREE));

        try {
            assertEquals(new BigInteger("69632"), bifn.fn(new BigInteger("557056"), THREE));
            fail("Not supposed to support unsigned shift for BigInteger");
        }
        catch (UnsupportedOperationException x) {
            // Expecting this result
        }
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements UnsignedShiftRight.Visitor
    {
        public Object host;
        public void visit(UnsignedShiftRight host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestUnsignedShiftRight.class);
    }
}

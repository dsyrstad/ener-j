// ============================================================================
// $Id: TestShiftLeft.java,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
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

public class TestShiftLeft extends FunctorTest<ShiftLeft<Long>> {
    public TestShiftLeft (String name){ super(name); }

    private ShiftLeft<Byte> bfn        = new ShiftLeft<Byte>(Byte.class);
    private ShiftLeft<Short> sfn       = new ShiftLeft<Short>(Short.class);
    private ShiftLeft<Integer> ifn     = new ShiftLeft<Integer>(Integer.class);
    private ShiftLeft<Long> lfn        = new ShiftLeft<Long>(Long.class);
    private ShiftLeft<BigInteger> bifn = new ShiftLeft<BigInteger>(BigInteger.class);

    public void testFunctorInterface() {
        Integer THREE = new Integer(3);
        byte b0=96, b1=12;
        short s0=1088, s1=136;
        
        assertEquals(new Byte(b0), bfn.fn(new Byte(b1), THREE));
        assertEquals(new Short(s0), sfn.fn(new Short(s1), THREE));
        assertEquals(new Integer(8704), ifn.fn(new Integer(1088), THREE));
        assertEquals(new Long(69632L), lfn.fn(new Long(8704L), THREE));
        assertEquals(new BigInteger("557056"), bifn.fn(new BigInteger("69632"), THREE));
        
        ShiftLeft<Long> lfn2 = makeSerial(new ShiftLeft<Long>(Long.class));
        assertEquals(new Long(4456448L), lfn.fn(new Long(557056L), THREE));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ifn.accept(tv);
        assertEquals(ifn, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ShiftLeft.Visitor
    {
        public Object host;
        public void visit(ShiftLeft host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestShiftLeft.class);
    }
}

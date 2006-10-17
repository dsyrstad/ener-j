// ============================================================================
// $Id: TestCast.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
// ============================================================================
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.fn.property;

import java.math.BigDecimal;
import java.util.Date;
import org.enerj.jga.DerivedObject;
import org.enerj.jga.SampleObject;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.EvaluationException;
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

public class TestCast extends FunctorTest<Cast<BigDecimal>> {
    public TestCast (String name){ super(name); }

    static public final Integer ONE = new Integer(1);
    static public final BigDecimal PRICE = new BigDecimal("29.99");
    
    private Cast<BigDecimal> cast = makeSerial(new Cast<BigDecimal>(BigDecimal.class));
    
    public void testSample() {
        Number num = PRICE;
        BigDecimal price = cast.fn(num);
        assertEquals(PRICE, price);

        try {
            cast.fn(ONE);
            fail("Expected ClassCastException passing Integer to Cast<BigDecimal>.fn()");
        }
        catch (ClassCastException x) {
            // This cast is expected -- ONE is not a BigDecimal
        }
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        cast.accept(tv);
        assertEquals(cast, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Cast.Visitor
    {
        public Object host;
        public void visit(Cast host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestCast.class);
    }
}

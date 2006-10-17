// ============================================================================
// $Id: TestConstruct.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
package org.enerj.jga.fn.property;

import java.math.BigDecimal;
import java.math.BigInteger;
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
public class TestConstruct extends FunctorTest<Construct<BigDecimal>> {
    public TestConstruct (String name){
        super(name);
    }

    private Class[] types;
    private Construct<BigDecimal> ctor;

    public void setUp() {
        types = new Class[1];
        types[0] = String.class;

        ctor = new Construct<BigDecimal>(types, BigDecimal.class);
    }

    public void tearDown() {}
    

    public void testConstructBigDecimal() {
        Object[] args = new Object[1]; args[0] = "12345.678";
        BigDecimal bd1 = makeSerial(ctor).fn(args);
        assertEquals(new BigDecimal(new BigInteger("12345678"), 3), bd1);
        try {
            args[0] = "";
            ctor.fn(args);
            fail("Should have thrown EvaluationException w/ NumberFormatException cargo");
        }
        catch (EvaluationException x) {
        }
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ctor.accept(tv);
        assertEquals(ctor, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Construct.Visitor
    {
        public Object host;
        public void visit(Construct host) { this.host = host; }
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestConstruct.class);
    }
}

// ============================================================================
// $Id: TestConstructUnary.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import org.enerj.jga.fn.property.ConstructUnary;

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
public class TestConstructUnary
    extends FunctorTest<ConstructUnary<String,BigDecimal>>
{
    public TestConstructUnary (String name){
        super(name);
    }

    private ConstructUnary<String,BigDecimal> ctor =
        new ConstructUnary<String,BigDecimal>(String.class,BigDecimal.class);
    
    public void testConstructBigDecimal() {
        BigDecimal bd1 = makeSerial(ctor).fn("12345.678");
        assertEquals(new BigDecimal(new BigInteger("12345678"), 3), bd1);
        try {
            ctor.fn("");
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
            implements ConstructUnary.Visitor
    {
        public Object host;
        public void visit(ConstructUnary host) { this.host = host; }
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestConstructUnary.class);
    }
}

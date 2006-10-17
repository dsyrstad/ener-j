// ============================================================================
// $Id: TestGreaterEqual.java,v 1.4 2006/01/12 23:36:19 dsyrstad Exp $
// Copyright (c) 2002  David A. Hall
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

package org.enerj.jga.fn.comparison;

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

public class TestGreaterEqual extends FunctorTest<GreaterEqual<Integer>> {
    public TestGreaterEqual (String name){ super(name); }

    private Integer ZERO = new Integer(0);
    private Integer ONE  = new Integer(1);
    
    private GreaterEqual<Integer> pred = makeSerial(new GreaterEqual.Comparable<Integer>());
    
    public void testFunctorInterface() {
        assertEquals(Boolean.FALSE, pred.fn(ZERO, ONE));
        assertEquals(Boolean.TRUE,  pred.fn(ONE,  ZERO));
        assertEquals(Boolean.TRUE,  pred.fn(ZERO, ZERO));
        assertEquals(Boolean.FALSE, pred.fn(ZERO, null));
        assertEquals(Boolean.FALSE, pred.fn(null, ZERO));
        assertEquals(Boolean.TRUE, pred.fn(null, null));
    }
    
    public void testPredicateInterface() {
        assertTrue(!pred.p(ZERO, ONE));
        assertTrue( pred.p(ONE,  ZERO));
        assertTrue( pred.p(ZERO, ZERO));
        assertTrue(!pred.p(ZERO, null));
        assertTrue(!pred.p(null, ZERO));
        assertTrue( pred.p(null, null));
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        pred.accept(tv);
        assertEquals(pred, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements GreaterEqual.Visitor
    {
        public Object host;
        public void visit(GreaterEqual host) { this.host = host; }
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestGreaterEqual.class);
    }
}

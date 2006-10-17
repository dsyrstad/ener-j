// ============================================================================
// $Id: TestApplyBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.adaptor;

import org.enerj.jga.SampleBinaryFunctor;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.BinaryFunctor;
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

public class TestApplyBinary extends FunctorTest<ApplyBinary<String,String>>
{
    public TestApplyBinary (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";

        BinaryFunctor<String,String,String> bf1 =
            new SampleBinaryFunctor<String,String,String>(FOO,BAR,FOO);
        BinaryFunctor<String,String,String> bf2 =
            new SampleBinaryFunctor<String,String,String>(FOO,BAR,BAR);
        BinaryFunctor<String,String,String> bf3 =
            new SampleBinaryFunctor<String,String,String>(FOO,BAR,BAZ);
        
        BinaryFunctor[] functors =
            new BinaryFunctor[]{ bf1,bf2,bf3 };
    
        private ApplyBinary<String,String> makeApply =
            makeSerial(new ApplyBinary<String,String>(functors));
    
    public void testFunctorInterface() {
        Object[] result = makeApply.fn(FOO, BAR);
        assertEquals(FOO, result[0]);
        assertEquals(BAR, result[1]);
        assertEquals(BAZ, result[2]);
        assertEquals(3, result.length);
    }

    public void testEmptyArgList() {
        BinaryFunctor[] noFns = new BinaryFunctor[] {};
        ApplyBinary<String,String> fn = new ApplyBinary<String,String>(noFns);
        Object[] result = fn.fn(FOO,BAR);
        assertEquals(0, result.length);
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        makeApply.accept(tv);
        assertEquals(makeApply, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ApplyBinary.Visitor
    {
        public Object host;
        public void visit(ApplyBinary host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestApplyBinary.class);
    }
}

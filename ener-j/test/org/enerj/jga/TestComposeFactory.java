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
// ============================================================================
// $Id: TestComposeFactory.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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
package org.enerj.jga;

import junit.framework.TestCase;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.Bind2nd;
import org.enerj.jga.fn.adaptor.ComposeUnary;
import org.enerj.jga.fn.comparison.Greater;
import org.enerj.jga.fn.comparison.Less;
import org.enerj.jga.fn.logical.LogicalAnd;


/**
 * TestComposeFactory.java
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 * @version
 */

public class TestComposeFactory extends TestCase {
    public TestComposeFactory (String name){ super(name); }

    static public final Integer ZERO = new Integer(0);
    static public final Integer TEN  = new Integer(10);

    public UnaryFunctor<Integer,Boolean> uf1 =
        new LogicalAnd().compose(new Greater.Comparable<Integer>().bind2nd(ZERO),
                                 new Less.Comparable<Integer>().bind2nd(TEN));

    public UnaryFunctor<Integer,Boolean> uf2 =
        new ComposeUnary<Integer,Boolean,Boolean,Boolean> (
            new Bind2nd<Integer,Integer,Boolean>(ZERO, new Greater.Comparable<Integer>()),
            new Bind2nd<Integer,Integer,Boolean>(TEN,  new Less.Comparable<Integer>()),
            new LogicalAnd());
    
    public UnaryFunctor<Integer,Boolean> uf3;

    public void setUp() {
        Bind2nd<Integer,Integer,Boolean> a =
            new Bind2nd<Integer,Integer,Boolean>( ZERO, new Greater.Comparable<Integer>());
        Bind2nd<Integer,Integer,Boolean> b =
            new Bind2nd<Integer,Integer,Boolean>( TEN,  new Less.Comparable<Integer>());
        uf3 = new ComposeUnary<Integer,Boolean,Boolean,Boolean>(a,b,new LogicalAnd());
    }

    public void tearDown() {
    }
    
    public void testAlternatives() {
        Integer five = new Integer(5);
        assertTrue(uf1.fn(five).booleanValue());
        assertTrue(uf2.fn(five).booleanValue());
        assertTrue(uf3.fn(five).booleanValue());

        Integer fifteen = new Integer(15);
        assertFalse(uf1.fn(fifteen).booleanValue());
        assertFalse(uf2.fn(fifteen).booleanValue());
        assertFalse(uf3.fn(fifteen).booleanValue());

        Integer negfive = new Integer(-5);
        assertFalse(uf1.fn(negfive).booleanValue());
        assertFalse(uf2.fn(negfive).booleanValue());
        assertFalse(uf3.fn(negfive).booleanValue());
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestComposeFactory.class);
    }
}

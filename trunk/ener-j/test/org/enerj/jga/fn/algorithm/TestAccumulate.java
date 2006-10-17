// ============================================================================
// $Id: TestAccumulate.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

package org.enerj.jga.fn.algorithm;

import java.util.Vector;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.adaptor.Project1st;
import org.enerj.jga.fn.adaptor.Project2nd;

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

public class TestAccumulate extends FunctorTest<Accumulate<String>> {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";

    public TestAccumulate (String name){ super(name); }

    Vector<String> list = new Vector<String>();

    Project1st<String,String> first = new Project1st<String,String>();
    Project2nd<String,String> second = new Project2nd<String,String>();

    public void setUp() {
        list.add(FOO);
        list.add(BAR);
        list.add(BAZ);
        list.add(FOO);
        list.add(BAR);
        list.add(BAZ);
    }

    public void tearDown() {
    }

    public void testAccumulateEmptyList() {
        Accumulate<String> accumulator = new Accumulate<String>(first);
        assertNull(accumulator.fn(new Vector<String>().iterator()));
    }
     
    public void testAccumulateEmptyListWithValue() {
        Accumulate<String> accumulator = new Accumulate<String>(FOO,first);
        assertEquals(FOO,accumulator.fn(new Vector<String>().iterator()));
    }
     
    public void testAccumulateList() {
        Accumulate<String> accumulator = new Accumulate<String>(first);
        assertEquals(FOO,accumulator.fn(list.iterator()));

        accumulator = new Accumulate<String>(second);
        assertEquals(BAZ,accumulator.fn(list.iterator()));
    }

    public void testAccumulateListValue() {
        Accumulate<String> accumulator = new Accumulate<String>(BAR,first);
        assertEquals(BAR,accumulator.fn(list.iterator()));

        accumulator = new Accumulate<String>(BAR, second);
        assertEquals(BAZ,accumulator.fn(list.iterator()));
    }
    
    public void testSerialization() {
        Accumulate<String> accumulator =
            makeSerial(new Accumulate<String>(BAR,first));
        assertEquals(BAR,accumulator.fn(list.iterator()));
    }
    
    
    public void testVisitableInterface() {
        Accumulate<String> accumulator = new Accumulate<String>(FOO,first);
        TestVisitor tv = new TestVisitor();
        accumulator.accept(tv);
        assertEquals(accumulator, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Accumulate.Visitor
    {
        public Object host;
        public void visit(Accumulate host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestAccumulate.class);
    }
}

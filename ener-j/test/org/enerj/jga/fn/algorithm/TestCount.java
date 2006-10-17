// ============================================================================
// $Id: TestCount.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.Iterator;
import java.util.Vector;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.Visitor;
import org.enerj.jga.fn.comparison.EqualEqual;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.util.Algorithms;

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

public class TestCount extends FunctorTest<Count<String>> {
    
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";

    static public UnaryFunctor<String,Boolean> eqBar =
        new EqualTo<String>().bind2nd(BAR);
    
    public TestCount (String name){ super(name); }

    Vector<String> list = new Vector<String>();

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

    public void testCountEmptyList() {
        Count<String> counter = new Count<String>(BAR);
        assertEquals(0L,counter.fn(new Vector<String>().iterator()).longValue());
    }
     
    public void testCountList() {
        Count<String> fooCounter = new Count<String>(FOO);
        assertEquals(2L, fooCounter.fn(list.iterator()).longValue());
        
        Count<String> barCounter = new Count<String>(eqBar);
        assertEquals(2L, barCounter.fn(list.iterator()).longValue());

        Count<String> qlxCounter =
            new Count<String>(new EqualEqual<String>(), QLX);
        
        assertEquals(0L, qlxCounter.fn(list.iterator()).longValue());

        Iterator<? extends String> iter = Algorithms.find(list, FOO);
        iter.next();
        assertEquals(1L, fooCounter.fn(iter).longValue());
    }
    
    public void testSerialization() {
        Count<String> fooCounter = makeSerial(new Count<String>(FOO));
        assertEquals(2L, fooCounter.fn(list.iterator()).longValue());
    }
        
    public void testVisitableInterface() {
        Count<String> counter = new Count<String>(BAR);
        TestVisitor tv = new TestVisitor();
        counter.accept(tv);
        assertEquals(counter, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Count.Visitor
    {
        public Object host;
        public void visit(Count host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestCount.class);
    }
}

// ============================================================================
// $Id: TestConditionalBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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

import junit.framework.AssertionFailedError;
import org.enerj.jga.SampleBinaryFunctor;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.Visitable;
import org.enerj.jga.fn.Visitor;

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

public class TestConditionalBinary extends FunctorTest<ConditionalBinary<String,String,String>> {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    
    public TestConditionalBinary (String name){ super(name); }

    private SampleBinaryFunctor<String,String,Boolean> testPasses =
            new SampleBinaryFunctor<String,String,Boolean>(FOO, BAR, Boolean.TRUE);
    
    private SampleBinaryFunctor<String,String,Boolean> testFails =
            new SampleBinaryFunctor<String,String,Boolean>(FOO, BAR, Boolean.FALSE);
    
    private SampleBinaryFunctor<String,String,String> trueFn =
            new SampleBinaryFunctor<String,String,String>(FOO, BAR, BAR);

    private SampleBinaryFunctor<String,String,String> falseFn =
            new SampleBinaryFunctor<String,String,String>(FOO, BAR, BAZ);
    
    public void testTruePath() {
        ConditionalBinary<String,String,String> c =
            new ConditionalBinary<String,String,String> (testPasses, trueFn, falseFn);

        assertEquals(BAR, c.fn(FOO,BAR));
        assertEquals(FOO, testPasses._gotX);
        assertEquals(BAR, testPasses._gotY);
        assertEquals(FOO, trueFn._gotX);
        assertEquals(BAR, trueFn._gotY);
        assertNull(falseFn._gotX);
        assertNull(falseFn._gotY);
    }

    public void testFalsePath() {
        ConditionalBinary<String,String,String> c =
            new ConditionalBinary<String,String,String> (testFails, trueFn, falseFn);

        assertEquals(BAZ, c.fn(FOO,BAR));
        assertEquals(FOO, testFails._gotX);
        assertEquals(BAR, testFails._gotY);
        assertNull(trueFn._gotX);
        assertNull(trueFn._gotY);
        assertEquals(FOO, falseFn._gotX);
        assertEquals(BAR, falseFn._gotY);
    }
    
    public void testSerialization() {
        ConditionalBinary<String,String,String> c = 
            makeSerial(new ConditionalBinary<String,String,String>(testPasses,trueFn,falseFn));
        assertEquals(BAR, c.fn(FOO,BAR));

        try {
            assertEquals(BAR, c.fn(BAZ,BAR));
            String msg = "Expeced AssertionFailed when given BAZ expecting FOO";
            fail(msg);
        }
        catch (AssertionFailedError x) {}
    }
    
    public void testVisitableInterface() {
        ConditionalBinary<String,String,String> c =
            new ConditionalBinary<String,String,String> (testPasses, trueFn, falseFn);
        
        TestVisitor tv = new TestVisitor();
        c.accept(tv);
        assertEquals(c, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ConditionalBinary.Visitor
    {
        public Object host;
        public void visit(ConditionalBinary host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestConditionalBinary.class);
    }
}

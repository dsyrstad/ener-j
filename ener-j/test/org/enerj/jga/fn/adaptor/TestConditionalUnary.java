// ============================================================================
// $Id: TestConditionalUnary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
import org.enerj.jga.SampleUnaryFunctor;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.Visitable;
import org.enerj.jga.fn.Visitor;

/**
 * Exercises ConditionalUnary
 *
 * <p>Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestConditionalUnary extends FunctorTest<ConditionalUnary<String,String>> {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    
    public TestConditionalUnary (String name){ super(name); }

    private SampleUnaryFunctor<String,Boolean> testPasses =
            new SampleUnaryFunctor<String,Boolean>(FOO, Boolean.TRUE);
    
    private SampleUnaryFunctor<String,Boolean> testFails =
            new SampleUnaryFunctor<String,Boolean>(FOO, Boolean.FALSE);
    
    private SampleUnaryFunctor<String,String> trueFn =
            new SampleUnaryFunctor<String,String>(FOO, BAR);

    private SampleUnaryFunctor<String,String> falseFn =
            new SampleUnaryFunctor<String,String>(FOO, BAZ);
    
    public void testTruePath() {
        ConditionalUnary<String,String> c =
            new ConditionalUnary<String,String> (testPasses, trueFn, falseFn);

        assertEquals(BAR, c.fn(FOO));
        assertEquals(FOO, testPasses._got);
        assertEquals(FOO, trueFn._got);
        assertNull(falseFn._got);
    }

    public void testFalsePath() {
        ConditionalUnary<String,String> c =
            new ConditionalUnary<String,String> (testFails, trueFn, falseFn);

        assertEquals(BAZ, c.fn(FOO));
        assertEquals(FOO, testFails._got);
        assertNull(trueFn._got);
        assertEquals(FOO, falseFn._got);
    }
    
    public void testSerialization() {
        ConditionalUnary<String,String> c = 
            makeSerial(new ConditionalUnary<String,String>(testPasses,trueFn,falseFn));
        assertEquals(BAR, c.fn(FOO));

        try {
            assertEquals(BAR, c.fn(BAZ));
            String msg = "Expeced AssertionFailed when given BAZ expecting FOO";
            fail(msg);
        }
        catch (AssertionFailedError x) {}
    }
    
    public void testVisitableInterface() {
        ConditionalUnary<String,String> c =
            new ConditionalUnary<String,String> (testPasses, trueFn, falseFn);
        
        TestVisitor tv = new TestVisitor();
        c.accept(tv);
        assertEquals(c, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ConditionalUnary.Visitor
    {
        public Object host;
        public void visit(ConditionalUnary host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestConditionalUnary.class);
    }
}

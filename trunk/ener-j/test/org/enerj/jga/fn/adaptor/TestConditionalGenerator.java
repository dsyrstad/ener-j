// ============================================================================
// $Id: TestConditionalGenerator.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
import org.enerj.jga.SampleGenerator;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.Visitable;
import org.enerj.jga.fn.Visitor;

/**
 * Exercises ConditionalGenerator
 *
 * <p>Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestConditionalGenerator extends FunctorTest<ConditionalGenerator<String>> {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    
    public TestConditionalGenerator (String name){ super(name); }

    private SampleGenerator<Boolean> testPasses = new SampleGenerator<Boolean>(Boolean.TRUE);
    private SampleGenerator<Boolean> testFails = new SampleGenerator<Boolean>(Boolean.FALSE);    
    private SampleGenerator<String> trueFn = new SampleGenerator<String>(BAR);
    private SampleGenerator<String> falseFn = new SampleGenerator<String>(BAZ);
    
    public void testTruePath() {
        ConditionalGenerator<String> c = new ConditionalGenerator<String>(testPasses,trueFn,falseFn);

        assertEquals(BAR, c.gen());
        assertEquals(1, testPasses._count);
        assertEquals(1, trueFn._count);
        assertEquals(0, falseFn._count);
    }

    public void testFalsePath() {
        ConditionalGenerator<String> c = new ConditionalGenerator<String>(testFails,trueFn,falseFn);

        assertEquals(BAZ, c.gen());
        assertEquals(1, testFails._count);
        assertEquals(0, trueFn._count);
        assertEquals(1, falseFn._count);
    }
    
    public void testSerialization() {
        ConditionalGenerator<String> c =
            makeSerial(new ConditionalGenerator<String>(testPasses,trueFn,falseFn));
        
        assertEquals(BAR, c.gen());
    }
    
    public void testVisitableInterface() {
        ConditionalGenerator<String> c =
            new ConditionalGenerator<String> (testPasses, trueFn, falseFn);
        
        TestVisitor tv = new TestVisitor();
        c.accept(tv);
        assertEquals(c, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ConditionalGenerator.Visitor
    {
        public Object host;
        public void visit(ConditionalGenerator host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestConditionalGenerator.class);
    }
}

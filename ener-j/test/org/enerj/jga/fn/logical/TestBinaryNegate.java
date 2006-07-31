// ============================================================================
// $Id: TestBinaryNegate.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

package org.enerj.jga.fn.logical;

import org.enerj.jga.SampleBinaryFunctor;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises TestBinaryNegate.java
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestBinaryNegate extends FunctorTest<BinaryNegate<String,String>> {
    
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    
    public TestBinaryNegate (String name){ super(name); }

    private SampleBinaryFunctor<String,String,Boolean> f =
        new SampleBinaryFunctor<String,String,Boolean>(FOO,BAR,Boolean.FALSE);
    
    private SampleBinaryFunctor<String,String,Boolean> t =
        new SampleBinaryFunctor<String,String,Boolean>(FOO,BAR,Boolean.TRUE);

    private BinaryNegate<String,String> _false = 
        makeSerial(new BinaryNegate<String,String>(t));
    
    private BinaryNegate<String,String>
        _true = new BinaryNegate<String,String>(f);
    
    public void testFunctorInterface() {
        assertEquals(Boolean.TRUE, _true.fn(FOO, BAR));
        assertEquals(Boolean.FALSE, _false.fn(FOO, BAR));
    }
    
    public void testPredicateInterface() {
        assertTrue(  _true.p(FOO, BAR));
        assertTrue(!_false.p(FOO, BAR));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        _true.accept(tv);
        assertEquals(_true, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements BinaryNegate.Visitor
    {
        public Object host;
        public void visit(BinaryNegate host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestBinaryNegate.class);
    }
}

// ============================================================================
// $Id: TestUnaryNegate.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import org.enerj.jga.SampleUnaryFunctor;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises TestUnaryNegate.java
 *
 * Created: Sun Apr 14 01:37:38 2002
 *
 * <p>Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestUnaryNegate extends FunctorTest<UnaryNegate<String>> {
    public TestUnaryNegate (String name){ super(name); }

    static private final String FOO = "_foo_";
    
    private SampleUnaryFunctor<String,Boolean> f =
        new SampleUnaryFunctor<String,Boolean>(FOO, Boolean.FALSE);
    
    private SampleUnaryFunctor<String,Boolean> t =
        new SampleUnaryFunctor<String,Boolean>(FOO, Boolean.TRUE);
    
    private UnaryNegate<String> _false = makeSerial(new UnaryNegate<String>(t));
    
    private UnaryNegate<String> _true = new UnaryNegate<String>(f);
    
    public void testFunctorInterface() {
        assertEquals(Boolean.FALSE, _false.fn(FOO));
        assertEquals(Boolean.TRUE, _true.fn(FOO));
    }
    
    public void testPredicateInterface() {
        assertTrue( _true.p(FOO));
        assertTrue(!_false.p(FOO));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        _true.accept(tv);
        assertEquals(_true, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements UnaryNegate.Visitor
    {
        public Object host;
        public void visit(UnaryNegate host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestUnaryNegate.class);
    }
}

// ============================================================================
// $Id: TestEqualEqual.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

/**
 * Exercises EqualEqual predicate
 *
 *
 * Created: Sun Apr 14 01:37:38 2002
 *
 * <p>Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestEqualEqual extends FunctorTest<EqualEqual<String>> {
    public TestEqualEqual (String name){ super(name); }

    private EqualEqual<String> pred = makeSerial(new EqualEqual<String>());

    static private final String ONE = "ONE";
    static private final String TWO = "TWO";

    private String TOO;
    
    public void setUp() {
        TOO = "W";
        TOO = "T" + TOO;
        TOO += "O";
    }
    
    public void testFunctorInterface() {
        assertEquals(Boolean.TRUE,  pred.fn(ONE, ONE));
        assertEquals(Boolean.FALSE, pred.fn(ONE, TWO));
        assertEquals(Boolean.FALSE, pred.fn(TWO, ONE));
        assertEquals(Boolean.FALSE, pred.fn(TWO, null));
        assertEquals(Boolean.FALSE, pred.fn(null,TWO));
        assertEquals(Boolean.FALSE, pred.fn(TWO, TOO));
        assertEquals(Boolean.TRUE,  pred.fn(null,null));
    }
    
    public void testPredicateInterface() {
        assertTrue( pred.p(ONE, ONE));
        assertTrue(!pred.p(ONE, TWO));
        assertTrue(!pred.p(TWO, ONE));
        assertTrue(!pred.p(TWO, null));
        assertTrue(!pred.p(null,TWO));
        assertTrue(!pred.p(TWO, TOO));
        assertTrue( pred.p(null,null));
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        pred.accept(tv);
        assertEquals(pred, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements EqualEqual.Visitor
    {
        public Object host;
        public void visit(EqualEqual host) { this.host = host; }
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestEqualEqual.class);
    }
}

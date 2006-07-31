// ============================================================================
// $Id: TestLogicalAnd.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.io.IOException;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises TestLogicalAnd
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestLogicalAnd extends FunctorTest<LogicalAnd> {
    public TestLogicalAnd (String name){ super(name); }

    private LogicalAnd pred = makeSerial(new LogicalAnd());
    
    public void testFunctorInterface() {
        assertEquals(Boolean.TRUE,  pred.fn(Boolean.TRUE,  Boolean.TRUE));
        assertEquals(Boolean.FALSE, pred.fn(Boolean.TRUE,  Boolean.FALSE));
        
        // No longer appropriate: the implementation returns arg1 & arg2,
        // instead of arg1 && arg2
//         assertEquals(Boolean.FALSE, pred.fn(Boolean.FALSE,(Boolean)null));
    }
    
    public void testPredicateInterface() {
        assertTrue( pred.p(Boolean.TRUE,  Boolean.TRUE));
        assertTrue(!pred.p(Boolean.TRUE,  Boolean.FALSE));
        // No longer appropriate: the implementation returns arg1 & arg2,
        // instead of arg1 && arg2
//         assertTrue(!pred.p(Boolean.FALSE,(Boolean)null));
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        pred.accept(tv);
        assertEquals(pred, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements LogicalAnd.Visitor
    {
        public Object host;
        public void visit(LogicalAnd host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestLogicalAnd.class);
    }
}

// ============================================================================
// $Id: TestIdentity.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.adaptor;

import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises TestIdentity.java
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestIdentity extends FunctorTest<Identity<String>> {
    public TestIdentity (String name){ super(name); }

    private Identity<String> func = makeSerial(new Identity<String>());
    
    public void testFunctorInterface() {
        assertNull(func.arg());
        assertNull(func.fn(null));
        assertNull(func.arg());
        
        String testString = "_TEST_STRING_";
        assertEquals(testString, func.fn(testString));
        assertEquals(testString, func.arg());
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        func.accept(tv);
        assertEquals(func, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements Identity.Visitor
    {
        public Object host;
        public void visit(Identity host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestIdentity.class);
    }
}

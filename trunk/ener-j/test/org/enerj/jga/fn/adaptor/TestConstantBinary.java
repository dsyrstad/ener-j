// ============================================================================
// $Id: TestConstantBinary.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import org.enerj.jga.SampleObject;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises ConstantBinary
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

// as if _this_ could ever fail 

public class TestConstantBinary
    extends FunctorTest<ConstantBinary<Object,Boolean,String>>
{
    static public final String FOO = "_foo_";
    
    public TestConstantBinary (String name){ super(name); }

    private ConstantBinary<Object,Boolean,String> func = 
        makeSerial(new ConstantBinary<Object,Boolean,String>(FOO));
    
    public void testFunctorInterface() {
        assertEquals(FOO, func.fn(null, null));

        String testString = "_TEST_STRING_";
        assertEquals(FOO, func.fn(testString, Boolean.TRUE));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        func.accept(tv);
        assertEquals(func, tv.host);
    }

    private class TestVisitor extends AbstractVisitor 
            implements ConstantBinary.Visitor
    {
        public Object host;
        public void visit(ConstantBinary host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestConstantBinary.class);
    }
}

// ============================================================================
// $Id: TestConstructDefault.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
// ============================================================================
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================
package org.enerj.jga.fn.property;

import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.FunctorTest;

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
public class TestConstructDefault extends FunctorTest<ConstructDefault<String>> {
    public TestConstructDefault (String name){
        super(name);
    }

    private ConstructDefault<String> ctor =
            new ConstructDefault<String>(String.class);

    public void testConstructString() {
        String bd1 = makeSerial(ctor).gen();
        assertEquals("", bd1);
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ctor.accept(tv);
        assertEquals(ctor, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ConstructDefault.Visitor
    {
        public Object host;
        public void visit(ConstructDefault host) { this.host = host; }
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestConstructDefault.class);
    }
}

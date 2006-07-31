// ============================================================================
// $Id: TestForEach.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

package org.enerj.jga.fn.algorithm;

import java.util.Vector;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.adaptor.Identity;

/**
 * Exercises Find
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestForEach extends FunctorTest<ForEach<String,String>> {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";

    public TestForEach (String name){ super(name); }

    Identity<String> id = new Identity<String>();

    public ForEach<String,String> accumulator =
        makeSerial(new ForEach<String,String>(id));
    
    public void testForEachEmptyList() {
        assertNull(accumulator.fn(new Vector<String>().iterator()));
    }
     
    public void testForEachList() {
        Vector<String> list = new Vector<String>();
        list.add(FOO);
        list.add(BAR);
        list.add(BAZ);
        list.add(FOO);
        list.add(BAR);
        list.add(BAZ);

        assertEquals(BAZ,accumulator.fn(list.iterator()));
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        accumulator.accept(tv);
        assertEquals(accumulator, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ForEach.Visitor
    {
        public Object host;
        public void visit(ForEach host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestForEach.class);
    }
}

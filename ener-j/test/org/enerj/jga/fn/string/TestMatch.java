// ============================================================================
// $Id: TestMatch.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
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
package org.enerj.jga.fn.string;

import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises Match
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */
public class TestMatch extends FunctorTest<Match> {
    public TestMatch (String name){
        super(name);
    }
    
    public void testMatchEmpty() {
        assertTrue(new Match().p(""));
        assertTrue(new Match((String)null).p(""));

        try{
            assertTrue(new Match().p(null));
            fail("Expecting NullPointerException on null argument");
        }
        catch(NullPointerException x) {}
    }

    public void testMatch() {
        Match m1 = makeSerial(new Match("A*B*"));
        assertTrue(m1.p(""));
        assertTrue(m1.p("AB"));
        assertFalse(m1.p("ABC"));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        Match matcher = new Match((String)null);
        matcher.accept(tv);
        assertEquals(matcher, tv.host);
    }
    
    private class TestVisitor extends AbstractVisitor
            implements Match.Visitor
    {
        public Object host;
        public void visit(Match host) { this.host = host; }
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestMatch.class);
    }
}

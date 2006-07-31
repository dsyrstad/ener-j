// ============================================================================
// $Id: TestDefaultFormat.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
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
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.FunctorTest;
import java.util.Date;

/**
 * Exercises DefaultFormat
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */
public class TestDefaultFormat extends FunctorTest<DefaultFormat<Float>> {
    public TestDefaultFormat (String name){
        super(name);
    }

    static public final Float F123e_m21 = new Float(123e-21F);

    public void testFormatNumber() {
        DefaultFormat<Float> formatter = makeSerial(new DefaultFormat<Float>());

        assertEquals("1.23E-19", formatter.fn(F123e_m21));
    }

     public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        DefaultFormat<Date> formatter = new DefaultFormat<Date>();
            
        formatter.accept(tv);
        assertEquals(formatter, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements DefaultFormat.Visitor
    {
        public Object host;
        public void visit(DefaultFormat host) { this.host = host; }
    }

   public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestDefaultFormat.class);
    }
}

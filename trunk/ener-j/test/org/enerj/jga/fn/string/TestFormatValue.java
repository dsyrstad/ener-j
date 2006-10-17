// ============================================================================
// $Id: TestFormatValue.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
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
public class TestFormatValue extends FunctorTest<FormatValue<Long>> {
    public TestFormatValue (String name){
        super(name);
    }

    static public final Long L12345 = new Long(12345L);

    public void testFormatNumber() {
        FormatValue<Long> formatter = 
            makeSerial(new FormatValue<Long>(NumberFormat.getNumberInstance()));

        assertEquals("12,345", formatter.fn(L12345));
    }

     public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        FormatValue<Date> formatter =
            new FormatValue<Date>(DateFormat.getTimeInstance());
            
        formatter.accept(tv);
        assertEquals(formatter, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements FormatValue.Visitor
    {
        public Object host;
        public void visit(FormatValue host) { this.host = host; }
    }

   public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestFormatValue.class);
    }
}

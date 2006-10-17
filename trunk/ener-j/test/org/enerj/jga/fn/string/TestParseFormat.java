// ============================================================================
// $Id: TestParseFormat.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
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

import java.awt.Color;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.FunctorTest;
import org.enerj.jga.fn.string.ParseFormat;

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
public class TestParseFormat extends FunctorTest<ParseFormat<Short>> {
    public TestParseFormat (String name){
        super(name);
    }

    private short s1234 = (short) 1234;
    private Short S1234 = new Short(s1234);
    private Long L1234 = new Long(1234);
    
    private NumberFormat format = NumberFormat.getNumberInstance();

    public void testParseNumber() {
        ParseFormat<Short> parser = makeSerial(new ParseFormat.Number<Short>(Short.class,format));
        Short sresult = parser.fn("1234.5678");
        assertEquals(S1234, sresult);

        NumberFormat intf = NumberFormat.getNumberInstance();
        intf.setParseIntegerOnly(true);
        parser =  new ParseFormat.Number<Short>(Short.class, intf);
        sresult = parser.fn("1234.5678");
        assertEquals(S1234, sresult);
    }

    public void testParseDate() {
        Date date = new GregorianCalendar(2003, 00, 01).getTime();
        DateFormat d84mat = DateFormat.getDateInstance(DateFormat.SHORT,Locale.US);
        ParseFormat<Date> parser0 = new ParseFormat.Date<Date>(Date.class,d84mat);
        Date dresult = parser0.fn("01/01/2003");
        assertEquals(date, dresult);

        ParseFormat<Timestamp> parser1 = new ParseFormat.Date<Timestamp>(Timestamp.class,d84mat);
        Timestamp tresult = parser1.fn("01/01/2003");
        assertEquals(date, tresult);
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        ParseFormat.Date<Date> parser =
            new ParseFormat.Date<Date>(Date.class,DateFormat.getTimeInstance());

        parser.accept(tv);
        assertEquals(parser, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements ParseFormat.Visitor
    {
        public Object host;
        public void visit(ParseFormat host) { this.host = host; }
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestParseFormat.class);
    }
}

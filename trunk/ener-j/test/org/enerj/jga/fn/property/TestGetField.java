// ============================================================================
// $Id: TestGetField.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

package org.enerj.jga.fn.property;

import java.math.BigDecimal;
import java.util.Date;
import org.enerj.jga.DerivedObject;
import org.enerj.jga.SampleObject;
import org.enerj.jga.Samples;
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

public class TestGetField extends FunctorTest<GetField<SampleObject,Integer>>
{
    public TestGetField (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final Integer ONE = new Integer(1);
    static public final Date NOW = new Date();
    static public final Date EPOCH = new Date(0L);
    static private final int COUNT = 21;
    static private final BigDecimal PRICE = new BigDecimal("15.99");
    
    private GetField<SampleObject,String>
        getName = new GetField<SampleObject,String>(SampleObject.class,"_name");
    private GetField<SampleObject,Date>
        getDate = new GetField<SampleObject,Date>(SampleObject.class,"_date");
    private GetField<SampleObject,Integer> 
        getCount = makeSerial(new GetField<SampleObject,Integer>(SampleObject.class,"_count"));
    
    public void testFunctorInterface() {
        SampleObject obj = new SampleObject(FOO, COUNT, PRICE, NOW);
        assertEquals(FOO, getName.fn(obj));
        assertEquals(NOW, getDate.fn(obj));
        assertEquals(COUNT, makeSerial(getCount).fn(obj).intValue());
    }

    public void testDerivedUsage() {
        DerivedObject obj = new DerivedObject(FOO, COUNT, PRICE, NOW);
        assertEquals(FOO, getName.fn(obj));
        assertEquals(NOW, getDate.fn(obj));
        assertEquals(COUNT, makeSerial(getCount).fn(obj).intValue());
    }

    public void testAccessControl() {
        SampleObject obj = new SampleObject(FOO, COUNT, PRICE, NOW);
        try {
            GetField<SampleObject,Object>
                getDetail = new GetField<SampleObject,Object>(SampleObject.class, "_detail");
            fail("Shouldn't be able to acecss \"Detail\" field");
        }
        catch(IllegalArgumentException x) {
            // Expecting to catch IllegalArgument
        }
    }

    public void testClassParm() {
        GetField<SampleObject,String>
            getNameCl = new GetField<SampleObject,String>(SampleObject.class, "_name");

        SampleObject obj1 = new SampleObject(FOO, COUNT);
        assertEquals(FOO, getNameCl.fn(obj1));
        
        DerivedObject obj2 = new DerivedObject(BAR, 0);
        assertEquals(BAR, getNameCl.fn(obj2));
    }

    public void testFieldNames() {
        assertEquals("_name", getName.getFieldName());
        assertEquals("_date", getDate.getFieldName());
        assertEquals("_count",getCount.getFieldName());
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        getName.accept(tv);
        assertEquals(getName, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements GetField.Visitor
    {
        public Object host;
        public void visit(GetField host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestGetField.class);
    }
}

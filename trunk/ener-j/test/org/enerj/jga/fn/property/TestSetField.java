// ============================================================================
// $Id: TestSetField.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

package org.enerj.jga.fn.property;

import java.math.BigDecimal;
import java.util.Date;
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

public class TestSetField
    extends FunctorTest<SetField<SampleObject,Integer>>
{
    public TestSetField (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final Integer ZERO = new Integer(0);
    static public final Integer ONE = new Integer(1);
    static public final Date NOW = new Date();
    static public final Date EPOCH = new Date(0L);
    static private final int COUNT = 21;
    static private final BigDecimal PRICE = new BigDecimal("15.99");
    
    private SetField<SampleObject,String> setName =
        new SetField<SampleObject,String>(SampleObject.class,"_name", String.class);
    private SetField<SampleObject,Date> setDate =
        new SetField<SampleObject,Date>(SampleObject.class,"_date", Date.class);
    private SetField<SampleObject,Integer> setCount =
        makeSerial(new SetField<SampleObject,Integer>(SampleObject.class, "_count", Integer.class));

    public void testFunctorInterface() {
        SampleObject obj = new SampleObject(FOO, COUNT, PRICE, NOW);
        assertEquals(null, setName.fn(obj, BAR));
        assertEquals(BAR, obj.getName());
        
        assertEquals(null, setDate.fn(obj, EPOCH));
        assertEquals(EPOCH, obj.getDate());
        
        assertEquals(null, setCount.fn(obj, ZERO));
        assertEquals(ZERO, obj.getCount()/*.intValue()*/);
    }
    
    public void testSetFnClCtor() {
        SampleObject obj = new SampleObject(FOO, COUNT, PRICE, NOW);
        SetField<SampleObject,String> setNameCl =
            new SetField<SampleObject,String>(SampleObject.class, "_name", String.class);
        assertEquals(null, setNameCl.fn(obj,BAR));
        assertEquals(BAR, obj.getName());
    }
    
    public void testFieldNames() {
        assertEquals("_name", setName.getFieldName());
        assertEquals("_date", setDate.getFieldName());
        assertEquals("_count",setCount.getFieldName());
    }
    
    public void testAccessControl() {
        SampleObject obj = new SampleObject(FOO, COUNT, PRICE, NOW);
        try {
            SetField<SampleObject,Object> getDetail =
                new SetField<SampleObject,Object>(SampleObject.class, "Detail", Object.class);
            fail("Shouldn't be able to acecss \"Detail\" property");
        }
        catch(IllegalArgumentException x) {
            // Expecting to catch IllegalArgument
        }
    }

    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        setName.accept(tv);
        assertEquals(setName, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements SetField.Visitor
    {
        public Object host;
        public void visit(SetField host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestSetField.class);
    }
}

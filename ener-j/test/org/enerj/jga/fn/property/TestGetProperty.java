// ============================================================================
// $Id: TestGetProperty.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

/**
 * Exercises GetProperty functor
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestGetProperty extends FunctorTest<GetProperty<SampleObject,Integer>> {
    public TestGetProperty (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final Integer ONE = new Integer(1);
    static public final Date NOW = new Date();
    static public final Date EPOCH = new Date(0L);
    static private final int COUNT = 21;
    static private final BigDecimal PRICE = new BigDecimal("15.99");
    
    private GetProperty<SampleObject,String>
        getName = new GetProperty<SampleObject,String>(SampleObject.class,"Name");
    private GetProperty<SampleObject,Date>
        getDate = new GetProperty<SampleObject,Date>(SampleObject.class,"getDate");
    private GetProperty<SampleObject,Integer> 
        getCount = makeSerial(new GetProperty<SampleObject,Integer>(SampleObject.class,"Count"));
    
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
            GetProperty<SampleObject,Object>
                getDetail = new GetProperty<SampleObject,Object>(SampleObject.class, "Detail");
            fail("Shouldn't be able to acecss \"Detail\" property");
        }
        catch(IllegalArgumentException x) {
            // Expecting to catch IllegalArgument
        }
    }

    public void testClassParm() {
        GetProperty<SampleObject,String>
            getNameCl = new GetProperty<SampleObject,String>(SampleObject.class, "Name");

        SampleObject obj1 = new SampleObject(FOO, COUNT);
        assertEquals(FOO, getNameCl.fn(obj1));
        
        DerivedObject obj2 = new DerivedObject(BAR, 0);
        assertEquals(BAR, getNameCl.fn(obj2));
    }

    public void testPropertyNames() {
        assertEquals("Name", getName.getPropertyName());
        assertEquals("Date", getDate.getPropertyName());
        assertEquals("Count",getCount.getPropertyName());
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        getName.accept(tv);
        assertEquals(getName, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements GetProperty.Visitor
    {
        public Object host;
        public void visit(GetProperty host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestGetProperty.class);
    }
}

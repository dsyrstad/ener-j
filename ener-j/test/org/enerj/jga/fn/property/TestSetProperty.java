// ============================================================================
// $Id: TestSetProperty.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

/**
 * Exercises SetProperty functor
 *
 * <p>Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestSetProperty extends FunctorTest<SetProperty<SampleObject,Integer>> {
    public TestSetProperty (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final Integer ZERO = new Integer(0);
    static public final Integer ONE = new Integer(1);
    static public final Date NOW = new Date();
    static public final Date EPOCH = new Date(0L);
    static private final int COUNT = 21;
    static private final BigDecimal PRICE = new BigDecimal("15.99");
    
    private SetProperty<SampleObject,String> setName =
        new SetProperty<SampleObject,String>(SampleObject.class,"Name",
                                             String.class);
    private SetProperty<SampleObject,Date> setDate =
        new SetProperty<SampleObject,Date>(SampleObject.class,"setDate",
                                           Date.class);
    private SetProperty<SampleObject,Integer> setCount =
        makeSerial(new SetProperty<SampleObject,Integer>(SampleObject.class,
                                                        "Count",Integer.class));

    public void testFunctorInterface() {
        SampleObject obj = new SampleObject(FOO, COUNT, PRICE, NOW);
        assertEquals(null, setName.fn(obj, BAR));
        assertEquals(BAR, obj.getName());
        
        assertEquals(NOW, setDate.fn(obj, EPOCH));
        assertEquals(EPOCH, obj.getDate());
        
        assertEquals(null, setCount.fn(obj, ZERO));
        assertEquals(ZERO, obj.getCount()/*.intValue()*/);
    }
    
    public void testSetFnClCtor() {
        SampleObject obj = new SampleObject(FOO, COUNT, PRICE, NOW);
        SetProperty<SampleObject,String> setNameCl =
            new SetProperty<SampleObject,String>(SampleObject.class,
                                                 "Name",String.class);
        assertEquals(null, setNameCl.fn(obj,BAR));
        assertEquals(BAR, obj.getName());
    }
    
    public void testPropertyNames() {
        assertEquals("Name", setName.getPropertyName());
        assertEquals("Date", setDate.getPropertyName());
        assertEquals("Count",setCount.getPropertyName());
    }
    
    public void testAccessControl() {
        SampleObject obj = new SampleObject(FOO, COUNT, PRICE, NOW);
        try {
            SetProperty<SampleObject,Object> getDetail =
                new SetProperty<SampleObject,Object>(SampleObject.class, "Detail", Object.class);
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
            implements SetProperty.Visitor
    {
        public Object host;
        public void visit(SetProperty host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestSetProperty.class);
    }
}

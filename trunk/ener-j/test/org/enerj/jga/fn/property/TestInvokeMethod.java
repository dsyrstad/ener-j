// ============================================================================
// $Id: TestInvokeMethod.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import org.enerj.jga.DerivedObject;
import org.enerj.jga.SampleObject;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises InvokeMethod functor
 *
 * <p>Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestInvokeMethod extends FunctorTest<InvokeMethod<DerivedObject,BigDecimal>> {
    
    public TestInvokeMethod (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final Integer ZERO = new Integer(0);
    static public final Integer ONE = new Integer(1);
    static public final Date NOW = new Date();
    static public final Date EPOCH = new Date(0L);
    static private final int COUNT = 21;
    static private final BigDecimal PRICE = new BigDecimal("15.99");
    static private final BigDecimal TAX = new BigDecimal("0.80");

    private InvokeMethod<DerivedObject,String> setName;
    private InvokeMethod<DerivedObject,Date> setDate;
    private InvokeMethod<SampleObject,Integer> setCount;
    private InvokeMethod<DerivedObject,BigDecimal> setPrice;
    private InvokeMethod<DerivedObject,String> toStringMeth;

    public void setUp() throws NoSuchMethodException {
        Class[] strArg = new Class[1]; strArg[0] = String.class;    
        setName = new InvokeMethod<DerivedObject,String>(DerivedObject.class, "setName", strArg);
        
        Class[] dateArg = new Class[1]; dateArg[0] = Date.class;
        Method dateMeth = DerivedObject.class.getMethod("setDate", dateArg);
        setDate = new InvokeMethod<DerivedObject,Date>(DerivedObject.class, dateMeth);

        Class[] intArg = new Class[1]; intArg[0] = Integer.class;
        Method countMeth = DerivedObject.class.getMethod("setCount", intArg);
        setCount = new InvokeMethod<SampleObject,Integer>(SampleObject.class, countMeth);
        
        Class[] decArg = new Class[1]; decArg[0] = BigDecimal.class;    
        setPrice = new InvokeMethod<DerivedObject,BigDecimal>(DerivedObject.class,"setPrice",decArg);
        
        toStringMeth =
            new InvokeMethod<DerivedObject,String>(DerivedObject.class, "toString", new Class[0]);
    }

    public void tearDown() {}
    
    public void testFunctorInterface() {
        DerivedObject obj = new DerivedObject(FOO, COUNT, PRICE, NOW);
        
        Object[] arg = new Object[1]; arg[0] = BAR;
        assertEquals(null, setName.fn(obj, arg));
        assertEquals(BAR, obj.getName());

        arg[0] = EPOCH;
        assertEquals(NOW, setDate.fn(obj, arg));
        assertEquals(EPOCH, obj.getDate());

        arg[0] = ZERO;
        assertEquals(null, setCount.fn(obj, arg));
        assertEquals(ZERO, obj.getCount());

        arg[0] = TAX;
        assertEquals(obj, makeSerial(setPrice).fn(obj, arg));
        assertEquals(TAX, obj.getPrice());

        Object noarg[]= new Object[0];
    }
    
    public void testMethodNames() {
        assertEquals("setName", setName.getMethodName());
        assertEquals("setDate", setDate.getMethodName());
        assertEquals("setCount",setCount.getMethodName());
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        setName.accept(tv);
        assertEquals(setName, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements InvokeMethod.Visitor
    {
        public Object host;
        public void visit(InvokeMethod host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestInvokeMethod.class);
    }
}

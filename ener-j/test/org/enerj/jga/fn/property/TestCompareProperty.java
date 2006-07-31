// ============================================================================
// $Id: TestCompareProperty.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import org.enerj.jga.SampleBinaryFunctor;
import org.enerj.jga.SampleObject;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises CompareProperty functor
 *
 * <p>Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestCompareProperty
    extends FunctorTest<CompareProperty<SampleObject,String>>
{
    public TestCompareProperty (String name){ super(name); }

    static private final String FOO = "_foo_";
    static private final String BAR = "_bar_";
    static private final int COUNT = 21;
    static private final BigDecimal PRICE = new BigDecimal("15.99");
    static private final Date NOW = new Date();
    static private final Date EPOCH = new Date(0L);
    
    private SampleBinaryFunctor<String,String,Boolean> nameEqFoo =
        new SampleBinaryFunctor<String,String,Boolean>(FOO, FOO, Boolean.TRUE);

    private SampleBinaryFunctor<String,String,Boolean> nameNeFoo =
        new SampleBinaryFunctor<String,String,Boolean>(FOO, BAR, Boolean.FALSE);

    private CompareProperty<SampleObject,String> testNameFoo =
        new CompareProperty<SampleObject,String>(SampleObject.class, "Name",
                                                 nameEqFoo, FOO);

    private CompareProperty<SampleObject,String> testNameBar =
        new CompareProperty<SampleObject,String>(SampleObject.class,"Name",
                                                 nameNeFoo, BAR);

    private SampleObject _foo = new SampleObject(FOO, COUNT, PRICE, NOW);
    private SampleObject _bar =
                      new SampleObject(BAR, 99, new BigDecimal("24.00"), EPOCH);
    
    private CompareProperty<SampleObject,String> testNameEqFoo =
        new CompareProperty<SampleObject,String>(SampleObject.class,"Name",FOO);

    private CompareProperty<SampleObject,String> testNameEqBar =
        makeSerial(new CompareProperty<SampleObject,String>(SampleObject.class,
                                                            "Name", BAR));

    public void testFunctorInterface() {
        assertEquals(Boolean.TRUE, testNameFoo.fn(_foo));
        assertEquals(FOO, nameEqFoo._gotX);
        assertEquals(FOO, nameEqFoo._gotY);
        
        assertEquals(Boolean.FALSE,testNameBar.fn(_foo));
        assertEquals(FOO, nameNeFoo._gotX);
        assertEquals(BAR, nameNeFoo._gotY);
    }
    
    public void testClassCtor() {
        CompareProperty<SampleObject,String> testNameEqFoo2 =
            new CompareProperty<SampleObject,String>(SampleObject.class, "Name",
                                                     FOO);

        assertEquals(Boolean.TRUE, testNameEqFoo2.fn(_foo));
    }
    
    public void testPredicateInterface() {
        assertTrue(testNameFoo.p(_foo));
        assertEquals(FOO, nameEqFoo._gotX);
        assertEquals(FOO, nameEqFoo._gotY);
        
        assertTrue(!testNameBar.p(_foo));
        assertEquals(FOO, nameNeFoo._gotX);
        assertEquals(BAR, nameNeFoo._gotY);
    }
    
    public void testEqFunctorInterface() {
        assertEquals(Boolean.TRUE, testNameEqFoo.fn(_foo));
        assertEquals(Boolean.FALSE,testNameEqBar.fn(_foo));
    }
    
    public void testEqPredicateInterface() {
        assertTrue(testNameEqFoo.p(_foo));
        assertTrue(!testNameEqBar.p(_foo));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        testNameEqFoo.accept(tv);
        assertEquals(testNameEqFoo, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements CompareProperty.Visitor
    {
        public Object host;
        public void visit(CompareProperty host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestCompareProperty.class);
    }
}

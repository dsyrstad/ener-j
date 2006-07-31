// ============================================================================
// $Id: TestInstanceOf.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.FunctorTest;

/**
 * Exercises InstanceOf functor
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestInstanceOf extends FunctorTest<InstanceOf<SampleObject>> {
    public TestInstanceOf (String name){ super(name); }

    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final Integer ONE = new Integer(1);
    static public final Date NOW = new Date();
    static public final Date EPOCH = new Date(0L);
    static private final int COUNT = 21;
    static private final BigDecimal PRICE = new BigDecimal("15.99");
    
    private InstanceOf<SampleObject> isSample =
        makeSerial(new InstanceOf<SampleObject>(SampleObject.class));
    
    public void testSample() {
        SampleObject obj = new SampleObject(FOO,COUNT,PRICE,NOW);
        DerivedObject der = new DerivedObject(FOO,COUNT,PRICE,NOW);
        assert(isSample.p(obj));
        assert(isSample.p(der));
    }

    public void testDerived() {
        InstanceOf<SampleObject> isDerived =
            new InstanceOf<SampleObject>(DerivedObject.class);
        SampleObject obj = new SampleObject(FOO,COUNT,PRICE,NOW);
        DerivedObject der = new DerivedObject(FOO,COUNT,PRICE,NOW);
        assert(!isDerived.p(obj));
        assert(isDerived.p(der));
    }
    
    public void testVisitableInterface() {
        TestVisitor tv = new TestVisitor();
        isSample.accept(tv);
        assertEquals(isSample, tv.host);
    }

    private class TestVisitor extends AbstractVisitor
            implements InstanceOf.Visitor
    {
        public Object host;
        public void visit(InstanceOf host) { this.host = host; }
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestInstanceOf.class);
    }
}

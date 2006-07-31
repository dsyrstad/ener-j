// ============================================================================
// $Id: TestIterators.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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

package org.enerj.jga.util;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import junit.framework.TestCase;
import org.enerj.jga.DerivedObject;
import org.enerj.jga.SampleObject;
import org.enerj.jga.SampleObjectComparator;
import org.enerj.jga.Samples;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.Visitor;
import org.enerj.jga.fn.adaptor.ConstantUnary;
import org.enerj.jga.fn.adaptor.Identity;
import org.enerj.jga.fn.arithmetic.Minus;
import org.enerj.jga.fn.arithmetic.Plus;
import org.enerj.jga.fn.comparison.Between;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.fn.comparison.Equality;
import org.enerj.jga.fn.comparison.GreaterEqual;
import org.enerj.jga.fn.comparison.Less;
import org.enerj.jga.fn.comparison.Less;
import org.enerj.jga.fn.comparison.Max;
import org.enerj.jga.fn.comparison.Min;
import org.enerj.jga.fn.logical.BinaryNegate;
import org.enerj.jga.fn.logical.LogicalAnd;
import org.enerj.jga.fn.property.ConstructUnary;
import org.enerj.jga.fn.property.GetProperty;
import org.enerj.jga.fn.string.Match;
import org.enerj.jga.util.Iterators;


/**
 * Exercises Iterators
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestIterators extends TestCase  {
    public TestIterators (String name){ super(name); }
    
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
    
    static public BinaryFunctor<String,String,Boolean> bothStartWith_b =
        new LogicalAnd().distribute(new Match("_b.*"), new Match("_b.*"));

    static public final long HRS = 60L * 60L * 1000L;
    static public final long DAYS = 24L * HRS;

    static public final Date NOW = new Date();
    static public final Date EPOCH = new Date(0L);
    static public final Date DAY_1 = new Date(DAYS);

    static public final Time TIME_0 = new Time(0L);
    
    static public UnaryFunctor<Date,Boolean> isEpochDay =
        new LogicalAnd().compose(new GreaterEqual.Comparable<Date>().bind2nd(EPOCH),
                                 new Less.Comparable<Date>().bind2nd(DAY_1));
    
    static public GetProperty<Date,Long> getTime =
        new GetProperty<Date,Long>(Date.class,"Time");

    static public UnaryFunctor<Date,Date> nextDay =
        new ConstructUnary<Long,Date>(Long.TYPE,Date.class)
                .compose(new Plus<Long>(Long.class)
                        .compose(getTime, new ConstantUnary<Date,Long>(new Long(DAYS))));
    
    static public BinaryFunctor<Date,Date,Boolean> within24Hrs =
        new Between.Comparable<Long>(new Long(-DAYS), new Long(DAYS))
                .compose(new Minus<Long>(Long.class)
                        .distribute(getTime, getTime));
    
    static public BinaryFunctor<Date,Date,Boolean> within1Hr =
        new Between.Comparable<Long>(new Long(-HRS), new Long(HRS))
                .compose(new Minus<Long>(Long.class)
                        .distribute(getTime, getTime));
    
    static public final Integer NEG = new Integer(-1);
    static public final Integer ZERO = new Integer(0);
    static public final Integer POS = new Integer(1);

    static public final SampleObject WIDGETS = new SampleObject("WIDGETS",42);
    static public final SampleObject MUMBLES = new SampleObject("mumblers",65);
    static public final SampleObject GIZMOS  = new SampleObject("GIZMOS",0);
    static public final SampleObject MOREWIDGETS =
                                               new SampleObject("WIDGETS",100);

    static public final DerivedObject DOODADS =
                          new DerivedObject("DOODADS", -32);
    static public final DerivedObject DOOHICKEYS =
                          new DerivedObject("DOOHICKEYS",Integer.MAX_VALUE);
    static public final DerivedObject DUDS =
                          new DerivedObject("DUDS", Integer.MIN_VALUE);
    static public final SampleObject SAMPLE_DUD =
                          new SampleObject("DUDS", Integer.MIN_VALUE);

    static public final SampleObjectComparator soComp =
                          new SampleObjectComparator();

    // ==============================
    // Iterators.find(Iterator,Value)
    // ==============================

    public void testFind() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);      
        v1.add(new Time(999999L));
        v1.add(new Date(-99999L)); 
        v1.add(TIME_0);
        v1.add(new Date(999999L));
        v1.add(new Time(-99999L));

        // FindIterator<Date>, Time
        FindIterator<Date> finder = new FindIterator<Date>(v1.iterator());
        Iterator<Date> iter1 = Iterators.find(finder,new Time(999999L));
        assertEquals(new Time(999999L), iter1.next());
        assertEquals(new Time(-99999L), iter1.next());

        Date d = iter1.next();
        assertTrue(d instanceof Time);
        assertEquals(d, EPOCH);

        iter1 = Iterators.find(iter1, new Date(-99999L));
        assertEquals(iter1.next(), new Time(-99999L));
        assertFalse(iter1.hasNext());                              

        // Iterator<? extends Date>, Time
        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 = Iterators.find(v2.iterator(),
                                                    new Time(999999L));
        
        assertEquals(new Time(999999L), iter2.next());
        assertEquals(new Time(-99999L), iter2.next());

        d = iter2.next();
        assertTrue(d instanceof Time);
        assertEquals(d, EPOCH);

        iter2 = Iterators.find(iter2, new Date(-99999L));
        assertEquals(iter2.next(), new Time(-99999L));
        assertFalse(iter2.hasNext());                              

        // FindIterator<Time>, Date
        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);      
        v3.add(new Time(999999L));
        v3.add(new Time(-99999L)); 
        v3.add(TIME_0);
        v3.add(new Time(999999L));
        v3.add(new Time(-99999L));

        FindIterator<Time> finder3 = new FindIterator<Time>(v3.iterator());
        Iterator<Date> iter3 = Iterators.find(finder3, new Date(999999L));
        assertEquals(new Time(999999L), iter3.next());
        assertEquals(new Time(-99999L), iter3.next());
               
        iter3 = Iterators.find(iter3, new Date(-99999L));
        assertEquals(iter3.next(), new Time(-99999L));
        assertFalse(iter3.hasNext());                              
    }

    // =================================
    // Iterators.find(Iterator,Equality)
    // =================================

    public void testFindEq() {
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        v1.add(DOODADS);    
        v1.add(DOOHICKEYS); 
        v1.add(DUDS);       

        // FindIterator<Sample>, Derived, EqualTo<Sample>
        FindIterator<SampleObject> finder1 =
            new FindIterator<SampleObject>(v1.iterator());
        Iterator<SampleObject> iter1 =
            Iterators.find(finder1, DOOHICKEYS, eq);
        
        assertEquals(DOOHICKEYS, iter1.next());
        assertEquals(DUDS, iter1.next());
        assertFalse(iter1.hasNext());

        // Iterator<? extends Sample>, Derived, EqualTo<Sample>
        Vector<? extends SampleObject> v2 = v1;
        Iterator<SampleObject> iter2 =
            Iterators.find(v2.iterator(), DOOHICKEYS, eq);
        
        assertEquals(DOOHICKEYS, iter2.next());
        assertEquals(DUDS, iter2.next());
        assertFalse(iter2.hasNext());

        // Iterator<Derived>, Derived, EqualTo<Sample>
        Vector<DerivedObject> v3 = new Vector<DerivedObject>();
        v3.add(DOODADS);    
        v3.add(DOOHICKEYS); 
        v3.add(DUDS);       

        Iterator<SampleObject> iter3 =
            Iterators.find(v3.iterator(), DOOHICKEYS, eq);
        
        assertEquals(DOOHICKEYS, iter3.next());
        assertEquals(DUDS, iter3.next());
        assertFalse(iter3.hasNext());

        // NOTE: to get Iterator<Derived> from Iterator<Derived>, Derived, the
        // Equality must be Equality<Derived> (can't be Equality<Sample>

        // Equality<DerivedObject> eq2 = new EqualComp<DerivedObject>(soComp);

    }

    // ================================
    // Iterators.find(Iterator,Functor)
    // ================================
    
    public void testFindFn() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Time(System.currentTimeMillis()));
        v1.add(NOW);
        v1.add(EPOCH);
        v1.add(new Time(1L));

        // FindIterator<Date>, Functor<Date>
        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        Iterator<Date> iter1 = Iterators.find(finder1, isEpochDay);
        
        assertEquals(TIME_0, iter1.next());
        assertEquals(new Date(1L), iter1.next());
        assertFalse(iter1.hasNext());                              

        // Iterator<? extends Date>, Functor<Date>
        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 = Iterators.find(v2.iterator(), isEpochDay);
        
        assertEquals(TIME_0, iter2.next());
        assertEquals(new Date(1L), iter2.next());
        assertFalse(iter2.hasNext());                              

        // FindIterator<Time>, Functor<Date>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(System.currentTimeMillis()));
        v3.add(new Time(-42L));
        v3.add(TIME_0);
        v3.add(new Time(3L));

        FindIterator<Time> finder3 = new FindIterator<Time>(v3.iterator());
        Iterator<Date> iter3 = Iterators.find(finder3, isEpochDay);
        
        assertEquals(TIME_0, iter3.next());
        assertEquals(new Date(3L), iter3.next());
        assertFalse(iter3.hasNext());                              
    }

    // ===============================
    // Iterators.count(Iterator,Value)
    // ===============================

    public void testCount() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);      
        v1.add(new Time(999999L));
        v1.add(new Date(-99999L)); 
        v1.add(TIME_0);
        v1.add(new Date(999999L));
        v1.add(new Time(-99999L));

        // FindIterator<Date>, Date
        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        assertEquals(2, Iterators.count(finder1, new Date(999999L)));

        // Iterator<? extends Date, Date
        Vector<? extends Date> v2 = v1;
        assertEquals(2, Iterators.count(v2.iterator(), new Date(-99999L)));

        // FindIterator<Time>, Date
        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);      
        v3.add(new Time(999999L));
        v3.add(new Time(-99999L)); 
        v3.add(TIME_0);
        v3.add(new Time(999999L));
        v3.add(new Time(-99999L));

        FindIterator<Time> finder = new FindIterator<Time>(v3.iterator());
        assertEquals(2, Iterators.count(finder, new Date(999999L)));
    }

    // ========================================
    // Iterators.count(Iterator,Equality,Value)
    // ========================================
    
    public void testCountEq() {
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        v1.add(DOODADS);
        v1.add(GIZMOS);
        v1.add(WIDGETS);
        v1.add(DOOHICKEYS);
        v1.add(GIZMOS);
        v1.add(DUDS);       
        v1.add(DOOHICKEYS);
        v1.add(GIZMOS);

        // FindIterator<Sample>, Equality<Sample>, Derived
        FindIterator<SampleObject> finder1 =
            new FindIterator<SampleObject>(v1.iterator());
        assertEquals(2, Iterators.count(finder1, eq, DOOHICKEYS));

        // Iterator<? extends Sample>, Equality<Sample>, Sample
        Vector<? extends SampleObject> v2 = v1;
        assertEquals(3, Iterators.count(v2.iterator(), eq, GIZMOS));

        // FindIterator<Derived>, Equality<Sample>, Sample
        Vector<DerivedObject> v3 = new Vector<DerivedObject>();
        v3.add(DOODADS);
        v3.add(DOOHICKEYS);
        v3.add(DUDS);       
        v3.add(DOOHICKEYS);

        FindIterator<DerivedObject> finder2 =
            new FindIterator<DerivedObject>(v3.iterator());
        assertEquals(1, Iterators.count(finder2, eq, SAMPLE_DUD));
    }

    // =================================
    // Iterators.count(Iterator,Functor)
    // =================================
    
    public void testCountFn() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Time(System.currentTimeMillis()));
        v1.add(NOW);
        v1.add(EPOCH);
        v1.add(new Time(1L));
        v1.add(EPOCH);      
        v1.add(new Date(999999L)); 
        v1.add(new Date(-99999L));
        v1.add(new Time(5L*DAYS));
        v1.add(new Time(-99999L));

        // FindIterator<Date>, Functor<Date>
        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        assertEquals(4, Iterators.count(finder1, isEpochDay));

        // Iterator<? extends Date>, Functor<Date>
        Vector<? extends Date> v2 = v1;
        assertEquals(4, Iterators.count(v2.iterator(), isEpochDay));

        // FindIterator<Time>, Functor<Date>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(System.currentTimeMillis()));
        v3.add(new Time(3L));
        v3.add(TIME_0);      
        v3.add(new Time(999999L)); 
        v3.add(new Time(-99999L));
        v3.add(new Time(5L*24L*60L*60L*3000L));
        v3.add(new Time(-99999L));

        FindIterator<Time> finder3 = new FindIterator<Time>(v3.iterator());
        assertEquals(3, Iterators.count(finder3, isEpochDay));
    }
    
    // ================================
    // Iterators.findAdjacent(Iterator)
    // ================================

    public void testFindAdj() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);      
        v1.add(new Time(999999L));
        v1.add(new Date(999999L));
        v1.add(new Date(-99999L)); 
        v1.add(new Time(-99999L));
        v1.add(TIME_0);

        // FindIterator<Date>
        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        LookAheadIterator<Date> iter1 = Iterators.findAdjacent(finder1);
        assertEquals(new Time(999999L), iter1.next());
        assertEquals(new Date(999999L), iter1.next());
        assertEquals(new Date(-99999L), iter1.next());
        assertEquals(new Time(-99999L), iter1.next());

        Date d = iter1.next();
        assertTrue(d instanceof Time);
        assertEquals(d, EPOCH);
        assertFalse(iter1.hasNext());

        // Iterator<? extends Date>
        Vector<? extends Date> v2 = v1;
        LookAheadIterator<Date> iter2 = Iterators.findAdjacent(v2.iterator());
        assertEquals(new Time(999999L), iter2.next());
        assertEquals(new Date(999999L), iter2.next());
        assertEquals(new Date(-99999L), iter2.next());
        assertEquals(new Time(-99999L), iter2.next());

        d = iter2.next();
        assertTrue(d instanceof Time);
        assertEquals(d, EPOCH);
        assertFalse(iter2.hasNext());

//         // FindIterator<Time>
//         Vector<Time> v3 = new Vector<Time>();
//         v3.add(TIME_0);      
//         v3.add(new Time(999999L));
//         v3.add(new Time(999999L));
//         v3.add(new Time(-99999L)); 
//         v3.add(new Time(-99999L));
//         v3.add(TIME_0);
        
//         FindIterator<Time> finder3 = new FindIterator<Time>(v3.iterator());
//         LookAheadIterator<Date> iter3 = Iterators.<Date>findAdjacent(finder3);
//         assertEquals(new Time(999999L), iter3.next());
//         assertEquals(new Time(999999L), iter3.next());
//         assertEquals(new Time(-99999L), iter3.next());
    }

    // ========================================
    // Iterators.findAdjacent(Iterator,Functor)
    // ========================================

    public void testFindAdjFn() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(NOW);
        v1.add(EPOCH);      
        v1.add(new Time(999999L));
        v1.add(new Date(-99999L)); 
        v1.add(NOW);
        v1.add(new Time(-99999L));
        v1.add(TIME_0);
        v1.add(NOW);

        // FindIterator<Date>, Functor<Date>
        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        LookAheadIterator<Date> iter1 =
            Iterators.findAdjacent(finder1, within24Hrs);
        assertEquals(TIME_0, iter1.next());

        iter1 = Iterators.findAdjacent(iter1, within24Hrs);
        assertEquals(new Time(999999L), iter1.next());
        assertEquals(new Date(-99999L), iter1.next());
        
        iter1 = Iterators.findAdjacent(iter1, within24Hrs);
        assertEquals(new Time(-99999L), iter1.next());
        assertEquals(TIME_0, iter1.next());
        assertEquals(NOW, iter1.next());
        assertFalse(iter1.hasNext());                              

        // Iterator<? extends Date>, Functor<Date> 
        Vector<? extends Date> v2 = v1;
        LookAheadIterator<Date> iter2 =
            Iterators.findAdjacent(v2.iterator(), within24Hrs);
        assertEquals(TIME_0, iter2.next());

        iter2 = Iterators.findAdjacent(iter2, within24Hrs);
        assertEquals(new Time(999999L), iter2.next());
        assertEquals(new Date(-99999L), iter2.next());
        
        iter2 = Iterators.findAdjacent(iter2, within24Hrs);
        assertEquals(new Time(-99999L), iter2.next());
        assertEquals(TIME_0, iter2.next());
        assertEquals(NOW, iter2.next());
        assertFalse(iter2.hasNext());                              
        
        // FindIterator<Time>, Functor<Date>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);      
        v3.add(new Time(18L*HRS));
        v3.add(new Time(-18L*HRS)); // 36 Hrs apart
        v3.add(TIME_0);
        v3.add(new Time(18L*HRS));
        v3.add(TIME_0);
        v3.add(new Time(-18L*HRS));
        v3.add(TIME_0);

        FindIterator<Time> finder3 = new FindIterator<Time>(v3.iterator());
        LookAheadIterator<Date> iter3 =
            Iterators.findAdjacent(finder3, within24Hrs);
        assertEquals(TIME_0, iter3.next());

        iter3 = Iterators.findAdjacent(iter3, within24Hrs);
        assertEquals(new Time(-18L*HRS), iter3.next());
        assertEquals(TIME_0, iter3.next());
        assertEquals(new Time(18L*HRS), iter3.next());
    }

    // ==========================================
    // Iterators.findElement(Iterator,Collection)
    // ==========================================

    public void testFindElement() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        v1.add(WIDGETS);
        v1.add(MUMBLES);
        v1.add(GIZMOS);
        v1.add(MOREWIDGETS);
        v1.add(DOODADS);
        v1.add(DOOHICKEYS);
        v1.add(DUDS);
        v1.add(SAMPLE_DUD);
        
        Vector<DerivedObject> c1 = new Vector<DerivedObject>();
        c1.add(DOOHICKEYS);
        c1.add(DOODADS);
        c1.add(DUDS);

        Vector<DerivedObject> c2 = new Vector<DerivedObject>();
        c2.add(DUDS);
        c2.add(DOODADS);

        Vector<SampleObject> cs1 = new Vector<SampleObject> ();
        cs1.addAll(c1);
        
        // FindIterator<Sample>, Collection<Sample>
        FindIterator<SampleObject> finder1 =
            new FindIterator<SampleObject>(v1.iterator());
        FindIterator<SampleObject> iter1 = Iterators.findElement(finder1, cs1);
        
        assertEquals(DOODADS, iter1.next());
        iter1 = Iterators.findElement(iter1, cs1);
        assertEquals(DOOHICKEYS, iter1.next());
        iter1 = Iterators.findElement(iter1, c2);
        assertEquals(DUDS, iter1.next());

        // Iterator<? extends Sample>, Collection<Derived>
        Vector<? extends SampleObject> v2 = v1;
        iter1 = Iterators.findElement(v2.iterator(), c1);
        assertEquals(DOODADS, iter1.next());
        iter1 = Iterators.findElement(iter1, c1);
        assertEquals(DOOHICKEYS, iter1.next());
        iter1 = Iterators.findElement(iter1, c2);
        assertEquals(DUDS, iter1.next());
    }

    // ==================================================
    // Iterators.findElement(Iterator,Collection,Functor)
    // ==================================================

    public void testFindElementFn() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Date(36L*DAYS));
        v1.add(new Date(48L*DAYS));
        v1.add(EPOCH);
        v1.add(NOW);
        v1.add(new Date(56L*DAYS));
        v1.add(new Date(6666666L));

        Vector<Time> c1 = new Vector<Time>();
        c1.add(new Time(999999L));

        // FindIterator<Date>, Collection<Time>
        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        FindIterator<Date> iter1 =
            Iterators.findElement(finder1, c1, within24Hrs);
        
        assertEquals(EPOCH, iter1.next());
        iter1 = Iterators.findElement(iter1, c1, within24Hrs);
        assertEquals(new Date(6666666L), iter1.next());

        // Iterator<? extends Date>, Collection<Date>
        Vector<Date> cd1 = new Vector<Date>();
        cd1.addAll(c1);

        Vector<? extends Date> v2 = v1;
        FindIterator<Date> iter2 =
            Iterators.findElement(v2.iterator(), cd1, within24Hrs);
        
        assertEquals(EPOCH, iter2.next());
        iter1 = Iterators.findElement(iter2, c1, within24Hrs);
        assertEquals(new Date(6666666L), iter1.next());
    }

    // ====================================
    // Iterators.match(Iterator,Collection)
    // ====================================

    public void testMatch() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Date(999999L));
        v1.add(NOW);
        v1.add(EPOCH);
        v1.add(new Date(-99999L));
        v1.add(new Date(666666L));
        v1.add(NOW);
        v1.add(EPOCH);

        Vector<Date> c1 = new Vector<Date>();
        c1.add(NOW);
        c1.add(EPOCH);

        // FindIterator<Date>, Collection<Date>
        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        LookAheadIterator<Date> iter1 = Iterators.match(finder1, c1);
        assertEquals(NOW, iter1.next());
        assertEquals(EPOCH,iter1.next());
        assertEquals(new Date(-99999L), iter1.next());

        iter1 = Iterators.match(iter1, c1);
        assertEquals(NOW, iter1.next());
        assertEquals(EPOCH,iter1.next());
        assertFalse(iter1.hasNext());

        Vector<? extends Date> v2 = v1;
        Vector<? extends Date> c2 = c1;

        // Iterator<? extends Date>, Collection<? extends Date>
        LookAheadIterator<Date> iter2 = Iterators.match(v2.iterator(), c2);
        assertEquals(NOW, iter2.next());
        assertEquals(EPOCH,iter2.next());
        assertEquals(new Date(-99999L), iter2.next());

        iter2 = Iterators.match(iter2, c2);
        assertEquals(NOW, iter2.next());
        assertEquals(EPOCH,iter2.next());
        assertFalse(iter2.hasNext());

        // FindIterator<Time>, Collection<Date>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(-4L*HRS));
        v3.add(TIME_0);
        v3.add(new Time(-12L*HRS));
        v3.add(TIME_0);
        v3.add(new Time(4L*HRS));
        v3.add(new Time(6L*HRS));
        v3.add(new Time(-1L*HRS));
        v3.add(new Time(12L*HRS));
        v3.add(TIME_0);
        v3.add(new Time(4L*HRS));

        Vector<Date> c3 = new Vector<Date>();
        c3.add(TIME_0);
        c3.add(new Time(4L*HRS));
            
        FindIterator<Date> finder3 = new FindIterator<Date>(v3.iterator());
        LookAheadIterator<Date> iter3 = Iterators.match(finder3, c3);
        assertEquals(TIME_0, iter3.next());
        assertEquals(new Time(4L*HRS), iter3.next());
        assertEquals(new Time(6L*HRS), iter3.next());

        iter3 = Iterators.match(iter3, c3);
        assertEquals(TIME_0, iter3.next());
        assertEquals(new Time(4L*HRS), iter3.next());
        assertFalse(iter3.hasNext());
    }

    // ============================================
    // Iterators.match(Iterator,Collection,Functor)
    // ============================================

    public void testMatchFn() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Date(999999L));
        v1.add(NOW);
        v1.add(EPOCH);
        v1.add(new Date(-99999L));
        v1.add(new Date(666666L));
        v1.add(NOW);
        v1.add(EPOCH);

        Vector<Date> c1 = new Vector<Date>();
        c1.add(EPOCH);
        c1.add(EPOCH);
        c1.add(EPOCH);

        // FindIterator<Date>, Collection<Date>
        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        LookAheadIterator<Date> iter1 = Iterators.match(finder1, c1, within1Hr);
        assertEquals(EPOCH,iter1.next());
        assertEquals(new Date(-99999L), iter1.next());
        assertEquals(new Date(666666L), iter1.next());

        iter1 = Iterators.match(iter1, c1, within1Hr);
        assertFalse(iter1.hasNext());

        Vector<? extends Date> v2 = v1;
        Vector<? extends Date> c2 = c1;

        // Iterator<? extends Date>, Collection<? extends Date>
        LookAheadIterator<Date> iter2 =
            Iterators.match(v2.iterator(), c2, within1Hr);
        
        assertEquals(EPOCH,iter2.next());
        assertEquals(new Date(-99999L), iter2.next());
        assertEquals(new Date(666666L), iter2.next());

        iter2 = Iterators.match(iter2, c2);
        assertFalse(iter2.hasNext());

        // FindIterator<Time>, Collection<Date>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(-9L*HRS/2));
        v3.add(new Time(1L*HRS/2));
        v3.add(new Time(-25L*HRS/2));
        v3.add(new Time(1L*HRS/2));
        v3.add(new Time(7L*HRS/2));
        v3.add(new Time(13L*HRS/2));
        v3.add(new Time(-3L*HRS/2));
        v3.add(new Time(25L*HRS/2));
        v3.add(new Time(1L*HRS/2));
        v3.add(new Time(9L*HRS/2));

        Vector<Date> c3 = new Vector<Date>();
        c3.add(TIME_0);
        c3.add(new Time(4L*HRS));
            
        FindIterator<Date> finder3 = new FindIterator<Date>(v3.iterator());
        LookAheadIterator<Date> iter3 = Iterators.match(finder3, c3, within1Hr);
        assertEquals(new Time(1L*HRS/2), iter3.next());
        assertEquals(new Time(7L*HRS/2), iter3.next());
        assertEquals(new Time(13L*HRS/2), iter3.next());

        iter3 = Iterators.match(iter3, c3, within1Hr);
        assertEquals(new Time(1L*HRS/2), iter3.next());
        assertEquals(new Time(9L*HRS/2), iter3.next());
        assertFalse(iter3.hasNext());
    }

    // =======================================
    // Iterators.mismatch(Iterator,Collection)
    // =======================================

    public void testMismatch() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);
        v1.add(new Date(18L*HRS));
        v1.add(new Date(-18L*HRS));

        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        LookAheadIterator<Date> iter1 = Iterators.mismatch(finder1, v1);
        assertFalse(iter1.hasNext());
        
        Vector<? extends Date> v2 = v1;
        LookAheadIterator<Date> iter2 = Iterators.mismatch(v2.iterator(), v1);
        assertFalse(iter2.hasNext());

        iter2 = Iterators.mismatch(v1.iterator(), v2);
        assertFalse(iter2.hasNext());

        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);
        v3.add(new Time(18L*HRS));
        FindIterator<Time> finder3 = new FindIterator<Time>(v3.iterator());
        LookAheadIterator<Date> iter3 = Iterators.mismatch(finder3, v2);
        assertFalse(iter3.hasNext());

        iter3 = Iterators.mismatch(v2.iterator(), v3);
        assertEquals(new Date(-18L*HRS), iter3.next());
    }
    
    // ===============================================
    // Iterators.mismatch(Iterator,Collection,Functor)
    // ===============================================
    
    public void testMismatchFn() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Date(1L*HRS/2));
        v1.add(new Date(37L*HRS/2));
        v1.add(new Date(-37L*HRS/2));
        BinaryNegate<Date,Date> bf = new BinaryNegate<Date,Date>(within1Hr);
        
        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        LookAheadIterator<Date> iter1 = Iterators.mismatch(finder1, v1, bf);
        assertFalse(iter1.hasNext());
        
        Vector<? extends Date> v2 = v1;
        LookAheadIterator<Date> iter2 = Iterators.mismatch(v2.iterator(),v1,bf);
        assertFalse(iter2.hasNext());

        iter2 = Iterators.mismatch(v1.iterator(), v2);
        assertFalse(iter2.hasNext());

        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(1L*HRS/2));
        v3.add(new Time(36L*HRS/2));
        FindIterator<Time> finder3 = new FindIterator<Time>(v3.iterator());
        LookAheadIterator<Date> iter3 = Iterators.mismatch(finder3, v2, bf);
        assertFalse(iter3.hasNext());

        iter3 = Iterators.mismatch(v2.iterator(), v3, bf);
        assertEquals(new Date(-37L*HRS/2), iter3.next());
    }
    
    // ==========================================
    // Iterators.findRepeated(Iterator,int,Value)
    // ==========================================

    public void testFindRep() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);      
        v1.add(new Time(999999L));
        v1.add(new Date(999999L));
        v1.add(new Date(-99999L)); 
        v1.add(new Time(-99999L));
        v1.add(TIME_0);

        // FindIterator<Date>, Date
        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        LookAheadIterator<Date> iter1 =
            Iterators.findRepeated(finder1, 2, new Date(999999L));
        assertEquals(new Time(999999L), iter1.next());
        assertEquals(new Date(999999L), iter1.next());

        Date d = iter1.next();
        assertFalse(d instanceof Time);
        assertEquals(d, new Date(-99999L));
        
        iter1 = Iterators.findRepeated(iter1, 2, EPOCH);
        assertFalse(iter1.hasNext());

        // Iterator<? extends Date>, Date
        Vector<? extends Date> v2 = v1;
        LookAheadIterator<Date> iter2 =
            Iterators.findRepeated(v2.iterator(), 2, new Date(999999L));
        assertEquals(new Time(999999L), iter2.next());
        assertEquals(new Date(999999L), iter2.next());

        d = iter2.next();
        assertFalse(d instanceof Time);
        assertEquals(d, new Date(-99999L));
        
        iter2 = Iterators.findRepeated(iter2, 2, EPOCH);
        assertFalse(iter2.hasNext());

        // FindIterator<Time>, Date
        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);      
        v3.add(new Time(999999L));
        v3.add(new Time(999999L));
        v3.add(new Time(-99999L)); 
        v3.add(new Time(-99999L));
        v3.add(TIME_0);

        FindIterator<Time> finder3 = new FindIterator<Time>(v3.iterator());
        LookAheadIterator<Date> iter3 =
            Iterators.findRepeated(finder3, 2, new Date(999999L));
        assertEquals(new Time(999999L), iter3.next());
        assertEquals(new Date(999999L), iter3.next());

    }

    // ===================================================
    // Iterators.findRepeated(Iterator,int,Value,Equality)
    // ===================================================
    
    public void testFindRepEq() {
        SampleObject dorbs =
            new SampleObject("dorbs", 0, new BigDecimal("32.95"), NOW);
        DerivedObject dorb2 =
            new DerivedObject("dorbs", 0, new BigDecimal("32.95"), NOW);
        
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        v1.add(DOODADS);
        v1.add(GIZMOS);
        v1.add(WIDGETS);
        v1.add(dorbs);
        v1.add(dorb2);
        v1.add(DOOHICKEYS);
        v1.add(DUDS);       
        v1.add(DUDS);       
        v1.add(GIZMOS);

        // FindIterator<Sample>, int, Derived, EqualTo<Sample>
        FindIterator<SampleObject> finder1 =
            new FindIterator<SampleObject>(v1.iterator());
        LookAheadIterator<SampleObject> iter1 =
            Iterators.findRepeated(finder1 ,2, dorb2, eq);
        
        assertEquals(dorbs, iter1.next());
        assertEquals(dorb2, iter1.peek(1));
        assertEquals(DOOHICKEYS, iter1.peek(2));
        
        iter1 = Iterators.findRepeated(iter1, 2, DUDS, eq);
        assertEquals(DUDS, iter1.peek(1));
        assertEquals(DUDS, iter1.peek(2));
        iter1.next();
        
        iter1 = Iterators.findRepeated(iter1, 2, DOODADS, eq);
        assertFalse(iter1.hasNext());

        // FindIterator<? extends Sample>, int, Derived, EqualTo<Sample>
        Vector<? extends SampleObject> v2 = v1;
        LookAheadIterator<SampleObject> iter2 =
            Iterators.findRepeated(v2.iterator(), 2, dorb2, eq);
        assertEquals(dorbs, iter2.next());
        assertEquals(dorb2, iter2.peek(1));
        assertEquals(DOOHICKEYS, iter2.peek(2));
        
        iter2 = Iterators.findRepeated(iter2, 2, DUDS, eq);
        assertEquals(DUDS, iter2.peek(1));
        assertEquals(DUDS, iter2.peek(2));
        iter2.next();
        
        iter2 = Iterators.findRepeated(iter2, 2, DOODADS, eq);
        assertFalse(iter2.hasNext());

        // FindIterator<Derived>, int, Derived, EqualTo<Sample>
        Vector<DerivedObject> v3 = new Vector<DerivedObject>();
        v3.add(DOODADS);
        v3.add(dorb2);
        v3.add(dorb2);
        v3.add(DOOHICKEYS);
        v3.add(DUDS);       
        v3.add(DUDS);       

        FindIterator<DerivedObject> finder3 =
            new FindIterator<DerivedObject>(v3.iterator());
        LookAheadIterator<SampleObject> iter3 =
            Iterators.findRepeated(finder3 ,2, dorbs, eq);
        
        assertEquals(dorb2, iter3.next());
        assertEquals(dorb2, iter3.peek(1));
        assertEquals(DOOHICKEYS, iter3.peek(2));
    }

    // ============================================
    // Iterators.findRepeated(Iterator,int,Functor)
    // ============================================

    public void testFindRepFn() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);      
        v1.add(new Time(999999L));
        v1.add(new Date(999999L));
        v1.add(NOW);
        v1.add(new Date(-99999L)); 
        v1.add(new Time(-99999L));
        v1.add(NOW);
        v1.add(TIME_0);

        // FindIterator<Date>, int, Functor<Date>
        FindIterator<Date> finder1= new FindIterator<Date>(v1.iterator());
        LookAheadIterator<? extends Date> iter1 =
            Iterators.findRepeated(finder1, 2, isEpochDay);
        assertEquals(TIME_0, iter1.peek(1));
        assertEquals(new Time(999999L), iter1.peek(2));
        iter1.next();
                     
        assertEquals(new Date(999999L), iter1.peek(1));
        assertEquals(new Time(999999L), iter1.peek(2));
        iter1.next();

        iter1 = Iterators.findRepeated(iter1, 2, isEpochDay);
        assertFalse(iter1.hasNext());

        // Iterator<? extends Date>, Functor<Date>
        Vector<? extends Date> v2 = v1;
        LookAheadIterator<? extends Date> iter2 =
            Iterators.findRepeated(v2.iterator(), 2, isEpochDay);
        
        assertEquals(TIME_0, iter2.peek(1));
        assertEquals(new Time(999999L), iter2.peek(2));
        iter2.next();
                     
        assertEquals(new Date(999999L), iter2.peek(1));
        assertEquals(new Time(999999L), iter2.peek(2));
        iter2.next();

        iter2 = Iterators.findRepeated(iter2, 2, isEpochDay);
        assertFalse(iter2.hasNext());

        // FindIterator<Time>, int, Functor<Date>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(-18L*HRS));      
        v3.add(new Time(18L*HRS));
        v3.add(new Time(18L*HRS));
        v3.add(new Time(-18L*HRS));
        v3.add(TIME_0); 
        v3.add(new Time(-18L*HRS));
        v3.add(new Time(18L*HRS));
        v3.add(TIME_0);

        FindIterator<Time> finder3= new FindIterator<Time>(v3.iterator());
        LookAheadIterator<Date> iter3 =
            Iterators.findRepeated(finder3, 2, isEpochDay);
        assertEquals(new Time(18L*HRS), iter3.next());
        assertEquals(new Time(18L*HRS), iter3.peek(1));
        assertEquals(new Time(-18L*HRS),iter3.peek(2));
    }

    // ===================================
    // Iterators.forEach(Iterator,Functor)
    // ===================================

    public void testForEach() {
        Date d1 = new Date(-99999L);
        Date d2 = new Date(999999L);

        Vector<Date> v1 = new Vector<Date>();
        v1.add(NOW);
        v1.add(EPOCH);
        v1.add(new Date(999999L));
        v1.add(new Date(-99999L));

        FindIterator<Date> finder1 = new FindIterator<Date>(v1.iterator());
        Identity<Date> id = new Identity<Date>();

        assertEquals(id, Iterators.forEach(finder1, id));
        assertEquals(d1, id.arg());

        v1.add(d2);
        Vector<? extends Date> v2 = v1;
        assertEquals(id, Iterators.forEach(v2.iterator(), id));
        assertEquals(d2, id.arg());

        Time t1 = new Time(-4L*HRS);
        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);
        v3.add(new Time(12L*HRS));
        v3.add(new Time(-4L*HRS));

        FindIterator<Time> finder3 = new FindIterator<Time>(v3.iterator());
        assertEquals(id, Iterators.forEach(finder3, id));
        assertEquals(t1, id.arg());
    }
    
    // ==================================
    // Iterators.equal(Iterator,Iterator)
    // ==================================
    
    public void testIsEqual() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(BAZ);
        assertTrue(Iterators.equal(v1.iterator(), v2.iterator()));
    }

    public void testIsEqualVar() {
        Vector<Date> v1 = new Vector<Date>();
        Vector<Time> v2 = new Vector<Time>();
        v1.add(EPOCH);      v2.add(TIME_0);
        v1.add(new Date(999999L)); v2.add(new Time(999999L));
        v1.add(new Date(-99999L)); v2.add(new Time(-99999L));
        assertTrue(Iterators.equal(v1.iterator(), v2.iterator()));
        assertTrue(Iterators.equal(v2.iterator(), v1.iterator()));
        assertTrue(Iterators.equal(v1.iterator(), v1.iterator()));
/*EA2.2:assertTrue(Iterators.<Date>equal(v2.iterator(), v2.iterator()));*/
        assertTrue(Iterators.equal(
                           new FindIterator<Date>(v1.iterator()),
                           new LookAheadIterator<Time>(v2.iterator())));
        assertFalse(Iterators.equal(
                           new SingletonIterator<Time>(new Time(99L)),
                           v1.iterator()));
    }
    
    public void testNotEqualValues() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(QLX);
        assertFalse(Iterators.equal(v1.iterator(), v2.iterator()));
    }
    
    public void testNotEqualLength() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(BAZ);
        v1.add(QLX);
        assertFalse(Iterators.equal(v1.iterator(), v2.iterator()));

        v2.add(QLX); v2.add(FOO);
        assertFalse(Iterators.equal(v1.iterator(), v2.iterator()));
    }

    // =============================================
    // Iterators.equal(Iterator,Iterator,Comparator)
    // =============================================
    
    public void testIsEqualComp() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<SampleObject> v2 = new Vector<SampleObject>();

        v1.add(WIDGETS); v2.add(WIDGETS);
        v1.add(GIZMOS);  v2.add(GIZMOS);
        v1.add(MUMBLES); v2.add(MUMBLES);
        assertTrue(Iterators.equal(v1.iterator(), v2.iterator(), soComp));
    }
    
    public void testIsEqualCompVar() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<DerivedObject> v2 = new Vector<DerivedObject>();
        v1.add(DOODADS);    v2.add(DOODADS);
        v1.add(DOOHICKEYS); v2.add(DOOHICKEYS);
        v1.add(DUDS);       v2.add(DUDS);
        assertTrue(Iterators.equal(v1.iterator(), v2.iterator(), soComp));
        assertTrue(Iterators.equal(v2.iterator(), v1.iterator(), soComp));
        assertTrue(Iterators.equal(v2.iterator(), v2.iterator(), soComp));
        assertTrue(Iterators.equal(
                             new FindIterator<SampleObject>(v1.iterator()),
                             new LookAheadIterator<DerivedObject>(v2.iterator()),
                             soComp));
        assertFalse(Iterators.equal(
                             new SingletonIterator<DerivedObject>(DOODADS),
                             v1.iterator(), soComp));
    }
    
    public void testNotEqualValueComp() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<SampleObject> v2 = new Vector<SampleObject>();

        v1.add(WIDGETS); v2.add(WIDGETS);
        v1.add(GIZMOS);  v2.add(GIZMOS);
        v1.add(MUMBLES); v2.add(MOREWIDGETS);
        assertFalse(Iterators.equal(v1.iterator(), v2.iterator(), soComp));
    }
    
    public void testNotEqualLengthComp() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<SampleObject> v2 = new Vector<SampleObject>();

        v1.add(WIDGETS); v2.add(WIDGETS);
        v1.add(GIZMOS);  v2.add(GIZMOS);
        v1.add(MUMBLES); v2.add(MUMBLES);
        v1.add(MOREWIDGETS);
        assertFalse(Iterators.equal(v1.iterator(), v2.iterator(), soComp));

        v2.add(MOREWIDGETS); v2.add(WIDGETS);
        assertFalse(Iterators.equal(v1.iterator(), v2.iterator(), soComp));
    }

    // ===========================================
    // Iterators.equal(Iterator,Iterator,BinaryFn)
    // ===========================================

    public void testIsEqualFn() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(BAR); v2.add(BAZ);
        v1.add(BAZ); v2.add(BAR);
        v1.add(BAR); v2.add(BAR);
        assertTrue(Iterators.equal(v1.iterator(),v2.iterator(),bothStartWith_b));
    }
    
    public void testIsEqualFnVar() {
        Vector<Date> v1 = new Vector<Date>();
        Vector<Time> v2 = new Vector<Time>();

        v1.add(EPOCH); v2.add(new Time(1L));
        v1.add(NOW); v2.add(new Time(System.currentTimeMillis()));
        assertTrue(Iterators.equal(v1.iterator(), v2.iterator(), within24Hrs));
        assertTrue(Iterators.equal(v2.iterator(), v1.iterator(), within24Hrs));
        assertTrue(Iterators.equal(v2.iterator(), v2.iterator(), within24Hrs));
        assertTrue(Iterators.equal(
                             new FindIterator<Date>(v1.iterator()),
                             new LookAheadIterator<Time>(v2.iterator()),
                             within24Hrs));
        assertFalse(Iterators.equal(
                             new SingletonIterator<Time>(TIME_0),
                             v1.iterator(), within24Hrs));
    }
    
    public void testNotEqualValueFn() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(BAR); v2.add(BAZ);
        v1.add(BAZ); v2.add(BAR);
        v1.add(QLX); v2.add(QLX);
        assertFalse(Iterators.equal(v1.iterator(),v2.iterator(),bothStartWith_b));
    }
    
    public void testNotEqualLengthFn() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(BAR); v2.add(BAZ);
        v1.add(BAZ); v2.add(BAR);
        v1.add(BAR);
        assertFalse(Iterators.equal(v1.iterator(),v2.iterator(),bothStartWith_b));

        v2.add(BAR); v2.add(BAZ);
        assertFalse(Iterators.equal(v1.iterator(),v2.iterator(),bothStartWith_b));
    }
    
    // =====================================
    // Iterators.lessThan(Iterator,Iterator)
    // =====================================

    public void testLessThanValues() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(QLX);
        assertTrue(Iterators.lessThan(v1.iterator(), v2.iterator()));
        assertFalse(Iterators.lessThan(v2.iterator(),v1.iterator()));
    }
    
    public void testLessThan() {
        Vector<Date> v1 = new Vector<Date>();
        Vector<Time> v2 = new Vector<Time>();
        v1.add(EPOCH);      v2.add(TIME_0);
        v1.add(new Date(999999L)); v2.add(new Time(999999L));
        v1.add(new Date(-99999L)); v2.add(new Time(-10000L));
        assertTrue(Iterators.lessThan(v1.iterator(), v2.iterator()));
        assertFalse(Iterators.lessThan(v1.iterator(), v1.iterator()));
        assertFalse(Iterators.lessThan(v2.iterator(), v1.iterator()));
/*EA2.2:assertFalse(Iterators.<Date>lessThan(v2.iterator(), v2.iterator()));*/
        assertTrue(Iterators.lessThan(
                           new FindIterator<Date>(v1.iterator()),
                           new LookAheadIterator<Time>(v2.iterator())));
    }
    
    public void testLessThanLength() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(BAZ);
        v1.add(QLX);
        assertFalse(Iterators.lessThan(v1.iterator(), v2.iterator()));
        assertTrue(Iterators.lessThan(v2.iterator(), v1.iterator()));
    }
    
    // ================================================
    // Iterators.lessThan(Iterator,Iterator,Comparator)
    // ================================================
    
    public void testLessThanValuesComp() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(FOO); v2.add(BAR);
        assertTrue(Iterators.lessThan(v1.iterator(), v2.iterator(),tc));
        assertFalse(Iterators.lessThan(v2.iterator(),v1.iterator(),tc));
    }
    
    public void testLessThanValuesCompVar() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<DerivedObject> v2 = new Vector<DerivedObject>();
        v1.add(DOODADS);    v2.add(DOODADS);
        v1.add(DOOHICKEYS); v2.add(DOOHICKEYS);
        v1.add(DOODADS);    v2.add(DUDS);
        assertTrue(Iterators.lessThan(v1.iterator(), v2.iterator(), soComp));
        assertFalse(Iterators.lessThan(v2.iterator(), v1.iterator(), soComp));
        assertFalse(Iterators.lessThan(v2.iterator(), v2.iterator(), soComp));
        assertTrue(Iterators.lessThan(
                            new FindIterator<SampleObject>(v1.iterator()),
                            new LookAheadIterator<DerivedObject>(v2.iterator()),
                            soComp));
        assertTrue(Iterators.lessThan(
                            new SingletonIterator<DerivedObject>(DOODADS),
                            v1.iterator(), soComp));
    }
    
    public void testLessThanLengthComp() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(BAZ);
        v1.add(QLX);
        assertFalse(Iterators.lessThan(v1.iterator(),v2.iterator(),tc));
        assertTrue(Iterators.lessThan(v2.iterator(), v1.iterator(),tc));
    }
    
    // =============================================
    // Iterators.lessThan(Iterator,Iterator,Functor)
    // =============================================
    
    public void testLessThanValuesFn() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();
        BinaryFunctor<String,String,Boolean> bf = new Less<String>(tc);

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(FOO); v2.add(BAR);
        assertTrue(Iterators.lessThan(v1.iterator(), v2.iterator(),bf));
        assertFalse(Iterators.lessThan(v2.iterator(),v1.iterator(),bf));
    }
    
    public void testLessThanValuesFnVar() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<DerivedObject> v2 = new Vector<DerivedObject>();
        BinaryFunctor<SampleObject,SampleObject,Boolean> bf = new Less<SampleObject>(soComp);
        
        v1.add(DOODADS);    v2.add(DOODADS);
        v1.add(DOOHICKEYS); v2.add(DOOHICKEYS);
        v1.add(DOODADS);    v2.add(DUDS);
        assertTrue(Iterators.lessThan(v1.iterator(), v2.iterator(), bf));
        assertFalse(Iterators.lessThan(v2.iterator(), v1.iterator(), bf));
        assertFalse(Iterators.lessThan(v2.iterator(), v2.iterator(), bf));
        assertTrue(Iterators.lessThan(
                            new FindIterator<SampleObject>(v1.iterator()),
                            new LookAheadIterator<DerivedObject>(v2.iterator()),
                            bf));
        assertTrue(Iterators.lessThan(
                            new SingletonIterator<DerivedObject>(DOODADS),
                            v1.iterator(), bf));
    }
    
    public void testLessThanLengthFn() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();
        BinaryFunctor<String,String,Boolean> bf = new Less<String>(tc);

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(BAZ);
        v1.add(QLX);
        assertFalse(Iterators.lessThan(v1.iterator(),v2.iterator(),bf));
        assertTrue(Iterators.lessThan(v2.iterator(), v1.iterator(),bf));
    }

    // ======================
    // minimumValue(Iterator)
    // ======================
    
    public void testMinValue() {
        Vector<Date> v = new Vector<Date>();
        v.add(EPOCH);      
        v.add(new Time(999999L));
        v.add(new Date(999999000L));
        v.add(new Date(-99999L));
        v.add(new Time(999999000L));
        v.add(NOW);
        v.add(TIME_0);
        v.add(new Date(-99999000L));
        v.add(new Date(999999L));
        v.add(new Time(-99999L));

        Vector<? extends Date> v2 = v;
        assertEquals(new Time(-99999000L),
                     Iterators.minimumValue(v2.iterator()));
    }

    // =================================
    // minimumValue(Iterator,Comparator)
    // =================================
    
    public void testMinimumValueComp() {
        Vector<String> v1 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();

        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAZ);
        assertEquals(FOO, Iterators.minimumValue(v1.iterator(), tc));
    }

    // =================================
    // minimumValue(Iterator,Functor)
    // =================================
    
    public void testMinimumValueFn() {
        Vector<String> v1 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();
        Min<String> bf = new Min<String>(tc);

        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAZ);
        assertEquals(FOO, Iterators.minimumValue(v1.iterator(), bf));
    }


    // ======================
    // maximumValue(Iterator)
    // ======================
    
    public void testMaxValue() {
        Vector<Date> v = new Vector<Date>();
        v.add(EPOCH);      
        v.add(new Time(999999L));
        v.add(new Date(999999000L));
        v.add(new Date(-99999L));
        v.add(new Time(999999000L));
        v.add(NOW);
        v.add(TIME_0);
        v.add(new Date(-99999000L));
        v.add(new Date(999999L));
        v.add(new Time(-99999L));

        Vector<? extends Date> v2 = v;
        assertEquals(NOW, Iterators.maximumValue(v2.iterator()));
    }

    // =================================
    // maximumValue(Iterator,Comparator)
    // =================================
    
    public void testMaximumValueComp() {
        Vector<String> v1 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();

        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAZ);
        assertEquals(BAR, Iterators.maximumValue(v1.iterator(), tc));
    }

    // =================================
    // maximumValue(Iterator,Functor)
    // =================================
    
    public void testMaximumValueFn() {
        Vector<String> v1 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();
        Max<String> bf = new Max<String>(tc);

        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAZ);
        assertEquals(BAR, Iterators.maximumValue(v1.iterator(), bf));
    }


    // =====================================
    // =====================================

    public void testAccumulateNoInit() {
        Integer[] ints = {
            new Integer(1), new Integer(2), new Integer(3), new Integer(4) };
        
        List<Integer> data = Arrays.asList(ints);
        Integer sum = Iterators.accumulate(Integer.class, data.iterator(),
                                           new Plus<Integer>(Integer.class));
        assertEquals(new Integer(10), sum);
    }
    
    public void testAccumulateInit() {
        Integer[] ints = {
            new Integer(1), new Integer(2), new Integer(3), new Integer(4) };
        List<Integer> data = Arrays.asList(ints);
        Integer sum = Iterators.accumulate(data.iterator(), new Integer(0),
                                           new Plus<Integer>(Integer.class));
        assertEquals(new Integer(10), sum);
    }

    public void testAccumulateFn() {
        Long[] longs = { new Long(1L),new Long(3L),new Long(4L),new Long(2L) };
        List<Long> data = Arrays.asList(longs);
        Long sum = Iterators.accumulate(data.iterator(), new Long(-2L),
                                        new Max.Comparable<Long>());
        assertEquals(new Long(4L), sum);
    }

    // ===========================
    // transform(Iterator,Functor)
    // ===========================

    public void testTransform() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Date(45L*DAYS));
        v1.add(new Date(90L*DAYS));
        v1.add(new Date(-15L*DAYS));

        // Iterator<Date>
        Iterator<Date> i1 = Iterators.transform(v1.iterator(), nextDay);
        assertEquals(new Date(46L*DAYS), i1.next());
        assertEquals(new Date(91L*DAYS), i1.next());
        assertEquals(new Date(-14L*DAYS),i1.next());
        assertFalse(i1.hasNext());

        // Iterator<? extends Date>
        Vector<? extends Date> v2 = v1;
        
        Iterator<Date> i2 = Iterators.transform(v2.iterator(), nextDay);
        assertEquals(new Date(46L*DAYS), i2.next());
        assertEquals(new Date(91L*DAYS), i2.next());
        assertEquals(new Date(-14L*DAYS),i2.next());
        assertFalse(i2.hasNext());

        // Iterator<Time>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(-18L*HRS));
        v3.add(new Time(-1L));
        
        Iterator<Date> i3 = Iterators.transform(v3.iterator(), nextDay);
        assertEquals(new Date(6L*HRS), i3.next());
        assertEquals(new Date(DAYS-1L), i3.next());
        assertFalse(i3.hasNext());
    }
    
    // ====================================
    // transform(Iterator,Iterator,Functor)
    // ====================================

    public void testBinaryTransform() {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(45L*DAYS));
        v1a.add(new Date(90L*DAYS));
        v1a.add(new Date(-15L*DAYS));

        Vector<Date> v1b = new Vector<Date>();
        v1b.add(new Date(32L*DAYS));
        v1b.add(new Date(95L*DAYS));
        v1b.add(new Date(0L*DAYS));
        
        // Iterator<Date>,Iterator<Date>
        Iterator<Date> i1 =
            Iterators.transform(v1a.iterator(), v1b.iterator(), new Max.Comparable<Date>());
        assertEquals(new Date(45L*DAYS),i1.next());
        assertEquals(new Date(95L*DAYS),i1.next());
        assertEquals(new Date(0L*DAYS), i1.next());
        assertFalse(i1.hasNext());

        // Iterator<? extends Date>,Iterator<Time>
        Vector<? extends Date> v2a = v1a;
        Vector<Time> v2b = new Vector<Time>();
        v2b.add(new Time(12L*HRS));
        v2b.add(new Time(18L*HRS));
        v2b.add(new Time(0L*HRS));
        
        Iterator<Date> i2 =
            Iterators.transform(v2a.iterator(), v2b.iterator(), new Max.Comparable<Date>());
        assertEquals(new Date(45L*DAYS),i2.next());
        assertEquals(new Date(90L*DAYS),i2.next());
        assertEquals(new Date(0L*DAYS), i2.next());
        assertFalse(i2.hasNext());
    }

    // ==================================
    // replaceAll(Iterator,Functor,Value)
    // ==================================
    
    public void testReplaceAll() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Date(45L*DAYS));
        v1.add(new Date(90L*DAYS));
        v1.add(new Date(-15L*DAYS));

        UnaryFunctor<Date,Boolean> bce = new Less.Comparable<Date>().bind2nd(EPOCH); 
        
        // Iterator<Date>
        Iterator<Date> i1 = Iterators.replaceAll(v1.iterator(),bce,EPOCH);
        assertEquals(new Date(45L*DAYS), i1.next());
        assertEquals(new Date(90L*DAYS), i1.next());
        assertEquals(EPOCH,i1.next());
        assertFalse(i1.hasNext());

        // Iterator<? extends Date>
        Vector<? extends Date> v2 = v1;
        
        Iterator<Date> i2 = Iterators.replaceAll(v2.iterator(),bce,EPOCH);
        assertEquals(new Date(45L*DAYS), i2.next());
        assertEquals(new Date(90L*DAYS), i2.next());
        assertEquals(EPOCH,i2.next());
        assertFalse(i2.hasNext());

        // Iterator<Time>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(-18L*HRS));
        v3.add(new Time(18L*HRS));
        
        Iterator<Date> i3 = Iterators.replaceAll(v3.iterator(),bce,EPOCH);
        assertEquals(EPOCH, i3.next());
        assertEquals(new Date(18L*HRS), i3.next());
        assertFalse(i3.hasNext());
    }


    // ===================================
    // removeAll(Iterator,Value)
    // ===================================

    public void testRemoveAll() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Date(-15L*DAYS));
        v1.add(new Date(45L*DAYS));
        v1.add(new Date(-15L*DAYS));
        v1.add(new Date(90L*DAYS));
        v1.add(new Date(-15L*DAYS));

        // Collection<Date>
        Iterator<Date> iter1 =
            Iterators.removeAll(v1.iterator(), new Date(-15L*DAYS));
        assertEquals(new Date(45L*DAYS), iter1.next());
        assertEquals(new Date(90L*DAYS), iter1.next());
        assertFalse(iter1.hasNext());

        // Collection<? extends Date>
        Vector<? extends Date> v2 = v1;
        Iterator<? extends Date> iter2
            = Iterators.removeAll(v2.iterator(),  new Date(-15L*DAYS));
        assertEquals(new Date(45L*DAYS), iter2.next());
        assertEquals(new Date(90L*DAYS), iter2.next());
        assertFalse(iter2.hasNext());

        // Collection<Time>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(-15L*HRS));
        v3.add(new Time(4L*HRS));
        v3.add(new Time(-15L*HRS));
        v3.add(new Time(9L*HRS));
        v3.add(new Time(-15L*HRS));

        Vector<Object> v3b = new Vector<Object>();

        Iterator<Date> iter3 =
             Iterators.removeAll(v3.iterator(), new Date(-15L*HRS));
        assertEquals(new Date(4L*HRS), iter3.next());
        assertEquals(new Date(9L*HRS), iter3.next());
        assertFalse(iter3.hasNext());
    }

    // ===================================
    // removeAll(Iterator,Value,Equality)
    // ===================================

    public void testRemoveAllEq() {
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        v1.add(DUDS);       
        v1.add(DOODADS);    
        v1.add(DUDS);       
        v1.add(DOOHICKEYS); 
        v1.add(DUDS);       

        Iterator<SampleObject> iter1 =
            Iterators.removeAll(v1.iterator(), DUDS, eq);
        assertEquals(DOODADS, iter1.next());
        assertEquals(DOOHICKEYS, iter1.next());
        assertFalse(iter1.hasNext());

        Vector<? extends SampleObject> v2 = v1;
        Iterator<? extends SampleObject> iter2 =
            Iterators.removeAll(v2.iterator(), DUDS, eq);
        assertEquals(DOODADS, iter2.next());
        assertEquals(DOOHICKEYS, iter2.next());
        assertFalse(iter2.hasNext());        

        Vector<DerivedObject> v3 = new Vector<DerivedObject>();
        v3.add(DUDS);       
        v3.add(DOODADS);    
        v3.add(DUDS);       
        v3.add(DOOHICKEYS); 
        v3.add(DUDS);       

        Iterator<SampleObject> iter3 =
            Iterators.removeAll(v3.iterator(), DUDS, eq);
        assertEquals(DOODADS, iter3.next());
        assertEquals(DOOHICKEYS, iter3.next());
        assertFalse(iter3.hasNext());
    }
    
    // ===================================
    // removeAll(Iterator,Functor)
    // ===================================

    public void testRemoveAllFn() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Date(20L*HRS));
        v1.add(new Date(45L*HRS));
        v1.add(new Date(15L*HRS));
        v1.add(new Date(90L*HRS));
        v1.add(new Date(1L*HRS));

        Iterator<Date> iter1 =
            Iterators.removeAll(v1.iterator(), isEpochDay);
        assertEquals(new Date(45L*HRS), iter1.next());
        assertEquals(new Date(90L*HRS), iter1.next());
        assertFalse(iter1.hasNext());

        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 =
            Iterators.removeAll(v2.iterator(), isEpochDay);
        assertEquals(new Date(45L*HRS), iter2.next());
        assertEquals(new Date(90L*HRS), iter2.next());
        assertFalse(iter2.hasNext());

        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(20L*HRS));
        v3.add(new Time(-4L*HRS));
        v3.add(new Time(15L*HRS));
        v3.add(new Time(-9L*HRS));
        v3.add(new Time(0L*HRS));

        Iterator<Date> iter3 =
            Iterators.removeAll(v3.iterator(), isEpochDay);
        assertEquals(new Date(-4L*HRS), iter3.next());
        assertEquals(new Date(-9L*HRS), iter3.next());
        assertFalse(iter3.hasNext());
    }

    // =========================================
    // unique(iterator,Functor)
    // =========================================

    public void testUniqueFn() {
        SampleObjectComparator soComp = new SampleObjectComparator();
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        v1.add(DOODADS);
        v1.add(DOODADS);
        v1.add(DOOHICKEYS);
        v1.add(DOOHICKEYS);
        v1.add(DOODADS);
        v1.add(DUDS);
        v1.add(DUDS);

        Iterator<SampleObject> iter1 = Iterators.unique(v1.iterator(),eq);
        assertEquals(DOODADS,iter1.next());
        assertEquals(DOOHICKEYS,iter1.next());
        assertEquals(DOODADS,iter1.next());
        assertEquals(DUDS, iter1.next());
        assertFalse(iter1.hasNext());

        Vector<? extends SampleObject> v2 = v1;
        Iterator<? extends SampleObject> iter2 =
                    Iterators.unique(v2.iterator(),eq);
        assertEquals(DOODADS,iter2.next());
        assertEquals(DOOHICKEYS,iter2.next());
        assertEquals(DOODADS,iter2.next());
        assertEquals(DUDS, iter2.next());
        assertFalse(iter2.hasNext());

        Vector<DerivedObject> v3 = new Vector<DerivedObject>();
        v3.add(DOODADS);
        v3.add(DOODADS);
        v3.add(DOOHICKEYS);
        v3.add(DOOHICKEYS);
        v3.add(DOODADS);
        v3.add(DUDS);
        v3.add(DUDS);
        
        Iterator<? extends SampleObject> iter3 =
                    Iterators.unique(v3.iterator(),eq);
        assertEquals(DOODADS,iter3.next());
        assertEquals(DOOHICKEYS,iter3.next());
        assertEquals(DOODADS,iter3.next());
        assertEquals(DUDS, iter3.next());
        assertFalse(iter3.hasNext());
    }

    // =========================================
    // merge(Iterator,Iterator)
    // =========================================

    public void testMerge () {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(15L*HRS));
        v1a.add(new Date(20L*HRS));
        v1a.add(new Date(45L*HRS));

        Vector<Date> v1b = new Vector<Date>();
        v1b.add(new Date(1L*HRS));
        v1b.add(new Date(36L*HRS));
        v1b.add(new Date(90L*HRS));

        Iterator<Date> iter1 = Iterators.merge(v1a.iterator(),v1b.iterator());
        assertEquals(new Date(1L*HRS),  iter1.next());
        assertEquals(new Date(15L*HRS), iter1.next());
        assertEquals(new Date(20L*HRS), iter1.next());
        assertEquals(new Date(36L*HRS), iter1.next());
        assertEquals(new Date(45L*HRS), iter1.next());
        assertEquals(new Date(90L*HRS), iter1.next());
        assertFalse(iter1.hasNext());

        Vector<? extends Date> v2a = v1a;
        Vector<? extends Date> v2b = v1b;
        Iterator<Date> iter2 = Iterators.merge(v2a.iterator(),v2b.iterator());
        assertEquals(new Date(1L*HRS),  iter2.next());
        assertEquals(new Date(15L*HRS), iter2.next());
        assertEquals(new Date(20L*HRS), iter2.next());
        assertEquals(new Date(36L*HRS), iter2.next());
        assertEquals(new Date(45L*HRS), iter2.next());
        assertEquals(new Date(90L*HRS), iter2.next());
        assertFalse(iter2.hasNext());

        Vector<Time> v3a = new Vector<Time>();
        v3a.add(new Time(20L*HRS));
        v3a.add(new Time(-4L*HRS));
        v3a.add(new Time(15L*HRS));
        v3a.add(new Time(-9L*HRS));

        Iterator<Date> iter3 = Iterators.merge(v3a.iterator(),v2a.iterator());
        assertEquals(new Date(15L*HRS), iter3.next());
        assertEquals(new Date(20L*HRS), iter3.next());
        assertEquals(new Date(-4L*HRS), iter3.next());
        assertEquals(new Date(15L*HRS), iter3.next());
        assertEquals(new Date(-9L*HRS), iter3.next());
        assertEquals(new Date(20L*HRS), iter3.next());
        assertEquals(new Date(45L*HRS), iter3.next());
        assertFalse(iter3.hasNext());
    }

    // =========================================
    // adjacentDiff(Number,Iterator)
    // =========================================

    public void testAdjacentDiff () {
        Vector<BigDecimal> v = new Vector<BigDecimal>();
        v.add(new BigDecimal("0.0"));
        v.add(new BigDecimal("1.5"));
        v.add(new BigDecimal("-1.5"));

        Iterator<BigDecimal> iter =
            Iterators.adjacentDiff(BigDecimal.class, v.iterator());
        assertEquals(new BigDecimal("-1.5"), iter.next());
        assertEquals(new BigDecimal("3.0"), iter.next());
        assertFalse(iter.hasNext());
    }

    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestIterators.class);
    }
}

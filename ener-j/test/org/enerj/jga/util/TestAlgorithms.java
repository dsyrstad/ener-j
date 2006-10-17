// ============================================================================
// $Id: TestAlgorithms.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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
import org.enerj.jga.fn.comparison.Max;
import org.enerj.jga.fn.comparison.Min;
import org.enerj.jga.fn.logical.BinaryNegate;
import org.enerj.jga.fn.logical.LogicalAnd;
import org.enerj.jga.fn.property.ConstructUnary;
import org.enerj.jga.fn.property.GetProperty;
import org.enerj.jga.fn.string.Match;
import org.enerj.jga.util.Algorithms;


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

public class TestAlgorithms extends TestCase  {
    public TestAlgorithms (String name){ super(name); }

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

    static public final SampleObject WIDGETS = new SampleObject("widgets",42);
    static public final SampleObject MUMBLES = new SampleObject("mumblers",65);
    static public final SampleObject GIZMOS  = new SampleObject("gizmos",0);
    static public final SampleObject MOREWIDGETS =
                                               new SampleObject("widgets",100);

    static public final DerivedObject DOODADS =
                          new DerivedObject("doodads", -32);
    static public final DerivedObject DOOHICKEYS =
                          new DerivedObject("doohickeys",Integer.MAX_VALUE);
    static public final DerivedObject DUDS =
                          new DerivedObject("duds", Integer.MIN_VALUE);
    static public final SampleObject SAMPLE_DUD =
                          new SampleObject("duds", Integer.MIN_VALUE);

    static public final SampleObjectComparator soComp =
                          new SampleObjectComparator();

    // ==============================
    // Algorithms.find(Iterator,Value)
    // ==============================

    public void testFind() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);      
        v1.add(new Time(999999L));
        v1.add(new Date(-99999L)); 
        v1.add(TIME_0);
        v1.add(new Date(999999L));
        v1.add(new Time(-99999L));

        // Collection<Date>, Time
        Iterator<Date> iter1 = Algorithms.find(v1,new Time(999999L));
        assertEquals(new Time(999999L), iter1.next());
        assertEquals(new Time(-99999L), iter1.next());

        Date d = iter1.next();
        assertTrue(d instanceof Time);
        assertEquals(d, EPOCH);

        // Collection<? extends Date>, Time
        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 = Algorithms.find(v2, new Time(999999L));
        
        assertEquals(new Time(999999L), iter2.next());
        assertEquals(new Time(-99999L), iter2.next());

        d = iter2.next();
        assertTrue(d instanceof Time);
        assertEquals(d, EPOCH);

        // Collection<Time>, Date
        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);      
        v3.add(new Time(999999L));
        v3.add(new Time(-99999L)); 
        v3.add(TIME_0);
        v3.add(new Time(999999L));
        v3.add(new Time(-99999L));

        Iterator<Date> iter3 = Algorithms.find(v3, new Date(999999L));
        assertEquals(new Time(999999L), iter3.next());
        assertEquals(new Time(-99999L), iter3.next());
    }

    // =================================
    // Algorithms.find(Iterator,Equality)
    // =================================

    public void testFindEq() {
        Equality<SampleObject> eq = new EqualTo<SampleObject>(new SampleObjectComparator());
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        v1.add(DOODADS);    
        v1.add(DOOHICKEYS); 
        v1.add(DUDS);       

        // Collection<Sample>, Derived, EqualTo<Sample>
        Iterator<SampleObject> iter1 = Algorithms.find(v1,DOOHICKEYS,eq);
        assertEquals(DOOHICKEYS, iter1.next());
        assertEquals(DUDS, iter1.next());
        assertFalse(iter1.hasNext());

        // Collection<? extends Sample>, Derived, EqualTo<Sample>
        Vector<? extends SampleObject> v2 = v1;
        Iterator<SampleObject> iter2 = Algorithms.find(v2,DOOHICKEYS,eq);
        assertEquals(DOOHICKEYS, iter2.next());
        assertEquals(DUDS, iter2.next());
        assertFalse(iter2.hasNext());

        // Collection<Derived>, Derived, EqualTo<Sample>
        Vector<DerivedObject> v3 = new Vector<DerivedObject>();
        v3.add(DOODADS);    
        v3.add(DOOHICKEYS); 
        v3.add(DUDS);       

        Iterator<SampleObject> iter3 = Algorithms.find(v3,DOOHICKEYS,eq);
        assertEquals(DOOHICKEYS, iter3.next());
        assertEquals(DUDS, iter3.next());
        assertFalse(iter3.hasNext());
    }

    // ================================
    // Algorithms.find(Iterator,Functor)
    // ================================
    
    public void testFindFn() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Time(System.currentTimeMillis()));
        v1.add(NOW);
        v1.add(EPOCH);
        v1.add(new Time(1L));

        // Collection<Date>, Functor<Date>
        Iterator<Date> iter1 = Algorithms.find(v1, isEpochDay);
        assertEquals(TIME_0, iter1.next());
        assertEquals(new Date(1L), iter1.next());
        assertFalse(iter1.hasNext());                              

        // Collection<? extends Date>, Functor<Date>
        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 = Algorithms.find(v2, isEpochDay);
        assertEquals(TIME_0, iter2.next());
        assertEquals(new Date(1L), iter2.next());
        assertFalse(iter2.hasNext());                              

        // Collection<Time>, Functor<Date>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(System.currentTimeMillis()));
        v3.add(new Time(-42L));
        v3.add(TIME_0);
        v3.add(new Time(3L));

        Iterator<Date> iter3 = Algorithms.find(v3, isEpochDay);
        assertEquals(TIME_0, iter3.next());
        assertEquals(new Date(3L), iter3.next());
        assertFalse(iter3.hasNext());                              
    }

    // ===============================
    // Algorithms.count(Iterator,Value)
    // ===============================

    public void testCount() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);      
        v1.add(new Time(999999L));
        v1.add(new Date(-99999L)); 
        v1.add(TIME_0);
        v1.add(new Date(999999L));
        v1.add(new Time(-99999L));

        // Collection<Date>, Date
        assertEquals(2, Algorithms.count(v1, new Date(999999L)));

        // Collection<? extends Date, Date
        Vector<? extends Date> v2 = v1;
        assertEquals(2, Algorithms.count(v2, new Date(-99999L)));

        // Collection<Time>, Date
        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);      
        v3.add(new Time(999999L));
        v3.add(new Time(-99999L)); 
        v3.add(TIME_0);
        v3.add(new Time(999999L));
        v3.add(new Time(-99999L));

        assertEquals(2, Algorithms.count(v3, new Date(999999L)));
    }

    // ========================================
    // Algorithms.count(Iterator,Equality,Value)
    // ========================================
    
    public void testCountEq() {
        Equality<SampleObject> eq = new EqualTo<SampleObject>(new SampleObjectComparator());
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        v1.add(DOODADS);
        v1.add(GIZMOS);
        v1.add(WIDGETS);
        v1.add(DOOHICKEYS);
        v1.add(GIZMOS);
        v1.add(DUDS);       
        v1.add(DOOHICKEYS);
        v1.add(GIZMOS);

        // Collection<Sample>, Equality<Sample>, Derived
        assertEquals(2, Algorithms.count(v1, eq, DOOHICKEYS));

        // Collection<? extends Sample>, Equality<Sample>, Sample
        Vector<? extends SampleObject> v2 = v1;
        assertEquals(3, Algorithms.count(v2, eq, GIZMOS));

        // Collection<Derived>, Equality<Sample>, Sample
        Vector<DerivedObject> v3 = new Vector<DerivedObject>();
        v3.add(DOODADS);
        v3.add(DOOHICKEYS);
        v3.add(DUDS);       
        v3.add(DOOHICKEYS);

        assertEquals(1, Algorithms.count(v3, eq, SAMPLE_DUD));
    }

    // =================================
    // Algorithms.count(Iterator,Functor)
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

        // Collection<Date>, Functor<Date>
        assertEquals(4, Algorithms.count(v1, isEpochDay));

        // Collection<? extends Date>, Functor<Date>
        Vector<? extends Date> v2 = v1;
        assertEquals(4, Algorithms.count(v2, isEpochDay));

        // Collection<Time>, Functor<Date>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(System.currentTimeMillis()));
        v3.add(new Time(3L));
        v3.add(TIME_0);      
        v3.add(new Time(999999L)); 
        v3.add(new Time(-99999L));
        v3.add(new Time(5L*24L*60L*60L*3000L));
        v3.add(new Time(-99999L));

        assertEquals(3, Algorithms.count(v3, isEpochDay));
    }
    
    // ================================
    // Algorithms.findAdjacent(Iterator)
    // ================================

    public void testFindAdj() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);      
        v1.add(new Time(999999L));
        v1.add(new Date(999999L));
        v1.add(new Date(-99999L)); 
        v1.add(new Time(-99999L));
        v1.add(TIME_0);

        // Collection<Date>
        Iterator<Date> iter1 = Algorithms.findAdjacent(v1);
        assertEquals(new Time(999999L), iter1.next());
        assertEquals(new Date(999999L), iter1.next());
        assertEquals(new Date(-99999L), iter1.next());
        assertEquals(new Time(-99999L), iter1.next());

        Date d = iter1.next();
        assertTrue(d instanceof Time);
        assertEquals(d, EPOCH);
        assertFalse(iter1.hasNext());

        // Collection<? extends Date>
        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 = Algorithms.findAdjacent(v2);
        assertEquals(new Time(999999L), iter2.next());
        assertEquals(new Date(999999L), iter2.next());
        assertEquals(new Date(-99999L), iter2.next());
        assertEquals(new Time(-99999L), iter2.next());

        d = iter2.next();
        assertTrue(d instanceof Time);
        assertEquals(d, EPOCH);
        assertFalse(iter2.hasNext());

//         // Collection<Time>
//         Vector<Time> v3 = new Vector<Time>();
//         v3.add(TIME_0);      
//         v3.add(new Time(999999L));
//         v3.add(new Time(999999L));
//         v3.add(new Time(-99999L)); 
//         v3.add(new Time(-99999L));
//         v3.add(TIME_0);
        
//         Iterator<Date> iter3 = Algorithms./*<Date>*/findAdjacent(v3);
//         assertEquals(new Time(999999L), iter3.next());
//         assertEquals(new Time(999999L), iter3.next());
//         assertEquals(new Time(-99999L), iter3.next());
    }

    // ========================================
    // Algorithms.findAdjacent(Iterator,Functor)
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

        // Collection<Date>, Functor<Date>
        Iterator<Date> iter1 = Algorithms.findAdjacent(v1, within24Hrs);
        assertEquals(TIME_0, iter1.next());
        assertEquals(new Time(999999L), iter1.next());
        assertEquals(new Date(-99999L), iter1.next());
        
        // Collection<? extends Date>, Functor<Date> 
        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 = Algorithms.findAdjacent(v2, within24Hrs);
        assertEquals(TIME_0, iter2.next());
        assertEquals(new Time(999999L), iter2.next());
        assertEquals(new Date(-99999L), iter2.next());
        
        // Collection<Time>, Functor<Date>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(18L*HRS));
        v3.add(new Time(-18L*HRS)); // 36 Hrs apart
        v3.add(TIME_0);      
        v3.add(new Time(18L*HRS));
        v3.add(TIME_0);

        Iterator<Date> iter3 = Algorithms.findAdjacent(v3, within24Hrs);
        assertEquals(new Time(-18L*HRS), iter3.next());
        assertEquals(TIME_0, iter3.next());
        assertEquals(new Time(18L*HRS), iter3.next());
    }

    // ==========================================
    // Algorithms.findElement(Iterator,Collection)
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

        Vector<SampleObject> cs1 = new Vector<SampleObject> ();
        cs1.addAll(c1);
        
        // Collection<Sample>, Collection<Sample>
        Iterator<SampleObject> iter1 = Algorithms.findElement(v1, cs1);
        assertEquals(DOODADS, iter1.next());

        // Collection<? extends Sample>, Collection<Derived>
        Vector<? extends SampleObject> v2 = v1;
        iter1 = Algorithms.findElement(v2, c1);
    }

    // ==================================================
    // Algorithms.findElement(Iterator,Collection,Functor)
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

        // Collection<Date>, Collection<Time>
        Iterator<Date> iter1 = Algorithms.findElement(v1, c1, within24Hrs);
        assertEquals(EPOCH, iter1.next());

        // Collection<? extends Date>, Collection<Date>
        Vector<Date> cd1 = new Vector<Date>();
        cd1.addAll(c1);

        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 = Algorithms.findElement(v2, cd1, within24Hrs);
        assertEquals(EPOCH, iter2.next());
    }

    // ====================================
    // Algorithms.match(Iterator,Collection)
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

        // Collection<Date>, Collection<Date>
        Iterator<Date> iter1 = Algorithms.match(v1, c1);
        assertEquals(NOW, iter1.next());
        assertEquals(EPOCH,iter1.next());
        assertEquals(new Date(-99999L), iter1.next());

        Vector<? extends Date> v2 = v1;
        Vector<? extends Date> c2 = c1;

        // Collection<? extends Date>, Collection<? extends Date>
        Iterator<Date> iter2 = Algorithms.match(v2, c2);
        assertEquals(NOW, iter2.next());
        assertEquals(EPOCH,iter2.next());
        assertEquals(new Date(-99999L), iter2.next());

        // Collection<Time>, Collection<Date>
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
            
        Iterator<Date> iter3 = Algorithms.match(v3, c3);
        assertEquals(TIME_0, iter3.next());
        assertEquals(new Time(4L*HRS), iter3.next());
        assertEquals(new Time(6L*HRS), iter3.next());
    }

    // ============================================
    // Algorithms.match(Iterator,Collection,Functor)
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

        // Collection<Date>, Collection<Date>
        Iterator<Date> iter1 = Algorithms.match(v1, c1, within1Hr);
        assertEquals(EPOCH,iter1.next());
        assertEquals(new Date(-99999L), iter1.next());
        assertEquals(new Date(666666L), iter1.next());

        Vector<? extends Date> v2 = v1;
        Vector<? extends Date> c2 = c1;

        // Collection<? extends Date>, Collection<? extends Date>
        Iterator<Date> iter2 = Algorithms.match(v2, c2, within1Hr);
        assertEquals(EPOCH,iter2.next());
        assertEquals(new Date(-99999L), iter2.next());
        assertEquals(new Date(666666L), iter2.next());

        // Collection<Time>, Collection<Date>
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
            
        Iterator<Date> iter3 = Algorithms.match(v3, c3, within1Hr);
        assertEquals(new Time(1L*HRS/2), iter3.next());
        assertEquals(new Time(7L*HRS/2), iter3.next());
        assertEquals(new Time(13L*HRS/2), iter3.next());
    }

    // =======================================
    // Algorithms.mismatch(Iterator,Collection)
    // =======================================

    public void testMismatch() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);
        v1.add(new Date(18L*HRS));
        v1.add(new Date(-18L*HRS));

        Iterator<Date> iter1 = Algorithms.mismatch(v1, v1);
        assertFalse(iter1.hasNext());
        
        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 = Algorithms.mismatch(v2, v1);
        assertFalse(iter2.hasNext());

        iter2 = Algorithms.mismatch(v1, v2);
        assertFalse(iter2.hasNext());

        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);
        v3.add(new Time(18L*HRS));

        Iterator<Date> iter3 = Algorithms.mismatch(v3, v2);
        assertFalse(iter3.hasNext());

        iter3 = Algorithms.mismatch(v2, v3);
        assertEquals(new Date(-18L*HRS), iter3.next());
    }
    
    // ===============================================
    // Algorithms.mismatch(Iterator,Collection,Functor)
    // ===============================================
    
    public void testMismatchFn() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Date(1L*HRS/2));
        v1.add(new Date(37L*HRS/2));
        v1.add(new Date(-37L*HRS/2));
        BinaryNegate<Date,Date> bf = new BinaryNegate<Date,Date>(within1Hr);
        
        Iterator<Date> iter1 = Algorithms.mismatch(v1, v1, bf);
        assertFalse(iter1.hasNext());
        
        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 = Algorithms.mismatch(v2,v1,bf);
        assertFalse(iter2.hasNext());

        iter2 = Algorithms.mismatch(v1, v2);
        assertFalse(iter2.hasNext());

        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(1L*HRS/2));
        v3.add(new Time(36L*HRS/2));
        Iterator<Date> iter3 = Algorithms.mismatch(v3, v2, bf);
        assertFalse(iter3.hasNext());

        iter3 = Algorithms.mismatch(v2, v3, bf);
        assertEquals(new Date(-37L*HRS/2), iter3.next());
    }
    
    // ==========================================
    // Algorithms.findRepeated(Iterator,int,Value)
    // ==========================================

    public void testFindRep() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(EPOCH);      
        v1.add(new Time(999999L));
        v1.add(new Date(999999L));
        v1.add(new Date(-99999L)); 
        v1.add(new Time(-99999L));
        v1.add(TIME_0);

        // Collection<Date>, Date
        Iterator<Date> iter1 =
            Algorithms.findRepeated(v1, 2, new Date(999999L));
        assertEquals(new Time(999999L), iter1.next());
        assertEquals(new Date(999999L), iter1.next());

        Date d = iter1.next();
        assertFalse(d instanceof Time);
        assertEquals(d, new Date(-99999L));
        
        // Collection<? extends Date>, Date
        Vector<? extends Date> v2 = v1;
        Iterator<Date> iter2 =
            Algorithms.findRepeated(v2, 2, new Date(999999L));
        assertEquals(new Time(999999L), iter2.next());
        assertEquals(new Date(999999L), iter2.next());

        d = iter2.next();
        assertFalse(d instanceof Time);
        assertEquals(d, new Date(-99999L));
        
        // Collection<Time>, Date
        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);      
        v3.add(new Time(999999L));
        v3.add(new Time(999999L));
        v3.add(new Time(-99999L)); 
        v3.add(new Time(-99999L));
        v3.add(TIME_0);

        Iterator<Date> iter3 =
            Algorithms.findRepeated(v3, 2, new Date(999999L));
        assertEquals(new Time(999999L), iter3.next());
        assertEquals(new Date(999999L), iter3.next());

    }

    // ====================================================
    // Algorithms.findRepeated(Iterator,int,Value,Equality)
    // ====================================================
    
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

        // Collection<Sample>, int, Derived, EqualTo<Sample>
        Iterator<SampleObject> iter1 =
            Algorithms.findRepeated(v1 ,2, dorb2, eq);
        
        assertEquals(dorbs, iter1.next());
        assertEquals(dorb2, iter1.next());
        assertEquals(DOOHICKEYS, iter1.next());
        
        // Collection<? extends Sample>, int, Derived, EqualTo<Sample>
        Vector<? extends SampleObject> v2 = v1;
        Iterator<SampleObject> iter2 =
            Algorithms.findRepeated(v2, 2, dorb2, eq);
        assertEquals(dorbs, iter2.next());
        assertEquals(dorb2, iter2.next());
        assertEquals(DOOHICKEYS, iter2.next());
        
        // Collection<Derived>, int, Derived, EqualTo<Sample>
        Vector<DerivedObject> v3 = new Vector<DerivedObject>();
        v3.add(DOODADS);
        v3.add(dorb2);
        v3.add(dorb2);
        v3.add(DOOHICKEYS);
        v3.add(DUDS);       
        v3.add(DUDS);       

        Iterator<SampleObject> iter3 =
            Algorithms.findRepeated(v3 ,2, dorbs, eq);
        
        assertEquals(dorb2, iter3.next());
        assertEquals(dorb2, iter3.next());
        assertEquals(DOOHICKEYS, iter3.next());
    }

    // ============================================
    // Algorithms.findRepeated(Iterator,int,Functor)
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

        // Collection<Date>, int, Functor<Date>
        Iterator<? extends Date> iter1 =
            Algorithms.findRepeated(v1, 2, isEpochDay);
        assertEquals(TIME_0, iter1.next());
        assertEquals(new Date(999999L), iter1.next());
        assertEquals(new Time(999999L), iter1.next());

        // Collection<? extends Date>, Functor<Date>
        Vector<? extends Date> v2 = v1;
        Iterator<? extends Date> iter2 =
            Algorithms.findRepeated(v2, 2, isEpochDay);
        
        assertEquals(TIME_0, iter2.next());
        assertEquals(new Date(999999L), iter2.next());
        assertEquals(new Time(999999L), iter2.next());

        // Collection<Time>, int, Functor<Date>
        Vector<Time> v3 = new Vector<Time>();
        v3.add(new Time(-18L*HRS));      
        v3.add(new Time(18L*HRS));
        v3.add(new Time(18L*HRS));
        v3.add(new Time(-18L*HRS));
        v3.add(TIME_0); 
        v3.add(new Time(-18L*HRS));
        v3.add(new Time(18L*HRS));
        v3.add(TIME_0);

        Iterator<Date> iter3 = Algorithms.findRepeated(v3, 2, isEpochDay);
        assertEquals(new Time(18L*HRS), iter3.next());
        assertEquals(new Time(18L*HRS), iter3.next());
        assertEquals(new Time(-18L*HRS),iter3.next());
    }

    // ===================================
    // Algorithms.forEach(Iterator,Functor)
    // ===================================

    public void testForEach() {
        Date d1 = new Date(-99999L);
        Date d2 = new Date(999999L);
       
        Vector<Date> v1 = new Vector<Date>();
        v1.add(NOW);
        v1.add(EPOCH);
        v1.add(new Date(999999L));
        v1.add(new Date(-99999L));

        Identity<Date> id = new Identity<Date>();
        assertEquals(id, Algorithms.forEach(v1, id));
        assertEquals(d1, id.arg());

        v1.add(d2);
        Vector<? extends Date> v2 = v1;
        assertEquals(id, Algorithms.forEach(v2, id));
        assertEquals(d2, id.arg());

        Time t1 = new Time(-4L*HRS);
        Vector<Time> v3 = new Vector<Time>();
        v3.add(TIME_0);
        v3.add(new Time(12L*HRS));
        v3.add(new Time(-4L*HRS));

        assertEquals(id, Algorithms.forEach(v3, id));
        assertEquals(t1, id.arg());
    }
    
    // ==================================
    // Algorithms.equal(Iterator,Iterator)
    // ==================================
    
    public void testIsEqual() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(BAZ);
        assertTrue(Algorithms.equal(v1, v2));
    }

    public void testIsEqualVar() {
        Vector<Date> v1 = new Vector<Date>();
        Vector<Time> v2 = new Vector<Time>();
        v1.add(EPOCH);      v2.add(TIME_0);
        v1.add(new Date(999999L)); v2.add(new Time(999999L));
        v1.add(new Date(-99999L)); v2.add(new Time(-99999L));
        assertTrue(Algorithms.equal(v1, v2));
        assertTrue(Algorithms.equal(v2, v1));
        assertTrue(Algorithms.equal(v2, v2));
    }
    
    public void testNotEqualValues() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(QLX);
        assertFalse(Algorithms.equal(v1, v2));
    }
    
    public void testNotEqualLength() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(BAZ);
        v1.add(QLX);
        assertFalse(Algorithms.equal(v1, v2));

        v2.add(QLX); v2.add(FOO);
        assertFalse(Algorithms.equal(v1, v2));
    }

    // =============================================
    // Algorithms.equal(Iterator,Iterator,Comparator)
    // =============================================
    
    public void testIsEqualComp() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<SampleObject> v2 = new Vector<SampleObject>();

        v1.add(WIDGETS); v2.add(WIDGETS);
        v1.add(GIZMOS);  v2.add(GIZMOS);
        v1.add(MUMBLES); v2.add(MUMBLES);
        assertTrue(Algorithms.equal(v1, v2, soComp));
    }
    
    public void testIsEqualCompVar() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<DerivedObject> v2 = new Vector<DerivedObject>();
        v1.add(DOODADS);    v2.add(DOODADS);
        v1.add(DOOHICKEYS); v2.add(DOOHICKEYS);
        v1.add(DUDS);       v2.add(DUDS);
        assertTrue(Algorithms.equal(v1, v2, soComp));
        assertTrue(Algorithms.equal(v2, v1, soComp));
        assertTrue(Algorithms.equal(v2, v2, soComp));
    }
    
    public void testNotEqualValueComp() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<SampleObject> v2 = new Vector<SampleObject>();

        v1.add(WIDGETS); v2.add(WIDGETS);
        v1.add(GIZMOS);  v2.add(GIZMOS);
        v1.add(MUMBLES); v2.add(MOREWIDGETS);
        assertFalse(Algorithms.equal(v1, v2, soComp));
    }
    
    public void testNotEqualLengthComp() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<SampleObject> v2 = new Vector<SampleObject>();

        v1.add(WIDGETS); v2.add(WIDGETS);
        v1.add(GIZMOS);  v2.add(GIZMOS);
        v1.add(MUMBLES); v2.add(MUMBLES);
        v1.add(MOREWIDGETS);
        assertFalse(Algorithms.equal(v1, v2, soComp));

        v2.add(MOREWIDGETS); v2.add(WIDGETS);
        assertFalse(Algorithms.equal(v1, v2, soComp));
    }

    // ===========================================
    // Algorithms.equal(Iterator,Iterator,BinaryFn)
    // ===========================================

    public void testIsEqualFn() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(BAR); v2.add(BAZ);
        v1.add(BAZ); v2.add(BAR);
        v1.add(BAR); v2.add(BAR);
        assertTrue(Algorithms.equal(v1,v2,bothStartWith_b));
    }
    
    public void testIsEqualFnVar() {
        Vector<Date> v1 = new Vector<Date>();
        Vector<Time> v2 = new Vector<Time>();

        v1.add(EPOCH); v2.add(new Time(1L));
        v1.add(NOW); v2.add(new Time(System.currentTimeMillis()));
        assertTrue(Algorithms.equal(v1, v2, within24Hrs));
        assertTrue(Algorithms.equal(v2, v1, within24Hrs));
        assertTrue(Algorithms.equal(v2, v2, within24Hrs));
    }
    
    public void testNotEqualValueFn() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(BAR); v2.add(BAZ);
        v1.add(BAZ); v2.add(BAR);
        v1.add(QLX); v2.add(QLX);
        assertFalse(Algorithms.equal(v1,v2,bothStartWith_b));
    }
    
    public void testNotEqualLengthFn() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(BAR); v2.add(BAZ);
        v1.add(BAZ); v2.add(BAR);
        v1.add(BAR);
        assertFalse(Algorithms.equal(v1,v2,bothStartWith_b));

        v2.add(BAR); v2.add(BAZ);
        assertFalse(Algorithms.equal(v1,v2,bothStartWith_b));
    }
    
    // =====================================
    // Algorithms.lessThan(Iterator,Iterator)
    // =====================================

    public void testLessThanValues() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(QLX);
        assertTrue(Algorithms.lessThan(v1, v2));
        assertFalse(Algorithms.lessThan(v2,v1));
    }
    
    public void testLessThan() {
        Vector<Date> v1 = new Vector<Date>();
        Vector<Time> v2 = new Vector<Time>();
        v1.add(EPOCH);      v2.add(TIME_0);
        v1.add(new Date(999999L)); v2.add(new Time(999999L));
        v1.add(new Date(-99999L)); v2.add(new Time(-10000L));
        assertTrue(Algorithms.lessThan(v1, v2));
        assertFalse(Algorithms.lessThan(v1, v1));
        assertFalse(Algorithms.lessThan(v2, v1));
/*EA2.2:assertFalse(Algorithms.<Date>lessThan(v2, v2)); */
    }
    
    public void testLessThanLength() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(BAZ);
        v1.add(QLX);
        assertFalse(Algorithms.lessThan(v1, v2));
        assertTrue(Algorithms.lessThan(v2, v1));
    }
    
    // ================================================
    // Algorithms.lessThan(Iterator,Iterator,Comparator)
    // ================================================
    
    public void testLessThanValuesComp() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(FOO); v2.add(BAR);
        assertTrue(Algorithms.lessThan(v1, v2,tc));
        assertFalse(Algorithms.lessThan(v2,v1,tc));
    }
    
    public void testLessThanValuesCompVar() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<DerivedObject> v2 = new Vector<DerivedObject>();
        v1.add(DOODADS);    v2.add(DOODADS);
        v1.add(DOOHICKEYS); v2.add(DOOHICKEYS);
        v1.add(DOODADS);    v2.add(DUDS);
        assertTrue(Algorithms.lessThan(v1, v2, soComp));
        assertFalse(Algorithms.lessThan(v2, v1, soComp));
        assertFalse(Algorithms.lessThan(v2, v2, soComp));
    }
    
    public void testLessThanLengthComp() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(BAZ); v2.add(BAZ);
        v1.add(QLX);
        assertFalse(Algorithms.lessThan(v1,v2,tc));
        assertTrue(Algorithms.lessThan(v2, v1,tc));
    }
    
    // =============================================
    // Algorithms.lessThan(Iterator,Iterator,Functor)
    // =============================================
    
    public void testLessThanValuesFn() {
        Vector<String> v1 = new Vector<String>();
        Vector<String> v2 = new Vector<String>();
        Comparator<String> tc = new Samples.TestComparator();
        BinaryFunctor<String,String,Boolean> bf = new Less<String>(tc);

        v1.add(FOO); v2.add(FOO);
        v1.add(BAR); v2.add(BAR);
        v1.add(FOO); v2.add(BAR);
        assertTrue(Algorithms.lessThan(v1, v2,bf));
        assertFalse(Algorithms.lessThan(v2,v1,bf));
    }
    
    public void testLessThanValuesFnVar() {
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        Vector<DerivedObject> v2 = new Vector<DerivedObject>();
        BinaryFunctor<SampleObject,SampleObject,Boolean> bf = new Less<SampleObject>(soComp);
        
        v1.add(DOODADS);    v2.add(DOODADS);
        v1.add(DOOHICKEYS); v2.add(DOOHICKEYS);
        v1.add(DOODADS);    v2.add(DUDS);
        assertTrue(Algorithms.lessThan(v1, v2, bf));
        assertFalse(Algorithms.lessThan(v2, v1, bf));
        assertFalse(Algorithms.lessThan(v2, v2, bf));
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
        assertFalse(Algorithms.lessThan(v1,v2,bf));
        assertTrue(Algorithms.lessThan(v2, v1,bf));
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
        assertEquals(new Time(-99999000L), Algorithms.minimumValue(v2));
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
        assertEquals(FOO, Algorithms.minimumValue(v1, tc));
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
        assertEquals(FOO, Algorithms.minimumValue(v1, bf));
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
        assertEquals(NOW, Algorithms.maximumValue(v2));
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
        assertEquals(BAR, Algorithms.maximumValue(v1, tc));
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
        assertEquals(BAR, Algorithms.maximumValue(v1, bf));
    }


    // =====================================
    // accumulate(Collection,value,functor)
    // =====================================

    public void testAccumulateNoInit() {
        Integer[] ints = {
            new Integer(1), new Integer(2), new Integer(3), new Integer(4) };
        List<Integer> data = Arrays.asList(ints);
        Integer sum = Algorithms.accumulate(Integer.class, data);
        assertEquals(new Integer(10), sum);
    }

    public void testAccumulateInit() {
        Integer[] ints = {
            new Integer(1), new Integer(2), new Integer(3), new Integer(4) };
        List<Integer> data = Arrays.asList(ints);
        Integer sum = Algorithms.accumulate(data, new Integer(0),
                                           new Plus<Integer>(Integer.class));
        assertEquals(new Integer(10), sum);
    }

    public void testAccumulateFn() {
        Long[] longs = { new Long(1L),new Long(3L),new Long(4L),new Long(2L) };
        List<Long> data = Arrays.asList(longs);
        Long sum = Algorithms.accumulate(data, new Long(-2L), new Max.Comparable<Long>());
        assertEquals(new Long(4L), sum);
    }

    // =======================
    // transform(List,Functor)
    // =======================

    public void testTransformList() {
        Vector<Date> v1 = new Vector<Date>();
        v1.add(new Date(45L*DAYS));
        v1.add(new Date(90L*DAYS));
        v1.add(new Date(-15L*DAYS));

        Algorithms.transform(v1, nextDay);
        Iterator<Date> i1 = v1.iterator();
        assertEquals(new Date(46L*DAYS), i1.next());
        assertEquals(new Date(91L*DAYS), i1.next());
        assertEquals(new Date(-14L*DAYS),i1.next());
        assertFalse(i1.hasNext());

        // NOTE: transform(List,Functor) can't be covariant, as it effectively
        // both reads and writes to the list.
    }
    
    // ============================
    // transform(List,List,Functor)
    // ============================

    public void testTransformCopy() {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(45L*DAYS));
        v1a.add(new Date(90L*DAYS));
        v1a.add(new Date(-15L*DAYS));

        // List<Date>,List<Date>
        Vector<Date> v1b = new Vector<Date>();
        Algorithms.transformCopy(v1a, v1b, nextDay);
        Iterator<Date> i1b = v1b.iterator();
        assertEquals(new Date(46L*DAYS), i1b.next());
        assertEquals(new Date(91L*DAYS), i1b.next());
        assertEquals(new Date(-14L*DAYS),i1b.next());
        assertFalse(i1b.hasNext());

        // List<? extends Date>,List<Object>
        Vector<? extends Date> v2a = v1a;
        Vector<Object> v2b = new Vector<Object>();
        Algorithms.transformCopy(v2a, v2b, nextDay);
        Iterator<Object> i2b = v2b.iterator();
        assertEquals(new Date(46L*DAYS), i2b.next());
        assertEquals(new Date(91L*DAYS), i2b.next());
        assertEquals(new Date(-14L*DAYS),i2b.next());
        assertFalse(i2b.hasNext());

        // List<Time>, List<Object>
        Vector<Time> v3a = new Vector<Time>();
        v3a.add(new Time(-18L*HRS));
        v3a.add(new Time(-1L));
        
        Vector<Object> v3b = new Vector<Object>();
        Algorithms.transformCopy(v3a, v3b, nextDay);
        Iterator<Object> i3b = v3b.iterator();
        assertEquals(new Date(6L*HRS), i3b.next());
        assertEquals(new Date(DAYS-1L), i3b.next());
        assertFalse(i3b.hasNext());
    }
    
    // =================================
    // transform(List,List,List,Functor)
    // =================================

    public void testBinaryTransformCopy() {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(45L*DAYS));
        v1a.add(new Date(90L*DAYS));
        v1a.add(new Date(-15L*DAYS));

        Vector<Date> v1b = new Vector<Date>();
        v1b.add(new Date(32L*DAYS));
        v1b.add(new Date(95L*DAYS));
        v1b.add(new Date(0L*DAYS));
        
        // List<Date>,List<Date>,List<Date>
        Vector<Date> v1c = new Vector<Date>();
        Algorithms.transformCopy(v1a, v1b, v1c, new Max.Comparable<Date>());
        Iterator<Date> i1c = v1c.iterator();
        assertEquals(new Date(45L*DAYS),i1c.next());
        assertEquals(new Date(95L*DAYS),i1c.next());
        assertEquals(new Date(0L*DAYS), i1c.next());
        assertFalse(i1c.hasNext());

        // List<? extends Date>,List<Time>,List<Object>
        Vector<? extends Date> v2a = v1a;
        Vector<Time> v2b = new Vector<Time>();
        v2b.add(new Time(12L*HRS));
        v2b.add(new Time(18L*HRS));
        v2b.add(new Time(0L*HRS));
        Vector<Object> v2c = new Vector<Object>();
        Algorithms.transformCopy(v2a, v2b, v2c, new Max.Comparable<Date>());
        Iterator<Object> i2c = v2c.iterator();
        assertEquals(new Date(45L*DAYS),i2c.next());
        assertEquals(new Date(90L*DAYS),i2c.next());
        assertEquals(new Date(0L*DAYS), i2c.next());
        assertFalse(i2c.hasNext());
    }
    
    // ===================================
    // replaceAll(List,Functor,Value)
    // ===================================
    
    public void testReplaceAll() {
        Vector<Date> v = new Vector<Date>();
        v.add(new Date(45L*DAYS));
        v.add(new Date(90L*DAYS));
        v.add(new Date(-15L*DAYS));

        UnaryFunctor<Date,Boolean> bce = new Less.Comparable<Date>().bind2nd(EPOCH);
        
        // List<Date>
        Algorithms.replaceAll(v, bce, EPOCH);
        Iterator<Date> iter = v.iterator();
        assertEquals(new Date(45L*DAYS), iter.next());
        assertEquals(new Date(90L*DAYS), iter.next());
        assertEquals(EPOCH,iter.next());
        assertFalse(iter.hasNext());
    }


    // ===================================
    // replaceAll(List,List,Functor,Value)
    // ===================================
    
    public void testReplaceAllCopy() {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(45L*DAYS));
        v1a.add(new Date(90L*DAYS));
        v1a.add(new Date(-15L*DAYS));

        UnaryFunctor<Date,Boolean> bce = new Less.Comparable<Date>().bind2nd(EPOCH);
        
        // List<Date>,List<Date>
        Vector<Date> v1b = new Vector<Date>();
        Algorithms.replaceAllCopy(v1a,v1b,bce,EPOCH);
        Iterator<Date> i1b = v1b.iterator();
        assertEquals(new Date(45L*DAYS), i1b.next());
        assertEquals(new Date(90L*DAYS), i1b.next());
        assertEquals(EPOCH,i1b.next());
        assertFalse(i1b.hasNext());

        // List<? extends Date>,List<? super Date>
        Vector<? extends Date> v2a = v1a;
        Vector<? super Date> v2b = new Vector<Object>();
        Algorithms.replaceAllCopy(v2a, v2b, bce, EPOCH);
        Iterator<? super Date> i2b = v2b.iterator();
        assertEquals(new Date(45L*DAYS), i2b.next());
        assertEquals(new Date(90L*DAYS), i2b.next());
        assertEquals(EPOCH,i2b.next());
        assertFalse(i2b.hasNext());

        // List<Time>,List<Object>
        Vector<Time> v3a = new Vector<Time>();
        v3a.add(new Time(-18L*HRS));
        v3a.add(new Time(18L*HRS));
        Vector<Object> v3b = new Vector<Object>();
        Algorithms.replaceAllCopy(v3a, v3b, bce, EPOCH);
        Iterator<Object> i3b = v3b.iterator();
        assertEquals(EPOCH, i3b.next());
        assertEquals(new Date(18L*HRS), i3b.next());
        assertFalse(i3b.hasNext());
    }

    // ===================================
    // removeAll(List,Value)
    // ===================================

    public void testRemoveAll() {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(-15L*DAYS));
        v1a.add(new Date(45L*DAYS));
        v1a.add(new Date(-15L*DAYS));
        v1a.add(new Date(90L*DAYS));
        v1a.add(new Date(-15L*DAYS));

        Algorithms.removeAll(v1a, new Date(-15L*DAYS));
        assertEquals(2, v1a.size());
        assertEquals(new Date(45L*DAYS), v1a.elementAt(0));
        assertEquals(new Date(90L*DAYS), v1a.elementAt(1));
    }
    
    public void testRemoveAllVar() {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(-15L*DAYS));
        v1a.add(new Date(45L*DAYS));
        v1a.add(new Date(-15L*DAYS));
        v1a.add(new Date(90L*DAYS));
        v1a.add(new Date(-15L*DAYS));

        Vector<? extends Date> v2a = v1a;

        Algorithms.removeAll(v2a, new Date(-15L*DAYS));
        assertEquals(2, v2a.size());
        assertEquals(new Date(45L*DAYS), v2a.elementAt(0));
        assertEquals(new Date(90L*DAYS), v2a.elementAt(1));
    }
    
    public void testRemoveAllVar2() {
        Vector<Time> v1a = new Vector<Time>();
        v1a.add(new Time(-15L*HRS));
        v1a.add(new Time(4L*HRS));
        v1a.add(new Time(-15L*HRS));
        v1a.add(new Time(9L*HRS));
        v1a.add(new Time(-15L*HRS));
        
        Algorithms.removeAll(v1a, new Date(-15L*HRS));
        assertEquals(2, v1a.size());
        assertEquals(new Date(4L*HRS), v1a.elementAt(0));
        assertEquals(new Date(9L*HRS), v1a.elementAt(1));
    }

    // ===================================
    // removeAll(List,Value,Equality)
    // ===================================

    public void testRemoveAllEq() {
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        v1.add(DUDS);       
        v1.add(DOODADS);    
        v1.add(DUDS);       
        v1.add(DOOHICKEYS); 
        v1.add(DUDS);       

        Algorithms.removeAll(v1, DUDS, eq);
        assertEquals(2, v1.size());
        assertEquals(DOODADS, v1.elementAt(0));
        assertEquals(DOOHICKEYS, v1.elementAt(1));
    }
    
    public void testRemoveAllEqVar() {
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v1 = new Vector<SampleObject>();
        v1.add(DUDS);       
        v1.add(DOODADS);    
        v1.add(DUDS);       
        v1.add(DOOHICKEYS); 
        v1.add(DUDS);       

        Vector<? extends SampleObject> v2 = v1;
        Algorithms.removeAll(v2, DUDS, eq);
        assertEquals(2, v2.size());
        assertEquals(DOODADS, v2.elementAt(0));
        assertEquals(DOOHICKEYS, v2.elementAt(1));
    }
    
    public void testRemoveAllEqVar2() {
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<DerivedObject> v1 = new Vector<DerivedObject>();
        v1.add(DUDS);       
        v1.add(DOODADS);    
        v1.add(DUDS);       
        v1.add(DOOHICKEYS); 
        v1.add(DUDS);       

        Algorithms.removeAll(v1, DUDS, eq);
        assertEquals(2, v1.size());
        assertEquals(DOODADS, v1.elementAt(0));
        assertEquals(DOOHICKEYS, v1.elementAt(1));
    }
    
    // ===================================
    // removeAll(List,Functor)
    // ===================================

    public void testRemoveAllFn() {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(20L*HRS));
        v1a.add(new Date(45L*HRS));
        v1a.add(new Date(15L*HRS));
        v1a.add(new Date(90L*HRS));
        v1a.add(new Date(1L*HRS));

        Algorithms.removeAll(v1a, isEpochDay);
        assertEquals(2, v1a.size());
        assertEquals(new Date(45L*HRS), v1a.elementAt(0));
        assertEquals(new Date(90L*HRS), v1a.elementAt(1));
    }
    
    public void testRemoveAllFnVar() {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(20L*HRS));
        v1a.add(new Date(45L*HRS));
        v1a.add(new Date(15L*HRS));
        v1a.add(new Date(90L*HRS));
        v1a.add(new Date(1L*HRS));

        Vector<? extends Date> v2a = v1a;

        Algorithms.removeAll(v2a, isEpochDay);
        assertEquals(2, v2a.size());
        assertEquals(new Date(45L*HRS), v2a.elementAt(0));
        assertEquals(new Date(90L*HRS), v2a.elementAt(1));
    }
    
    public void testRemoveAllFnVar2() {
        Vector<Time> v1a = new Vector<Time>();
        v1a.add(new Time(20L*HRS));
        v1a.add(new Time(-4L*HRS));
        v1a.add(new Time(15L*HRS));
        v1a.add(new Time(-9L*HRS));
        v1a.add(new Time(0L*HRS));
        
        Algorithms.removeAll(v1a, isEpochDay);
        assertEquals(2, v1a.size());
        assertEquals(new Date(-4L*HRS), v1a.elementAt(0));
        assertEquals(new Date(-9L*HRS), v1a.elementAt(1));
    }

    // ===================================
    // removeAllCopy(List,List,Value)
    // ===================================

    public void testRemoveAllCopy() {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(-15L*DAYS));
        v1a.add(new Date(45L*DAYS));
        v1a.add(new Date(-15L*DAYS));
        v1a.add(new Date(90L*DAYS));
        v1a.add(new Date(-15L*DAYS));

        // Collection<Date>,Collection<Date>
        Vector<Date> v1b = new Vector<Date>();
        Algorithms.removeAllCopy(v1a, v1b, new Date(-15L*DAYS));
        assertEquals(2, v1b.size());
        assertEquals(new Date(45L*DAYS), v1b.elementAt(0));
        assertEquals(new Date(90L*DAYS), v1b.elementAt(1));

        // Collection<? extends Date>, Collection<? super Date>
        Vector<? extends Date> v2a = v1a;
        Vector<? super Date> v2b = new Vector<Object>();
        Algorithms.removeAllCopy(v2a, v2b,  new Date(-15L*DAYS));
        assertEquals(2, v2b.size());
        assertEquals(new Date(45L*DAYS), v2b.elementAt(0));
        assertEquals(new Date(90L*DAYS), v2b.elementAt(1));

        // Collection<Time>, Collection<Object>
        Vector<Time> v3a = new Vector<Time>();
        v3a.add(new Time(-15L*HRS));
        v3a.add(new Time(4L*HRS));
        v3a.add(new Time(-15L*HRS));
        v3a.add(new Time(9L*HRS));
        v3a.add(new Time(-15L*HRS));

        Vector<Object> v3b = new Vector<Object>();
        
        Algorithms.removeAllCopy(v3a, v3b, new Date(-15L*HRS));
        assertEquals(2, v3b.size());
        assertEquals(new Date(4L*HRS), v3b.elementAt(0));
        assertEquals(new Date(9L*HRS), v3b.elementAt(1));
    }

    // ===================================
    // removeAllCopy(List,Value,Equality)
    // ===================================

    public void testRemoveAllCopyEq() {
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v1a = new Vector<SampleObject>();
        v1a.add(DUDS);       
        v1a.add(DOODADS);    
        v1a.add(DUDS);       
        v1a.add(DOOHICKEYS); 
        v1a.add(DUDS);       

        Vector<SampleObject> v1b = new Vector<SampleObject>();
        Algorithms.removeAllCopy(v1a, v1b, DUDS, eq);
        assertEquals(5, v1a.size());
        assertEquals(2, v1b.size());
        assertEquals(DOODADS, v1b.elementAt(0));
        assertEquals(DOOHICKEYS, v1b.elementAt(1));

        Vector<? extends SampleObject> v2a = v1a;
        Vector<? super SampleObject> v2b = new Vector<Object>();
        Algorithms.removeAllCopy(v2a, v2b, DUDS, eq);
        assertEquals(5, v2a.size());
        assertEquals(2, v2b.size());
        assertEquals(DOODADS, v2b.elementAt(0));
        assertEquals(DOOHICKEYS, v2b.elementAt(1));

        Vector<DerivedObject> v3a = new Vector<DerivedObject>();
        v3a.add(DUDS);       
        v3a.add(DOODADS);    
        v3a.add(DUDS);       
        v3a.add(DOOHICKEYS); 
        v3a.add(DUDS);       

        Vector<Object> v3b = new Vector<Object>();
        Algorithms.removeAllCopy(v3a, v3b, DUDS, eq);
        assertEquals(5, v3a.size());
        assertEquals(2, v3b.size());
        assertEquals(DOODADS, v3b.elementAt(0));
        assertEquals(DOOHICKEYS, v3b.elementAt(1));
    }
    
    // ===================================
    // removeAll(List,Functor)
    // ===================================

    public void testRemoveAllCopyFn() {
        Vector<Date> v1a = new Vector<Date>();
        v1a.add(new Date(20L*HRS));
        v1a.add(new Date(45L*HRS));
        v1a.add(new Date(15L*HRS));
        v1a.add(new Date(90L*HRS));
        v1a.add(new Date(1L*HRS));

        Vector<Date> v1b = new Vector<Date>();
        Algorithms.removeAllCopy(v1a, v1b, isEpochDay);
        assertEquals(5, v1a.size());
        assertEquals(2, v1b.size());
        assertEquals(new Date(45L*HRS), v1b.elementAt(0));
        assertEquals(new Date(90L*HRS), v1b.elementAt(1));

        Vector<? extends Date> v2a = v1a;
        Vector<? super Date> v2b = new Vector<Object>();

        Algorithms.removeAllCopy(v2a, v2b, isEpochDay);
        assertEquals(5, v2a.size());
        assertEquals(2, v2b.size());
        assertEquals(new Date(45L*HRS), v2b.elementAt(0));
        assertEquals(new Date(90L*HRS), v2b.elementAt(1));

        Vector<Time> v3a = new Vector<Time>();
        v3a.add(new Time(20L*HRS));
        v3a.add(new Time(-4L*HRS));
        v3a.add(new Time(15L*HRS));
        v3a.add(new Time(-9L*HRS));
        v3a.add(new Time(0L*HRS));

//         Vector<Object> v3b = new Vector<Object>();
//         Algorithms.removeAllCopy(v3a, v3b, isEpochDay);
//         assertEquals(5, v3a.size());
//         assertEquals(2, v3b.size());
//         assertEquals(new Date(-4L*HRS), v3b.elementAt(0));
//         assertEquals(new Date(-9L*HRS), v3b.elementAt(1));
    }

    // ===================================
    // unique(List,Functor)
    // ===================================

    public void testUniqueFn() {
        SampleObjectComparator soComp = new SampleObjectComparator();
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v = new Vector<SampleObject>();
        v.add(WIDGETS);
        v.add(WIDGETS);
        v.add(MUMBLES);
        v.add(MUMBLES);
        v.add(WIDGETS);
        v.add(GIZMOS);
        v.add(GIZMOS);

        Algorithms.unique(v, eq);
        assertEquals(4, v.size());
        assertEquals(WIDGETS,v.elementAt(0));
        assertEquals(MUMBLES,v.elementAt(1));
        assertEquals(WIDGETS,v.elementAt(2));
        assertEquals(GIZMOS, v.elementAt(3));
    }
    
    public void testUniqueFn2() {
        SampleObjectComparator soComp = new SampleObjectComparator();
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<DerivedObject> v = new Vector<DerivedObject>();
        v.add(DOODADS);
        v.add(DOODADS);
        v.add(DOOHICKEYS);
        v.add(DOOHICKEYS);
        v.add(DOODADS);
        v.add(DUDS);
        v.add(DUDS);

        Algorithms.unique(v, eq);
        assertEquals(4, v.size());
        assertEquals(DOODADS,v.elementAt(0));
        assertEquals(DOOHICKEYS,v.elementAt(1));
        assertEquals(DOODADS,v.elementAt(2));
        assertEquals(DUDS, v.elementAt(3));
    }
    
    // =========================================
    // uniqueCopy(Collection,Collection,Functor)
    // =========================================

    public void testUniqueCopyFn() {
        SampleObjectComparator soComp = new SampleObjectComparator();
        Equality<SampleObject> eq = new EqualTo<SampleObject>(soComp);
        Vector<SampleObject> v1a = new Vector<SampleObject>();
        v1a.add(DOODADS);
        v1a.add(DOODADS);
        v1a.add(DOOHICKEYS);
        v1a.add(DOOHICKEYS);
        v1a.add(DOODADS);
        v1a.add(DUDS);
        v1a.add(DUDS);

        Vector<SampleObject> v1b = new Vector<SampleObject>();
        Algorithms.uniqueCopy(v1a, v1b, eq);
        assertEquals(7, v1a.size());
        assertEquals(4, v1b.size());
        assertEquals(DOODADS,v1b.elementAt(0));
        assertEquals(DOOHICKEYS,v1b.elementAt(1));
        assertEquals(DOODADS,v1b.elementAt(2));
        assertEquals(DUDS, v1b.elementAt(3));

        Vector<? extends SampleObject> v2a = v1a;
        Vector<? super SampleObject> v2b = new Vector<Object>();
        Algorithms.uniqueCopy(v2a, v2b, eq);
        assertEquals(7, v2a.size());
        assertEquals(4, v2b.size());
        assertEquals(DOODADS,v2b.elementAt(0));
        assertEquals(DOOHICKEYS,v2b.elementAt(1));
        assertEquals(DOODADS,v2b.elementAt(2));
        assertEquals(DUDS, v2b.elementAt(3));

        Vector<DerivedObject> v3a = new Vector<DerivedObject>();
        v3a.add(DOODADS);
        v3a.add(DOODADS);
        v3a.add(DOOHICKEYS);
        v3a.add(DOOHICKEYS);
        v3a.add(DOODADS);
        v3a.add(DUDS);
        v3a.add(DUDS);
        
        Vector<Object> v3b = new Vector<Object>();
        Algorithms.uniqueCopy(v3a, v3b, eq);
        assertEquals(7, v3a.size());
        assertEquals(4, v3b.size());
        assertEquals(DOODADS,v3b.elementAt(0));
        assertEquals(DOOHICKEYS,v3b.elementAt(1));
        assertEquals(DOODADS,v3b.elementAt(2));
        assertEquals(DUDS, v3b.elementAt(3));
    }

    // =========================================
    // merge(Collection,Collection)
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

        Vector<Date> v1c = new Vector<Date>();
        Algorithms.mergeCopy(v1a, v1b, v1c);
        assertEquals(6, v1c.size());
        assertEquals(new Date(1L*HRS),  v1c.elementAt(0));
        assertEquals(new Date(15L*HRS), v1c.elementAt(1));
        assertEquals(new Date(20L*HRS), v1c.elementAt(2));
        assertEquals(new Date(36L*HRS), v1c.elementAt(3));
        assertEquals(new Date(45L*HRS), v1c.elementAt(4));
        assertEquals(new Date(90L*HRS), v1c.elementAt(5));

        Vector<? extends Date> v2a = v1a;
        Vector<? extends Date> v2b = v1b;
        Vector<? super Date> v2c = new Vector<Object>();
        Algorithms.mergeCopy(v2a, v2b, v2c);
        assertEquals(6, v2c.size());
        assertEquals(new Date(1L*HRS),  v2c.elementAt(0));
        assertEquals(new Date(15L*HRS), v2c.elementAt(1));
        assertEquals(new Date(20L*HRS), v2c.elementAt(2));
        assertEquals(new Date(36L*HRS), v2c.elementAt(3));
        assertEquals(new Date(45L*HRS), v2c.elementAt(4));
        assertEquals(new Date(90L*HRS), v2c.elementAt(5));

        Vector<Time> v3a = new Vector<Time>();
        v3a.add(new Time(20L*HRS));
        v3a.add(new Time(-4L*HRS));
        v3a.add(new Time(15L*HRS));
        v3a.add(new Time(-9L*HRS));

        Vector<Date> v3c = new Vector<Date>();
        Algorithms.mergeCopy(v3a,v2a,v3c);
        assertEquals(7, v3c.size());
        assertEquals(new Date(15L*HRS), v3c.elementAt(0));
        assertEquals(new Date(20L*HRS), v3c.elementAt(1));
        assertEquals(new Date(-4L*HRS), v3c.elementAt(2));
        assertEquals(new Date(15L*HRS), v3c.elementAt(3));
        assertEquals(new Date(-9L*HRS), v3c.elementAt(4));
        assertEquals(new Date(20L*HRS), v3c.elementAt(5));
        assertEquals(new Date(45L*HRS), v3c.elementAt(6));
    }


    // =========================================
    // merge(Collection,Collection,Comparator)
    // =========================================

    // =========================================================================
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestAlgorithms.class);
    } 
}

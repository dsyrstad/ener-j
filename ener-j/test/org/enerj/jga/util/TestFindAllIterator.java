// ============================================================================
// $Id: TestFindAllIterator.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import junit.framework.TestCase;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.algorithm.FindAdjacent;
import org.enerj.jga.fn.algorithm.FindElement;
import org.enerj.jga.fn.algorithm.FindMismatch;
import org.enerj.jga.fn.algorithm.FindRepeated;
import org.enerj.jga.fn.algorithm.FindSequence;
import org.enerj.jga.fn.arithmetic.Minus;
import org.enerj.jga.fn.comparison.Between;
import org.enerj.jga.fn.property.GetProperty;


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

public class TestFindAllIterator extends TestCase {
    static public final long HRS = 60L * 60L * 1000L;
    static public final long DAYS = 24L * HRS;

    static public final Date NOW = new Date();
    static public final Date EPOCH = new Date(0L);
    static public final Date DAY_1 = new Date(DAYS);

    static public final Time TIME_0 = new Time(0L);
    
    static public GetProperty<Date,Long> getTime =
        new GetProperty<Date,Long>(Date.class,"Time");
    
    static public BinaryFunctor<Date,Date,Boolean> within24Hrs =
        new Between.Comparable<Long>(new Long(-DAYS), new Long(DAYS))
                .compose(new Minus<Long>(Long.class)
                        .distribute(getTime, getTime));
    
    Vector<Date> list = new Vector<Date>();

    public void setUp() {
        list.add(NOW);
        list.add(NOW);
        list.add(EPOCH);      
        list.add(new Time(999999L));
        list.add(new Date(-99999L)); 
        list.add(NOW);
        list.add(NOW);
        list.add(EPOCH);      
        list.add(TIME_0);
        list.add(new Time(-99999L));
        list.add(TIME_0);
        list.add(TIME_0);
        list.add(NOW);
    }

    public void tearDown() {
    }

    public void testFindAllAdj () {
        FindAllIterator<Date> i1 =
            new FindAllIterator<Date>(list.iterator(),new FindAdjacent<Date>());

        assertTrue(i1.hasNext());
        Iterator<? extends Date> i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(EPOCH, i2.next());
        
        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        
        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(EPOCH, i2.next());
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        
        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());

        assertFalse(i1.hasNext());
    }
        
    public void testFindAllAdjWC () {
        Vector<? extends Date> v1 = list;
        FindAllIterator<Date> i1 =
            new FindAllIterator<Date>(v1.iterator(), new FindAdjacent<Date>());

        assertTrue(i1.hasNext());
        Iterator<? extends Date> i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(EPOCH, i2.next());
  
        assertTrue(i1.hasNext());
        i2 = i1.next(); 
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
  
        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(EPOCH, i2.next());
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
  
        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());

        assertFalse(i1.hasNext());
    }

    
    public void testFindAllAdjVar () {
        Vector<Time> v3 = new Vector<Time>();
        Time tp18 = new Time( 18L*HRS);
        Time tm18 = new Time(-18L*HRS);
     
        v3.add(TIME_0);      
        v3.add(tp18);
        v3.add(tm18); // 36 Hrs apart
        v3.add(tp18);
        v3.add(tm18);
        v3.add(tp18);
        v3.add(TIME_0);
        v3.add(tm18);
        v3.add(tp18);
        v3.add(tm18);
        v3.add(TIME_0);
        v3.add(tm18);
        v3.add(TIME_0);
        v3.add(tm18);
        v3.add(tp18);
        v3.add(tm18);

        FindAllIterator<Date> i1 = new FindAllIterator<Date>(v3.iterator(),
                                           new FindAdjacent<Date>(within24Hrs));
        assertTrue(i1.hasNext());
        Iterator<? extends Date> i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        assertTrue(i2.hasNext()); assertEquals(tp18,   i2.next());
        assertTrue(i2.hasNext()); assertEquals(tm18,   i2.next());

        assertTrue(i1.hasNext());
        i2 = i1.next(); 
        assertTrue(i2.hasNext()); assertEquals(tp18,   i2.next());
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        assertTrue(i2.hasNext()); assertEquals(tm18,   i2.next());
        assertTrue(i2.hasNext()); assertEquals(tp18,   i2.next());

        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(tm18,   i2.next());
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        assertTrue(i2.hasNext()); assertEquals(tm18,   i2.next());
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());

        assertFalse(i1.hasNext());
    }

    public void testNoHasHextCalls() {
        FindAllIterator<Date> i1 =
            new FindAllIterator<Date>(list.iterator(),new FindAdjacent<Date>());

        assertTrue(i1.hasNext());
        Iterator<? extends Date> i2 = i1.next();
        assertEquals(NOW, i2.next());
        assertEquals(NOW, i2.next());
        assertEquals(EPOCH, i2.next());
        
        i2 = i1.next();
        assertEquals(NOW, i2.next());
        assertEquals(NOW, i2.next());
        
        i2 = i1.next();
        assertEquals(EPOCH, i2.next());
        assertEquals(TIME_0, i2.next());
        
        i2 = i1.next();
        assertEquals(TIME_0, i2.next());
        assertEquals(TIME_0, i2.next());

        assertFalse(i1.hasNext());
    }


    public void testTooManyHasHextCalls() {
        FindAllIterator<Date> i1 =
            new FindAllIterator<Date>(list.iterator(),new FindAdjacent<Date>());

        assertTrue(i1.hasNext());
        assertTrue(i1.hasNext());
        Iterator<? extends Date> i2 = i1.next();
        assertTrue(i2.hasNext());assertTrue(i2.hasNext());
        assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext());assertTrue(i2.hasNext());
        assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext());assertTrue(i2.hasNext());
        assertEquals(EPOCH,i2.next());
        
        assertTrue(i1.hasNext());
        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext());assertTrue(i2.hasNext());
        assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext());assertTrue(i2.hasNext());
        assertEquals(NOW, i2.next());
        
        assertTrue(i1.hasNext());
        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext());assertTrue(i2.hasNext());
        assertEquals(EPOCH,i2.next());
        assertTrue(i2.hasNext());assertTrue(i2.hasNext());
        assertEquals(TIME_0,i2.next());
        
        assertTrue(i1.hasNext());
        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext());assertTrue(i2.hasNext());
        assertEquals(TIME_0,i2.next());
        assertTrue(i2.hasNext());assertTrue(i2.hasNext());
        assertEquals(TIME_0,i2.next());

        assertFalse(i1.hasNext());
    }
    
    public void testFindAllSequence () {
        List<Date> seq = new ArrayList<Date>();
        seq.add(NOW); seq.add(NOW); seq.add(EPOCH);
        FindAllIterator<Date> i1 = new FindAllIterator<Date>(list.iterator(),
                                                 new FindSequence<Date>(seq));

        assertTrue(i1.hasNext());
        Iterator<? extends Date> i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(EPOCH, i2.next());
        assertTrue(i2.hasNext()); assertEquals(new Time(999999L), i2.next());
        
        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(EPOCH, i2.next());
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        
        assertFalse(i1.hasNext());
    }
        
    public void testFindAllElements () {
        List<Date> seq = new ArrayList<Date>();
        seq.add(NOW); seq.add(EPOCH);

        // the default uses EqualTo, which will give misleading results.
        FindAllIterator<Date> i1 = new FindAllIterator<Date>(list.iterator(),
                                                 new FindElement<Date>(seq));
        assertTrue(i1.hasNext());
        Iterator<? extends Date> i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        
        assertTrue(i1.hasNext()); i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        
        assertTrue(i1.hasNext()); i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(EPOCH, i2.next());
        
        assertTrue(i1.hasNext()); i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        
        assertTrue(i1.hasNext()); i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        
        assertTrue(i1.hasNext()); i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(EPOCH, i2.next());
        
        assertTrue(i1.hasNext()); i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        
        assertTrue(i1.hasNext()); i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        
        assertTrue(i1.hasNext()); i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        
        assertTrue(i1.hasNext()); i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        
        assertFalse(i1.hasNext());
    }
    
    public void testFindAllRepeated () {
        FindAllIterator<Date> i1 = new FindAllIterator<Date>(list.iterator(),
                                                 new FindRepeated<Date>(2,NOW));

        assertTrue(i1.hasNext());
        Iterator<? extends Date> i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(EPOCH, i2.next());
        assertTrue(i2.hasNext()); assertEquals(new Time(999999L), i2.next());
        
        assertTrue(i1.hasNext());
        i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(NOW, i2.next());
        assertTrue(i2.hasNext()); assertEquals(EPOCH, i2.next());
        assertTrue(i2.hasNext()); assertEquals(TIME_0, i2.next());
        
        assertFalse(i1.hasNext());
    }
        
        
    public void testFindAllMismatch () {
        Vector<Date> vector = new Vector<Date>(list.size());
        vector.addAll(list);
        vector.set(4, NOW);
        vector.set(9, EPOCH);
        FindMismatch<Date> mismatch = new FindMismatch<Date>(vector);
        FindAllIterator<Date> i1 =
            new FindAllIterator<Date>(list.iterator(),mismatch);

        assertTrue(i1.hasNext());
        Iterator<? extends Date> i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(new Time(-99999L), i2.next());
        assertEquals(NOW, mismatch.getMismatchedElement());
        
        i2 = i1.next();
        assertTrue(i2.hasNext()); assertEquals(new Time(-99999L), i2.next());
        assertEquals(EPOCH, mismatch.getMismatchedElement());
        
        assertFalse(i1.hasNext());
    }
    
    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestFindAllIterator.class);
    }
}

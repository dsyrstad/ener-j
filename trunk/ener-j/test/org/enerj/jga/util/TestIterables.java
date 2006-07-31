// ============================================================================
// $Id: TestIterables.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
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

import java.util.ArrayList;
import java.util.Iterator;
import junit.framework.TestCase;
import org.enerj.jga.SampleObject;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ConstantBinary;
import org.enerj.jga.fn.adaptor.Project1st;
import org.enerj.jga.fn.algorithm.FindSequence;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.fn.comparison.Min;
import org.enerj.jga.fn.comparison.NotEqualTo;
import org.enerj.jga.fn.logical.LogicalAnd;
import org.enerj.jga.fn.property.GetProperty;
import org.enerj.jga.fn.string.Match;

import static org.enerj.jga.util.ArrayIterator.*;
import static org.enerj.jga.util.Iterables.*;
import static org.enerj.jga.util.Iterators.*;

// resolve ambiguities
import static org.enerj.jga.util.Iterables.merge;
import static org.enerj.jga.util.Iterables.transform;

/**
 * Exercises Iterables
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class TestIterables extends TestCase  {
    public TestIterables (String name){ super(name); }
    
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";

    public void testFindAll() {
        ArrayList<String> v1 = new ArrayList<String>();
        v1.add(FOO);
        v1.add(BAR);
        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAR);
        v1.add(FOO);
        v1.add(BAR);
        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAZ);
        v1.add(FOO);
        v1.add(BAR);
        v1.add(FOO);

        ArrayList<String> seq = new ArrayList<String>();
        seq.add(BAR); seq.add(FOO);

        UnaryFunctor<Iterator<? extends String>,? extends Iterator<String>>
            findSeq = new FindSequence<String>(seq);

        int count = 0;
        for(Iterator<? extends String> i : findAll(v1, findSeq)) {
            assertEquals(BAR, i.next());
            assertEquals(FOO, i.next());
            ++count;
        }

        assertEquals(4, count);
    }

    
    public void testFindAllArr() {
        String[] v1 = new String[] { FOO,BAR,FOO,BAR,BAR,FOO,BAR,FOO,BAR,BAZ,FOO,BAR,FOO };
        
        ArrayList<String> seq = new ArrayList<String>();
        seq.add(BAR); seq.add(FOO);

        UnaryFunctor<Iterator<? extends String>,? extends Iterator<String>>
            findSeq = new FindSequence<String>(seq);

        int count = 0;
        for(Iterator<? extends String> i : findAll(v1, findSeq)) {
            assertEquals(BAR, i.next());
            assertEquals(FOO, i.next());
            ++count;
        }

        assertEquals(4, count);
    }
    
    
    public void testFilter(){
        ArrayList<String> v1 = new ArrayList<String>();
        ArrayList<String> xp = new ArrayList<String>();

        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAZ);
        v1.add(QLX);

        xp.add(BAR);
        xp.add(BAZ);

        int i = 0;
        UnaryFunctor<String,Boolean> startsWith_b = new Match("_b.*");
        for(String s : filter(v1, startsWith_b)) {
            assertEquals(xp.get(i++), s);
        }
    }

    
    public void testFilterArr(){
        String[] v1 = new String[] { FOO, BAR, BAZ, QLX };
        String[] xp = new String[] { BAR, BAZ };

        UnaryFunctor<String,Boolean> startsWith_b = new Match("_b.*");

        int i = 0;
        for(String s : filter(v1, startsWith_b)) {
            assertEquals(xp[i++], s);
        }
    }

    
    public void testFilterLookAhead(){
        ArrayList<String> v1 = new ArrayList<String>();
        ArrayList<String> xp = new ArrayList<String>();

        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAZ);
        v1.add(QLX);

        xp.add(BAR);
        xp.add(BAZ);
        xp.add(QLX);

        UnaryFunctor<String,Boolean> startsWith_b = new Match("_b.*");

        int i = 0;
        LookAheadIterator<String> finder = new LookAheadIterator<String>(v1.iterator());
        for(String s : filter(finder, startsWith_b)) {
            assertEquals(xp.get(i), s);
            assertEquals(xp.get(i+1), finder.peek(1));
            ++i;
        }
    }

    
    public void testFilterLookAheadArr(){
        String[] v1 = new String[] { FOO, BAR, BAZ, QLX };
        String[] xp = new String[] { BAR, BAZ, QLX };

        UnaryFunctor<String,Boolean> startsWith_b = new Match("_b.*");

        int i = 0;
        LookAheadIterator<String> finder = new LookAheadIterator<String>(iterate(v1));
        for(String s : filter(finder, startsWith_b)) {
            assertEquals(xp[i], s);
            assertEquals(xp[i+1], finder.peek(1));
            ++i;
        }
    }

    
    public void testMerge(){
        ArrayList<String> v1 = new ArrayList<String>();
        ArrayList<String> v2 = new ArrayList<String>();
        ArrayList<String> xp = new ArrayList<String>();

        v1.add(BAR);
        v1.add(FOO);
        v1.add(QLX);

        v2.add(BAZ);

        xp.add(BAR);
        xp.add(BAZ);
        xp.add(FOO);
        xp.add(QLX);

        int i = 0;
        for(String s : merge(v1, v2)) {
            assertEquals(xp.get(i++), s);
        }
    }
    
    
    public void testMergeArr(){
        String[] v1 = new String[]{ BAR,FOO,QLX };
        String[] v2 = new String[]{ BAZ };
        String[] xp = new String[]{ BAR,BAZ,FOO,QLX };

        int i = 0;
        for(String s : merge(v1, v2)) {
            assertEquals(xp[i++], s);
        }
    }
    
    
    public void testMergeFirst(){
        ArrayList<String> v1 = new ArrayList<String>();
        ArrayList<String> v2 = new ArrayList<String>();
        ArrayList<String> xp = new ArrayList<String>();

        v1.add(FOO);
        v1.add(QLX);

        v2.add(BAR);
        v2.add(BAZ);

        xp.add(FOO);
        xp.add(QLX);
        xp.add(BAR);
        xp.add(BAZ);

        // Using constant TRUE on a merge is an AppendIterator
        BinaryFunctor<String,String,Boolean> pred =
            new ConstantBinary<String,String,Boolean>(Boolean.TRUE);
        
        int i = 0;
        for(String s : merge(v1, v2, pred)) {
            assertEquals(xp.get(i++), s);
        }

        // this pass would be ambiguous: see imports for resolution
        i = 0;
        for(String s : merge(new FindIterator<String>(v1.iterator()),
                             new FindIterator<String>(v2.iterator()), pred))
        {
            assertEquals(xp.get(i++), s);
        }
    }

    
    public void testMergeFirstArr(){
        String[] v1 = new String[] { FOO,QLX };
        String[] v2 = new String[] { BAR,BAZ };
        String[] xp = new String[] { FOO,QLX,BAR,BAZ };

        // Using constant TRUE on a merge is an AppendIterator
        BinaryFunctor<String,String,Boolean> pred =
            new ConstantBinary<String,String,Boolean>(Boolean.TRUE);
        
        int i = 0;
        for(String s : merge(v1, v2, pred)) {
            assertEquals(xp[i++], s);
        }

        // this pass would be ambiguous: see imports for resolution
        i = 0;
        for(String s : merge(new FindIterator<String>(iterate(v1)),
                             new FindIterator<String>(iterate(v2)), pred))
        {
            assertEquals(xp[i++], s);
        }
    }

    
    public void testTransform() {
        ArrayList<SampleObject>  v1 = new ArrayList<SampleObject>();
        ArrayList<String> xp = new ArrayList<String>();

        String APPLES = "apples";
        String ORANGES = "oranges";
        String PEARS = "pears";
        String GRAPES = "grapes";
        
        v1.add(new SampleObject(APPLES, 21));  xp.add(APPLES);
        v1.add(new SampleObject(ORANGES, 35)); xp.add(ORANGES);
        v1.add(new SampleObject(PEARS, 33));   xp.add(PEARS);
        v1.add(new SampleObject(GRAPES, 12));  xp.add(GRAPES);

        UnaryFunctor<SampleObject,String> getName =
            new GetProperty<SampleObject,String>(SampleObject.class,"Name");

        int i = 0;
        for(String s : transform(v1, getName)) {
            assertEquals(xp.get(i++), s);
        }
    }

    
    public void testTransformArr() {
        String APPLES = "apples";
        String ORANGES = "oranges";
        String PEARS = "pears";
        String GRAPES = "grapes";
        
        String[] xp = new String[] { APPLES, ORANGES, PEARS, GRAPES };
        SampleObject[] v1 = new SampleObject[] { new SampleObject(APPLES, 21),
                                                 new SampleObject(ORANGES, 35),
                                                 new SampleObject(PEARS, 33),
                                                 new SampleObject(GRAPES, 12)
                                               };

        int i = 0;
        UnaryFunctor<SampleObject,String> getName =
            new GetProperty<SampleObject,String>(SampleObject.class,"Name");
        for(String s : transform(v1, getName)) {
            assertEquals(xp[i++], s);
        }
    }

    
    public void testTransformAdj() {
        ArrayList<String> v1 = new ArrayList<String>();
        ArrayList<String> xp = new ArrayList<String>();

        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAZ);
        v1.add(QLX);

        xp.add(BAR);
        xp.add(BAR);
        xp.add(BAZ);
        
        BinaryFunctor<String,String,String> fn = new Min.Comparable<String>();
        
        int i = 0;
        for(String s : transform(v1, fn)) {
            assertEquals(xp.get(i++), s);
        }

        i = 0;
        CachingIterator<String> cache = new CachingIterator<String>(v1.iterator(),2);
        for(String s : transform(cache, fn)) {
            assertEquals(xp.get(i), s); // checks the result
            assertEquals(v1.get(i + 1), cache.cached(1)); // checks crnt object
            assertEquals(v1.get(i),     cache.cached(2)); // checks prev object
            ++i;
        }
    }

    
    public void testTransformAdjArr() {
        String[] v1 = new String[] { FOO,BAR,BAZ,QLX };
        String[] xp = new String[] { BAR,BAR,BAZ };
        
        int i = 0;
        BinaryFunctor<String,String,String> fn = new Min.Comparable<String>();
        for(String s : transform(v1, fn)) {
            assertEquals(xp[i++], s);
        }

        i = 0;
        CachingIterator<String> cache = new CachingIterator<String>(iterate(v1),2);
        for(String s : transform(cache, fn)) {
            assertEquals(xp[i], s); // checks the result
            assertEquals(v1[i + 1], cache.cached(1)); // checks crnt object
            assertEquals(v1[i],     cache.cached(2)); // checks prev object
            ++i;
        }
    }

    
    public void testTransformBin() {
        ArrayList<String> v1 = new ArrayList<String>();
        ArrayList<String> v2 = new ArrayList<String>();
        ArrayList<String> xp = new ArrayList<String>();

        v1.add(FOO); v1.add(BAR); v1.add(BAZ); v1.add(QLX);
        v2.add(BAR); v2.add(BAZ); v2.add(FOO); v2.add(QLX);
        xp.add(BAR); xp.add(BAR); xp.add(BAZ); xp.add(QLX);

        BinaryFunctor<String,String,String> min = new Min.Comparable<String>();
    
        int i = 0;
        for(String s : transform(v1, v2, min)) {
            assertEquals(xp.get(i++), s);
        }
    }

    
    public void testTransformBinArr() {
        String[] v1 = new String[] { FOO,BAR,BAZ,QLX };
        String[] v2 = new String[] { BAR,BAZ,FOO,QLX };
        String[] xp = new String[] { BAR,BAR,BAZ,QLX };

        int i = 0;
        BinaryFunctor<String,String,String> min = new Min.Comparable<String>();
        for(String s : transform(v1, v2, min)) {
            assertEquals(xp[i++], s);
        }
    }

    
    public void testUnique(){
        ArrayList<String> v1 = new ArrayList<String>();
        ArrayList<String> xp = new ArrayList<String>();

        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAR);
        v1.add(BAR);
        v1.add(BAZ);
        v1.add(BAZ);
        v1.add(QLX);
        v1.add(QLX);
        v1.add(QLX);

        xp.add(FOO);
        xp.add(BAR);
        xp.add(BAZ);
        xp.add(QLX);

        int i = 0;
        for(String s : unique(v1)) {
            assertEquals(xp.get(i++), s);
        }

        i = 0;
        for(String s : unique(v1.iterator())) {
            assertEquals(xp.get(i++), s);
        }

        LookAheadIterator<String> lai =
            new LookAheadIterator<String>(v1.iterator());
        
        i = 0;
        for(String s : Iterables.unique(lai)) {
            assertEquals(xp.get(i++), s);
            if (!s.equals(FOO)) // FOO is not duplicated
                assertEquals(s,lai.peek(1));
        }
    }

    
    public void testUniqueArr(){
        String[] v1 = new String[] { FOO,BAR,BAR,BAR,BAZ,BAZ,QLX,QLX,QLX };
        String[] xp = new String[] { FOO,BAR,BAZ,QLX };

        int i = 0;
        for(String s : unique(v1)) {
            assertEquals(xp[i++], s);
        }

        i = 0;
        LookAheadIterator<String> lai = new LookAheadIterator<String>(iterate(v1));
        for(String s : Iterables.unique(lai)) {
            assertEquals(xp[i++], s);
            if (!s.equals(FOO)) // FOO is not duplicated
                assertEquals(s,lai.peek(1));
        }
    }

    
    public void testUniqueFn(){
        ArrayList<String> v1 = new ArrayList<String>();
        ArrayList<String> xp = new ArrayList<String>();

        v1.add(FOO);
        v1.add(BAR);
        v1.add(BAR);
        v1.add(BAR);
        v1.add(BAZ);
        v1.add(BAZ);
        v1.add(QLX);
        v1.add(QLX);
        v1.add(QLX);

        xp.add(FOO);
        xp.add(BAR);
        xp.add(QLX);
        xp.add(QLX);
        xp.add(QLX);

        UnaryFunctor<String,Boolean> startsWith_b = new Match("_b.*");
        BinaryFunctor<String,String,Boolean> bothStartWith_b =
            new LogicalAnd().distribute(startsWith_b, startsWith_b);

        int i = 0;
        for(String s : unique(v1, bothStartWith_b)) {
            assertEquals(xp.get(i++), s);
        }

        i = 0;
        for(String s : unique(v1.iterator(), bothStartWith_b)) {
            assertEquals(xp.get(i++), s);
        }

        LookAheadIterator<String> lai =
            new LookAheadIterator<String>(v1.iterator());
        
        i = 0;
        for(String s : Iterables.unique(lai, bothStartWith_b)) {
            assertEquals(xp.get(i++), s);
            if (!s.equals(FOO) && lai.hasNextPlus(1)) // FOO is not duplicated
                assertEquals(s,lai.peek(1));
        }
    }

    
    public void testUniqueFnArr(){
        String[] v1 = new String[] { FOO,BAR,BAR,BAR,BAZ,BAZ,QLX,QLX,QLX };
        String[] xp = new String[] { FOO,BAR,QLX,QLX,QLX };

        UnaryFunctor<String,Boolean> startsWith_b = new Match("_b.*");
        BinaryFunctor<String,String,Boolean> bothStartWith_b =
            new LogicalAnd().distribute(startsWith_b, startsWith_b);

        int i = 0;
        for(String s : unique(v1, bothStartWith_b)) {
            assertEquals(xp[i++], s);
        }

        i = 0;
        LookAheadIterator<String> lai = new LookAheadIterator<String>(iterate(v1));
        for(String s : Iterables.unique(lai, bothStartWith_b)) {
            assertEquals(xp[i++], s);
            if (!s.equals(FOO) && lai.hasNextPlus(1)) // FOO is not duplicated
                assertEquals(s,lai.peek(1));
        }
    }


    static public void main (String[] args) {
        junit.swingui.TestRunner.run(TestIterables.class);
    }
}

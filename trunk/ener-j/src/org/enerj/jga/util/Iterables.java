// ============================================================================
// $Id: Iterables.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
// ============================================================================
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;

import static org.enerj.jga.util.ArrayIterator.iterate;

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

public class Iterables {

    /**
     * Returns iterators that point to the next instance in an iterable that
     * meets the condition described in the functor.
     */
    static public <T> Iterable<Iterator<? extends T>>
    findAll(Iterable<? extends T> i, UnaryFunctor<Iterator<? extends T>, ? extends Iterator<T>> fn) {
        return new FindAllIterator<T>(i.iterator(), fn);
    }

    
    /**
     * Returns iterators that point to the next instance in an iterable that
     * meets the condition described in the functor.
     */
    static public <T> Iterable<Iterator<? extends T>>
    findAll(T[] ts, UnaryFunctor<Iterator<? extends T>, ? extends Iterator<T>> fn) {
        return new FindAllIterator<T>(iterate(ts), fn);
    }

    
    /**
     * Returns only elements for which the predicate is true
     */
    static public <T> Iterable<T> filter(Iterable<? extends T> i, UnaryFunctor<T,Boolean> pred) {
        return new FilterIterator<T>(i.iterator(), pred);
    }

    
    /**
     * Returns only elements for which the predicate is true
     */
    static public <T> Iterable<T> filter(T[] ts, UnaryFunctor<T,Boolean> pred) {
        return new FilterIterator<T>(iterate(ts), pred);
    }

    
    /**
     * Returns all elements of both iterables, always choosing the lesser of the
     * two current elements.
     */
    static public <T extends Comparable/*EA2.2:*/<T>/**/> Iterable<T>
    merge(Iterable<? extends T> i1, Iterable<? extends T> i2) {
        return new MergeIterator<T>(i1.iterator(), i2.iterator(), new ComparableComparator<T>());
    }

    
    /**
     * Returns all elements of both arrays, always choosing the lesser of the
     * two current elements.
     */
    static public <T extends Comparable/*EA2.2:*/<T>/**/> Iterable<T> merge(T[] ts1, T[] ts2) {
        return new MergeIterator<T>(iterate(ts1), iterate(ts2), new ComparableComparator<T>());
    }

    
    /**
     * Returns all elements of both iterables, always choosing the lesser of the
     * two current elements.
     */
    static public <T> Iterable<T>
    merge(Iterable<? extends T> i1,Iterable<? extends T> i2,Comparator<T> comp) {
        return new MergeIterator<T>(i1.iterator(), i2.iterator(), comp);
    }

    
    /**
     * Returns all elements of both arrays, always choosing the lesser of the
     * two current elements.
     */
    static public <T> Iterable<T> merge(T[] ts1, T[] ts2, Comparator<T> comp) {
        return new MergeIterator<T>(iterate(ts1), iterate(ts2), comp);
    }

    
    /**
     * Returns all elements of both iterables, using the given predicate to
     * choose which element to return.  If the predicate is true, choose the
     * current element of the first iterable, otherwise choose the current
     * element of the second iterable.  When one is exhausted, returns elements
     * remaining in the other.
     */
    static public <T> Iterable<T>
    merge(Iterable<? extends T> i1, Iterable<? extends T> i2, BinaryFunctor<T,T,Boolean> fn) {
        return new MergeIterator<T>(i1.iterator(), i2.iterator(), fn);
    }

    
    /**
     * Returns all elements of both arrays, using the given predicate to
     * choose which element to return.  If the predicate is true, choose the
     * current element of the first iterable, otherwise choose the current
     * element of the second iterable.  When one is exhausted, returns elements
     * remaining in the other.
     */
    static public <T> Iterable<T> merge(T[] ts1, T[] ts2, BinaryFunctor<T,T,Boolean> fn) {
        return new MergeIterator<T>(iterate(ts1), iterate(ts2), fn);
    }

    
    /**
     * Returns the results of applying the given functor to each element in turn.
     */
    static public <T,R> Iterable<R> transform(Iterable<? extends T> i, UnaryFunctor<T,R> fn) {
        return new TransformIterator<T,R>(i.iterator(), fn);
    }

    
    /**
     * Returns the results of applying the given functor to each element in turn.
     */
    static public <T,R> Iterable<R> transform(T[] ts, UnaryFunctor<T,R> fn) {
        return new TransformIterator<T,R>(iterate(ts), fn);
    }

    
    /**
     * Returns the results of applying the given functor to corresponding elements.
     */
    static public <T1,T2,R> Iterable<R>
    transform(Iterable<? extends T1> i1, Iterable<? extends T2>i2, BinaryFunctor<T1,T2,R> fn) {
        return new TransformBinaryIterator<T1,T2,R>(i1.iterator(), i2.iterator(), fn);
    }

    
    /**
     * Returns the results of applying the given functor to corresponding elements.
     */
    static public <T1,T2,R> Iterable<R> transform(T1[] ts1, T2[] ts2, BinaryFunctor<T1,T2,R> fn) {
        return new TransformBinaryIterator<T1,T2,R>(iterate(ts1), iterate(ts2), fn);
    }

    
    /**
     * Returns the results of applying the given functor to succesive pairs of elements.
     */
    static public <T,R> Iterable<R> transform(Iterable<? extends T> i, BinaryFunctor<T,T,R> fn) {
        return new TransformAdjacentIterator<T,R>(i.iterator(), fn);
    }

    
    /**
     * Returns the results of applying the given functor to succesive pairs of elements.
     */
    static public <T,R> Iterable<R> transform(T[] ts, BinaryFunctor<T,T,R> fn) {
        return new TransformAdjacentIterator<T,R>(iterate(ts), fn);
    }

    
    /**
     * Returns unduplicated results: will not return the same value twice in
     * succession.  This version uses T.equals() to test for equality.
     */
    static public <T> Iterable<T> unique(Iterable<? extends T> i) {
        return new UniqueIterator<T>(i.iterator());
    }

    
    /**
     * Returns unduplicated results: will not return the same value twice in
     * succession.  This version uses T.equals() to test for equality.
     */
    static public <T> Iterable<T> unique(T[] ts) {
        return new UniqueIterator<T>(iterate(ts));
    }

    
    /**
     * Returns unduplicated results: will not return the same value twice in
     * succession, as determined by the given predicate.  The predicate should
     * return true when the adjacent items are the same.
     */
    static public <T> Iterable<T> unique(Iterable<? extends T> i, BinaryFunctor<T,T,Boolean> eq) {
        return new UniqueIterator<T>(i.iterator(), eq);
    }

    
    /**
     * Returns unduplicated results: will not return the same value twice in
     * succession, as determined by the given predicate.  The predicate should
     * return true when the adjacent items are the same.
     */
    static public <T> Iterable<T> unique(T[] ts, BinaryFunctor<T,T,Boolean> eq) {
        return new UniqueIterator<T>(iterate(ts), eq);
    }

    
    /**
     * Adds all of the elements of the iterable to the collection.  If
     * necessary and possible, the collection will be enlarged: if enlarging
     * the collection is not possible, then the runtime exception thrown.
     * Augmentation of the Collection.addAll(Collection) API method.
     */
    static public <T> boolean addAll(Collection<? super T> c, Iterable<T> iter) {
        return Algorithms.addAll(c, iter.iterator());
    }

    
    /**
     * Adds all of the elements of the array to the collection.  If
     * necessary and possible, the collection will be enlarged: if enlarging
     * the collection is not possible, then the runtime exception thrown.
     * Augmentation of the Collection.addAll(Collection) API method.
     */
    static public <T> boolean addAll(Collection<? super T> c, T[] ts) {
        return Algorithms.addAll(c, iterate(ts));
    }
}

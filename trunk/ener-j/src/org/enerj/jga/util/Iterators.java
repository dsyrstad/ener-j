// ============================================================================
// $Id: Iterators.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.Conditional;
import org.enerj.jga.fn.adaptor.Constant;
import org.enerj.jga.fn.adaptor.Identity;
import org.enerj.jga.fn.algorithm.Accumulate;
import org.enerj.jga.fn.algorithm.Count;
import org.enerj.jga.fn.algorithm.Find;
import org.enerj.jga.fn.algorithm.FindAdjacent;
import org.enerj.jga.fn.algorithm.FindElement;
import org.enerj.jga.fn.algorithm.FindMismatch;
import org.enerj.jga.fn.algorithm.FindRepeated;
import org.enerj.jga.fn.algorithm.FindSequence;
import org.enerj.jga.fn.algorithm.ForEach;
import org.enerj.jga.fn.algorithm.MaxValue;
import org.enerj.jga.fn.algorithm.Merge;
import org.enerj.jga.fn.algorithm.MinValue;
import org.enerj.jga.fn.algorithm.RemoveAll;
import org.enerj.jga.fn.algorithm.ReplaceAll;
import org.enerj.jga.fn.algorithm.TransformBinary;
import org.enerj.jga.fn.algorithm.TransformUnary;
import org.enerj.jga.fn.algorithm.Unique;
import org.enerj.jga.fn.arithmetic.Arithmetic;
import org.enerj.jga.fn.arithmetic.ArithmeticFactory;
import org.enerj.jga.fn.arithmetic.Plus;
import org.enerj.jga.fn.comparison.EqualTo;
import org.enerj.jga.fn.comparison.Equality;
import org.enerj.jga.fn.comparison.Less;
import org.enerj.jga.fn.comparison.Min;
import org.enerj.jga.fn.comparison.Max;
import org.enerj.jga.fn.comparison.NotEqualTo;
import org.enerj.jga.fn.logical.BinaryNegate;
import org.enerj.jga.fn.logical.UnaryNegate;
import org.enerj.jga.util.FilterIterator;
import org.enerj.jga.fn.arithmetic.Minus;

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

public class Iterators {

    // ----------------------------------------------------------------------
    // Finding algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds an arbitrary value in an iteration using the equals() method.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the next instance of value in the iteration,
     * using the equals() method of the value.  If the value is not in the
     * iteration, then the returned iterator's hasNext() will report false.
     */

    static public <T> FindIterator<T>
    find (Iterator<? extends T> iterator, T value)
    {
        return new Find<T>(value).fn(iterator);
    }

    /**
     * Finds an arbitrary value in an iteration using the given Equality
     * operator.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the next instance of value in the iteration, using
     * the given equality operator.   If the value is not in the
     * iteration, then the returned iterator's hasNext() will report false.
     */
  
    static public <T> FindIterator<T>
    find (Iterator<? extends T> iterator, T value, Equality<T> eq)
    {
        return new Find<T>(eq, value).fn(iterator);
    }
 
    /**
     * Finds a value in a collection for which the given function returns TRUE.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the next instance in the iteration for which the
     * given function returns true.  If the value is not in the
     * iteration, then the returned iterator's hasNext() will report false.
     */
  
    static public <T> FindIterator<T>
    find (Iterator<? extends T> iterator, UnaryFunctor<T,Boolean> eq)
    {
        return new Find<T>(eq).fn(iterator);
    }

    // ----------------------------------------------------------------------
    // Counting algorithms
    // ----------------------------------------------------------------------

    /**
     * Counts the number of occurrences of value in the iteration, 
     * using the equals() method of the value.
     * @return the number of instances found
     */

    static public <T> long
    count (Iterator<? extends T> iterator, T value)
    {
        return new Count<T>(value).fn(iterator).longValue();
    }

    /**
     * Counts the number of occurrences of value in the iteration, using
     * the given equality operator.
     * @return the number of instances found
     */
  
    static public <T> long
    count(Iterator<? extends T> iterator, Equality<T> eq, T value)
    {
        return new Count<T>(eq, value).fn(iterator).longValue();
    }
 
    /**
     * Counts the items in the collection for which the given function returns
     * TRUE.
     * @return the number of instances found
     */
  
    static public <T> long
    count (Iterator<? extends T> iterator, UnaryFunctor<T,Boolean> eq)
    {
        return new Count<T>(eq).fn(iterator).longValue();
    }

    // ----------------------------------------------------------------------
    // Adjacent Find algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds adjacent pairs of equivalent values in an iteration using the
     * equals() method.
     * @return  an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first of a pair of adjacent values.  If no
     * pair of values exists in the iteration, then the returned iterator's
     * hasNext() will report false.
     */

    static public <T> LookAheadIterator<T>
    findAdjacent(Iterator<? extends T> iterator)
    {
        return new FindAdjacent<T>().fn(iterator);
    }

    /**
     * Finds adjacent pairs of equivalent values in an iteration for which the
     * given function returns TRUE.
     * @return  an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first of a pair of adjacent values.  If no
     * pair of values exists in the iteration, then the returned iterator's
     * hasNext() will report false.
     */

    static public <T> LookAheadIterator<T>
    findAdjacent(Iterator<? extends T> iterator,
                 BinaryFunctor<T,T,Boolean> bf)
    {
        return new FindAdjacent<T>(bf).fn(iterator);
    }

    // ----------------------------------------------------------------------
    // FindElement algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds any value from the given collection using the collection's
     * contains() method.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first instance of any value found in the
     * second collection.  If no such value is found in the iteration,
     * then the returned iterator's hasNext() will report false.
     */
 
    static public <T> FindIterator<T>
    findElement(Iterator<? extends T> iterator,
                Collection<? extends T> desired)
    {
        return new FindElement<T>(desired).fn(iterator);
    }

    /**
     * Finds any value from the given collection using the given functor
     * to determine equivalence.  Each item in the iteration will be
     * compared to every item in the second collection using the given
     * functor, stopping when the iteration is exhausted or when any
     * pair returns TRUE.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first instance of any value in the second
     * collection, where equivelency is determined by the given functor. If no
     * such value is found in the iteration, then the returned iterator's
     * hasNext() will report false.
     */
 
    static public <T> FindIterator<T>
    findElement(Iterator<? extends T> iter,
                Collection<? extends T> coll,
                BinaryFunctor<T,T,Boolean>eq)
    {
        return new FindElement<T>(coll,eq).fn(iter);
    }

    // ----------------------------------------------------------------------
    // SequenceMatch algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds the given pattern in the iteration using the equals method.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first element of a sequence that matches
     * the entire contents of the collection.  If no such match is
     * found in the iteration, then the returned iterator's hasNext()
     * will report false.  If the pattern is empty, then the iterator will not
     * be advanced.
     */
    static public <T> LookAheadIterator<T>
    match(Iterator<? extends T> iterator, Collection<? extends T> pattern)
    {
        return new FindSequence<T>(pattern).fn(iterator);
    }

    /**
     * Finds the given pattern in the collection  using the given functor
     * to determine equivalence.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first element of a sequence that matches
     * the entire contents of the collection.  If no such match is
     * found in the iteration, then the returned iterator's hasNext()
     * will report false.  If the pattern is empty, then the iterator will not
     * be advanced.
     */
    static public <T> LookAheadIterator<T>
    match(Iterator<? extends T> iterator, Collection<? extends T> pattern,
          BinaryFunctor<T, T, Boolean> eq)
    {
        return new FindSequence<T>(pattern, eq).fn(iterator);
    }                                       

    /**
     * Finds the point at which two collections differ, using NotEqualTo.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first element in the iteration that does
     * not equal the corresponding element in the pattern.  If the pattern
     * matches the iteration but is longer, than the returned iterator's
     * hasNext() will report false.  If the pattern is empty, then the
     * iteration is not advanced.
     */
    static public <T> LookAheadIterator<T>
    mismatch(Iterator<? extends T> iterator,
             Collection<? extends T> pattern)
    {
        return new FindMismatch<T>(pattern).fn(iterator);
    }

    /**
     * Finds the point at which two collections differ, using the given functor
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first element in the iteration for which the
     * given function returns TRUE when given the element and the corresponding
     * element in the pattern.  If the pattern matches the iteration but is
     * longer, than the returned iterator's hasNext() will report false.  If the
     * pattern is empty, then the iteration is not advanced.
     */
    static public <T> LookAheadIterator<T>
    mismatch(Iterator<? extends T> iterator,
             Collection<? extends T> pattern,
             BinaryFunctor<T, T, Boolean> neq)
    {
        return new FindMismatch<T>(pattern, neq).fn(iterator);
    }                                       

    // ----------------------------------------------------------------------
    // FindRepeated algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds arbitrary length runs of a given value in an iteration using the
     * equals() method.  Runs of length zero are well-defined: every iteration
     * begins with a run of length zero of all possible values.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first of n adjacent instances of value.  If no
     * run of values of the requested length exist in the iteration, then the
     * returned iterator's hasNext() will report false.
     */

    static public <T> LookAheadIterator<T>
    findRepeated (Iterator<? extends T> iterator, int n, T value)
    {
        return new FindRepeated<T>(n, value).fn(iterator);
    }

    /**
     * Finds arbitrary length runs of a given value in an iteration using the
     * given equality operator.  Runs of length zero are well-defined: every
     * iteration begins with a run of length zero of all possible values.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first of n adjacent instances of value.  If no
     * run of values of the requested length exist in the iteration, then the
     * returned iterator's hasNext() will report false.
     */
  
    static public <T> LookAheadIterator<T>
    findRepeated (Iterator<? extends T> iterator, int n, T value,
                  Equality<T> eq)
    {
        return new FindRepeated<T>(n, value, eq).fn(iterator);
    }
 
    /**
     * Finds arbitrary length runs of a given value in an iteration for which the
     * given function returns TRUE.  Runs of length zero are well-defined: every
     * iteration begins with a run of length zero of all possible values.
     * @return an iterator based on the given iterator whose next() [if it
     * hasNext()] will return the first of n adjacent instances of value.  If no
     * run of values of the requested length exist in the iteration, then the
     * returned iterator's hasNext() will report false.
     */
  
    static public <T> LookAheadIterator<T>
    findRepeated (Iterator<? extends T> iterator, int n,
                  UnaryFunctor<T,Boolean> eq)
    {
        return new FindRepeated<T>(n, eq).fn(iterator);
    }

    // ----------------------------------------------------------------------
    // ForEach algorithms
    // ----------------------------------------------------------------------

    /**
     * Applies the given UnaryFunctor to every element in the iteration, and
     * returns the Functor.  This is useful when the Functor gathers information
     * on each successive call.
     * @return the functor, after it has been called once for every element
     */

    static public <T,R> UnaryFunctor<T,R>
    forEach(Iterator<? extends T> iterator, UnaryFunctor<T,R> fn)
    {
        new ForEach<T,R>(fn).fn(iterator);
        return fn;
    }        

    // ----------------------------------------------------------------------
    // Equality algorithms
    // ----------------------------------------------------------------------

    /**
     * Returns true if the two iterations are equal, using the Comparable
     * interface to compare elements in the iterations.
     * @return true if the two iterations are equal
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> boolean
    equal(Iterator<? extends T> iterator1,
          Iterator<? extends T> iterator2)
    {
        return equal(iterator1, iterator2, new ComparableComparator<T>());
    }

    /**
     * Returns true if the two iterations are equal, using the given Comparator
     * to compare elements in the iterations.
     * @return true if the two iterations are equal
     */

    static public <T> boolean
    equal(Iterator<? extends T> iterator1,
          Iterator<? extends T> iterator2,
          Comparator<T> comp)
    {
        IteratorComparator<T> comp2 = new IteratorComparator<T>(comp);
        EqualTo<Iterator<? extends T>> eq =
            new EqualTo<Iterator<? extends T>>(comp2);
        return eq.p(iterator1, iterator2);
    }
 
    /**
     * Returns true if the two iterations are equal, using the given
     * BinaryFunctor to compare elements in the iterations.
     * @return true if the two iterations are equal
     */

    static public <T> boolean
    equal(Iterator<? extends T> iter1, Iterator<? extends T> iter2,
          BinaryFunctor<T,T,Boolean> eq)
    {
        while (iter1.hasNext() && iter2.hasNext()) {
            if (!eq.fn(iter1.next(),iter2.next()).booleanValue())
                return false;
        }

        return iter1.hasNext() == iter2.hasNext();
    }

    // ----------------------------------------------------------------------
    // Iterator Comparison algorithms
    // ----------------------------------------------------------------------

    /**
     * Returns true if the first iterator is lexically less than the second,
     * using the default comparison operation to compare the elements in each
     * iterator.
     * @return true if the first iteration is less than the second
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> boolean
    lessThan(Iterator<? extends T> iter1,
             Iterator<? extends T> iter2)
    {
        Comparator<T> comp1 = new ComparableComparator<T>();
        IteratorComparator<T> comp2 = new IteratorComparator<T>(comp1);
        return new Less<Iterator<? extends T>>(comp2).p(iter1, iter2);
    }

    /**
     * Returns true if the first iterator is lexically less than the second,
     * using the given comparator to compare the elements in each iterator.
     * @return true if the first iteration is less than the second
     */

    static public <T> boolean
    lessThan(Iterator<? extends T> iter1,
             Iterator<? extends T> iter2,
             Comparator<T> comp)
    {
        IteratorComparator<T> comp2 = new IteratorComparator<T>(comp);
        return new Less<Iterator<? extends T>>(comp2).p(iter1, iter2);
    }

    /**
     * Returns true if the first iterator is lexically less than the second,
     * using the given operator to compare the elements in each iterator.  The
     * first is less than the second if it is not longer than the second and if
     * the first corresponding element that is not equal is less.
     * @return true if the first iteration is less than the second
     */

    static public <T> boolean
    lessThan(Iterator<? extends T> i1, Iterator<? extends T> i2,
             final BinaryFunctor<T,T,Boolean> lt)
    {
        IteratorComparator<T> comp =
            new IteratorComparator<T>(new Comparator<T>() {
                public int compare(T x,T y) {
                    return lt.fn(x,y).booleanValue() ? -1 :
                           lt.fn(y,x).booleanValue() ? 1 : 0;
                }
            });
        
        return new Less<Iterator<? extends T>>(comp).p(i1, i2);
    }
    
    // ----------------------------------------------------------------------
    // Minimum/Maximum algorithms
    // ----------------------------------------------------------------------
 
    /**
     * Finds the minimum value in an iteration using the natural ordering of
     * the iterator's elements.
     * @return the minimum value found in the iteration
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> T
    minimumValue(Iterator<? extends T> iterator)
    {
        return minimumValue(iterator,
                            new Min<T>(new ComparableComparator<T>()));
    }
    /**
     * Finds the minimum value in an iteration using the given comparator.
     * @return the minimum value found in the iteration
     */

    static public <T> T
    minimumValue(Iterator<? extends T> iterator, Comparator<T> comp)
    {
        return minimumValue(iterator, new Min<T>(comp));
    }
 
    /**
     * Finds the minimum value in an iteration using the given functor to
     * compare elements.  The functor is presumed to return the lesser of
     * its two arguments.
     * @return the minimum value found in the iteration
     */

    static public <T> T
    minimumValue(Iterator<? extends T> iterator, BinaryFunctor<T,T,T> bf)
    {
        return new Accumulate<T>(bf).fn(iterator);
    }

    /**
     * Finds the maximum value in an iteration using the natural ordering of
     * the iterator's elements.
     * @return the maximum value found in the iteration
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> T
    maximumValue(Iterator<? extends T> iterator)
    {
        return maximumValue(iterator,
                            new Max<T>(new ComparableComparator<T>()));
    }
 
    /**
     * Finds the maximum value in an iteration using the given comparator.
     * @return the maximum value found in the iteration
     */

    static public <T> T
    maximumValue(Iterator<? extends T> iterator, Comparator<T> comp)
    {
        return maximumValue(iterator, new Max<T>(comp));
    }
 
    /**
     * Finds the maximum value in an iteration using the given functor to
     * compare elements.  The functor is presumed to return the lesser of
     * its two arguments.
     * @return the maximum value found in the iteration
     */

    static public <T> T
    maximumValue(Iterator<? extends T> iterator, BinaryFunctor<T,T,T> bf)
    {
        return new Accumulate<T>(bf).fn(iterator);
    }

    // ----------------------------------------------------------------------
    // Accumulate algorithms
    // ----------------------------------------------------------------------

    /**
     * Adds each number in the iterator, returning the sum.
     * @return the final sum.  If the iterator is empty, then zero is
     * returned
     */
    static public <T extends Number> T
    accumulate(Class<T> numtype, Iterator<T> iterator)
    {
        return accumulate(numtype, iterator, new Plus<T>(numtype));
    }

    /**
     * Applies the binary functor to each number in the iterator, returning
     * the final result.  Along with each number is passed the result of the
     * previous call of the functor (or zero for the first call to the functor).
     * The elements in the iterator are always passed in the 2nd postion.
     * @return the final result.  If the iterator is empty, then zero is
     * returned
     */
    static public <T extends Number> T
    accumulate(Class<T> numtype, Iterator<T> iterator, BinaryFunctor<T,T,T> bf)
    {
         Arithmetic<T> _math = ArithmeticFactory.getArithmetic(numtype);
         if (_math == null) {
             throw new IllegalArgumentException();
         }
        
        return new Accumulate<T>(_math.zero(), bf).fn(iterator);
    }

    /**
     * Applies the binary functor to each element in the iterator, returning
     * the final result.  Along with each element is passed the result of the
     * previous call of the functor (or the initial value for the first call
     * to the functor).  The elements in the iteration are always passed in the
     * 2nd postion.
     * @return the final result.  If the iteration is empty, then the initial
     * value is returned
     */
    static public <T> T
    accumulate(Iterator<T> iterator, T initial, BinaryFunctor<T,T,T> bf)
    {
        return new Accumulate<T>(initial, bf).fn(iterator);
    }
    
    // ----------------------------------------------------------------------
    // Transform algorithms
    // ----------------------------------------------------------------------

    /**
     * Applies the UnaryFunctor to each element in the input, returning an
     * iterator over the results.
     * @return an iterator based on the given iterator that will return the
     * results obtained when passing each element of the input iteration to the
     * given unary functor.
     */
    static public <T,R> TransformIterator<T,R>
    transform(Iterator<? extends T> iter, UnaryFunctor<T,R> uf)
    {
        return new TransformUnary<T,R>(uf).fn(iter);
    }

    /**
     * Applies the BinaryFunctor to corresponding elements of the two input
     * iterators, and returns an iterator over the results.  The resulting
     * iterator will have the same number of elements as the shorter of the two
     * input iterations.
     * @return an iterator that will return the results obtained when passing
     * each pair of corresponding elements of the input iterations to the
     * given binary functor.
     */
    static public <T1,T2,R> Iterator<R>
    transform(Iterator<? extends T1> i1, Iterator<? extends T2> i2,
              BinaryFunctor<T1,T2,R> bf)
    {
        return new TransformBinary<T1,T2,R>(bf).fn(i1, i2);
    }
    
    // ----------------------------------------------------------------------
    // replaceAll algorithms
    // ----------------------------------------------------------------------

    /**
     * Tests each element in an iterator, replacing those for which the test is
     * true with the replacement value.
     */
    static public <T> Iterator<T>
    replaceAll(Iterator<? extends T> iter, UnaryFunctor<T,Boolean> test,
               T value)
    {
        return new ReplaceAll<T>(test,value).fn(iter);
    }


    // ----------------------------------------------------------------------
    // Filtering algorithms
    // ----------------------------------------------------------------------

    /**
     * Filters an arbitrary value from an iteration using the equals() method.
     * @return an iterator based on the given iterator that will return not
     * include elements equal to the given value
     */

    static public <T> FilterIterator<T>
    removeAll (Iterator<? extends T> iterator, T value)
    {
        return new RemoveAll<T>(value).fn(iterator);
    }

    /**
     * Filters an arbitrary value from an iteration using the given Equality
     * operator.
     * @return an iterator based on the given iterator that will not include
     * elements that are equal to the value using the given Equality
     */
  
    static public <T> FilterIterator<T>
    removeAll (Iterator<? extends T> iterator, T value, Equality<T> eq)
    {
        return new RemoveAll<T>(eq, value).fn(iterator);
    }
 
    /**
     * Filters values from an iteration for which the given function returns
     * TRUE.
     * @return an iterator based on the given iterator that will not include
     * elements that pass the given test.
     */
  
    static public <T> FilterIterator<T>
    removeAll (Iterator<? extends T> iterator, UnaryFunctor<T,Boolean> eq)
    {
        return new RemoveAll<T>(eq).fn(iterator);
    }

    // ----------------------------------------------------------------------
    // unique algorithms
    // ----------------------------------------------------------------------

    /**
     * Skips duplicate values in the given iteration.
     * @return an iterator based on the given iterator that will not return the
     * same element twice in succession
     */
    
    static public <T> UniqueIterator<T>
    unique (Iterator<? extends T> iterator)
    {
        return new Unique<T>().fn(iterator);
    }

    /**
     * Skips duplicate values in the given iteration.
     * @return an iterator based on the given iterator that will not return the
     * same element twice in succession, using the given functor to compare
     * elements
     */
    
    static public <T> UniqueIterator<T>
    unique (Iterator<? extends T> iterator, BinaryFunctor<T,T,Boolean> eq)
    {
        return new Unique<T>(eq).fn(iterator);
    }
    
    // ----------------------------------------------------------------------
    // merge algorithms
    // ----------------------------------------------------------------------

    /**
     * Merges two iterations together.  Walks both iterators, choosing the
     * lesser of the two current values.
     * @return an iterator based on the two input iterators that contains their
     * merged contents
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> MergeIterator<T>
    merge (Iterator<? extends T> iter1, Iterator<? extends T> iter2)
    {
        return new Merge<T>(new ComparableComparator<T>()).fn(iter1,iter2);
    }

    /**
     * Merges two iterations together using the given comparator.  Walks both
     * iterators, choosing the lesser of the two current values.
     * @return an iterator based on the two input iterators that contains their
     * merged contents
     */

    static public <T> MergeIterator<T>
    merge (Iterator<? extends T> iter1, Iterator<? extends T> iter2,
           Comparator<T> comp)
    {
        return new Merge<T>(comp).fn(iter1,iter2);
    }
    
    // ----------------------------------------------------------------------
    // adjacent diff algorithms
    // ----------------------------------------------------------------------

    static public <T extends Number> TransformAdjacentIterator<T,T>
    adjacentDiff(Class<T> type, Iterator<? extends T> iter)
    {
        return new TransformAdjacentIterator<T,T>(iter, new Minus<T>(type));
    }
}

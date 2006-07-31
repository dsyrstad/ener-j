// ============================================================================
// $Id: Algorithms.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import java.util.List;
import java.util.ListIterator;
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
import org.enerj.jga.fn.comparison.NotEqualTo;
import org.enerj.jga.fn.logical.BinaryNegate;
import org.enerj.jga.fn.logical.UnaryNegate;
import org.enerj.jga.util.FilterIterator;
import org.enerj.jga.util.UniqueIterator;
/**
 * Facade for the Algorithms adapted from STL, defined to work primarily with
 * collections.  These algorithms are adapted from STL, with modifications to be
 * consistent with typical java practice.  For example, typical STL algorithms
 * are defined with pairs of iterators defining a half-open range over some
 * implied collection. It works in C++ because the STL iterators can be compared
 * for equality.  Java iterators are not guaranteed to be comparable to each
 * other by contract, so the same signatures wouldn't work.
 * <p>
 * Typically, where an STL algorithm would take a pair of iterators, we'll take
 * a collection.  Where an STL algorithm would return an iterator, we'll return
 * an iterator.  Note that it will always be java.lang.Iterator when using
 * this class: for some of the more powerful uses, use the Iterators class,
 * which will often return an implementation of Iterator that is tailored for
 * the semantics of the algorithm that was called.
 * <p>
 * The algorithms in this class and the same set of algorithms in the Iterators
 * class will always return the same results when called with identical
 * arguments.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class Algorithms {

    // ----------------------------------------------------------------------
    // Finding algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds an arbitrary value in a collection using the equals() method.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first instance of the value.  If the value is not in the
     * collection, then the returned iterator's hasNext() will report false.
     */

    static public <T> Iterator<T>
    find (Collection<? extends T> collection, T value)
    {
        return new Find<T>(value).fn(collection.iterator());
    }

    /**
     * Finds an arbitrary value in a collection using the given Equality
     * operator.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first instance of the value.  If the value is not in the
     * collection, then the returned iterator's hasNext() will report false.
     */

    static public <T> Iterator<T>
    find (Collection<? extends T> collection, T value, Equality<T> eq)
    {
        return new Find<T>(eq, value).fn(collection.iterator());
    }

    /**
     * Finds a value in a collection for which the given function returns TRUE.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first instance of the value.  If the value is not in the
     * collection, then the returned iterator's hasNext() will report false.
     */

    static public <T> Iterator<T>
    find (Collection<? extends T> collection, UnaryFunctor<T,Boolean> eq)
    {
        return new Find<T>(eq).fn(collection.iterator());
    }
 
    // ----------------------------------------------------------------------
    // Counting algorithms
    // ----------------------------------------------------------------------

    /**
     * Counts the number of occurrences of value in the collection, using the
     * equals() method of the value
     * @return the number of instances found
     */

    static public <T> long count (Collection<? extends T> collection, T value) {
        return new Count<T>(value).fn(collection.iterator()).longValue();
    }

    /**
     * Counts the number of occurrences of value in the collection, using the
     * given equality operator.
     * @return the number of instances found
     */

    static public <T> long
    count (Collection<? extends T> collection, Equality<T> eq, T value)
    {
        return new Count<T>(eq, value).fn(collection.iterator()).longValue();
    }

    /**
     * Counts the items in the collection for which the given function returns
     * true.
     * @return the number of instances found
     */

    static public <T> long
    count (Collection<? extends T> collection, UnaryFunctor<T,Boolean> eq)
    {
        return new Count<T>(eq).fn(collection.iterator()).longValue();
    }

    // ----------------------------------------------------------------------
    // Adjacent Find algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds adjacent pairs of equivalent values in a collection using the
     * equals() method.
     * @return  an iterator from the collection whose next() [if it hasNext()]
     * will return the first of a pair of adjacent values.  If no pair of values
     * exists in the collection, then the returned iterator's hasNext() will
     * report false.
     */

    static public <T> Iterator<T>
    findAdjacent(Collection<? extends T> collection)
    {
        return new FindAdjacent<T>().fn(collection.iterator());
    }

    /**
     * Finds adjacent pairs of equivalent values in a collection for which the
     * given function returns TRUE.
     * @return  an iterator from the collection whose next() [if it hasNext()]
     * will return the first of a pair of adjacent values.  If no pair of values
     * exists in the collection, then the returned iterator's hasNext() will
     * report false.
     */

    static public <T> Iterator<T>
    findAdjacent(Collection<? extends T> c, BinaryFunctor<T,T,Boolean> bf)
    {
        return new FindAdjacent<T>(bf).fn(c.iterator());
    }

    // ----------------------------------------------------------------------
    // FindElement algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds any value from the given collection using the collection's
     * contains() method.
     * @return an iterator from the first collection whose next() [if it
     * hasNext()] will return the first instance of any value found in the
     * second collection.  If no such value is found in the first collection,
     * then the returned iterator's hasNext() will report false.
     */
 
    static public <T> Iterator<T>
    findElement(Collection<? extends T> c, Collection<? extends T> desired)
    {
        return new FindElement<T>(desired).fn(c.iterator());
    }

    /**
     * Finds any value from the given collection using the given functor
     * to determine equivalence.  Each item in the first collection will be
     * compared to every item in the second collection using the given
     * functor, stopping when the first collection is exhausted or when any
     * pair returns TRUE.
     * @return an iterator from the first collection whose next() [if it
     * hasNext()] will return the first instance of any value in the second
     * collection, where equivelency is determined by the given functor.
     * If no such value is found in the first collection,
     * then the returned iterator's hasNext() will report false.
     */
 
    static public <T> Iterator<T>
    findElement(Collection<? extends T> c, Collection<? extends T> desired,
                BinaryFunctor<T,T,Boolean> eq)
    {
        return new FindElement<T>(desired, eq).fn(c.iterator());
    }

    // ----------------------------------------------------------------------
    // SequenceMatch algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds the given pattern in the collection using the equals method.
     * @return an iterator from the first collection whose next() [if it
     * hasNext()] will return the first element of a sequence that matches
     * the entire contents of the second collection.  If no such match is
     * found in the first collection, then the returned iterator's hasNext()
     * will report false.  If the pattern is empty, then the iterator points
     * to the first element in the collection.
     */
    static public <T> Iterator<T>
    match(Collection<? extends T> c, Collection<? extends T> pattern) {
        return new FindSequence<T>(pattern).fn(c.iterator());
    }

    /**
     * Finds the given pattern in the collection  using the given functor
     * to determine equivalence.
     * @return an iterator from the first collection whose next() [if it
     * hasNext()] will return the first element of a sequence that matches
     * the entire contents of the second collection.  If no such match is
     * found in the first collection, then the returned iterator's hasNext()
     * will report false.  If the pattern is empty, then the iterator points
     * to the first element in the collection.
     */
    static public <T> Iterator<T>
    match(Collection<? extends T> c, Collection<? extends T> pattern,
          BinaryFunctor<T, T, Boolean> eq)
    {
        return new FindSequence<T>(pattern, eq).fn(c.iterator());
    }                                       

    /**
     * Finds the point at which two collections differ, using NotEqualTo.
     * @return an iterator from the first collection whose next() [if it
     * hasNext()] will return the first element in the collection that does
     * not equal the corresponding element in the pattern.  If the pattern
     * matches the collection but is longer, than the returned iterator's
     * hasNext() will report false.  If the pattern is empty, then the iterator
     * points to the first element in the collection.
     */
    static public <T> Iterator<T>
    mismatch(Collection<? extends T> c, Collection<? extends T> pattern)
    {
        return new FindMismatch<T>(pattern).fn(c.iterator());
    }

    /**
     * Finds the point at which two collections differ, using the given functor
     * @return an iterator from the first collection whose next() [if it
     * hasNext()] will return the first element in the collection for which the
     * given function returns TRUE when given the element and the correspondind
     * element in the pattern.  If the pattern matches the collection but is
     * longer, than the returned iterator's hasNext() will report false.  If the
     * pattern is empty, then the iterator points to the first element in the
     * collection.
     */
    static public <T> Iterator<T>
    mismatch(Collection<? extends T> c, Collection<? extends T> pattern,
             BinaryFunctor<T,T,Boolean> neq)
    {
        return new FindMismatch<T>(pattern, neq).fn(c.iterator());
    }                                       

    // ----------------------------------------------------------------------
    // FindRepeated algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds arbitrary length runs of a given value in a collection using the
     * equals() method.  Runs of length zero are well-defined: every iteration
     * begins with a run of length zero of all possible values.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first of n adjacent instances of value.  If no run of
     * values of the requested length exist in the collection, then the returned
     * iterator's hasNext() will report false.
     */

    static public <T> Iterator<T>
    findRepeated (Collection<? extends T> c, int n, T value)
    {
        return new FindRepeated<T>(n, value).fn(c.iterator());
    }

    /**
     * Finds arbitrary length runs of a given value in a collection using the
     * given Equality operator.  Runs of length zero are well-defined: every
     * iteration begins with a run of length zero of all possible values.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first of n adjacent instances of value.  If no run of
     * values of the requested length exist in the collection, then the returned
     * iterator's hasNext() will report false.
     */

    static public <T> Iterator<T>
    findRepeated (Collection<? extends T> c, int n, T value, Equality<T> eq)
    {
        return new FindRepeated<T>(n, value, eq).fn(c.iterator());
    }

    /**
     * Finds arbitrary length runs of values in a collection for which the given
     * functor returns TRUE.  Runs of length zero are well-defined: every
     * iteration begins with a run of length zero of all possible values.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first of n adjacent instances of value.  If no run of
     * values of the requested length exist in the collection, then the returned
     * iterator's hasNext() will report false.
     */

    static public <T> Iterator<T>
    findRepeated (Collection<? extends T> c, int n, UnaryFunctor<T, Boolean> eq)
    {
        return new FindRepeated<T>(n, eq).fn(c.iterator());
    }
 
    // ----------------------------------------------------------------------
    // ForEach algorithms
    // ----------------------------------------------------------------------

    /**
     * Applies the given UnaryFunctor to every element in the collection, and
     * returns the Functor.  This is useful when the Functor gathers information
     * on each successive call.
     * @return the functor, after it has been called once for every element
     */

    static public <T,R> UnaryFunctor<T,R>
    forEach(Collection<? extends T> c,UnaryFunctor<T,R> fn)
    {
        new ForEach<T,R>(fn).fn(c.iterator());
        return fn;
    }


    // ----------------------------------------------------------------------
    // Collection Equality algorithms
    // ----------------------------------------------------------------------

    /**
     * Returns true if the two collections are equal.
     * @return true if the two collections are equal
     */

    static public <T> boolean
    equal(Collection<? extends T> c1, Collection<? extends T> c2)
    {
        return new EqualTo<Collection<? extends T>>().p(c1,c2);
    }

    /**
     * Returns true if the two collections are equal, using the given comparator
     * to compare the elements in each collection
     * @return true if the two collections are equal.
     */

    static public <T> boolean
    equal(Collection<? extends T> c1, Collection<? extends T> c2,
          Comparator<T> comp)
    {
        return Iterators.equal(c1.iterator(), c2.iterator(), comp);
    }

   /**
    * Returns true if the two collections are equal, using the given functor
    * to compare the elements in each collection.  The functor is expected to
    * evaluate its two argments and return true if they are "equal", therefore
    * this method returns true if the iterations contain the same number of
    * elements and if the functor returns true for all pairs of elements.
    * @return true if the two collections are equal
    */

   static public <T> boolean
   equal(Collection<? extends T> c1, Collection<? extends T> c2,
         BinaryFunctor<T,T,Boolean> eq)
    {
       return Iterators.equal(c1.iterator(), c2.iterator(), eq);
    }
                                 
    // ----------------------------------------------------------------------
    // Collection Comparison algorithms
    // ----------------------------------------------------------------------

    /**
     * Returns true if the first collection is lexically less than the second,
     * using the default comparison operation to compare the elements in each
     * collection.
     * @return true if c1 < c2
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> boolean
    lessThan(Collection<? extends T> c1, Collection<? extends T> c2) {
        return Iterators.lessThan(c1.iterator(), c2.iterator());
    }

    /**
     * Returns true if the first collection is lexically less than the second,
     * using the given comparator to compare the elements in each collection.
     * @return true if c1 < c2
     */

    static public <T> boolean
    lessThan(Collection<? extends T> c1, Collection<? extends T> c2,
             Comparator<T> comp)
    {
        return Iterators.lessThan(c1.iterator(), c2.iterator(), comp);
    }

    /**
     * Returns true if the first collection is lexically less than the second,
     * using the given operator to compare the elements in each collection.  The
     * first is less than the second if it is not longer than the second and if
     * the first corresponding element that is not equal is less.
     * @return true if c1 < c2
     */

    static public <T> boolean
    lessThan(Collection<? extends T> c1, Collection<? extends T> c2,
             BinaryFunctor<T,T,Boolean> lt)
    {
        return Iterators.lessThan(c1.iterator(), c2.iterator(), lt);
    }
                                 
    // ----------------------------------------------------------------------
    // Minimum/Maximum algorithms
    // ----------------------------------------------------------------------

    /**
     * Finds the position of the minimum value in a collection using the natural
     * ordering of the collection's elements.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first instance of the minimum value in the collection.
     * If the collection is empty, then the returned iterator's hasNext() will
     * report false.
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> Iterator<T>
    minimum(Collection<? extends T> c)
    {
        T min = minimumValue(c);
        return new Find<T>(min).fn(c.iterator());
    }

    /**
     * Finds the position of the minimum value in a collection using the given
     * comparator.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first instance of the minimum value in the collection.
     * If the collection is empty, then the returned iterator's hasNext() will
     * report false.
     */

    static public <T> Iterator<T>
    minimum(Collection<? extends T> c, Comparator<T> comp)
    {
        T min = minimumValue(c, comp);
        return new Find<T>(min).fn(c.iterator());
    }

    /**
     * Finds the position of the minimum value in a collection using the given
     * functor to compare elements.  The functor is presumed to return the lesser
     * of its two arguments.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first instance of the minimum value in the collection.
     * If the collection is empty, then the returned iterator's hasNext() will
     * report false.
     */

    static public <T> Iterator<T>
    minimum(Collection<? extends T> c, BinaryFunctor<T,T,T> bf)
    {
        T min = minimumValue(c, bf);
        return new Find<T>(min).fn(c.iterator());
    }
 
    /**
     * Finds the minimum value in a collection using the natural ordering of
     * the collection's elements.
     * @return the minimum value found in the collection
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> T
    minimumValue(Collection<? extends T> c)
    {
        return new MinValue<T>(new ComparableComparator<T>()).fn(c);
    }
 
    /**
     * Finds the minimum value in a collection using the given comparator.
     * @return the minimum value found in the collection
     */

    static public <T> T
    minimumValue(Collection<? extends T> c, Comparator<T> comp)
    {
        return new MinValue<T>(comp).fn(c);
    }
 
    /**
     * Finds the minimum value in a collection using the given functor to
     * compare elements.  The functor is presumed to return the lesser of
     * its two arguments.
     * @return the minimum value found in the collection
     */

    static public <T> T
    minimumValue(Collection<? extends T> c, BinaryFunctor<T,T,T> bf)
    {
        return new Accumulate<T>(bf).fn(c.iterator());
    }
 
    /**
     * Finds the position of the maximum value in a collection using the natural
     * ordering of the collection's elements.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first instance of the maximum value in the collection.
     * If the collection is empty, then the returned iterator's hasNext() will
     * report false.
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> Iterator<T>
    maximum(Collection<? extends T> c)
    {
        T max = maximumValue(c);
        return new Find<T>(max).fn(c.iterator());
    }

    /**
     * Finds the position of the maximum value in a collection using the given
     * comparator.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first instance of the maximum value in the collection.
     * If the collection is empty, then the returned iterator's hasNext() will
     * report false.
     */

    static public <T > Iterator<T>
    maximum(Collection<? extends T> c, Comparator<T> comp)
    {
        T max = maximumValue(c, comp);
        return new Find<T>(max).fn(c.iterator());
    }

    /**
     * Finds the position of the maximum value in a collection using the given
     * functor to compare elements.  The functor is presumed to return the 
     * greater of its two arguments.
     * @return an iterator from the collection whose next() [if it hasNext()]
     * will return the first instance of the maximum value in the collection.
     * If the collection is empty, then the returned iterator's hasNext() will
     * report false.
     */

    static public <T> Iterator<T>
    maximum(Collection<? extends T> c,BinaryFunctor<T,T,T> bf)
    {
        T max = maximumValue(c, bf);
        return new Find<T>(max).fn(c.iterator());
    }

    
    /**
     * Finds the maximum value in a collection using the natural ordering of
     * the collection's elements.
     * @return the maximum value found in the collection
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> T
    maximumValue(Collection<? extends T> c)
    {
        return new MaxValue<T>(new ComparableComparator<T>()).fn(c);
    }

    /**
     * Finds the minimum value in a collection using the given comparator.
     * @return the maximum value found in the collection
     */

    static public <T> T
    maximumValue(Collection<? extends T> c, Comparator<T> comp)
    {
        return new MaxValue<T>(comp).fn(c);
    }
    
    /**
     * Finds the maximum value in a collection using the given functor to
     * compare elements.  The functor is presumed to return the greater of
     * its two arguments.
     * @return the maximum value found in the collection
     */

    static public <T> T
    maximumValue(Collection<? extends T> c, BinaryFunctor<T,T,T> fn)
    {
        return new Accumulate<T>(fn).fn(c.iterator());
    }

    // ----------------------------------------------------------------------
    // Accumulate algorithms
    // ----------------------------------------------------------------------

    /**
     * Adds each number in the collection, returning the sum.
     * @return the final sum.  If the collection is empty, then zero is
     * returned
     */
    static public <T extends Number> T
    accumulate(Class<T> numtype, Collection<T> c)
    {
        return accumulate(numtype, c, new Plus<T>(numtype));
    }

    /**
     * Applies the binary functor to each number in the collection, returning
     * the final result.  Along with each number is passed the result of the
     * previous call of the functor (or zero for the first call to the functor).
     * The elements in the collection are always passed in the 2nd postion.
     * @return the final result.  If the collection is empty, then zero is
     * returned
     */
    static public <T extends Number> T
    accumulate(Class<T> numtype, Collection<T> c, BinaryFunctor<T,T,T> bf)
    {
         Arithmetic<T> _math = ArithmeticFactory.getArithmetic(numtype);
         if (_math == null) {
             throw new IllegalArgumentException();
         }
        
        return new Accumulate<T>(_math.zero(), bf).fn(c.iterator());
    }

    /**
     * Applies the binary functor to each element in the collection, returning
     * the final result.  Along with each element is passed the result of the
     * previous call of the functor (or the initial value for the first call
     * to the functor).  The elements in the collection are always passed in the
     * 2nd postion.
     * @return the final result.  If the collection is empty, then the initial
     * value is returned
     */
    static public <T> T
    accumulate(Collection<T> c, T initial, BinaryFunctor<T,T,T> bf)
    {
        return new Accumulate<T>(initial, bf).fn(c.iterator());
    }

    // ----------------------------------------------------------------------
    // Transform algorithms
    // ----------------------------------------------------------------------

    /**
     * Applies the UnaryFunctor to each element in the list, and replaces
     * each element with the result. This method would, in an ideal world,
     * belong in the Collections class, as its signature is more like the
     * algorithm methods in that class than in Algorithms.
     */
    
    static public <T> void  transform(List<T> lin, UnaryFunctor<T,T> uf) {
        // NOTE: can't be covariant, as it both reads from the list and writes
        // to it (effectively)
        ListIterator<T> liter = lin.listIterator();
        while(liter.hasNext()) {
            liter.set(uf.fn(liter.next()));
        }
    }

    /**
     * Applies the UnaryFunctor to each element in the input collection, and
     * appends the result to the output collection.  The output collection will
     * generally grow as a result of this operation (in contrast with the STL
     * transform operation, which will not by itself change the size of the
     * output collection)
     */
    static public <T,R> void transformCopy(Collection<? extends T> cin,
                                           Collection<? super R> cout,
                                           UnaryFunctor<T,R> uf)
    {
        addAll(cout, new TransformUnary<T,R>(uf).fn(cin.iterator()));
    }

    /**
     * Applies the BinaryFunctor to corresponding elements of the two input
     * collections, and appends the result to the output collection.  The
     * output collection will generally grow as a result of this operation (in
     * contrast with the STL transform operation, which will not by itself
     * change the size of the output collection)
     */
    static public <T1,T2,R> void transformCopy(Collection<? extends T1> c1,
                                               Collection<? extends T2> c2,
                                               Collection<? super R> cout,
                                               BinaryFunctor<T1,T2,R> bf)
    {
        TransformBinary<T1,T2,R> xform = new TransformBinary<T1,T2,R>(bf);
        addAll(cout, xform.fn(c1.iterator(),c2.iterator()));
    }
    
    // ----------------------------------------------------------------------
    // replaceAll algorithms
    // ----------------------------------------------------------------------

    /**
     * Tests each element in the list, and replaces elements that pass with
     * the given value.  This method would, in an ideal world, belong in the
     * Collections class, as its signature is more like the algorithm methods
     * in that class than in Algorithms.
     */
    
    static public <T> void
    replaceAll(List<T> lin, UnaryFunctor<T,Boolean> uf, T value)
    {
        // NOTE: can't be covariant, as it both reads from the list and writes
        // to it (effectively)
        ListIterator<T> liter = lin.listIterator();
        while(liter.hasNext()) {
            if (uf.fn(liter.next()).booleanValue()) {
                liter.set(value);
            }
        }
    }

    /**
     * Copies each element in the input collection to the output collection,
     * except that those elements that pass the given test are replaced with the
     * constant value.
     */
    static public <T,R> void replaceAllCopy(Collection<? extends T> cin,
                                            Collection<? super T> cout,
                                            UnaryFunctor<T,Boolean> test,T value)
    {
        addAll(cout, new ReplaceAll<T>(test,value).fn(cin.iterator()));
    }

    // ----------------------------------------------------------------------
    // filter algorithms
    // ----------------------------------------------------------------------

    /**
     * removes all instances of the given element from the list
     */

    static public <T> void removeAll (List<? extends T> lin, T value) {
        removeAll(lin, new EqualTo<T>().bind2nd(value));
    }

    /**
     * removes all instances of the given element from the list
     */

    static public <T> void
    removeAll (List<? extends T> lin, T value, Equality<T> eq)
    {
        removeAll(lin, eq.bind2nd(value));
    }

    /**
     * removes all elements from the list for which the functor returns TRUE
     */

    static public <T> void
    removeAll (List<? extends T> lin, UnaryFunctor<T,Boolean> uf)
    {
        ListIterator<? extends T> liter = lin.listIterator();
        while (liter.hasNext()) {
            if (uf.fn(liter.next()).booleanValue()) {
                liter.remove();
            }
        } 
    }
    
    /**
     * Copies each element in the input collection except those equal to the
     * given value to the output collection,
     */

    static public <T> void
    removeAllCopy (Collection<? extends T> cin, Collection<? super T> cout,
                   T value)
    {
        addAll(cout, new RemoveAll<T>(value).fn(cin.iterator()));
    }

    /**
     * Copies each element in the input collection except those equal to the
     * given value (using the given Equality operator) to the output collection,
     */
  
    static public <T> void
    removeAllCopy (Collection<? extends T> cin, Collection<? super T> cout,
                   T value, Equality<T> eq)
    {
        addAll(cout, new RemoveAll<T>(eq,value).fn(cin.iterator()));
     }

     /**
      * Copies each element in the input collection except those for which the
      * given function returns TRUE to the output collection.
      */

     static public <T> void
     removeAllCopy (Collection<? extends T> cin, Collection<? super T> cout,
                    UnaryFunctor<T,Boolean> eq)
     {
        addAll(cout, new RemoveAll<T>(eq).fn(cin.iterator()));
    }

    // ----------------------------------------------------------------------
    // unique algorithms
    // ----------------------------------------------------------------------

    /**
     * removes all adjacent duplicate values in the given list, using equals()
     * to compare adjacent elements
     */

    static public <T> void unique (List<? extends T> lin) {
        unique(lin, new EqualTo<T>());
    }

    /**
     * removes all adjacent duplicate values in the given list, using the given
     * functor to compare adjacent elements
     */

    static public <T> void
    unique (List<? extends T> lin, BinaryFunctor<T,T,Boolean> eq)
    {
        ListIterator<? extends T> liter = lin.listIterator();

        T last = null;
        // skip the first element, if there is one
        if (liter.hasNext())
            last = liter.next();

        while (liter.hasNext()) {
            T next = liter.next();
            if (eq.fn(last, next).booleanValue()) {
                liter.remove();
            }
            else {
                last = next;
            }
        }
    }

    /**
     * Copies the elements from the input collection to the output collection,
     * skipping adjacent duplacate elements.
     */
    
    static public <T> void
    uniqueCopy (Collection<? extends T> cin, Collection<? super T> cout)
    {
        addAll(cout, new Unique<T>().fn(cin.iterator()));
    }

    /**
     * Copies the elements from the input collection to the output collection,
     * skipping adjacent duplacate elements.
     */
    
    static public <T> void
    uniqueCopy (Collection<? extends T> cin, Collection<? super T> cout,
                BinaryFunctor<T,T,Boolean> eq)
    {
        addAll(cout, new Unique<T>(eq).fn(cin.iterator()));
    }
    
    // ----------------------------------------------------------------------
    // merge algorithms
    // ----------------------------------------------------------------------

    /**
     * merges two collections, appending values to the output collection
     */

    static public <T extends Comparable/*@*/<? super T>/*@*/> void
    mergeCopy (Collection<? extends T> cin1, Collection<? extends T> cin2,
               Collection<? super T> cout)
    {
        mergeCopy(cin1, cin2, cout, new ComparableComparator<T>());
    }

    /**
     * merges two collections using the given comparator, appending values to
     * the output collection
     */

    static public <T> void
    mergeCopy (Collection<? extends T> cin1, Collection<? extends T> cin2,
               Collection<? super T> cout, Comparator<T> comp)
    {
        Merge<T> merger = new Merge<T>(comp);
        addAll(cout, merger.fn(cin1.iterator(),cin2.iterator()));
    }

    // ----------------------------------------------------------------------
    // utilities
    // ----------------------------------------------------------------------

    /**
     * Adds all of the elements of the iterator to the collection.  If
     * necessary and possible, the collection will be enlarged: if enlarging
     * the collection is not possible, then the runtime exception thrown.
     * Augmentation of the Collection.addAll(Collection) API method.
     */
    static public <T> boolean addAll(Collection<? super T> c,
                                     Iterator<T> iter)
    {
        boolean b = false;
        while(iter.hasNext()) {
            b |= c.add(iter.next());
        }

        return b;
    }    
}

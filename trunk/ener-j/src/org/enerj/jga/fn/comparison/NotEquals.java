//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/jga/fn/comparison/NotEquals.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $

package org.enerj.jga.fn.comparison;

import java.util.Comparator;

import org.enerj.jga.fn.BinaryPredicate;
import org.enerj.jga.util.ComparableComparator;

/**
 * Binary Predicate that returns TRUE for arguments <b>x</b> and <b>y</b> when
 * x = y.  The comparison is performed using a comparator supplied at
 * construction time, although a default comparator will be used if the nested
 * Comparable class' default constructor is used.
 * The behaviour of this class in the presence of null arguments is left to the
 * implementation of the specific Comparator, however it is generally safe to
 * assume that using null arguments will cause a NullPointerException to be
 * thrown.
 * <p>
 */
public class NotEquals<T> extends BinaryPredicate<T, T>
{
    static final long serialVersionUID = 0;

    private Comparator<? super T> mComparator;

    /**
     * Builds a NotEquals predicate using the given Comparator.
     * 
     * @throws IllegalArgumentException if the argument is null.
     */
    public NotEquals(Comparator<? super T> comp)
    {
        if (comp == null) {
            throw new IllegalArgumentException("Comparator may not be null");
        }

        mComparator = comp;
    }

    /**
     * Gets the Comparator in use by this functor.
     * 
     * @return the Comparator in use by this functor.
     */
    public Comparator<? super T> getComparator()
    {
        return mComparator;
    }

    // BinaryPredicate interface

    /**
     * Given Comparable arguments <b>x</b> and <b>y</b>, returns x = y, using the comparator.
     *
     * @return x = y. If x or y are null, but not both null, true is returned.
     * 
     * @throws NullPointerException if either argument is null.
     */
    public Boolean fn(T x, T y)
    {
        if (x == null) {
            if (y == null) {
                return Boolean.FALSE;
            }
            
            return Boolean.TRUE;
        }
        else if (y == null) {
            return Boolean.TRUE;
        }

        return Boolean.valueOf(mComparator.compare(x, y) != 0);
    }

    public void accept(org.enerj.jga.fn.Visitor v)
    {
        if (v instanceof NotEquals.Visitor) {
            ((NotEquals.Visitor)v).visit(this);
        }
    }

    public String toString()
    {
        return "NotEquals";
    }


    // Acyclic Visitor

    /**
     * Interface for classes that may interpret a <b>NotEquals</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor
    {
        public void visit(NotEquals host);
    }


    /**
     * NotEquals predicate for use with Comparable arguments.  This class exists
     * as an implementation detail that works around a limit in the javac
     * inferencer -- in all substantive ways, this is simply a NotEquals functor.
     */

    public static class Comparable<TT extends java.lang.Comparable<? super TT>> extends NotEquals<TT>
    {
        static final long serialVersionUID = 0;

        public Comparable()
        {
            super( new ComparableComparator<TT>() );
        }
    }
}

//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/jga/fn/comparison/Like.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $

package org.enerj.jga.fn.comparison;

import java.util.Comparator;

import org.enerj.jga.fn.BinaryPredicate;
import org.enerj.jga.util.ComparableComparator;
import org.enerj.util.LikeMatcher;

/**
 * Binary Predicate that returns TRUE for arguments <b>x</b> and <b>y</b> when
 * x like y, where y is an OQL LIKE pattern. 
 * <p>
 */
public class Like<T> extends BinaryPredicate<T, T>
{
    static final long serialVersionUID = 0;


    /**
     * Builds a Like predicate.
     */
    public Like()
    {
    }

    // BinaryPredicate interface

    /**
     * Given Comparable arguments <b>x</b> and <b>y</b>, returns x like y.
     *
     * @return x like y. If x or y are null, false is returned.
     */
    public Boolean fn(T x, T y)
    {
        if (x == null || y == null) {
            return Boolean.FALSE;
        }

        
        return new LikeMatcher(y.toString(), false, '\\').compare(x);
    }

    public void accept(org.enerj.jga.fn.Visitor v)
    {
        if (v instanceof Like.Visitor) {
            ((Like.Visitor)v).visit(this);
        }
    }

    public String toString()
    {
        return "Like";
    }


    // Acyclic Visitor

    /**
     * Interface for classes that may interpret a <b>Like</b> predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor
    {
        public void visit(Like host);
    }
}

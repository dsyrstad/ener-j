//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/SelectCollectionFunctor.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Collection;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.SelectCollection;

/**
 * Wraps the collection passed as an argument to fn() in a FilteredCollection.  
 * <p>
 * 
 * @version $Id: SelectCollectionFunctor.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class SelectCollectionFunctor extends UnaryFunctor
{
    private static final long serialVersionUID = -189833295690554029L;

    private TrackedValueFunctor mTrackedValueFunctor;
    private UnaryFunctor mFilterFunctor = null;
    private UnaryFunctor mProjectionFunctor = null;
   
    //--------------------------------------------------------------------------------
    /**
     * Construct a SelectCollectionFunctor.
     * 
     * @param aFilter the Functor to filter the collection by. May be null for no filtering.
     * @param aTrackedValueFunctor a TrackedValueFunctor that will track the current iterator value. May be
     *  null if there is no tracked value.
     * @param aProjectionFunctor a UnaryFunctor that will project the proper values during iteration. May be null
     *  to project the iterated object directly.
     */
    public SelectCollectionFunctor(UnaryFunctor aFilter, UnaryFunctor aProjectionFunctor, TrackedValueFunctor aTrackedValueFunctor) 
    {
        mFilterFunctor = aFilter;
        mTrackedValueFunctor = aTrackedValueFunctor;
        mProjectionFunctor = aProjectionFunctor;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Set the Functor to perform filtering. Must be set before the collection is used or else
     * the collection will give inconsistent results.
     * 
     * @param aFilter the Functor to filter the collection by. May be null for no filtering.
     */
    public void setFilterFunctor(UnaryFunctor aFilter)
    {
        mFilterFunctor = aFilter;
    }

    //--------------------------------------------------------------------------------
    /**
     * Set the Functor to perform projection. Must be set before the collection is used or else
     * the collection will give inconsistent results.
     * 
     * @param aProjectionFunctor a UnaryFunctor that will project the proper values during iteration. May be null
     *  to project the iterated object directly.
     */
    public void setProjectionFunctor(UnaryFunctor aProjectionFunctor)
    {
        mProjectionFunctor = aProjectionFunctor;
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object arg)
    {
        if (arg == null || !(arg instanceof Collection)) {
            throw new IllegalArgumentException("FiltedCollectionFunctor.fn() expected a Collection, got: " + arg);
        }
        
        return new SelectCollection((Collection)arg, mFilterFunctor, mProjectionFunctor, mTrackedValueFunctor);
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(SelectCollectionFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof SelectCollectionFunctor.Visitor)
            ((SelectCollectionFunctor.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "SelectCollectionFunctor[filter=" + mFilterFunctor + ", projection=" + mProjectionFunctor + ", trackedValueFunctor=" + mTrackedValueFunctor + ']';
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>SelectCollectionFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(SelectCollectionFunctor host);
    }
}

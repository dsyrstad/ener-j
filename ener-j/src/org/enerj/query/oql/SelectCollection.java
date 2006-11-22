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
/**
 * 
 */
package org.enerj.query.oql;

import java.util.Collection;
import java.util.Iterator;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.util.FilterIterator;
import org.enerj.query.oql.fn.TrackedValueFunctor;

/**
 * Filtered Collection for OQL select statements. <p>
 * 
 * @version $Id: SelectCollection.java,v 1.2 2006/02/09 03:42:24 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class SelectCollection extends BaseSelectCollection
{
    private Collection mWrappedCollection;
    private TrackedValueFunctor mTrackedValueFunctor;
    private UnaryFunctor mFilterFunctor = null;
    private UnaryFunctor mProjectionFunctor = null;
    
    /** >= 0 if it's been calculated. */
    private int mSize = -1;
    

    /**
     * Construct a SelectCollection that is unfiltered and unprojected. 
     *
     * @param aWrappedCollection the collection to select against.
     * @param aTrackedValueFunctor a TrackedValueFunctor that will track the current iterator value. May be
     *  null if there is no tracked value.
     */
    public SelectCollection(Collection aWrappedCollection, TrackedValueFunctor aTrackedValueFunctor)
    {
        this(aWrappedCollection, aTrackedValueFunctor, null, null);
    }
    

    /**
     * Construct a SelectCollection that is filtered. 
     *
     * @param aWrappedCollection the collection to select against.
     * @param aFilter the Functor to filter the collection by. May be null for no filtering.
     * @param aTrackedValueFunctor a TrackedValueFunctor that will track the current iterator value. May be
     *  null if there is no tracked value.
     * @param aProjectionFunctor a UnaryFunctor that will project the proper values during iteration. May be null
     *  to project the iterated object directly.
     */
    public SelectCollection(Collection aWrappedCollection, UnaryFunctor aFilter, UnaryFunctor aProjectionFunctor, TrackedValueFunctor aTrackedValueFunctor)
    {
        mWrappedCollection = aWrappedCollection;
        mFilterFunctor = aFilter;
        mTrackedValueFunctor = aTrackedValueFunctor;
        mProjectionFunctor = aProjectionFunctor;
    }
    

    // Collection Interface...



    public Iterator iterator()
    {
        Iterator wrappedIterator;
        if (mFilterFunctor == null) {
            wrappedIterator = mWrappedCollection.iterator();
        }
        else {
            wrappedIterator = new FilterIterator(mWrappedCollection.iterator(), mFilterFunctor);
        }
        
        if (mTrackedValueFunctor == null && mProjectionFunctor == null) {
            return wrappedIterator;
        }
        
        return new TrackAndProjectIterator(wrappedIterator);
    }


    public int size()
    {
        if (mSize < 0) {
            mSize = 0;
            for (Object obj : this) {
                ++mSize;
            }
        }
        
        return mSize;
    }


    // ...Collection Interface.




    /**
     * Iterator that tracks the value at its current iteration and also projects based on
     * mProjectionFunctor.
     */
    private final class TrackAndProjectIterator implements Iterator
    {
        private Iterator mWrappedIterator;
        

        private TrackAndProjectIterator(Iterator aWrappedIterator)
        {
            mWrappedIterator = aWrappedIterator;
        }


        public Object next()
        {
            Object value = mWrappedIterator.next();
            if (mTrackedValueFunctor != null) {
                mTrackedValueFunctor.setValue(value);
            }
            
            if (mProjectionFunctor != null) {
                // Note: Values to project are derived from tracked variables. Hence, null is passed to fn(). 
                value = mProjectionFunctor.fn(null);
            }
            
            return value;
        }


        public boolean hasNext()
        {
            return mWrappedIterator.hasNext();
        }


        public void remove()
        {
            mWrappedIterator.remove();
        }
    }
}

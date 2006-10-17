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
//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/CartesianProductCollection.java,v 1.3 2005/11/21 02:06:47 dsyrstad Exp $

package org.enerj.query.oql;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cartesian Product Collection for OQL select-join statements. Wraps two collections and performs a Cartesian
 * Product while iterating. Placeholder until we have a join optimizer.<p>
 * 
 * @version $Id: CartesianProductCollection.java,v 1.3 2005/11/21 02:06:47 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class CartesianProductCollection extends BaseSelectCollection
{
    private Collection mLeftCollection;
    private Collection mRightCollection;
    /** >= 0 if it's been calculated. */
    private int mSize = -1;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a CartesianProductCollection that is unfiltered. 
     *
     * @param aWrappedCollection the collection to select against.
     * @param aTrackedValueFunctor a TrackedValueFunctor that will track the current iterator value. May be
     *  null if there is no tracked value.
     */
    public CartesianProductCollection(Collection aLeftCollection, Collection aRightCollection)
    {
        mLeftCollection = aLeftCollection;
        mRightCollection = aRightCollection;
    }
    
    //--------------------------------------------------------------------------------
    // Collection Interface...
    //--------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------
    public Iterator iterator()
    {
        return new ProductIterator();
    }

    //--------------------------------------------------------------------------------
    public int size()
    {
        if (mSize >= 0) {
            return mSize;
        }
        
        mSize = mLeftCollection.size() + mRightCollection.size();
        return mSize;
    }

    //--------------------------------------------------------------------------------
    // ...Collection Interface.
    //--------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    /**
     * Iterator that performs the Cartesian (cross) product.
     */
    private final class ProductIterator implements Iterator
    {
        private Iterator mLeftIterator;
        private Object mCurrentLeftObject;
        private Iterator mRightIterator;
        private boolean mRightIsEmpty;

        //--------------------------------------------------------------------------------
        private ProductIterator()
        {
            mLeftIterator = mLeftCollection.iterator();
            mRightIterator = mRightCollection.iterator();
            // If left is not empty but right is empty, result is immediate hasNext() = false.
            mRightIsEmpty = !mRightIterator.hasNext();
            
            // Prime first left object.
            if (mLeftIterator.hasNext()) {
                mCurrentLeftObject = mLeftIterator.next();
            }
        }

        //--------------------------------------------------------------------------------
        public Object next()
        {
            if (!hasNext()) {
                throw new NoSuchElementException("End of iterator reached");
            }
            
            Object rightObject = mRightIterator.next();
            //Object[] value = new Object[] { mCurrentLeftObject, rightObject };
            // It's not necessary to return anything. TrackedValueFunctor takes care of tracking the
            // value for each iterator. Projection will take care of only projecting the
            // values the match the where clause.
            Object value = "CartesianProductJoin";

            // Prime next left object if right is now empty.
            if (!mRightIterator.hasNext() && mLeftIterator.hasNext()) {
                // Read another left object and re-init right iterator.
                mCurrentLeftObject = mLeftIterator.next();
                mRightIterator = mRightCollection.iterator();
            }

            return value;
        }

        //--------------------------------------------------------------------------------
        public boolean hasNext()
        {
            return mRightIsEmpty || mLeftIterator.hasNext() || mRightIterator.hasNext();
        }

        //--------------------------------------------------------------------------------
        public void remove()
        {
            throw new UnsupportedOperationException("Collection is immutable");
        }
    }
}

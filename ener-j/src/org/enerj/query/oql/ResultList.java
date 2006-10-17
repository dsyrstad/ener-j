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
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ResultList.java,v 1.3 2006/02/14 02:39:36 dsyrstad Exp $

package org.enerj.query.oql;

import gnu.trove.TLongArrayList;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.RandomAccess;

import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJExtent;
import org.enerj.core.EnerJImplementation;

/**
 * Final list of results returned from a query. Used to summarize results so that the functor
 * tree is not hit more than once. The iterator from the wrapped Collection is only scanned once.<p>
 * 
 * @version $Id: ResultList.java,v 1.3 2006/02/14 02:39:36 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ResultList extends AbstractList implements RandomAccess
{
    private Collection mQueryCollection;
    /** Current iterator used while building mList from mCollection */
    private Iterator mQueryIterator = null;
    private TLongArrayList mOIDList;
    private int mSize = -1; // -1 = not initialized.
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a ResultList. The iterator from the given Collection is only scanned once.
     *
     * @param aQueryCollection the Collection returned from the query functor tree.
     */
    public ResultList(Collection aQueryCollection)
    {
        mQueryCollection = aQueryCollection;
        mOIDList = new TLongArrayList(1000);
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see java.util.List#get(int)
     */
    public Object get(int anIndex)
    {
        int listSize = mOIDList.size(); 
        if (listSize <= anIndex) {
            // Populate the list up to anIndex. Iterator stays properly positioned because it is 
            // only accessed by this method.
            if (mQueryIterator == null) {
                mQueryIterator = mQueryCollection.iterator();
            }
            
            Object value = null;
            int i = listSize;
            for (; mQueryIterator.hasNext() && i <= anIndex; i++) {
                value = mQueryIterator.next();
                mOIDList.add( EnerJImplementation.getEnerJObjectId(value) );
            }
            
            if (i <= anIndex) {
                throw new IndexOutOfBoundsException("index " + anIndex);
            }
            
            // Return the last value retrieved.
            return value;
        }

        long oid = mOIDList.get(anIndex);
        return EnerJDatabase.getCurrentDatabase().getObjectForOID(oid);
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see java.util.Collection#size()
     */
    public int size()
    {
        // NOTE: We want to avoid calling mCollection.size() because it may cause a complete
        // scan of the iterator. Instead we scan using get until we get an ArrayIndexOutOfBounds.
        // This way the OID list is built at the same time as the size is calculated.
        if (mSize < 0) {
            if (mQueryCollection instanceof EnerJExtent) {
                // Slight optimization. EnerJExtent's size() is faster than iterating.
                mSize = mQueryCollection.size();
            }
            else {
                for (mSize = 0; ; ++mSize) {
                    try {
                        get(mSize);
                    }
                    catch (IndexOutOfBoundsException e) {
                        // OK. Just break out
                        break;
                    }
                }
            }
        }

        return mSize;
    }

}

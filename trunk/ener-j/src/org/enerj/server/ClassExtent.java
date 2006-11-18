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
//$Header: $

package org.enerj.server;

import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJImplementation;
import org.enerj.core.Extent;
import org.enerj.core.SparseBitSet;

/**
 * Represents the extent for a class. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ClassExtent
{
    /** The extent (all objects of this class). Index to the bitset is the OID. */
    private SparseBitSet mExtent;
    
    /** Transiently cached extent - lazily constructed. */
    transient private Extent mCachedExtent = null; 

    public ClassExtent()
    {
        //  TODO  make the node size configurable based on how many objects you want to store.
        mExtent = new SparseBitSet(1024);
        // TODO  mIndexes = new ArrayList();

    }

    //----------------------------------------------------------------------
    /**
     * Gets the mutable extent for this class.
     *
     * @return the extent as a SparseBitSet whose index is an OID.
     */
    public SparseBitSet getExtentBitSet()
    {
        return mExtent;
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Returns the Extent for this class and it's subclasses.
     *
     * @return an Extent. Returns null if the class cannot be found.
     */
    public Extent getExtent()
    {
        /*
        if (mCachedExtent == null) {
            EnerJDatabase db = EnerJImplementation.getEnerJDatabase(this);
            try {
                mCachedExtent = db.getExtent(Class.forName(getClassName()), true);
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }
        */
        return mCachedExtent;
    }

}

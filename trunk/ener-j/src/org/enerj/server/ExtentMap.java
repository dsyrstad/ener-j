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
// Ener-J
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/ObjectServerSession.java,v 1.4 2006/01/09 02:25:12 dsyrstad Exp $
package org.enerj.server;

import java.util.Map;

import org.enerj.core.RegularDMap;
import org.enerj.core.SparseBitSet;


/**
 * Maps extents for all classes that define extents.
 *
 * @version $Id: ExtentMap.java,v 1.4 2006/01/09 02:25:12 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class ExtentMap
{
    // TODO Use a VeryLargeDMap? 
    /** Extent mapping. Key is Class name. */
    private Map<String,SparseBitSet> mExtentMap = new RegularDMap();
    
    /**
     * Construct a new ExtentMap.
     */
    ExtentMap()
    {
    }

    /**
     * Creates a new extent for the specified class name.
     * 
     * @param aClassName the class name.
     */
    void createExtentForClassName(String aClassName)
    {
        mExtentMap.put(aClassName, new SparseBitSet());
    }
    
    /**
     * Gets the extent for the specified CID.
     * 
     * @param aClassName the class name.
     * 
     * @return the SparseBitSet representing the extent, or null if no extent is defined for the class.
     */
    SparseBitSet getExtent(String aClassName)
    {
        return mExtentMap.get(aClassName);
    }
}

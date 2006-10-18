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
// Copyright 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/SystemCIDMap.java,v 1.4 2006/05/16 02:40:27 dsyrstad Exp $

package org.enerj.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.enerj.server.ObjectServer;

/**
 * Provides a mapping of system persistable class names and corresponding CIDs.
 * This is needed for bootstrapping purposes, so we can read the root and teh schema
 * without out them trying to load themselves, which results in a stack overflow.
 *
 * @version $Id: SystemCIDMap.java,v 1.4 2006/05/16 02:40:27 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class SystemCIDMap 
{
    /** Key is String (classname), value is Long (CID). */
    private static HashMap<String, Long> sNameToCIDMap = new HashMap<String, Long>(64);
    /** Key is value is Long (CID), String (classname). */
    private static HashMap<Long, String> sCIDToNameMap = new HashMap<Long, String>(64);
    static {
        sNameToCIDMap.put("org.enerj.core.DatabaseRoot",         1L);
        sNameToCIDMap.put("org.enerj.core.Schema",               2L);
        sNameToCIDMap.put("org.enerj.core.ClassVersionSchema",   3L);
        sNameToCIDMap.put("org.enerj.core.LogicalClassSchema",   4L);
        sNameToCIDMap.put("org.enerj.core.RegularDArray",        5L);
        sNameToCIDMap.put("org.enerj.core.RegularDBag",          6L);
        sNameToCIDMap.put("org.enerj.core.RegularDList",         7L);
        sNameToCIDMap.put("org.enerj.core.RegularDMap",          8L);
        sNameToCIDMap.put("org.enerj.core.RegularDSet",          9L);
        sNameToCIDMap.put("org.enerj.core.SparseBitSet",         10L);
        sNameToCIDMap.put("org.enerj.core.SparseBitSet$RootNode",11L);
        sNameToCIDMap.put("org.enerj.core.SparseBitSet$SecondLevelNode",12L);
        sNameToCIDMap.put("org.enerj.core.SparseBitSet$LeafNode",13L);
        sNameToCIDMap.put("org.enerj.core.VeryLargeDArray",      14L);
        
        for (Map.Entry<String, Long> entry : (Set<Map.Entry<String, Long>>)sNameToCIDMap.entrySet()) {
            sCIDToNameMap.put(entry.getValue(), entry.getKey());
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the System CID corresponding to a system persistable class name.
     *
     * @param aClassName a system persistable class name.
     *
     * @return a System CID, or ObjectServer.NULL_CID if aClassName is not
     *  a system persistable.
     */
    public static long getSystemCIDForClassName(String aClassName)
    {
        Long cid = (Long)sNameToCIDMap.get(aClassName);
        if (cid == null) {
            return ObjectServer.NULL_CID;
        }
        
        return cid.longValue();
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the system persistable class name corresponding to a System CID.
     *
     * @param aCID a system-defined CID.
     *
     * @return a system persistable class name, or null if aCID does not represent
     *  a system persistable.
     */
    public static String getSystemClassNameForCID(long aCID)
    {
        return (String)sCIDToNameMap.get( new Long(aCID) );
    }

    //----------------------------------------------------------------------
    /**
     * Determines if the given CID is a System CID.
     *
     * @param aCID a CID.
     *
     * @return true if it is a system CID, else false.
     */
    public static boolean isSystemCID(long aCID)
    {
        return aCID > 0 && aCID < ObjectServer.LAST_SYSTEM_CID;
    }

    //----------------------------------------------------------------------
    /**
     * Gets all of the system persistable class names.
     *
     * @return an Iterator of Strings representing all of the system persistable class names.
     */
    public static Iterator<String> getSystemClassNames()
    {
        return sNameToCIDMap.keySet().iterator();
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets all of the system CID values.
     *
     * @return an Iterator of Longs representing all of the system CIDs.
     */
    public static Iterator<Long> getSystemCIDs()
    {
        return sCIDToNameMap.keySet().iterator();
    }
    
}
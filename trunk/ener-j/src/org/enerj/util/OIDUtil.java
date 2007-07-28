/*******************************************************************************
 * Copyright 2000, 2007 Visual Systems Corporation.
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

package org.enerj.util;

import org.enerj.core.ObjectSerializer;

/**
 * Static utilities to deal with OIDs. <p>
 * 
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class OIDUtil
{
    /** Mask for OIDs to get the Object Index from the OID.  
     */
    private static long OIDX_OID_MASK = 0xFFFFFFFFFFFL; // Least significant 44 bits
    private static long CIDX_OID_BIT_SHIFT = 44; // Shift OID (>>>) 44 bits to get CIDX (upper 20 bits).
    private static int MAX_CIDX = 0xFFFFF;

    // No construction.
    private OIDUtil() { } 

    /**
     * Gets the Object Index from an OID. 
     *
     * @param anOID the OID.
     * 
     * @return the OIDX.
     */
    public static long getOIDX(long anOID)
    {
        return anOID & OIDX_OID_MASK;
    }
    
    /**
     * Gets the Class Index from an OID. 
     *
     * @param anOID the OID.
     * 
     * @return the CIDX.
     */
    public static int getCIDX(long anOID)
    {
        return (int)(anOID >>> CIDX_OID_BIT_SHIFT);
    }
    
    /**
     * Creates an OID from a Class Index and Object Index.
     *
     * @param aCIDX the class index.
     * @param anOIDX the object index.
     * 
     * @return the OID.
     */
    public static long createOID(int aCIDX, long anOIDX)
    {
        assert anOIDX <= OIDX_OID_MASK;
        assert aCIDX <= MAX_CIDX;
        
        return ((long)aCIDX << CIDX_OID_BIT_SHIFT) | anOIDX; 
    }
}

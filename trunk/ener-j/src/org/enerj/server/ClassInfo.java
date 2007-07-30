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

package org.enerj.server;

/**
 * Represent class information returned by the server. <p>
 * 
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ClassInfo
{
    private String mClassName;
    private int mCIDX; 

    /**
     * Construct a ClassInfo. 
     */
    public ClassInfo(String aClassName, int cidx)
    {
        mClassName = aClassName;
        mCIDX = cidx;
    }

    /**
     * Gets the CIDX.
     *
     * @return the CIDX.
     */
    public int getCIDX()
    {
        return mCIDX;
    }

    /**
     * Sets someCidx.
     *
     * @param someCidx a int.
     */
    public void setCIDX(int someCidx)
    {
        mCIDX = someCidx;
    }

    /**
     * Gets the className.
     *
     * @return a String.
     */
    public String getClassName()
    {
        return mClassName;
    }
    
    public String toString()
    {
        return "ClassInfo:[" + mClassName + ']';
    }
}

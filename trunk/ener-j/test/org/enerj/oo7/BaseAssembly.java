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
package org.enerj.oo7;

import java.util.ArrayList;

public class BaseAssembly extends Assembly
{
    private ArrayList componentsShared;
    private ArrayList componentsPrivate;

    public BaseAssembly()
    {
    }

    public BaseAssembly(int id, int buildDate)
    {
        super(id, buildDate);
    }


    /**
     * Gets the componentsPrivate.
     *
     * @return a ArrayList.
     */
    public ArrayList getComponentsPrivate()
    {
        return componentsPrivate;
    }


    /**
     * Sets BaseAssembly.java.
     *
     * @param someComponentsPrivate a ArrayList.
     */
    public void setComponentsPrivate(ArrayList someComponentsPrivate)
    {
        componentsPrivate = someComponentsPrivate;
    }


    /**
     * Gets the componentsShared.
     *
     * @return a ArrayList.
     */
    public ArrayList getComponentsShared()
    {
        return componentsShared;
    }


    /**
     * Sets BaseAssembly.java.
     *
     * @param someComponentsShared a ArrayList.
     */
    public void setComponentsShared(ArrayList someComponentsShared)
    {
        componentsShared = someComponentsShared;
    }
}

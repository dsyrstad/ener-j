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

import java.util.List;

public class BaseAssembly extends Assembly
{
    private List<CompositePart> componentsShared;
    private List<CompositePart> componentsPrivate;

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
     * @return a List.
     */
    public List<CompositePart> getComponentsPrivate()
    {
        return componentsPrivate;
    }


    /**
     * Sets BaseAssembly.java.
     *
     * @param someComponentsPrivate a List.
     */
    public void setComponentsPrivate(List<CompositePart> someComponentsPrivate)
    {
        componentsPrivate = someComponentsPrivate;
    }


    /**
     * Gets the componentsShared.
     *
     * @return a List.
     */
    public List<CompositePart> getComponentsShared()
    {
        return componentsShared;
    }


    /**
     * Sets BaseAssembly.java.
     *
     * @param someComponentsShared a List.
     */
    public void setComponentsShared(List<CompositePart> someComponentsShared)
    {
        componentsShared = someComponentsShared;
    }
}

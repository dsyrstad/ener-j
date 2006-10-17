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

public class Module extends DesignObj
{
    private ArrayList assemblies;
    private Assembly designRoot;
    private Manual manual;

    //--------------------------------------------------------------------------------
    /**
     * Construct a Module. 
     *
     */
    public Module()
    {
        super();
    }

    //--------------------------------------------------------------------------------
    /**
     * Construct a Module. 
     *
     * @param someId
     * @param someBuildDate
     */
    public Module(int someId, int someBuildDate)
    {
        super(someId, someBuildDate);
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the assemblies.
     *
     * @return a ArrayList.
     */
    public ArrayList getAssemblies()
    {
        return assemblies;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Module.java.
     *
     * @param someAssemblies a ArrayList.
     */
    public void setAssemblies(ArrayList someAssemblies)
    {
        assemblies = someAssemblies;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the designRoot.
     *
     * @return a Assembly.
     */
    public Assembly getDesignRoot()
    {
        return designRoot;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Module.java.
     *
     * @param someDesignRoot a Assembly.
     */
    public void setDesignRoot(Assembly someDesignRoot)
    {
        designRoot = someDesignRoot;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the manual.
     *
     * @return a Manual.
     */
    public Manual getManual()
    {
        return manual;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Module.java.
     *
     * @param someManual a Manual.
     */
    public void setManual(Manual someManual)
    {
        manual = someManual;
    }
}

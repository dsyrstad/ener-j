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

public class ComplexAssembly extends Assembly
{
    private Module module;
    private List<Assembly> subAssemblies;

    public ComplexAssembly()
    {
    }

    public ComplexAssembly(int id, int buildDate, Module module)
    {
        super(id, buildDate);
        this.module = module;
    }


    /**
     * Gets the module.
     *
     * @return a Module.
     */
    public Module getModule()
    {
        return module;
    }


    /**
     * Sets ComplexAssembly.java.
     *
     * @param someModule a Module.
     */
    public void setModule(Module someModule)
    {
        module = someModule;
    }


    /**
     * Gets the subAssemblies.
     *
     * @return a List<Assembly>.
     */
    public List<Assembly> getSubAssemblies()
    {
        return subAssemblies;
    }


    /**
     * Sets ComplexAssembly.java.
     *
     * @param someSubAssemblies a List<Assembly>.
     */
    public void setSubAssemblies(List<Assembly> someSubAssemblies)
    {
        subAssemblies = someSubAssemblies;
    }
}

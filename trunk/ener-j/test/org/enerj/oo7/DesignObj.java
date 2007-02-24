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

import org.enerj.annotations.Index;


public class DesignObj
{
    @Index(name="id")
    private int id;
    private int buildDate;
    private String type;

    public DesignObj()
    {
    }

    public DesignObj(int id, int buildDate)
    {
        this.id = id;
        this.buildDate = buildDate;
    }


    /**
     * Gets the buildDate.
     *
     * @return a int.
     */
    public int getBuildDate()
    {
        return buildDate;
    }


    /**
     * Sets DesignObj.java.
     *
     * @param someBuildDate a int.
     */
    public void setBuildDate(int someBuildDate)
    {
        buildDate = someBuildDate;
    }


    /**
     * Gets the id.
     *
     * @return a int.
     */
    public int getId()
    {
        return id;
    }


    /**
     * Sets DesignObj.java.
     *
     * @param someId a int.
     */
    public void setId(int someId)
    {
        id = someId;
    }


    /**
     * Gets the type.
     *
     * @return a String.
     */
    public String getType()
    {
        return type;
    }


    /**
     * Sets DesignObj.java.
     *
     * @param someType a String.
     */
    public void setType(String someType)
    {
        type = someType;
    }
}

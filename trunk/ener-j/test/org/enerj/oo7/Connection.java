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

public class Connection
{
    private AtomicPart from;
    private AtomicPart to;
    private int id;
    private String type;

    public Connection()
    {
    }

    public Connection(int anId, String aType, AtomicPart aFrom, AtomicPart aTo)
    {
        id = anId;
        type = aType;
        from = aFrom;
        to = aTo;

        ArrayList bag = from.getTo();
        if (bag == null) {
            bag = new ArrayList();
            from.setTo(bag);
        }
        bag.add(this);

        bag = to.getFrom();
        if (bag == null) {
            bag = new ArrayList();
            to.setFrom(bag);
        }
        
        bag.add(this);
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the from.
     *
     * @return a AtomicPart.
     */
    public AtomicPart getFrom()
    {
        return from;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Connection.java.
     *
     * @param someFrom a AtomicPart.
     */
    public void setFrom(AtomicPart someFrom)
    {
        from = someFrom;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the id.
     *
     * @return a int.
     */
    public int getId()
    {
        return id;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Connection.java.
     *
     * @param someId a int.
     */
    public void setId(int someId)
    {
        id = someId;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the to.
     *
     * @return a AtomicPart.
     */
    public AtomicPart getTo()
    {
        return to;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Connection.java.
     *
     * @param someTo a AtomicPart.
     */
    public void setTo(AtomicPart someTo)
    {
        to = someTo;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the type.
     *
     * @return a String.
     */
    public String getType()
    {
        return type;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Connection.java.
     *
     * @param someType a String.
     */
    public void setType(String someType)
    {
        type = someType;
    }
}

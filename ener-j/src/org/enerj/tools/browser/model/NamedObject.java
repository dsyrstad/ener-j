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
//Ener-J
//Copyright 2000-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/tools/enerjbrowser/model/NamedObject.java,v 1.2 2006/01/17 02:41:09 dsyrstad Exp $

package org.enerj.tools.browser.model;

/**
 * A object that has a name associated with it. <p>
 * 
 * @version $Id: NamedObject.java,v 1.2 2006/01/17 02:41:09 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class NamedObject
{
    private String mName;
    private Object mObject;
    

    /**
     * Construct a NamedObject. 
     *
     * @param aName
     * @param anObject
     */
    public NamedObject(String aName, Object anObject) 
    {
        mName = aName;
        mObject = anObject;
    }
    

    public String getName()
    {
        return mName;
    }


    public Object getObject()
    {
        return mObject;
    }
    

    public String toString()
    {
        return mName;
    }


    public boolean equals(Object anObj)
    {
        if (mObject == null) {
            return anObj == null;
        }
        
        if (anObj == null) {
            return false;
        }
        
        return mObject.equals(anObj);
    }


    public int hashCode()
    {
        if (mObject != null) {
            return mObject.hashCode();
        }
        
        return 37;
    }
}

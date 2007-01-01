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
//$Header: $

package org.enerj.core;

import org.enerj.annotations.Persist;

/**
 * Wraps a Second Class Object (SCO - e.g., a String or Integer) so that it can be stored as
 * a First Class Object (FCO) with full persistent object identity. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Persist
public class SCOWrapper
{
    private Object mObject;
    
    /**
     * Construct a SCOWrapper. 
     *
     */
    public SCOWrapper(Object anObject)
    {
        mObject = anObject;
    }

    public void setObject(Object anObject)
    {
        mObject = anObject;
    }
    
    public Object getObject()
    {
        return mObject;
    }
}

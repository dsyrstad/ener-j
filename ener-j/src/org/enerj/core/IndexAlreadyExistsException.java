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
//$Header: $

package org.enerj.core;

import org.odmg.ODMGException;

/**
 * Indicates that the index being added already exists for this class. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class IndexAlreadyExistsException extends ODMGException
{

    /**
     * Construct a IndexAlreadyExistsException. 
     *
     */
    public IndexAlreadyExistsException()
    {
    }

    /**
     * Construct a IndexAlreadyExistsException. 
     *
     * @param someMsg
     */
    public IndexAlreadyExistsException(String someMsg)
    {
        super(someMsg);
    }

    /**
     * Construct a IndexAlreadyExistsException. 
     *
     * @param someMsg
     * @param someCause
     */
    public IndexAlreadyExistsException(String someMsg, Throwable someCause)
    {
        super(someMsg, someCause);
    }

    /**
     * Construct a IndexAlreadyExistsException. 
     *
     * @param someCause
     */
    public IndexAlreadyExistsException(Throwable someCause)
    {
        super(someCause);
    }

}

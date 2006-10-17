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
// Ener-J
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/CorruptDatabaseException.java,v 1.1 2005/08/16 04:24:45 dsyrstad Exp $

package org.enerj.core;

import java.io.*;
import java.util.*;

import org.odmg.*;

/**
 * Indicates that Ener-J has detected a corrupted database.
 *
 * @version $Id: CorruptDatabaseException.java,v 1.1 2005/08/16 04:24:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class CorruptDatabaseException extends ODMGRuntimeException {
    //--------------------------------------------------------------------------
    /**
     * Construct an instance of the exception.
     */
    public CorruptDatabaseException() 
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    /**
     * Construct an instance of the exception with a descriptive message.
     * @param	msg	A description of the exception.
     */
    public CorruptDatabaseException(String msg) 
    {
        super(msg);
    }

    //--------------------------------------------------------------------------
    /**
     * Construct with an error message and a cause.
     * @param msg	The error message associated with this exception.
     * @param cause the original cause.
     */
    public CorruptDatabaseException(String msg, Throwable cause) 
    {
        super(msg, cause);
    }
}


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
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/MetaDataException.java,v 1.2 2006/05/01 22:18:22 dsyrstad Exp $

package org.enerj.enhancer;

/**
 * Thrown if there is an error with the meta data. <p>
 * 
 * @version $Id: MetaDataException.java,v 1.2 2006/05/01 22:18:22 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
class MetaDataException extends Exception
{
    
    //----------------------------------------------------------------------
    /**
     * Constructs a new MetaDataException.
     * 
     * @param aMsg a message.
     * @param aCause the offending throwable, may be null.
     */
    MetaDataException(String aMsg, Throwable aCause) 
    {
        super(aMsg, aCause);
    }

    //----------------------------------------------------------------------
    /**
     * Constructs a new MetaDataException.
     * 
     * @param aMsg a message.
     */
    MetaDataException(String aMsg) 
    {
        super(aMsg);
    }

    //----------------------------------------------------------------------
    /**
     * Constructs a new MetaDataException.
     * 
     * @param aCause the offending throwable, may be null.
     */
    MetaDataException(Throwable aCause) 
    {
        super(aCause);
    }

}

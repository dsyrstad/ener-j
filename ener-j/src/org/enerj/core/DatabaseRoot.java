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
// Copyright 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/DatabaseRoot.java,v 1.5 2006/05/16 02:40:27 dsyrstad Exp $

package org.enerj.core;

import java.util.Date;
import java.util.Map;

import org.odmg.ODMGException;
import org.enerj.annotations.Persist;

/**
 * Root object of a Ener-J ODBMS.
 *
 * @version $Id: DatabaseRoot.java,v 1.5 2006/05/16 02:40:27 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
@Persist
public class DatabaseRoot 
{
    private static final int CURRENT_DATABASE_VERSION = 1;

    /** The version of this database. */
    private int mDatabaseRootVersion = CURRENT_DATABASE_VERSION;

    /** Description of the database. */
    private String mDescription;

    /** The GMT date on which this database was created. */
    //  TODO  need other audit info on this and other classes - who, machine, mod date
    private Date mCreateDate;

    /** Database Schema. Database definitions go here. */
    private Schema mSchema;
    
    /** Bindery. Root objects bound by Database.bind() go here.  */
    private Map mBindery;
    
    //  TODO  non-definition references go here (e.g., runtime info, like currently logged in users, extents/indexes...)

    //----------------------------------------------------------------------
    /**
     * Constructs an empty DatabaseRoot.
     *
     * @param aDescription a description of the new database. If this is null,
     *  the description is set to an empty string.
     *
     * @throws ODMGException if an error occurs.
     */
    public DatabaseRoot(String aDescription) throws ODMGException
    {
        mCreateDate = new Date();
        mDescription = (aDescription == null ? "" : aDescription);
        mSchema = new Schema(mDescription);
        //  TODO  This should be a larger map (VeryLargeDMap?)
        mBindery = new RegularDMap();
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the database description.
     *
     * @return the database description.
     */
    public String getDescription()
    {
        return mDescription;
    }
    
    //----------------------------------------------------------------------
    /**
     * Sets the database description.
     *
     * @param aDescription a description of the database. If this is null,
     *  the description is set to an empty string.
     */
    public void setDescription(String aDescription)
    {
        mDescription = (aDescription == null ? "" : aDescription);
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the date that this database was created.
     *
     * @return the creation date.
     */
    public Date getCreationDate()
    {
        return mCreateDate;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the root of the database schema.
     *
     * @return the database schema.
     */
    public Schema getSchema()
    {
        return mSchema;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the bindery map. Root objects bound by Database.bind() are found here.
     *
     * @return the bindery map.
     */
    public Map getBindery()
    {
        return mBindery;
    }
}

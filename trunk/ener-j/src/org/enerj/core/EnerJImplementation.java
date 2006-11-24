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
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/EnerJImplementation.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import org.odmg.DArray;
import org.odmg.DBag;
import org.odmg.DList;
import org.odmg.DMap;
import org.odmg.DSet;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.OQLQuery;
import org.odmg.Transaction;

/**
 * Ener-J implementation of org.odmg.Implementation.
 * Also provides static versions of these methods for faster access.
 *
 * @version $Id: EnerJImplementation.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.Implementation
 */
public class EnerJImplementation implements Implementation
{
    public static final String OBJ_JUMPED_TRANSACTIONS_MSG = "Object's use jumped between transactions";

    private static EnerJImplementation sSingleton;


    /**
     * Constructor is private to force use of the static getInstance()
     * method. 
     */
    private EnerJImplementation()
    {
    }


    /**
     * Gets the singleton instance of Implementation.
     */
    public static final EnerJImplementation getInstance()
    {
        if (sSingleton == null) {
            sSingleton = new EnerJImplementation();
        }

        return sSingleton;
    }


    /**
     * Gets the Ener-J representation of the object's identifier.
     *
     * @param anObject The object whose identifier is being accessed.
     *
     * @return The object's identifier in the form of a long. Returns ObjectServer.NULL_OID
     *  if object is not a Persistable or is transient.
     */
    public static final long getEnerJObjectId(Object anObject)
    {
        if (anObject instanceof Persistable) {
            Persistable persistable = (Persistable)anObject;
            Persister persister = persistable.enerj_GetPersister();
            if (persister == null) {
x                EnerJTransaction txn = EnerJTransaction.getCurrentTransaction();
                if (txn == null) {
                    return ObjectSerializer.NULL_OID; // Transient
                }
                
                persister = (Persister)txn.getDatabase();
            }
        
            return persister.getOID(anObject);
        }
        
        return ObjectSerializer.NULL_OID;
    }


    /**
     * Get the Ener-J <code>Database</code> implementation that contains the
     * object <code>obj</code>.
     *
     * @param obj The object. This should be a Persistable object.
     *
     * @return The <code>EnerJDatabase</code> that contains the object. Returns null
     * if the object is not a Persistable.
     */
    public static final EnerJDatabase getEnerJDatabase(Object obj)
    {
        if (obj instanceof Persistable) {
            Persister persister = ((Persistable)obj).enerj_GetPersister();
            if (persister instanceof EnerJDatabase) {
                return (EnerJDatabase)persister;
            }
        }

        return null;
    }
    

    /**
     * Marks a persistable as modified. This is necessary if the contents
     * of an array contained in a persistable are modified.
     *
     * @param anObject the persistable object.
     */
    public static final void setModified(Object anObject)
    {
        if (anObject instanceof Persistable) {
            PersistableHelper.addModified((Persistable)anObject);
        }
    }
    

    /**
     * Get a new EnerJTransaction instance. Similar to newTransaction(), only
     * it returns a EnerJTransaction.
     */
    public final EnerJTransaction newEnerJTransaction()
    {
        return new EnerJTransaction();
    }


    /** 
     * Get the current EnerJTransaction for this thread. Similar to currentTransaction(),
     * only it returns a EnerJTransaction.
     */
    public final EnerJTransaction currentEnerJTransaction()
    {
        return EnerJTransaction.getCurrentTransaction();
    }


    /**
     * Get a new EnerJDatabase instance. Similar to newDatabase(), only it returns
     * a EnerJDatabase. 
     */
    public final EnerJDatabase newEnerJDatabase()
    {
        return new EnerJDatabase();
    }


    // Start of org.odmg.Implementation interface methods...



    // Javadoc will be copied from org.odmg.Implementation interface.
    public final Transaction newTransaction()
    {
        return newEnerJTransaction();
    }


    // Javadoc will be copied from org.odmg.Implementation interface.
    public final Transaction currentTransaction()
    {
        return currentEnerJTransaction();
    }


    // Javadoc will be copied from org.odmg.Implementation interface.
    public final Database newDatabase()
    {
        return newEnerJDatabase();
    }


    // Javadoc will be copied from org.odmg.Implementation interface.
    public final OQLQuery newOQLQuery()
    {
        return new EnerJOQLQuery();
    }


    // Javadoc will be copied from org.odmg.Implementation interface.
    public final DList newDList()
    {
        return new RegularDList();
    }


    // Javadoc will be copied from org.odmg.Implementation interface.
    public final DBag newDBag()
    {
        return new RegularDBag();
    }


    // Javadoc will be copied from org.odmg.Implementation interface.
    public final DSet newDSet()
    {
        return new RegularDSet();
    }


    // Javadoc will be copied from org.odmg.Implementation interface.
    public final DArray newDArray()
    {
        return new RegularDArray();
    }


    // Javadoc will be copied from org.odmg.Implementation interface.
    public final DMap newDMap()
    {
        return new RegularDMap();
    }


    // Javadoc will be copied from org.odmg.Implementation interface.
    public final String getObjectId(Object obj)
    {
        long oid = getEnerJObjectId(obj);

        return String.valueOf(oid);
    }
    

    // Javadoc will be copied from org.odmg.Implementation interface.
    public final Database getDatabase(Object obj)
    {
        return getEnerJDatabase(obj);
    }


    // ...End of org.odmg.Implementation interface methods.

}

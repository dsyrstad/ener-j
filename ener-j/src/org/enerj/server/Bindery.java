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

package org.enerj.server;

import java.util.Map;

import org.enerj.annotations.Persist;
import org.enerj.core.ObjectSerializer;
import org.enerj.core.RegularDMap;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.ObjectNameNotUniqueException;

/**
 * The Ener-J Bindery where root-level objects are bound to names. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Persist
public class Bindery
{
    // TODO This should be a VeryLargeHashMap, or something of the like. Otherwise everything will contend on a single object.
    private Map<String, Long> mBinderyMap  = (Map<String, Long>)new RegularDMap();
    
    /**
     * Construct a Bindery. 
     */
    public Bindery()
    {
    }

    /**
     * Associate a name with an object and make it persistent.
     * An object instance may be bound to more than one name.
     * Binding a previously transient object to a name makes that object persistent.
     *
     * @param anOID The OID of the object to be named.
     * @param aName The name to be given to the object.
     *
     * @throws org.odmg.ObjectNameNotUniqueException If an attempt is made to bind a name
     * to an object and that name is already bound to an object.
     */
    public void bind(long anOID, String aName) throws ObjectNameNotUniqueException
    {
        if (mBinderyMap.containsKey(aName)) {
            throw new ObjectNameNotUniqueException("An object named " + aName + " already exists.");
        }
        
        mBinderyMap.put(aName, anOID);
    }
    

    /**
     * Lookup an object via its name.
     *
     * @param aName The name of an object.
     *
     * @return The OID of the object corresponding to aName.
     *
     * @throws ObjectNameNotFoundException If there is no object with the specified name.
     */
    public long lookup(String aName) throws ObjectNameNotFoundException
    {
        long oid = mBinderyMap.get(aName);
        if (oid == ObjectSerializer.NULL_OID) {
            throw new ObjectNameNotFoundException("No object named " + aName + " exists.");
        }
        
        return oid;
    }
    

    /**
     * Disassociate a name with an object
     *
     * @param aName The name of an object.
     *
     * @throws ObjectNameNotFoundException If no object by aName exists in the database.
     */
    public void unbind(String aName) throws ObjectNameNotFoundException
    {
        if (mBinderyMap.remove(aName) == null) {
            throw new ObjectNameNotFoundException("No object named " + aName + " exists.");
        }
    }
}

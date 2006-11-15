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
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/MetaObjectServerSession.java,v 1.4 2005/11/04 03:54:25 dsyrstad Exp $

package org.enerj.server;

import java.io.*;
import java.util.*;

import org.odmg.*;

/**
 * Represents a session returned by a MetaObjectServer. Only one thread may use
 * a session at any given time. Only one top-level transaction may be active on
 * a session at any give time.
 *
 * @version $Id: MetaObjectServerSession.java,v 1.4 2005/11/04 03:54:25 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface MetaObjectServerSession extends ObjectServerSession
{
    //----------------------------------------------------------------------
    /**
     * Gets the MetaObjectServer associated with this session.
     *
     * @return a MetaObjectServer, or null if this session is disconnected.
     */
    public MetaObjectServer getMetaObjectServer();

    //----------------------------------------------------------------------
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
    public void bind(long anOID, String aName) throws ObjectNameNotUniqueException;
    
    //----------------------------------------------------------------------
    /**
     * Lookup an object via its name.
     *
     * @param aName The name of an object.
     *
     * @return The OID of the object corresponding to aName.
     *
     * @throws ObjectNameNotFoundException If there is no object with the specified name.
     */
    public long lookup(String aName) throws ObjectNameNotFoundException;
    
    //----------------------------------------------------------------------
    /**
     * Disassociate a name with an object
     *
     * @param aName The name of an object.
     *
     * @throws ObjectNameNotFoundException If no object by aName exists in the database.
     */
    public void unbind(String aName) throws ObjectNameNotFoundException;

    //----------------------------------------------------------------------
    /**
     * Removes an object from the extent and any indexes. Does not necessarily cause
     * it to be garbage collected.
     *
     * @param anOID the OID of the object to be removed.
     *
     * @throws ObjectNotPersistentException if the object does not exist in the extent.
     */
    public void removeFromExtent(long anOID) throws ObjectNotPersistentException;
    
    //----------------------------------------------------------------------
    /**
     * Determines the number of objects in an Extent.
     *
     * @param aClassName the class name to iterate over. If wantSubclasses is true,
     *  then aClassName does not have to be a persistable class.
     *
     * @param wantSubclasses if true, the sizes of all subclasses of aClassName are also included in the result.
     *
     * @return the number of objects in the Extent.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public long getExtentSize(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException;
    
    //----------------------------------------------------------------------
    /**
     * Creates an ExtentIterator.
     *
     * @param aClassName the class name to iterate over. If wantSubclasses is true,
     *  then aClassName does not have to be a persistable class.
     *
     * @param wantSubclasses if true, all subclasses of aClassName are also included in the iterator.
     *
     * @return an ExtentIterator used to iterate over the extent.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public ExtentIterator createExtentIterator(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException;
}


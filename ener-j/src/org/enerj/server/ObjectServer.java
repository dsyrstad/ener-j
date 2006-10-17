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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/ObjectServer.java,v 1.6 2006/05/16 02:40:27 dsyrstad Exp $

package org.enerj.server;


/**
 * Ener-J Object Server interface. All things that serve objects to clients
 * from the database implement this interface. 
 *
 * Beyond this interface, the following additional requirements
 * are placed on an ObjectServer implementation:<p>
 *
 * 1. A static factory method "connect" must be defined. The signature
 *    for the method is:<p>
 *
 * public static ObjectServerSession connect(Properties someProperties) throws ODMGException;<p>
 *
 * Parameters:<br> someProperties - a map of properties which define how to connect to the ObjectServer.<p>
 *
 * Throws: ODMGException in the event of an error. These errors include, but are not limited to:
 *  DatabaseNotFoundException if the database doesn't exist;  DatabaseIsReadOnlyException if the 
 *  database is read-only (e.g., on a read-only filesystem), but OPEN_READ_ONLY was not specified
 *  (note that this is really an ODMGRuntimeException). <p>
 *
 * Returns: a ObjectServerSession which references the same ObjectServer object 
 * for a given URI that refers to the same ObjectServer. <p>
 *
 * 2. The ObjectServer must be thread-safe. It is not recommended that the "synchronized"
 *    keyword be used on methods since this will have the effect of single-threading
 *    the ObjectServer. Finer-grained/shorter-term locking should be used instead.<p>
 *
 * @version $Id: ObjectServer.java,v 1.6 2006/05/16 02:40:27 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface ObjectServer
{
    /** System OID: the null OID. */
    public static final long NULL_OID = 0L;
    /** System OID: the DatabaseRoot OID. */
    public static final long DATABASE_ROOT_OID = 1L;
    /** System CID: the DatabaseRoot CID. */
    public static final long DATABASE_ROOT_CID = 1L;
    /** First available user OID. */
    public static final long FIRST_USER_OID = 1000L;
    
    /** Null Class Id (CID). */
    public static final long NULL_CID = 0L;
    /** Last available system CID. CIDs from 1 to this value are reserved for pre-enhanced system classes. */
    public static final long LAST_SYSTEM_CID = 10000L;

}


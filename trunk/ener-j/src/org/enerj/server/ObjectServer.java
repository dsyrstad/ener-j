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

import org.odmg.ODMGException;


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
 * Standard property keys are:<br>
 * <ul>
 * <li>enerj.dbname - the database name. Required.</li>
 * <li>enerj.accessmode - the database access mode. This is the integer value of the org.odmg.Database access modes. Required.</li>
 * <li>enerj.username - the username for the database. Optional.</li>
 * <li>enerj.password - the password for username. Optional.</li>
 * <li>enerj.hostname - the host to connect to. Required for a remote connection. Does not exist for a in-client local connection.</li>
 * <li>enerj.port - the port on the host to connect to. Optional. </li>
 * <li>enerj.dbpath - a list of database directory names, separated by File.pathSeparator. Required on non-proxy servers.</li> 
 * </ul>
 * 
 * These are also statically defined by this interface.
 * <p>
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
    // Property keys.
    /** The database name. Required. */
    public static final String ENERJ_DBNAME_PROP = "enerj.dbname";
    /** The database access mode. This is the integer value of the org.odmg.Database access modes. Required. */
    public static final String ENERJ_ACCESS_MODE_PROP = "enerj.accessmode";
    /** The username for the database. Optional. */
    public static final String ENERJ_USERNAME_PROP = "enerj.username";
    /** The password for username. Optional. */
    public static final String ENERJ_PASSWORD_PROP = "enerj.password"; 
    /** The host to connect to. Required for a remote connection. Must not exist for a in-client local connection. */
    public static final String ENERJ_HOSTNAME_PROP = "enerj.hostname";
    /** The port on the host to connect to. Optional. */
    public static final String ENERJ_PORT_PROP = "enerj.port"; 
    /** A list of database directory names, separated by File.pathSeparator. */
    public static final String ENERJ_DBPATH_PROP = "enerj.dbpath";
    /** The directory where the database configuration was found. */
    public static final String ENERJ_DBDIR_PROP = "enerj.dbdir";

    
    /**
     * Instructs the server to shutdown.
     *
     * @throws ODMGException if an error occurs.
     */
    public void shutdown() throws ODMGException;

}


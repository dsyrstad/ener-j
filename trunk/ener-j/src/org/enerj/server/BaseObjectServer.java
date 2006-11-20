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

import java.io.IOException;
import java.util.Properties;

import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.enerj.core.Schema;
import org.odmg.ODMGException;

/**
 * A base ObjectServer that may be used by ObjectServer implementations. Implmenting ObjectServer
 * must support a boolean property of "enerj.schemaSession" (ENERJ_SCHEMA_SESSION_PROPERTY) to indicate the connection is the 
 * privileged schema session.<p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
abstract public class BaseObjectServer implements ObjectServer
{
    /** System OID: the Schema. */
    public static final long SCHEMA_OID = 1L;
    /** System OID: the Bindery. */
    public static final long BINDERY_OID = 2L;
    /** System OID: the Class Extents. */
    public static final long EXTENTS_OID = 3L;

    static final String ENERJ_SCHEMA_SESSION_PROPERTY = "enerj.schemaSession";
    
    /** Properties that were used to create the server/session. */
    private Properties mProperties;
    
    /** Schema synchronization lock. */
    private Object mSchemaLock = new Object();
    /** The privileged schema session. Do not use this directly! */
    private BaseObjectServerSession mSchemaSession = null;
    /** Cached write-thru copy of the schema. */
    private Schema mCachedSchema = null;


    
    /**
     * Construct a BaseObjectServer. 
     */
    protected BaseObjectServer(Properties someProperties)
    {
        mProperties = someProperties;
    }

    /**
     * Gets a required property from the specified properties.
     *
     * @param someProperties the properties.
     * @param aKey the property key.
     *
     * @return the String value.
     *
     * @throws ODMGException if the property was not found.
     */
    static String getRequiredProperty(Properties someProperties, String aKey) throws ODMGException
    {
        String value = someProperties.getProperty(aKey);
        if (value == null) {
            throw new ODMGException(aKey + " was not specified");
        }
        
        return value;
    }
    

    /**
     * Gets a required property as an int from the specified properties.
     *
     * @param someProperties the properties.
     * @param aKey the property key.
     *
     * @return the int value.
     *
     * @throws ODMGException if the property was not found or the 
     *  value is not a number.
     */
    static int getRequiredIntProperty(Properties someProperties, String aKey) throws ODMGException
    {
        String value = getRequiredProperty(someProperties, aKey);
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new ODMGException("Value '" + value + " for key " + aKey + " is not an integer");
        }
    }
    
    /**
     * Gets an optional boolean property from the specified properties.
     *
     * @param someProperties the properties.
     * @param aKey the property key.
     *
     * @return the boolean value, or false if the property was not defined.
     */
    static boolean getBooleanProperty(Properties someProperties, String aKey)
    {
        String value = someProperties.getProperty(aKey);
        if (value == null) { 
            return false;
        }

        return Boolean.valueOf(value);
    }

    
    /**
     * Gets the privileged schema session. 
     *
     * @return the privileged schema session. 
     * @throws ODMGException if an error occurs.
     */
    private BaseObjectServerSession getSchemaSession() throws ODMGException
    {
        synchronized (mSchemaLock) {
            if (mSchemaSession == null) {
                Properties sessionProps = new Properties(mProperties);
                sessionProps.setProperty(ENERJ_SCHEMA_SESSION_PROPERTY, "true");
                PluginHelper.connect(getClass().getName(), sessionProps);
            }

            return mSchemaSession;
        }
    }
    
    /**
     * Gets a read-only copy of the schema.
     *
     * @return the Schema.
     * 
     * @throws ODMGException if an error occurs.
     */
    Schema getSchema() throws ODMGException
    {
        synchronized (mSchemaLock) {
            if (mCachedSchema != null) {
                BaseObjectServerSession schemaSession = getSchemaSession();
                schemaSession.beginTransaction();
                try {
                    mCachedSchema = (Schema)schemaSession.getObjectForOID(SCHEMA_OID);
                    // Resolve all references in the schema. Also disassociate from this Persister. 
                    PersistableHelper.resolveObject((Persistable)mCachedSchema, true);
                }
                catch (IOException e) {
                    throw new ODMGException(e);
                }
                finally {
                    schemaSession.rollbackTransaction();
                }
            }

            return mCachedSchema;
        }
    }

}

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

package org.enerj.server.bdb;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.enerj.core.ClassSchema;
import org.enerj.core.ClassVersionSchema;
import org.enerj.core.IndexAlreadyExistsException;
import org.enerj.core.IndexSchema;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.enerj.core.Schema;
import org.enerj.core.SystemCIDMap;
import org.enerj.server.Bindery;
import org.enerj.server.ObjectServer;
import org.enerj.server.PluginHelper;
import org.enerj.util.StringUtil;
import org.odmg.ODMGException;

/**
 * A base ObjectServer that may be used by ObjectServer implementations. Implmenting ObjectServer
 * must support a boolean property of "enerj.schemaSession" (ENERJ_SCHEMA_SESSION_PROPERTY) to indicate the connection is the 
 * privileged schema session.<p>
 * 
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
abstract public class BaseObjectServer implements ObjectServer
{
    private static final Logger sLogger = Logger.getLogger(BaseObjectServer.class.getName()); 

    /** System OID: the Schema. */
    public static final long SCHEMA_OID = 1L;
    /** System OID: the Bindery. */
    public static final long BINDERY_OID = 2L;
    public static final long NEXT_OID_NUM_OID = 3L; // OID whose data contains the next available OID number.

    static final String ENERJ_SCHEMA_SESSION_PROPERTY = "enerj.schemaSession";
    
    protected static final String SCHEMA_CLASS_NAME = Schema.class.getName();
    protected static final String[] sObjectNameArray = { Object.class.getName() };
    
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
     * Get the properties used on connnect().
     * 
     * @return the properties used on connnect().
     */
    protected Properties getConnectProperties()
    {
        return mProperties;
    }
    
    /**
     * Initialize primordial database objects needed to run the database.
     *
     * @param aSession a session on which to write the objects.
     * 
     * @throws ODMGException if an error occurs.
     */
    protected static void initDBObjects(BaseObjectServerSession aSession) throws ODMGException
    {
        aSession.pushAsPersister();
        aSession.setInSchemaInit(true);
        try {
            aSession.beginTransaction();

            Schema schema = new Schema("");

            // Initialize DB Schema. Add schema classes themselves to schema to bootstrap it.
            for (String schemaClassName : SystemCIDMap.getSystemClassNames()) {
                ClassSchema classSchema = new ClassSchema(schema, schemaClassName, null);
                schema.addClassSchema(classSchema);
                
                long cid = SystemCIDMap.getSystemCIDForClassName(schemaClassName);
                ClassVersionSchema version = new ClassVersionSchema(classSchema, cid, sObjectNameArray, null, null, null, null);
                classSchema.addVersion(version);
            }

            // Special OID for schema.
            Persistable schemaPersistable = (Persistable)schema;
            PersistableHelper.setOID(aSession, BaseObjectServer.SCHEMA_OID, schemaPersistable);
            aSession.addToModifiedList(schemaPersistable);

            // Create the bindery.
            // Special OID for bindery.
            Bindery bindery = new Bindery();
            Persistable binderyPersistable = (Persistable)bindery;
            PersistableHelper.setOID(aSession, BaseObjectServer.BINDERY_OID, binderyPersistable);
            aSession.addToModifiedList(binderyPersistable);
            
            aSession.flushModifiedObjects();
            
            aSession.commitTransaction();
        }
        finally {
            aSession.setInSchemaInit(false);
            aSession.popAsPersister();
        }
    }
    
    /**
     * Gets the privileged schema session, creating it if it doesn't exist. 
     *
     * @return the privileged schema session. 
     * @throws ODMGException if an error occurs.
     */
    protected BaseObjectServerSession getSchemaSession() throws ODMGException
    {
        synchronized (mSchemaLock) {
            if (mSchemaSession == null) {
                Properties sessionProps = new Properties(mProperties);
                sessionProps.setProperty(ENERJ_SCHEMA_SESSION_PROPERTY, "true");
                mSchemaSession = (BaseObjectServerSession)PluginHelper.connect(getClass().getName(), sessionProps);
            }

            return mSchemaSession;
        }
    }
    
    /**
     * Gets the privileged schema session. 
     *
     * @return the privileged schema session or null if it doesn't exist.
     *  
     * @throws ODMGException if an error occurs.
     */
    protected BaseObjectServerSession getSchemaSessionOrNull()
    {
        return mSchemaSession;
    }
    
    public void shutdown() throws ODMGException
    {
        mSchemaSession = null;
        mCachedSchema = null;
    }

    /**
     * Gets a read-only copy of the schema.
     *
     * @return the Schema or null if it does not exist (can happen during database creation).
     * 
     * @throws ODMGException if an error occurs.
     */
    Schema getSchema() throws ODMGException
    {
        synchronized (mSchemaLock) {
            if (mCachedSchema == null) {
                BaseObjectServerSession schemaSession = getSchemaSession();
                schemaSession.pushAsPersister();
                try {
                    schemaSession.beginTransaction();
                    mCachedSchema = (Schema)schemaSession.getObjectForOID(SCHEMA_OID);
                    // Resolve all references in the schema. Also disassociate from this Persister. 
                    PersistableHelper.resolveObject((Persistable)mCachedSchema, true);
                }
                catch (IOException e) {
                    throw new ODMGException(e);
                }
                finally {
                    schemaSession.rollbackTransaction();
                    schemaSession.popAsPersister();
                }
            }
            
            return mCachedSchema;
        }
    }
    
    
    /**
     * Adds a new ClassVersion to the schema if it doesn't already exist.
     * 
     * @param aClassName
     * @param aCID the class Id of this class.
     * @param someSuperTypeNames  Array of superclass and superinterface names that go all of the way up the hierarchy.
     *  Class names might not be Persistable in our schema.
     * @param anOriginalBytecodeDef the original, unenhanced bytecodes for the class.
     * @param somePersistentFieldNames the names of the persistent fields of the class.
     * @param someTransientFieldNames the names of the transient fields of the class.
     * 
     * @throws ODMGException if an error occurs.
     */
    void addClassVersionToSchema(String aClassName, long aCID, String[] someSuperTypeNames, 
                    byte[] anOriginalByteCodeDef, String[] somePersistentFieldNames, 
                    String[] someTransientFieldNames) throws ODMGException
    {
        synchronized (mSchemaLock) {
            // Only do this if we cached the schema at this point. This is used as an
            // indicator that we're not creating the database. TODO we could probably use a more direct indicator
            if (mCachedSchema != null) {
                BaseObjectServerSession schemaSession = getSchemaSession();
                schemaSession.pushAsPersister();
                boolean success = false;
                try {
                    schemaSession.beginTransaction();
                    Schema schema = (Schema)schemaSession.getObjectForOID(SCHEMA_OID);
    
                    ClassSchema logicalClass = schema.findClassSchema(aClassName);
                    if (logicalClass == null) {
                        logicalClass = new ClassSchema(schema, aClassName, "");
                        schema.addClassSchema(logicalClass);
                    }
                    
                    ClassVersionSchema classVersion = 
                        new ClassVersionSchema(logicalClass, aCID, someSuperTypeNames, anOriginalByteCodeDef,
                                    null, somePersistentFieldNames, someTransientFieldNames);
                    logicalClass.addVersion(classVersion);
                    
                    // Create an extent for the class.
                    // TODOLOW maybe this should be optional?
//                    ExtentMap extentMap = (ExtentMap)schemaSession.getObjectForOID(EXTENTS_OID);
//                    extentMap.createExtentForClassName(aClassName);
    
                    schemaSession.flushModifiedObjects();
                    schemaSession.commitTransaction();
                    // Force cached schema to be re-read.
                    mCachedSchema = null;
                    success = true;
                }
                finally {
                    if (!success) {
                        schemaSession.rollbackTransaction();
                    }
    
                    schemaSession.popAsPersister();
                }
            }
        }
    }
    
    /**
     * Adds an index to the schema.
     *
     * @param aClassName the name of the class to add the index to.
     * @param anIndexSchema the schema for the index. The name of the index must be unique within the
     *  indexes defined for the class.
     * 
     * @throws ODMGException
     * @throws IndexAlreadyExistsException if the index already exists.
     */
    public void addIndex(String aClassName, IndexSchema anIndexSchema) throws ODMGException, IndexAlreadyExistsException
    {
        // Validate that the index name exists
        if (StringUtil.isEmpty(anIndexSchema.getName())) {
            throw new ODMGException("No index name specified for index on class " + aClassName);
        }

        synchronized (mSchemaLock) {
            BaseObjectServerSession schemaSession = getSchemaSession();
            schemaSession.pushAsPersister();
            schemaSession.setInSchemaInit(true);
            boolean success = false;
            try {
                schemaSession.beginTransaction();
                Schema schema = (Schema)schemaSession.getObjectForOID(SCHEMA_OID);
                
                // Class name exists?
                ClassSchema classSchema = schema.findClassSchema(aClassName);
                if (classSchema == null) {
                    throw new ODMGException("Class " + aClassName + " does not exist");
                }

                // Validate that the index name is unique
                List<IndexSchema> indexes = classSchema.getIndexes();
                for (IndexSchema indexSchema : indexes) {
                    if (indexSchema.getName().equals(anIndexSchema.getName())) {
                        throw new IndexAlreadyExistsException("Index " + anIndexSchema.getName() + " already exists on class " + aClassName);
                    }
                }
                
                // Create an index for the class.
                // TODOLOW maybe this should be optional?
//                IndexMap indexMap = (IndexMap)schemaSession.getObjectForOID(INDEXES_OID);
//                indexMap.createIndexForClass(classSchema, anIndexSchema);
                
                classSchema.getIndexes().add(anIndexSchema);

                schemaSession.flushModifiedObjects();
                schemaSession.commitTransaction();
                // Force cached schema to be re-read.
                mCachedSchema = null;
                success = true;
            }
            finally {
                schemaSession.setInSchemaInit(false);
                if (!success) {
                    schemaSession.rollbackTransaction();
                }

                schemaSession.popAsPersister();
            }
        }
        
    }
}

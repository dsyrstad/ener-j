/*******************************************************************************
 * Copyright 2000, 2007 Visual Systems Corporation.
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

package org.enerj.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.enerj.annotations.Persist;
import org.odmg.ODMGException;

/**
 * Schema root for a Ener-J ODBMS.<p>
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
@Persist
public class Schema 
{
    private static final int CURRENT_DATABASE_VERSION = 1;
    private static final int CURRENT_SCHEMA_VERSION = 1;

    // Note: All object references within this class should be objects defined in SystemCID, or SCOs.
    
    /** This database's version. */
    private int mDatabaseVersion = CURRENT_DATABASE_VERSION;

    /** The version of this schema.  */
    private int mSchemaVersion = CURRENT_SCHEMA_VERSION;

    /** Description of the schema. */
    private String mDescription;

    /** The GMT date on which this schema was created. */
    //  TODO  need other audit info on this and other classes - who, machine, mod date
    private Date mCreateDate;

    /** Map of class descriptions. Key is class name. Value is ClassSchema. */
    private LinkedHashMap<String, ClassSchema> mClassMap;

    /** Map of class IDs (CIDs) to ClassVersionSchema. Key is Long(CID). Value is ClassVersionSchema. */
    private Map<Long, ClassVersionSchema> mClassVersionSchemaMap;
    
    /** Map of class indexes (CIDXs) to ClassSchema. Key is Integer(CIDX). Value is ClassSchema. */
    private Map<Integer, ClassSchema> mClassSchemaMap;
    
    /** The next CIDX to be used. Note that this starts past the system CIDs because system CIDs and system CIDXs are the same. */
    private int mNextCIDX = (int)ObjectSerializer.LAST_SYSTEM_CID + 1;
    
    /** Maps possibly non-Persistable class or interface name to a set of Persistable subclasses for that name. 
     * Key is fully-qualified dot-form class or interface name, value is a Set of ClassVersionSchema.
     */
    private Map<String, Set<ClassVersionSchema>> mSubclassMap;
     
    //  TODO  EnerJUsers object for user login validation. 
    //  TODO  Add permissions object to each logical class


    /**
     * Constructs a Schema containing only the core database classes.
     *
     * @param aDescription a description of the new schema. If this is null,
     *  the description is set to an empty string.
     *
     * @throws ODMGException in the event of an error.
     */
    public Schema(String aDescription) throws ODMGException
    {
        mCreateDate = new Date();
        mDescription = (aDescription == null ? "" : aDescription);
        mClassMap = new LinkedHashMap<String, ClassSchema>(1024);
        mClassSchemaMap = new HashMap<Integer, ClassSchema>(2048);
        mClassVersionSchemaMap = new HashMap<Long, ClassVersionSchema>(2048);
        mSubclassMap = new HashMap<String, Set<ClassVersionSchema>>(2048);
    }
    
    /**
     * Gets the next Class Index (CIDX) to use. The next number in the Schema is incremented. 
     *
     * @return the next CIDX.
     */
    public int getNextClassIndex()
    {
        return mNextCIDX++;
    }

    /**
     * Gets the schema description.
     *
     * @return the schema description.
     */
    public String getDescription()
    {
        return mDescription;
    }

    /**
     * Sets the schema description.
     *
     * @param aDescription a description of the schema. If this is null,
     *  the description is set to an empty string.
     */
    public void setDescription(String aDescription)
    {
        mDescription = (aDescription == null ? "" : aDescription);
    }
    
    /**
     * Gets the date that this schema was created.
     *
     * @return the creation date.
     */
    public Date getCreationDate()
    {
        return mCreateDate;
    }
    
    /**
     * Add a ClassSchema to this Schema.
     *
     * @param aClassSchema the ClassSchema to be added.
     *
     * @throws org.odmg.ObjectNameNotUniqueException if the class name
     *  is already defined in the Schema.
     */
    public void addClassSchema(ClassSchema aClassSchema) throws org.odmg.ObjectNameNotUniqueException
    {
        String className = aClassSchema.getClassName();
        if (mClassMap.containsKey(className)) {
            throw new org.odmg.ObjectNameNotUniqueException("Class " + className + " is already in the schema.");
        }
        
        mClassMap.put(className, aClassSchema);
    }

    /**
     * Removes a ClassSchema from this Schema.
     * This should be used with extreme caution because objects referencing the
     * class may still exist.
     *
     * @param aClassName the class name to be removed.
     *
     * @throws org.odmg.ObjectNameNotFoundException if the class name
     *  is not defined in the Schema.
     */
    public void removeClassSchema(String aClassName) throws org.odmg.ObjectNameNotFoundException
    {
        /**  TODO  run thru the versions and remove them from the class id map */
        if (mClassMap.remove(aClassName) == null) {
            throw new org.odmg.ObjectNameNotFoundException("Class " + aClassName + " does not exist in the schema.");
        }
    }

    /**
     * Finds a ClassSchema in this Schema.
     *
     * @param aClassName the class name to find.
     *
     * @return the ClassSchema for aClassName, or null if the class name
     *  cannot be found in the Schema.
     */
    public ClassSchema findClassSchema(String aClassName)
    {
        return (ClassSchema)mClassMap.get(aClassName);
    }

    /**
     * Gets a Collection of all ClassSchemas.
     *
     * @return an Collection of ClassSchema, representing all of the classes
     *  in this schema. The Collection will be empty if no ClassSchemas
     *  exist. The classes are ordered from the first one added to the schema to
     *  the last one.
     */
    public Collection<ClassSchema> getClassSchemas()
    {
        return mClassMap.values();
    }
    

    /**
     * Gets a set of all persistable direct subclasses of the given class or
     * interface name.
     *
     * @param aClassName the class or interface name.  May or may not be persistable.
     *
     * @return a Set of ClassVersionSchema representing the subclasses. An empty set
     *  is return if no persistable subclasses exist.
     */
    public Set<ClassVersionSchema> getPersistableSubclasses(String aClassName)
    {
        Set subclasses = (Set)mSubclassMap.get(aClassName);
        if (subclasses == null) {
            return (Set<ClassVersionSchema>)Collections.EMPTY_SET;
        }
        
        return subclasses;
    }
    
    /**
     * Adds a ClassVersionSchema to the global map of CIDs to class version.
     * Should only be used by ClassSchema.
     *
     * @param aClassVersionSchema the ClassVersionSchema to add.
     *
     * @throws org.odmg.ObjectNameNotUniqueException if the CID
     *  is already defined in the Schema.
     */
    void addClassVersion(ClassVersionSchema aClassVersionSchema) throws org.odmg.ObjectNameNotUniqueException
    {
        int cidxKey = aClassVersionSchema.getClassSchema().getClassIndex();
        long cid = aClassVersionSchema.getClassId();
        if (doesCIDExist(cid)) {
            throw new org.odmg.ObjectNameNotUniqueException("Class ID " + cid + " is already in the schema.");
        }
        
        mClassVersionSchemaMap.put(cid, aClassVersionSchema);
        mClassSchemaMap.put(cidxKey, aClassVersionSchema.getClassSchema());

        String[] superTypeNames = aClassVersionSchema.getSuperTypeNames();
        for (int i = 0; i < superTypeNames.length; i++) {
            addToSubclassMap(superTypeNames[i], aClassVersionSchema);
        }
    }

    /**
     * Adds a subclass to the set of subclasses for a given superclass name.
     *
     * @param aSuperclassName the name of the superclass, not necessarily a persistable class.
     * @param aClassVersionSchema the persistable subclass.
     */
    private void addToSubclassMap(String aClassName, ClassVersionSchema aClassVersionSchema)
    {
        Set subclasses = (Set)mSubclassMap.get(aClassName);
        if (subclasses == null) {
            subclasses = new PersistentHashSet(200);
            mSubclassMap.put(aClassName, subclasses);
        }
        
        subclasses.add(aClassVersionSchema);
    }
    
    /**
     * Removes a subclass from the set of subclasses for a given superclass name.
     *
     * @param aClassName the name of the superclass, not necessarily a persistable class.
     * @param aClassVersionSchema the persistable subclass.
     */
    private void removeFromSubclassMap(String aClassName, ClassVersionSchema aClassVersionSchema)
    {
        Set subclasses = (Set)mSubclassMap.get(aClassName);
        if (subclasses != null) {
            subclasses.remove(aClassVersionSchema);
        }
    }
    
    /**
     * Removes a ClassVersionSchema from the global map of CIDs to class version.
     * Should only be used by ClassSchema.
     *
     * @param aCID the CID to be removed.
     *
     * @throws org.odmg.ObjectNameNotFoundException if the class index
     *  is not defined in the Schema.
     */
    void removeClassVersion(long aCID) throws org.odmg.ObjectNameNotFoundException
    {
        ClassVersionSchema classVersionSchema = findClassVersion(aCID);
        if (classVersionSchema == null) {
            throw new org.odmg.ObjectNameNotFoundException("Class ID " + aCID + " is not in the schema.");
        }
        
        String[] superTypeNames = classVersionSchema.getSuperTypeNames();
        for (int i = 0; i < superTypeNames.length; i++) {
            removeFromSubclassMap(superTypeNames[i], classVersionSchema);
        }

        mClassVersionSchemaMap.remove(aCID);
        // Do NOT remove from mClassSchemaMap, there may be other versions.
    }


    /**
     * Finds a ClassVersionSchema in this Schema using the CID.
     *
     * @param aCID the CID to find.
     *
     * @return the ClassVersionSchema for aCID, or null if aCID
     *  cannot be found in the Schema.
     */
    public ClassVersionSchema findClassVersion(long aCID)
    {
        return mClassVersionSchemaMap.get(aCID);
    }

    /**
     * Finds a ClassSchema in this Schema using the CIDX.
     *
     * @param aCIDX the CIDX to find.
     *
     * @return the ClassVersionSchema for aCIDX, or null if aCIDX
     *  cannot be found in the Schema.
     */
    public ClassSchema findClassSchema(int aCIDX)
    {
        return mClassSchemaMap.get(aCIDX);
    }

    /**
     * Determines if a CID exists in this Schema.
     *
     * @param aCID the CID to find.
     *
     * @return true if aCID exists, or null if aCID cannot be found in the Schema.
     */
    public boolean doesCIDExist(long aCID)
    {
        return mClassVersionSchemaMap.containsKey(aCID);
    }


    /**
     * Gets the Database Version.
     *
     * @return the database version number.
     */
    public int getDatabaseVersion()
    {
        return mDatabaseVersion;
    }


    /**
     * Gets the Schema Version.
     *
     * @return the schema version number.
     */
    public int getSchemaVersion()
    {
        return mSchemaVersion;
    }
}

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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/Schema.java,v 1.7 2006/05/14 02:43:16 dsyrstad Exp $

package org.enerj.server.schema;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.enerj.annotations.Persist;
import org.enerj.core.RegularDSet;
import org.odmg.ODMGException;

/**
 * Schema root for a Ener-J ODBMS.
 *
 * @version $Id: Schema.java,v 1.7 2006/05/14 02:43:16 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
@Persist
public class Schema 
{
    private static final int CURRENT_DATABASE_VERSION = 1;
    private static final int CURRENT_SCHEMA_VERSION = 1;
    
    private static final Set EMPTY_SET = new HashSet(1);

    // Note: All object references should be org.enerj.server.schema objects, or SCOs.
    
    /** This database's version. */
    private int mDatabaseVersion = CURRENT_DATABASE_VERSION;

    /** The version of this schema.  */
    private int mSchemaVersion = CURRENT_SCHEMA_VERSION;

    /** Description of the schema. */
    private String mDescription;

    /** The GMT date on which this schema was created. */
    //  TODO  need other audit info on this and other classes - who, machine, mod date
    private Date mCreateDate;

    /** Map of class descriptions. Key is class name. Value is LogicalClassSchema. */
    private LinkedHashMap<String, LogicalClassSchema> mClassMap;
    
    /** Map of class ids to ClassVersionSchema. Key is Long(CID). Value is ClassVersionSchema. */
    private Map<Long, ClassVersionSchema> mClassIdMap;
    
    /** Maps possibly non-Persistable class or interface name to a set of Persistable subclasses for that name. 
     * Key is fully-qualified dot-form class or interface name, value is a Set of ClassVersionSchema.
     */
    private Map<String, Set<ClassVersionSchema>> mSubclassMap;
     
    //  TODO  EnerJUsers object for user login validation. 
    //  TODO  Add permissions object to each logical class

    //----------------------------------------------------------------------
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
        mClassMap = new LinkedHashMap<String, LogicalClassSchema>(1024);
        mClassIdMap = new HashMap<Long, ClassVersionSchema>(2048);
        mSubclassMap = new HashMap<String, Set<ClassVersionSchema>>(2048);
    }

    //----------------------------------------------------------------------
    /**
     * Resolve all supertype names (classes and interfaces) for the given class.
     *
     * @param aClass the class.
     *
     * @return an array of supertype names.
     */
    private static String[] getSuperTypeNames(Class aClass)
    {
        // Build the names of the super-types.
        Set superTypeNames = new HashSet(100);

        // Do all superclasses first.
        Class currentClass = aClass;
        Class superClass;
        while ((superClass = currentClass.getSuperclass()) != null) {
            superTypeNames.add( superClass.getName() );
            currentClass = superClass;
        }

        // Get all interface names.
        resolveInterfaceNames(aClass, superTypeNames);

        String[] returnSuperTypeNames = new String[ superTypeNames.size() ];
        superTypeNames.toArray(returnSuperTypeNames);
        return returnSuperTypeNames;
    }

    //----------------------------------------------------------------------
    /**
     * Resolve all superinterface names recursively and add them to someNames.
     *
     * @param aClass the class or interface to resolve the superinterfaces on.
     * @param someNames a Set that will receive the interface names.
     */
    private static void resolveInterfaceNames(Class aClass, Set someNames)
    {
        Class[] interfaces = aClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            someNames.add( interfaces[i].getName() );
            resolveInterfaceNames(interfaces[i], someNames);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Gets the schema description.
     *
     * @return the schema description.
     */
    public String getDescription()
    {
        return mDescription;
    }
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
    /**
     * Gets the date that this schema was created.
     *
     * @return the creation date.
     */
    public Date getCreationDate()
    {
        return mCreateDate;
    }
    
    //----------------------------------------------------------------------
    /**
     * Add a LogicalClassSchema to this Schema.
     *
     * @param aLogicalClassSchema the LogicalClassSchema to be added.
     *
     * @throws org.odmg.ObjectNameNotUniqueException if the class name
     *  is already defined in the Schema.
     */
    public void addLogicalClass(LogicalClassSchema aLogicalClassSchema) throws org.odmg.ObjectNameNotUniqueException
    {
        String className = aLogicalClassSchema.getClassName();
        if (mClassMap.containsKey(className)) {
            throw new org.odmg.ObjectNameNotUniqueException("Class " + className + " is already in the schema.");
        }
        
        mClassMap.put(className, aLogicalClassSchema);
    }

    //----------------------------------------------------------------------
    /**
     * Removes a LogicalClassSchema from this Schema.
     * This should be used with extreme caution because objects referencing the
     * class may still exist.
     *
     * @param aClassName the class name to be removed.
     *
     * @throws org.odmg.ObjectNameNotFoundException if the class name
     *  is not defined in the Schema.
     */
    public void removeLogicalClass(String aClassName) throws org.odmg.ObjectNameNotFoundException
    {
        /**  TODO  run thru the versions and remove them from the class id map */
        if (mClassMap.remove(aClassName) == null) {
            throw new org.odmg.ObjectNameNotFoundException("Class " + aClassName + " does not exist in the schema.");
        }
    }

    //----------------------------------------------------------------------
    /**
     * Finds a LogicalClassSchema in this Schema.
     *
     * @param aClassName the class name to find.
     *
     * @return the LogicalClassSchema for aClassName, or null if the class name
     *  cannot be found in the Schema.
     */
    public LogicalClassSchema findLogicalClass(String aClassName)
    {
        return (LogicalClassSchema)mClassMap.get(aClassName);
    }

    //----------------------------------------------------------------------
    /**
     * Gets an Iterator of all LogicalClassSchemas.
     *
     * @return an Collection of LogicalClassSchema, representing all of the classes
     *  in this schema. The Collection will be empty if no LogicalClassSchemas
     *  exist. The classes are ordered from the first one added to the schema to
     *  the last one.
     */
    public Collection<LogicalClassSchema> getLogicalClasses()
    {
        return mClassMap.values();
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets a set of all persistable direct subclasses of the given class or
     * interface name.
     *
     * @param aClassName the class or interface name.  May or may not be persistable.
     *
     * @return a Set of ClassVersionSchema representing the subclasses. An empty set
     *  is return if no persistable subclasses exist.
     */
    public Set getPersistableSubclasses(String aClassName)
    {
        Set subclasses = (Set)mSubclassMap.get(aClassName);
        if (subclasses == null) {
            return EMPTY_SET;
        }
        
        return subclasses;
    }
    
    //----------------------------------------------------------------------
    /**
     * Adds a ClassVersionSchema to the global map of Class Id to class version.
     * Should only be used by LogicalClassSchema.
     *
     * @param aClassVersionSchema the ClassVersionSchema to add.
     *
     * @throws org.odmg.ObjectNameNotUniqueException if the class id
     *  is already defined in the Schema.
     */
    void addClassVersion(ClassVersionSchema aClassVersionSchema) throws org.odmg.ObjectNameNotUniqueException
    {
        long cidKey = aClassVersionSchema.getClassId();
        if (mClassIdMap.containsKey(cidKey)) {
            throw new org.odmg.ObjectNameNotUniqueException("Class id " + cidKey + " is already in the schema. Try modifying your class to produce a different Id.");
        }
        
        mClassIdMap.put(cidKey, aClassVersionSchema);

        String[] superTypeNames = aClassVersionSchema.getSuperTypeNames();
        for (int i = 0; i < superTypeNames.length; i++) {
            addToSubclassMap(superTypeNames[i], aClassVersionSchema);
        }
    }

    //----------------------------------------------------------------------
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
            subclasses = new RegularDSet(200);
            mSubclassMap.put(aClassName, subclasses);
        }
        
        subclasses.add(aClassVersionSchema);
    }
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
    /**
     * Removes a ClassVersionSchema from the global map of Class Id to class version.
     * Should only be used by LogicalClassSchema.
     *
     * @param aCID the class id to remove.
     *
     * @throws org.odmg.ObjectNameNotFoundException if the class id
     *  is not defined in the Schema.
     */
    void removeClassVersion(long aCID) throws org.odmg.ObjectNameNotFoundException
    {
        ClassVersionSchema classVersionSchema = findClassVersion(aCID);
        if (classVersionSchema == null) {
            throw new org.odmg.ObjectNameNotFoundException("Class id " + aCID + " is not in the schema.");
        }
        
        String[] superTypeNames = classVersionSchema.getSuperTypeNames();
        for (int i = 0; i < superTypeNames.length; i++) {
            removeFromSubclassMap(superTypeNames[i], classVersionSchema);
        }

        mClassIdMap.remove(aCID);
    }

    //----------------------------------------------------------------------
    /**
     * Finds a ClassVersionSchema in this Schema using the Class Id.
     *
     * @param aCID the class id to find.
     *
     * @return the ClassVersionSchema for aCID, or null if aCID
     *  cannot be found in the Schema.
     */
    public ClassVersionSchema findClassVersion(long aCID)
    {
        return (ClassVersionSchema)mClassIdMap.get(aCID);
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the Database Version.
     *
     * @return the database version number.
     */
    public int getDatabaseVersion()
    {
        return mDatabaseVersion;
    }

    //--------------------------------------------------------------------------------
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

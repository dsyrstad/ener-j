// Ener-J
// Copyright 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/LogicalClassSchema.java,v 1.5 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import java.util.ArrayList;
import java.util.Date;

import org.enerj.annotations.Persist;

/**
 * Schema for a logical class in a Ener-J ODBMS.
 * A logical class represents the class name, extent, indexes, and a list of
 * versions of a class (ClassVersionSchema).
 *
 * @version $Id: LogicalClassSchema.java,v 1.5 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
@Persist
public class LogicalClassSchema
{
    /** The Schema which contains this class. */
    private Schema mSchema;
    
    /** Description of the class. */
    private String mDescription;
    
    /** The GMT date on which this logical class was created. */
    private Date mCreateDate;

    /** Class Name */
    private String mClassName;

    /** The versions of the class. In order of earliest to latest. This is an array of ClassVersionSchema. */
    private ArrayList mClassVersions;

    /** The extent (all objects of this class). Index to the bitset is the OID. */
    private SparseBitSet mExtent;
    
    /** Transiently cached extent - lazily constructed. */
    transient private Extent mCachedExtent = null; 

    //----------------------------------------------------------------------
    /**
     * Constructs an empty LogicalClassSchema.
     *
     * @param aSchema the Schema object which will contain this class.
     * @param aClassName the name of the class.
     * @param aDescription the class description. If this is null, the
     *  description is set to an empty string.
     */
    public LogicalClassSchema(Schema aSchema, String aClassName, String aDescription)
    {
        assert aSchema != null && aClassName != null;
        
        mSchema = aSchema;
        mClassName = aClassName;
        mCreateDate = new Date();
        setDescription(aDescription);
        mClassVersions = new ArrayList();
        //  TODO  make the node size configurable based on how many objects you want to store.
        mExtent = new SparseBitSet(1024);
        // TODO  mIndexes = new ArrayList();
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the class description.
     *
     * @return the class description.
     */
    public String getDescription()
    {
        return mDescription;
    }
    
    //----------------------------------------------------------------------
    /**
     * Sets the class description.
     *
     * @param aDescription a description of the new class. If this is null,
     *  the description is set to an empty string.
     */
    public void setDescription(String aDescription)
    {
        mDescription = (aDescription == null ? "" : aDescription);
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the date that this logical class was created.
     *
     * @return the creation date.
     */
    public Date getCreationDate()
    {
        return mCreateDate;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the class name.
     *
     * @return the class name.
     */
    public String getClassName()
    {
        return mClassName;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the Schema containing this class.
     *
     * @return a Schema.
     */
    public Schema getSchema()
    {
        return mSchema;
    }
    
    //----------------------------------------------------------------------
    /**
     * Add a ClassVersionSchema to this logical class. The class is appended
     * to the list of versions. The class is also added to Schema's global
     * map of class Ids.
     *
     * @param aClassVersionSchema the ClassVersionSchema to be added.
     *
     * @throws org.odmg.ObjectNameNotUniqueException if the class id
     *  is already defined within this logical class.
     */
    public void addVersion(ClassVersionSchema aClassVersionSchema) throws org.odmg.ObjectNameNotUniqueException
    {
        long cid = aClassVersionSchema.getClassId();
        if (findVersion(cid) != null) {
            throw new org.odmg.ObjectNameNotUniqueException("Class id " + cid + " is already in the schema.");
        }
        
        mSchema.addClassVersion(aClassVersionSchema);
        mClassVersions.add(aClassVersionSchema);
    }

    //----------------------------------------------------------------------
    /**
     * Removes a ClassVersionSchema from this logical class.
     * This should be used with extreme caution because objects referencing this
     * class may still exist.
     *
     * @param aCID the class Id to be removed.
     *
     * @throws org.odmg.ObjectNameNotFoundException if the class id
     *  is not defined within this logical class.
     */
    public void removeVersion(long aCID) throws org.odmg.ObjectNameNotFoundException
    {
        mSchema.removeClassVersion(aCID);
        for (int i = 0; i < mClassVersions.size(); i++) {
            ClassVersionSchema classVersion = (ClassVersionSchema)mClassVersions.get(i);
            if (classVersion.getClassId() == aCID) {
                mClassVersions.remove(i);
                return;
            }
        }

        throw new org.odmg.ObjectNameNotFoundException("Class id " + aCID + " does not exist in the schema.");
    }

    //----------------------------------------------------------------------
    /**
     * Finds a ClassVersionSchema within this logical class.
     *
     * @param aCID the class Id to find.
     *
     * @return the ClassVersionSchema for aClassName, or null if the class Id
     *  cannot be found within this logical class.
     */
    public ClassVersionSchema findVersion(long aCID)
    {
        for (int i = 0; i < mClassVersions.size(); i++) {
            ClassVersionSchema classVersion = (ClassVersionSchema)mClassVersions.get(i);
            if (classVersion.getClassId() == aCID) {
                return classVersion;
            }
        }
        
        return null;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the latest ClassVersionSchema within this logical class.
     *
     * @return the ClassVersionSchema for aClassName, or null if there are no versions yet.
     */
    public ClassVersionSchema getLatestVersion()
    {
        int size = mClassVersions.size();
        if (size == 0) {
            return null;
        }

        return (ClassVersionSchema)mClassVersions.get(size - 1);
    }

    //----------------------------------------------------------------------
    /**
     * Gets the all of ClassVersionSchemas within this logical class.
     *
     * @return an array of ClassVersionSchema, this will be a zero length array 
     *  if there are no versions yet. The array is in order from earliest to 
     *  latest.
     */
    public ClassVersionSchema[] getVersions()
    {
        ClassVersionSchema[] versions = new ClassVersionSchema[ mClassVersions.size() ];
        mClassVersions.toArray(versions);
        return versions;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the mutable extent for this class.
     *
     * @return the extent as a SparseBitSet whose index is an OID.
     */
    public SparseBitSet getExtentBitSet()
    {
        return mExtent;
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Returns the Extent for this class and it's subclasses.
     *
     * @return an Extent. Returns null if the class cannot be found.
     */
    public Extent getExtent()
    {
        if (mCachedExtent == null) {
            EnerJDatabase db = EnerJImplementation.getEnerJDatabase(this);
            try {
                mCachedExtent = db.getExtent(Class.forName(getClassName()), true);
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }
        
        return mCachedExtent;
    }

    //----------------------------------------------------------------------
    /**
     * Two LogicalClassSchema's are equal if their class names are equal.
     *
     * @return true if they are equal.
     */
    public boolean equals(Object anOther)
    {
        if ( !(anOther instanceof LogicalClassSchema) ) {
            return false;
        }
        
        return mClassName.equals( ((LogicalClassSchema)anOther).mClassName );
    }

    //----------------------------------------------------------------------
    /**
     * The hashcode of a LogicalClassSchema is the hashCode of the class name.
     *
     * @return the hashCode.
     */
    public int hashCode()
    {
        return mClassName.hashCode();
    }
}

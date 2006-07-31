// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/ClassDef.java,v 1.5 2006/05/05 13:47:09 dsyrstad Exp $

package org.enerj.enhancer;


/**
 * A Class defintion from a property file.
 *
 * @version $Id: ClassDef.java,v 1.5 2006/05/05 13:47:09 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class ClassDef implements Cloneable
{
    static final int TYPE_CAPABLE = 0;
    static final int TYPE_NOT_CAPABLE = 1;
    static final int TYPE_AWARE = 2;
    // Serialized types are a bad idea. There's little opportunity for ever
    // evolving them to a newer version.
    //static final int TYPE_SERIALIZED = 0;

    private String mName;
    private int mPersistentType;
    private FieldDef[] mFieldDefs;
    
    //----------------------------------------------------------------------
    /** 
     * Construct a new ClassDef.
     *
     * @param aName the class name.
     * @param aType one of the TYPE_* values defined here.
     * @param someFieldDefs the array of field definitions.
     */
    ClassDef(String aName, int aType, FieldDef[] someFieldDefs)
    {
        mName = aName;
        mPersistentType = aType;
        mFieldDefs = someFieldDefs;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the class name.
     *
     * @return the class name.
     */
    String getName()
    {
        return mName;
    }
    
    //----------------------------------------------------------------------
    /**
     * Sets the class name.
     *
     * @param aName the class name.
     */
    void setName(String aName)
    {
        mName = aName;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the persistent type. 
     *
     * @return the type as one of the TYPE_* values defined here.
     */
    int getPersistentType()
    {
        return mPersistentType;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the list of field definitions.
     *
     * @return the array of FieldDefs.
     */
    FieldDef[] getFieldDefs()
    {
        return mFieldDefs;
    }

    //----------------------------------------------------------------------
    // From Object...
    public Object clone() 
    {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }
}



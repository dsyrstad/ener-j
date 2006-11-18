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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/ClassVersionSchema.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.server.schema;

import java.util.*;

import org.odmg.*;
import org.enerj.annotations.Persist;

/**
 * Schema for a version of a class in a Ener-J ODBMS.
 * A class version represents a specific implementation of a class at a point in time.
 *
 * @version $Id: ClassVersionSchema.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
@Persist
public class ClassVersionSchema 
{
    // Note: All object references should be org.enerj.server.schema objects, or SCOs.

    /** The LogicalClassSchema which contains this class. */
    private LogicalClassSchema mLogicalClassSchema;
    
    /** The GMT date on which this class version was created. */
    private Date mCreateDate;

    /** Class Id. */
    private long mCID;
    
    /** Array of superclass and superinterface names that go all of the way up the hierarchy.
     * Class names contained here might not be Persistable in our schema.
     */
    private String[] mSuperTypeNames;
    
    /** Original, unenhanced bytecodes for this class. */
    private byte[] mOriginalBytecodes;
    
    /** Original Enhanced bytecodes. */
    private byte[] mEnhancedBytecodes;
    
    /** Proxy bytecodes to next version of class. This is null if no successor exits. */
    private byte[] mProxyBytecodes;
    
    /** Persistent field names */
    private String[] mPersistentFields;
    
    /** Transient field names. Needed to enhance PersistenceAware classes. */
    private String[] mTransientFields;

    //----------------------------------------------------------------------
    /**
     * Constructs a ClassVersionSchema.
     *
     * @param aLogicalClassSchema the LogicalClassSchema object which will contain this class version.
     *   aLogicalClassSchema.getClassName() must be loadable by the class loader.
     * @param aCID the class Id of this class.
     * @param someSuperTypeNames  Array of superclass and superinterface names that go all of the way up the hierarchy.
     *  Class names might not be Persistable in our schema.
     * @param anOriginalBytecodeDef the original, unenhanced bytecodes for the class.
     * @param anEnhancedBytecodeDef the enhanced bytecodes, based on anOriginalBytecodeDef. May be null.
     * @param somePersistentFieldNames the names of the persistent fields of the class.
     * @param someTransientFieldNames the names of the transient fields of the class.
     *
     * @throws ODMGException if an error occurs resolving the class' supertypes.
     */
    public ClassVersionSchema(LogicalClassSchema aLogicalClassSchema, long aCID, 
            String[] someSuperTypeNames, byte[] anOriginalBytecodeDef, byte[] anEnhancedBytecodeDef,
            String[] somePersistentFieldNames, String[] someTransientFieldNames)
            throws ODMGException
    {
        assert aLogicalClassSchema != null;
        
        mLogicalClassSchema = aLogicalClassSchema;
        mCreateDate = new Date();
        mCID = aCID;
        mSuperTypeNames = someSuperTypeNames;
        mOriginalBytecodes = anOriginalBytecodeDef;
        mEnhancedBytecodes = anEnhancedBytecodeDef;
        mPersistentFields = somePersistentFieldNames;
        mTransientFields = someTransientFieldNames;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets an array of all supertype names (classes and interfaces) for this class.
     *
     * @return an array of all supertype names. All persitable class will have at
     *  least one supertype of java.lang.Object.
     */
    public String[] getSuperTypeNames()
    {
        return mSuperTypeNames;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the date that this class version was created.
     *
     * @return the creation date.
     */
    public Date getCreationDate()
    {
        return mCreateDate;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the class Id of this class.
     *
     * @return the class Id.
     */
    public long getClassId()
    {
        return mCID;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the LogicalClassSchema containing this class.
     *
     * @return a LogicalClassSchema.
     */
    public LogicalClassSchema getLogicalClassSchema()
    {
        return mLogicalClassSchema;
    }
    
    //----------------------------------------------------------------------
    /** 
     * Gets the Original, unenhanced bytecodes.
     *
     * @return the original bytecodes.
     */
    public byte[] getOriginalBytecodes()
    {
        return mOriginalBytecodes;
    }    

    //----------------------------------------------------------------------
    /** 
     * Gets the enhanced bytecodes, based on the original bytecodes for this version.
     *
     * @return the enhanced bytecodes. May be null.
     */
    public byte[] getEnhancedBytecodes()
    {
        return mEnhancedBytecodes;
    }    

    //----------------------------------------------------------------------
    /** 
     * Gets the proxy bytecodes to the next version of this class.
     *
     * @return the enhanced proxy bytecodes. May be null if no proxy exists
     *  yet.
     */
    public byte[] getProxyBytecodes()
    {
        return mProxyBytecodes;
    }    

    //----------------------------------------------------------------------
    /** 
     * Sets the proxy bytecodes to the next version of this class.
     * This method should only be used by the enhancer.
     *
     * @param aProxyBytecodeDef the enhanced proxy bytecodes. 
     */
    public void setProxyBytecodes(byte[] aProxyBytecodeDef)
    {
        mProxyBytecodes = aProxyBytecodeDef;
    }    

    //----------------------------------------------------------------------
    /**
     * Gets the list of persistent field names.
     *
     * @return an array containing the names of the persistent fields.
     */
    public String[] getPersistentFieldNames()
    {
        return mPersistentFields;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the list of transient field names.
     *
     * @return an array containing the names of the transient fields.
     */
    public String[] getTransientFieldNames()
    {
        return mTransientFields;
    }


    //----------------------------------------------------------------------
    /**
     * Two ClassVersionSchema's are equal if their class Ids are equal.
     *
     * @return true if they are equal.
     */
    public boolean equals(Object anOther)
    {
        if ( !(anOther instanceof ClassVersionSchema) ) {
            return false;
        }
        
        return mCID == ((ClassVersionSchema)anOther).mCID;
                
    }

    //----------------------------------------------------------------------
    /**
     * The hashcode of a ClassVersionSchema is its class Id, truncated to
     * an int.
     *
     * @return the hashCode.
     */
    public int hashCode()
    {
        return (int)mCID;
    }
}

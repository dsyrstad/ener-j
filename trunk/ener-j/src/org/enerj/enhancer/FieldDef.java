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
// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/FieldDef.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.enhancer;

/**
 * A field defintion from a property file.
 *
 * @version $Id: FieldDef.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class FieldDef
{
    /** Use the definition from the actual class to determine if field is persistent */
    static final int PERSISTENT_USE_FIELDDEF = 0;
    /** Force field to be transient */
    static final int PERSISTENT_NO = 1;
    /** Force field to be persistent */
    static final int PERSISTENT_YES = 2;

    private String mName;
    private int mPersistentOverride;

    /** 
     * The class to which this field refers. Useful for anonymous collections. 
     * For example, this can make "Vector" become a typed collection as in 
     * "Vector<mRefersToClassName>".
     */
    private String mRefersToClassName;

    /** 
     * The field name within the class referenced by this field, which refers back
     * to the class which encloses this field.
     */
    private String mInverseReferenceFieldName;
    
    //----------------------------------------------------------------------
    /**
     * Construct a new FieldDef.
     *
     * @param aName the field name.
     * @param aPersistentOverride one of the PERSISTENT_* flags defined here.
     * @param aRefersToClassName the class name to which this field refers (for anonymous references).
     * @param aInverseReferenceFieldName the field name in the referenced class that refers back
     *   to this class.
     */
    FieldDef(String aName, int aPersistentOverride, String aRefersToClassName, String aInverseReferenceFieldName)
    {
        mName = aName;
        mPersistentOverride = aPersistentOverride;
        mRefersToClassName = aRefersToClassName;
        mInverseReferenceFieldName = aInverseReferenceFieldName;
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
     * Gets the persistent override.
     *
     * @return the override as one of the PERSISTENT_* values defined here.
     */
    int getPersistentOverride() 
    {
        return mPersistentOverride;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the "refers to" class name.
     *
     * @return the class name, or null if one was not defined.
     */
    String getRefersToClassName()
    {
        return mRefersToClassName;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the "inverse reference" field name.
     *
     * @return the field name, or null if one was not defined.
     */
    String getInverseReferenceFieldName()
    {
        return mInverseReferenceFieldName;
    }
}

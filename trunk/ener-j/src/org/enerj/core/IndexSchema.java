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

package org.enerj.core;

import java.util.Date;

import org.enerj.annotations.Index;
import org.enerj.annotations.Persist;

/**
 * Schema for an Index. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Persist
public class IndexSchema implements Cloneable
{
    private int mType;
    private String mName;
    private String[] mProperties;
    private boolean mAllowNullKeys;
    private boolean mAllowDuplicateKeys;
    /** The Key Comparator class name, if any. */
    private String mComparatorClassName;
    private Date mCreateDate;
    
    /**
     * Construct a IndexSchema from an annotation. 
     *
     * @param anIndexAnnotation
     * @param aPropertyName if not null, the property to use in place of anIndexAnnotation.properties()
     *  if it is empty.
     */
    public IndexSchema(Index anIndexAnnotation, String aPropertyName)
    {
        mType = anIndexAnnotation.type().ordinal(); // Store ordinal Because it's persistent TODO handle enums
        mName = anIndexAnnotation.name();
        mProperties = anIndexAnnotation.properties();
        mAllowNullKeys = anIndexAnnotation.allowNullKeys();
        mAllowDuplicateKeys = anIndexAnnotation.allowDuplicateKeys();
        mComparatorClassName =  anIndexAnnotation.comparator();
        
        if (mProperties.length == 0) {
            mProperties = new String[] { aPropertyName };
        }

        if (mName.length() == 0) {
            // Generate from properties.
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < mProperties.length; i++) {
                buf.append(mProperties[i]);
                buf.append("_");
            }
            
            buf.append( System.currentTimeMillis() );
        }
        
        if (mComparatorClassName.length() == 0) {
            mComparatorClassName = null;
        }
    }

    /**
     * Answers whether the index allows duplicate keys.
     *
     * @return true if duplicate keys are allowed, else false.
     */
    public boolean allowsDuplicateKeys()
    {
        return mAllowDuplicateKeys;
    }

    /**
     * Answers whether the index allows null keys.
     *
     * @return true if null keys are allowed, else false.
     */
    public boolean allowsNullKeys()
    {
        return mAllowNullKeys;
    }

    /**
     * Gets the Comparator class name.
     *
     * @return a the Class name of the Comparator.
     */
    public String getComparatorClassName()
    {
        return mComparatorClassName;
    }

    /**
     * Gets the date this index was created.
     *
     * @return a Date.
     */
    public Date getCreateDate()
    {
        return mCreateDate;
    }

    /**
     * Gets the index name.
     *
     * @return the index name.
     */
    public String getName()
    {
        return mName;
    }

    /**
     * Gets the properties being indexed.
     *
     * @return a String[] of property names with at least one element.
     */
    public String[] getProperties()
    {
        return mProperties;
    }

    /**
     * Gets the index type.
     *
     * @return an Index.Type.
     */
    public Index.Type getType()
    {
        return Index.Type.values()[mType];
    }

    @Override
    public IndexSchema clone()
    {
        try {
            return (IndexSchema)super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null; // Cannot happen.
        }
    }
}

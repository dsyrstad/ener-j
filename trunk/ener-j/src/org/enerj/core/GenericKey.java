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
//$Header: $

package org.enerj.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import org.enerj.apache.commons.beanutils.PropertyUtils;
import org.enerj.apache.commons.collections.comparators.NullComparator;
import org.odmg.ODMGRuntimeException;

/**
 * Represents a generic immutable index key. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class GenericKey implements Comparable<GenericKey>, Comparator<GenericKey>
{
    /** Key components. Stored as an SCO. */
    private Object[] mComponents;
    
    /**
     * Construct a GenericKey. 
     *
     * @param anIndexSchema the index schema corresponding to anIndexObject. 
     * @param anIndexedObject the object being indexed.
     */
    public GenericKey(IndexSchema anIndexSchema, Object anIndexedObject)
    {
        // Build the key. All the capabilities of Apach Beanutils are available for a property.
        String[] properties = anIndexSchema.getProperties();
        mComponents = new Object[ properties.length ];
        for (int i = 0; i < properties.length; i++) {
            try {
                mComponents[i] = PropertyUtils.getProperty(anIndexedObject, properties[i]);
            }
            catch (IllegalAccessException e) {
                throw new ODMGRuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new ODMGRuntimeException(e.getCause());
            }
            catch (NoSuchMethodException e) {
                throw new ODMGRuntimeException(e);
            }
            
            if (mComponents[i] != null && !(mComponents[i] instanceof Comparable)) {
                throw new ODMGRuntimeException("Property \"" + properties[i] + "\" must be a Comparable.");
            }
        }
        
        // TODO Handle this. anIndexSchema.getComparatorClassName(); Use it to compare elements.
    }
    
    /**
     * Construct a GenericKey from components 
     *
     * @param someComponents the key components, which must be {@link Comparable}s and must
     *  contain at least one element.
     */
    public GenericKey(Object[] someComponents)
    {
        assert someComponents != null && someComponents.length >= 1;
        for (int i = 0; i < someComponents.length; i++) {
            assert someComponents[i] instanceof Comparable;
        }
        
        mComponents = someComponents;
    }
    
    /**
     * Gets the components of the key.
     *
     * @return the components of the key, which will contain at least one element.
     */
    public Object[] getComponents()
    {
        return mComponents;
    }
    
    /** 
     * {@inheritDoc}
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(GenericKey anObject1, GenericKey anObject2)
    {
        assert anObject1 != null && anObject2 != null && anObject1.mComponents.length == anObject2.mComponents.length;
        
        // This is a ComparableComparator that handles nulls.
        Comparator comparator = NullComparator.COMPARABLE_INSTANCE_NULLS_HIGH;
        for (int i = 0; i < anObject1.mComponents.length; i++) {
            int result = comparator.compare(anObject1.mComponents[i], anObject2.mComponents[i]);
            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    /** 
     * {@inheritDoc}
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(GenericKey anObject)
    {
        return compare(this, anObject);
    }

    /** 
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object anObject)
    {
        return compareTo((GenericKey)anObject) == 0;
    }

    /** 
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        int hash = 0;
        for (Object component : mComponents) {
            hash = (hash * 31) + (component == null ? 0 : component.hashCode());
        }
        
        return hash;
    }

}

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

package org.enerj.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import org.enerj.annotations.Persist;
import org.enerj.apache.commons.beanutils.PropertyUtils;
import org.enerj.apache.commons.collections.comparators.NullComparator;
import org.enerj.util.TypeUtil;
import org.odmg.ODMGRuntimeException;

/**
 * Represents a generic immutable index key. <p>
 * 
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Persist
public class GenericKey implements Comparable<GenericKey>, Comparator<GenericKey>
{
    transient Object[] mResolvedComponents = null;
    
    /** Key components. Stored as an SCO. Not used if only 1 key component. */
    private Object[] mComponents = null;
    // Slight Optimization if 1 key component. Eliminates serialization of the array.
    private Object mComponent = null;
    
    /**
     * Construct a GenericKey. 
     *
     * @param anIndexSchema the index schema corresponding to anIndexObject. 
     * @param anIndexedObject the object from which the key will be created.
     * 
     * @return a GenericKey.
     */
    public static GenericKey createKey(IndexSchema anIndexSchema, Object anIndexedObject)
    {
        // Build the key. All the capabilities of Apache Beanutils are available for a property.
        // TODO Remove reliance on BeanUtils. It also has dependencies on commons-logging.
        String[] properties = anIndexSchema.getProperties();
        Object[] components = new Object[ properties.length ];
        if (anIndexedObject != null) {
            for (int i = 0; i < properties.length; i++) {
                try {
                    components[i] = PropertyUtils.getProperty(anIndexedObject, properties[i]);
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
                
                if (components[i] != null && !(components[i] instanceof Comparable)) {
                    throw new ODMGRuntimeException("Property \"" + properties[i] + "\" must be a Comparable.");
                }
            }
        }
        
        // TODO Handle this. anIndexSchema.getComparatorClassName(); Use it to compare elements.
        
        return new GenericKey(components);
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
        
        if (someComponents.length == 1) {
            mComponent = someComponents[0];
        }
        else {
            mComponents = someComponents;
        }
    }

    /**
     * Construct a GenericKey for later de-serialization. Components are not initialized.
     */
    public GenericKey()
    {
    }
    
    /**
     * Gets the components of the key.
     *
     * @return the components of the key, which will contain at least one element.
     */
    public Object[] getComponents()
    {
        if (mResolvedComponents == null) {
            if (mComponents != null) {
                mResolvedComponents = mComponents;
            }
            else {
                mResolvedComponents = new Object[] { mComponent };
            }
        }
        
        return mResolvedComponents;
    }
    
    /** 
     * {@inheritDoc}
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(GenericKey anObject1, GenericKey anObject2)
    {
        assert anObject1 != null && anObject2 != null;
        
        // This is a ComparableComparator that handles nulls.
        Comparator comparator = NullComparator.COMPARABLE_INSTANCE_NULLS_HIGH;
        Object[] components1 = anObject1.getComponents();
        Object[] components2 = anObject2.getComponents();

        // The lengths of the components arrays may be different if a partial key search is being done. 
        for (int i = 0; i < components1.length && i < components2.length; i++) {
            Object o1 = components1[i];
            Object o2 = components2[i];
            Class o1Class = o1.getClass();
            Class o2Class = o2.getClass();
            if (o1 != null && o2 != null && o1Class != o2Class) {
                // Must perform type conversion.
                Object[] objs = new Object[] { o1, o2 };
                TypeUtil.makeComparable(objs);
                o1 = objs[0];
                o2 = objs[1];
            }
            
            int result = comparator.compare(o1, o2);
            if (result != 0) {
                return result;
            }
        }
        
        // Treat different lengths as one key less than the other.
        return components1.length - components2.length;
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
        for (Object component : getComponents()) {
            hash = (hash * 31) + (component == null ? 0 : component.hashCode());
        }
        
        return hash;
    }

}

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

import java.util.Collection;
import java.util.Map;

/**
 * Defines a contract to get values for duplicate keys from a map that supports duplicate keys. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public interface DuplicateKeyMap<K, V> extends Map<K, V>
{
    /**
     * Returns a collection of values that correspond to key. This will be an empty or
     * singleton collection if duplicate keys are not supported. If duplicate keys are supported,
     * this will contain the corresponding values for all of the keys in no particular order. 
     *
     * @param key the key to be retrieved.
     * 
     * @return a Collection of values, which will be empty if no matching keys exist.
     */
    public Collection<V> getValues(Object key);
}

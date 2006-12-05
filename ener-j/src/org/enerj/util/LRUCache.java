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
//Ener-J
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/LRUCache.java,v 1.1 2006/01/20 01:33:38 dsyrstad Exp $

package org.enerj.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implements a LRU cache of a specific size. <p>
 * 
 * @version $Id: LRUCache.java,v 1.1 2006/01/20 01:33:38 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class LRUCache<K,V> extends LinkedHashMap<K,V>
{
    private static final long serialVersionUID = 1L;

    private int mMaxNumEntries;
    private Notifiable<K,V> mNotifiable;
    

    /**
     * Construct a LRUCache. 
     *
     * @param aMaxNumEntries maximum number of entries allowed in the cache.
     * @param aNotifiable a listener that is notified when an object is about to be dropped from the cache.
     *  May be null if notification is not desired.
     */
    public LRUCache(int aMaxNumEntries, Notifiable<K,V> aNotifiable)
    {
        super((aMaxNumEntries * 3) / 4 + 1, .75F, true);
        
        mMaxNumEntries = aMaxNumEntries;
    }
    
    /**
     * Sets the object to be notified. 
     *
     * @param aNotifiable a listener that is notified when an object is about to be dropped from the cache.
     *  May be null if notification is not desired.
     */
    public void setNotifiable(Notifiable<K,V> aNotifiable)
    {
        mNotifiable = aNotifiable;
    }

    protected boolean removeEldestEntry(Map.Entry<K,V> anEldestEntry)
    {
        boolean shouldRemove = size() > mMaxNumEntries;
        
        if (mNotifiable == null) {
            return shouldRemove;
        }
        
        if (shouldRemove) {
            mNotifiable.removeEntry(anEldestEntry.getKey(), anEldestEntry.getValue());
        }
        
        return false; // The notifiable will decide whether to remove the entry and the actual removal.
    }

    
    /**
     * Called by LRUCache to remove an entry from the cache. <p>
     */
    public interface Notifiable<K, V>
    {
        /**
         * Removes the entry to drop it from the cache.
         *
         * @param key the key of the object to be removed.
         * @param obj the object to be removed.
         */
        void removeEntry(K key, V obj);
    }
}

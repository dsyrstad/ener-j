//Ener-J
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/LRUCache.java,v 1.1 2006/01/20 01:33:38 dsyrstad Exp $

package org.enerj.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implements a LRU cache of a specific size by extended LinkedHashMap. <p>
 * 
 * @version $Id: LRUCache.java,v 1.1 2006/01/20 01:33:38 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class LRUCache<K,V> extends LinkedHashMap<K,V>
{
    private int mMaxNumEntries;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a LRUCache. 
     *
     * @param aMaxNumEntries maximum number of entries allowed in the cache.
     */
    public LRUCache(int aMaxNumEntries)
    {
        super((aMaxNumEntries * 3) / 4 + 1, .75F, true);
        
        mMaxNumEntries = aMaxNumEntries;
    }
    
    //--------------------------------------------------------------------------------
    protected boolean removeEldestEntry(Map.Entry anEldestEntry)
    {
         return size() < mMaxNumEntries;
    }

}

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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/ObjectServerSession.java,v 1.4 2006/01/09 02:25:12 dsyrstad Exp $
package org.enerj.server;

import java.util.Comparator;
import java.util.Map;

import org.enerj.annotations.Index;
import org.enerj.annotations.Persist;
import org.enerj.apache.commons.collections.comparators.NullComparator;
import org.enerj.core.IndexSchema;
import org.enerj.core.LargePersistentHashMap;
import org.enerj.core.LogicalClassSchema;
import org.enerj.core.PersistentBxTree;
import org.odmg.ODMGException;


/**
 * Maps indexes for all classes that define indexes.
 *
 * @version $Id:  $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
@Persist
class IndexMap
{
    /** Index mapping. Key is Class name + '@' + index name. */
    private Map<String, Map> mIndexMap = new LargePersistentHashMap<String, Map>();
    
    /**
     * Construct a new IndexMap.
     */
    IndexMap()
    {
    }

    /**
     * Creates a key for the map.
     *
     * @param aClassSchema
     * @param anIndexSchema
     * 
     * @return the key for the map.
     */
    private String createKey(LogicalClassSchema aClassSchema, IndexSchema anIndexSchema)
    {
       return aClassSchema.getClassName() + '@' + anIndexSchema.getName(); 
    }
    
    /**
     * Creates a new index for the specified class and index schema.
     * 
     * @param aClassSchema
     * @param anIndexSchema
     * 
     * @throws ODMGException if an error occurs.
     */
    void createIndexForClass(LogicalClassSchema aClassSchema, IndexSchema anIndexSchema) throws ODMGException
    {
        String key = createKey(aClassSchema, anIndexSchema);
        Map map;
        if (anIndexSchema.getType() == Index.Type.BTree) {
            // TODO Comparator should be loaded using DatabaseClassLoader
            Comparator comparator = null;
            String compClassName = anIndexSchema.getComparatorClassName();
            if (compClassName != null) {
                try {
                    Class<? extends Comparator> compClass = (Class<? extends Comparator>)Class.forName(compClassName);
                    comparator = compClass.newInstance();
                }
                catch (ClassNotFoundException e) {
                    throw new ODMGException("Cannot find comparator class " + compClassName + " for index " + 
                                    anIndexSchema.getName() + " on class " + aClassSchema.getClassName(), e);
                }
                catch (Exception e) {
                    throw new ODMGException("Cannot create comparator class " + compClassName + " for index " + 
                                    anIndexSchema.getName() + " on class " + aClassSchema.getClassName() + 
                                    ". It must have a public no-arg constructor.", e);
                }
            }
            
            if (comparator == null && anIndexSchema.allowsNullKeys()) {
                // TODOLOW should be option for null high/low comparison
                comparator = NullComparator.COMPARABLE_INSTANCE_NULLS_HIGH;
            }
            
            map = new PersistentBxTree(PersistentBxTree.DEFAULT_KEYS_PER_NODE, comparator, 
                            anIndexSchema.allowsDuplicateKeys(), false, anIndexSchema.isAscending());
        }
        else {
            map = new LargePersistentHashMap(LargePersistentHashMap.DEFAULT_NODE_SIZE, 
                            anIndexSchema.allowsDuplicateKeys());
        }

        mIndexMap.put(key, map);
    }
    
    /**
     * Drops an existing index for the specified class and index schema.
     * 
     * @param aClassSchema
     * @param anIndexSchema
     */
    void dropIndexForClass(LogicalClassSchema aClassSchema, IndexSchema anIndexSchema)
    {
        String key = createKey(aClassSchema, anIndexSchema);
        mIndexMap.remove(key);
    }

    /**
     * Gets the index for the specified class and index schema.
     * 
     * @param aClassSchema
     * @param anIndexSchema
     * 
     * @return the Map representing the index, or null if no index is defined for the class.
     */
    Map getBtreeIndex(LogicalClassSchema aClassSchema, IndexSchema anIndexSchema)
    {
        String key = createKey(aClassSchema, anIndexSchema);
        return (Map)mIndexMap.get(key);
    }
}

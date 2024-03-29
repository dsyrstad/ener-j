/*
 *  Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.enerj.apache.commons.collections.set;

import java.util.Set;

import org.enerj.apache.commons.collections.collection.SynchronizedCollection;

/**
 * Decorates another <code>Set</code> to synchronize its behaviour for a
 * multi-threaded environment.
 * <p>
 * Methods are synchronized, then forwarded to the decorated set.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Stephen Colebourne
 */
public class SynchronizedSet extends SynchronizedCollection implements Set {

    /** Serialization version */
    private static final long serialVersionUID = -8304417378626543635L;

    /**
     * Factory method to create a synchronized set.
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static Set decorate(Set set) {
        return new SynchronizedSet(set);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SynchronizedSet(Set set) {
        super(set);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @param lock  the lock object to use, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SynchronizedSet(Set set, Object lock) {
        super(set, lock);
    }

    /**
     * Gets the decorated set.
     * 
     * @return the decorated set
     */
    protected Set getSet() {
        return (Set) collection;
    }

}

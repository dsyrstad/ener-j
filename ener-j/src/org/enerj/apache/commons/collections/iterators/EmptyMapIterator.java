/*
 *  Copyright 2004 The Apache Software Foundation
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
package org.enerj.apache.commons.collections.iterators;

import org.enerj.apache.commons.collections.MapIterator;
import org.enerj.apache.commons.collections.ResettableIterator;

/** 
 * Provides an implementation of an empty map iterator.
 *
 * @since Commons Collections 3.1
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Stephen Colebourne
 */
public class EmptyMapIterator extends AbstractEmptyIterator implements MapIterator, ResettableIterator {

    /**
     * Singleton instance of the iterator.
     * @since Commons Collections 3.1
     */
    public static final MapIterator INSTANCE = new EmptyMapIterator();

    /**
     * Constructor.
     */
    protected EmptyMapIterator() {
        super();
    }

}

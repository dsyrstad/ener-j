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

import java.util.NoSuchElementException;

/** 
 * Provides an implementation of an empty iterator.
 *
 * @since Commons Collections 3.1
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Stephen Colebourne
 */
abstract class AbstractEmptyIterator {
 
    /**
     * Constructor.
     */
    protected AbstractEmptyIterator() {
        super();
    }

    public boolean hasNext() {
        return false;
    }

    public Object next() {
        throw new NoSuchElementException("Iterator contains no elements");
    }

    public boolean hasPrevious() {
        return false;
    }

    public Object previous() {
        throw new NoSuchElementException("Iterator contains no elements");
    }

    public int nextIndex() {
        return 0;
    }

    public int previousIndex() {
        return -1;
    }

    public void add(Object obj) {
        throw new UnsupportedOperationException("add() not supported for empty Iterator");
    }

    public void set(Object obj) {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public void remove() {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public Object getKey() {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public Object getValue() {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public Object setValue(Object value) {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public void reset() {
        // do nothing
    }

}

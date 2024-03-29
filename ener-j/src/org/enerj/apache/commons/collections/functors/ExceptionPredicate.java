/*
 *  Copyright 2001-2004 The Apache Software Foundation
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
package org.enerj.apache.commons.collections.functors;

import java.io.Serializable;

import org.enerj.apache.commons.collections.FunctorException;
import org.enerj.apache.commons.collections.Predicate;

/**
 * Predicate implementation that always throws an exception.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 348444 $ $Date: 2005-11-23 14:06:56 +0000 (Wed, 23 Nov 2005) $
 *
 * @author Stephen Colebourne
 */
public final class ExceptionPredicate implements Predicate, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 7179106032121985545L;
    
    /** Singleton predicate instance */
    public static final Predicate INSTANCE = new ExceptionPredicate();

    /**
     * Factory returning the singleton instance.
     * 
     * @return the singleton instance
     * @since Commons Collections 3.1
     */
    public static Predicate getInstance() {
        return INSTANCE;
    }

    /**
     * Restricted constructor.
     */
    private ExceptionPredicate() {
        super();
    }

    /**
     * Evaluates the predicate always throwing an exception.
     * 
     * @param object  the input object
     * @return never
     * @throws FunctorException always
     */
    public boolean evaluate(Object object) {
        throw new FunctorException("ExceptionPredicate invoked");
    }
    
}

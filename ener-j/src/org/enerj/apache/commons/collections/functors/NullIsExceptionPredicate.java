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
 * Predicate implementation that throws an exception if the input is null.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 348444 $ $Date: 2005-11-23 14:06:56 +0000 (Wed, 23 Nov 2005) $
 *
 * @author Stephen Colebourne
 */
public final class NullIsExceptionPredicate implements Predicate, PredicateDecorator, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 3243449850504576071L;
    
    /** The predicate to decorate */
    private final Predicate iPredicate;
    
    /**
     * Factory to create the null exception predicate.
     * 
     * @param predicate  the predicate to decorate, not null
     * @return the predicate
     * @throws IllegalArgumentException if the predicate is null
     */
    public static Predicate getInstance(Predicate predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new NullIsExceptionPredicate(predicate);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param predicate  the predicate to call after the null check
     */
    public NullIsExceptionPredicate(Predicate predicate) {
        super();
        iPredicate = predicate;
    }

    /**
     * Evaluates the predicate returning the result of the decorated predicate
     * once a null check is performed.
     * 
     * @param object  the input object
     * @return true if decorated predicate returns true
     * @throws FunctorException if input is null
     */
    public boolean evaluate(Object object) {
        if (object == null) {
            throw new FunctorException("Input Object must not be null");
        }
        return iPredicate.evaluate(object);
    }

    /**
     * Gets the predicate being decorated.
     * 
     * @return the predicate as the only element in an array
     * @since Commons Collections 3.1
     */
    public Predicate[] getPredicates() {
        return new Predicate[] {iPredicate};
    }

}

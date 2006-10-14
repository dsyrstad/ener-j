// ============================================================================
// $Id: Conditional.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
// Copyright (c) 2002  David A. Hall
// ============================================================================
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.fn.adaptor;

import org.enerj.jga.fn.UnaryFunctor;

/**
 * @deprecated renamed to ConditionalUnary
 **/

public class Conditional<T,R> extends ConditionalUnary<T,R> {

    static final long serialVersionUID = 1290914353089478157L;

    /**
     * Builds a Conditional functor, given the condition to test, and the two
     * functors that may be executed.
     * @throws IllegalArgumentException if any of the functors is missing
     */
    public Conditional(UnaryFunctor<T,Boolean> test, UnaryFunctor<T,R> trueFn,
                       UnaryFunctor<T,R> falseFn)
    {
        super(test, trueFn, falseFn);
    }
}
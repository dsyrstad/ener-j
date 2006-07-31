// ============================================================================
// $Id: BinaryPredicate.java,v 1.3 2005/08/12 02:56:47 dsyrstad Exp $
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

package org.enerj.jga.fn;

import java.io.Serializable;

/**
 * A Predicate that takes two arguments and returns a boolean result.  The two
 * arguments are of type <code>T1</code> and <code>T2</code>
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

abstract public class BinaryPredicate<T1,T2>
    extends BinaryFunctor<T1,T2,Boolean>
{
    /**
     * Evaluates the predicate and returns true or false.
     */
    public boolean p(T1 arg1, T2 arg2) { return fn(arg1, arg2); }
} 


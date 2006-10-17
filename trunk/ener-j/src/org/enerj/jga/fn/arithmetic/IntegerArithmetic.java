// ============================================================================
// $Id: IntegerArithmetic.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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

package org.enerj.jga.fn.arithmetic;

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

public interface IntegerArithmetic<T extends Number>
     extends Arithmetic<T>
{
     /**
      * For numeric arguments x and y, returns x % y
      * @return the modulus of the two arguments
      */

     public T modulus (T x, T y);

     /**
      * For numeric arguments x and y, returns x & y
      * @return x & y
      */

     public T and (T x, T y);

    /**
      * For numeric arguments x and y, returns x | y
      * @return x | y
      */

     public T or (T x, T y);

    /**
      * For numeric arguments x and y, returns x ^ y
      * @return x ^ y
      */

     public T xor (T x, T y);

     /**
      * For numeric arguments x, returns ~x
      * @return the one's complement of the argument
      */

     public T not (T x);

    /**
     * @return x << y
     */

    public T shiftLeft(T x, Integer y);

    /**
     * @return x >> y
     */

    public T signedShiftRight(T x, Integer y);

    /**
     * Optional.
     * @return x >>> y
     */

    public T unsignedShiftRight(T x, Integer y);
}

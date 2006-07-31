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

/**
 * Defines arithmetic operations appropriate for Integral Numbers.
 * <p>
 * An implementation of IntegerArithmetic for classes (such as 
 * BigInteger) that provide the appropriate operations can simply map these
 * methods to the methods provided by the Number.  For the reference types,
 * the implementation will need to dereference the arguments, perform the
 * specified operation on the resulting primitives, and box up the result in
 * a new reference type.
 * <p>
 * Implementations of IntegerArithmetic for user-defined Number classes must
 * be registered with the ArithmeticFactory class.  See the Arithmetic class
 * for details.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 * 
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

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

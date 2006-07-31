// ============================================================================
// $Id: Arithmetic.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import java.io.Serializable;

/**
 * Defines arithmetic operations for classes derived from Number.
 * <p>
 * An implementation of Arithmetic for classes (such as BigDecimal and
 * BigInteger) that provide the appropriate operations can simply map these
 * methods to the methods provided by the Number.  For the reference types,
 * the implementation will need to dereference the arguments, perform the
 * specified operation on the resulting primitives, and box up the result in
 * a new reference type.
 * <p>
 * This interface may be used with user-defined Number implementations.  For
 * Example, assuming that a <code>Fraction</code> class has been defined,
 * support for Fraction Arithmetic could be provided by<br>
 * <pre>
 * public class FractionMath implements Arithmetic&lt;Fraction&gt; {
 *     public Fraction plus (Fraction x, Fraction y) {
 *         // implementation omitted
 *     }
 *     ...
 * }
 * </pre>
 * <p>
 * To use Fractions with the various arithmetic Functors, it is necessary to
 * register the Arithmetic implementation with the ArithmeticFactory.<br>
 * <pre>
 * ArithmeticFactory.register(Fraction.class, new FractionMath());
 * </pre>
 * <p>
 * Copyright &copy; 2003  David A. Hall
 * 
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public interface Arithmetic<T extends Number> extends Serializable {
    /**
     * Returns the given value in the appropriate type
     * @throws IllegalArgumentException if the value cannot be converted
     */

    public T valueOf(Number value) throws IllegalArgumentException;
    
    /**
     * Returns the value 0 of the appropriate type
     */

    public T zero();

    /**
     * Returns the value 1 of the appropriate type
     */

    public T one();

    /**
    * For numeric arguments x and y, returns x + y
    * @return the sum of the two arguments
    */

    public T plus (T x, T y);

   /**
    * For numeric arguments x and y, returns x - y
    * @return the difference of the two arguments
    */
     
    public T minus (T x, T y);
     
   /**
    * For numeric arguments x and y, returns x * y
    * @return the product of the two arguments
    */

    public T multiplies (T x, T y);

    /**
     * For numeric arguments x and y, returns x / y
     * @return the quotient of the two arguments
     */

    public T divides (T x, T y);

    /**
     * for numeric argument x, returns -x
     * @return the negative of its argument
     */

    public T negate (T x);
}


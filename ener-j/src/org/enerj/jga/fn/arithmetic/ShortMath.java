// ============================================================================
// $Id: ShortMath.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
// ============================================================================
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.fn.arithmetic;

/**
 * Provides Arithmetic implementation for Shorts
 * <p>
 * Copyright &copy; 2003  David A. Hall
 * 
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

class ShortMath implements IntegerArithmetic<Short> {
     
    static final long serialVersionUID = 9167946402888608933L;

    static private final Short ZERO = new Short((short) 0);
    static private final Short ONE  = new Short((short) 1);
    
    /**
     * Returns the given value in the appropriate type
     * @throws IllegalArgumentException if the value cannot be converted
     */

    public Short valueOf(Number value) throws IllegalArgumentException {
        return new Short(value.shortValue());
    }

    /**
     * Returns the value 0 of the appropriate type
     */

    public Short zero() {
        return ZERO;
    }

    /**
     * Returns the value 1 of the appropriate type
     */

    public Short one() {
        return ONE;
    }

   /**
    * For numeric arguments x and y, returns x + y
    * @return the sum of the two arguments
    */

    public Short plus (Short x, Short y) {
        return new Short((short) (x.shortValue() + y.shortValue()));
    }

   /**
    * For numeric arguments x and y, returns x - y
    * @return the difference of the two arguments
    */
     
    public Short minus (Short x, Short y) {
        return new Short((short) (x.shortValue() - y.shortValue()));
    }
     
   /**
    * For numeric arguments x and y, returns x * y
    * @return the product of the two arguments
    */

    public Short multiplies (Short x, Short y){
        return new Short((short) (x.shortValue() * y.shortValue()));
    }
         

    /**
     * For numeric arguments x and y, returns x / y
     * @return the quotient of the two arguments
     */

    public Short divides (Short x, Short y) {
        return new Short((short) (x.shortValue() / y.shortValue()));
    }
     
    /**
     * for numeric argument x, returns -x
     * @return the negative of its argument
     */

    public Short negate (Short x) {
        return new Short((short) -x.shortValue());
    }
    
   /**
    * For numeric arguments x and y, returns x % y
    * @return the modulus of the two arguments
    */
     
    public Short modulus (Short x, Short y) {
        return new Short((short) (x.shortValue() % y.shortValue()));
    }
    /**
     * For numeric arguments x and y, returns x &amp; y
     * @return x amp; y
     */

    public Short and (Short x, Short y) {
        return new Short((short) (x.shortValue() & y.shortValue()));
    }

    /**
     * For numeric arguments x and y, returns x | y
     * @return x | y
     */

    public Short or (Short x, Short y) {
        return new Short((short) (x.shortValue() | y.shortValue()));
    }

    /**
     * For numeric arguments x and y, returns x ^ y
     * @return x ^y
     */

    public Short xor (Short x, Short y) {
        return new Short((short) (x.shortValue() ^ y.shortValue()));
    }

    /**
     * For numeric argument x, returns ~x
     * @return the one's complement of the argument
     */

    public Short not (Short x) {
        return new Short((short) ~x.shortValue());
    }

    
    /**
     * @return x << y
     */

    public Short shiftLeft(Short x, Integer y) {
        return new Short((short) (x.shortValue() << y.intValue()));
    }        

    /**
     * @return x >> y
     */

    public Short signedShiftRight(Short x, Integer y) {
        return new Short((short) (x.shortValue() >> y.intValue()));
    }

    /**
     * Optional.
     * @return x >>> y
     */

    public Short unsignedShiftRight(Short x, Integer y) {
        return new Short((short) (x.shortValue() >>> y.intValue()));
    }
}

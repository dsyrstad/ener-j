// ============================================================================
// $Id: IntegerMath.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

/**
 * Provides Arithmetic implementation for Integers
 * <p>
 * Copyright &copy; 2003  David A. Hall
 * 
 * @author <a href="mailto:dave@dolphin.hallsquared.org">David A. Hall</a>
 */

package org.enerj.jga.fn.arithmetic;

class IntegerMath implements IntegerArithmetic<Integer> {
     
    static final long serialVersionUID = 4213102951085209726L;

    static private final Integer ZERO = new Integer(0);
    static private final Integer ONE  = new Integer(1);
    
    /**
     * Returns the given value in the appropriate type
     * @throws IllegalArgumentException if the value cannot be converted
     */

    public Integer valueOf(Number value) throws IllegalArgumentException {
        return new Integer(value.intValue());
    }

    /**
     * Returns the value 0 of the appropriate type
     */

    public Integer zero() {
        return ZERO;
    }

    /**
     * Returns the value 1 of the appropriate type
     */

    public Integer one() {
        return ONE;
    }

   /**
    * For numeric arguments x and y, returns x + y
    * @return the sum of the two arguments
    */

    public Integer plus (Integer x, Integer y) {
        return new Integer(x.intValue() + y.intValue());
    }

   /**
    * For numeric arguments x and y, returns x - y
    * @return the difference of the two arguments
    */
     
    public Integer minus (Integer x, Integer y) {
        return new Integer(x.intValue() - y.intValue());
    }
     
   /**
    * For numeric arguments x and y, returns x * y
    * @return the product of the two arguments
    */

    public Integer multiplies (Integer x, Integer y){
        return new Integer(x.intValue() * y.intValue());
    }
         

    /**
     * For numeric arguments x and y, returns x / y
     * @return the quotient of the two arguments
     */

    public Integer divides (Integer x, Integer y) {
        return new Integer(x.intValue() / y.intValue());
    }

    /**
     * for numeric argument x, returns -x
     * @return the negative of its argument
     */

    public Integer negate (Integer x) {
        return new Integer(-x.intValue());
    }
    
   /**
    * For numeric arguments x and y, returns x % y
    * @return the modulus of the two arguments
    */
     
    public Integer modulus (Integer x, Integer y) {
        return new Integer(x.intValue() % y.intValue());
    }

    /**
     * For numeric arguments x and y, returns x &amp; y
     * @return x amp; y
     */

    public Integer and (Integer x, Integer y) {
        return new Integer(x.intValue() & y.intValue());
    }

    /**
     * For numeric arguments x and y, returns x | y
     * @return x | y
     */

    public Integer or (Integer x, Integer y) {
        return new Integer(x.intValue() | y.intValue());
    }

    /**
     * For numeric arguments x and y, returns x ^ y
     * @return x ^ y
     */

    public Integer xor (Integer x, Integer y) {
        return new Integer(x.intValue() ^ y.intValue());
    }

    /**
     * For numeric argument x, returns ~x
     * @return the one's complement of the argument
     */

    public Integer not (Integer x) {
        return new Integer(~x.intValue());
    }

    /**
     * @return x << y
     */

    public Integer shiftLeft(Integer x, Integer y) {
        return new Integer(x.intValue() << y.intValue());
    }        

    /**
     * @return x >> y
     */

    public Integer signedShiftRight(Integer x, Integer y) {
        return new Integer(x.intValue() >> y.intValue());
    }

    /**
     * Optional.
     * @return x >>> y
     */

    public Integer unsignedShiftRight(Integer x, Integer y) {
        return new Integer(x.intValue() >>> y.intValue());
    }
}

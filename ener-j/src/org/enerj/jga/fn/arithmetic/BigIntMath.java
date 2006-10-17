// ============================================================================
// $Id: BigIntMath.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import java.math.BigInteger;

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

class BigIntMath implements IntegerArithmetic<BigInteger> {
    
    static final long serialVersionUID = 8004298803104674966L;
    
    /**
     * Returns the given value in the appropriate type
     * @throws IllegalArgumentException if the value cannot be converted
     */

    public BigInteger valueOf(Number value) throws IllegalArgumentException {
        return BigInteger.valueOf(value.longValue());
    }

    /**
     * Returns the value 0 of the appropriate type
     */

    public BigInteger zero() {
        return BigInteger.ZERO;
    }

    /**
     * Returns the value 1 of the appropriate type
     */

    public BigInteger one() {
        return BigInteger.ONE;
    }

   /**
    * For numeric arguments x and y, returns x + y
    * @return the sum of the two arguments
    */

    public BigInteger plus (BigInteger x, BigInteger y) {
        return x.add(y);
    }

   /**
    * For numeric arguments x and y, returns x - y
    * @return the difference of the two arguments
    */
     
    public BigInteger minus (BigInteger x, BigInteger y) {
        return x.subtract(y);
    }
     
   /**
    * For numeric arguments x and y, returns x * y
    * @return the product of the two arguments
    */

    public BigInteger multiplies (BigInteger x, BigInteger y){
        return x.multiply(y);
    }
         
    /**
     * For numeric arguments x and y, returns x / y 
     * @return the quotient of the two arguments
     */

    public BigInteger divides (BigInteger x, BigInteger y) {
        return x.divide(y);
    }
     
    /**
     * for numeric argument x, returns -x
     * @return the negative of its argument
     */

    public BigInteger negate (BigInteger x) {
        return x.negate();
    }
    
   /**
    * For numeric arguments x and y, returns x % y
    * @return the modulus of the two arguments
    */
     
    public BigInteger modulus (BigInteger x, BigInteger y) {
        return x.mod(y);
    }

    /**
     * For numeric arguments x and y, returns x &amp; y
     * @return x amp; y
     */

    public BigInteger and (BigInteger x, BigInteger y) {
        return x.and(y);
    }

    /**
     * For numeric arguments x and y, returns x | y
     * @return x | y
     */

    public BigInteger or (BigInteger x, BigInteger y) {
        return x.or(y);
    }

    /**
     * For numeric arguments x and y, returns x ^ y
     * @return x ^ y
     */

    public BigInteger xor (BigInteger x, BigInteger y) {
        return x.xor(y);
    }

    /**
     * For numeric argument x, returns ~x
     * @return the one's complement of the argument
     */

    public BigInteger not (BigInteger x) {
        return x.not();
    }
    
    /**
     * @return x << y
     */

    public BigInteger shiftLeft(BigInteger x, Integer y) {
        return x.shiftLeft(y.intValue());
    }        

    /**
     * @return x >> y
     */

    public BigInteger signedShiftRight(BigInteger x, Integer y) {
        return x.shiftRight(y.intValue());
    }

    /**
     * Optional.
     * @return x >>> y
     */

    public BigInteger unsignedShiftRight(BigInteger x, Integer y) {
        String msg = "BigInteger cannot support unsigned shift";
        throw new UnsupportedOperationException(msg);
    }
}

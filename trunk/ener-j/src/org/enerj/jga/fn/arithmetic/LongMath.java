// ============================================================================
// $Id: LongMath.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

class LongMath implements IntegerArithmetic<Long> {
     
    static final long serialVersionUID = 7284047144544492350L;

    static private final Long ZERO = new Long(0L);
    static private final Long ONE  = new Long(1L);
    
    /**
     * Returns the given value in the appropriate type
     * @throws IllegalArgumentException if the value cannot be converted
     */

    public Long valueOf(Number value) throws IllegalArgumentException {
        return new Long(value.longValue());
    }

    /**
     * Returns the value 0 of the appropriate type
     */

    public Long zero() {
        return ZERO;
    }

    /**
     * Returns the value 1 of the appropriate type
     */

    public Long one() {
        return ONE;
    }

   /**
    * For numeric arguments x and y, returns x + y
    * @return the sum of the two arguments
    */

    public Long plus (Long x, Long y) {
        return new Long(x.longValue() + y.longValue());
    }

   /**
    * For numeric arguments x and y, returns x - y
    * @return the difference of the two arguments
    */
     
    public Long minus (Long x, Long y) {
        return new Long(x.longValue() - y.longValue());
    }
     
   /**
    * For numeric arguments x and y, returns x * y
    * @return the product of the two arguments
    */

    public Long multiplies (Long x, Long y){
        return new Long(x.longValue() * y.longValue());
    }
         

    /**
     * For numeric arguments x and y, returns x / y
     * @return the quotient of the two arguments
     */

    public Long divides (Long x, Long y) {
        return new Long(x.longValue() / y.longValue());
    }
     
    /**
     * for numeric argument x, returns -x
     * @return the negative of its argument
     */

    public Long negate (Long x) {
        return new Long(-x.longValue());
    }
    
   /**
    * For numeric arguments x and y, returns x % y
    * @return the modulus of the two arguments
    */
     
    public Long modulus (Long x, Long y) {
        return new Long(x.longValue() % y.longValue());
    }

    /**
     * For numeric arguments x and y, returns x &amp; y
     * @return x amp; y
     */

    public Long and (Long x, Long y) {
        return new Long(x.longValue() & y.longValue());
    }

    /**
     * For numeric arguments x and y, returns x | y
     * @return x | y
     */

    public Long or (Long x, Long y) {
        return new Long(x.longValue() | y.longValue());
    }

    /**
     * For numeric arguments x and y, returns x ^ y
     * @return x ^ y
     */

    public Long xor (Long x, Long y) {
        return new Long(x.longValue() ^ y.longValue());
    }

    /**
     * For numeric argument x, returns ~x
     * @return the one's complement of the argument
     */

    public Long not (Long x) {
        return new Long(~x.longValue());
    }

    /**
     * @return x << y
     */

    public Long shiftLeft(Long x, Integer y) {
        return new Long(x.longValue() << y.intValue());
    }        

    /**
     * @return x >> y
     */

    public Long signedShiftRight(Long x, Integer y) {
        return new Long(x.longValue() >> y.intValue());
    }

    /**
     * Optional.
     * @return x >>> y
     */

    public Long unsignedShiftRight(Long x, Integer y) {
        return new Long(x.longValue() >>> y.intValue());
    }
}

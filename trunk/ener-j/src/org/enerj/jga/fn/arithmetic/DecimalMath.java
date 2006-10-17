// ============================================================================
// $Id: DecimalMath.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

import java.math.BigDecimal;

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

class DecimalMath implements Arithmetic<BigDecimal> {

    static final long serialVersionUID = 2565703485705053014L;    

    static private final BigDecimal ZERO = new BigDecimal(0.0);
    static private final BigDecimal ONE  = new BigDecimal(1.0);
    
    /**
     * Returns the given value in the appropriate type
     * @throws IllegalArgumentException if the value cannot be converted
     */

    public BigDecimal valueOf(Number value) throws IllegalArgumentException {
        if (value instanceof BigDecimal)
            return (BigDecimal) value;
        else
            return new BigDecimal(value.toString());
    }

    /**
     * Returns the value 0 of the appropriate type
     */

    public BigDecimal zero() {
        return ZERO;
    }

    /**
     * Returns the value 1 of the appropriate type
     */

    public BigDecimal one() {
        return ONE;
    }

   /**
    * For numeric arguments x and y, returns x + y
    * @return the sum of the two arguments
    */

    public BigDecimal plus (BigDecimal x, BigDecimal y) {
        return x.add(y);
    }

   /**
    * For numeric arguments x and y, returns x - y
    * @return the difference of the two arguments
    */
     
    public BigDecimal minus (BigDecimal x, BigDecimal y) {
        return x.subtract(y);
    }
     
   /**
    * For numeric arguments x and y, returns x * y
    * @return the product of the two arguments
    */
     

    public BigDecimal multiplies (BigDecimal x, BigDecimal y){
        return x.multiply(y);
    }
         

    /**
     * For numeric arguments x and y, returns x / y (rounded HALF_UP)
     * @return the quotient of the two arguments
     */

    public BigDecimal divides (BigDecimal x, BigDecimal y) {
        return x.divide(y,BigDecimal.ROUND_HALF_UP);
    }

    /**
     * for numeric argument x, returns -x
     * @return the negative of its argument
     */

    public BigDecimal negate (BigDecimal x) {
        return x.negate();
    }
}

// ============================================================================
// $Id: FloatMath.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.arithmetic;

class FloatMath implements Arithmetic<Float> {

    static final long serialVersionUID = 8813677408947677131L;
         
    static private final Float ZERO = new Float(0.0f);
    static private final Float ONE  = new Float(1.0f);
    
    /**
     * Returns the given value in the appropriate type
     * @throws IllegalArgumentException if the value cannot be converted
     */

    public Float valueOf(Number value) throws IllegalArgumentException {
        return new Float(value.floatValue());
    }

    /**
     * Returns the value 0 of the appropriate type
     */

    public Float zero() {
        return ZERO;
    }

    /**
     * Returns the value 1 of the appropriate type
     */

    public Float one() {
        return ONE;
    }

   /**
    * For numeric arguments x and y, returns x + y
    * @return the sum of the two arguments
    */

    public Float plus (Float x, Float y) {
        return new Float(x.floatValue() + y.floatValue());
    }

   /**
    * For numeric arguments x and y, returns x - y
    * @return the difference of the two arguments
    */
     
    public Float minus (Float x, Float y) {
        return new Float(x.floatValue() - y.floatValue());
    }
     
   /**
    * For numeric arguments x and y, returns x * y
    * @return the product of the two arguments
    */
     

    public Float multiplies (Float x, Float y){
        return new Float(x.floatValue() * y.floatValue());
    }
         

    /**
     * For numeric arguments x and y, returns x / y
     * @return the quotient of the two arguments
     */

    public Float divides (Float x, Float y) {
        return new Float(x.floatValue() / y.floatValue());
    }

    /**
     * for numeric argument x, returns -x
     * @return the negative of its argument
     */

    public Float negate (Float x) {
        return new Float(-x.floatValue());
    }
}

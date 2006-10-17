// ============================================================================
// $Id: ByteMath.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

class ByteMath implements IntegerArithmetic<Byte> {
    
    static final long serialVersionUID = -8417130628589605890L;
    
    static public final Byte ZERO = new Byte((byte) 0);
    static public final Byte ONE  = new Byte((byte) 1);
    
    /**
     * Returns the given value in the appropriate type
     * @throws IllegalArgumentException if the value cannot be converted
     */

    public Byte valueOf(Number value) throws IllegalArgumentException {
        return new Byte(value.byteValue());
    }
    
    /**
     * Returns the value 0 of the appropriate type
     */

    public Byte zero() {
        return ZERO;
    }

    /**
     * Returns the value 1 of the appropriate type
     */

    public Byte one() {
        return ONE;
    }

   /**
    * For numeric arguments x and y, returns x + y
    * @return the sum of the two arguments
    */

    public Byte plus (Byte x, Byte y) {
        return new Byte((byte) (x.byteValue() + y.byteValue()));
    }

   /**
    * For numeric arguments x and y, returns x - y
    * @return the difference of the two arguments
    */
     
    public Byte minus (Byte x, Byte y) {
        return new Byte((byte) (x.byteValue() - y.byteValue()));
    }
     
   /**
    * For numeric arguments x and y, returns x * y
    * @return the product of the two arguments
    */

    public Byte multiplies (Byte x, Byte y){
        return new Byte((byte) (x.byteValue() * y.byteValue()));
    }
         

    /**
     * For numeric arguments x and y, returns x / y
     * @return the quotient of the two arguments
     */

    public Byte divides (Byte x, Byte y) {
        return new Byte((byte) (x.byteValue() / y.byteValue()));
    }
     
    /**
     * for numeric argument x, returns -x
     * @return the negative of its argument
     */

    public Byte negate (Byte x) {
        return new Byte((byte) -x.byteValue());
    }
    
   /**
    * For numeric arguments x and y, returns x % y
    * @return the modulus of the two arguments
    */
     
    public Byte modulus (Byte x, Byte y) {
        return new Byte((byte) (x.byteValue() % y.byteValue()));
    }

    /**
     * For numeric arguments x and y, returns x &amp; y
     * @return x amp; y
     */

    public Byte and (Byte x, Byte y) {
        return new Byte((byte) (x.byteValue() & y.byteValue()));
    }

    /**
     * For numeric arguments x and y, returns x | y
     * @return x | y
     */

    public Byte or (Byte x, Byte y) {
        return new Byte((byte) (x.byteValue() | y.byteValue()));
    }

    /**
     * For numeric arguments x and y, returns x ^ y
     * @return x ^ y
     */

    public Byte xor (Byte x, Byte y) {
        return new Byte((byte) (x.byteValue() ^ y.byteValue()));
    }

    /**
     * For numeric argument x, returns ~x
     * @return the one's complement of the argument
     */

    public Byte not (Byte x) {
        return new Byte((byte) ~(x.byteValue()));
    }

    /**
     * @return x << y
     */

    public Byte shiftLeft(Byte x, Integer y) {
        return new Byte((byte) (x.byteValue() << y.intValue()));
    }        

    /**
     * @return x >> y
     */

    public Byte signedShiftRight(Byte x, Integer y) {
        return new Byte((byte) (x.byteValue() >> y.intValue()));
    }

    /**
     * Optional.
     * @return x >>> y
     */

    public Byte unsignedShiftRight(Byte x, Integer y) {
        return new Byte((byte) (x.byteValue() >>> y.intValue()));
    }
}

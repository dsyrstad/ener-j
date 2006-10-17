// ============================================================================
// $Id: ArithmeticFactory.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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
import java.math.BigInteger;
import java.util.HashMap;

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

public class ArithmeticFactory {
    static private HashMap<Class<? extends Number>,Arithmetic<? extends Number>>
        _arithmeticMap = new HashMap<Class<? extends Number>,Arithmetic<? extends Number>>();
    
    static private HashMap<Class<? extends Number>,IntegerArithmetic<? extends Number>>
        _integralMap = new HashMap<Class<? extends Number>,IntegerArithmetic<? extends Number>>();

    /**
     * Registers the Arithmetic implementation for the given class
     */
    static public <T extends Number> void register (Class<T> c, Arithmetic<T> math) {
        _arithmeticMap.put(c, math);
    }
    
    /**
     * Registers the IntegerArithmetic implementation for the given class
     */
    static public <T extends Number> void register (Class<T> c, IntegerArithmetic<T> math) {
        _arithmeticMap.put(c, math);
        _integralMap.put(c, math);
    }

    /**
     * Returns the Arithmetic implementation registered for the given class
     * @return the Arithmetic implementation registered for the given class
     */
    static public <T extends Number> Arithmetic<T> getArithmetic (Class<T> c) {
        // @SuppressWarnings
        // the map is private, and the register methods ensure that the specific
        // runtime type of each implementation is associated with the correct
        // class.
        return (Arithmetic<T>) _arithmeticMap.get(c);
    }
    
    /**
     * Returns the IntegerArithmetic implementation registered for the given
     * class
     * @return the IntegerArithmetic implementation registered for the given
     * class
     */
    static public <T extends Number> IntegerArithmetic<T> getIntegralArithmetic (Class<T> c) {
        // @SuppressWarnings
        // the map is private, and the register methods ensure that the specific
        // runtime type of each implementation is associated with the correct
        // class.
        return (IntegerArithmetic<T>) _integralMap.get(c);
    }
    
    static {
        register(Byte.class,       new ByteMath());
        register(Short.class,      new ShortMath());
        register(Integer.class,    new IntegerMath());
        register(Long.class,       new LongMath());
        register(Float.class,      new FloatMath());
        register(Double.class,     new DoubleMath());
        register(BigDecimal.class, new DecimalMath());
        register(BigInteger.class, new BigIntMath());
    }
}

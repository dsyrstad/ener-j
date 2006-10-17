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
//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/TypeUtil.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.arithmetic.ValueOf;

/**
 * Static utilities dealing with scalar types.
 * 
 * @version $Id: TypeUtil.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class TypeUtil
{
    /** Key is a Number Class, Value is an Integer indicating rank. */
    private static final Map<Class<?>, Integer> NUMBER_RANK_MAP = new HashMap<Class<?>, Integer>();
    private static final Map<Class<?>, UnaryFunctor> NUMBER_PROMOTION_MAP = new HashMap<Class<?>, UnaryFunctor>();
    
    static {
        NUMBER_RANK_MAP.put(Byte.class,       new Integer(2) ); 
        NUMBER_RANK_MAP.put(Short.class,      new Integer(3) );
        NUMBER_RANK_MAP.put(Integer.class,    new Integer(4) );
        NUMBER_RANK_MAP.put(Long.class,       new Integer(5) );
        NUMBER_RANK_MAP.put(Float.class,      new Integer(6) );
        NUMBER_RANK_MAP.put(Double.class,     new Integer(7) );
        NUMBER_RANK_MAP.put(BigInteger.class, new Integer(8) );
        NUMBER_RANK_MAP.put(BigDecimal.class, new Integer(9) );
        NUMBER_RANK_MAP.put(Number.class,     new Integer(10) );
        
        NUMBER_PROMOTION_MAP.put(Byte.class,       new ValueOf(Byte.class) ); 
        NUMBER_PROMOTION_MAP.put(Short.class,      new ValueOf(Short.class) );
        NUMBER_PROMOTION_MAP.put(Integer.class,    new ValueOf(Integer.class) );
        NUMBER_PROMOTION_MAP.put(Long.class,       new ValueOf(Long.class) );
        NUMBER_PROMOTION_MAP.put(Float.class,      new ValueOf(Float.class) );
        NUMBER_PROMOTION_MAP.put(Double.class,     new ValueOf(Double.class) );
        NUMBER_PROMOTION_MAP.put(BigInteger.class, new ValueOf(BigInteger.class) );
        NUMBER_PROMOTION_MAP.put(BigDecimal.class, new ValueOf(BigDecimal.class) );
    }

    
    //--------------------------------------------------------------------------------
    /**
     * Gets the ordinal type rank of the given Number Class. The rank can be used to determine type promotion.
     * The following Number types are supported:
     * BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short, Number. Number is supported as an
     * abstract type. It has the highest rank.
     * 
     * @param aNumberClass a Class derived from Number, or Number.class itself.  
     * 
     * @return the rank of the Number type. Zero is the lowest rank, positive numbers indicate
     *  higher rank. 
     */
    public static int getRank(Class aNumberClass)
    {
        Integer rank = (Integer)NUMBER_RANK_MAP.get(aNumberClass);
        assert rank != null;
        return rank.intValue();
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets a UnaryFunctor that will promote a numeric value to aNumberClass type.
     *  
     * @param aNumberClass a class derived from Number.
     *  The following Number types are supported:
     *  BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short
     * 
     * @return a UnaryFunctor that will promote a numeric value to the given numeric type.
     *  Resulting functor is wrapped in NullUnary to handle null and undefined types.
     */
    public static UnaryFunctor getNumberPromotionFunctor(Class aNumberClass) 
    {
        UnaryFunctor functor = NUMBER_PROMOTION_MAP.get(aNumberClass);
        assert functor != null;
        return functor;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Determines if the given class is numeric. 
     * 
     * @return true if the given class is derived from Number.
     */
    public static boolean isNumericType(Class aNumberClass)
    {
        return NUMBER_RANK_MAP.get(aNumberClass) != null;
    }

    //--------------------------------------------------------------------------------
    /**
     * Determines if the given class is a string type.
     * 
     * @return true if the given class is String or Character.
     */
    public static boolean isStringType(Class aClass)
    {
        return !isNumericType(aClass) && !isCollectionType(aClass) && !isMapType(aClass);
    }

    //--------------------------------------------------------------------------------
    /**
     * Determines if the given class is a scalar type.
     * 
     * @return true if aClass meets the requirements of isStringType(Class) or isNumericType(Class).
     */
    public static boolean isScalarType(Class aClass)
    {
        return isNumericType(aClass) || isStringType(aClass);
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Determines if the given class is a Collection type.
     * 
     * @return true if aClass is derived from Collection.
     */
    public static boolean isCollectionType(Class aClass)
    {
        return Collection.class.isAssignableFrom(aClass); 
    }

    //--------------------------------------------------------------------------------
    /**
     * Determines if the given class is a Map type.
     * 
     * @return true if aClass is derived from Map.
     */
    public static boolean isMapType(Class aClass)
    {
        return Map.class.isAssignableFrom(aClass); 
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the generic parameterized type for the specified Collection type.
     * 
     * @param aCollectionClass a class derived from Collection.
     * 
     * @return the generic parameterized type, or Object.class if there is no parameterized type.
     */
    public static Class getCollectionGenericType(Class aCollectionClass)
    {
        // Use generic parameter type, if available.
        TypeVariable[] elementTypes = aCollectionClass.getTypeParameters();
        if (elementTypes.length == 1 && elementTypes[0] instanceof ParameterizedType) {
            Type type = ((ParameterizedType)elementTypes[0]).getRawType();
            if (type instanceof Class) {
                return (Class)type;
            }
        }
        
        return Object.class;
    }
}

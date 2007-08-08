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

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.enerj.apache.commons.beanutils.Converter;
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
    // Static initialization of these Maps is at the bottom of the class.
    /** Key is a Number Class, Value is an Integer indicating rank. */
    private static final Map<Class<?>, Integer> NUMBER_RANK_MAP = new HashMap<Class<?>, Integer>();
    private static final Map<Class<?>, UnaryFunctor> NUMBER_PROMOTION_MAP = new HashMap<Class<?>, UnaryFunctor>();
    private static final Map<ClassPair, Converter> CONVERTER_MAP = new HashMap<ClassPair, Converter>();  
    
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
    

    /**
     * Determines if the given class is numeric. 
     * 
     * @return true if the given class is derived from Number.
     */
    public static boolean isNumericType(Class aNumberClass)
    {
        return NUMBER_RANK_MAP.get(aNumberClass) != null;
    }


    /**
     * Determines if the given class is a string type.
     * 
     * @return true if the given class is String or Character.
     */
    public static boolean isStringType(Class aClass)
    {
        return !isNumericType(aClass) && !isCollectionType(aClass) && !isMapType(aClass);
    }


    /**
     * Determines if the given class is a scalar type.
     * 
     * @return true if aClass meets the requirements of isStringType(Class) or isNumericType(Class).
     */
    public static boolean isScalarType(Class aClass)
    {
        return isNumericType(aClass) || isStringType(aClass);
    }
    

    /**
     * Determines if the given class is a Collection type.
     * 
     * @return true if aClass is derived from Collection.
     */
    public static boolean isCollectionType(Class aClass)
    {
        return Collection.class.isAssignableFrom(aClass); 
    }


    /**
     * Determines if the given class is a Map type.
     * 
     * @return true if aClass is derived from Map.
     */
    public static boolean isMapType(Class aClass)
    {
        return Map.class.isAssignableFrom(aClass); 
    }
    

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
    
    /**
     * Convert one or both of the two objects in the given array so that the two objects are Comparable.
     * The objects are converted in place in the array (i.e., the return value is the objects in 
     * the array.
     * 
     * @param objs an array of exactly two objects, neither of which are null. 
     */
    public static void makeComparable(Object[] objs)
    {
        assert objs.length == 2 && objs[0] != null && objs[1] != null;
        
        Object o1 = objs[0];
        Object o2 = objs[1];
        Class o1Class = o1.getClass();
        Class o2Class = o2.getClass();
        if (o1Class == o2Class) {
            return; // Short-cut
        }
        
        boolean reversed = false;

        // Special case for numeric -> numeric conversions. Saves a lot of mapping.
        if (isNumericType(o1Class) && isNumericType(o2Class)) {
            int rank1 = getRank(o1Class);
            int rank2 = getRank(o2Class);
            if (rank1 > rank2) {
                // Swap objects to use same logic.
                Object tmp = o1;
                o1 = o2;
                o2 = tmp;
                Class tmpClass = o1Class;
                o1Class = o2Class;
                o2Class = tmpClass;
                int tmpRank = rank1;
                rank1 = rank2;
                rank2 = tmpRank;
                reversed = true;
            }
            
            o1 = getNumberPromotionFunctor(o2Class).fn(o1);
        }
        else {
        
            // Worse case: Both strings if not in map.
        }
        
        
        if (reversed) {
            objs[0] = o2;
            objs[1] = o1;
        }
        else {
            objs[0] = o1;
            objs[1] = o2;
        }
    }
    
    
    /**
     * A class pair key.
     */
    private static final class ClassPair
    {
        private Class class1;
        private Class class2;
        
        ClassPair(Class class1, Class class2)
        {
            this.class1 = class1;
            this.class2 = class2;
        }

        @Override
        public boolean equals(Object obj)
        {
            ClassPair other = (ClassPair)obj;
            
            return this.class1 == other.class1 && this.class2 == other.class2;
        }

        @Override
        public int hashCode()
        {
            return class1.hashCode() * 31 ^ class2.hashCode();
        }
    }

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
        
        // Boolean  Character   Byte    Short   Integer Long    Float   Double  BigInteger  BigDecimal  java.util.Date  java.sql.Date   Time    Timestamp   Calendar    File    URL String
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Character.class), O2StringToBooleanConverter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Byte.class), BooleanToNumberConverter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Short.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Integer.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Long.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Float.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Double.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, BigInteger.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, BigDecimal.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, java.util.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Boolean.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(Character.class, Byte.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, Short.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, Integer.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, Long.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, Float.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, Double.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, BigInteger.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, BigDecimal.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, java.util.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Character.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(Byte.class, Short.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, Integer.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, Long.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, Float.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, Double.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, BigInteger.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, BigDecimal.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, java.util.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Byte.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(Short.class, Integer.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, Long.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, Float.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, Double.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, BigInteger.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, BigDecimal.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, java.util.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Short.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(Integer.class, Long.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, Float.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, Double.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, BigInteger.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, BigDecimal.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, java.util.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Integer.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(Long.class, Float.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, Double.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, BigLong.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, BigDecimal.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, java.util.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Long.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(Float.class, Double.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Float.class, BigInteger.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Float.class, BigDecimal.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Float.class, java.util.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Float.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Float.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Float.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Float.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Float.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Float.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Float.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(Double.class, BigInteger.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Double.class, BigDecimal.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Double.class, java.util.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Double.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Double.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Double.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Double.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Double.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Double.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Double.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(BigInteger.class, BigDecimal.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigInteger.class, java.util.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigInteger.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigInteger.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigInteger.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigInteger.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigInteger.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigInteger.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigInteger.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(BigDecimal.class, java.util.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigDecimal.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigDecimal.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigDecimal.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigDecimal.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigDecimal.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigDecimal.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(BigDecimal.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(java.util.Date.class, java.sql.Date.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.util.Date.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.util.Date.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.util.Date.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.util.Date.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.util.Date.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.util.Date.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(java.sql.Date.class, Time.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.sql.Date.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.sql.Date.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.sql.Date.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.sql.Date.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(java.sql.Date.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(Time.class, Timestamp.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Time.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Time.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Time.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Time.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(Timestamp.class, Calendar.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Timestamp.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Timestamp.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Timestamp.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(Calendar.class, File.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Calendar.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(Calendar.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(File.class, URL.class), Converter.INSTANCE);
        CONVERTER_MAP.put(new ClassPair(File.class, String.class), Converter.INSTANCE);

        CONVERTER_MAP.put(new ClassPair(URL.class, String.class), Converter.INSTANCE);
    }
}

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

package org.enerj.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.arithmetic.ValueOf;

/**
 * Static utilities dealing with scalar types.
 * 
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class TypeUtil
{
    // Static initialization of these Maps is at the bottom of the class.
    /** Key is a Number Class, Value is an Integer indicating rank. */
    private static final Map<Class<?>, Integer> NUMBER_RANK_MAP = new HashMap<Class<?>, Integer>();
    private static final Map<Class<?>, UnaryFunctor> NUMBER_PROMOTION_MAP = new HashMap<Class<?>, UnaryFunctor>();
    private static final Map<ClassPair, Converter> CONVERTER_MAP = new HashMap<ClassPair, Converter>(256);  
    
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

        // Do at most 2 successive conversions. Some conversions require intermediate steps.
        for (int i = 0; i < 2; i++) {
            Class o1Class = o1.getClass();
            Class o2Class = o2.getClass();
            if (o1Class == o2Class ||
                o1Class.isAssignableFrom(o2Class) ||
                o2Class.isAssignableFrom(o1Class)) {
                break; // Done!
            }
    
            // Special case for numeric -> numeric conversions. Saves a lot of mapping.
            if (isNumericType(o1Class) && isNumericType(o2Class)) {
                int rank1 = getRank(o1Class);
                int rank2 = getRank(o2Class);
                if (rank1 > rank2) {
                    o2 = getNumberPromotionFunctor(o1Class).fn(o2);
                }
                else {
                    o1 = getNumberPromotionFunctor(o2Class).fn(o1);
                }
            }
            else {
                // For purposes of lookup, treat Calendar sub-types as Calendar
                Class o1LookupClass = o1 instanceof Calendar ? Calendar.class : o1Class;
                Class o2LookupClass = o2 instanceof Calendar ? Calendar.class : o2Class;

                Converter converter = CONVERTER_MAP.get( new ClassPair(o1LookupClass, o2LookupClass) );
                if (converter == null) {
                    // Worse case: Both strings if not in map. Then we're done.
                    if (o1 instanceof java.util.Date || o1 instanceof java.util.Calendar) {
                        o1 = DateToStringConverter.INSTANCE.convert(o1Class, o1);
                    }
                    
                    if (o2 instanceof java.util.Date || o2 instanceof java.util.Calendar) {
                        o2 = DateToStringConverter.INSTANCE.convert(o2Class, o2);
                    }

                    o1 = o1.toString();
                    o2 = o2.toString();
                    break;
                }
                
                if (converter.isObject1Conversion) {
                    o1 = converter.convert(o2Class, o1);
                }
                else {
                    o2 = converter.convert(o1Class, o2);
                }
            }
        }
        
        objs[0] = o1;
        objs[1] = o2;
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
    
    abstract private static class Converter
    {
        private boolean isObject1Conversion;

        Converter()
        {
            this(true);
        }

        Converter(boolean isObject1Conversion)
        {
            this.isObject1Conversion = isObject1Conversion;
        }
        
        /**
         * Convert value into an object of type.
         *
         * @param type desired result type.
         * @param value the value to convert. This must never be null.
         */
        abstract Object convert(Class type, Object value);

        /** @return true if conversion is for the first of two objects. */
        boolean isObject1Conversion()
        {
            return isObject1Conversion;
        }

        void setObject1Conversion(boolean isObject1Conversion)
        {
            this.isObject1Conversion = isObject1Conversion;
        }
    }
    
    private static final class StringToBooleanConverter extends Converter
    {
        StringToBooleanConverter() { }
        
        StringToBooleanConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            String str = value.toString();
            if (str.equalsIgnoreCase("true") || 
                str.equalsIgnoreCase("yes") ||
                str.equalsIgnoreCase("t") ||
                str.equalsIgnoreCase("y") ||
                str.equals("1")) {
                return Boolean.TRUE;
            }
            else {
                return Boolean.FALSE;
            }
        }
    }

    private static final class BooleanToIntConverter extends Converter
    {
        BooleanToIntConverter() { }
        
        BooleanToIntConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            return ((Boolean)value) ? (Integer)1 : (Integer)0;
        }
    }

    private static final class CharToIntConverter extends Converter
    {
        CharToIntConverter() { }
        
        CharToIntConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            return (Integer)(int)((Character)value).charValue();
        }
    }

    private static final class NumberToLongConverter extends Converter
    {
        NumberToLongConverter() { }
        
        NumberToLongConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            return ((Number)value).longValue();
        }
    }

    private static final class StringToDoubleConverter extends Converter
    {
        StringToDoubleConverter() { }
        
        StringToDoubleConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            return Double.valueOf(value.toString());
        }
    }

    private static final class StringToBigDecimalConverter extends Converter
    {
        StringToBigDecimalConverter() { }
        
        StringToBigDecimalConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            return new BigDecimal(value.toString());
        }
    }

    private static final class NumberToUtilDateConverter extends Converter
    {
        NumberToUtilDateConverter() { }
        
        NumberToUtilDateConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            return new java.util.Date( ((Number)value).longValue() );
        }
    }

    private static final class NumberToSqlDateConverter extends Converter
    {
        NumberToSqlDateConverter() { }
        
        NumberToSqlDateConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            return new java.sql.Date( ((Number)value).longValue() );
        }
    }

    private static final class NumberToTimeConverter extends Converter
    {
        NumberToTimeConverter() { }
        
        NumberToTimeConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            return new java.sql.Time( ((Number)value).longValue() );
        }
    }

    private static final class NumberToTimestampConverter extends Converter
    {
        NumberToTimestampConverter() { }
        
        NumberToTimestampConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            return new java.sql.Timestamp( ((Number)value).longValue() );
        }
    }

    private static final class NumberToCalendarConverter extends Converter
    {
        NumberToCalendarConverter() { }
        
        NumberToCalendarConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            if (type != GregorianCalendar.class) {
                throw new IllegalArgumentException("Cannot convert to Calendar of type " + type + " from Long");
            }
            
            Calendar cal = new GregorianCalendar();
            cal.setTimeZone( TimeZone.getTimeZone("GMT") );
            cal.setTimeInMillis(((Number)value).longValue());
            return cal;
        }
    }

    private static final class DateToTimestampConverter extends Converter
    {
        DateToTimestampConverter() { }
        
        DateToTimestampConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            return new java.sql.Timestamp( ((java.util.Date)value).getTime() );
        }
    }

    private static final class DateToCalendarConverter extends Converter
    {
        DateToCalendarConverter() { }
        
        DateToCalendarConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            if (type != GregorianCalendar.class) {
                throw new IllegalArgumentException("Cannot convert to Calendar of type " + type + " from Long");
            }
            
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(((Number)value).longValue());
            return cal;
        }
    }

    // Converts a Date or Calendar to a string. TZ is GMT, or that of the calendar.
    private static final class DateToStringConverter extends Converter
    {
        static final DateToStringConverter INSTANCE = new DateToStringConverter(); 
        DateToStringConverter() { }
        
        DateToStringConverter(boolean isObject1Conversion)
        {
            super(isObject1Conversion);
        }
        
        Object convert(Class type, Object value)
        {
            TimeZone tz;
            if (value instanceof Calendar) {
                Calendar cal = (Calendar)value; 
                value = cal.getTime();
                tz = cal.getTimeZone();
            }
            else {
                tz = TimeZone.getTimeZone("GMT");
            }
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            formatter.setTimeZone(tz);
            return formatter.format((java.util.Date)value);
        }
    }

    
    static {
        NUMBER_RANK_MAP.put(Byte.class,       new Integer(2) ); 
        NUMBER_RANK_MAP.put(Short.class,      new Integer(3) );
        NUMBER_RANK_MAP.put(Integer.class,    new Integer(4) );
        NUMBER_RANK_MAP.put(Long.class,       new Integer(5) );
        NUMBER_RANK_MAP.put(BigInteger.class, new Integer(6) );
        NUMBER_RANK_MAP.put(Float.class,      new Integer(7) );
        NUMBER_RANK_MAP.put(Double.class,     new Integer(8) );
        NUMBER_RANK_MAP.put(BigDecimal.class, new Integer(9) );
        NUMBER_RANK_MAP.put(Number.class,     new Integer(10) );
        
        NUMBER_PROMOTION_MAP.put(Byte.class,       new ValueOf(Byte.class) ); 
        NUMBER_PROMOTION_MAP.put(Short.class,      new ValueOf(Short.class) );
        NUMBER_PROMOTION_MAP.put(Integer.class,    new ValueOf(Integer.class) );
        NUMBER_PROMOTION_MAP.put(Long.class,       new ValueOf(Long.class) );
        NUMBER_PROMOTION_MAP.put(BigInteger.class, new ValueOf(BigInteger.class) );
        NUMBER_PROMOTION_MAP.put(Float.class,      new ValueOf(Float.class) );
        NUMBER_PROMOTION_MAP.put(Double.class,     new ValueOf(Double.class) );
        NUMBER_PROMOTION_MAP.put(BigDecimal.class, new ValueOf(BigDecimal.class) );
        
        // Boolean  Character   Byte    Short   Integer Long    Float   Double  BigInteger  BigDecimal  java.util.Date  java.sql.Date   Time    Timestamp   Calendar    File    URL String
        // If Mapping is Number -> Number or mapping would be converting both to strings, it is not in this map.
        // Some conversions convert to an intermediate type which must be passed thru the conversion again.
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Character.class), new StringToBooleanConverter(false));
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Byte.class), new BooleanToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Short.class), new BooleanToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Integer.class), new BooleanToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Long.class), new BooleanToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Float.class), new BooleanToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Boolean.class, Double.class), new BooleanToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Boolean.class, BigInteger.class), new BooleanToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Boolean.class, BigDecimal.class), new BooleanToIntConverter(true));

        CONVERTER_MAP.put(new ClassPair(Character.class, Byte.class), new CharToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Character.class, Short.class), new CharToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Character.class, Integer.class), new CharToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Character.class, Long.class), new CharToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Character.class, Float.class), new CharToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Character.class, Double.class), new CharToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Character.class, BigInteger.class), new CharToIntConverter(true));
        CONVERTER_MAP.put(new ClassPair(Character.class, BigDecimal.class), new CharToIntConverter(true));

        Class[] numberTypes = new Class[] { Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class, BigInteger.class, BigDecimal.class
        };
        
        for (Class numberType : numberTypes) {
            CONVERTER_MAP.put(new ClassPair(numberType, java.util.Date.class), new NumberToUtilDateConverter(true));
            CONVERTER_MAP.put(new ClassPair(numberType, java.sql.Date.class), new NumberToSqlDateConverter(true));
            CONVERTER_MAP.put(new ClassPair(numberType, Time.class), new NumberToTimeConverter(true));
            CONVERTER_MAP.put(new ClassPair(numberType, Timestamp.class), new NumberToTimestampConverter(true));
            CONVERTER_MAP.put(new ClassPair(numberType, Calendar.class), new NumberToCalendarConverter(true));
            CONVERTER_MAP.put(new ClassPair(numberType, String.class), new StringToDoubleConverter(false));
        }

        CONVERTER_MAP.put(new ClassPair(BigDecimal.class, String.class), new StringToBigDecimalConverter(false));

        // java.util.Date/java.sql.Date/Time are already comparable.
        CONVERTER_MAP.put(new ClassPair(java.util.Date.class, Timestamp.class), new DateToTimestampConverter(true));
        CONVERTER_MAP.put(new ClassPair(java.util.Date.class, Calendar.class), new DateToCalendarConverter(true));

        CONVERTER_MAP.put(new ClassPair(java.sql.Date.class, Timestamp.class), new DateToTimestampConverter(true));
        CONVERTER_MAP.put(new ClassPair(java.sql.Date.class, Calendar.class), new DateToCalendarConverter(true));

        CONVERTER_MAP.put(new ClassPair(Time.class, Timestamp.class), new DateToTimestampConverter(true));
        CONVERTER_MAP.put(new ClassPair(Time.class, Calendar.class), new DateToCalendarConverter(true));

        CONVERTER_MAP.put(new ClassPair(Timestamp.class, Calendar.class), new DateToCalendarConverter(true));
        
        // Create reverse mapping. We have to make a copy of the map entries because we'll be modifying the map.
        Set<Map.Entry<ClassPair, Converter>> entries = new HashSet<Map.Entry<ClassPair,Converter>>(256);
        entries.addAll(CONVERTER_MAP.entrySet());
        for (Map.Entry<ClassPair, Converter> entry : entries) {
            ClassPair currPair = entry.getKey();
            Converter currConverter = entry.getValue();
            try {
                Converter newConverter = currConverter.getClass().newInstance();
                // Reverse the sense of conversion.
                newConverter.setObject1Conversion( !currConverter.isObject1Conversion );
                CONVERTER_MAP.put(new ClassPair(currPair.class2, currPair.class1), newConverter);
            }
            catch (Exception e) {
                throw new RuntimeException("Error initializing CONVERTER_MAP", e);
            }
        }
    }
}

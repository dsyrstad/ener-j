/*******************************************************************************
 * Copyright 2000, 2007 Visual Systems Corporation.
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

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import junit.framework.TestCase;


/**
 * Tests static utilities dealing with scalar types.
 * 
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class TypeUtilTest extends TestCase
{

    /**
     * Construct a new TypeUtilTest.
     *
     * @param name
     */
    public TypeUtilTest(String name)
    {
        super(name);
    }

    /**
     * Test method for {@link org.enerj.util.TypeUtil#getRank(java.lang.Class)}.
     */
    public void testGetRank()
    {
        int[] ranks = {
            // These are in order by rank
            TypeUtil.getRank(Byte.class), 
            TypeUtil.getRank(Short.class),
            TypeUtil.getRank(Integer.class),
            TypeUtil.getRank(Long.class),
            TypeUtil.getRank(BigInteger.class),
            TypeUtil.getRank(Float.class),
            TypeUtil.getRank(Double.class),
            TypeUtil.getRank(BigDecimal.class),
            TypeUtil.getRank(Number.class)
        };

        for (int i = 1; i < ranks.length; i++) {
            assertTrue("Rank[" + (i-1) + "] should be < Rank[" + i + "]", ranks[i-1] < ranks[i]);
        }
    }

    /**
     * Test method for {@link org.enerj.util.TypeUtil#getNumberPromotionFunctor(java.lang.Class)}.
     */
    public void testGetNumberPromotionFunctor()
    {
        {
            Byte value = (Byte)TypeUtil.getNumberPromotionFunctor(Byte.class).fn(Integer.valueOf(5));
            assertEquals(Byte.valueOf((byte)5), value);
        }
        {
            Short value = (Short)TypeUtil.getNumberPromotionFunctor(Short.class).fn(Integer.valueOf(5));
            assertEquals(Short.valueOf((short)5), value);
        }
        {
            Integer value = (Integer)TypeUtil.getNumberPromotionFunctor(Integer.class).fn(Long.valueOf(5));
            assertEquals(Integer.valueOf(5), value);
        }
        {
            Long value = (Long)TypeUtil.getNumberPromotionFunctor(Long.class).fn(Integer.valueOf(5));
            assertEquals(Long.valueOf(5), value);
        }
        {
            Float value = (Float)TypeUtil.getNumberPromotionFunctor(Float.class).fn(Double.valueOf(5.1));
            assertEquals(Float.valueOf(5.1F), value);
        }
        {
            Double value = (Double)TypeUtil.getNumberPromotionFunctor(Double.class).fn(Integer.valueOf(5));
            assertEquals(Double.valueOf(5.0), value);
        }
        {
            BigInteger value = (BigInteger)TypeUtil.getNumberPromotionFunctor(BigInteger.class).fn(Integer.valueOf(5));
            assertEquals(BigInteger.valueOf(5), value);
        }
        {
            BigDecimal value = (BigDecimal)TypeUtil.getNumberPromotionFunctor(BigDecimal.class).fn(Double.valueOf(5.1));
            assertEquals(BigDecimal.valueOf(5.1), value);
        }
    }

    /**
     * Test method for {@link org.enerj.util.TypeUtil#isNumericType(java.lang.Class)}.
     */
    public void testIsNumericType()
    {
        assertTrue( TypeUtil.isNumericType(Double.class) );
        assertFalse( TypeUtil.isNumericType(String.class) );
    }

    /**
     * Test method for {@link org.enerj.util.TypeUtil#isStringType(java.lang.Class)}.
     */
    public void testIsStringType()
    {
        assertFalse( TypeUtil.isStringType(Double.class) );
        assertTrue( TypeUtil.isStringType(String.class) );
    }

    /**
     * Test method for {@link org.enerj.util.TypeUtil#isScalarType(java.lang.Class)}.
     */
    public void testIsScalarType()
    {
        assertTrue( TypeUtil.isScalarType(Double.class) );
        assertTrue( TypeUtil.isScalarType(String.class) );
        assertFalse( TypeUtil.isScalarType(HashMap.class) );
    }

    /**
     * Test method for {@link org.enerj.util.TypeUtil#isCollectionType(java.lang.Class)}.
     */
    public void testIsCollectionType()
    {
        assertFalse( TypeUtil.isCollectionType(Double.class) );
        assertFalse( TypeUtil.isCollectionType(String.class) );
        assertTrue( TypeUtil.isCollectionType(ArrayList.class) );
    }

    /**
     * Test method for {@link org.enerj.util.TypeUtil#isMapType(java.lang.Class)}.
     */
    public void testIsMapType()
    {
        assertFalse( TypeUtil.isMapType(Double.class) );
        assertFalse( TypeUtil.isMapType(String.class) );
        assertFalse( TypeUtil.isMapType(ArrayList.class) );
        assertTrue( TypeUtil.isMapType(HashMap.class) );
    }

    /**
     * Call makeComparable and then make sure that we can compare the result.
     * 
     * @param o1 the smaller value
     * @param o2 the larger value
     * @param expectedO1 the expected smaller value
     * @param expectedO2 the expected larger value
     * @param expectedResult the expected comparison result.
     */
    private void checkMakeComparable(Object o1, Object o2, Object expectedO1, Object expectedO2, int expectedResult)
    {
        Object[] objs = new Object[] { o1, o2 };
        TypeUtil.makeComparable(objs);
        
        assertEquals("Expected type does not match.", expectedO1.getClass(), objs[0].getClass()); 
        assertEquals("Expected type does not match.", expectedO2.getClass(), objs[1].getClass());
        
        assertEquals("Expected value does not match.", expectedO1, objs[0]); 
        assertEquals("Expected value does not match.", expectedO2, objs[1]);
        
        int result = ((Comparable)objs[0]).compareTo(objs[1]);
        assertTrue("Result not correct. Result: " + result + 
            ". o1=" + objs[0] + ", o2=" + objs[1] + ". Result types o1=" + objs[0].getClass() +
            " o2=" + objs[1].getClass() + ". Input types: o1=" + o1.getClass() + " o2=" + o2.getClass(), 
            expectedResult == result ||
            (expectedResult < 0 && result < 0) ||
            (expectedResult > 0 && result > 0) );
        
        // Test reverse
        result = ((Comparable)objs[1]).compareTo(objs[0]);
        assertTrue("Reverse result not correct. Result: " + result + 
            ". o1=" + objs[0] + ", o2=" + objs[1] + ". Result types o1=" + objs[0].getClass() +
            " o2=" + objs[1].getClass() + ". Input types: o1=" + o1.getClass() + " o2=" + o2.getClass(), 
            (expectedResult == 0 && result == 0) ||
            (expectedResult < 0 && result > 0) ||   /* Sense of result reversed */
            (expectedResult > 0 && result < 0) );
    }
    
    private Calendar createCalendar(long millis)
    {
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone( TimeZone.getTimeZone("GMT") );
        cal.setTimeInMillis(millis);
        return cal;
    }
    
    /**
     * Test method for {@link org.enerj.util.TypeUtil#makeComparable(java.lang.Object[])}.
     */
    public void testMakeComparable() throws Exception
    {
        // Test all combinations of Numbers.
        checkMakeComparable(Byte.valueOf((byte)5), Byte.valueOf((byte)6), Byte.valueOf((byte)5), Byte.valueOf((byte)6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), Short.valueOf((short)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), Float.valueOf(5.1F), Float.valueOf(5), Float.valueOf(5.1F), -1);
        checkMakeComparable(Byte.valueOf((byte)5), Double.valueOf(6.1), Double.valueOf(5), Double.valueOf(6.1), -1);
        checkMakeComparable(Byte.valueOf((byte)5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), BigDecimal.valueOf(5.1), BigDecimal.valueOf(5), BigDecimal.valueOf(5.1), -1);
        checkMakeComparable(Short.valueOf((short)5), Byte.valueOf((byte)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(Short.valueOf((short)5), Short.valueOf((short)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(Short.valueOf((short)5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Short.valueOf((short)5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Short.valueOf((short)5), Float.valueOf(5.1F), Float.valueOf(5), Float.valueOf(5.1F), -1);
        checkMakeComparable(Short.valueOf((short)5), Double.valueOf(5.1), Double.valueOf(5), Double.valueOf(5.1), -1);
        checkMakeComparable(Short.valueOf((short)5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(Short.valueOf((short)5), BigDecimal.valueOf(5.1), BigDecimal.valueOf(5), BigDecimal.valueOf(5.1), -1);
        checkMakeComparable(Integer.valueOf(5), Byte.valueOf((byte)6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), Short.valueOf((short)6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), Float.valueOf(5.1F), Float.valueOf(5.0F), Float.valueOf(5.1F), -1);
        checkMakeComparable(Integer.valueOf(5), Double.valueOf(5.1), Double.valueOf(5.0), Double.valueOf(5.1), -1);
        checkMakeComparable(Integer.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), BigDecimal.valueOf(5.1), BigDecimal.valueOf(5), BigDecimal.valueOf(5.1), -1);
        checkMakeComparable(Long.valueOf(5), Byte.valueOf((byte)6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), Short.valueOf((short)6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), Integer.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), Float.valueOf(5.1F), Float.valueOf(5.0F), Float.valueOf(5.1F), -1);
        checkMakeComparable(Long.valueOf(5), Double.valueOf(5.1), Double.valueOf(5.0), Double.valueOf(5.1), -1);
        checkMakeComparable(Long.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), BigDecimal.valueOf(5.1), BigDecimal.valueOf(5), BigDecimal.valueOf(5.1), -1);
        checkMakeComparable(Float.valueOf(5.0F), Byte.valueOf((byte)6), Float.valueOf(5.0F), Float.valueOf(6), -1);
        checkMakeComparable(Float.valueOf(5.0F), Short.valueOf((short)6), Float.valueOf(5.0F), Float.valueOf(6), -1);
        checkMakeComparable(Float.valueOf(5.0F), Integer.valueOf(6), Float.valueOf(5.0F), Float.valueOf(6), -1);
        checkMakeComparable(Float.valueOf(5.0F), Long.valueOf(6), Float.valueOf(5.0F), Float.valueOf(6), -1);
        checkMakeComparable(Float.valueOf(5.0F), Float.valueOf(5.1F), Float.valueOf(5.0F), Float.valueOf(5.1F), -1);
        checkMakeComparable(Float.valueOf(5.0F), Double.valueOf(5.1), Double.valueOf(5.0), Double.valueOf(5.1), -1);
        checkMakeComparable(Float.valueOf(5.0F), BigInteger.valueOf(6), Float.valueOf(5.0F), Float.valueOf(6.0F), -1);
        checkMakeComparable(Float.valueOf(5.0F), BigDecimal.valueOf(5.1), BigDecimal.valueOf(5.0), BigDecimal.valueOf(5.1), -1);
        checkMakeComparable(Double.valueOf(5.0), Byte.valueOf((byte)6), Double.valueOf(5.0), Double.valueOf(6.0), -1);
        checkMakeComparable(Double.valueOf(5.0), Short.valueOf((short)6), Double.valueOf(5.0), Double.valueOf(6.0), -1);
        checkMakeComparable(Double.valueOf(5.0), Integer.valueOf(6), Double.valueOf(5.0), Double.valueOf(6.0), -1);
        checkMakeComparable(Double.valueOf(5.0), Long.valueOf(6), Double.valueOf(5.0), Double.valueOf(6.0), -1);
        checkMakeComparable(Double.valueOf(5.0), Float.valueOf(6.0F), Double.valueOf(5.0), Double.valueOf(6.0), -1);
        checkMakeComparable(Double.valueOf(5.0), Double.valueOf(5.1), Double.valueOf(5.0), Double.valueOf(5.1), -1);
        checkMakeComparable(Double.valueOf(5.0), BigInteger.valueOf(6), Double.valueOf(5.0), Double.valueOf(6.0), -1);
        checkMakeComparable(Double.valueOf(5.0), BigDecimal.valueOf(5.1), BigDecimal.valueOf(5.0), BigDecimal.valueOf(5.1), -1);
        checkMakeComparable(BigInteger.valueOf(5), Byte.valueOf((byte)6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), Short.valueOf((short)6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), Integer.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), Long.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), Float.valueOf(5.1F), Float.valueOf(5.0F), Float.valueOf(5.1F), -1);
        checkMakeComparable(BigInteger.valueOf(5), Double.valueOf(5.1), Double.valueOf(5.0), Double.valueOf(5.1), -1);
        checkMakeComparable(BigInteger.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), BigDecimal.valueOf(5.1), BigDecimal.valueOf(5), BigDecimal.valueOf(5.1), -1);
        checkMakeComparable(BigDecimal.valueOf(5.0), Byte.valueOf((byte)6), BigDecimal.valueOf(5.0), BigDecimal.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5.0), Short.valueOf((short)6), BigDecimal.valueOf(5.0), BigDecimal.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5.0), Integer.valueOf(6), BigDecimal.valueOf(5.0), BigDecimal.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5.0), Long.valueOf(6), BigDecimal.valueOf(5.0), BigDecimal.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5.0), Float.valueOf(5.1F), BigDecimal.valueOf(5.0), BigDecimal.valueOf(5.1), -1);
        checkMakeComparable(BigDecimal.valueOf(5.0), Double.valueOf(5.1), BigDecimal.valueOf(5.0), BigDecimal.valueOf(5.1), -1);
        checkMakeComparable(BigDecimal.valueOf(5.0), BigInteger.valueOf(6), BigDecimal.valueOf(5.0), BigDecimal.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5.0), BigDecimal.valueOf(5.1), BigDecimal.valueOf(5.0), BigDecimal.valueOf(5.1), -1);
        
        // Test combinations of all other types.
        checkMakeComparable(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, -1);
        checkMakeComparable(Boolean.FALSE, Character.valueOf('Y'), Boolean.FALSE, Boolean.TRUE, -1);
        checkMakeComparable(Boolean.FALSE, Byte.valueOf((byte)10), Integer.valueOf(0), Integer.valueOf(10), -1);
        checkMakeComparable(Boolean.FALSE, Short.valueOf((short)10), Integer.valueOf(0), Integer.valueOf(10), -1);
        checkMakeComparable(Boolean.FALSE, Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(10), -1);
        checkMakeComparable(Boolean.FALSE, Long.valueOf(10), Long.valueOf(0), Long.valueOf(10), -1);
        checkMakeComparable(Boolean.FALSE, BigInteger.valueOf(10), BigInteger.valueOf(0), BigInteger.valueOf(10), -1);
        checkMakeComparable(Boolean.FALSE, Float.valueOf(10.0F), Float.valueOf(0), Float.valueOf(10.0F), -1);
        checkMakeComparable(Boolean.FALSE, Double.valueOf(10.0), Double.valueOf(0.), Double.valueOf(10.0), -1);
        checkMakeComparable(Boolean.FALSE, BigDecimal.valueOf(10.0), BigDecimal.valueOf(0), BigDecimal.valueOf(10.0), -1);
        checkMakeComparable(Boolean.FALSE, new java.util.Date(10L), "false", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(Boolean.FALSE, new java.sql.Date(10L),  "false", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(Boolean.FALSE, new Time(10L),  "false", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(Boolean.FALSE, new Timestamp(10L), "false", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(Boolean.FALSE, createCalendar(10L),  "false", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(Boolean.FALSE, new File("AAA"), "false", "AAA", 1);
        checkMakeComparable(Boolean.FALSE, "string", "false", "string", -1);
        
        checkMakeComparable(Character.valueOf('X'), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, -1);
        checkMakeComparable(Character.valueOf('A'), Character.valueOf('B'), Character.valueOf('A'), Character.valueOf('B'), -1);
        checkMakeComparable(Character.valueOf('A'), Byte.valueOf((byte)10), Integer.valueOf(65), Integer.valueOf(10), 1);
        checkMakeComparable(Character.valueOf('A'), Short.valueOf((short)10), Integer.valueOf(65), Integer.valueOf(10), 1);
        checkMakeComparable(Character.valueOf('A'), Integer.valueOf(10), Integer.valueOf(65), Integer.valueOf(10), 1);
        checkMakeComparable(Character.valueOf('A'), Long.valueOf(10), Long.valueOf(65), Long.valueOf(10), 1);
        checkMakeComparable(Character.valueOf('A'), BigInteger.valueOf(10), BigInteger.valueOf(65), BigInteger.valueOf(10), 1);
        checkMakeComparable(Character.valueOf('A'), Float.valueOf(10.0F), Float.valueOf(65), Float.valueOf(10), 1);
        checkMakeComparable(Character.valueOf('A'), Double.valueOf(10.0), Double.valueOf(65), Double.valueOf(10), 1);
        checkMakeComparable(Character.valueOf('A'), BigDecimal.valueOf(10.0), BigDecimal.valueOf(65), BigDecimal.valueOf(10.0), 1);
        checkMakeComparable(Character.valueOf('A'), new java.util.Date(10L), "A", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(Character.valueOf('A'), new java.sql.Date(10L), "A", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(Character.valueOf('A'), new Time(10L), "A", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(Character.valueOf('A'), new Timestamp(10L), "A", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(Character.valueOf('A'), createCalendar(10L), "A", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(Character.valueOf('A'), new File("AAA"), "A", "AAA", -1);
        checkMakeComparable(Character.valueOf('A'), "string", "A", "string", -1);

        checkMakeComparable(Byte.valueOf((byte)5), Boolean.TRUE, Integer.valueOf(5), Integer.valueOf(1), 1);
        checkMakeComparable(Byte.valueOf((byte)5), Character.valueOf('B'), Integer.valueOf(5), Integer.valueOf(66), -1);
        checkMakeComparable(Byte.valueOf((byte)5), new java.util.Date(10L), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(Byte.valueOf((byte)5), new java.sql.Date(10L), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(Byte.valueOf((byte)5), new Time(10L), new Time(5), new Time(10), -1);
        checkMakeComparable(Byte.valueOf((byte)5), new Timestamp(10L), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(Byte.valueOf((byte)5), createCalendar(10L), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(Byte.valueOf((byte)5), new File("AAA"), "5", "AAA", -1);
        checkMakeComparable(Byte.valueOf((byte)5), "10.1", Double.valueOf(5), Double.valueOf(10.1), -1);
        
        checkMakeComparable(Short.valueOf((short)5), Boolean.TRUE, Integer.valueOf(5), Integer.valueOf(1), 1);
        checkMakeComparable(Short.valueOf((short)5), Character.valueOf('B'), Integer.valueOf(5), Integer.valueOf(66), -1);
        checkMakeComparable(Short.valueOf((short)5), new java.util.Date(10L), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(Short.valueOf((short)5), new java.sql.Date(10L), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(Short.valueOf((short)5), new Time(10L), new Time(5), new Time(10), -1);
        checkMakeComparable(Short.valueOf((short)5), new Timestamp(10L), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(Short.valueOf((short)5), createCalendar(10L), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(Short.valueOf((short)5), new File("AAA"), "5", "AAA", -1);
        checkMakeComparable(Short.valueOf((short)5), "10.1", Double.valueOf(5), Double.valueOf(10.1), -1);
        
        checkMakeComparable(Integer.valueOf(5), Boolean.TRUE, Integer.valueOf(5), Integer.valueOf(1), 1);
        checkMakeComparable(Integer.valueOf(5), Character.valueOf('B'), Integer.valueOf(5), Integer.valueOf(66), -1);
        checkMakeComparable(Integer.valueOf(5), new java.util.Date(10L), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(Integer.valueOf(5), new java.sql.Date(10L), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(Integer.valueOf(5), new Time(10L), new Time(5), new Time(10), -1);
        checkMakeComparable(Integer.valueOf(5), new Timestamp(10L), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(Integer.valueOf(5), createCalendar(10L), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(Integer.valueOf(5), new File("AAA"), "5", "AAA", -1);
        checkMakeComparable(Integer.valueOf(5), "10.1", Double.valueOf(5), Double.valueOf(10.1), -1);
        
        checkMakeComparable(Long.valueOf(5), Boolean.TRUE, Long.valueOf(5), Long.valueOf(1), 1);
        checkMakeComparable(Long.valueOf(5), Character.valueOf('B'), Long.valueOf(5), Long.valueOf(66), -1);
        checkMakeComparable(Long.valueOf(5), new java.util.Date(10L), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(Long.valueOf(5), new java.sql.Date(10L), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(Long.valueOf(5), new Time(10L), new Time(5), new Time(10), -1);
        checkMakeComparable(Long.valueOf(5), new Timestamp(10L), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(Long.valueOf(5), createCalendar(10L), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(Long.valueOf(5), new File("AAA"), "5", "AAA", -1);
        checkMakeComparable(Long.valueOf(5), "10.1", Double.valueOf(5), Double.valueOf(10.1), -1);
        
        checkMakeComparable(BigInteger.valueOf(5), Boolean.TRUE, BigInteger.valueOf(5), BigInteger.valueOf(1), 1);
        checkMakeComparable(BigInteger.valueOf(5), Character.valueOf('B'), BigInteger.valueOf(5), BigInteger.valueOf(66), -1);
        checkMakeComparable(BigInteger.valueOf(5), new java.util.Date(10L), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(BigInteger.valueOf(5), new java.sql.Date(10L), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(BigInteger.valueOf(5), new Time(10L), new Time(5), new Time(10), -1);
        checkMakeComparable(BigInteger.valueOf(5), new Timestamp(10L), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(BigInteger.valueOf(5), createCalendar(10L), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(BigInteger.valueOf(5), new File("AAA"), "5", "AAA", -1);
        checkMakeComparable(BigInteger.valueOf(5), "10.1", Double.valueOf(5), Double.valueOf(10.1), -1);
        
        checkMakeComparable(Float.valueOf(5), Boolean.TRUE, Float.valueOf(5), Float.valueOf(1), 1);
        checkMakeComparable(Float.valueOf(5), Character.valueOf('B'), Float.valueOf(5), Float.valueOf(66), -1);
        checkMakeComparable(Float.valueOf(5), new java.util.Date(10L), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(Float.valueOf(5), new java.sql.Date(10L), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(Float.valueOf(5), new Time(10L), new Time(5), new Time(10), -1);
        checkMakeComparable(Float.valueOf(5), new Timestamp(10L), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(Float.valueOf(5), createCalendar(10L), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(Float.valueOf(5), new File("AAA"), "5.0", "AAA", -1);
        checkMakeComparable(Float.valueOf(5), "10.1", Double.valueOf(5), Double.valueOf(10.1), -1);
        
        checkMakeComparable(Double.valueOf(5), Boolean.TRUE, Double.valueOf(5), Double.valueOf(1), 1);
        checkMakeComparable(Double.valueOf(5), Character.valueOf('B'), Double.valueOf(5), Double.valueOf(66), -1);
        checkMakeComparable(Double.valueOf(5), new java.util.Date(10L), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(Double.valueOf(5), new java.sql.Date(10L), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(Double.valueOf(5), new Time(10L), new Time(5), new Time(10), -1);
        checkMakeComparable(Double.valueOf(5), new Timestamp(10L), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(Double.valueOf(5), createCalendar(10L), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(Double.valueOf(5), new File("AAA"), "5.0", "AAA", -1);
        checkMakeComparable(Double.valueOf(5), "10.1", Double.valueOf(5), Double.valueOf(10.1), -1);
        
        checkMakeComparable(BigDecimal.valueOf(5), Boolean.TRUE, BigDecimal.valueOf(5), BigDecimal.valueOf(1), 1);
        checkMakeComparable(BigDecimal.valueOf(5), Character.valueOf('B'), BigDecimal.valueOf(5), BigDecimal.valueOf(66), -1);
        checkMakeComparable(BigDecimal.valueOf(5), new java.util.Date(10L), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(BigDecimal.valueOf(5), new java.sql.Date(10L), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(BigDecimal.valueOf(5), new Time(10L), new Time(5), new Time(10), -1);
        checkMakeComparable(BigDecimal.valueOf(5), new Timestamp(10L), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(BigDecimal.valueOf(5), createCalendar(10L), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(BigDecimal.valueOf(5), new File("AAA"), "5", "AAA", -1);
        checkMakeComparable(BigDecimal.valueOf(5), "10.1", BigDecimal.valueOf(5), BigDecimal.valueOf(10.1), -1);
        
        checkMakeComparable(new java.util.Date(5L), Boolean.TRUE, "1970-01-01 00:00:00.005", "true", -1);
        checkMakeComparable(new java.util.Date(5L), Character.valueOf('B'), "1970-01-01 00:00:00.005", "B", -1);
        checkMakeComparable(new java.util.Date(5L), Byte.valueOf((byte)10), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(new java.util.Date(5L), Short.valueOf((short)10), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(new java.util.Date(5L), Integer.valueOf(10), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(new java.util.Date(5L), Long.valueOf(10), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(new java.util.Date(5L), BigInteger.valueOf(10), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(new java.util.Date(5L), Float.valueOf(10.1F), new java.util.Date(5), new java.util.Date(10), -1);
        checkMakeComparable(new java.util.Date(5L), Double.valueOf(10.1), new java.util.Date(5L), new java.util.Date(10L), -1);
        checkMakeComparable(new java.util.Date(5L), BigDecimal.valueOf(10.1), new java.util.Date(5L), new java.util.Date(10L), -1);
        checkMakeComparable(new java.util.Date(5L), new java.util.Date(10L), new java.util.Date(5L), new java.util.Date(10L), -1);
        checkMakeComparable(new java.util.Date(5L), new java.sql.Date(10L), new java.util.Date(5L), new java.sql.Date(10L), -1);
        checkMakeComparable(new java.util.Date(5L), new Time(10L), new java.util.Date(5L), new Time(10L), -1);
        checkMakeComparable(new java.util.Date(5L), new Timestamp(10L), new Timestamp(5L), new Timestamp(10L), -1);
        checkMakeComparable(new java.util.Date(5L), createCalendar(10L), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(new java.util.Date(5L), new File("AAA"), "1970-01-01 00:00:00.005", "AAA", -1);
        checkMakeComparable(new java.util.Date(5L), "string", "1970-01-01 00:00:00.005", "string", -1);
        
        checkMakeComparable(new java.sql.Date(5L), Boolean.TRUE, "1970-01-01 00:00:00.005", "true", -1);
        checkMakeComparable(new java.sql.Date(5L), Character.valueOf('B'), "1970-01-01 00:00:00.005", "B", -1);
        checkMakeComparable(new java.sql.Date(5L), Byte.valueOf((byte)10), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(new java.sql.Date(5L), Short.valueOf((short)10), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(new java.sql.Date(5L), Integer.valueOf(10), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(new java.sql.Date(5L), Long.valueOf(10), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(new java.sql.Date(5L), BigInteger.valueOf(10), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(new java.sql.Date(5L), Float.valueOf(10.1F), new java.sql.Date(5), new java.sql.Date(10), -1);
        checkMakeComparable(new java.sql.Date(5L), Double.valueOf(10.1), new java.sql.Date(5L), new java.sql.Date(10L), -1);
        checkMakeComparable(new java.sql.Date(5L), BigDecimal.valueOf(10.1), new java.sql.Date(5L), new java.sql.Date(10L), -1);
        checkMakeComparable(new java.sql.Date(5L), new java.sql.Date(10L), new java.sql.Date(5L), new java.sql.Date(10L), -1);
        checkMakeComparable(new java.sql.Date(5L), new java.sql.Date(10L), new java.sql.Date(5L), new java.sql.Date(10L), -1);
        checkMakeComparable(new java.sql.Date(5L), new Time(10L), new java.sql.Date(5L), new Time(10L), -1);
        checkMakeComparable(new java.sql.Date(5L), new Timestamp(10L), new Timestamp(5L), new Timestamp(10L), -1);
        checkMakeComparable(new java.sql.Date(5L), createCalendar(10L), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(new java.sql.Date(5L), new File("AAA"), "1970-01-01 00:00:00.005", "AAA", -1);
        checkMakeComparable(new java.sql.Date(5L), "string", "1970-01-01 00:00:00.005", "string", -1);
        
        checkMakeComparable(new Time(5L), Boolean.TRUE, "1970-01-01 00:00:00.005", "true", -1);
        checkMakeComparable(new Time(5L), Character.valueOf('B'), "1970-01-01 00:00:00.005", "B", -1);
        checkMakeComparable(new Time(5L), Byte.valueOf((byte)10), new Time(5), new Time(10), -1);
        checkMakeComparable(new Time(5L), Short.valueOf((short)10), new Time(5), new Time(10), -1);
        checkMakeComparable(new Time(5L), Integer.valueOf(10), new Time(5), new Time(10), -1);
        checkMakeComparable(new Time(5L), Long.valueOf(10), new Time(5), new Time(10), -1);
        checkMakeComparable(new Time(5L), BigInteger.valueOf(10), new Time(5), new Time(10), -1);
        checkMakeComparable(new Time(5L), Float.valueOf(10.1F), new Time(5), new Time(10), -1);
        checkMakeComparable(new Time(5L), Double.valueOf(10.1), new Time(5L), new Time(10L), -1);
        checkMakeComparable(new Time(5L), BigDecimal.valueOf(10.1), new Time(5L), new Time(10L), -1);
        checkMakeComparable(new Time(5L), new Time(10L), new Time(5L), new Time(10L), -1);
        checkMakeComparable(new Time(5L), new Time(10L), new Time(5L), new Time(10L), -1);
        checkMakeComparable(new Time(5L), new Time(10L), new Time(5L), new Time(10L), -1);
        checkMakeComparable(new Time(5L), new Timestamp(10L), new Timestamp(5L), new Timestamp(10L), -1);
        checkMakeComparable(new Time(5L), createCalendar(10L), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(new Time(5L), new File("AAA"), "1970-01-01 00:00:00.005", "AAA", -1);
        checkMakeComparable(new Time(5L), "string", "1970-01-01 00:00:00.005", "string", -1);

        checkMakeComparable(new Timestamp(5L), Boolean.TRUE, "1970-01-01 00:00:00.005", "true", -1);
        checkMakeComparable(new Timestamp(5L), Character.valueOf('B'), "1970-01-01 00:00:00.005", "B", -1);
        checkMakeComparable(new Timestamp(5L), Byte.valueOf((byte)10), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(new Timestamp(5L), Short.valueOf((short)10), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(new Timestamp(5L), Integer.valueOf(10), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(new Timestamp(5L), Long.valueOf(10), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(new Timestamp(5L), BigInteger.valueOf(10), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(new Timestamp(5L), Float.valueOf(10.1F), new Timestamp(5), new Timestamp(10), -1);
        checkMakeComparable(new Timestamp(5L), Double.valueOf(10.1), new Timestamp(5L), new Timestamp(10L), -1);
        checkMakeComparable(new Timestamp(5L), BigDecimal.valueOf(10.1), new Timestamp(5L), new Timestamp(10L), -1);
        checkMakeComparable(new Timestamp(5L), new Timestamp(10L), new Timestamp(5L), new Timestamp(10L), -1);
        checkMakeComparable(new Timestamp(5L), new Timestamp(10L), new Timestamp(5L), new Timestamp(10L), -1);
        checkMakeComparable(new Timestamp(5L), new Time(10L), new Timestamp(5L), new Timestamp(10L), -1);
        checkMakeComparable(new Timestamp(5L), new Timestamp(10L), new Timestamp(5L), new Timestamp(10L), -1);
        checkMakeComparable(new Timestamp(5L), createCalendar(10L), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(new Timestamp(5L), new File("AAA"), "1970-01-01 00:00:00.005", "AAA", -1);
        checkMakeComparable(new Timestamp(5L), "string", "1970-01-01 00:00:00.005", "string", -1);
        
        checkMakeComparable(createCalendar(5L), Boolean.TRUE, "1970-01-01 00:00:00.005", "true", -1);
        checkMakeComparable(createCalendar(5L), Character.valueOf('B'), "1970-01-01 00:00:00.005", "B", -1);
        checkMakeComparable(createCalendar(5L), Byte.valueOf((byte)10), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(createCalendar(5L), Short.valueOf((short)10), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(createCalendar(5L), Integer.valueOf(10), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(createCalendar(5L), Long.valueOf(10), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(createCalendar(5L), BigInteger.valueOf(10), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(createCalendar(5L), Float.valueOf(10.1F), createCalendar(5), createCalendar(10), -1);
        checkMakeComparable(createCalendar(5L), Double.valueOf(10.1), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(createCalendar(5L), BigDecimal.valueOf(10.1), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(createCalendar(5L), createCalendar(10L), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(createCalendar(5L), createCalendar(10L), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(createCalendar(5L), new Time(10L), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(createCalendar(5L), new Timestamp(10L), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(createCalendar(5L), createCalendar(10L), createCalendar(5L), createCalendar(10L), -1);
        checkMakeComparable(createCalendar(5L), new File("AAA"), "1970-01-01 00:00:00.005", "AAA", -1);
        checkMakeComparable(createCalendar(5L), "string", "1970-01-01 00:00:00.005", "string", -1);

        checkMakeComparable(new File("AAA"), Boolean.TRUE, "AAA", "true", -1);
        checkMakeComparable(new File("AAA"), Character.valueOf('B'), "AAA", "B", -1);
        checkMakeComparable(new File("AAA"), Byte.valueOf((byte)10), "AAA", "10", 1);
        checkMakeComparable(new File("AAA"), Short.valueOf((short)10), "AAA", "10", 1);
        checkMakeComparable(new File("AAA"), Integer.valueOf(10), "AAA", "10", 1);
        checkMakeComparable(new File("AAA"), Long.valueOf(10), "AAA", "10", 1);
        checkMakeComparable(new File("AAA"), BigInteger.valueOf(10), "AAA", "10", 1);
        checkMakeComparable(new File("AAA"), Float.valueOf(10.0F), "AAA", "10.0", 1);
        checkMakeComparable(new File("AAA"), Double.valueOf(10.0), "AAA", "10.0", 1);
        checkMakeComparable(new File("AAA"), BigDecimal.valueOf(10.0), "AAA", "10.0", 1);
        checkMakeComparable(new File("AAA"), new java.util.Date(10L), "AAA", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(new File("AAA"), new java.sql.Date(10L), "AAA", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(new File("AAA"), new Time(10L), "AAA", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(new File("AAA"), new Timestamp(10L), "AAA", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(new File("AAA"), createCalendar(10L), "AAA", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable(new File("AAA"), new File("BBB"), new File("AAA"), new File("BBB"), -1);
        checkMakeComparable(new File("AAA"), "string", "AAA", "string", -1);
        
        checkMakeComparable("5", Boolean.TRUE, "5", "true", -1);
        checkMakeComparable("5", Character.valueOf('B'), "5", "B", -1);
        checkMakeComparable("5", Byte.valueOf((byte)10), Double.valueOf(5), Double.valueOf(10), -1);
        checkMakeComparable("5", Short.valueOf((short)10), Double.valueOf(5), Double.valueOf(10), -1);
        checkMakeComparable("5", Integer.valueOf(10), Double.valueOf(5), Double.valueOf(10), -1);
        checkMakeComparable("5", Long.valueOf(10), Double.valueOf(5), Double.valueOf(10), -1);
        checkMakeComparable("5", BigInteger.valueOf(10), Double.valueOf(5), Double.valueOf(10), -1);
        checkMakeComparable("5", Float.valueOf(10.0F), Double.valueOf(5), Double.valueOf(10.0), -1);
        checkMakeComparable("5", Double.valueOf(10.0), Double.valueOf(5), Double.valueOf(10.0), -1);
        checkMakeComparable("5", BigDecimal.valueOf(10.0), BigDecimal.valueOf(5), BigDecimal.valueOf(10.0), -1);
        checkMakeComparable("5", new java.util.Date(10L), "5", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable("5", new java.sql.Date(10L), "5", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable("5", new Time(10L), "5", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable("5", new Timestamp(10L), "5", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable("5", createCalendar(10L), "5", "1970-01-01 00:00:00.010", 1);
        checkMakeComparable("5", new File("AAA"), "5", "AAA", -1);
        checkMakeComparable("5", "string", "5", "string", -1);
    }
}

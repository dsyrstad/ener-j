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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

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
     * @param o1
     * @param o2
     * @param expectedResult less than 0 if o1 < o2, 0 if equal, or greater than 0 if o1 > 2.  
     */
    private void checkMakeComparable(Object o1, Object o2, Object expectedO1, Object expectedO2, int expectedResult)
    {
        Object[] objs = new Object[] { o1, o2 };
        TypeUtil.makeComparable(objs);
        
        assertEquals("Types do not match. o1=" + objs[0] + ", o2=" + objs[1], objs[0].getClass(), objs[1].getClass());
        
        assertEquals("Expected type does not match.", expectedO1.getClass(), o1.getClass()); 
        assertEquals("Expected type does not match.", expectedO2.getClass(), o2.getClass());
        
        assertEquals("Expected value does not match.", expectedO1, o1); 
        assertEquals("Expected value does not match.", expectedO2, o2);
        
        int result = ((Comparable)objs[0]).compareTo(objs[1]);
        assertTrue("Result not correct. Expected: " + expectedResult + " actual: " + result + 
            ". o1=" + objs[0] + ", o2=" + objs[1] + ". Result types o1=" + objs[0].getClass() +
            " o2=" + objs[1].getClass() + ". Input types: o1=" + o1.getClass() + " o2=" + o2.getClass(), 
            expectedResult == result ||
            (expectedResult < 0 && result < 0) ||
            (expectedResult > 0 && result > 0) );
        
        // Test reverse
        result = ((Comparable)objs[1]).compareTo(objs[0]);
        assertTrue("Reverse result not correct. Expected: " + expectedResult + " actual: " + result + 
            ". o1=" + objs[0] + ", o2=" + objs[1] + ". Result types o1=" + objs[0].getClass() +
            " o2=" + objs[1].getClass() + ". Input types: o1=" + o1.getClass() + " o2=" + o2.getClass(), 
            (expectedResult == 0 && result == 0) ||
            (expectedResult < 0 && result > 0) ||   /* Sense of result reversed */
            (expectedResult > 0 && result < 0) );
    }
    
    /**
     * Test method for {@link org.enerj.util.TypeUtil#makeComparable(java.lang.Object[])}.
     */
    public void testMakeComparable()
    {
        // Test all combinations of Numbers.
        Number[] smallerNumbers = new Number[] { 
            Byte.valueOf((byte)5), Short.valueOf((byte)5), Integer.valueOf(5), Long.valueOf(5),
            Float.valueOf(5), Double.valueOf(5), BigInteger.valueOf(5), BigDecimal.valueOf(5)
        };

        Number[] largerNumbers = new Number[] { 
            Byte.valueOf((byte)6), Short.valueOf((byte)6), Integer.valueOf(6), Long.valueOf(6),
            // Values of 5.1 check for inappropriate integer truncation to 5, which would == the smaller value. 
            Float.valueOf(5.1F), Double.valueOf(5.1), BigInteger.valueOf(6), BigDecimal.valueOf(5.1)
        };

        for (Number smallerNumber : smallerNumbers) {
            for (Number largerNumber : largerNumbers) {
                System.out.println("checkMakeComparable(new " + smallerNumber.getClass().getSimpleName() +
                    "(5), new " + largerNumber.getClass().getSimpleName() + "(6), new " + 
                    largerNumber.getClass().getSimpleName() + "(5), new " + 
                    largerNumber.getClass().getSimpleName() + "(6), -1);");
            }
        }
        
        checkMakeComparable(Byte.valueOf((byte)5), Byte.valueOf((byte)6), Byte.valueOf((byte)5), Byte.valueOf((byte)6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), Short.valueOf((short)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), Float.valueOf(6), Float.valueOf(5), Float.valueOf(6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), Double.valueOf(6), Double.valueOf(5), Double.valueOf(6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(Byte.valueOf((byte)5), BigDecimal.valueOf(6), BigDecimal.valueOf(5), BigDecimal.valueOf(6), -1);
        checkMakeComparable(Short.valueOf((short)5), Byte.valueOf((byte)6), Byte.valueOf((byte)5), Byte.valueOf((byte)6), -1);
        checkMakeComparable(Short.valueOf((short)5), Short.valueOf((short)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(Short.valueOf((short)5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Short.valueOf((short)5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Short.valueOf((short)5), Float.valueOf(6), Float.valueOf(5), Float.valueOf(6), -1);
        checkMakeComparable(Short.valueOf((short)5), Double.valueOf(6), Double.valueOf(5), Double.valueOf(6), -1);
        checkMakeComparable(Short.valueOf((short)5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(Short.valueOf((short)5), BigDecimal.valueOf(6), BigDecimal.valueOf(5), BigDecimal.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), Byte.valueOf((byte)6), Byte.valueOf((byte)5), Byte.valueOf((byte)6), -1);
        checkMakeComparable(Integer.valueOf(5), Short.valueOf((short)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), Float.valueOf(6), Float.valueOf(5), Float.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), Double.valueOf(6), Double.valueOf(5), Double.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(Integer.valueOf(5), BigDecimal.valueOf(6), BigDecimal.valueOf(5), BigDecimal.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), Byte.valueOf((byte)6), Byte.valueOf((byte)5), Byte.valueOf((byte)6), -1);
        checkMakeComparable(Long.valueOf(5), Short.valueOf((short)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(Long.valueOf(5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), Float.valueOf(6), Float.valueOf(5), Float.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), Double.valueOf(6), Double.valueOf(5), Double.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(Long.valueOf(5), BigDecimal.valueOf(6), BigDecimal.valueOf(5), BigDecimal.valueOf(6), -1);
        checkMakeComparable(Float.valueOf(5), Byte.valueOf((byte)6), Byte.valueOf((byte)5), Byte.valueOf((byte)6), -1);
        checkMakeComparable(Float.valueOf(5), Short.valueOf((short)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(Float.valueOf(5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Float.valueOf(5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Float.valueOf(5), Float.valueOf(6), Float.valueOf(5), Float.valueOf(6), -1);
        checkMakeComparable(Float.valueOf(5), Double.valueOf(6), Double.valueOf(5), Double.valueOf(6), -1);
        checkMakeComparable(Float.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(Float.valueOf(5), BigDecimal.valueOf(6), BigDecimal.valueOf(5), BigDecimal.valueOf(6), -1);
        checkMakeComparable(Double.valueOf(5), Byte.valueOf((byte)6), Byte.valueOf((byte)5), Byte.valueOf((byte)6), -1);
        checkMakeComparable(Double.valueOf(5), Short.valueOf((short)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(Double.valueOf(5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(Double.valueOf(5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(Double.valueOf(5), Float.valueOf(6), Float.valueOf(5), Float.valueOf(6), -1);
        checkMakeComparable(Double.valueOf(5), Double.valueOf(6), Double.valueOf(5), Double.valueOf(6), -1);
        checkMakeComparable(Double.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(Double.valueOf(5), BigDecimal.valueOf(6), BigDecimal.valueOf(5), BigDecimal.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), Byte.valueOf((byte)6), Byte.valueOf((byte)5), Byte.valueOf((byte)6), -1);
        checkMakeComparable(BigInteger.valueOf(5), Short.valueOf((short)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(BigInteger.valueOf(5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), Float.valueOf(6), Float.valueOf(5), Float.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), Double.valueOf(6), Double.valueOf(5), Double.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(BigInteger.valueOf(5), BigDecimal.valueOf(6), BigDecimal.valueOf(5), BigDecimal.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5), Byte.valueOf((byte)6), Byte.valueOf((byte)5), Byte.valueOf((byte)6), -1);
        checkMakeComparable(BigDecimal.valueOf(5), Short.valueOf((short)6), Short.valueOf((short)5), Short.valueOf((short)6), -1);
        checkMakeComparable(BigDecimal.valueOf(5), Integer.valueOf(6), Integer.valueOf(5), Integer.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5), Long.valueOf(6), Long.valueOf(5), Long.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5), Float.valueOf(6), Float.valueOf(5), Float.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5), Double.valueOf(6), Double.valueOf(5), Double.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5), BigInteger.valueOf(6), BigInteger.valueOf(5), BigInteger.valueOf(6), -1);
        checkMakeComparable(BigDecimal.valueOf(5), BigDecimal.valueOf(6), BigDecimal.valueOf(5), BigDecimal.valueOf(6), -1);
        
    }
    
    private static final class CompTest
    {
        Object o1;
        Object o2;
        Object expectedO1;
        Object expectedO2;
        int expectedResult;

        CompTest(Object o1, Object o2, Object expectedO1, Object expectedO2, int expectedResult)
        {
            this.o1 = o1;
            this.o2 = o2;
            this.expectedO1 = expectedO1;
            this.expectedO2 = expectedO2;
            this.expectedResult = expectedResult;
        }
    }

}

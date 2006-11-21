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
// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/PersistentFieldsTest.java,v 1.4 2006/06/09 02:39:16 dsyrstad Exp $

package org.enerj.enhancer;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.enerj.annotations.Persist;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests enhancement of persistent and non-persistent fields.
 *
 * @version $Id: PersistentFieldsTest.java,v 1.4 2006/06/09 02:39:16 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class PersistentFieldsTest extends TestCase
{
    private static final String DATABASE_URI = "enerj://root:root@-/PF1TestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";
    private static final String DATABASE2_URI = "enerj://root:root@-/PF2TestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";
    private static final String DATABASE3_URI = "enerj://root:root@-/PF3TestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";
    
    //----------------------------------------------------------------------
    public PersistentFieldsTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(PersistentFieldsTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(PersistentFieldsTest.class);
    }
    
    //----------------------------------------------------------------------
    /**
     * Make sure proper basic enhancement was done.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testEnhancement() throws Exception
    {
        String[] allFields = {
            // Non-persistent fields
            "mTestNonPersistentFinal",
            "mTestNonPersistentStatic",
            "mTestNonPersistentStatic2",
            "mTestNonPersistentTransient",
            "mIntOverriddenNonPersistent1",
            "mIntOverriddenNonPersistent2",

            // Persistent fields
            "mPrivateInt",
            "mPublicInt",
            "mProtectedInt",
            "mPackageInt",
            "mByte",
            "mBoolean",
            "mChar",
            "mShort",
            "mInt",
            "mLong",
            "mFloat",
            "mDouble",
            "mByteObj",
            "mBooleanObj",
            "mCharObj",
            "mShortObj",
            "mIntObj",
            "mLongObj",
            "mFloatObj",
            "mDoubleObj",
            "mString",
            "mBigDecimal",
            "mBigInteger",
            "mLocale",
            "mByteArray",
            "mBooleanArray",
            "mCharArray",
            "mShortArray",
            "mIntArray",
            "mLongArray",
            "mFloatArray",
            "mDoubleArray",
            "mStringSCOArray",
            "mObjectArray",
            "mThisClassArray",
            "mInt2DArray",
            "mObject2DArray",
            "mInt4DArray",
            "mUtilDate",
            "mSqlDate",
            "mSqlTime",
            "mSqlTimestamp",
            "mArrayList",
            "mLinkedList",
            "mTreeSet",
            "mVector",
            "mStack",
            "mHashSet",
            "mLinkedHashSet",
            "mHashMap",
            "mHashtable",
            "mLinkedHashMap",
            "mProperties",
            "mTreeMap",
            "mIdentityHashMap",
            "mObject",
            "mIntOverriddenPersistent1",
            "mIntOverriddenPersistent2",
        };

        // Test: All persistent field enerj_Get_ and enerj_Set_ methods for fields named above must be present.
        String classSuffix = PersistentFieldsTest.TestClass1.class.getName().replace('.', '_') + '_';
        String getPrefix = "enerj_Get_" + classSuffix;
        String setPrefix = "enerj_Set_" + classSuffix;
        for (int i = 0; i < allFields.length; i++) {
            Field field = PersistentFieldsTest.TestClass1.class.getDeclaredField(allFields[i]);
            int fieldModifiers = (field.getModifiers() & ~(Modifier.STATIC | Modifier.TRANSIENT | Modifier.FINAL));
            boolean fieldIsStatic = (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC;

            // Get: This will throw NoSuchMethodException if it doesn't exist.
            // Test: Must take 1 param and type must be the test class - unless static field, then zero params.
            Method method;
            if (fieldIsStatic) {
                method = PersistentFieldsTest.TestClass1.class.getDeclaredMethod(getPrefix + 
                        allFields[i], new Class[0]);
            }
            else {
                method = PersistentFieldsTest.TestClass1.class.getDeclaredMethod(getPrefix + 
                        allFields[i], new Class[] { PersistentFieldsTest.TestClass1.class } );
            }
            
            // Test: Match method scope against field scope.
            String fieldModifiersString = Modifier.toString(fieldModifiers);
            String methodModifiersString = Modifier.toString(method.getModifiers() & ~(Modifier.STATIC | Modifier.FINAL));
            assertTrue("Field and method scope must match " + getPrefix + allFields[i] + " Field modifiers=" + fieldModifiersString +
                       " Method modifiers=" + methodModifiersString,
                       fieldModifiersString.equals(methodModifiersString));

            // Test: Match method return type aginst field type
            assertTrue("Field type and method return type must match " + getPrefix + allFields[i], 
                field.getType().equals( method.getReturnType() ) );

            // Set: This will throw NoSuchMethodException if it doesn't exist.
            // Test: Must have 2 params if not a static field. Method param 1 type must be the test class.
            // Method param 2 type must be field type. If a static field, there is just one param which
            // is the field type.
            if (fieldIsStatic) {
                method = PersistentFieldsTest.TestClass1.class.getDeclaredMethod(setPrefix + 
                        allFields[i], new Class[] { field.getType() } );
            }
            else {
                method = PersistentFieldsTest.TestClass1.class.getDeclaredMethod(setPrefix + 
                        allFields[i], new Class[] { PersistentFieldsTest.TestClass1.class, field.getType() } );
            }

            // Test: Match method scope against field scope.
            fieldModifiersString = Modifier.toString(fieldModifiers);
            methodModifiersString = Modifier.toString(method.getModifiers() & ~(Modifier.STATIC | Modifier.FINAL));
            assertTrue("Field and method scope must match " + setPrefix + allFields[i] + " Field modifiers=" + fieldModifiersString +
                       " Method modifiers=" + methodModifiersString,
                       fieldModifiersString.equals(methodModifiersString));

            // Test: Return must be void
            assertTrue("Method return type must be void " + setPrefix + allFields[i],
                method.getReturnType().equals(Void.TYPE) );
        }
        
        // Test: No other field enerj_Get_/enerj_Set_ methods should be present.
        Method[] methods = PersistentFieldsTest.TestClass1.class.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getName();
            String methodPrefix;
            if (name.startsWith("enerj_Get_")) {
                methodPrefix = getPrefix;
            }
            else if (name.startsWith("enerj_Set_")) {
                methodPrefix = setPrefix;
            }
            else {
                continue;
            }
            
            // Search to see if it is an existing field
            boolean found = false;
            for (int fieldIdx = 0; fieldIdx < allFields.length; ++fieldIdx) {
                if (name.equals(methodPrefix + allFields[fieldIdx])) {
                    found = true;
                }
            }
            
            assertTrue("Expected to find matching field for method: " + name, found);
        }
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    @Persist
    private static final class TestClass1
    {
        // These should not be persistent
        private final String mTestNonPersistentFinal = "";  // final
        static String mTestNonPersistentStatic;        // static
        static String mTestNonPersistentStatic2;       // static
        transient int mTestNonPersistentTransient;     // transient - not overridden

        // These should be persistent
        // Test scopes
        private int mPrivateInt;
        public int mPublicInt;
        protected int mProtectedInt;
        int mPackageInt;

        // Test types
        private byte mByte;
        private boolean mBoolean;
        private char mChar;
        private short mShort;
        private int mInt;
        private long mLong;
        private float mFloat;
        private double mDouble;

        // Test primitive wrapper types
        private Byte mByteObj;
        private Boolean mBooleanObj;
        private Character mCharObj;
        private Short mShortObj;
        private Integer mIntObj;
        private Long mLongObj;
        private Float mFloatObj;
        private Double mDoubleObj;

        // Test rest of the immutable SCOs
        private String mString;
        private java.math.BigDecimal mBigDecimal;
        private java.math.BigInteger mBigInteger;
        private java.util.Locale mLocale;

        // Test mutable SCOs
        private byte[] mByteArray;
        private boolean[] mBooleanArray;
        private char[] mCharArray;
        private short[] mShortArray;
        private int[] mIntArray;
        private long[] mLongArray;
        private float[] mFloatArray;
        private double[] mDoubleArray;
        private String[] mStringSCOArray;
        private Object[] mObjectArray;
        private TestClass1[] mThisClassArray;
        private int[][] mInt2DArray;
        private Object[][] mObject2DArray;
        private int[][][][] mInt4DArray;
        private java.util.Date mUtilDate;
        private java.sql.Date mSqlDate;
        private java.sql.Time mSqlTime;
        private java.sql.Timestamp mSqlTimestamp;
        private java.util.ArrayList mArrayList;
        private java.util.LinkedList mLinkedList;
        private java.util.TreeSet mTreeSet;
        private java.util.Vector mVector;
        private java.util.Stack mStack;
        private java.util.HashSet mHashSet;
        private java.util.LinkedHashSet mLinkedHashSet;
        private java.util.HashMap mHashMap;
        private java.util.Hashtable mHashtable;
        private java.util.LinkedHashMap mLinkedHashMap;
        private java.util.Properties mProperties;
        private java.util.TreeMap mTreeMap;
        private java.util.IdentityHashMap mIdentityHashMap;

        // Generic type - unknown until runtime.
        private Object mObject;
        
        // Persistent fields - as overridden by metadata
        @Persist(true)
        transient private int mIntOverriddenPersistent1;
        // this already is persistent, but metadata redundantly says so.
        @Persist(true)
        private int mIntOverriddenPersistent2;  
        
        // Non persistent fields - as overridden by metadata
        @Persist(false)
        private int mIntOverriddenNonPersistent1;
        // this already is non-persistent, but metadata redundantly says so.
        @Persist(false)
        transient private int mIntOverriddenNonPersistent2;
    }
    
}

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
// This is not an official test. It's mainly for experimentation with the enhancer.

package org.enerj.enhancer;

import java.util.*;
import java.io.*;

import org.enerj.core.*;

// Test class for enhancement.

class TestClass implements Cloneable
{
    // These should not be persistent
    private final String mTestNonPersistentFinal = "a";
    static String mTestNonPersistentStatic;
    transient int mTestNonPersistentTransient;

    // These should be persistent
    // Test scopes
    private int mPrivateInt;
    public int mPublicInt;
    protected String mProtectedString;
    long mPackageLong;

    // Test types
    private char mChar;
    private byte mByte;
    private short mShort;
    private int mInt;
    private long mLong;
    private float mFloat;
    private double mDouble;
    private Character mCharObj;
    private Byte mByteObj;
    private Short mShortObj;
    private Integer mIntObj;
    private Long mLongObj;
    private Float mFloatObj;
    private Double mDoubleObj;
    private String mString;
    private TestClass mTestClassObj;
    private Object[] mObjectArray = new Object[10];
    private Properties mProperties;
    
    // Static initializer - <clinit> - should not be enhanced
    static {
        mTestNonPersistentStatic = "foo";
    }
    
    //----------------------------------------------------------------------
    TestClass()
    {
        // Intentionally do a new here.
        this( new String("test"), 5 );
        System.out.println(mLong);
        mLong = 10239;
        System.out.println(mLong);
    }

    //----------------------------------------------------------------------
    TestClass(String aString, int aValue)
    {
        // implicit super();
        // Make sure branches are not munged
        if (aValue == 5) {
            mString = aString;
            mShortObj = new Short((short)32);
        }
        else {
            mString = aString + "cc";
            mShortObj = new Short((short)66);
        }
        
        mDoubleObj = new Double(55.);
    }
    
    //----------------------------------------------------------------------
    void testFieldAccessMethod()
    {
        System.out.println(mLong);
        ++mLong;
        System.out.println(mLong);
    }

    //----------------------------------------------------------------------
    static void testFieldAccessStaticMethod(TestClass aTestClass)
    {
        System.out.println(aTestClass.mLong);
        aTestClass.mLong = 10255;
        System.out.println(aTestClass.mLong);
    }

    //----------------------------------------------------------------------
    public Object clone() throws CloneNotSupportedException
    {
        TestClass clone = (TestClass)super.clone();
        
        if (mLong == 44) {
            clone.mString = "4848";
        }
        else {
            clone.mString = "clone";
            return clone;
        }
        
        clone.mShortObj = new Short((short)55);
        return clone;
    }
    
    //----------------------------------------------------------------------
    // Optional EnerJ callback.
    private void enerjPostLoad()
    {
        System.out.println("enerjPostLoad called on " + this);
    }
    
    //----------------------------------------------------------------------
    // Optional EnerJ callback.
    private void enerjPreStore()
    {
        System.out.println("enerjPreStore called on " + this);
    }
    
    //----------------------------------------------------------------------
    // Optional EnerJ callback.
    private void enerjPostStore()
    {
        System.out.println("enerjPostStore called on " + this);
    }
    
    //----------------------------------------------------------------------
    // Optional EnerJ callback.
    private void enerjPreHollow()
    {
        System.out.println("enerjPreHollow called on " + this);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args)  throws Exception
    {
        // Let's see if this class even loads....
        TestClass test = new TestClass();
        org.enerj.core.Persistable persistable = (org.enerj.core.Persistable)test;
        
        // This should really be a transaction...
        persistable.enerj_SetAllowNonTransactionalRead(true);
        persistable.enerj_SetAllowNonTransactionalWrite(true);

        test.testFieldAccessMethod();
        testFieldAccessStaticMethod(test);
        
        test.mIntObj = new Integer(234);

        // Write it to a Byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        persistable.enerj_WriteObject(new ObjectSerializer(dos) );
        
        byte[] bytes = baos.toByteArray();
        System.out.println("Wrote " + bytes.length + " bytes");
        
        // Read it back into another object
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        TestClass test2 = new TestClass();
        org.enerj.core.Persistable persistable2 = (org.enerj.core.Persistable)test2;
        persistable2.enerj_SetAllowNonTransactionalRead(true);
        persistable2.enerj_SetAllowNonTransactionalWrite(true);
        persistable2.enerj_ReadObject( new ObjectSerializer(dis) );
        
        if (test2.mLong != test.mLong) {
            System.out.println("mLongs differ");
        }
        
        if (test2.mIntObj.intValue() != test.mIntObj.intValue()) {
            System.out.println("mIntObjs differ");
        }

        TestClass testclone = (TestClass)test.clone();
        System.out.println("Clone works");
        
        System.out.println("done");
    }
}

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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/templates/EnhancerTemplate_Orig.java,v 1.3 2005/08/12 02:56:53 dsyrstad Exp $

package org.enerj.enhancer.templates;

import java.util.Date;

import org.enerj.annotations.Index;

/**
 * Class file enhancer template for Ener-J. This is a "top-level" persistable.
 * This class provides a bytecode prototype for developement of the enhancer.
 * This is the class prior to enhancement. Ignore the _Orig extension, it wouldn't normally exist.
 *
 * @version $Id: EnhancerTemplate_Orig.java,v 1.3 2005/08/12 02:56:53 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
@Index(type=Index.Type.BTree, properties={ "mLong", "mString" })
class EnhancerTemplate_Orig extends java.util.Date implements Cloneable
{
    static int mPackageStaticInt;
    transient int mPackageTransientInt;
    
    private byte mByte;
    private Byte mByteObj;
    private boolean mBoolean;
    private Boolean mBooleanObj;
    private char mChar;
    private Character mCharObj;
    private short mShort;
    @Index
    private Short mShortObj;
    private int mInt;
    private Integer mIntObj;
    private long mLong;
    private Long mLongObj;
    private float mFloat;
    private Float mFloatObj;
    private double mDouble;
    private Double mDoubleObj;
    private String mString;
    private Object mObject;

    private int[] mIntArray;
    private int[][] m2dArray;
    private Object[] mObjArray;
    

    // User-defined constructor.
    public EnhancerTemplate_Orig(int aParam)
    {
        super(34L); // To Date.<init>(long)
        mInt = aParam;
    }


    // User-defined methods ...

    
    public void test(Object obj) 
    {
        Date[][] array = (Date[][])obj;
    }


    public void runOn(int aParam)
    {
        System.out.println("mLong is " + mLong);
        mInt = aParam;
    }

    public void arrayAccess()
    {
        mIntArray[0] = 5;
    }

    // User-defined clone.
    public Object clone() 
    {
        EnhancerTemplate_Orig clone = (EnhancerTemplate_Orig)super.clone();
        clone.mDouble = 0.;
        clone.mIntArray = (int[])this.mIntArray.clone();
 

        return clone;
    }


    // User-defined clone. Version 2 - does not use super.clone();
    public Object clone2() 
    {
        EnhancerTemplate_Orig clone = new EnhancerTemplate_Orig(5);
        clone.mDouble = 0.;
        clone.mIntArray = (int[])this.mIntArray.clone();

        return clone;
    }


    /**
     * User callback.
     */
    private void enerjPreHollow()
    {
    }
    
    
    // ...End of User-defined methods.


}

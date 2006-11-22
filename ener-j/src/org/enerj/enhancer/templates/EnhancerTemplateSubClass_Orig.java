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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/templates/EnhancerTemplateSubClass_Orig.java,v 1.3 2005/08/12 02:56:53 dsyrstad Exp $

package org.enerj.enhancer.templates;

import java.io.*;

import org.enerj.core.*;
import org.odmg.*;

/**
 * Class file enhancer template for Ener-J. This is an example of a 
 * sub-class of EnhancerTemplate.
 * This class provides a bytecode prototype for developement of the enhancer.
 * This is the class prior to enhancement. Ignore the _Orig extension, it wouldn't normally exist.
 *
 * @version $Id: EnhancerTemplateSubClass_Orig.java,v 1.3 2005/08/12 02:56:53 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class EnhancerTemplateSubClass_Orig extends EnhancerTemplate_Orig implements Cloneable
{
    private byte mByte;
    private Byte mByteObj;
    private boolean mBoolean;
    private Boolean mBooleanObj;
    private char mChar;
    private Character mCharObj;
    private short mShort;
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
    public EnhancerTemplateSubClass_Orig(long aParam)
    {
        super(aParam > 0 ? 5 : 4);
        mLong = aParam;
    }


    // User-defined methods ...



    public void doIt(int aParam)
    {
        mInt = aParam;
    }


    // User-defined clone.
    public Object clone()
    {
        EnhancerTemplateSubClass_Orig clone = (EnhancerTemplateSubClass_Orig)super.clone();
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

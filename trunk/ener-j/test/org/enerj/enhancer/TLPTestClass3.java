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

package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for TopLevelPersistableEnhancementTest.
 * Note super-class doesn't have an exposed no-arg constructor.
 */
@Persist
class TLPTestClass3 extends TLPTestClassNP
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    TLPTestClass3(int aValue)
    {
        super(true);
        mValue = aValue;
    }
    
    //----------------------------------------------------------------------
    // No-arg constructor because superclass doesn't have one.
    TLPTestClass3()
    {
        this(4);
    }
    
    //----------------------------------------------------------------------
    public void someMethod()
    {
        mValue = 22;
        sSomeValue = 23;
    }

    //----------------------------------------------------------------------
    public boolean equals(Object anObject)
    {
        if (!(anObject instanceof TLPTestClass3)) {
            return false;
        }
        
        TLPTestClass3 obj = (TLPTestClass3)anObject;
        return this.mValue == obj.mValue;
    }
}

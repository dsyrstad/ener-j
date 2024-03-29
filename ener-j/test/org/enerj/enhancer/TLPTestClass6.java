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
 * Note super-class doesn't have an exposed no-arg constructor and
 * neither does this class. This should be a runtime error.
 */
@Persist
class TLPTestClass6 extends TLPTestClassNP
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    

    TLPTestClass6(int aValue)
    {
        super(true);
        mValue = aValue;
    }
    

    public void someMethod()
    {
        mValue = 22;
        sSomeValue = 23;
    }


    public boolean equals(Object anObject)
    {
        if (!(anObject instanceof TLPTestClass6)) {
            return false;
        }
        
        TLPTestClass6 obj = (TLPTestClass6)anObject;
        return this.mValue == obj.mValue;
    }
}

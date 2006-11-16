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

import org.enerj.annotations.PersistenceAware;
import org.enerj.core.*;

/**
 * Test class for PersistentAwareEnhancementTest.
 * This class is only PersistentAware.
 */
@PersistenceAware
class PAETestClass1
{
    private int mValue;
    PAETestClass2 mPersistable = null;
    
    //----------------------------------------------------------------------
    PAETestClass1()
    {
        mValue = 5;
        // Here's the reference to the Persistable object.
        mPersistable = new PAETestClass2(mValue);
        PersistableHelper.setNonTransactional(mPersistable);
    }
    
    //----------------------------------------------------------------------
    void modifyPersistable()
    {
        // This should be enhanced
        mPersistable.mExposedField = 22;
        
        // The persitable should now be modified
        
        // This should get enhanced too.
        String test = "The value is " + mPersistable.mExposedField;
    }
    
}
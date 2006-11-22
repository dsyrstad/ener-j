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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/SetFieldTest.java,v 1.4 2006/06/09 02:39:16 dsyrstad Exp $

package org.enerj.enhancer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.enerj.annotations.Persist;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;

/**
 * Tests setting persistent fields functionality.
 *
 * @version $Id: SetFieldTest.java,v 1.4 2006/06/09 02:39:16 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class SetFieldTest extends TestCase 
{
    private static final String DATABASE_URI = "enerj://root:root@-/SetFieldTestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";


    public SetFieldTest(String aTestName) 
    {
        super(aTestName);
    }
    

    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(SetFieldTest.class);
    }
    

    public static Test suite() 
    {
        return new TestSuite(SetFieldTest.class);
    }


    /**
     * Test enerj_Set_
     */
    public void testSetField() throws Exception
    {
        TestClass1 test = new TestClass1();
        PersistableHelper.setNonTransactional(test);
        test.manipulateFieldWithNull();
        // If we get here without Exceptions, we succeeded.
    }
    


    /**
     * Test class
     */
    @Persist
    private static class TestClass1
    {
        private int mInt;
        private String mString;
        private Object mObj;
        

        TestClass1()
        {
        }
        

        /**
         * Set mString to a value, to a null, and back.
         * Tests enerj_Set_'s proper handling of null values for the special case
         * of objects that are String, Byte, Character, Integer, Boolean, Short,
         * Long, Float, and Double.
         */
        void manipulateFieldWithNull()
        {
            Persistable persistable = (Persistable)this;

            // Set to null when null
            persistable.enerj_SetModified(false);
            mString = null; 
            assertTrue("Should not be modified", !persistable.enerj_IsModified());

            // Set to non-null when null
            persistable.enerj_SetModified(false);
            mString = "SomeValue";
            assertTrue("Should be modified", persistable.enerj_IsModified());

            // Set to same value, different String object
            persistable.enerj_SetModified(false);
            mString = new String("SomeValue");
            assertTrue("Should not be modified", !persistable.enerj_IsModified());

            // Set to null when non-null
            persistable.enerj_SetModified(false);
            mString = null; 
            assertTrue("Should be modified", persistable.enerj_IsModified());
        }
    }
    
}

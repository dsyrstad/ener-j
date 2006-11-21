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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/NonTLPersistableEnhancementTest.java,v 1.5 2006/06/09 02:39:17 dsyrstad Exp $

package org.enerj.enhancer;

import java.lang.reflect.Constructor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;

/**
 * Tests enhancement of Non-Top-level Persistable classes.
 *
 * @version $Id: NonTLPersistableEnhancementTest.java,v 1.5 2006/06/09 02:39:17 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class NonTLPersistableEnhancementTest extends AbstractPersistableEnhancementTest 
{
    private static final String DATABASE_URI = "enerj://root:root@-/NTLPETestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";

    //----------------------------------------------------------------------
    public NonTLPersistableEnhancementTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(NonTLPersistableEnhancementTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(NonTLPersistableEnhancementTest.class);
    }
    
    //----------------------------------------------------------------------
    /**
     * Make sure proper basic enhancement was done for fields and methods.
     *
     * @param aTestClass the class to be checked.
     *
     * @throws Exception when something unexpected occurs.
     */
    private void checkFieldsAndMethods(Class aTestClass, int aPersistentSize) throws Exception
    {
        String[] expectedFields = new String[] { "enerj_sClassId", "mValue", "sSomeValue", };

        String classSuffix = aTestClass.getName().replace('.', '_');
        String[] expectedMethods;
        if (aTestClass == NTLPTestClass1.class) {
            expectedMethods = new String[] { 
                "enerj_Get_" + classSuffix + "_mValue", "enerj_Set_" + classSuffix + "_mValue",
                "enerj_Get_" + classSuffix + "_sSomeValue", "enerj_Set_" + classSuffix + "_sSomeValue",
                "enerj_GetClassId", "enerj_GetClassIdStatic", "enerj_ReadObject", "enerj_WriteObject",  "enerj_Hollow",
                "someMethod", "equals", "clone"
            };
        }
        else {
            expectedMethods = new String[] { 
                "enerj_Get_" + classSuffix + "_mValue", "enerj_Set_" + classSuffix + "_mValue",
                "enerj_Get_" + classSuffix + "_sSomeValue", "enerj_Set_" + classSuffix + "_sSomeValue",
                "enerj_GetClassId", "enerj_GetClassIdStatic", "enerj_ReadObject", "enerj_WriteObject",  "enerj_Hollow",
                "someMethod", "equals"
            };
        }
        
        checkCommonEnhancement(aTestClass, expectedFields, expectedMethods, true, aPersistentSize);
        
        // Superclass should implement Persistable
        Class[] interfaces = aTestClass.getSuperclass().getInterfaces();
        assertTrue("Expected one interface to be implemented on superclass", interfaces.length == 1);
        
        assertTrue("Expected Persistable interface on superclass", interfaces[0] == org.enerj.core.Persistable.class);
    }

    //----------------------------------------------------------------------
    /**
     * Make sure proper basic enhancement was done.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testEnhancement() throws Exception
    {
        checkFieldsAndMethods(NTLPTestClass1.class, 8);
        checkFieldsAndMethods(NTLPTestClass2.class, 12);
    }
    
    //----------------------------------------------------------------------
    /**
     * Test that the "new" flag is set on instantiation.
     *
     * @param aTestClass the class to be checked.
     * @param aPersistable a "new"ed instance of aTestClass.
     * @throws Exception when something unexpected occurs.
     */
    private void checkNew(Class aTestClass, Persistable aPersistable) throws Exception
    {
        PersistableHelper.setNonTransactional(aPersistable);
        // Tests enhancement of constructor to call PersistableHelper to set NEW
        assertTrue("Expected it to be new", aPersistable.enerj_IsNew() );
        // Tests that constructor was NOT enhanced for putfield.
        assertTrue("Expected it to be not modified", !aPersistable.enerj_IsModified() );

        // Construct like the database will. Neither the New nor modified flags should be 
        // set because the superclass (EnerJDatabase) constructor is used.
        Constructor constructor = 
            NTLPTestClass1.class.getConstructor(new Class[] { org.enerj.core.EnerJDatabase.class });
        Persistable persistable = (Persistable)constructor.newInstance(new Object[] { null } );
        // The above shouldn't have thrown an exception.
        assertNotNull("Construction should not return null", persistable);

        PersistableHelper.setNonTransactional(persistable);
        // Tests that constructor was NOT enhanced for new nor putfield.
        assertTrue("Expected it to be not new and not modified", !persistable.enerj_IsNew() && !persistable.enerj_IsModified() );
    }
    
    //----------------------------------------------------------------------
    /**
     * Test that the "new" flag is set on instantiation.
     * >> Should be moved to common Persistable tests.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testNew() throws Exception
    {
        checkNew(NTLPTestClass1.class, (Persistable)new NTLPTestClass1(666) );
        checkNew(NTLPTestClass2.class, (Persistable)new NTLPTestClass2(667) );
    }

}

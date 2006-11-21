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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/PersistentAwareEnhancementTest.java,v 1.4 2006/06/09 02:39:16 dsyrstad Exp $

package org.enerj.enhancer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.enerj.core.Persistable;

/**
 * Tests enhancement of PersistentAware classes.
 *
 * @version $Id: PersistentAwareEnhancementTest.java,v 1.4 2006/06/09 02:39:16 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class PersistentAwareEnhancementTest extends TestCase 
{
    private static final String DATABASE_URI = "enerj://root:root@-/PersistentAwareTestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";

    //----------------------------------------------------------------------
    public PersistentAwareEnhancementTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(PersistentAwareEnhancementTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(PersistentAwareEnhancementTest.class);
    }

    //----------------------------------------------------------------------
    /**
     * Make sure proper basic enhancement was done.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testEnhancement() throws Exception
    {
        Class paeTestClass1 = PAETestClass1.class;

        // There should be only one no-arg constructor on PAETestClass1
        Constructor[] constructors = paeTestClass1.getDeclaredConstructors();
        assertTrue("Expected only one constructor", constructors.length == 1);
        
        Class[] paramTypes = constructors[0].getParameterTypes();
        assertTrue("Expected zero parameters to constructor", paramTypes.length == 0);
        
        // It should define one interface: PersistentAware
        Class[] interfaces = paeTestClass1.getInterfaces();
        assertTrue("Expected one interface", interfaces.length == 1);
        
        assertTrue(interfaces[0] == org.enerj.core.PersistentAware.class);
        
        // There should be no addded "enerj_" fields.
        Field[] fields = paeTestClass1.getDeclaredFields();
        assertTrue("Expected two fields", fields.length == 2);
        
        for (int i = 0; i < fields.length; i++) {
            assertTrue(!fields[i].getName().startsWith("enerj_"));
        }

        // There should be no addded "enerj_" methods.
        Method[] methods = paeTestClass1.getDeclaredMethods();
        assertTrue("Expected one method", methods.length == 1);
        
        for (int i = 0; i < methods.length; i++) {
            assertTrue(!methods[i].getName().startsWith("enerj_"));
        }
    }

    //----------------------------------------------------------------------
    /**
     * Make sure proper getfield/putfield enhancement was done.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testGetPutFieldEnhancement() throws Exception
    {
        PAETestClass1 paeTestClass1 = new PAETestClass1();
        assertTrue("Initial value should be 5", paeTestClass1.mPersistable.mExposedField == 5);
        
        ((Persistable)paeTestClass1.mPersistable).enerj_SetModified(false);
        paeTestClass1.modifyPersistable();
        
        assertTrue("Persistable should be modified", ((Persistable)paeTestClass1.mPersistable).enerj_IsModified() );
    }

}

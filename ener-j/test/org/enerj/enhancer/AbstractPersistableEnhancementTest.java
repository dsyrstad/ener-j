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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/AbstractPersistableEnhancementTest.java,v 1.1 2006/01/12 04:39:45 dsyrstad Exp $

package org.enerj.enhancer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

import org.enerj.core.ObjectSerializer;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJImplementation;

/**
 * Common tests for enhancement of Persistable classes.
 *
 * @version $Id: AbstractPersistableEnhancementTest.java,v 1.1 2006/01/12 04:39:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
abstract class AbstractPersistableEnhancementTest extends TestCase 
{
    private static boolean sEnhanced = false;
    

    public AbstractPersistableEnhancementTest(String aTestName) 
    {
        super(aTestName);
    }
    

    /**
     * Check fields and methods.
     *
     * @param aTestClass the class to be checked.
     *
     * @throws Exception when something unexpected occurs.
     */
    protected void checkFieldsAndMethods(Class aTestClass, String[] anExpectedFieldsArray, 
        String[] anExpectedMethodsArray) throws Exception
    {
        // Test fields
        Field[] fields = aTestClass.getDeclaredFields();
        assertTrue("Expected " + anExpectedFieldsArray.length +" fields on class " + aTestClass + ", got " + fields.length, 
            fields.length == anExpectedFieldsArray.length);

        // Search list and null-out fields we've found.
        int matches = 0;
        for (int i = 0; i < fields.length; i++) {
            boolean found = false;
            for (int fieldIdx = 0; fieldIdx < anExpectedFieldsArray.length; ++fieldIdx) {
                if (anExpectedFieldsArray[fieldIdx] != null && fields[i].getName().equals(anExpectedFieldsArray[fieldIdx])) {
                    anExpectedFieldsArray[fieldIdx] = null;
                    ++matches;
                    found = true;
                    break;
                }
            }
            
            assertTrue("Unexpected field found " + fields[i].getName(), found);
        }
        
        assertTrue("Expected all field names to match", matches == anExpectedFieldsArray.length);

        // Test methods
        Method[] methods = aTestClass.getDeclaredMethods();
        assertTrue("Expected " + anExpectedMethodsArray.length + " methods, got " + methods.length + " on " + aTestClass, 
            methods.length == anExpectedMethodsArray.length);
        
        // Search list and null-out methods we've found.
        matches = 0;
        for (int i = 0; i < methods.length; i++) {
            boolean found = false;
            for (int methodIdx = 0; methodIdx < anExpectedMethodsArray.length; ++methodIdx) {
                String methodName = methods[i].getName();
                if (anExpectedMethodsArray[methodIdx] != null && methodName.equals(anExpectedMethodsArray[methodIdx])) {
                    anExpectedMethodsArray[methodIdx] = null;
                    ++matches;
                    found = true;
                    
                    // Checks for enerj_Set/Get_...
                    if (methodName.startsWith("enerj_Set_") || methodName.startsWith("enerj_Get_")) {
                        int skipLen = ("enerj_Xet_" + aTestClass.getName().replace('.', '_') + '_').length();
                        String fieldName = methodName.substring(skipLen);
                        java.lang.reflect.Field field = aTestClass.getDeclaredField(fieldName);
                        int fieldModifiers = field.getModifiers();
                        
                        // Make sure method is static and has same accessibility as field.
                        int fieldMask = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;
                        String expectedModifiersString = Modifier.toString(Modifier.STATIC | (fieldModifiers & fieldMask));
                        
                        int methodModifiers = methods[i].getModifiers();
                        String methodModifiersString = Modifier.toString(methodModifiers);
                        assertTrue("Expected modifiers=" + expectedModifiersString + 
                            " method modifiers=" + methodModifiersString + " on method " + methodName + " don't match",
                            expectedModifiersString.equals(methodModifiersString) );
                        
                        // Check return type - must be the same as the field for 
                        // enerj_Get_ and void for enerj_Set_
                        Class returnType = methods[i].getReturnType();
                        Class fieldType = field.getType();
                        if (methodName.startsWith("enerj_Set_")) {
                            fieldType = Void.TYPE;
                        }
                        
                        assertTrue("Expected field type and method return type to match on method " + methodName +
                            " field type is " + fieldType + ", method return type is " + returnType,
                            fieldType == returnType);
                    }
                    break;
                }
            }
            
            assertTrue("Unexpected method found " + methods[i].getName(), found);
        }
        
        assertTrue("Expected all method names to match", matches == anExpectedMethodsArray.length);
    }
    

    /**
     * Make sure proper basic enhancement was done for fields and methods.
     *
     * @param aTestClass the class to be checked.
     *
     * @throws Exception when something unexpected occurs.
     */
    protected void checkCommonEnhancement(Class aTestClass, String[] anExpectedFieldsArray, 
        String[] anExpectedMethodsArray, boolean shouldCheckNew, int aPersistentSize) throws Exception
    {
        // Should implement Persistable
        Class[] interfaces = aTestClass.getInterfaces();
        int numInterfaces = interfaces.length;
        int checkInterface = 0;
        if (numInterfaces == 2) {
            if (interfaces[0] == java.lang.Cloneable.class) {
                // Allow Cloneable, but pretend it's not there.
                --numInterfaces;
                checkInterface = 1;
            } 
            
        }
        
        assertEquals("Expected one interface to be implemented", 1, numInterfaces);
        
        assertEquals("Expected Persistable interface", org.enerj.core.Persistable.class, interfaces[checkInterface]);

        checkFieldsAndMethods(aTestClass, anExpectedFieldsArray, anExpectedMethodsArray);

        if (shouldCheckNew) {
            checkNew(aTestClass, aPersistentSize);
        }
        
        checkClone(aTestClass);
    }


    /**
     * Make sure proper basic enhancement was done for fields and methods.
     *
     * @param aTestClass the class to be checked.
     *
     * @throws Exception when something unexpected occurs.
     */
    protected void checkNew(Class aTestClass, int aPersistentSize) throws Exception
    {
        // This should not throw an exception.
        Constructor constructor = 
            aTestClass.getConstructor(new Class[] { org.enerj.core.EnerJDatabase.class });
        
        // Construct like the database will. Neither the New nor modified flags should be 
        // set because the superclass (EnerJDatabase) constructor is used.
        Persistable persistable = null;
        try {
            persistable = (Persistable)constructor.newInstance(new Object[] { null } );
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof Exception) {
                throw (Exception)t;
            }
            else if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            else {
                fail("Unknown Throwable:" + t.toString());
            }
        }

        // The above shouldn't have thrown an exception.
        assertNotNull("Construction should not return null", persistable);

        persistable.enerj_SetLoaded(true);

        PersistableHelper.setNonTransactional(persistable);
        // Tests that modified is not set right now.
        assertTrue("Expected it to be not modified", !persistable.enerj_IsModified() );

        // Call "someMethod" on the object. This should set the modified flag due to
        // assignment of mValue. It should also verify that a method call was not generated
        // for the static field sSomeValue becuase it is not persistent and no method was generated
        // for it.
        java.lang.reflect.Method method = aTestClass.getMethod("someMethod");
        try {
            method.invoke(persistable);
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof Exception) {
                throw (Exception)t;
            }
            else if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            else {
                fail("Unknown Throwable:" + t.toString());
            }
        }
        
        // It should be modified now.
        assertTrue("Expected it to be modified", persistable.enerj_IsModified() );
        
        
        // Test writing to a Byte Buffer
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(1000);
        DataOutputStream dataOutput = new DataOutputStream(byteOutputStream);
        persistable.enerj_WriteObject( new ObjectSerializer(dataOutput) );
        
        // Check for correct size.
        byte[] buf  = byteOutputStream.toByteArray();
        int bytesWritten = buf.length;
        assertTrue("Expected to write " + aPersistentSize + " bytes, wrote " + bytesWritten + " on object " + 
            aTestClass.getName(), aPersistentSize == bytesWritten);
        
        // Make a new object and read back.
        Persistable persistable2 = (Persistable)constructor.newInstance(new Object[] { null } );
        persistable2.enerj_SetLoaded(true);
        PersistableHelper.setNonTransactional(persistable2);

        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(buf);
        DataInputStream dataInput = new DataInputStream(byteInputStream);
        persistable2.enerj_ReadObject( new ObjectSerializer(dataInput) );
        int bytesRead = buf.length - byteInputStream.available();
        assertTrue("Expected to read " + aPersistentSize + " bytes, read " + bytesRead + " on object " + 
            aTestClass.getName(), aPersistentSize == bytesRead);
        
        // Verify that objects are equal
        assertTrue("Expected written object and read object to be equal", persistable.equals(persistable2));
    }


    /**
     * Test clone if class implements it. NTLPTestClass1 and TLPTestClass1
     * implement Cloneable. The parent of NTLPTestClass1 does not.
     *
     * @param aTestClass the class to be checked.
     *
     * @throws Exception when something unexpected occurs.
     */
    protected void checkClone(Class aTestClass) throws Exception
    {
        if (!java.lang.Cloneable.class.isAssignableFrom(aTestClass)) {
            return;
        }
        
        System.out.println("Testing checkClone on " + aTestClass);

        EnerJDatabase database = (EnerJDatabase)EnerJImplementation.getInstance().newDatabase();
        database.open("enerj://root:root@-/TestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer", EnerJDatabase.OPEN_READ_WRITE);
        
        org.odmg.Transaction txn = EnerJImplementation.getInstance().newTransaction();
        txn.begin();

        // Construct like the database will.
        Constructor constructor = aTestClass.getConstructor(new Class[] { org.enerj.core.EnerJDatabase.class });
        Persistable persistable = (Persistable)constructor.newInstance(new Object[] { null } );
        
        // Reset new and set modified and loaded to make sure these are properly reset by the clone method.
        persistable.enerj_SetNew(false);
        persistable.enerj_SetModified(true);
        persistable.enerj_SetLoaded(true);
        persistable.enerj_SetPrivateOID(393838L); // Some unordinary OID

        Method cloneMethod = aTestClass.getMethod("clone");
        Persistable clone = (Persistable)cloneMethod.invoke(persistable);

        assertTrue("Should be new", clone.enerj_IsNew());
        assertTrue("Should not be modified", !clone.enerj_IsModified());
        assertTrue("Should not be loaded", !clone.enerj_IsLoaded());
        assertTrue("OIDs should be different", database.getOID(clone) != database.getOID(persistable));
        txn.abort();
        database.close();
    }
}

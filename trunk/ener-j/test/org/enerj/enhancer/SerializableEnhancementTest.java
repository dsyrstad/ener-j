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
//Ener-J
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/SerializableEnhancementTest.java,v 1.1 2006/06/06 22:40:58 dsyrstad Exp $

package org.enerj.enhancer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.enerj.annotations.Persist;
import org.enerj.core.Persistable;

import junit.framework.TestCase;

/**
 * Tests enhancement of Serializables. <p>
 * 
 * @version $Id: SerializableEnhancementTest.java,v 1.1 2006/06/06 22:40:58 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class SerializableEnhancementTest extends TestCase
{


    /**
     * Construct a SerializableEnhancementTest. 
     */
    public SerializableEnhancementTest()
    {
    }


    /**
     * Construct a SerializableEnhancementTest. 
     *
     * @param aName
     */
    public SerializableEnhancementTest(String aName)
    {
        super(aName);
    }
    
    

    /**
     * Create a copy of an Object via serialization.
     *
     * @param obj the original Object.
     * 
     * @return the copy.
     */
    private Object createViaSerialization(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

            ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Persistable)ois.readObject();
        }
        catch (IOException x) {
            throw new RuntimeException(x);
        }
        catch (ClassNotFoundException x) {
            throw new RuntimeException(x);
       }
    }
    

    /**
     * Test enhancement of a top-level persistable that doesn't implement the
     * readObject method.
     *
     * @throws Exception
     */
    public void testTLPWithoutReadObject() throws Exception
    {
        // Enhancer should have added a readObject method.
        // This should not throw. If the method doesn't exist, it will throw.
        TLPWithoutReadObject.class.getDeclaredMethod("readObject", new Class[] { ObjectInputStream.class } );
        
        // PersistableHelper.initPersistable() should be called on deserialization.
        TLPWithoutReadObject testObj = (TLPWithoutReadObject)createViaSerialization(new TLPWithoutReadObject());
        
        // This should have been set.
        assertTrue(((Persistable)testObj).enerj_AllowsNonTransactionalRead());
        
        // Should be able to call the test() method without throwing an exception.
        testObj.test();
    }


    /**
     * Test enhancement of a top-level persistable that implements the
     * readObject method.
     *
     * @throws Exception
     */
    public void testTLPWithReadObject() throws Exception
    {
        // Enhancer should have modified the existing readObject method.
        // This should not throw. If the method doesn't exist, it will throw.
        TLPWithReadObject.class.getDeclaredMethod("readObject", new Class[] { ObjectInputStream.class } );
        
        // PersistableHelper.initPersistable() should be called on deserialization.
        TLPWithReadObject testObj = (TLPWithReadObject)createViaSerialization(new TLPWithReadObject());
        
        // This should have been set.
        assertTrue(((Persistable)testObj).enerj_AllowsNonTransactionalRead());
        
        // Should be able to call the test() method without throwing an exception.
        testObj.test();
    }


    /**
     * Test enhancement of a non-top-level persistable that doesn't implement the
     * readObject method. 
     *
     * @throws Exception
     */
    public void testNTLPWithoutReadObject() throws Exception
    {
        // Enhancer should have added a readObject method to the superclass, but
        // NOT this class. This should throw.
        try {
            NTLPWithoutReadObject.class.getDeclaredMethod("readObject", new Class[] { ObjectInputStream.class } );
            fail("Expected Exception");
        }
        catch (NoSuchMethodException e) {
            // Expected
        }
        
        // PersistableHelper.initPersistable() in the super-class generated readObject
        // should be called on deserialization.
        NTLPWithoutReadObject testObj = (NTLPWithoutReadObject)createViaSerialization(new NTLPWithoutReadObject());
        
        // This should have been set.
        assertTrue(((Persistable)testObj).enerj_AllowsNonTransactionalRead());
        
        // Should be able to call the test() method without throwing an exception.
        testObj.test();
    }



    @Persist
    private static class TLPWithoutReadObject implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private int mField;
        
        void test()
        {
            mField = 1;
        }
    }



    @Persist
    private static class TLPWithReadObject implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private int mField;
        
        void test()
        {
            mField = 1;
        }
        
        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
        {
            // Do something.
            int x = ois.hashCode() + 5;
        }
    }



    @Persist
    private static class NTLPWithoutReadObject extends TLPWithoutReadObject implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private int mField;
        
        void test()
        {
            mField = 1;
        }
    }
}

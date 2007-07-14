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
// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/SchemaTest.java,v 1.3 2006/06/08 02:29:27 dsyrstad Exp $

package org.enerj.core;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.annotations.Persist;
import org.odmg.Database;
import org.odmg.Implementation;

/**
 * Tests Schema functionality.
 *
 * @version $Id: SchemaTest.java,v 1.3 2006/06/08 02:29:27 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class SchemaTest extends DatabaseTestCase
{
    public SchemaTest(String aTestName) 
    {
        super(aTestName);
    }

    public static Test suite() 
    {
        return new TestSuite(SchemaTest.class);
    }

    /**
     * Tests Schema.
     */
    public void testSchema() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        EnerJTransaction txn = (EnerJTransaction)impl.newTransaction();
        txn.begin();

        // Create some classes to add them to the schema.
        db.makePersistent( new TestClass1(1) );
        db.makePersistent( new TestClass2(2) );
        db.makePersistent( new TestClass3(3) );

        Schema schema = db.getSchema();
        
        // Test creation date
        // Sleep a bit so we're sure current time exceeds creation time
        try {  Thread.sleep(200L);  }  catch (Exception e) { }
        Date now = new Date();
        assertTrue("Creation date should before now", schema.getCreationDate().before(now) );
        Date anHourAgo = new Date( now.getTime() - (60L * 60L * 1000L) );
        assertTrue("Creation date should be after a reasonable date", schema.getCreationDate().after(anHourAgo) );

        // Make sure we can find our classes.
        checkTestClassExistsAndHasOneVersion(schema, TestClass1.class);
        checkTestClassExistsAndHasOneVersion(schema, TestClass2.class);
        checkTestClassExistsAndHasOneVersion(schema, TestClass3.class);
        
        // Test adding classes to the schema. These don't actually get stored in the database, we're 
        // just testing the methods.
        final String classPrefix = "org.enerj.somepkg.Class";
        final int numLogical = 10;
        for (int i = 0; i < numLogical; i++) {
            schema.addClassSchema( new ClassSchema(schema, classPrefix + i, "Description " + i) );
        }
        
        // Test findClassSchema().
        for (int i = 0; i < numLogical; i++) {
            ClassSchema classSchema = schema.findClassSchema(classPrefix + i);
            assertNotNull("class schema should not be null", classSchema);
            assertTrue("class scehma should be equal", classSchema.getClassName().equals(classPrefix + i) );
        }
        
        // Try adding a non-unique name.
        try {
            schema.addClassSchema( new ClassSchema(schema, classPrefix + 5, "Description " + 5) );
            fail("Should have thrown an Exception");
        }
        catch (org.odmg.ObjectNameNotUniqueException e) {
            // Expected
        }

        // Test getClassSchemas().
        Iterator iterator = schema.getClassSchemas().iterator();
        boolean[] checkList = new boolean[numLogical];
        int numFound = 0;
        boolean testClassFound = false;
        while ( iterator.hasNext() ) {
            ClassSchema classSchema = (ClassSchema)iterator.next();
            if (classSchema.getClassName().equals(TestClass1.class.getName())) {
                testClassFound = true;
                continue;
            }

            // Skip non-test classes
            String schemaClassName = classSchema.getClassName();
            if (schemaClassName.startsWith(classPrefix)) {
                // Trim off the classPrefix to get the index from the class name 
                String idxString = schemaClassName.substring( classPrefix.length() );
                int idx = Integer.parseInt(idxString);
                assertTrue("Idx should be in range", idx >=0 && idx < numLogical);
                assertTrue("Should not be checked yet", !checkList[idx]);
                checkList[idx] = true;
                ++numFound;
            }
            
        }
        
        assertTrue("Number found should be correct", numFound == numLogical);
        assertTrue("TestClass should have been found", testClassFound);
        

        // Test removeClassSchema().
        for (int i = 0; i < numLogical; i++) {
            schema.removeClassSchema(classPrefix + i);
            assertNull("Logical class should no longer exist", schema.findClassSchema(classPrefix + i) );

            // Try to remove it again
            try {
                schema.removeClassSchema(classPrefix + i);
                fail("Should have thrown an Exception");
            }
            catch (org.odmg.ObjectNameNotFoundException e) {
                // Expected
            }
        }
        
        // Test findClassVersion() and allocateClassId().
        ClassSchema classSchema = new ClassSchema(schema, this.getClass().getName(), "");
        long cid = ObjectSerializer.LAST_SYSTEM_CID + 99938;

        assertNull("CID shouldn't already exist", schema.findClassVersion(cid) );
        ClassVersionSchema classVersion = 
            new ClassVersionSchema(classSchema, cid, new String[0],
                new byte[0], new byte[0], new String[0], new String[0]);
        classSchema.addVersion(classVersion);
        
        classVersion = schema.findClassVersion(cid);
        assertNotNull("Version should exist", classVersion);
        assertTrue("CID should match", classVersion.getClassId() == cid);

        // TODO        test remove class version.

        txn.commit();
        db.close();
    }
    
    private void checkTestClassExistsAndHasOneVersion(Schema aSchema, Class aClass) throws Exception
    {
        ClassSchema classSchema = aSchema.findClassSchema(aClass.getName());

        assertSame(aSchema, classSchema.getSchema());
        assertEquals(aClass.getName(), classSchema.getClassName());
        
        ClassVersionSchema[] versions = classSchema.getVersions(); 
        assertEquals(1, versions.length);
        assertEquals(versions[0], classSchema.getLatestVersion());
        assertEquals(versions[0], classSchema.findVersion(versions[0].getClassId()));

        assertSame(classSchema, versions[0].getClassSchema());
        
        assertEquals(2, versions[0].getSuperTypeNames().length);
        List<String> superTypes = Arrays.asList(versions[0].getSuperTypeNames());
        assertTrue(superTypes.contains(Persistable.class.getName()));
        assertTrue(superTypes.contains(Object.class.getName()));

        assertEquals(1, versions[0].getPersistentFieldNames().length);
        assertEquals("mValue", versions[0].getPersistentFieldNames()[0] );

        assertEquals(1, versions[0].getTransientFieldNames().length);
        assertEquals("mTransient", versions[0].getTransientFieldNames()[0] );
    }
    
    private void checkClassSchema(ClassSchema aClassSchema) throws Exception
    {
        for (ClassVersionSchema version : aClassSchema.getVersions()) { 
            checkClassVersionSchema(version);
        }
    }

    private void checkClassVersionSchema(ClassVersionSchema aClassVersionSchema) throws Exception
    {
        // Test getOriginalBytecodes()
        byte[] bytecodes = aClassVersionSchema.getOriginalBytecodes();
        assertTrue("Original bytecodes should match", bytecodes.length == 1);
        assertTrue("byte[0] should match", bytecodes[0] == 0x01);
        

        // Test getEnhancedBytecodes()
        bytecodes = aClassVersionSchema.getEnhancedBytecodes();
        assertTrue("Enhanced bytecodes should match", bytecodes.length == 2);
        assertTrue("byte[0] should match", bytecodes[0] == 0x02);
        assertTrue("byte[1] should match", bytecodes[1] == 0x03);
        

        // Test getPersistentFieldNames()
        String[] fieldNames = aClassVersionSchema.getPersistentFieldNames();
        assertTrue("Field names should match", fieldNames.length == 3);
        assertTrue("fieldNames[0] should match", fieldNames[0].equals("A") );
        assertTrue("fieldNames[1] should match", fieldNames[1].equals("B") );
        assertTrue("fieldNames[2] should match", fieldNames[2].equals("C") );
        

        // Test getTransientFieldNames()
        fieldNames = aClassVersionSchema.getTransientFieldNames();
        assertTrue("Field names should match", fieldNames.length == 4);
        assertTrue("fieldNames[0] should match", fieldNames[0].equals("W") );
        assertTrue("fieldNames[1] should match", fieldNames[1].equals("X") );
        assertTrue("fieldNames[2] should match", fieldNames[2].equals("Y") );
        assertTrue("fieldNames[3] should match", fieldNames[3].equals("Z") );
        

        // Test get/setProxyBytecodes()
        assertNull("Current proxy bytecodes should be null", aClassVersionSchema.getProxyBytecodes() );
        aClassVersionSchema.setProxyBytecodes(new byte[] { 0x0a, 0x0b, 0x0c });
        bytecodes = aClassVersionSchema.getProxyBytecodes();
        assertTrue("Proxy bytecodes should match", bytecodes.length == 3);
        assertTrue("byte[0] should match", bytecodes[0] == 0x0a);
        assertTrue("byte[1] should match", bytecodes[1] == 0x0b);
        assertTrue("byte[2] should match", bytecodes[2] == 0x0c);
        
    }


    @Persist
    private static class TestClass1
    {
        transient int mTransient;
        private int mValue;
        

        TestClass1(int aValue)
        {
            mValue = aValue;
        }
            

        int getValue()
        {
            return mValue;
        }


        void setValue(int aValue)
        {
            mValue = aValue;
        }
    }


    @Persist
    private static class TestClass2
    {
        transient int mTransient;
        private int mValue;
        

        TestClass2(int aValue)
        {
            mValue = aValue;
        }
            

        int getValue()
        {
            return mValue;
        }


        void setValue(int aValue)
        {
            mValue = aValue;
        }
    }


    @Persist
    private static class TestClass3
    {
        private static int mTransient;
        private int mValue;
        

        TestClass3(int aValue)
        {
            mValue = aValue;
        }
            

        int getValue()
        {
            return mValue;
        }


        void setValue(int aValue)
        {
            mValue = aValue;
        }
    }
}

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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.Transaction;
import org.enerj.annotations.Persist;

import java.util.Date;
import java.util.Iterator;

/**
 * Tests DatabaseRoot and Schema functionality.
 *
 * @version $Id: SchemaTest.java,v 1.3 2006/06/08 02:29:27 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class SchemaTest extends TestCase
{
    private static final String DATABASE_URI = "enerj://root:root@-/SchemaTestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";
    
    //----------------------------------------------------------------------
    public SchemaTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(SchemaTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(SchemaTest.class);
    }

    //----------------------------------------------------------------------
    /**
     * Tests DatabaseRoot.
     */
    public void testDatabaseRoot() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        DatabaseRoot root = db.getDatabaseRoot();
        
        //----------------------------------------------------------------------
        // Test creation date
        // Sleep a bit so we're sure current time exceeds creation time
        try {  Thread.sleep(2L);  }  catch (Exception e) { }
        Date now = new Date();
        assertTrue("Creation date should before now", root.getCreationDate().before(now) );
        Date anHourAgo = new Date( now.getTime() - (60L * 60L * 1000L) );
        assertTrue("Creation date should after a reasonable date", root.getCreationDate().after(anHourAgo) );
        
        //----------------------------------------------------------------------
        // Test get/setDescription()
        final String desc = "A Database description";
        assertTrue("Current description should not be test description", !root.getDescription().equals(desc) );
        
        root.setDescription(desc);
        assertTrue("Description should be test description", root.getDescription().equals(desc) );
        
        //----------------------------------------------------------------------
        // Test getSchema().
        assertNotNull("Schema should be non-null", root.getSchema() );
        
        txn.commit();
        db.close();
    }

    //----------------------------------------------------------------------
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

        Schema schema = db.getDatabaseRoot().getSchema();
        
        //----------------------------------------------------------------------
        // Test creation date
        // Sleep a bit so we're sure current time exceeds creation time
        try {  Thread.sleep(2L);  }  catch (Exception e) { }
        Date now = new Date();
        assertTrue("Creation date should before now", schema.getCreationDate().before(now) );
        Date anHourAgo = new Date( now.getTime() - (60L * 60L * 1000L) );
        assertTrue("Creation date should after a reasonable date", schema.getCreationDate().after(anHourAgo) );
        
        //----------------------------------------------------------------------
        // Test get/setDescription()
        final String desc = "A Schema description";
        assertTrue("Current description should not be test description", !schema.getDescription().equals(desc) );
        
        schema.setDescription(desc);
        assertTrue("Description should be test description", schema.getDescription().equals(desc) );

        //----------------------------------------------------------------------
        // Test addLogicalClass(), findLogicalClass().
        final String classPrefix = "org.enerj.somepkg.Class";
        final int numLogical = 10;
        for (int i = 0; i < numLogical; i++) {
            schema.addLogicalClass( new LogicalClassSchema(schema, classPrefix + i, "Description " + i) );
        }
        
        for (int i = 0; i < numLogical; i++) {
            LogicalClassSchema logicalClass = schema.findLogicalClass(classPrefix + i);
            assertNotNull("Logical class should not be null", logicalClass);
            assertTrue("Logical class should be equal", logicalClass.getClassName().equals(classPrefix + i) );
        }
        
        // Try adding a non-unique name.
        try {
            schema.addLogicalClass( new LogicalClassSchema(schema, classPrefix + 5, "Description " + 5) );
            fail("Should have thrown an Exception");
        }
        catch (org.odmg.ObjectNameNotUniqueException e) {
            // Expected
        }
        
        // Create a TestClass and make it persistent so that it gets added to the schema. We have to flush
        // to force this to happen now.
        db.makePersistent( new TestClass(1) );
        txn.flush();
        

        //----------------------------------------------------------------------
        // Test getLogicalClasses().
        Iterator iterator = schema.getLogicalClasses().iterator();
        boolean[] checkList = new boolean[numLogical];
        int numFound = 0;
        boolean testClassFound = false;
        while ( iterator.hasNext() ) {
            LogicalClassSchema logicalClass = (LogicalClassSchema)iterator.next();
            if (logicalClass.getClassName().equals("org.enerj.core.SchemaTest$TestClass")) {
                testClassFound = true;
                continue;
            }

            // Skip non-test classes
            String logicalClassName = logicalClass.getClassName();
            if (logicalClassName.startsWith(classPrefix)) {
                // Trim off the classPrefix to get the index from the class name 
                String idxString = logicalClassName.substring( classPrefix.length() );
                int idx = Integer.parseInt(idxString);
                assertTrue("Idx should be in range", idx >=0 && idx < numLogical);
                assertTrue("Should not be checked yet", !checkList[idx]);
                checkList[idx] = true;
                ++numFound;
            }
            
        }
        
        assertTrue("Number found should be correct", numFound == numLogical);
        assertTrue("TestClass should have been found", testClassFound);
        
        //----------------------------------------------------------------------
        // Test removeLogicalClass().
        for (int i = 0; i < numLogical; i++) {
            schema.removeLogicalClass(classPrefix + i);
            assertNull("Logical class should no longer exist", schema.findLogicalClass(classPrefix + i) );

            // Try to remove it again
            try {
                schema.removeLogicalClass(classPrefix + i);
                fail("Should have thrown an Exception");
            }
            catch (org.odmg.ObjectNameNotFoundException e) {
                // Expected
            }
        }
        
        iterator = schema.getLogicalClasses().iterator();
        boolean found = false;
        while ( iterator.hasNext() ) {
            LogicalClassSchema lClass = (LogicalClassSchema)iterator.next();
            // Skip System classes
            String lClassName = lClass.getClassName();
            if (lClassName.equals("org.enerj.core.SchemaTest$TestClass")) {
                found = true;
                break;
            }
        }

        assertTrue("Test class should exist", found);

        //----------------------------------------------------------------------
        // Test findClassVersion() and allocateClassId().
        LogicalClassSchema logicalClass = new LogicalClassSchema(schema, this.getClass().getName(), "");
        long cid = ObjectSerializer.LAST_SYSTEM_CID + 99938;

        assertNull("CID shouldn't already exist", schema.findClassVersion(cid) );
        ClassVersionSchema classVersion = 
            new ClassVersionSchema(logicalClass, cid, new String[0],
                new byte[0], new byte[0], new String[0], new String[0]);
        logicalClass.addVersion(classVersion);
        
        classVersion = schema.findClassVersion(cid);
        assertNotNull("Version should exist", classVersion);
        assertTrue("CID should match", classVersion.getClassId() == cid);

        txn.commit();
        db.close();
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests LogicalClassSchema.
     */
    public void testLogicalClassSchema() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        Schema schema = db.getDatabaseRoot().getSchema();

        //----------------------------------------------------------------------
        // Test creation date
        LogicalClassSchema logicalClass = schema.findLogicalClass("org.enerj.core.SchemaTest$TestClass");
        // Sleep a bit so we're sure current time exceeds creation time
        try {  Thread.sleep(2L);  }  catch (Exception e) { }
        Date now = new Date();
        assertTrue("Creation date should before now", logicalClass.getCreationDate().before(now) );
        Date anHourAgo = new Date( now.getTime() - (60L * 60L * 1000L) );
        assertTrue("Creation date should after a reasonable date", logicalClass.getCreationDate().after(anHourAgo) );

        //----------------------------------------------------------------------
        // Test get/setDescription()
        final String desc = "A Class description";
        assertTrue("Current description should not be test description", !logicalClass.getDescription().equals(desc) );
        
        logicalClass.setDescription(desc);
        assertTrue("Description should be test description", logicalClass.getDescription().equals(desc) );

        //----------------------------------------------------------------------
        // Test getSchema()
        assertTrue("Schema should match", logicalClass.getSchema() == schema);
        
        //----------------------------------------------------------------------
        // Test getClassName()
        assertTrue("Class name should match", logicalClass.getClassName().equals("org.enerj.core.SchemaTest$TestClass") );
        
        //----------------------------------------------------------------------
        // Test addVersion()

        logicalClass = new LogicalClassSchema(schema, "VersionTest", "");
        schema.addLogicalClass(logicalClass);

        // This is a class with no versions for testing.
        LogicalClassSchema logicalClass2 = new LogicalClassSchema(schema, "VersionTest2", "");
        schema.addLogicalClass(logicalClass2);

        final int numVersions = 10;
        long[] cids = new long[numVersions];
        long cidCounter = ObjectSerializer.LAST_SYSTEM_CID;
        for (int i = 0; i < numVersions; i++) {
            cids[i] = cidCounter++;
            ClassVersionSchema version = new ClassVersionSchema(logicalClass, cids[i],  new String[0],
                new byte[0], new byte[0], new String[0], new String[0]);
            logicalClass.addVersion(version);
            
            // Try to add it again - should get exception
            try {
                logicalClass.addVersion(version);
                fail("Expected an Exception");
            }
            catch (org.odmg.ObjectNameNotUniqueException e) {
                // Expected
            }
        }

        //----------------------------------------------------------------------
        // Test getVersions()
        ClassVersionSchema[] versions = logicalClass.getVersions();
        for (int i = 0; i < numVersions; i++) {
            assertTrue("Version CID should match", versions[i].getClassId() == cids[i]);
        }
        
        assertTrue("No Versions should exist", logicalClass2.getVersions().length == 0);
        
        //----------------------------------------------------------------------
        // Test findVersion()
        for (int i = 0; i < numVersions; i++) {
            ClassVersionSchema version = logicalClass.findVersion(cids[i]);
            assertTrue("Version CID should match", version.getClassId() == cids[i]);
        }

        long missingCID = ObjectSerializer.LAST_SYSTEM_CID - 1;
        assertNull("CID should not exist", logicalClass.findVersion(missingCID) );

        //----------------------------------------------------------------------
        // Test getLatestVersion()
        ClassVersionSchema version = logicalClass.getLatestVersion();
        assertTrue("Version CID should match", version.getClassId() == cids[ numVersions - 1] );
        
        assertNull("No versions should exist", logicalClass2.getLatestVersion() );

        //----------------------------------------------------------------------
        // Test removeVersion()
        for (int i = 0; i < numVersions; i++) {
            logicalClass.removeVersion(cids[i]);
            
            assertNull("Version should not exist", logicalClass.findVersion(cids[i]) );
            
            // Try to remove it again
            try {
                logicalClass.removeVersion(cids[i]);
                fail("Expected an Exception");
            }
            catch (org.odmg.ObjectNameNotFoundException e) {
                // Expected
            }
        }
        
        // Cleanup
        schema.removeLogicalClass("VersionTest");
        schema.removeLogicalClass("VersionTest2");

        txn.commit();
        db.close();
    }

    //----------------------------------------------------------------------
    /**
     * Tests ClassVersionSchema.
     */
    public void testClassVersionSchema() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        Schema schema = db.getDatabaseRoot().getSchema();
        LogicalClassSchema logicalClass = new LogicalClassSchema(schema, "VersionTest", "");
        schema.addLogicalClass(logicalClass);

        long versionCID = ObjectSerializer.LAST_SYSTEM_CID + 2928;
        ClassVersionSchema version = new ClassVersionSchema(logicalClass, versionCID, new String[0],
            new byte[] { 0x01 }, new byte[] { 0x02, 0x03 },
            new String[] { "A", "B", "C" }, 
            new String[] { "W", "X", "Y", "Z" } );
        logicalClass.addVersion(version);

        //----------------------------------------------------------------------
        // Test creation date
        // Sleep a bit so we're sure current time exceeds creation time
        try {  Thread.sleep(1000L);  }  catch (Exception e) { }
        Date now = new Date();
        assertTrue("Creation date should before now", version.getCreationDate().before(now) );
        Date anHourAgo = new Date( now.getTime() - (60L * 60L * 1000L) );
        assertTrue("Creation date should after a reasonable date", version.getCreationDate().after(anHourAgo) );

        //----------------------------------------------------------------------
        // Test getClassId()
        assertTrue("CID should match", version.getClassId() == versionCID);

        //----------------------------------------------------------------------
        // Test getLogicalClassSchema()
        assertTrue("LogicalClass should match", version.getLogicalClassSchema() == logicalClass);
        
        //----------------------------------------------------------------------
        // Test getOriginalBytecodes()
        byte[] bytecodes = version.getOriginalBytecodes();
        assertTrue("Original bytecodes should match", bytecodes.length == 1);
        assertTrue("byte[0] should match", bytecodes[0] == 0x01);
        
        //----------------------------------------------------------------------
        // Test getEnhancedBytecodes()
        bytecodes = version.getEnhancedBytecodes();
        assertTrue("Enhanced bytecodes should match", bytecodes.length == 2);
        assertTrue("byte[0] should match", bytecodes[0] == 0x02);
        assertTrue("byte[1] should match", bytecodes[1] == 0x03);
        
        //----------------------------------------------------------------------
        // Test getPersistentFieldNames()
        String[] fieldNames = version.getPersistentFieldNames();
        assertTrue("Field names should match", fieldNames.length == 3);
        assertTrue("fieldNames[0] should match", fieldNames[0].equals("A") );
        assertTrue("fieldNames[1] should match", fieldNames[1].equals("B") );
        assertTrue("fieldNames[2] should match", fieldNames[2].equals("C") );
        
        //----------------------------------------------------------------------
        // Test getTransientFieldNames()
        fieldNames = version.getTransientFieldNames();
        assertTrue("Field names should match", fieldNames.length == 4);
        assertTrue("fieldNames[0] should match", fieldNames[0].equals("W") );
        assertTrue("fieldNames[1] should match", fieldNames[1].equals("X") );
        assertTrue("fieldNames[2] should match", fieldNames[2].equals("Y") );
        assertTrue("fieldNames[3] should match", fieldNames[3].equals("Z") );
        
        //----------------------------------------------------------------------
        // Test get/setProxyBytecodes()
        assertNull("Current proxy bytecodes should be null", version.getProxyBytecodes() );
        version.setProxyBytecodes(new byte[] { 0x0a, 0x0b, 0x0c });
        bytecodes = version.getProxyBytecodes();
        assertTrue("Proxy bytecodes should match", bytecodes.length == 3);
        assertTrue("byte[0] should match", bytecodes[0] == 0x0a);
        assertTrue("byte[1] should match", bytecodes[1] == 0x0b);
        assertTrue("byte[2] should match", bytecodes[2] == 0x0c);
        

        // Cleanup
        schema.removeLogicalClass("VersionTest");

        txn.commit();
        db.close();
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    @Persist
    private static class TestClass
    {
        private int mValue;
        
        //----------------------------------------------------------------------
        TestClass(int aValue)
        {
            mValue = aValue;
        }
            
        //----------------------------------------------------------------------
        int getValue()
        {
            return mValue;
        }

        //----------------------------------------------------------------------
        void setValue(int aValue)
        {
            mValue = aValue;
        }
    }
}

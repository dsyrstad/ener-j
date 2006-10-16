// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/MetadataTest.java,v 1.5 2006/07/28 23:53:41 dsyrstad Exp $

package org.enerj.enhancer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.enerj.core.Persistable;
import org.enerj.util.asm.ClassReflector;

/**
 * Tests miscellaneous Metadata functionality.
 *
 * @version $Id: MetadataTest.java,v 1.5 2006/07/28 23:53:41 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class MetadataTest extends TestCase 
{
    private static final String DATABASE_URI = "enerj://root:root@-/MetadataTestDB?DefaultMetaObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";

    private static boolean sEnhanced = false;
    
    //----------------------------------------------------------------------
    public MetadataTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(MetadataTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(MetadataTest.class);
    }
    
    //----------------------------------------------------------------------
    /**
     * Syntax error tests.
     */
    public void testSyntaxErrors() throws Exception
    {
        try {
            TestHelper.enhance("MetadataTest1.meta"); 
            fail("Should have thrown an Exception");
        }
        catch (Exception e) {
            Exception enhancerException = Enhancer.getLastRunEnhancerException();
            assertNotNull("Exception should be non-null", enhancerException);
            assertTrue("Message should contain errant line number (6)", 
                enhancerException.getMessage().indexOf(" 6:") >= 0);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Test bad keys for class and field.
     */
    public void testBadKeys() throws Exception
    {
        // Errors line numbers are actually reported at the end of the block of key/value pairs
        // Bad class key
        try {
            TestHelper.enhance("MetadataTest2.meta"); 
            fail("Should have thrown an Exception");
        }
        catch (Exception e) {
            Exception enhancerException = Enhancer.getLastRunEnhancerException();
            assertNotNull("Exception should be non-null", enhancerException);
            assertTrue("Message should contain errant line number (5)", 
                enhancerException.getMessage().indexOf(" 5:") >= 0);
        }

        // Bad field key
        try {
            TestHelper.enhance("MetadataTest3.meta"); 
            fail("Should have thrown an Exception");
        }
        catch (Exception e) {
            Exception enhancerException = Enhancer.getLastRunEnhancerException();
            assertNotNull("Exception should be non-null", enhancerException);
            assertTrue("Message should contain errant line number (10)", 
                enhancerException.getMessage().indexOf(" 10:") >= 0);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Test bad key values for class and field.
     */
    public void testBadKeyValues() throws Exception
    {
        // Errors line numbers are actually reported at the line after the end of the block of key/value pairs
        // Bad class key values
        try {
            TestHelper.enhance("MetadataTest4.meta"); 
            fail("Should have thrown an Exception");
        }
        catch (Exception e) {
            Exception enhancerException = Enhancer.getLastRunEnhancerException();
            assertNotNull("Exception should be non-null", enhancerException);
            assertTrue("Message should contain errant line number (8)", 
                enhancerException.getMessage().indexOf(" 8:") >= 0);
        }

        // Bad field key values
        try {
            TestHelper.enhance("MetadataTest5.meta"); 
            fail("Should have thrown an Exception");
        }
        catch (Exception e) {
            Exception enhancerException = Enhancer.getLastRunEnhancerException();
            assertNotNull("Exception should be non-null", enhancerException);
            assertTrue("Message should contain errant line number (16)", 
                enhancerException.getMessage().indexOf(" 16:") >= 0);
        }
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Checks that SimpleTestClass and SimpleTestClass2 are Persistables after enhancement.
     * We purposely avoid using Java to load the classes because the enhancer may have modified
     * them after they were loaded.  
     *
     * @throws Exception
     */
    private void checkForPersistableSimpleTestClasses() throws Exception
    {
        String persistableName = Persistable.class.getName();
        assertTrue("SimpleTestClass must be Persistable", new ClassReflector("org.enerj.enhancer.subpackage.SimpleTestClass").containsSuperInterface(persistableName) );
        assertTrue("SimpleTestClass2 must be Persistable", new ClassReflector("org.enerj.enhancer.subpackage.SimpleTestClass2").containsSuperInterface(persistableName) );
    }
    
    //----------------------------------------------------------------------
    /**
     * Test "class *" defaults.
     */
    public void testClassDefaults() throws Exception
    {
        // Test "class *" defaults - which should be persistent=capable.
        // This should not throw an Exception
        TestHelper.enhance("subpackage/MetadataTestClassDefaults.meta"); 
        
        checkForPersistableSimpleTestClasses();
    }
    
    //----------------------------------------------------------------------
    /**
     * Test "class {packagename}" defaults.
     */
    public void testPackageDefaults() throws Exception
    {
        // Test "class *" defaults - which should be persistent=capable.
        // This should not throw an Exception
        TestHelper.enhance("subpackage/MetadataTestPackageDefaults.meta"); 
        
        checkForPersistableSimpleTestClasses();
        
        // This should not have been enhanced.
        assertFalse("TestClassNever must not be Persistable", 
            new ClassReflector("org.enerj.enhancer.MetadataTest$TestClassNever").containsSuperInterface(Persistable.class.getName()) );

        // This interface should not have been enhanced (interfaces never are), even though
        // it was in the specified package. It should not have the Persistable interface.
        assertFalse("SimpleTestInterface must not be Persistable", 
                        new ClassReflector("org.enerj.enhancer.subpackage.SimpleTestInterface").containsSuperInterface(Persistable.class.getName()) );
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Test class for Metadata.
     */
    private static class TestClass
    {
        private int mInt;

        //----------------------------------------------------------------------
        TestClass()
        {
        }
    }   

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Test class for Metadata. NEVER gets enhanced.
     */
    private static class TestClassNever
    {
        //----------------------------------------------------------------------
        TestClassNever()
        {
        }
    }   
}

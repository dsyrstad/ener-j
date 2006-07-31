// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/MetadataTest.java,v 1.5 2006/07/28 23:53:41 dsyrstad Exp $

package org.enerj.enhancer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.enerj.core.Persistable;

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
    
    //----------------------------------------------------------------------
    /**
     * Test "class *" defaults.
     */
    public void testClassDefaults() throws Exception
    {
        // Test "class *" defaults - which should be persistent=capable.
        // This should not throw an Exception
        TestHelper.enhance("subpackage/MetadataTestClassDefaults.meta"); 
        
        // The two classes should implement Persistable, so the following casts should work.
        Persistable p1 = (Persistable)(Object)new org.enerj.enhancer.subpackage.SimpleTestClass();
        Persistable p2 = (Persistable)(Object)new org.enerj.enhancer.subpackage.SimpleTestClass2();
    }
    
    //----------------------------------------------------------------------
    /**
     * Test "class {packagename}" defaults.
     */
    public void testPackageDefaults() throws Exception
    {
        // Test "class *" defaults - which should be persistent=capable.
        // This should not throw an Exception
        TestHelper.enhance("subpackage/MetadataTestClassDefaults.meta"); 
        
        // The two classes should implement Persistable, so the following casts should work.
        Persistable p1 = (Persistable)(Object)new org.enerj.enhancer.subpackage.SimpleTestClass();
        Persistable p2 = (Persistable)(Object)new org.enerj.enhancer.subpackage.SimpleTestClass2();
        
        // This should not have been enhanced. It should throw ClassCastException
        try {
            Persistable p3 = (Persistable)(Object)new org.enerj.enhancer.MetadataTest.TestClassNever();
            fail("ClassCastException expected - class should not be enhanced");
        }
        catch (ClassCastException e) {
            // Ok
        }

        // This interface should not have been enhanced (interfaces never are), even though
        // it was in the specified package. It should not have the Persistable interface.
        Class[] interfaces = org.enerj.enhancer.subpackage.SimpleTestInterface.class.getInterfaces();
        assertTrue("Interface should not be enhanced", interfaces.length == 0);
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

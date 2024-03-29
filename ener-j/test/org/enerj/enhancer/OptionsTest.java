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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/OptionsTest.java,v 1.6 2006/07/28 23:53:41 dsyrstad Exp $

package org.enerj.enhancer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.enerj.core.Persistable;

/**
 * Tests enhancer options.
 *
 * @version $Id: OptionsTest.java,v 1.6 2006/07/28 23:53:41 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class OptionsTest extends TestCase 
{

    public OptionsTest(String aTestName) 
    {
        super(aTestName);
    }

    public static Test suite() 
    {
        return new TestSuite(OptionsTest.class);
    }
    

    /**
     * Test that a warning is generated when no classes are enhanced.
     */
    public void testNoClassesEnhancedWarning() throws Exception
    {
        TestHelper.enhance("OptionsTest1.meta");
        
        // Warning should be generated, but enhancement should not fail.
        Exception lastException = Enhancer.getLastRunEnhancerException();
        assertNull(lastException);
    }
    

    /**
     * Test that zero metadata files specified generates an error.
     */
    public void testNoMetadataFiles() throws Exception
    {
        int returnCode = Enhancer.runEnhancer(new String[] { 
                "--outdir", "/yyy", 
            } );
        assertTrue("No metadata files should not generate error", returnCode == 0);
    }
    

    /**
     * Test --help.
     */
    public void testHelp() throws Exception
    {
        int returnCode = Enhancer.runEnhancer(new String[] { "--help", } );
        assertTrue("--help should generate error return code", returnCode != 0);
    }
    

    /**
     * Test no options.
     */
    public void testNoOptions() throws Exception
    {
        int returnCode = Enhancer.runEnhancer(new String[0] );
        assertTrue("no options should generate error return code", returnCode != 0);
    }
    

    /**
     * Test invalid options.
     */
    public void testInvalidOption() throws Exception
    {
        int returnCode = Enhancer.runEnhancer(new String[] { "-xyzzy" } );
        assertTrue("Bad option should generate error return code", returnCode != 0);

        returnCode = Enhancer.runEnhancer(new String[] { "--outdir" } );
        assertTrue("Missing --outdir param should generate error return code", returnCode != 0);
    }
    

    /**
     * Test nonexistent metadata file.
     */
    public void testNonExistentMetadataFile() throws Exception
    {
        String badFileName = "xyzzyMetadataFile";
        int returnCode = Enhancer.runEnhancer(new String[] { "--outdir", "/x", badFileName } );
        assertTrue("Missing metadata file should generate error return code", returnCode != 0);
        Exception lastException = Enhancer.getLastRunEnhancerException();
        Throwable cause = lastException.getCause();
        assertTrue("Filename should be in the message", 
            cause.getMessage().indexOf(badFileName) >= 0);
    }
    

    /**
     * Tests multiple metadata files.
     */
    public void testMultipleMetaDataFiles() throws Exception
    {
        TestHelper.enhance( new String[] { "OptionsTest2.meta", "OptionsTest3.meta", "OptionsTest4.meta" });

        // The two classes should implement Persistable, so the following casts should work.
        Persistable p1 = (Persistable)(Object)new org.enerj.enhancer.OptionsTest.TestClass();
        Persistable p2 = (Persistable)(Object)new org.enerj.enhancer.OptionsTest.TestClass2();
        Persistable p3 = (Persistable)(Object)new org.enerj.enhancer.OptionsTest.TestClass3();
    }



    /**
     * Test class for Options.
     */
    private static class TestClass
    {
        private int mInt;


        TestClass()
        {
        }
    }   



    /**
     * Test class for Options.
     */
    private static class TestClass2
    {
        private int mInt;


        TestClass2()
        {
        }
    }   



    /**
     * Test class for Options.
     */
    private static class TestClass3
    {
        private int mInt;


        TestClass3()
        {
        }
    }   
}

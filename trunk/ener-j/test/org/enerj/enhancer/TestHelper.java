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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/TestHelper.java,v 1.5 2006/06/09 02:39:16 dsyrstad Exp $

package org.enerj.enhancer;

import java.io.File;

import org.enerj.util.FileUtil;

/**
 * Helper for the Enhancer tests.
 *
 * @version $Id: TestHelper.java,v 1.5 2006/06/09 02:39:16 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class TestHelper
{

    /**
     * Get the specified classes enhanced.
     * The source class files for the enhancer are expected to be in 
     * {somebasedirectory)/classes/{package}/.
     * The enhanced classes directory must be in the classpath before the
     * unenhanced classes. The enhanced classes directory must be named "testEnhancedClasses"
     * and it must be a peer with the source classes directory.
     * The test code directory containing the metadata file must be in the "test"
     * directory, which is on peer with the enhanced classes directory, under the 
     * package of this class.
     *
     * @param aMetaDataName the base name of the metadata, assumed to be in the
     *  same package as the classes.
     *
     * @throws Exception in the event of an error.
     */
    public static void enhance(String aMetaDataName) throws Exception
    {
        enhance(new String[] { aMetaDataName });
    }
    

    /**
     * Get the specified classes enhanced.
     * The source class files for the enhancer are expected to be in 
     * {somebasedirectory)/classes/{package}/.
     * The enhanced classes directory must be in the classpath before the
     * unenhanced classes. The enhanced classes directory must be named "testEnhancedClasses"
     * and it must be a peer with the source classes directory.
     * The test code directory containing the metadata file must be in the "test"
     * directory, which is on peer with the enhanced classes directory, under the 
     * package of this class.
     *
     * @param someMetaDataNames the base names of the metadata, assumed to be in the
     *  same package as the classes.
     *
     * @throws Exception in the event of an error.
     */
    public static void enhance(String[] someMetaDataNames) throws Exception
    {
        // Find the directory of the first class. This will be our sourcepath.
        String classDir = "." + File.separatorChar + "testClasses";
        String baseDir = FileUtil.getParentDirectory(classDir) + File.separatorChar;

        String packageName = TestHelper.class.getPackage().getName();

        // Metadata file is in "test" directory, a peer to sourceDir, under same package as the class.
        String packageDir = packageName.replace('.', File.separatorChar);
        String metaDataFilePrefix = baseDir + "test" + File.separatorChar + packageDir + File.separatorChar;
        
        // Run the enhancer
        String destDir = baseDir + "testClasses";
        
        int addlArgs = 2;
        String[] options = new String[ someMetaDataNames.length + addlArgs ];
        options[0] = "--outdir";
        options[1] = destDir;
        
        for (int i = 0; i < someMetaDataNames.length; i++) {
            options[i + addlArgs] = metaDataFilePrefix + someMetaDataNames[i];
        }
        
        for (String option : options) {
            System.out.println("Enhancer option: " + option);
        }
        
        int returnCode = Enhancer.runEnhancer(options);
        if (returnCode != 0) {
            throw new Exception("Expected zero return code");
        }
    }
}


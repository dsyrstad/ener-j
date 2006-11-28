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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/Enhancer.java,v 1.14 2006/06/09 02:39:41 dsyrstad Exp $

package org.enerj.enhancer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.enerj.core.EnerJTransaction;
import org.enerj.util.ClassUtil;



/**
 * Ener-J class file enhancer.
 * Try "java .... org.enerj.enhancer.Enhancer --help"
 * for usage information.
 *
 * @version $Id: Enhancer.java,v 1.14 2006/06/09 02:39:41 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class Enhancer 
{
    private static Exception sLastException = null;
    
    private boolean mDebug = false;
    private MetaData mMetaData;
    private String mOutputDir = null;
    private ByteArrayOutputStream mByteStream = null;
    private int mNumClasses = 0;
    private long mElapsedTotal = 0;


    /**
     * Construct an instance of the Enhancer.
     */
    public Enhancer()
    {
    }


    public static void main(String[] args) throws Exception
    {
        int rc = runEnhancer(args);
        if (rc == 0 || System.getProperty("ANT") == null) {
            System.exit(rc);
        }
        else if (rc != 0) {
            throw new Exception("ANT: Enhancer exiting with return code " + rc);
        }
    }
    

    /**
     * A wrapper for main() so that this can be called without System.exit()
     * being called.
     *
     * @return the exit status, 0 if good, non-zero if bad.
     */
    public static int runEnhancer(String[] args)
    {
        sLastException = null;
        Enhancer enhancer = new Enhancer();

        List<String> propFiles = new LinkedList<String>();

        // Parse command line arguments.
        for (int i = 0; i < args.length; i++) {
            if (args[i].length() > 0 && args[i].charAt(0) == '-') {  // command line switch
                if (args[i].equals("--outdir")) {
                    ++i;
                    if (i >= args.length) {
                        System.err.println("Missing argument to --outdir");
                        return 1;
                    }
                    
                    enhancer.mOutputDir = args[i];
                }
                else if (args[i].equals("--debug")) {
                    enhancer.mDebug = true;
                }
                else if (args[i].equals("--help")) {
                    System.out.println("Ener-J Enhancer - Copyright 2001-2006 Visual Systems Corporation");
                    System.out.println( "Usage: " + Enhancer.class.getName() + " --outdir directory [options] metadata-file...");
                    System.out.println("  --outdir directory = the directory where .class files are written.");
                    System.out.println("      This may be the same as one of directories in the Java classpath.");
                    System.out.println("      The Java classpath must consist of directory names or JAR files for");
                    System.out.println("      classes to be found by package or wildcard.");
                    System.out.println("  --help = Prints this help text.");
                    System.out.println("  --debug = turns on debugging messages.");
                    System.out.println("");
                    System.out.println("  metadata-file... = one or more Ener-J metadata files.");
                    return 1;
                }
                else {
                    System.err.println("Unknown option " + args[i]);
                    System.err.println("Try --help for options.");
                    return 1;
                }
            }
            else {
                propFiles.add(args[i]);
            }
        }

        if (enhancer.mOutputDir == null) {
            System.err.println("Missing --outdir option");
            return 1;
        }

        if (propFiles.size() == 0) {
          System.err.println("No metadata files specified");
          return 1;
        }
        
        // TODO refactor the nasties below
        int returnCode = 0;
        EnerJTransaction txn = null;
        try {
            enhancer.mMetaData = new MetaData(propFiles);
        }
        catch (Exception e) {
            sLastException = e;
            System.err.println("Error parsing metadata:");
            if (e.getClass() == Exception.class) {
                System.err.println(e.getMessage());
            }
            else {
                System.err.println(e.toString());
            }
            
            if (enhancer.mDebug) {
                e.printStackTrace();
            }
            
            returnCode = 1;
        }
        catch (Throwable t) {
            t.printStackTrace();
            returnCode = 1;
        }

        if (returnCode == 0) {
            try {
                enhancer.enhanceClasses();
            }
            catch (Exception e) {
                sLastException = e;
                System.err.println("Error while enhancing: ");
                e.printStackTrace();
                if (e.getClass() == Exception.class) {
                    System.err.println(e.getMessage());
                }
                else {
                    System.err.println(e.toString());
                }

                if (enhancer.mDebug) {
                    e.printStackTrace();
                }

                returnCode = 1;
            }
            catch (Throwable t) {
                t.printStackTrace();
                returnCode = 1;
            }
        }
        
        return returnCode;
    }


    /**
     * Get the last exception caught by runEnhancer. May be null.
     */
    public static Exception getLastRunEnhancerException()
    {
        return sLastException;
    }
    

    /**
     * Enhance all classes specified by the metadata.
     *
     * @throws Exception if an error occurs.
     */
    void enhanceClasses() throws Exception
    {
        long startAll = System.currentTimeMillis();

        // Recurse thru the classpath. We only handle directories and JAR files.
        String classpath = System.getProperty("java.class.path");
        StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            String component = tokenizer.nextToken();
            File base;
            String lcComponent = component.toLowerCase(); 
            if (lcComponent.endsWith(".jar") || lcComponent.endsWith(".zip")) {
            	// TODO We don't support scanning JAR files yet. JARs *should* be treated like directories by File...
            	/*
            	component = component.replace(File.separatorChar, '/');
                URI jarURI = new URI("jar:file:/" + component + "!/");
                */
            	continue;
            }
            else {
                base = new File(component);
            }
            
            findAndEnhanceClasses(base.getAbsolutePath(), base);
        }

        if (mNumClasses == 0) {
            // This exception is saved soley so the test classes can test this condition.
            sLastException = new Exception("Warning: No classes were enhanced.");
            System.err.println("Warning: No classes were enhanced.");
        }
        
        long elapsedAll = System.currentTimeMillis() - startAll;
        System.out.println("Enhanced " + mNumClasses + " classes. Total time: " + 
            elapsedAll + "ms. Avg Time per class: " + mElapsedTotal / ((mNumClasses > 0) ? mNumClasses : 1));
    }
    

    /**
     * Recursively scan aDirectory for files to scan.
     *
     * @param aDirectory the directory to recursively scan.
     *
     * @throws Exception if an error occurs.
     */
    private void findAndEnhanceClasses(String aBaseDirectory, File aDirectory) throws Exception
    {
        if (!aDirectory.isDirectory()) {
            return;
        }
        
        File[] files = aDirectory.listFiles();
        if (files == null) {
            return;
        }
        
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String fullPath = files[i].getAbsolutePath();
                if (fullPath.endsWith(".class")) {
                    // "- 6" strips off ".class". Then strip off leading source directory, "+ 1" skips separator.
                    // Then convert all separators to '.'
                    String className = fullPath.substring(0, fullPath.length() - 6).
                            substring(aBaseDirectory.length() + 1).
                            replace(File.separatorChar, '.');
                    if (mMetaData.isClassEnhanceable(className, null)) {
                        long start = System.currentTimeMillis();
                        enhance(className);
                        long elapsed = System.currentTimeMillis() - start;
                        mElapsedTotal += elapsed;
                        System.out.println("Time to enhance: " + elapsed + "ms");
                        ++mNumClasses;
                    }
                }
            }
            else if (files[i].isDirectory()) {
                // A directory, recurse
                findAndEnhanceClasses(aBaseDirectory, files[i]);
            }
        }
    }


    /**
     * Enhance the named class.
     *
     * @param aClassName the class name to be enhanced.
     *
     * @throws Exception if an error occurs.
     */
    private void enhance(String aClassName) throws Exception
    {
        // Read the bytecode for the class.
        byte[] originalClassBytes = ClassUtil.getBytecode(aClassName);

        ClassReader classReader = new ClassReader(originalClassBytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassEnhancer classEnhancer;
        try {
            classEnhancer = new ClassEnhancer(classWriter, aClassName, originalClassBytes, mMetaData);
            classReader.accept(classEnhancer, null, 0);
        }
        catch (AlreadyEnhancedException e) {
            System.out.println( e.getLocalizedMessage() );
            return;
        }
        catch (SkipEnhancementException e) {
            return;
        }
        //catch (EnhancerException e) -- let it throw out of method.

        String destPathToClass = mOutputDir + File.separator + aClassName.replace('.', '/') + ".class";
        byte[] newClassBytes = classWriter.toByteArray();
        
        System.out.println("Writing enhanced class to: " + destPathToClass);
        // Create directory structure if necessary. 
        new File(destPathToClass).getParentFile().mkdirs();
        FileOutputStream outStream = new FileOutputStream(destPathToClass);
        outStream.write(newClassBytes);
        outStream.close();
    }

}

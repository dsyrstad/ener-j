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
// Copyright 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/DatabaseClassLoader.java,v 1.2 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import java.util.*;
import java.io.*;

import org.odmg.*;

/**
 * Loads enhanced classes from a Ener-J database.
 * <p>
 * Classes in the database and found by class name and class Id (CID). 
 * The CID determines the version of the class with which the client application was compiled.
 * The CID comes from a "classname.classid" file in the local classpath. This
 * file is loaded as a resource using the parent ClassLoader. If there is no ".classid" 
 * resource for the given class, the class is loaded using the parent loader. 
 * If there is a ".classid" file but no corresponding classname/CID can be found 
 * in the database, a ClassNotFoundException is thrown. If a Transaction is not active
 * when an attempt is made to load from the database, a temporary transaction is 
 * used.
 * <p>
 * The database used to load the class is detected based on the following search,
 * in order: <p>
 * 1. If there is an open transaction, the database associated with the transaction is used.<p>
 * 2. EnerJDatabase.getCurrentDatabase is called. This method first calls getCurrentDatabaseForThread.
 * Failing that, getCurrentDatabaseForProcess is called. <p>
 * 3. If no database is found, but we do have a CID, a ClassNotFoundException is thrown
 * (i.e., loading is not delegated to the parent ClassLoader).<p>
 * <p>
 *  TODO  The above behavior may cause problems if we need to support multiple
 *  Databases open in a single thread.
 *  TODO  NOT USED CURRENTLY.... Will be used for schema evolution.
 *
 * @version $Id: DatabaseClassLoader.java,v 1.2 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class DatabaseClassLoader extends ClassLoader
{
    /** Number of bytes read at a time during parent class loading. */
    private static final int CHUNK_SIZE = 8192;
    
    //----------------------------------------------------------------------
    /**
     * Constructs a new DatabaseClassLoader. 
     *
     * @param aParentLoader the loader which is delegated to if we cannot
     *  find the class in the database.
     */
    public DatabaseClassLoader(ClassLoader aParentLoader)
    {
        super(aParentLoader);
    }
    
    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public Class loadClass(String aClassName, boolean shouldResolve) throws ClassNotFoundException
    {
        System.out.println("DatabaseClassLoader: in loadClass(" + aClassName + ", " + shouldResolve + ')');

        // Yeah, Yeah, I know...You're supposed to override findClass. However, the
        // problem is that the overridden findClass is not called unless the parent
        // class loader cannot find the class. We want to ensure that persistable classes
        // are always resolved from the database first so that the database can handle things
        // like schema evolution. We also want to ensure that we are the ClassLoader
        // set on _any_ class loaded thru this ClassLoader. Normally, this wouldn't
        // be the case. For example, if we delegated to the parent classloader's findClass when
        // EnerJLauncher is loading the app main class, the app main class would
        // probably have a ClassLoader of "sun.misc.Launcher$AppClassLoader".
        // This is not what we want because the app main class will load other classes
        // which we want to be loaded using DatabaseClassLoader. (If class D is 
        // referenced by class C, class D is loaded using class C's ClassLoader).
        
        Class loadedClass = findLoadedClass(aClassName);
        if (loadedClass != null) {
            if (shouldResolve) {
                resolveClass(loadedClass);
            }

            System.out.println("DatabaseClassLoader: Returning previously loaded class: " + aClassName);
            return loadedClass;
        }

        
        // Try to find the .classid file using the parent loader.
        String resourceName = aClassName.replace('.', '/') + ".classid";
        InputStream resourceStream = getResourceAsStream(resourceName);
        boolean useParentLoader = false;
        long cid = 0L;
        if (resourceStream == null) {
            useParentLoader = true;
        }
        else {
            try {
                DataInputStream resourceDataStream = new DataInputStream(resourceStream);
                cid = resourceDataStream.readLong();
                resourceDataStream.close();
            }
            catch (IOException e) {
                // Oops! Switch to parent.
                useParentLoader = true;
                System.out.println("DatabaseClassLoader: IOException: " + e + " switching to parent");
            }
            finally {
                try {
                    resourceStream.close();
                }
                catch (IOException e) {
                    // Ignore
                }

                resourceStream = null;
            }
        }
        
        if (useParentLoader) {
            // Cannot find .classid, use parent to load class.
            System.out.println("DatabaseClassLoader: No .classid, using parent: " + aClassName);
            // This should use the parent to load aClassName.
            //return super.loadClass(aClassName, shouldResolve);
            return loadClassFromParentAsDatabaseClassLoader(aClassName, shouldResolve);
        }

        // Make sure we have access to a database
        EnerJTransaction currentTxn = EnerJTransaction.getCurrentTransaction();
        EnerJDatabase database;
        if (currentTxn == null) {
            database = EnerJDatabase.getCurrentDatabase();
        }
        else {
            database = currentTxn.getDatabase();
        }

        // We have a CID, but no database is open. This is an error and class
        // loading should not be d to the parent.
        if (database == null || !database.isOpen()) {
            throw new ClassNotFoundException("Could not load class " + aClassName + 
                ", CID " + cid + ". No database is currently open.");
        }

        // Load class from database.
        System.out.println("DatabaseClassLoader: Loading class " + aClassName + ", CID " + cid + " from database");
        try {
            // Do we have an open transaction?
            EnerJTransaction tempTxn = null;
            if (currentTxn == null || !currentTxn.isOpen()) {
                // Create a temporary one.
                System.out.println("DatabaseClassLoader: creating temporary txn");
                tempTxn = new EnerJTransaction();
                tempTxn.begin(database);
            }

            //  TODO  this is nasty because we read lock some of the schema here. this needs 
            //  TODO  to be pushed to the objectserver/schemaserver
            Schema schema = database.getDatabaseRoot().getSchema();
            ClassVersionSchema classVersion = schema.findClassVersion(cid);
            if (classVersion == null) {
                throw new ClassNotFoundException("Could not find class " + aClassName + 
                    ", CID " + cid + " in database schema");
            }

            // Ensure class name matches.
            String logicalClassName = classVersion.getLogicalClassSchema().getClassName();
            if ( !logicalClassName.equals(aClassName) ) {
                throw new ClassNotFoundException("Requested class " + aClassName + 
                    ", CID " + cid + " does not match name in database schema: " + logicalClassName);
            }

            // Proxy bytecodes override the enhanced version.
            byte[] classBytes = classVersion.getProxyBytecodes();
            if (classBytes == null) {
                classBytes = classVersion.getEnhancedBytecodes();
            }
            
            Class dbClass = defineClass(aClassName, classBytes, 0, classBytes.length);
            
            if (shouldResolve) {
                resolveClass(dbClass);
            }

            // Close the temp txn if we opened it.
            if (tempTxn != null) {
                tempTxn.commit();
                tempTxn = null;
                System.out.println("DatabaseClassLoader: closed temporary txn");
            }

            System.out.println("DatabaseClassLoader: LOADED class " + aClassName + ", CID " + cid + " from database!");

            //  TODO  unlock EnerJSchema, et.al. from current txn? Nested txn would be nice!

            return dbClass;
        }
        catch (Exception e) {
            throw new ClassNotFoundException("Could not load class " + aClassName + 
                ", CID " + cid + " from database", e);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Loads the class bytes using the parent's class loading algorithm, but 
     * defines the class with our ClassLoader. The only exception to this rule
     * is the following packages: java.*, javax.*, sun.*, com.sun.*, org.omg.*,
     * and sunw.*.
     *
     * @param aClassName the class name to load from the parent.
     * @param shouldResolve true if the class should be resolved ala loadClass.
     *
     * @return a Class with a ClassLoader of DatabaseClassLoader.
     *
     * @throws ClassNotFoundException if the class cannot be found.
     */
    public Class loadClassFromParentAsDatabaseClassLoader(String aClassName, boolean shouldResolve)
        throws ClassNotFoundException
    {
        if (aClassName.startsWith("java.") || 
            aClassName.startsWith("javax.") ||
            aClassName.startsWith("sun.") ||
            aClassName.startsWith("com.sun.") ||
            aClassName.startsWith("org.omg.") ||
            aClassName.startsWith("sunw.") ) {
            return super.loadClass(aClassName, shouldResolve);
        }
        
        String slashifiedClassName = aClassName.replace('.', '/') + ".class";
        InputStream classStream = getResourceAsStream(slashifiedClassName);
        if (classStream == null) {
            throw new ClassNotFoundException("DatabaseClassLoader could not load " + aClassName + " via parent loader: " + slashifiedClassName);
        }
        
        byte[] classBytes = new byte[ 20 * 1024 ];
        int numRead = 0;
        try {
            int readLen = 0;
            do {
                if ((numRead + CHUNK_SIZE) > classBytes.length) {
                    // Resize array.
                    byte[] newBytes = new byte[ classBytes.length + CHUNK_SIZE ];
                    System.arraycopy(classBytes, 0, newBytes, 0, classBytes.length);
                    classBytes = newBytes;
                }

                readLen = classStream.read(classBytes, numRead, CHUNK_SIZE);
                if (readLen > 0) {
                    numRead += readLen;
                }
            }
            while (readLen >= 0);
        }
        catch (IOException e) {
            throw new ClassNotFoundException("DatabaseClassLoader could not load " + aClassName + " via parent loader", e);
        }
        finally {
            try {
                classStream.close();
            }
            catch (IOException e) { 
                // Who cares? We weren't writing to the file. Ignore it.
            }
        }

        Class returnClass = defineClass(aClassName, classBytes, 0, numRead);
        if (shouldResolve) {
            resolveClass(returnClass);
        }
        
        return returnClass;
    }
    
}


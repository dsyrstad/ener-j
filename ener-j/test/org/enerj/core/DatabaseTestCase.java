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
//$Header: $

package org.enerj.core;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.enerj.server.PagedObjectServer;
import org.enerj.server.bdb.BDBObjectServer;

/**
 * Handles creation and deletion of a temporary database for testing. NOTE: This is a TestCase
 * to support older TestCases. New TestCases should not extend this class. Static methods
 * are available instead. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public abstract class DatabaseTestCase extends TestCase
{
    private static final Logger sLogger = Logger.getLogger(DatabaseTestCase.class.getName());
    
    private static final String DB_PATH = System.getProperty("enerj.dbpath");
    private static final String DBNAME = "GeneralDB";
    private static final String DBNAME2 = "GeneralDB2";
    private static final String PARENT_DIR = DB_PATH + "/JUnit/";
    
    protected static final String DATABASE_URI = DBNAME;
    protected static final String DATABASE2_URI = DBNAME2;

    /**
     * Construct a DatabaseTestCase. 
     *
     */
    public DatabaseTestCase()
    {
    }

    /**
     * Construct a DatabaseTestCase. 
     *
     * @param aName
     */
    public DatabaseTestCase(String aName)
    {
        super(aName);
    }

    public void setUp() throws Exception
    {
        createDatabase1();
    }


    public void tearDown() throws Exception
    {
        clearDBFiles();
    }

    public static void createDatabase1() throws Exception
    {
        clearDBFiles();
        System.setProperty("enerj.dbpath", PARENT_DIR);
        BDBObjectServer.createDatabase("Test", DBNAME);
    }

    /**
     *  Delete database files.
     */
    public static void clearDBFiles()
    {
        deleteDBFilesInDir(PARENT_DIR + '/' + DBNAME);
        clearDB2Files();
    }
    
    public static void deleteDBFilesInDir(String dirName)
    {
        for (File file : new File(dirName).listFiles()) {
            String name = file.getName();
            if (name.endsWith(".jdb") || name.endsWith(".enerj") || name.endsWith(".log")) {
                file.delete();
            }
        }
    }

    /**
     *  Delete database files.
     */
    public static void clearDB2Files()
    {
        deleteDBFilesInDir(PARENT_DIR + '/' + DBNAME2);
    }
    
    /**
     * Create the second database.
     *
     * @throws Exception if an error occurs.
     */
    public static void createDatabase2() throws Exception
    {
        clearDB2Files();
        System.setProperty("enerj.dbpath", PARENT_DIR);
        BDBObjectServer.createDatabase("Test", DBNAME2);
    }

}

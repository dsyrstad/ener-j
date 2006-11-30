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
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.enerj.server.PagedObjectServer;

/**
 * Handles creation and deletion of a temporary database for testing. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public abstract class AbstractDatabaseTestCase extends TestCase
{
    private static final String DBNAME = "GeneralDB";
    private static final String DBNAME2 = "GeneralDB2";
    private static final String PARENT_DIR = "databases/JUnit/";
    
    protected static final String DATABASE_URI = DBNAME;
    protected static final String DATABASE2_URI = DBNAME2;

    private File mDBPageFile = new File(PARENT_DIR + DBNAME + '/' + DBNAME + ".enerj");
    private File mDBLogFile = new File(PARENT_DIR + DBNAME + '/' + DBNAME + ".log");

    private File mDBPageFile2 = new File(PARENT_DIR + DBNAME2 + '/' + DBNAME2 + ".enerj");
    private File mDBLogFile2 = new File(PARENT_DIR + DBNAME2 + '/' + DBNAME2 + ".log");
    private boolean mCreatedDB2 = false;

    /**
     * Construct a AbstractDatabaseTestCase. 
     *
     */
    public AbstractDatabaseTestCase()
    {
    }

    /**
     * Construct a AbstractDatabaseTestCase. 
     *
     * @param aName
     */
    public AbstractDatabaseTestCase(String aName)
    {
        super(aName);
    }

    public void setUp() throws Exception
    {
        clearDBFiles();
        System.setProperty("enerj.dbpath", PARENT_DIR);
        PagedObjectServer.createDatabase("Test", DBNAME, 0L, 0L);
    }


    public void tearDown() throws Exception
    {
        clearDBFiles();
    }


    /**
     *  Delete database files.
     */
    private void clearDBFiles()
    {
        mDBPageFile.delete();
        mDBLogFile.delete();
        
        if (mCreatedDB2) {
            clearDB2Files();
        }
    }

    /**
     *  Delete database files.
     */
    private void clearDB2Files()
    {
        mDBPageFile2.delete();
        mDBLogFile2.delete();
    }
    
    /**
     * Create the second database.
     *
     * @throws Exception if an error occurs.
     */
    protected void createDatabase2() throws Exception
    {
        clearDB2Files();
        System.setProperty("enerj.dbpath", PARENT_DIR);
        PagedObjectServer.createDatabase("Test", DBNAME2, 0L, 0L);
    }

}

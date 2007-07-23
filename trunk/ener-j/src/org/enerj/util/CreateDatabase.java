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
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/CreateDatabase.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.enerj.server.ObjectServer;
import org.enerj.server.PagedObjectServer;
import org.enerj.server.PluginHelper;
import org.enerj.server.bdb.BDBObjectServer;
import org.odmg.DatabaseNotFoundException;
import org.odmg.ODMGException;

/**
 * Utility to create a database.
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class CreateDatabase 
{
    

    public static void usage()
    {
        System.err.println("Usage: " + CreateDatabase.class.getName() + " DatabaseName");
        System.err.println("DatabaseName - The database name.");
        System.err.println("    The database name must correspond to a base filename with \".properties\"");
        System.err.println("    appended that can be found along the path specified by enerj.dbpath.");
        System.err.println("    This is a Java properties file. See the plug-in class descriptions");
        System.err.println("    for information on the contents of the configuration file.");

        System.exit(1);
    }
    

    public static void main(String[] args) throws Exception
    {
        if (args.length < 1) {
            usage();
        }
        
        
        String dbName = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                /*if (args[i].equals("--max-vol-size") && (i + 1) < args.length) {
                    ++i;
                    maxVolSize = Long.parseLong(args[i]);
                }
                else if (args[i].equals("--pre-alloc") && (i + 1) < args.length) {
                    ++i;
                    preAlloc = Long.parseLong(args[i]);
                } else */
                {
                    System.err.println("Invalid option: " + args[i]);
                    usage();
                }
            }
            else if (dbName == null) {
                dbName = args[i];
            }
            else {
                System.err.println("Too many arguments: " + args[i]);
                usage();
            }
        }
        
        if (dbName == null) {
            System.err.println("Too few arguments.");
            usage();
        }

        try {
            createDatabase(dbName);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        System.exit(0);
    }
    
    /**
     * Creates a database. enerj.dbpath must be set.
     *
     * @param dbName the name of the database.
     */
    public static void createDatabase(String dbName) throws ODMGException
    {
        // Load database properties.  First copy system properties.
        Properties props = new Properties( System.getProperties() );

        String propFileName = dbName + File.separatorChar + dbName + ".properties";
        String dbPath = props.getProperty(ObjectServer.ENERJ_DBPATH_PROP);
        if (dbPath == null) {
            throw new ODMGException("Property " + ObjectServer.ENERJ_DBPATH_PROP + " must be defined");
        }
        
        File propFile = FileUtil.findFileOnPath(propFileName, dbPath);
        if (propFile == null) {
            throw new DatabaseNotFoundException("Cannot find " + propFileName + " in any of the directories " + dbPath); 
        }

        FileInputStream inPropFile = null;
        try {
            inPropFile = new FileInputStream(propFile);
            props.load(inPropFile);
        }
        catch (IOException e) {
            throw new ODMGException("Error reading " + propFile, e);
        }
        finally {
            if (inPropFile != null) {
                try {
                    inPropFile.close();
                }
                catch (IOException e) {
                    throw new ODMGException("Error closing properties file: " + propFile, e);
                }
                
                inPropFile = null;
            }
        }

        props.setProperty(ObjectServer.ENERJ_DBDIR_PROP, propFile.getParentFile().getAbsolutePath());
        props.setProperty(ObjectServer.ENERJ_DBNAME_PROP, dbName);

        PluginHelper.createDatabase(null, props);
    }
}

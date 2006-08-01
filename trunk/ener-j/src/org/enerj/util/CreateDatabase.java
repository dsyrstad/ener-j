// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/CreateDatabase.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.util;

import org.enerj.server.PagedObjectServer;

/**
 * Utility to create a database.
 *
 * @version $Id: CreateDatabase.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class CreateDatabase 
{
    
    //----------------------------------------------------------------------
    public static void usage()
    {
        System.err.println("Usage: " + CreateDatabase.class.getName() + " [options] DatabaseName");
        System.err.println("DatabaseName - The database name.");
        System.err.println("    The database name must correspond to a base filename with \".properties\"");
        System.err.println("    appended that can be found along the path specified by enerj.dbpath.");
        System.err.println("    This is a Java properties file. See the plug-in class descriptions");
        System.err.println("    for information on the contents of the configuration file.");
        System.err.println("");
        System.err.println("Options:");
        System.err.println("  --description \"...\" - a description of the database.");
        System.err.println("  --max-vol-size # - the maximum size in bytes for the volume. This will be");
        System.err.println("     rounded up to the nearest page boundary. If this value is zero, the ");
        System.err.println("     volume will grow unbounded. Default is zero.");
        System.err.println("  --pre-alloc # - the number of bytes to pre-allocate. This will be rounded up");
        System.err.println("     to the nearest page boundary. Default is zero.");

        System.exit(1);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) throws Exception
    {
        if (args.length < 1) {
            usage();
        }
        
        long maxVolSize = 0;
        long preAlloc = 0;
        
        String dbName = null;
        String description = null;
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (args[i].equals("--max-vol-size") && (i + 1) < args.length) {
                    ++i;
                    maxVolSize = Long.parseLong(args[i]);
                }
                else if (args[i].equals("--pre-alloc") && (i + 1) < args.length) {
                    ++i;
                    preAlloc = Long.parseLong(args[i]);
                }
                else {
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
            PagedObjectServer.createDatabase(description, dbName, maxVolSize, preAlloc);
        }
        catch (Exception e) {
            //System.err.println( e.toString() );
            e.printStackTrace();
            System.exit(1);
        }
        
        System.exit(0);
    }
}

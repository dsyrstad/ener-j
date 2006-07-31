// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/DumpRedoLog.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.util;

import java.util.*;

import org.odmg.*;

import org.enerj.core.*;
import org.enerj.server.*;
import org.enerj.server.logentry.*;

/**
 * Utility to dump a redo log.
 *
 * @version $Id: DumpRedoLog.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class DumpRedoLog 
{
    
    //----------------------------------------------------------------------
    public static void usage()
    {
        System.err.println("Usage: " + DumpRedoLog.class.getName() + " log-name");
        System.exit(1);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) throws Exception
    {
        if (args.length < 1) {
            usage();
        }
        
        String logName = args[0];
        
        RedoLogServer server = new ArchivingRedoLogServer(logName);
        long position = server.getFirstLogEntryPosition();
        LogEntry entry;
        while ((entry = server.read(position)) != null) {
            System.out.println("Position: " + position + ':' + new Date(entry.getTimestamp()).toString() + ": " + entry);
            position = entry.getNextLogEntryPosition();
        }

        System.exit(0);
    }
}

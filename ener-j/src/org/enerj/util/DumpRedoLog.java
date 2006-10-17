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

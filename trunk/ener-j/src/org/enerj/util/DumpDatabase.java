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

package org.enerj.util;

import java.util.Properties;

import org.enerj.server.FilePageServer;
import org.enerj.server.OIDList;
import org.enerj.server.PagedStore;

/**
 * Dumps a database created by PagedObjectServer. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class DumpDatabase
{

    /**
     * 
     *
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        Properties props = new Properties(System.getProperties());
        props.setProperty("PagedObjectServer.PageServerClass", FilePageServer.class.getName());
        props.setProperty("FilePageServer.volume", args[0]);
        PagedStore store = new PagedStore(props, null, null, true);
        
        OIDList oidList = store.getOIDList();
        long maxOID = oidList.getListSize();
        
        for (long oid = 1; oid < maxOID; ++oid) {
            long offset = oidList.getObjectOffsetForOID(oid);
            if (offset == 0) {
                continue;
            }
            
            long cid = oidList.getCIDforOID(oid);
            System.out.println("OID " + oid + " cid " + Long.toHexString(cid) + " offset " + offset);
        }
        
        store.disconnect();
    }

}

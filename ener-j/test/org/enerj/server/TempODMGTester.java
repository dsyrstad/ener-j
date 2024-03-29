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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/TempODMGTester.java,v 1.3 2006/06/09 02:39:32 dsyrstad Exp $

package org.enerj.server;

import java.io.File;

import org.odmg.DArray;
import org.odmg.Database;
import org.odmg.DatabaseNotFoundException;
import org.odmg.Implementation;
import org.odmg.ObjectNameNotFoundException;
import org.enerj.annotations.Persist;
import org.enerj.core.EnerJImplementation;
import org.enerj.core.EnerJTransaction;
import org.enerj.core.LargePersistentArrayList;
import org.enerj.util.CreateDatabase;


/**
 * Tests ODMG....
 *
 * @version $Id: TempODMGTester.java,v 1.3 2006/06/09 02:39:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class TempODMGTester
{
    private static final String URI = "enerj://root:root@-/TempODMGTest";


    private static void createDB() throws Exception
    {
        System.setProperty("enerj.dbpath", "databases/JUnit");
        File tmpPageFile = new File("databases/JUnit/TempODMGTest/TempODMGTest-volume");
        tmpPageFile.delete();
        
        // Delete the log file
        File tmpLogFile = new File("databases/JUnit/TempODMGTest/TempODMGTest.log");
        tmpLogFile.delete();

        CreateDatabase.createDatabase("TempODMGTest");
    }
    

    public static void main(String[] args) throws Exception
    {
        int numAdds = 180000;
        
        Implementation impl = EnerJImplementation.getInstance();
        Database db = impl.newDatabase();
        
        try {
            db.open(URI, Database.OPEN_READ_WRITE);
        }
        catch (DatabaseNotFoundException e) {
            //e.printStackTrace();
            createDB();
            db.open(URI, Database.OPEN_READ_WRITE);
        }
        
        EnerJTransaction txn = (EnerJTransaction)impl.newTransaction();
        txn.begin();
        
        DArray list;
        String binding = "TheArray";
        try {
            list = (DArray)db.lookup(binding);
        }
        catch (ObjectNameNotFoundException e) {
            list = new LargePersistentArrayList();
            db.bind(list, binding);
        }
        
        System.out.println("List contains " + list.size() + " items");
        long start;
        long end;
        
        /*start = System.currentTimeMillis();
        Iterator iter = list.iterator();
        int cnt = 0;
        for (; iter.hasNext(); cnt++) {
            Address addr = (Address)iter.next();
            if (!addr.getName().startsWith("Some Name")) {
                System.err.println("! Name not correct in Obj " + cnt + " name=" + addr.getName() + " addr=" + addr.getAddr());
            }
        }
        
        end = System.currentTimeMillis();
        System.out.println("Loaded " + cnt + " objects in " + (end - start) + " ms");
        */
        
        Address[] objs = new Address[numAdds];
        
        for (int i = 0; i < numAdds; i++) {
            objs[i] = new Address("Some Name " + i, "Some Addr " + i);
        }

        start = System.currentTimeMillis();
        
        for (int i = 0; i < numAdds; i++) {
            list.add(objs[i]);
            if ((i % 1000) == 9999) {
                txn.flush(); // TODO this should be automagic
            }
        }
        
        txn.commit();

        end = System.currentTimeMillis();
        System.out.println("Added " + numAdds + " objects in " + (end - start) + " ms");
        
        db.close();
        
        System.exit(1);
    }
    


    @Persist
    public static class Address
    {
        private String mName;
        private String mAddr;
        

        Address(String aName, String anAddr)
        {
            mName = aName;
            mAddr = anAddr;
        }


        String getName()
        {
            return mName;
        }


        String getAddr()
        {
            return mAddr;
        }
    }

}

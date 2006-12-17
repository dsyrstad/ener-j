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

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.enerj.annotations.Persist;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.Transaction;

/**
 * Tests PersistentBxTree. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class PersistentBxTreeTest extends AbstractDatabaseTestCase
{
    private static final String[] FIRST_NAMES = { "Dan", "Tina", "Bob", "Sue", "Emily", "Cole", "Mike", "Borusik", "Ole", "Lena", };
    private static final String[] LAST_NAMES = { "Smith", "Jones", "Funkmister", "Johnson", "Anderson", "Syrstad", "Robinson",  };
    private static final String[] CITIES = { "Burnsville", "Bloomington", "Minneapolis", "St Paul", "Washington", "Seattle", "Phoenix", "New York", "Clevland", "San Jose", };

    /**
     * Construct a PersistentBxTreeTest. 
     *
     * @param arg0
     */
    public PersistentBxTreeTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Test method for {@link org.enerj.core.PersistentBxTree#put(java.lang.Object, org.enerj.core.Persistable)}.
     */
    public void testPut() throws Exception
    {
        // Create an array of objects and the shuffle them.
        TestClass1[] objs = new TestClass1[100000];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = new TestClass1(i,
                            FIRST_NAMES[ i % FIRST_NAMES.length ],
                            LAST_NAMES[ i % LAST_NAMES.length ],
                            CITIES[ i % CITIES.length ]);
        }
        
        Collections.shuffle( Arrays.asList(objs), new Random(0L) ); // Use a consistent seed

        Implementation impl = EnerJImplementation.getInstance();
        Database db = impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        long start = System.currentTimeMillis();
        try {
            PersistentBxTree<Integer, TestClass1> tree = new PersistentBxTree<Integer, TestClass1>(450, null, false, true);
            db.bind(tree, "BTree");
            for (TestClass1 obj : objs) {
                //System.out.println("Inserting " + obj.mId);
                tree.insert(obj.mId, obj);
                //tree.dumpTree();
            }
       }
        finally {
            txn.commit();
            long end = System.currentTimeMillis();
            System.out.println("Insert time " + (end-start) + "ms");
            db.close();
        }
    }

    @Persist
    private static final class TestClass1
    {
        int mId;
        String mFirstName;
        String mLastName;
        String mCity;
        
        TestClass1(int anId, String aFirstName, String aLastName, String aCity)
        {
            mId = anId;
            mFirstName = aFirstName;
            mLastName = aLastName;
            mCity = aCity;
        }
    }
}

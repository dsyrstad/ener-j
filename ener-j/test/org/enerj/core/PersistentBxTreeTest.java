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
import org.enerj.annotations.PersistenceAware;
import org.enerj.util.StringUtil;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.Transaction;

/**
 * Tests PersistentBxTree. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@PersistenceAware
public class PersistentBxTreeTest extends AbstractDatabaseTestCase
{
    private static final String[] FIRST_NAMES = { "Dan", "Tina", "Bob", "Sue", "Emily", "Cole", "Mike", "Borusik", "Ole", "Lena", };
    private static final String[] LAST_NAMES = { "Smith", "Jones", "Funkmeister", "Johnson", "Anderson", "Syrstad", "Robinson",  };
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
    public void testLargeRandomPut() throws Exception
    {
        // Create an array of objects and the shuffle them.
        TestClass1[] objs = new TestClass1[100000];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = new TestClass1(i,
                            FIRST_NAMES[ i % FIRST_NAMES.length ],
                            LAST_NAMES[ i % LAST_NAMES.length ],
                            CITIES[ i % CITIES.length ]);
        }
        
        // Shuffle array using a consistent seed
        Collections.shuffle(Arrays.asList(objs), new Random(1L));

        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = new EnerJDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        long start = System.currentTimeMillis();
        try {
            PersistentBxTree<Integer, TestClass1> tree = new PersistentBxTree<Integer, TestClass1>(10, null, false, false, true);
            db.bind(tree, "BTree");
            for (int i = 0; i < objs.length; i++) {
                TestClass1 obj = objs[i];
                tree.insert(obj.getId(), obj);

                try {
                }
                catch (IllegalStateException e) {
                    System.out.println("While inserting " + obj.getId() + " on iteration " + i);
                    tree.dumpTree();
                    throw e;
                }
            }
        }
        finally {
            txn.commit();
            db.close();
        }

        long end = System.currentTimeMillis();
        System.out.println("Insert time " + (end-start) + "ms");

        db = new EnerJDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);
        db.setAllowNontransactionalReads(true);

        start = System.currentTimeMillis();
        try {
            PersistentBxTree<Integer, TestClass1> tree = (PersistentBxTree<Integer, TestClass1>)db.lookup("BTree");

            // Make sure that the tree is valid.
            tree.validateTree();

            for (TestClass1 obj : objs) {
                assertTrue("Id does not exist " + obj.getId(), tree.containsKey(obj.getId()) );
                TestClass1 getObj = tree.get(obj.getId());
                assertNotNull(getObj);
                assertEquals(obj.getId(), getObj.getId());
                if (obj.getLastName() == null) {
                    System.out.println(StringUtil.toString(obj, false, true));
                }
                
                assertEquals(obj.getLastName(), getObj.getLastName() );
            }
        }
        finally {
            db.close();
        }

        end = System.currentTimeMillis();
        System.out.println("ContainsKey/Get time " + (end-start) + "ms");
    }

    @Persist
    private static final class TestClass1
    {
        private int mId;
        private String mFirstName;
        private String mLastName;
        private String mCity;
        
        TestClass1(int anId, String aFirstName, String aLastName, String aCity)
        {
            mId = anId;
            mFirstName = aFirstName;
            mLastName = aLastName;
            mCity = aCity;
        }

        String getCity()
        {
            return mCity;
        }

        String getFirstName()
        {
            return mFirstName;
        }

        int getId()
        {
            return mId;
        }

        String getLastName()
        {
            return mLastName;
        }
    }
}

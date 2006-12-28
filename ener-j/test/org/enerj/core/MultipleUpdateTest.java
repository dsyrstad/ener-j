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
 * Tests mutliple updates to an object in a single transaction. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@PersistenceAware
public class MultipleUpdateTest extends AbstractDatabaseTestCase
{

    /**
     * Construct a MultipleUpdateTest. 
     *
     * @param arg0
     */
    public MultipleUpdateTest(String arg0)
    {
        super(arg0);
    }

    public void testMultipleUpdates() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = new EnerJDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        EnerJTransaction txn = new EnerJTransaction();
        txn.begin();

        long start = System.currentTimeMillis();
        final int lastIdValue = 1000;
        long oid = 0;
        try {
            TestClass1 obj = new TestClass1(0, "Start", "StartLast", "StartCity");
            db.makePersistent(obj);
            oid = EnerJImplementation.getEnerJObjectId(obj);

            txn.flush();
            obj = null;
            db.evict(oid);
            
            for (int i = 0; i < lastIdValue; i++) {
                obj = (TestClass1)(Object)db.getObjectForOID(oid);
                int currId = obj.getId();
                assertEquals(i, currId);
                obj.setId(currId + 1);

                txn.flush();
                obj = null;
                db.evict(oid);
            }
        }
        finally {
            txn.commit();
            db.close();
        }

        long end = System.currentTimeMillis();
        System.out.println("Update time " + (end-start) + "ms");

        db = new EnerJDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);
        db.setAllowNontransactionalReads(true);

        try {
            TestClass1 obj = (TestClass1)(Object)db.getObjectForOID(oid);
            assertEquals(lastIdValue, obj.getId() );
        }
        finally {
            db.close();
        }
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

        void setId(int anId) 
        {
            mId = anId;
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

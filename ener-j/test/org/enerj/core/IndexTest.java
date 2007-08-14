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

package org.enerj.core;

import java.util.Random;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.annotations.Index;
import org.enerj.annotations.Indexes;
import org.enerj.annotations.Persist;
import org.enerj.server.DBIterator;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.Transaction;

/**
 * Tests Index functionality.
 *
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class IndexTest extends DatabaseTestCase
{
    public IndexTest(String aTestName) 
    {
        super(aTestName);
    }
    

    public static Test suite() 
    {
        return new TestSuite(IndexTest.class);
    }


    /**
     * Tests 
     */
    public void testBasic() throws Exception
    {
        Implementation impl = EnerJImplementation.getInstance();
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        String[] values = { "Orange", "Red", "Brown", "Green", "Blue", "Black", "Yellow" };
        final int numObjs = 1000;
        Random rand = new Random();
        try {
            for (int i = 0; i < numObjs; i++) {
                TestClass1 test = new TestClass1(rand.nextInt(5000), values[i % values.length]);
                db.makePersistent(test);
            }
        }
        finally {
            txn.commit();
            db.close();
        }

        // Re-open and read from iterator.
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        txn = impl.newTransaction();
        txn.begin();

        try {
            // Check size.
            long size = db.getIndexKeyRangeSize(TestClass1.class, "testIndex2", null, null);
            assertEquals((long)numObjs, size);
            
            DBIterator iter = db.getIndexIterator(TestClass1.class, "testIndex2", null, null);
        }
        finally {
            txn.commit();
            db.close();
        }

    }


    @Persist
    @Indexes( {
        @Index(name="testIndex", properties={ "string" }),
        @Index(name="testIndex2", properties={ "value" }),
        @Index(name="testIndex3", properties={ "value", "string" })
    } )
    public static class TestClass1
    {
        private int mValue;
        private String mString;
        

        TestClass1(int aValue, String aString)
        {
            mValue = aValue;
            mString = aString;
        }
            

        public int getValue()
        {
            return mValue;
        }


        public void setValue(int aValue)
        {
            mValue = aValue;
        }


        public String getString()
        {
            return mString;
        }


        public void setString(String someString)
        {
            mString = someString;
        }
    }
}

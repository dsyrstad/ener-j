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
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/BasicODMGTest.java,v 1.2 2006/06/05 01:17:03 dsyrstad Exp $

package org.enerj.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.annotations.Index;
import org.enerj.annotations.Persist;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.Transaction;

/**
 * Tests Index functionality.
 *
 * @version $Id: BasicODMGTest.java,v 1.2 2006/06/05 01:17:03 dsyrstad Exp $
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
        Database db = impl.newDatabase();
        
        db.open(DATABASE_URI, Database.OPEN_READ_WRITE);

        Transaction txn = impl.newTransaction();
        txn.begin();

        String[] values = { "Orange", "Red", "Brown", "Green", "Blue", "Black", "Yellow" };
        try {
            for (int i = 0; i < values.length; i++) {
                TestClass1 test = new TestClass1(i, values[i]);
                db.bind(test, "Link" + i);
            }
        }
        finally {
            txn.commit();
            db.close();
        }

    }


    @Persist
    private static class TestClass1
    {
        private int mValue;
        @Index(name="testIndex")
        private String mString;
        

        TestClass1(int aValue, String aString)
        {
            mValue = aValue;
            mString = aString;
        }
            

        int getValue()
        {
            return mValue;
        }


        void setValue(int aValue)
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

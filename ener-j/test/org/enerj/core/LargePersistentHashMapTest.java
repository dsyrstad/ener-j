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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/RegularDMapTest.java,v 1.2 2006/01/12 04:39:44 dsyrstad Exp $

package org.enerj.core;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.enerj.apache.commons.collections.map.AbstractTestMap;
import org.odmg.Database;
import org.odmg.QueryableCollection;

/**
 * Tests org.enerj.core.RegularDMap.
 *
 * @version $Id: RegularDMapTest.java,v 1.2 2006/01/12 04:39:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class LargePersistentHashMapTest extends TestCase
{

    public LargePersistentHashMapTest(String aTestName) 
    {
        super(aTestName);
    }

    public static Test suite() 
    {
        TestSuite suite = new TestSuite(LargePersistentHashMapTest.class);
        
        suite.addTestSuite( LargePersistentHashMapTest.InternalDMapTest.class );
        suite.addTestSuite( LargePersistentHashMapTest.InternalQueryableCollectionTest.class );

        return suite;
    }
    
    /*
     * You can have additional methods here..... 
     */
    public void testNothing() throws Exception
    {
        // Placeholder until a specific test is added.
    }


    // Inner classes used to test interfaces and abstract classes.

    /**
     * Tests DMap interface of RegularDMap.
     */
    public static final class InternalDMapTest extends AbstractDMapTest
    {

        public InternalDMapTest(String aName)
        {
            super(aName);
        }


        public Map createMap() throws Exception
        {
            return new LargePersistentHashMap();
        }


        public boolean allowsNullKeys()
        {
            return true;
        }
    }
    
    /**
     * Uses Apache Collections Tests.
     */
    // Note: this misses some tests because of BulkTest problems interacting with the database.    
    public static final class ApacheCollectionsMapTest extends AbstractTestMap
    {
        private EnerJDatabase mDB;
        private EnerJTransaction mTxn;
        
        public ApacheCollectionsMapTest(String aName)
        {
            super(aName);
        }
        
        public void setUp() throws Exception
        {
            DatabaseTestCase.createDatabase1();
            mDB = new EnerJDatabase();
            mDB.open(DatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
            mTxn = new EnerJTransaction();
            mTxn.begin(mDB);
            super.setUp();
        }


        public void tearDown() throws Exception
        {
            mTxn.commit();
            mDB.close();
            DatabaseTestCase.clearDBFiles();
            super.tearDown();
        }

        @Override
        public void testSimpleSerialization() throws Exception
        {
            if (isTestSerialization()) {
                super.testSimpleSerialization();
            }
            // Else don't test this. We don't need to support it.
        }

        @Override
        public void testSerializeDeserializeThenCompare() throws Exception
        {
            if (isTestSerialization()) {
                super.testSerializeDeserializeThenCompare();
            }
            // Else don't test this. We don't need to support it.
        }

        @Override
        public Map makeEmptyMap()
        {
            return new LargePersistentHashMap();
        }

        @Override
        public Map makeConfirmedMap() 
        {
            return new HashMap();
        }

        @Override
        public boolean isAllowNullKey()
        {
            return true;
        }

        @Override
        public boolean isAllowNullValue()
        {
            return true;
        }

        @Override
        public boolean isTestSerialization()
        {
            return false; // We don't need to support this
        }
    }
    
    /**
     * Tests QueryableCollection interface.
     */
    public static final class InternalQueryableCollectionTest extends AbstractQueryableCollectionTest
    {
        private EnerJDatabase mDB;
        private EnerJTransaction mTxn;
        

        public InternalQueryableCollectionTest(String aName)
        {
            super(aName);
        }


        public void setUp() throws Exception
        {
            DatabaseTestCase.createDatabase1();
            mDB = new EnerJDatabase();
            mDB.open(DatabaseTestCase.DATABASE_URI, Database.OPEN_READ_WRITE);
            mTxn = new EnerJTransaction();
            mTxn.begin(mDB);
            super.setUp();
        }


        public void tearDown() throws Exception
        {
            mTxn.commit();
            mDB.close();
            DatabaseTestCase.clearDBFiles();
            super.tearDown();
        }

        public QueryableCollection createQueryableCollection() throws Exception
        {
            return new LargePersistentHashMap();
        }
    }

}

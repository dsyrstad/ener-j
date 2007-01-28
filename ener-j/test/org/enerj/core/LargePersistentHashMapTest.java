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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private EnerJDatabase mDB;
    private EnerJTransaction mTxn;


    public LargePersistentHashMapTest(String aTestName) 
    {
        super(aTestName);
    }

    public static Test suite() 
    {
        TestSuite suite = new TestSuite(LargePersistentHashMapTest.class);
        
        suite.addTestSuite( LargePersistentHashMapTest.InternalDMapTest.class );
        suite.addTestSuite( LargePersistentHashMapTest.ApacheCollectionsMapTest.class );
        suite.addTestSuite( LargePersistentHashMapTest.InternalQueryableCollectionTest.class );

        return suite;
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

    /**
     * Tests Duplicate keys and duplicate null keys. 
     */
    public void testDuplicateKeysAndNulls() throws Exception
    {
        final int maxKey = 100000;
        final Integer dupKey = maxKey / 2;
        final int dupKeyCnt = 1000;
        final int nullKeyCnt = 100;
        
        DuplicateKeyMap<Integer, String> map = new LargePersistentHashMap<Integer, String>(LargePersistentHashMap.DEFAULT_NODE_SIZE, true); 
        for (int i = 0; i < maxKey; i++) {
            map.put(i, "Value " + i);
        }
        
        // Add the duplicate keys (-1 because one of the keys was already inserted)
        for (int i = 0; i < (dupKeyCnt - 1); i++) {
            String prev = map.put(dupKey, "Value " + dupKey + " #" + i);
            assertNull(prev);
        }
        
        // Add the null duplicate key
        for (int i = 0; i < nullKeyCnt; i++) {
            String prev = map.put(null, "Value null #" + i);
            assertNull(prev);
        }
        
        assertEquals("Size should match", maxKey + (dupKeyCnt - 1) + nullKeyCnt, map.size());
        
        // Check the duplicate values.
        Collection<String> dupValues = map.getValues(dupKey);
        assertEquals(dupKeyCnt, dupValues.size());
        
        Set<String> checkValues = new HashSet<String>(dupValues);
        // Make sure all values were unique
        assertEquals(dupKeyCnt, checkValues.size());
        
        // Check the null duplicate values.
        dupValues = map.getValues(null);
        assertEquals(nullKeyCnt, dupValues.size());
        
        checkValues = new HashSet<String>(dupValues);
        // Make sure all values were unique
        assertEquals(nullKeyCnt, checkValues.size());
    }


    // Inner classes used to test interfaces and abstract classes.

    /**
     * Tests DMap interface of RegularDMap.
     */
    public static final class InternalDMapTest extends AbstractDMapTest
    {
        private EnerJDatabase mDB;
        private EnerJTransaction mTxn;

        public InternalDMapTest(String aName)
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

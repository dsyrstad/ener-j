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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/sco/SCOTest.java,v 1.4 2006/06/09 02:38:31 dsyrstad Exp $

package org.enerj.sco;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.enerj.annotations.Persist;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJImplementation;

/**
 * Tests Mutable SCOs.
 *
 * @version $Id: SCOTest.java,v 1.4 2006/06/09 02:38:31 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class SCOTest extends TestCase
{
    private static final String DATABASE_URI = "enerj://root:root@-/SCOTestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";
    

    public SCOTest(String aTestName) 
    {
        super(aTestName);
    }
    

    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(SCOTest.class);
    }
    

    public static Test suite() 
    {
        return new TestSuite(SCOTest.class);
    }
    

    /**
     * Write aPersistable to database and then reload back into a new object.
     *
     * @throws Exception when something unexpected occurs.
     */
    public static Persistable storeAndLoad(EnerJDatabase aDatabase, Persistable aPersistable) throws Exception
    {
        
        long oid = aDatabase.getOID(aPersistable);
        aDatabase.storePersistable(aPersistable);
        aDatabase.getTransaction().flush();
        aDatabase.evictAll();

        Persistable persistable2 = aDatabase.getObjectForOID(oid);
        assertTrue("Object should not be loaded, and not be new", !persistable2.enerj_IsLoaded() && !persistable2.enerj_IsNew());
        
        return persistable2;
    }
    

    /**
     * Basic SCO tests.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testSCOs() throws Exception
    {
        EnerJDatabase database = (EnerJDatabase)EnerJImplementation.getInstance().newDatabase();
        database.open("enerj://root:root@-/SCOTestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer", EnerJDatabase.OPEN_READ_WRITE);
        
        org.odmg.Transaction txn = EnerJImplementation.getInstance().newTransaction();
        txn.begin();

        // Persist with values.
        TestClass1 testObj = new TestClass1();
        Persistable persistable = (Persistable)(Object)testObj;
        PersistableHelper.setNonTransactional(persistable);
        
        testObj.setFieldValues();
        
        Persistable persistable2 = storeAndLoad(database, persistable);
        PersistableHelper.setNonTransactional(persistable2);
        TestClass1 testObj2 = (TestClass1)(Object)persistable2;
        
        assertTrue("Should be independent objects", persistable != persistable2);
        assertTrue("OIDs should be the same", database.getOID(persistable) == database.getOID(persistable2) );
        testObj2.changeFieldValues();

        txn.abort();
        database.close();
    }
    



    @Persist
    private static class TestClass1
    {
        // Test mutable SCOs
        private Date[][] mDate2DArray;
        private java.util.Date mUtilDate;
        private java.sql.Date mSqlDate;
        private java.sql.Time mSqlTime;
        private java.sql.Timestamp mSqlTimestamp;
        private java.util.ArrayList mArrayList;
        private java.util.LinkedList mLinkedList;
        private java.util.TreeSet mTreeSet;
        private java.util.Vector mVector;
        private java.util.Stack mStack;
        private java.util.HashSet mHashSet;
        private java.util.LinkedHashSet mLinkedHashSet;
        private java.util.HashMap mHashMap;
        private java.util.Hashtable mHashtable;
        private java.util.LinkedHashMap mLinkedHashMap;
        private java.util.Properties mProperties;
        private java.util.TreeMap mTreeMap;
        private java.util.IdentityHashMap mIdentityHashMap;
        private java.util.ArrayList mArrayListContainingSCO;


        TestClass1()
        {
        }


        void setFieldValues()
        {
            mUtilDate = new java.util.Date(); // Today 
            mSqlDate = new java.sql.Date(28438L); 
            mSqlTime = new java.sql.Time(2384783121L);
            mSqlTimestamp = new java.sql.Timestamp(987654321L);
            mSqlTimestamp.setNanos(3243);

            // All collections are initially empty.
            mArrayList = new java.util.ArrayList();
            mLinkedList = new java.util.LinkedList();
            mVector = new java.util.Vector();
            mStack = new java.util.Stack();
            mHashSet = new java.util.HashSet();
            mTreeSet = new java.util.TreeSet();
            mLinkedHashSet = new java.util.LinkedHashSet();

            mHashMap = new java.util.HashMap();
            mHashtable = new java.util.Hashtable();
            mLinkedHashMap = new java.util.LinkedHashMap();
            mProperties = new java.util.Properties();
            mTreeMap = new java.util.TreeMap();
            mIdentityHashMap = new java.util.IdentityHashMap();

            mDate2DArray = new Date[][] {
                { new java.util.Date(), new java.util.Date(), },
                { new java.util.Date(), new java.util.Date(), new java.util.Date(), },
            };

            mArrayListContainingSCO = new java.util.ArrayList();
            mArrayListContainingSCO.add( new java.util.Date() );
        }


        /**
         * Test mutator methods on the java.util.Collection interface.
         *
         * @throws AssertionFailedException if one the assertions does not hold true.
         */
        void testCollectionMutators(Persistable aPersistable, Collection aCollection)
        {
            aCollection.clear();
            for (int i = 0; i < 10; i++) {
                aPersistable.enerj_SetModified(false);
                aCollection.add( new Integer(i) );
                assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
                assertTrue("Value should be set", aCollection.contains( new Integer(i) ) );
            }

            aPersistable.enerj_SetModified(false);
            aCollection.remove( new Integer(5) );
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be gone", !aCollection.contains( new Integer(5) ) );

            Collection tmpCollection = new ArrayList();
            for (int i = 10; i < 20; i++) {
                tmpCollection.add( new Integer(i) );
            }
            
            aPersistable.enerj_SetModified(false);
            aCollection.addAll(tmpCollection);
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be set", aCollection.contains( new Integer(15) ) );

            aPersistable.enerj_SetModified(false);
            aCollection.removeAll(tmpCollection);
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should not be set", !aCollection.contains( new Integer(15) ) );

            tmpCollection = new ArrayList();
            for (int i = 2; i < 10; i += 2) {
                tmpCollection.add( new Integer(i) );
            }

            aPersistable.enerj_SetModified(false);
            aCollection.retainAll(tmpCollection);
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should not be set", !aCollection.contains( new Integer(1) ) );
            assertTrue("Value should be set", aCollection.contains( new Integer(2) ) );

            aPersistable.enerj_SetModified(false);
            aCollection.clear();
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Size should be zero", aCollection.size() == 0 );
        }
        

        /**
         * Test mutator methods on the java.util.List interface.
         *
         * @throws AssertionFailedException if one the assertions does not hold true.
         */
        void testListMutators(Persistable aPersistable, List aList)
        {
            aList.clear();

            for (int i = 0; i < 10; i++) {
                aPersistable.enerj_SetModified(false);
                aList.add(i, new Integer(i) );
                assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
                assertTrue("Value should be set", aList.get(i).equals( new Integer(i) ) );
            }

            aPersistable.enerj_SetModified(false);
            aList.set(2, new Integer(34) );
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be set", aList.get(2).equals( new Integer(34) ) );

            Collection tmpCollection = new ArrayList();
            for (int i = 10; i < 20; i++) {
                tmpCollection.add( new Integer(i) );
            }

            aPersistable.enerj_SetModified(false);
            aList.addAll(10, tmpCollection);
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be set", aList.get(10).equals( new Integer(10) ) );

            aPersistable.enerj_SetModified(false);
            aList.remove(5);
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should not be set", !aList.contains( new Integer(5) ) );

        }
        

        /**
         * Test mutator methods on the java.util.Map interface.
         *
         * @throws AssertionFailedException if one the assertions does not hold true.
         */
        void testMapMutators(Persistable aPersistable, Map aMap)
        {
            // Note: Use same object for put/get keys so IdentityHashMap will work too.
            aMap.clear();
            Object key5 = null;
            for (int i = 0; i < 10; i++) {
                aPersistable.enerj_SetModified(false);
                Object key = new Integer(i);
                if (i == 5) {
                    key5 = key;
                }
                
                aMap.put(key, new Long(i) );
                assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
                assertTrue("Value should be set", aMap.get(key).equals( new Long(i) ) );
            }

            aPersistable.enerj_SetModified(false);
            aMap.remove(key5);
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be gone", !aMap.containsKey(key5) );

            Map tmpMap = new HashMap();
            Object key15 = null;
            for (int i = 10; i < 20; i++) {
                Object key = new Integer(i);
                if (i == 15) {
                    key15 = key;
                }

                tmpMap.put(key, new Long(i) );
            }
            
            aPersistable.enerj_SetModified(false);
            aMap.putAll(tmpMap);
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be set", aMap.containsKey(key15) );


            aPersistable.enerj_SetModified(false);
            aMap.clear();
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Size should be zero", aMap.size() == 0 );
        }
        

        /**
         * Test mutator methods on java.util.LinkedList.
         *
         * @throws AssertionFailedException if one the assertions does not hold true.
         */
        void testLinkedList(Persistable aPersistable, LinkedList aLinkedList)
        {
            testCollectionMutators(aPersistable, aLinkedList);
            testListMutators(aPersistable, aLinkedList);

            // LinkedList-specific methods
            aLinkedList.clear();
            for (int i = 0; i < 10; i++) {
                aLinkedList.add(i, new Integer(i) );
            }

            aPersistable.enerj_SetModified(false);
            aLinkedList.addFirst( new Integer(11) );
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be set", aLinkedList.getFirst().equals( new Integer(11) ) );

            aPersistable.enerj_SetModified(false);
            aLinkedList.addLast( new Integer(12) );
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be set", aLinkedList.getLast().equals( new Integer(12) ) );

            aPersistable.enerj_SetModified(false);
            aLinkedList.removeFirst();
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be set", aLinkedList.getFirst().equals( new Integer(0) ) );

            aPersistable.enerj_SetModified(false);
            aLinkedList.removeLast();
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be set", aLinkedList.getLast().equals( new Integer(9) ) );
        }
        

        /**
         * Test mutator methods on java.util.Vector.
         *
         * @throws AssertionFailedException if one the assertions does not hold true.
         */
        void testVector(Persistable aPersistable, Vector aVector)
        {
            testCollectionMutators(aPersistable, aVector);
            testListMutators(aPersistable, aVector);

            aVector.clear();

            for (int i = 0; i < 10; i++) {
                aPersistable.enerj_SetModified(false);
                aVector.addElement( new Integer(i) );
                assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
                assertTrue("Value should be set", aVector.contains( new Integer(i) ) );
            }

            aPersistable.enerj_SetModified(false);
            aVector.removeElement( new Integer(5) );
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be gone", !aVector.contains( new Integer(5) ) );

            aPersistable.enerj_SetModified(false);
            aVector.removeAllElements();
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Size should be zero", aVector.size() == 0 );

            aVector.clear();

            for (int i = 0; i < 10; i++) {
                aPersistable.enerj_SetModified(false);
                aVector.insertElementAt(new Integer(i), i);
                assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
                assertTrue("Value should be set", aVector.get(i).equals( new Integer(i) ) );
            }

            aPersistable.enerj_SetModified(false);
            aVector.setElementAt(new Integer(34), 2);
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should be set", aVector.get(2).equals( new Integer(34) ) );

            aPersistable.enerj_SetModified(false);
            aVector.removeElementAt(5);
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Value should not be set", !aVector.contains( new Integer(5) ) );

            aPersistable.enerj_SetModified(false);
            aVector.setSize(4);
            assertTrue("Owner should be modified", aPersistable.enerj_IsModified());
            assertTrue("Size should be set", aVector.size() == 4);
        }


        void testSCOClone()
        {
            SCOTracker[] scos = new SCOTracker[] {
                (SCOTracker)mUtilDate.clone(),
                (SCOTracker)mSqlDate.clone(),
                (SCOTracker)mSqlTime.clone(),
                (SCOTracker)mSqlTimestamp.clone(),
                (SCOTracker)mArrayList.clone(),
                (SCOTracker)mLinkedList.clone(),
                (SCOTracker)mTreeSet.clone(),
                (SCOTracker)mVector.clone(),
                (SCOTracker)mStack.clone(),
                (SCOTracker)mHashSet.clone(),
                (SCOTracker)mLinkedHashSet.clone(),
                (SCOTracker)mHashMap.clone(),
                (SCOTracker)mHashtable.clone(),
                (SCOTracker)mLinkedHashMap.clone(),
                (SCOTracker)mProperties.clone(),
                (SCOTracker)mTreeMap.clone(),
                (SCOTracker)mIdentityHashMap.clone(),
            };
            
            for (int i = 0; i < scos.length; i++) {
                assertTrue("Owner should be null on clone", scos[i].getOwnerFCO() == null);
            }
        }
        

        /**
         * To be used after loading from DB (when SCO subclasses are created) 
         * to test Owner modification when SCO
         * updated. The values should also be set properly on the SCO.
         *
         * @throws AssertionFailedException if one the assertions does not hold true.
         */
        void changeFieldValues()
        {
            Persistable persistable = (Persistable)(Object)this;

            // java.util.Date
            persistable.enerj_SetModified(false);
            mUtilDate.setTime(45L);
            assertTrue("Owner should be modified", persistable.enerj_IsModified());
            assertTrue("Value should be set", mUtilDate.getTime() == 45L);

            // java.sql.Date
            persistable.enerj_SetModified(false);
            mSqlDate.setTime(45L);
            assertTrue("Owner should be modified", persistable.enerj_IsModified());
            assertTrue("Value should be set", mSqlDate.getTime() == 45L);

            // java.sql.Time
            persistable.enerj_SetModified(false);
            mSqlTime.setTime(45L);
            assertTrue("Owner should be modified", persistable.enerj_IsModified());
            assertTrue("Value should be set", mSqlTime.getTime() == 45L);

            // java.sql.Timestamp
            persistable.enerj_SetModified(false);
            mSqlTimestamp.setTime(45000L);
            assertTrue("Owner should be modified", persistable.enerj_IsModified());
            persistable.enerj_SetModified(false);
            mSqlTimestamp.setNanos(12345);
            assertTrue("Owner should be modified", persistable.enerj_IsModified());
            assertTrue("Value should be set", mSqlTimestamp.getTime() == 45000L);
            assertTrue("Nanos Value should be set", mSqlTimestamp.getNanos() == 12345);
            
            // java.util.ArrayList
            testCollectionMutators(persistable, mArrayList);
            testListMutators(persistable, mArrayList);

            // java.util.LinkedList
            testLinkedList(persistable, mLinkedList);
            
            // java.util.Vector
            testVector(persistable, mVector);
            
            // java.util.Stack
            testVector(persistable, mStack);
            
            mStack.clear();
            persistable.enerj_SetModified(false);
            mStack.push( new Integer(55) );
            assertTrue("Owner should be modified", persistable.enerj_IsModified());
            assertTrue("Value should be set", mStack.contains( new Integer(55) ) );
            
            persistable.enerj_SetModified(false);
            mStack.pop();
            assertTrue("Owner should be modified", persistable.enerj_IsModified());
            assertTrue("Stack should be empty", mStack.size() == 0);

            // java.util.HashSet
            testCollectionMutators(persistable, mHashSet);

            // java.util.LinkedHashSet 
            testCollectionMutators(persistable, mLinkedHashSet);

            // java.util.HashMap
            testMapMutators(persistable, mHashMap);

            // java.util.Hashtable
            testMapMutators(persistable, mHashtable);

            // java.util.LinkedHashMap 
            testMapMutators(persistable, mLinkedHashMap);

            // java.util.Properties 
            testMapMutators(persistable, mProperties);

            mProperties.clear();
            persistable.enerj_SetModified(false);
            mProperties.setProperty("foo", "bar");
            assertTrue("Owner should be modified", persistable.enerj_IsModified());
            assertTrue("Value should be set", mProperties.getProperty("foo").equals("bar") );

            mProperties.clear();
            persistable.enerj_SetModified(false);
            try {
                mProperties.load( new java.io.ByteArrayInputStream("red=black\ngreen=orange".getBytes()) );
            }
            catch (IOException e) {
                fail("Didn't expect exception: " + e);
            }

            assertTrue("Owner should be modified", persistable.enerj_IsModified());
            assertTrue("Value should be set", mProperties.getProperty("red").equals("black") );
            assertTrue("Value should be set", mProperties.getProperty("green").equals("orange") );

            // java.util.TreeMap 
            testMapMutators(persistable, mTreeMap);
            
            // java.util.IdentityHashMap 
            testMapMutators(persistable, mIdentityHashMap);

            // Test clone() on all mutable SCOs
            testSCOClone();
            
            // Test modifying SCOs contained in 2D array. Should cause owner to be modified.
            for (int i = 0; i < mDate2DArray.length; i++) {
                for (int j = 0; j < mDate2DArray[i].length; j++) {
                    persistable.enerj_SetModified(false);
                    mDate2DArray[i][j].setTime(443L);
                    assertTrue("Owner should be modified", persistable.enerj_IsModified());
                }
            }
            
            // Test modifying SCOs contained in an ArrayList.
            persistable.enerj_SetModified(false);
            ((Date)mArrayListContainingSCO.get(0)).setTime(234893L);
            assertTrue("Owner should be modified", persistable.enerj_IsModified());
        }
    }
}

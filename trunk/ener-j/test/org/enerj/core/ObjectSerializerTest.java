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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/ObjectSerializerTest.java,v 1.2 2006/06/09 02:39:23 dsyrstad Exp $

package org.enerj.core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.enerj.annotations.Persist;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests ObjectSerializer.
 *
 * @version $Id: ObjectSerializerTest.java,v 1.2 2006/06/09 02:39:23 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class ObjectSerializerTest extends TestCase
{
    private static final String DATABASE_URI = "enerj://root:root@-/ObjectSerializerTestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";
    
    //----------------------------------------------------------------------
    public ObjectSerializerTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(ObjectSerializerTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(ObjectSerializerTest.class);
    }
    
    //----------------------------------------------------------------------
    /**
     * Recursively compares two objects for equality.<p>
     * - If both objects are null, they are equal.<p>
     * - If both objects are the exact same object, they are equal.<p>
     * - If one object is null, but the other is not, they are not equal.<p>
     * - The two objects must have the same type (i.e., 
     * anObject1.getClass().equals(anObject2.getClass()), otherwise they are not equal.
     * For purposes of comparing SCO subclasses, it is permissible for the class of
     * one object to be the same as superclass of the other object.<p>
     * - If the two objects represent arrays, the lengths must be the same and
     * each element is recursively compared for equality using this method. This
     * supports multidimensional arrays and arrays of Collections. If the element
     * type of the array is primitive, it is essentially compared using ==.<p>
     * - If the two objects represent collections, the sizes must be the same and
     * each element is recursively compared for equality using this method. This
     * supports Collections of Collections and Collections of arrays.<p>
     * - If the two objects represent Maps, the sizes must be the same and
     * each key in the first Map must exist in the second Map, and the values
     * corresponding to each key are recursively compared for equality using this method.
     * This supports Maps of Maps/Collections, and Maps of arrays.<p>
     * - If the two objects do not match the above criteria, the equals() method
     * on the first object is used to compare it to the second object.<p>
     *
     * @param anObject1 the first object to compare.
     * @param anObject2 the second object to compare.
     * @param aContext a String representing the comparison context (e.g., "mFieldX").
     *
     * @return normally if the two objects are equal as described above.
     *
     * @throws AssertionFailedError if some part of the two objects are not equal (via JUnit
     *  assert methods).
     */
    private static void checkEqual(Object anObject1, Object anObject2, String aContext)
    {
        // Both null or Same object - possible because of recursive refs
        if (anObject1 == anObject2) {
            return;
        }
        
        // At this point, both must be non-null.
        assertTrue("checkEqual: Both must be non-null: " + aContext, anObject1 != null && anObject2 != null);
        
        Class object1Class = anObject1.getClass();
        Class object2Class = anObject2.getClass();
        // Classes must be the same. Special SCO case: Allow the parent class to be equal to the direct class.
        assertTrue("checkEqual: Must be of the same class, or SCO must have the same parent class:" + aContext, 
            object1Class.equals(object2Class) || 
            object1Class.equals( object2Class.getSuperclass() ) ||
            object2Class.equals( object1Class.getSuperclass()) );
        
        // If the objects are TestClass1 objects, don't recursively compare them
        // if the object IDs match. Otherwise self references will cause infinite 
        // recursion. The assumption is that TestClass1.equals() will be used to 
        // initiate the calls to checkEquals() on TestClass1 fields.
        if (anObject1 instanceof TestClass1 && 
            EnerJImplementation.getEnerJObjectId(anObject1) == EnerJImplementation.getEnerJObjectId(anObject2)) {
            return;
        }
        
        if (object1Class.isArray()) {
            int length = Array.getLength(anObject1);
            assertTrue("checkEqual: array lengths must be the same: " + aContext,
                length == Array.getLength(anObject2));
            
            for (int i = 0; i < length; ++i) {
                checkEqual(Array.get(anObject1, i), Array.get(anObject2, i), aContext + '[' + i + ']');
            }
            
            return;
        }
        else if (anObject1 instanceof HashSet) {
            // HashSet doesn't care about ordering
            Set set1 = (Set)anObject1;
            Set set2 = (Set)anObject2;
            int size = set1.size();
            assertTrue("checkEqual: HashSet sizes must be the same:" + aContext,
                size == set2.size());
            
            Iterator iterator1 = set1.iterator();
            while (iterator1.hasNext()) {
                Object key = iterator1.next();
                assertTrue("checkEqual: HashSet must contain key '" + key + "': " + aContext,
                    set2.contains(key));
            }
            
            return;
        }
        else if (anObject1 instanceof Collection) {
            Collection collection1 = (Collection)anObject1;
            Collection collection2 = (Collection)anObject2;
            int size = collection1.size();
            assertTrue("checkEqual: Collection sizes must be the same: " + aContext,
                size == collection2.size());
            
            Iterator iterator1 = collection1.iterator();
            Iterator iterator2 = collection2.iterator();
            for (int i = 0; iterator1.hasNext(); i++) {
                checkEqual(iterator1.next(), iterator2.next(), aContext + ".get(" + i + ')');
            }
            
            return;
        }
        else if (anObject1 instanceof IdentityHashMap) {
            // IdentityHashMaps are a special case. We can't simply do lookup on one map
            // with a key from another because the key's JVM identity is going to be different
            // between the two sets. What we have to do is iterate over one map and then
            // for each key iterate over the second map to find the same key value.
            // Once we find it, we compare the values.
            Map map1 = (Map)anObject1;
            Map map2 = (Map)anObject2;
            int size = map1.size();
            assertTrue("checkEqual: Map sizes must be the same: " + aContext,
                size == map2.size());
            
            Iterator iterator1 = map1.entrySet().iterator();
            while (iterator1.hasNext()) {
                Map.Entry entry1 = (Map.Entry)iterator1.next();
                Object key1 = entry1.getKey();
                
                // Try to find key in second map.
                Iterator iterator2 = map2.entrySet().iterator();
                boolean keyFound = false;
                while (iterator2.hasNext()) {
                    Map.Entry entry2 = (Map.Entry)iterator2.next();
                    Object key2 = entry2.getKey();
                    if ((key1 == null && key2 == null) ||
                        (key1 != null && key1.equals(key2))) {
                        // Found matching key, are values equal?
                        keyFound = true;
                        checkEqual(entry1.getValue(), entry2.getValue(), aContext + ".get('" + key1 + "')");
                    }
                }

                assertTrue("checkEqual: Map must contain key '" + key1 + "': " + aContext, keyFound);
            }
            
            return;
        }
        else if (anObject1 instanceof Map) {
            Map map1 = (Map)anObject1;
            Map map2 = (Map)anObject2;
            int size = map1.size();
            assertTrue("checkEqual: Map sizes must be the same: " + aContext,
                size == map2.size());
            
            Iterator iterator1 = map1.entrySet().iterator();
            while (iterator1.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator1.next();
                Object key = entry.getKey();
                // value may be null, so simply saying map2.get(key) and comparing values
                // is not enough. get() can return null if the key doesn't exist in the map.
                assertTrue("checkEqual: Map must contain key '" + key + "': " + aContext,
                    map2.containsKey(key));
                checkEqual(entry.getValue(), map2.get(key), aContext + ".get('" + key + "')");
            }
            
            return;
        }
        else {
            assertTrue("checkEqual: Objects must be equal: " + aContext + 
                ". anObject1(" + anObject1.getClass() + ")=" + anObject1 + 
                ", anObject2(" + anObject2.getClass() + ")=" + anObject2, 
                anObject1.equals(anObject2) );
        }
    }
    
    //----------------------------------------------------------------------
    /** 
     * Gets a random object for fillCollection.
     */
    private static Object createRandomObject(int aNestedCollectionDepth)
    {
        ++aNestedCollectionDepth;
        switch ((int)(Math.random() * 12.)) {
        case 0:
            return null;

        case 1:
            return "Yow!" + Math.random();

        case 2:
            return new java.util.Date( (long)(Math.random() * 3483822.) );

        case 3:
            return new int[] { 4, 223, (int)(Math.random() * 19202.), 66, 77 };

        case 4:
            return new Long[] { new Long(328L), new Long(123L), new Long(654) };

        case 5:
            ArrayList list = new ArrayList(200);
            if (aNestedCollectionDepth < 2) {
                fillCollection(list, aNestedCollectionDepth);
            }
            return list;

        case 6:
            return new java.math.BigInteger("-237848901237489012734890127348907123489012893471293847128903" + 
                (int)(Math.random() * 23749829.));

        case 7:
            return new java.math.BigDecimal("-23749012378949812323423441.12348912734891273401093274102933478103" + 
                (int)(Math.random() * 217492389.));

        case 8:
            HashMap map = new HashMap(150);
            if (aNestedCollectionDepth < 2) {
                fillMap(map, aNestedCollectionDepth);
            }
            return map;

        case 9:
            return new java.util.Locale("yw", "US" + (int)(Math.random() * 23.), "dk");

        case 10:
            return new java.sql.Time( (long)(Math.random() * 238829292.) );

        case 11:
            java.sql.Timestamp ts = new java.sql.Timestamp( (long)(Math.random() * 238829292.) );
            ts.setNanos( (int)(Math.random() * 28829292.) );
            return ts;
        }

        throw new RuntimeException("Shouldn't happen");
    }
    
    //----------------------------------------------------------------------
    /** 
     * Fills a collection with a random set of values.
     */
    private static void fillCollection(Collection aCollection, int aNestedCollectionDepth)
    {
        int len = (int)(Math.random() * ((aNestedCollectionDepth > 0) ? 10. : 1000.));
        
        for (int i = 0; i < len; i++) {
            aCollection.add( createRandomObject(aNestedCollectionDepth) );
        }
    }
    
    //----------------------------------------------------------------------
    /** 
     * Fills a map with a random set of key/values.
     */
    private static void fillMap(Map aMap, int aNestedCollectionDepth)
    {
        int len = (int)(Math.random() * ((aNestedCollectionDepth > 0) ? 10. : 1000.));
        // Map keys usually must be the same type, so use a date.
        for (int i = 0; i < len; i++) {
            Object value = createRandomObject(aNestedCollectionDepth);
            // Hashtable doesn't allow null values (why??) so subvert the code here.
            if (value == null) {
                value = new Integer(0);
            }

            aMap.put( new Date((long)(Math.random() * 123198237918227.)), value );
        }
    }
    
    //----------------------------------------------------------------------
    /** 
     * Fills a set with a random set of values. Values are all of the same type.
     */
    private static void fillSet(Collection aCollection, int aNestedCollectionDepth)
    {
        int len = (int)(Math.random() * ((aNestedCollectionDepth > 0) ? 10. : 1000.));
        for (int i = 0; i < len; i++) {
           aCollection.add( new Date((long)(Math.random() * 123198237918227.)) );
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Write aPersistable to database and then reload back into a new object.
     *
     * @throws Exception when something unexpected occurs.
     */
    public static Persistable storeAndLoad(EnerJDatabase aDatabase, Persistable aPersistable) throws Exception
    {
        long oid = aDatabase.getOID(aPersistable);
        aDatabase.storePersistable(aPersistable);

        EnerJImplementation.getInstance().currentEnerJTransaction().flush();
        aDatabase.evictAll();

        Persistable persistable2 = aDatabase.getObjectForOID(oid);
        assertTrue("Object should not be loaded, and not be new", !persistable2.enerj_IsLoaded() && !persistable2.enerj_IsNew());
        
        return persistable2;
    }
    
    //----------------------------------------------------------------------
    /**
     * Make sure basic write/read serialization works.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testBasicSerialization() throws Exception
    {
        EnerJDatabase database = (EnerJDatabase)EnerJImplementation.getInstance().newDatabase();
        database.open(DATABASE_URI, EnerJDatabase.OPEN_READ_WRITE);
        
        org.odmg.Transaction txn = EnerJImplementation.getInstance().newTransaction();
        txn.begin();

        // Persist with values.
        TestClass1 testObj = new TestClass1();
        Persistable persistable = (Persistable)(Object)testObj;
        PersistableHelper.setNonTransactional(persistable);
        
        testObj.setFieldValues();
        
        Persistable persistable2 = storeAndLoad(database, persistable);
        PersistableHelper.setNonTransactional(persistable2);
        
        TestClass1 testObj2 = (TestClass1)persistable2;
        
        assertTrue("Should be independent objects", persistable != persistable2);
        testObj.isEqualTo(persistable2);
        assertTrue("OIDs should be the same", database.getOID(persistable) == database.getOID(persistable2) );
        assertTrue("Shared SCOs should be the same", testObj2.isDupDateRefSameAsUtilDate() );

        txn.abort();
        database.close();
    }
    
    //----------------------------------------------------------------------
    /**
     * Make sure write/read serialization of nulls works.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testNullValues() throws Exception
    {
        EnerJDatabase database = (EnerJDatabase)EnerJImplementation.getInstance().newDatabase();
        database.open(DATABASE_URI, EnerJDatabase.OPEN_READ_WRITE);
        
        org.odmg.Transaction txn = EnerJImplementation.getInstance().newTransaction();
        txn.begin();

        // Persist with values.
        TestClass1 testObj = new TestClass1();
        Persistable persistable = (Persistable)(Object)testObj;
        PersistableHelper.setNonTransactional(persistable);
        
        testObj.setObjectsToNull();
        
        Persistable persistable2 = storeAndLoad(database, persistable);
        PersistableHelper.setNonTransactional(persistable2);
        
        assertTrue("Should be independent objects", persistable != persistable2);
        testObj.isEqualTo(persistable2);
        assertTrue("OIDs should be the same", database.getOID(persistable) == database.getOID(persistable2) );

        txn.abort();
        database.close();
    }
    
    //----------------------------------------------------------------------
    /**
     * Make sure write/read of BigDecimal works properly.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testBigDecimal() throws Exception
    {
        EnerJDatabase database = (EnerJDatabase)EnerJImplementation.getInstance().newDatabase();
        database.open(DATABASE_URI, EnerJDatabase.OPEN_READ_WRITE);
        
        org.odmg.Transaction txn = EnerJImplementation.getInstance().newTransaction();
        txn.begin();

        // Persist with values.
        TestClass1 testObj = new TestClass1();
        Persistable persistable = (Persistable)(Object)testObj;
        PersistableHelper.setNonTransactional(persistable);
        
        testObj.setObjectsToNull();
        testObj.setZeroScaleBigDecimal();
        
        Persistable persistable2 = storeAndLoad(database, persistable);
        PersistableHelper.setNonTransactional(persistable2);
        
        assertTrue("Should be independent objects", persistable != persistable2);
        testObj.isEqualTo(persistable2);
        assertTrue("OIDs should be the same", database.getOID(persistable) == database.getOID(persistable2) );

        txn.abort();
        database.close();
    }
    
    //----------------------------------------------------------------------
    /**
     * Make sure nested FCOs work.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testNestedFCOs() throws Exception
    {
        EnerJDatabase database = (EnerJDatabase)EnerJImplementation.getInstance().newDatabase();
        database.open(DATABASE_URI, EnerJDatabase.OPEN_READ_WRITE);
        
        org.odmg.Transaction txn = EnerJImplementation.getInstance().newTransaction();
        txn.begin();

        // Persist with values.
        FCO1 testObj = new FCO1();
        Persistable persistable = (Persistable)(Object)testObj;
        PersistableHelper.setNonTransactional(persistable);
        
        testObj.setFieldValues(database);
        
        Persistable persistable2 = storeAndLoad(database, persistable);
        PersistableHelper.setNonTransactional(persistable2);
        
        assertTrue("Should be independent objects", persistable != persistable2);
        testObj.isEqualTo(persistable2);
        assertTrue("OIDs should be the same", database.getOID(persistable) == database.getOID(persistable2) );

        txn.abort();
        database.close();
    }
    
    //----------------------------------------------------------------------
    /**
     * Make sure TreeSet/Maps with Comparators work.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testTreesWithComparators() throws Exception
    {
        EnerJDatabase database = (EnerJDatabase)EnerJImplementation.getInstance().newDatabase();
        database.open(DATABASE_URI, EnerJDatabase.OPEN_READ_WRITE);
        
        org.odmg.Transaction txn = EnerJImplementation.getInstance().newTransaction();
        txn.begin();

        // Persist with values.
        FCO1 testObj = new FCO1();
        Persistable persistable = (Persistable)(Object)testObj;
        PersistableHelper.setNonTransactional(persistable);
        
        testObj.createTreesWithComparator();
        testObj.dumpTrees();
        
        Persistable persistable2 = storeAndLoad(database, persistable);
        PersistableHelper.setNonTransactional(persistable2);
        
        assertTrue("Should be independent objects", persistable != persistable2);
        testObj.isEqualTo(persistable2);
        assertTrue("OIDs should be the same", database.getOID(persistable) == database.getOID(persistable2) );
        
        FCO1 testObj2 = (FCO1)persistable2;
        testObj2.dumpTrees(); // This causes it to load and recreate the trees

        txn.abort();
        database.close();
    }
    
    //----------------------------------------------------------------------
    /**
     * Make sure TreeSet/Maps with Bad Comparators throw exception.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testTreesWithBadComparators() throws Exception
    {
        EnerJDatabase database = (EnerJDatabase)EnerJImplementation.getInstance().newDatabase();
        database.open(DATABASE_URI, EnerJDatabase.OPEN_READ_WRITE);
        
        org.odmg.Transaction txn = EnerJImplementation.getInstance().newTransaction();
        txn.begin();

        // Persist with values.
        FCO1 testObj = new FCO1();
        Persistable persistable = (Persistable)(Object)testObj;
        PersistableHelper.setNonTransactional(persistable);
        
        testObj.createTreesWithBadComparator();
        
        try {
            Persistable persistable2 = storeAndLoad(database, persistable);
            PersistableHelper.setNonTransactional(persistable2);
            ((FCO1)persistable2).dumpTrees(); // This causes it to load and recreate the trees.
            fail("Should throw org.odmg.ODMGRuntimeException");
        }
        catch (org.odmg.ODMGRuntimeException e) {
            // OK.
            System.out.println(e);
        }
        

        txn.abort();
        database.close();
    }
    
    //----------------------------------------------------------------------
    /**
     * Make sure basic write/read serialization works on a Non-Top-Level
     * Persistable.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testBasicSerializationNTLP() throws Exception
    {
        EnerJDatabase database = (EnerJDatabase)EnerJImplementation.getInstance().newDatabase();
        database.open(DATABASE_URI, EnerJDatabase.OPEN_READ_WRITE);
        
        org.odmg.Transaction txn = EnerJImplementation.getInstance().newTransaction();
        txn.begin();

        // Persist with values.
        TestClass2 testObj = new TestClass2();
        Persistable persistable = (Persistable)(Object)testObj;
        PersistableHelper.setNonTransactional(persistable);
        
        testObj.setFieldValues();
        
        Persistable persistable2 = storeAndLoad(database, persistable);
        PersistableHelper.setNonTransactional(persistable2);
        
        assertTrue("Should be independent objects", persistable != persistable2);
        testObj.isEqualTo(persistable2);
        assertTrue("OIDs should be the same", database.getOID(persistable) == database.getOID(persistable2) );

        txn.abort();
        database.close();
    }
    
    //----------------------------------------------------------------------
    /**
     * Make sure write/read serialization of nulls works on a Non-Top-Level
     * Persistable.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testNullValuesNTLP() throws Exception
    {
        EnerJDatabase database = (EnerJDatabase)EnerJImplementation.getInstance().newDatabase();
        database.open(DATABASE_URI, EnerJDatabase.OPEN_READ_WRITE);
        
        org.odmg.Transaction txn = EnerJImplementation.getInstance().newTransaction();
        txn.begin();

        // Persist with values.
        TestClass2 testObj = new TestClass2();
        Persistable persistable = (Persistable)(Object)testObj;
        PersistableHelper.setNonTransactional(persistable);
        
        testObj.setObjectsToNull();
        
        Persistable persistable2 = storeAndLoad(database, persistable);
        PersistableHelper.setNonTransactional(persistable2);
        
        assertTrue("Should be independent objects", persistable != persistable2);
        testObj.isEqualTo(persistable2);
        assertTrue("OIDs should be the same", database.getOID(persistable) == database.getOID(persistable2) );

        txn.abort();
        database.close();
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    @Persist
    private static class TestClass1
    {
        // These should not be persistent
        private final String mTestNonPersistentFinal = "";  // final
        static String mTestNonPersistentStatic;        // static
        static String mTestNonPersistentStatic2;       // static
        transient int mTestNonPersistentTransient;     // transient - not overridden

        // These should be persistent
        // Test scopes
        private int mPrivateInt;
        public int mPublicInt;
        protected int mProtectedInt;
        int mPackageInt;

        // Test types
        private byte mByte;
        private boolean mBoolean;
        private char mChar;
        private short mShort;
        private int mInt;
        private long mLong;
        private float mFloat;
        private double mDouble;

        // Test primitive wrapper types
        private Byte mByteObj;
        private Boolean mBooleanObj;
        private Character mCharObj;
        private Short mShortObj;
        private Integer mIntObj;
        private Long mLongObj;
        private Float mFloatObj;
        private Double mDoubleObj;

        // Test rest of the immutable SCOs
        private String mString;
        private java.math.BigDecimal mBigDecimal;
        private java.math.BigInteger mBigInteger;
        private java.util.Locale mLocale;

        // Test mutable SCOs
        private byte[] mByteArray;
        private boolean[] mBooleanArray;
        private char[] mCharArray;
        private short[] mShortArray;
        private int[] mIntArray;
        private long[] mLongArray;
        private float[] mFloatArray;
        private double[] mDoubleArray;
        private String[] mStringSCOArray;
        private Object[] mObjectArray;
        private TestClass1[] mThisClassArray;
        private int[][] mInt2DArray;
        private Object[][] mObject2DArray;
        private int[][][][] mInt4DArray;
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
        private java.util.Date mDupDateRef;

        // Generic type - unknown until runtime.
        private Object mObject;
        
        // Persistent fields - as overridden by metadata
        @Persist(true)
        transient private int mIntOverriddenPersistent1;
        // this already is persistent, but metadata redundantly says so.
        @Persist(true)
        private int mIntOverriddenPersistent2;  
        
        // Non persistent fields - as overridden by metadata
        @Persist(false)
        private int mIntOverriddenNonPersistent1;
        // this already is non-persistent, but metadata redundantly says so.
        @Persist(false)
        transient private int mIntOverriddenNonPersistent2;

        //----------------------------------------------------------------------
        TestClass1()
        {
        }

        //----------------------------------------------------------------------
        void setFieldValues()
        {
            mPrivateInt = 5;
            mPublicInt = 10;
            mProtectedInt = Integer.MAX_VALUE;
            mPackageInt = Integer.MIN_VALUE;
            mByte = 124;
            mBoolean = true;
            mChar = 'Z';
            mShort = 3832;
            mInt = -123;
            mLong = 4321L;
            mFloat = 23.432F;
            mDouble = 848423.238;

            mByteObj = new Byte((byte)12);
            mBooleanObj = new Boolean(false);
            mCharObj = new Character('C');
            mShortObj = new Short((short)-88);
            mIntObj = new Integer(65);
            mLongObj = new Long(234L);
            mFloatObj = new Float(.876F);
            mDoubleObj = new Double(-373.332);
            mString = new String("Some String");    // Use new explcitly
            mBigDecimal = new java.math.BigDecimal("-12345678923479238479283292468013579.98765432348923874273478236467821");
            mBigInteger = new java.math.BigInteger("102938475698765432975314567893487293487289323478293847923");
            mLocale = new java.util.Locale("en", "US", "win");

            mByteArray = new byte[] { 45, 23, -120, 34, 0, 66, 88, 38, 100, -105, Byte.MAX_VALUE, Byte.MIN_VALUE };
            mBooleanArray = new boolean[] { true, true, false, true, false, true, true, false, false };
            mCharArray = new char[] { 'A', 'E', 'I', 0, Character.MAX_VALUE, 'O', 'U', Character.MIN_VALUE };
            mShortArray = new short[] { Short.MAX_VALUE, Short.MIN_VALUE, 3488, 9120, -12, 0 };
            mIntArray = new int[] { 123, 0, -78373, 687, Integer.MIN_VALUE, Integer.MAX_VALUE, 282 };
            mLongArray = new long[] { Long.MAX_VALUE, Long.MIN_VALUE, 38, 2882910, 2892992, 0, -238839292 };
            mFloatArray = new float[] { Float.MIN_VALUE, Float.MAX_VALUE, 383F, 0.F, .999F, -3.14159F };
            mDoubleArray = new double[] { 23847.23847, 3.14159, Double.MAX_VALUE, -88., 0., Double.MIN_VALUE, 99.99 };

            mStringSCOArray = new String[] { "AString", null, "someotherstring", mString,  "foo" };
            mObjectArray = new Object[] { "CouldBeAString", new int[] { 1, 2, 3, 4 }, new Byte((byte)55), new ArrayList(), new java.util.Date() };
            // This is a direct recursive reference to self.
            mThisClassArray = new TestClass1[] { null, this };
            mInt2DArray = new int[][] { 
                { 1, 2, 3, 4, 5}, 
                { 9, 8, 7, 6, 5, 44}, 
                { -33, 44, 55, 66}, 
                null, 
                { 88 },
            };
            mObject2DArray = new Object[][] {
                { "Something", new String[] { "ABC", "EFG" }, new java.util.Date[0] },
                { new java.math.BigInteger("123842739847239478"), java.util.Locale.JAPANESE, new Double(334.) },  
            };
            // An array of 4000 zeros...
            mInt4DArray = new int[10][20][4][5];

            mUtilDate = new java.util.Date(); // Today 
            mDupDateRef = mUtilDate;
            mSqlDate = new java.sql.Date(28438L); 
            mSqlTime = new java.sql.Time(2384783121L);
            mSqlTimestamp = new java.sql.Timestamp(987654321L);
            mSqlTimestamp.setNanos(3243);

            mArrayList = new java.util.ArrayList();
            fillCollection(mArrayList, 0);
            mLinkedList = new java.util.LinkedList();
            fillCollection(mLinkedList, 0);
            mVector = new java.util.Vector();
            fillCollection(mVector, 0);
            mStack = new java.util.Stack();
            fillCollection(mStack, 0);
            mHashSet = new java.util.HashSet();
            fillSet(mHashSet, 0);
            mTreeSet = new java.util.TreeSet();
            fillSet(mTreeSet, 0);
            mLinkedHashSet = new java.util.LinkedHashSet();
            fillSet(mLinkedHashSet, 0);

            mHashMap = new java.util.HashMap();
            fillMap(mHashMap, 0);
            mHashtable = new java.util.Hashtable();
            fillMap(mHashtable, 0);
            mLinkedHashMap = new java.util.LinkedHashMap();
            fillMap(mLinkedHashMap, 0);
            mProperties = new java.util.Properties();
            int length = (int)(Math.random() * 32.);
            for (int i = 0; i < length; i++) {
                mProperties.setProperty("Key-" + Math.random(), "Value-" + Math.random());
            }

            mTreeMap = new java.util.TreeMap();
            fillMap(mTreeMap, 0);
            mIdentityHashMap = new java.util.IdentityHashMap();
            fillMap(mIdentityHashMap, 0);

            mObject = new java.util.Date(38283L);

            mIntOverriddenPersistent1 = (int)(Math.random() * 98721);
            mIntOverriddenPersistent2 = 932;
        }
        
        //----------------------------------------------------------------------
        /**
         * Check that the mDupDateRef is the same object as mUtilDate after
         * loading from the DB. It was setup this way before the object was 
         * persisted. This tests that SCOs shared within an FCO are reloaded
         * with the same semantics (shared identity).
         */
        boolean isDupDateRefSameAsUtilDate()
        {
            return mDupDateRef == mUtilDate;
        }
        
        //----------------------------------------------------------------------
        void setObjectsToNull()
        {
            mByteObj = null;
            mBooleanObj = null;
            mCharObj = null;
            mShortObj = null;
            mIntObj = null;
            mLongObj = null;
            mFloatObj = null;
            mDoubleObj = null;
            mString = null;
            mBigDecimal = null;
            mBigInteger = null;
            mLocale = null;
            mByteArray = null;
            mBooleanArray = null;
            mCharArray = null;
            mShortArray = null;
            mIntArray = null;
            mLongArray = null;
            mFloatArray = null;
            mDoubleArray = null;
            mStringSCOArray = null;
            mObjectArray = null;
            mThisClassArray = null;
            mInt2DArray = null;
            mObject2DArray = null;
            mInt4DArray = null;
            mUtilDate = null;
            mSqlDate = null;
            mSqlTime = null;
            mSqlTimestamp = null;
            mArrayList = null;
            mLinkedList = null;
            mTreeSet = null;
            mVector = null;
            mStack = null;
            mHashSet = null;
            mLinkedHashSet = null;
            mHashMap = null;
            mHashtable = null;
            mLinkedHashMap = null;
            mProperties = null;
            mTreeMap = null;
            mIdentityHashMap = null;
            mObject = null;
        }
        
        //----------------------------------------------------------------------
        public void setZeroScaleBigDecimal()
        {
            mBigDecimal = new java.math.BigDecimal("12345678923479238479283292468013579");
            mBigDecimal.setScale(0);
        }

        //----------------------------------------------------------------------
        /**
         * Tests for equality and returns normally if the objects are equal, or
         * throws AssertionFailedError if they are not.
         */
        public void isEqualTo(Object anObject)
        {
            assertTrue("TestClass1.areEqual: must be a TestClass1 object and target object must not be null",
                anObject instanceof TestClass1 && anObject != null);
            
            if (anObject == this) {
                return;
            }
            
            TestClass1 target = (TestClass1)anObject;
            // At this point, we know 'this' and target are not null

            assertTrue("TestClass1.areEqual: primitive fields must be equal",
                mPrivateInt == target.mPrivateInt &&
                mPublicInt == target.mPublicInt &&
                mProtectedInt == target.mProtectedInt &&
                mPackageInt == target.mPackageInt &&
                mByte == target.mByte &&
                mBoolean == target.mBoolean &&
                mChar == target.mChar &&
                mShort == target.mShort &&
                mInt == target.mInt &&
                mLong == target.mLong &&
                mFloat == target.mFloat &&
                mDouble == target.mDouble &&
                mIntOverriddenPersistent1 == target.mIntOverriddenPersistent1 &&
                mIntOverriddenPersistent2 == target.mIntOverriddenPersistent2);

            checkEqual(mByteObj, target.mByteObj, "mByteObj");
            checkEqual(mBooleanObj, target.mBooleanObj, "mBooleanObj");
            checkEqual(mCharObj, target.mCharObj, "mCharObj");
            checkEqual(mShortObj, target.mShortObj, "mShortObj");
            checkEqual(mIntObj, target.mIntObj, "mIntObj");
            checkEqual(mLongObj, target.mLongObj, "mLongObj");
            checkEqual(mFloatObj, target.mFloatObj, "mFloatObj");
            checkEqual(mDoubleObj, target.mDoubleObj, "mDoubleObj");
            checkEqual(mString, target.mString, "mString");
            checkEqual(mBigDecimal, target.mBigDecimal, "mBigDecimal");
            checkEqual(mBigInteger, target.mBigInteger, "mBigInteger");
            checkEqual(mLocale, target.mLocale, "mLocale");
            checkEqual(mByteArray, target.mByteArray, "mByteArray");
            checkEqual(mBooleanArray, target.mBooleanArray, "mBooleanArray");
            checkEqual(mCharArray, target.mCharArray, "mCharArray");
            checkEqual(mShortArray, target.mShortArray, "mShortArray");
            checkEqual(mIntArray, target.mIntArray, "mIntArray");
            checkEqual(mLongArray, target.mLongArray, "mLongArray");
            checkEqual(mFloatArray, target.mFloatArray, "mFloatArray");
            checkEqual(mDoubleArray, target.mDoubleArray, "mDoubleArray");
            checkEqual(mStringSCOArray, target.mStringSCOArray, "mStringSCOArray");
            checkEqual(mObjectArray, target.mObjectArray, "mObjectArray");
            checkEqual(mThisClassArray, target.mThisClassArray, "mThisClassArray");
            checkEqual(mInt2DArray, target.mInt2DArray, "mInt2DArray");
            checkEqual(mObject2DArray, target.mObject2DArray, "mObject2DArray");
            checkEqual(mInt4DArray, target.mInt4DArray, "mInt4DArray");
            checkEqual(mUtilDate, target.mUtilDate, "mUtilDate");
            checkEqual(mSqlDate, target.mSqlDate, "mSqlDate");
            checkEqual(mSqlTime, target.mSqlTime, "mSqlTime");
            checkEqual(mSqlTimestamp, target.mSqlTimestamp, "mSqlTimestamp");
            checkEqual(mArrayList, target.mArrayList, "mArrayList");
            checkEqual(mLinkedList, target.mLinkedList, "mLinkedList");
            checkEqual(mTreeSet, target.mTreeSet, "mTreeSet");
            checkEqual(mVector, target.mVector, "mVector");
            checkEqual(mStack, target.mStack, "mStack");
            checkEqual(mHashSet, target.mHashSet, "mHashSet");
            checkEqual(mLinkedHashSet, target.mLinkedHashSet, "mLinkedHashSet");
            checkEqual(mHashMap, target.mHashMap, "mHashMap");
            checkEqual(mHashtable, target.mHashtable, "mHashtable");
            checkEqual(mLinkedHashMap, target.mLinkedHashMap, "mLinkedHashMap");
            checkEqual(mProperties, target.mProperties, "mProperties");
            checkEqual(mTreeMap, target.mTreeMap, "mTreeMap");
            checkEqual(mIdentityHashMap, target.mIdentityHashMap, "mIdentityHashMap");
            checkEqual(mObject, target.mObject, "mObject");
        }
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    @Persist
    private static class TestClass2 extends TestClass1
    {
        private byte mByte;
        private boolean mBoolean;
        private char mChar;
        private short mShort;
        private int mInt;
        private long mLong;
        private float mFloat;
        private double mDouble;
        private Byte mByteObj;

        //----------------------------------------------------------------------
        TestClass2()
        {
        }

        //----------------------------------------------------------------------
        void setFieldValues()
        {
            super.setFieldValues();
            mByte = 12;
            mBoolean = false;
            mChar = 'A';
            mShort = 32;
            mInt = -13;
            mLong = 421L;
            mFloat = 23332.432F;
            mDouble = 3423.238;

            mByteObj = new Byte((byte)23);
        }

        //----------------------------------------------------------------------
        void setObjectsToNull()
        {
            super.setObjectsToNull();
            mByteObj = null;
        }

        //----------------------------------------------------------------------
        /**
         * Tests for equality and returns normally if the objects are equal, or
         * throws AssertionFailedError if they are not.
         */
        public void isEqualTo(Object anObject)
        {
            assertTrue("TestClass2.areEqual: must be a TestClass2 object and target object must not be null",
                anObject instanceof TestClass2 && anObject != null);
            
            if (anObject == this) {
                return;
            }
            
            TestClass2 target = (TestClass2)anObject;
            // At this point, we know 'this' and target are not null

            assertTrue("TestClass2.areEqual: primitive fields must be equal",
                mByte == target.mByte &&
                mBoolean == target.mBoolean &&
                mChar == target.mChar &&
                mShort == target.mShort &&
                mInt == target.mInt &&
                mLong == target.mLong &&
                mFloat == target.mFloat &&
                mDouble == target.mDouble);

            checkEqual(mByteObj, target.mByteObj, "mByteObj");
        }
    }

    //----------------------------------------------------------------------
    private static class TreeComparator implements Comparator
    {
        TreeComparator()
        {
        }
        
        public int compare(Object anObject1, Object anObject2)
        {
            String string1 = (String)anObject1;
            String string2 = (String)anObject2;
            
            return string1.compareTo(string2);
        }
    }
    
    //----------------------------------------------------------------------
    private static class BadTreeComparator implements Comparator
    {
        BadTreeComparator(int aValue)
        {
        }
        
        public int compare(Object anObject1, Object anObject2)
        {
            String string1 = (String)anObject1;
            String string2 = (String)anObject2;
            
            return string1.compareTo(string2);
        }
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    @Persist
    private static class FCO1
    {
        private FCO2 mFCO2Ref;
        private TreeSet mTreeSet;
        private TreeMap mTreeMap;

        //----------------------------------------------------------------------
        FCO1()
        {
        }

        //----------------------------------------------------------------------
        void createTreesWithComparator()
        {
            Comparator comparator = new TreeComparator();
            mTreeSet = new TreeSet(comparator);
            mTreeMap = new TreeMap(comparator);
            
            mTreeSet.add("Cat");
            mTreeSet.add("Dog");
            mTreeSet.add("Animal");
            mTreeSet.add("Zebra");
            
            mTreeMap.put("Cat", "Meow");
            mTreeMap.put("Dog", "Bark");
            mTreeMap.put("Animal", "Sniff");
            mTreeMap.put("Zebra", "Neigh");
        }
        
        //----------------------------------------------------------------------
        void createTreesWithBadComparator()
        {
            Comparator comparator = new BadTreeComparator(5);
            mTreeSet = new TreeSet(comparator);
            mTreeMap = new TreeMap(comparator);
            
            mTreeSet.add("Cat");
            mTreeSet.add("Dog");
            mTreeSet.add("Animal");
            mTreeSet.add("Zebra");
            
            mTreeMap.put("Cat", "Meow");
            mTreeMap.put("Dog", "Bark");
            mTreeMap.put("Animal", "Sniff");
            mTreeMap.put("Zebra", "Neigh");
        }
        
        //----------------------------------------------------------------------
        void dumpTrees()
        {
            System.out.println("Maps---->");
            Iterator iterator = mTreeSet.iterator();
            for (int i = 0; iterator.hasNext(); ++i) {
                System.out.println("mTreeSet[" + i + "]=" + iterator.next());
            }
            iterator = mTreeMap.entrySet().iterator();
            for (int i = 0; iterator.hasNext(); ++i) {
                Map.Entry entry = (Map.Entry)iterator.next();
                System.out.println("mTreeMap[" + i + "]=" + entry.getKey() + " value=" + entry.getValue());
            }
            System.out.println("<----");
        }
        
        //----------------------------------------------------------------------
        void setFieldValues(EnerJDatabase aDatabase) throws Exception
        {
            mFCO2Ref = new FCO2();
            PersistableHelper.setNonTransactional(mFCO2Ref);
            mFCO2Ref.setFieldValues(aDatabase);
            storeAndLoad(aDatabase, (Persistable)(Object)mFCO2Ref);
        }

        //----------------------------------------------------------------------
        void setObjectsToNull()
        {
            mFCO2Ref = null;
        }

        //----------------------------------------------------------------------
        /**
         * Tests for equality and returns normally if the objects are equal, or
         * throws AssertionFailedError if they are not.
         */
        public void isEqualTo(Object anObject)
        {
            assertTrue("FCO1.areEqual: must be a FCO1 object and target object must not be null",
                anObject instanceof FCO1 && anObject != null);
            
            if (anObject == this) {
                return;
            }
            
            FCO1 target = (FCO1)anObject;
            // At this point, we know 'this' and target are not null
            PersistableHelper.setNonTransactional(target.mFCO2Ref);

            if (mFCO2Ref == null) {
                if (target.mFCO2Ref == null) {
                    return;
                }
                
                fail("mFCO2Ref and target.mFCO2Ref are not both null");
            }
            
            mFCO2Ref.isEqualTo(target.mFCO2Ref);
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    @Persist
    private static class FCO2
    {
        private FCO3 mFCO3Ref;

        //----------------------------------------------------------------------
        FCO2()
        {
        }

        //----------------------------------------------------------------------
        void setFieldValues(EnerJDatabase aDatabase) throws Exception
        {
            mFCO3Ref = new FCO3();
            PersistableHelper.setNonTransactional(mFCO3Ref);
            mFCO3Ref.setFieldValues();
            storeAndLoad(aDatabase, (Persistable)(Object)mFCO3Ref);
        }

        //----------------------------------------------------------------------
        void setObjectsToNull()
        {
            mFCO3Ref = null;
        }

        //----------------------------------------------------------------------
        /**
         * Tests for equality and returns normally if the objects are equal, or
         * throws AssertionFailedError if they are not.
         */
        public void isEqualTo(Object anObject)
        {
            assertTrue("FCO2.areEqual: must be a FCO2 object and target object must not be null",
                anObject instanceof FCO2 && anObject != null);
            
            if (anObject == this) {
                return;
            }
            
            FCO2 target = (FCO2)anObject;
            // At this point, we know 'this' and target are not null

            PersistableHelper.setNonTransactional(target.mFCO3Ref);
            if (mFCO3Ref == null) {
                if (target.mFCO3Ref == null) {
                    return;
                }
                
                fail("mFCO3Ref and target.mFCO3Ref are not both null");
            }
            
            mFCO3Ref.isEqualTo(target.mFCO3Ref);
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    @Persist
    private static class FCO3
    {
        private int aValue;

        //----------------------------------------------------------------------
        FCO3()
        {
        }

        //----------------------------------------------------------------------
        void setFieldValues()
        {
            aValue = 38238;
        }

        //----------------------------------------------------------------------
        void setObjectsToNull()
        {
        }

        //----------------------------------------------------------------------
        /**
         * Tests for equality and returns normally if the objects are equal, or
         * throws AssertionFailedError if they are not.
         */
        public void isEqualTo(Object anObject)
        {
            assertTrue("FCO3.areEqual: must be a FCO3 object and target object must not be null",
                anObject instanceof FCO3 && anObject != null);
            
            if (anObject == this) {
                return;
            }
            
            FCO3 target = (FCO3)anObject;
            // At this point, we know 'this' and target are not null

            assertTrue("aValue must be the same", aValue == target.aValue);
        }
    }

}

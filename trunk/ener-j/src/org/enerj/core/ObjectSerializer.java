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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/ObjectSerializer.java,v 1.3 2006/05/30 19:05:26 dsyrstad Exp $

package org.enerj.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.enerj.sco.JavaSqlDateSCO;
import org.enerj.sco.JavaSqlTimeSCO;
import org.enerj.sco.JavaSqlTimestampSCO;
import org.enerj.sco.JavaUtilArrayListSCO;
import org.enerj.sco.JavaUtilDateSCO;
import org.enerj.sco.JavaUtilHashMapSCO;
import org.enerj.sco.JavaUtilHashSetSCO;
import org.enerj.sco.JavaUtilHashtableSCO;
import org.enerj.sco.JavaUtilIdentityHashMapSCO;
import org.enerj.sco.JavaUtilLinkedHashMapSCO;
import org.enerj.sco.JavaUtilLinkedHashSetSCO;
import org.enerj.sco.JavaUtilLinkedListSCO;
import org.enerj.sco.JavaUtilPropertiesSCO;
import org.enerj.sco.JavaUtilStackSCO;
import org.enerj.sco.JavaUtilTreeMapSCO;
import org.enerj.sco.JavaUtilTreeSetSCO;
import org.enerj.sco.JavaUtilVectorSCO;
import org.enerj.sco.SCOTracker;

/**
 * Helper class for Persistable.enerj_ReadObject and enerj_WriteObject. 
 * Handles internal serialization and representation of objects.
 *
 * @version $Id: ObjectSerializer.java,v 1.3 2006/05/30 19:05:26 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class ObjectSerializer
{
    /** First available user OID. */
    public static final long FIRST_USER_OID = 1000L;
    /** Last available system CID. CIDs from 1 to this value are reserved for pre-enhanced system classes. */
    public static final long LAST_SYSTEM_CID = 1000L;
    /** System OID: the null OID. */
    public static final long NULL_OID = 0L;
    /** Null Class Id (CID). */
    public static final long NULL_CID = 0L;

    // Type ID markers (a psuedo-class ID). Mainly for SCOs.
    // ! ! ! NOTE ! ! ! - The assigned numbers must NEVER change!
    private static final byte sNull_TypeId                        =   0;
    private static final byte sFCO_TypeId                         =   1;
    private static final byte sSharedSCO_TypeId                   =   2;
    private static final byte sByteArray_TypeId                   =   3;
    private static final byte sBooleanArray_TypeId                =   4;
    private static final byte sCharArray_TypeId                   =   5;
    private static final byte sShortArray_TypeId                  =   6;
    private static final byte sIntArray_TypeId                    =   7;
    private static final byte sLongArray_TypeId                   =   8;
    private static final byte sFloatArray_TypeId                  =   9;
    private static final byte sDoubleArray_TypeId                 =  10;
    private static final byte sObjectArray_TypeId                 =  11;
    private static final byte sJava_lang_Byte_TypeId              =  12;
    private static final byte sJava_lang_Boolean_TypeId           =  13;
    private static final byte sJava_lang_Character_TypeId         =  14;
    private static final byte sJava_lang_Short_TypeId             =  15;
    private static final byte sJava_lang_Integer_TypeId           =  16;
    private static final byte sJava_lang_Long_TypeId              =  17;
    private static final byte sJava_lang_Float_TypeId             =  18;
    private static final byte sJava_lang_Double_TypeId            =  19;
    private static final byte sJava_lang_String_TypeId            =  20;
    private static final byte sJava_math_BigDecimal_TypeId        =  21;
    private static final byte sJava_math_BigInteger_TypeId        =  22;
    private static final byte sJava_util_Locale_TypeId            =  23;
    private static final byte sJava_util_Date_TypeId              =  24;
    private static final byte sJava_sql_Date_TypeId               =  25;
    private static final byte sJava_sql_Time_TypeId               =  26;
    private static final byte sJava_sql_Timestamp_TypeId          =  27;
    private static final byte sJava_util_ArrayList_TypeId         =  28;
    private static final byte sJava_util_LinkedList_TypeId        =  29;
    private static final byte sJava_util_TreeSet_TypeId           =  30;
    private static final byte sJava_util_Vector_TypeId            =  31;
    private static final byte sJava_util_Stack_TypeId             =  32;
    private static final byte sJava_util_HashSet_TypeId           =  33;
    private static final byte sJava_util_LinkedHashSet_TypeId     =  34;
    private static final byte sJava_util_HashMap_TypeId           =  35;
    private static final byte sJava_util_Hashtable_TypeId         =  36;
    private static final byte sJava_util_LinkedHashMap_TypeId     =  37;
    private static final byte sJava_util_Properties_TypeId        =  38;
    private static final byte sJava_util_TreeMap_TypeId           =  39;
    private static final byte sJava_util_IdentityHashMap_TypeId   =  40;
    private static final byte sJava_lang_Class_TypeId             =  41;  

    private static final Serializer sObjectArraySerializer = new ObjectArraySerializer();
    
    private static final Serializer[] sSCOSerializers = {
        new ByteArraySerializer(),
        new BooleanArraySerializer(),
        new CharArraySerializer(),
        new ShortArraySerializer(),
        new IntArraySerializer(),
        new LongArraySerializer(),
        new FloatArraySerializer(),
        new DoubleArraySerializer(),
        sObjectArraySerializer,
        new Java_lang_Byte_Serializer(),
        new Java_lang_Boolean_Serializer(),
        new Java_lang_Character_Serializer(),
        new Java_lang_Short_Serializer(),
        new Java_lang_Integer_Serializer(),
        new Java_lang_Long_Serializer(),
        new Java_lang_Float_Serializer(),
        new Java_lang_Double_Serializer(),
        new Java_lang_String_Serializer(),
        new Java_lang_Class_Serializer(),
        new Java_math_BigDecimal_Serializer(),
        new Java_math_BigInteger_Serializer(),
        new Java_util_Locale_Serializer(),
        new Java_util_Date_Serializer(),
        new Java_sql_Date_Serializer(),
        new Java_sql_Time_Serializer(),
        new Java_sql_Timestamp_Serializer(),
        new Java_util_ArrayList_Serializer(),
        new Java_util_LinkedList_Serializer(),
        new Java_util_TreeSet_Serializer(),
        new Java_util_Vector_Serializer(),
        new Java_util_Stack_Serializer(),
        new Java_util_HashSet_Serializer(),
        new Java_util_LinkedHashSet_Serializer(),
        new Java_util_HashMap_Serializer(),
        new Java_util_Hashtable_Serializer(),
        new Java_util_LinkedHashMap_Serializer(),
        new Java_util_Properties_Serializer(),
        new Java_util_TreeMap_Serializer(),
        new Java_util_IdentityHashMap_Serializer(),
    };
    
    private static final Class sByteArrayClass;
    private static final Class sBooleanArrayClass;
    private static final Class sCharArrayClass;
    private static final Class sShortArrayClass;
    private static final Class sIntArrayClass;
    private static final Class sLongArrayClass;
    private static final Class sFloatArrayClass;
    private static final Class sDoubleArrayClass;

    /** HashMap mapping SCO type id to corresponding Serializer object */
    private static HashMap sTypeIdToSerializer = new HashMap(sSCOSerializers.length);
    /** HashMap mapping SCO class to corresponding Serializer object */
    private static HashMap sClassToSerializer = new HashMap(sSCOSerializers.length);
    

    // Initialize HashMaps and array class types
    static {
        // This must be done prior to initializing the maps.
        try {
            sByteArrayClass = Class.forName("[B");
            sBooleanArrayClass = Class.forName("[Z");
            sCharArrayClass = Class.forName("[C");
            sShortArrayClass = Class.forName("[S");
            sIntArrayClass = Class.forName("[I");
            sLongArrayClass = Class.forName("[J");
            sFloatArrayClass = Class.forName("[F");
            sDoubleArrayClass = Class.forName("[D");
        }
        catch (ClassNotFoundException e) {
            // Shouldn't ever happen
            throw new RuntimeException("Couldn't derive array class: " + e);
        }

        for (int i = 0; i < sSCOSerializers.length; i++) {
            sTypeIdToSerializer.put( new Byte(sSCOSerializers[i].getTypeId()), sSCOSerializers[i]);
            sClassToSerializer.put(sSCOSerializers[i].getRepresentingClass(), sSCOSerializers[i]);
            Class proxy = sSCOSerializers[i].getProxyClass();
            if (proxy != null) {
                sClassToSerializer.put(proxy, sSCOSerializers[i]);
            }
        }
    }
    
    private ReadContext mReadContext;
    private WriteContext mWriteContext;
    

    /**
     * Construct a new ObjectSerializer for resolving objects.
     */
    public ObjectSerializer()
    {
    }
    

    /**
     * Construct a new ObjectSerializer for input.
     *
     * @param aDataInput the DataInput that provides the stream to read from.
     */
    public ObjectSerializer(DataInput aDataInput)
    {
        mReadContext = new ReadContext(aDataInput, this);
    }
    

    /**
     * Construct a new ObjectSerializer for output.
     *
     * @param aDataInput the DataInput that provides the stream to read from.
     * @param aPersister a Persister context to use for resolving objects.
     */
    public ObjectSerializer(DataOutput aDataOutput)
    {
        mWriteContext = new WriteContext(aDataOutput, this);
    }
    

    /**
     * Gets the DataInput stream associated with this serializer.
     * 
     * @return the DataInput stream associated with this serializer, or null if this serializer
     *  is not configured for input.
     */
    public DataInput getDataInput()
    {
        return (mReadContext == null ? null : mReadContext.mStream);
    }
    

    /**
     * Gets the DataOutput stream associated with this serializer.
     * 
     * @return the DataOutput stream associated with this serializer, or null if this serializer
     *  is not configured for output.
     */
    public DataOutput getDataOutput()
    {
        return (mWriteContext == null ? null : mWriteContext.mStream);
    }
    

    /**
     * Writes a Object to a stream.
     *
     * @param aValue the value to be written (either a SCO or FCO).
     * @param aPersistable the calling Persistable object (used for a Database context). This
     *  is the same as aValue if aValue is a FCO. Otherwise it is the owner FCO.
     *
     * @throws IOException if an error occurs
     */
    public void writeObject(Object aValue, Persistable aPersistable) throws IOException
    {
        if (aValue == null) {
            mWriteContext.mStream.writeByte(sNull_TypeId);
        }
        else if (aValue instanceof Persistable) {
            mWriteContext.mStream.writeByte(sFCO_TypeId);
            writeFCO(aValue);
        }
        else {
            // Is it a SCO
            // Have we written it already?
            Integer scoId = (Integer)mWriteContext.getSCOMap().get(aValue);
            if (scoId != null) {
                // SCO already been written, only write it's Id this time.
                mWriteContext.mStream.writeByte(sSharedSCO_TypeId);
                mWriteContext.mStream.writeInt( scoId.intValue() );
                return;
            }

            // First time writing this SCO
            Class valueClass = aValue.getClass();
            Serializer serializer = (Serializer)sClassToSerializer.get(valueClass);
            if (serializer == null) {
                if (valueClass.isArray()) {
                    // If we still have an array, it must be an array of Objects ("[L...;" or "[[...").
                    // Because of the variability of these classes, these class types are not in the 
                    // sClassToSerializer HashMap. Just set it up here.
                    serializer = sObjectArraySerializer;
                }
                else {
                    throw new org.odmg.ClassNotPersistenceCapableException("A persistent field of " + aPersistable.getClass() + " does not refer to a FCO nor SCO. Rather it refers to " + valueClass);
                }
            }

            mWriteContext.mStream.writeByte( serializer.getTypeId() );
            serializer.write(mWriteContext, aValue, aPersistable);
            // Add this SCO to the local context
            mWriteContext.getSCOMap().put(aValue, new Integer( mWriteContext.getSCOMap().size() ) );
        }
    }

    /**
     * Resolve the object's entire object graph recursively until all instances are fully loaded.
     * This allows the object's entire graph to be used without a dependence on the persister (i.e.,
     * {@link Persistable#enerj_GetPersister()} will return null.
     *
     * @param anObject the object to be resolved (either a SCO or FCO).
     * @param shouldDisassociate if true, the object tree will be disassociated from 
     *  its Persister.
     *
     * @throws IOException if an error occurs
     */
    public void resolveObject(Object anObject, boolean shouldDisassociate) throws IOException
    {
        if (anObject == null) {
            return;
        }
        else if (anObject instanceof Persistable) {
            PersistableHelper.resolveObject(this, (Persistable)anObject, shouldDisassociate);
        }
        else {
            // Is it a SCO
            Class valueClass = anObject.getClass();
            Serializer serializer = (Serializer)sClassToSerializer.get(valueClass);
            if (serializer == null) {
                if (valueClass.isArray()) {
                    // If we still have an array, it must be an array of Objects ("[L...;" or "[[...").
                    // Because of the variability of these classes, these class types are not in the 
                    // sClassToSerializer HashMap. Just set it up here.
                    serializer = sObjectArraySerializer;
                }
                else {
                    throw new org.odmg.ClassNotPersistenceCapableException("A persistent field of " + anObject.getClass() + " does not refer to a FCO nor SCO. Rather it refers to " + valueClass);
                }
            }

            serializer.resolve(this, anObject, shouldDisassociate);
        }
    }

    /**
     * Resolve the collection's entire object graph recursively until all instances are fully loaded.
     * This allows the collection's entire graph to be used without a dependence on the persister (i.e.,
     * {@link Persistable#enerj_GetPersister()} will return null.
     *
     * @param aCollection the collection to be resolved.
     * @param shouldDisassociate if true, the object tree will be disassociated from 
     *  its Persister.
     *
     * @throws IOException if an error occurs
     */
    public void resolveCollection(Collection aCollection, boolean shouldDisassociate) throws IOException
    {
        for (Object obj : aCollection) {
            resolveObject(obj, shouldDisassociate);
        }
    }
    
    /**
     * Resolve the map's entire object graph recursively until all instances are fully loaded.
     * This allows the map's entire graph to be used without a dependence on the persister (i.e.,
     * {@link Persistable#enerj_GetPersister()} will return null.
     *
     * @param aMap the map to be resolved.
     * @param shouldDisassociate if true, the object tree will be disassociated from 
     *  its Persister.
     *
     * @throws IOException if an error occurs
     */
    public void resolveMap(Map aMap, boolean shouldDisassociate) throws IOException
    {
        for (Map.Entry entry : (Set<Map.Entry>)aMap.entrySet()) {
            resolveObject(entry.getKey(), shouldDisassociate);
            resolveObject(entry.getValue(), shouldDisassociate);
        }
    }
    

    /**
     * Reads a Object from a stream.
     *
     * @param aPersistable the calling Persistable object (used for a Database context).
     *
     * @return the value (either a SCO or FCO).
     *
     * @throws IOException if an error occurs
     */
    public Object readObject(Persistable aPersistable) throws IOException
    {
        byte typeId = mReadContext.mStream.readByte();
        switch (typeId) {
        case sNull_TypeId:
            return null;

        case sFCO_TypeId:
            return readFCO(aPersistable);
            
        case sSharedSCO_TypeId:
            int scoId = mReadContext.mStream.readInt();
            try {
                return mReadContext.getSCOArray().get(scoId);
            }
            catch (IndexOutOfBoundsException e) {
                throw new org.odmg.ODMGRuntimeException("Internal: SCO Id " + scoId + " has not been read from the stream yet");
            }

        default:
            Serializer serializer = (Serializer)sTypeIdToSerializer.get( new Byte(typeId) );
            if (serializer == null) {
                throw new org.odmg.ODMGRuntimeException("Internal: unknown type id=" + typeId);
            }
            
            Object obj = serializer.read(mReadContext, aPersistable);
            // Add SCO to Id array
            mReadContext.getSCOArray().add(obj);
            return obj;
        }
        
        // Not reached
    }


    /**
     * Writes a FCO to a stream. 
     *
     * @param aValue the value to be written (a FCO).
     *
     * @throws IOException if an error occurs
     */
    private void writeFCO(Object aValue) throws IOException
    {
        long oid = ObjectSerializer.NULL_OID;
        if (aValue != null) {
            Persistable persistable = (Persistable)aValue;
            oid = persistable.enerj_GetPersister().getOID(persistable);
        }

        mWriteContext.mStream.writeLong(oid);
    }


    /**
     * Reads a FCO from a stream.
     *
     * @param aPersistable the calling Persistable object (used for a Persister context).
     *
     * @return the value.
     *
     * @throws IOException if an error occurs
     */
    private Object readFCO(Persistable aPersistable) throws IOException
    {
       long oid = mReadContext.mStream.readLong();
        if (oid == ObjectSerializer.NULL_OID) {
            // Because of the type ID, we shouldn't normally get a NULL_OID unless
            // the OID was cleared in the database. If we get a sNull_TypeID, we never
            // get to this method.
            return null;
        }
        else {
            return aPersistable.enerj_GetPersister().getObjectForOID(oid);
        }
    }


    /**
     * Writes all of the Objects in a Collection to a stream.
     *
     * @param aCollection the Collection to write.
     * @param aPersistable the owner Persistable object.
     *
     * @throws IOException if an error occurs.
     */
    private void writeCollection(Collection aCollection, Persistable aPersistable) throws IOException
    {
        mWriteContext.mStream.writeInt( aCollection.size() );
        Iterator iterator = aCollection.iterator();
        while (iterator.hasNext()) {
            writeObject(iterator.next(), aPersistable);
        }
    }
    

    /**
     * Reads anObjectCount of Objects from a stream into a Collection.
     * Assumes that the size value (written by writeCollection) has already been
     * read from aContext.mStream. The size value may be necessary for proper construction
     * of aCollection.
     *
     * @param aCollection the Collection to load.
     * @param anObjectCount the number of objects to read.
     * @param aPersistable the owner Persistable object.
     *
     * @throws IOException if an error occurs.
     */
    private void readCollection(Collection aCollection, int anObjectCount, Persistable aPersistable) throws IOException
    {
        for (int i = 0; i < anObjectCount; i++) {
            aCollection.add( readObject(aPersistable) );
        }
    }
    

    /**
     * Writes all of the Objects in a Map to a stream.
     *
     * @param aMap the Map to write.
     * @param aPersistable the owner Persistable object.
     *
     * @throws IOException if an error occurs.
     */
    private void writeMap(Map aMap, Persistable aPersistable) throws IOException
    {
        mWriteContext.mStream.writeInt( aMap.size() );
        Iterator iterator = aMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            writeObject(entry.getKey(), aPersistable);
            writeObject(entry.getValue(), aPersistable);
        }
    }
    

    /**
     * Reads anObjectCount of Objects from a stream into a Map.
     * Assumes that the size value (written by writeMap) has already been
     * read from aContext.mStream. The size value may be necessary for proper construction
     * of aMap.
     *
     * @param aMap the Map to load.
     * @param anObjectCount the number of objects to read.
     * @param aPersistable the owner Persistable object.
     *
     * @throws IOException if an error occurs.
     */
    private void readMap(Map aMap, int anObjectCount, Persistable aPersistable) throws IOException
    {
        for (int i = 0; i < anObjectCount; i++) {
            Object key = readObject(aPersistable);
            Object value = readObject(aPersistable);
            aMap.put(key, value);
        }
    }
    

    // Read/Write contexts



    /**
     * Context used for reading objects.
     */
    private static final class ReadContext
    {
        /** DataInput stream for reading. */
        public DataInput mStream;
        public ObjectSerializer mSerializer;

        /** Array of SCOs already read from the stream. Null if zero SCOs
         * have been read. Array is ordered by the SCO Id.
         */
        private ArrayList mSCOArray = null;
        

        public ReadContext(DataInput aStream, ObjectSerializer aSerializer)
        {
            mStream = aStream;
            mSerializer = aSerializer;
        }
        

        /** 
         * Resets this object so that it may be used again as if it were newly
         * constructed.
         */
        public void reset()
        {
            if (mSCOArray != null) {
                mSCOArray.clear();
            }
        }
        

        public ArrayList getSCOArray()
        {
            if (mSCOArray == null) {
                mSCOArray = new ArrayList();
            }
            
            return mSCOArray;
        }
    }
    

    /**
     * Context used for writing objects.
     */
    private static final class WriteContext
    {
        /** DataOutput stream for writing. */
        public DataOutput mStream;
        public ObjectSerializer mSerializer;

        /** IdentityHashMap of SCOs already written from the stream. Null if zero SCOs
         * have been written. Key is the SCO object, value is the SCO Id as an Integer.
         */
        private IdentityHashMap mSCOMap = null;
        

        public WriteContext(DataOutput aStream, ObjectSerializer aSerializer)
        {
            mStream = aStream;
            mSerializer = aSerializer;
        }
        

        /** 
         * Resets this object so that it may be used again as if it were newly
         * constructed.
         */
        public void reset()
        {
            if (mSCOMap != null) {
                mSCOMap.clear();
            }
        }
        

        public IdentityHashMap getSCOMap()
        {
            if (mSCOMap == null) {
                mSCOMap = new IdentityHashMap();
            }
            
            return mSCOMap;
        }
    }
    

    // Serializers...



    /** Interface for all serializers.
     */
    private interface Serializer
    {

        /**
         * Gets the object type id for this SCO.
         *
         * @return byte representing the type id.
         */
        public byte getTypeId();


        /**
         * Gets the Class object representing this SCO.
         *
         * @return the direct class of the SCO.
         */
        public Class getRepresentingClass();
        

        /**
         * Gets the Proxy Class object representing this SCO.
         *
         * @return the proxy class of the SCO (e.g., JavaUtilDateSCO). This
         *  may be null if there is no proxy.
         */
        public Class getProxyClass();
        

        /**
         * Writes a SCO to aContext.mStream.
         *
         * @param aContext a WriteContext object.
         * @param anObject the value to be written.
         * @param anOwner an Owner FCO for the SCO.
         *
         * @throws IOException if an error occurs
         */
        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException;


        /**
         * Reads a SCO from aContext.mStream.
         *
         * @param aContext a ReadContext object.
         * @param anOwner an Owner FCO for the SCO.
         *
         * @return the value.
         *
         * @throws IOException if an error occurs
         */
        public Object read(ReadContext aContext, Persistable anOwner) throws IOException;
        
        /**
         * Resolve the object's entire object graph recursively until all instances are fully loaded.
         * This allows the object's entire graph to be used without a dependence on the persister (i.e.,
         * {@link Persistable#enerj_GetPersister()} will return null.
         *
         * @param anObjectSerializer the ObjectSerializer invoking this method. 
         * @param anObject the object to be resolved (either a SCO or FCO).
         * @param shouldDisassociate if true, the object tree will be disassociated from 
         *  its Persister.
         *
         * @throws IOException if an error occurs
         */
        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException;
    }


    /** Internal serializer for a byte[] SCO.
     */
    private static final class ByteArraySerializer implements Serializer
    {

        ByteArraySerializer()
        {
        }


        public byte getTypeId()
        {
            return sByteArray_TypeId;
        }


        public Class getRepresentingClass()
        {
            return sByteArrayClass;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            byte[] aValue = (byte[])anObject;
            aContext.mStream.writeInt(aValue.length);
            aContext.mStream.write(aValue);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readInt();
            byte[] array = new byte[len];
            aContext.mStream.readFully(array);
            return array;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a boolean[] SCO.
     */
    private static final class BooleanArraySerializer implements Serializer
    {

        BooleanArraySerializer()
        {
        }


        public byte getTypeId()
        {
            return sBooleanArray_TypeId;
        }


        public Class getRepresentingClass()
        {
            return sBooleanArrayClass;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            boolean[] aValue = (boolean[])anObject;
            aContext.mStream.writeInt(aValue.length);
            for (int i = 0; i < aValue.length; i++) {
                aContext.mStream.writeBoolean(aValue[i]);
            }
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readInt();
            boolean[] array = new boolean[len];
            for (int i = 0; i < len; i++) {
                array[i] = aContext.mStream.readBoolean();
            }

            return array;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a char[] SCO.
     */
    private static final class CharArraySerializer implements Serializer
    {

        CharArraySerializer()
        {
        }


        public byte getTypeId()
        {
            return sCharArray_TypeId;
        }


        public Class getRepresentingClass()
        {
            return sCharArrayClass;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            char[] aValue = (char[])anObject;
            aContext.mStream.writeInt(aValue.length);
            for (int i = 0; i < aValue.length; i++) {
                aContext.mStream.writeChar(aValue[i]);
            }
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readInt();
            char[] array = new char[len];
            for (int i = 0; i < len; i++) {
                array[i] = aContext.mStream.readChar();
            }

            return array;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a short[] SCO.
     */
    private static final class ShortArraySerializer implements Serializer
    {

        ShortArraySerializer()
        {
        }


        public byte getTypeId()
        {
            return sShortArray_TypeId;
        }


        public Class getRepresentingClass()
        {
            return sShortArrayClass;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            short[] aValue = (short[])anObject;
            aContext.mStream.writeInt(aValue.length);
            for (int i = 0; i < aValue.length; i++) {
                aContext.mStream.writeShort(aValue[i]);
            }
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readInt();
            short[] array = new short[len];
            for (int i = 0; i < len; i++) {
                array[i] = aContext.mStream.readShort();
            }

            return array;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a int[] SCO.
     */
    private static final class IntArraySerializer implements Serializer
    {

        IntArraySerializer()
        {
        }


        public byte getTypeId()
        {
            return sIntArray_TypeId;
        }


        public Class getRepresentingClass()
        {
            return sIntArrayClass;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            int[] aValue = (int[])anObject;
            aContext.mStream.writeInt(aValue.length);
            for (int i = 0; i < aValue.length; i++) {
                aContext.mStream.writeInt(aValue[i]);
            }
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readInt();
            int[] array = new int[len];
            for (int i = 0; i < len; i++) {
                array[i] = aContext.mStream.readInt();
            }

            return array;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a long[] SCO.
     */
    private static final class LongArraySerializer implements Serializer
    {

        LongArraySerializer()
        {
        }


        public byte getTypeId()
        {
            return sLongArray_TypeId;
        }


        public Class getRepresentingClass()
        {
            return sLongArrayClass;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            long[] aValue = (long[])anObject;
            aContext.mStream.writeInt(aValue.length);
            for (int i = 0; i < aValue.length; i++) {
                aContext.mStream.writeLong(aValue[i]);
            }
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readInt();
            long[] array = new long[len];
            for (int i = 0; i < len; i++) {
                array[i] = aContext.mStream.readLong();
            }

            return array;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a float[] SCO.
     */
    private static final class FloatArraySerializer implements Serializer
    {

        FloatArraySerializer()
        {
        }


        public byte getTypeId()
        {
            return sFloatArray_TypeId;
        }


        public Class getRepresentingClass()
        {
            return sFloatArrayClass;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            float[] aValue = (float[])anObject;
            aContext.mStream.writeInt(aValue.length);
            for (int i = 0; i < aValue.length; i++) {
                aContext.mStream.writeFloat(aValue[i]);
            }
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readInt();
            float[] array = new float[len];
            for (int i = 0; i < len; i++) {
                array[i] = aContext.mStream.readFloat();
            }

            return array;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a double[] SCO.
     */
    private static final class DoubleArraySerializer implements Serializer
    {

        DoubleArraySerializer()
        {
        }


        public byte getTypeId()
        {
            return sDoubleArray_TypeId;
        }


        public Class getRepresentingClass()
        {
            return sDoubleArrayClass;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            double[] aValue = (double[])anObject;
            aContext.mStream.writeInt(aValue.length);
            for (int i = 0; i < aValue.length; i++) {
                aContext.mStream.writeDouble(aValue[i]);
            }
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readInt();
            double[] array = new double[len];
            for (int i = 0; i < len; i++) {
                array[i] = aContext.mStream.readDouble();
            }

            return array;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a Object[] SCO. This includes arrays of
     * object types ("[Lxxxx;") and multi-dimensional arrays ("[[..." - which are
     * also arrays of Object).
     */
    private static final class ObjectArraySerializer implements Serializer
    {

        ObjectArraySerializer()
        {
        }


        public byte getTypeId()
        {
            return sObjectArray_TypeId;
        }


        public Class getRepresentingClass()
        {
            // We can't generically determine this. Use a this class as a placeholder.
            return this.getClass();
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            // Write class name of component type. For example, this would be "java.lang.Integer"
            // for an Integer[]. It would be "[[I" for a int[][][] (number of dimensions minus 1).
            aContext.mStream.writeUTF( anObject.getClass().getComponentType().getName() );
            Object[] aValue = (Object[])anObject;
            aContext.mStream.writeInt(aValue.length);
            for (int i = 0; i < aValue.length; i++) {
                aContext.mSerializer.writeObject(aValue[i], anOwner);
            }
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            String componentClassName = aContext.mStream.readUTF();
            Class componentClass;
            try {
                componentClass = Class.forName(componentClassName);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Cannot load class " + componentClassName + " while reading array for a field in class " + anOwner);
            }
            
            int len = aContext.mStream.readInt();
            Object[] array = (Object[])Array.newInstance(componentClass, len);
            for (int i = 0; i < len; i++) {
                array[i] = aContext.mSerializer.readObject(anOwner);
            }

            return array;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            for (Object aValue : (Object[])anObject) {
                anObjectSerializer.resolveObject(aValue, shouldDisassociate);
            }
        }
    }


    /** Internal serializer for a java.lang.Byte SCO.
     */
    private static final class Java_lang_Byte_Serializer implements Serializer
    {

        Java_lang_Byte_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_lang_Byte_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.lang.Byte.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.lang.Byte aValue = (java.lang.Byte)anObject;
            aContext.mStream.writeByte( aValue.byteValue () );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            byte value = aContext.mStream.readByte();
            return new Byte(value);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.lang.Boolean SCO.
     */
    private static final class Java_lang_Boolean_Serializer implements Serializer
    {

        Java_lang_Boolean_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_lang_Boolean_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.lang.Boolean.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.lang.Boolean aValue = (java.lang.Boolean)anObject;
            aContext.mStream.writeBoolean( aValue.booleanValue() );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            boolean value = aContext.mStream.readBoolean();
            return (value ? Boolean.TRUE : Boolean.FALSE);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.lang.Character SCO.
     */
    private static final class Java_lang_Character_Serializer implements Serializer
    {

        Java_lang_Character_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_lang_Character_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.lang.Character.class;
        }
        

        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.lang.Character aValue = (java.lang.Character)anObject;
            aContext.mStream.writeChar( aValue.charValue () );
        }


        public Class getProxyClass()
        {
            return null;
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            char value = aContext.mStream.readChar();
            return new Character(value);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.lang.Short SCO.
     */
    private static final class Java_lang_Short_Serializer implements Serializer
    {

        Java_lang_Short_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_lang_Short_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.lang.Short.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.lang.Short aValue = (java.lang.Short)anObject;
            aContext.mStream.writeShort( aValue.shortValue () );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            short value = aContext.mStream.readShort();
            return new Short(value);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.lang.Integer SCO.
     */
    private static final class Java_lang_Integer_Serializer implements Serializer
    {

        Java_lang_Integer_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_lang_Integer_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.lang.Integer.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.lang.Integer aValue = (java.lang.Integer)anObject;
            aContext.mStream.writeInt( aValue.intValue () );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int value = aContext.mStream.readInt();
            return new Integer(value);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.lang.Long SCO.
     */
    private static final class Java_lang_Long_Serializer implements Serializer
    {

        Java_lang_Long_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_lang_Long_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.lang.Long.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.lang.Long aValue = (java.lang.Long)anObject;
            aContext.mStream.writeLong( aValue.longValue () );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            long value = aContext.mStream.readLong();
            return new Long(value);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.lang.Float SCO.
     */
    private static final class Java_lang_Float_Serializer implements Serializer
    {

        Java_lang_Float_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_lang_Float_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.lang.Float.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.lang.Float aValue = (java.lang.Float)anObject;
            aContext.mStream.writeFloat( aValue.floatValue () );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            float value = aContext.mStream.readFloat();
            return new Float(value);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.lang.Double SCO.
     */
    private static final class Java_lang_Double_Serializer implements Serializer
    {

        Java_lang_Double_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_lang_Double_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.lang.Double.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.lang.Double aValue = (java.lang.Double)anObject;
            aContext.mStream.writeDouble( aValue.doubleValue () );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            double value = aContext.mStream.readDouble();
            return new Double(value);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.lang.String SCO.
     */
    private static final class Java_lang_String_Serializer implements Serializer
    {

        Java_lang_String_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_lang_String_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.lang.String.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.lang.String aValue = (java.lang.String)anObject;
            byte[] bytes = aValue.getBytes("UTF8");
            aContext.mStream.writeInt(bytes.length);
            aContext.mStream.write(bytes);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readInt();
            byte[] bytes = new byte[len];
            aContext.mStream.readFully(bytes);
            return new String(bytes, "UTF8");
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.lang.Class SCO.
     */
    private static final class Java_lang_Class_Serializer implements Serializer
    {

        Java_lang_Class_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_lang_Class_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.lang.Class.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.lang.Class aValue = (java.lang.Class)anObject;
            byte[] bytes = aValue.getName().getBytes("UTF8");
            aContext.mStream.writeShort(bytes.length);
            aContext.mStream.write(bytes);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readShort();
            byte[] bytes = new byte[len];
            aContext.mStream.readFully(bytes);
            String className = new String(bytes, "UTF8");
            try {
                return Class.forName(className);
            }
            catch (ClassNotFoundException e) {
                throw new IOException("Cannot find class " + className);
            }
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.math.BigDecimal SCO.
     */
    private static final class Java_math_BigDecimal_Serializer implements Serializer
    {
        private static final Java_math_BigInteger_Serializer sBigIntegerSerializer = 
                new Java_math_BigInteger_Serializer();


        Java_math_BigDecimal_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_math_BigDecimal_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.math.BigDecimal.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.math.BigDecimal aValue = (java.math.BigDecimal)anObject;
            // This assumes that scale will never exceed 65535 places (written in unsigned short form).
            aContext.mStream.writeShort( aValue.scale() );
            // Let BigInteger do all of the work.
            sBigIntegerSerializer.write(aContext, aValue.unscaledValue(), anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int scale = aContext.mStream.readUnsignedShort();
            java.math.BigInteger bigInt = (java.math.BigInteger)sBigIntegerSerializer.read(aContext, anOwner);
            return new java.math.BigDecimal(bigInt, scale);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.math.BigInteger SCO.
     */
    private static final class Java_math_BigInteger_Serializer implements Serializer
    {

        Java_math_BigInteger_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_math_BigInteger_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.math.BigInteger.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.math.BigInteger aValue = (java.math.BigInteger)anObject;
            byte[] bytes = aValue.toByteArray();
            // This assumes no more than 65535 digits (written in unsigned short form).
            aContext.mStream.writeShort(bytes.length);
            aContext.mStream.write(bytes);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int len = aContext.mStream.readUnsignedShort();
            byte[] bytes = new byte[len];
            aContext.mStream.readFully(bytes);
            return new java.math.BigInteger(bytes);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.util.Locale SCO.
     */
    private static final class Java_util_Locale_Serializer implements Serializer
    {

        Java_util_Locale_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_Locale_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.Locale.class;
        }
        

        public Class getProxyClass()
        {
            return null;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.Locale aValue = (java.util.Locale)anObject;
            aContext.mStream.writeUTF( aValue.getLanguage() );
            aContext.mStream.writeUTF( aValue.getCountry() );
            aContext.mStream.writeUTF( aValue.getVariant() );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            String language = aContext.mStream.readUTF();
            String country = aContext.mStream.readUTF();
            String variant = aContext.mStream.readUTF();
            return new java.util.Locale(language, country, variant);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.util.Date SCO.
     */
    private static final class Java_util_Date_Serializer implements Serializer
    {

        Java_util_Date_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_Date_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.Date.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilDateSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.Date aValue = (java.util.Date)anObject;
            aContext.mStream.writeLong( aValue.getTime() );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            long value = aContext.mStream.readLong();
            return new JavaUtilDateSCO(value, anOwner);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.sql.Date SCO.
     */
    private static final class Java_sql_Date_Serializer implements Serializer
    {

        Java_sql_Date_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_sql_Date_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.sql.Date.class;
        }
        

        public Class getProxyClass()
        {
            return JavaSqlDateSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.sql.Date aValue = (java.sql.Date)anObject;
            aContext.mStream.writeLong( aValue.getTime() );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            long value = aContext.mStream.readLong();
            return new JavaSqlDateSCO(value, anOwner);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.sql.Time SCO.
     */
    private static final class Java_sql_Time_Serializer implements Serializer
    {

        Java_sql_Time_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_sql_Time_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.sql.Time.class;
        }
        

        public Class getProxyClass()
        {
            return JavaSqlTimeSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.sql.Time aValue = (java.sql.Time)anObject;
            aContext.mStream.writeLong( aValue.getTime() );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            long value = aContext.mStream.readLong();
            return new JavaSqlTimeSCO(value, anOwner);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.sql.Timestamp SCO.
     */
    private static final class Java_sql_Timestamp_Serializer implements Serializer
    {

        Java_sql_Timestamp_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_sql_Timestamp_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.sql.Timestamp.class;
        }
        

        public Class getProxyClass()
        {
            return JavaSqlTimestampSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.sql.Timestamp aValue = (java.sql.Timestamp)anObject;
            aContext.mStream.writeLong( aValue.getTime() );
            aContext.mStream.writeInt( aValue.getNanos() );
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            long value = aContext.mStream.readLong();
            int nanos = aContext.mStream.readInt();
            return new JavaSqlTimestampSCO(value, nanos, anOwner);
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.util.ArrayList SCO.
     */
    private static final class Java_util_ArrayList_Serializer implements Serializer
    {

        Java_util_ArrayList_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_ArrayList_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.ArrayList.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilArrayListSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.ArrayList aValue = (java.util.ArrayList)anObject;
            aContext.mSerializer.writeCollection(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            // Set the owner AFTER reading the collection to prevent the owner from being marked modified.
            Collection collection = new JavaUtilArrayListSCO(size, null);
            aContext.mSerializer.readCollection(collection, size, anOwner);
            ((SCOTracker)collection).setOwnerFCO(anOwner);
            return collection;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            // Nothing to do.
        }
    }


    /** Internal serializer for a java.util.LinkedList SCO.
     */
    private static final class Java_util_LinkedList_Serializer implements Serializer
    {

        Java_util_LinkedList_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_LinkedList_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.LinkedList.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilLinkedListSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.LinkedList aValue = (java.util.LinkedList)anObject;
            aContext.mSerializer.writeCollection(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            Collection collection = new JavaUtilLinkedListSCO(null);
            aContext.mSerializer.readCollection(collection, size, anOwner);
            ((SCOTracker)collection).setOwnerFCO(anOwner);
            return collection;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveCollection((Collection)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.TreeSet SCO.
     */
    private static final class Java_util_TreeSet_Serializer implements Serializer
    {

        Java_util_TreeSet_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_TreeSet_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.TreeSet.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilTreeSetSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.TreeSet aValue = (java.util.TreeSet)anObject;
            Comparator comparator = aValue.comparator();
            if (comparator == null) {
                aContext.mStream.writeUTF("");
            }
            else {
                aContext.mStream.writeUTF( comparator.getClass().getName() );
            }
            
            aContext.mSerializer.writeCollection(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            String comparatorClassName = aContext.mStream.readUTF();
            Comparator comparator = null;
            if (comparatorClassName.length() > 0) {
                try {
                    comparator = (Comparator)Class.forName(comparatorClassName).newInstance();
                }
                catch (Exception e) {
                    throw new org.odmg.ClassNotPersistenceCapableException("Cannot access a public no-arg constructor on " + comparatorClassName + ": " + e);
                }
            }
            
            int size = aContext.mStream.readInt();
            Collection collection = new JavaUtilTreeSetSCO(comparator, null);
            aContext.mSerializer.readCollection(collection, size, anOwner);
            ((SCOTracker)collection).setOwnerFCO(anOwner);
            return collection;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveCollection((Collection)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.Vector SCO.
     */
    private static final class Java_util_Vector_Serializer implements Serializer
    {

        Java_util_Vector_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_Vector_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.Vector.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilVectorSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.Vector aValue = (java.util.Vector)anObject;
            aContext.mSerializer.writeCollection(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            Collection collection = new JavaUtilVectorSCO(size, null);
            aContext.mSerializer.readCollection(collection, size, anOwner);
            ((SCOTracker)collection).setOwnerFCO(anOwner);
            return collection;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveCollection((Collection)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.Stack SCO.
     */
    private static final class Java_util_Stack_Serializer implements Serializer
    {

        Java_util_Stack_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_Stack_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.Stack.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilStackSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.Stack aValue = (java.util.Stack)anObject;
            aContext.mSerializer.writeCollection(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            Collection collection = new JavaUtilStackSCO(null);
            aContext.mSerializer.readCollection(collection, size, anOwner);
            ((SCOTracker)collection).setOwnerFCO(anOwner);
            return collection;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveCollection((Collection)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.HashSet SCO.
     */
    private static final class Java_util_HashSet_Serializer implements Serializer
    {

        Java_util_HashSet_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_HashSet_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.HashSet.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilHashSetSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.HashSet aValue = (java.util.HashSet)anObject;
            aContext.mSerializer.writeCollection(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            Collection collection = new JavaUtilHashSetSCO(size + (size / 3), null);
            aContext.mSerializer.readCollection(collection, size, anOwner);
            ((SCOTracker)collection).setOwnerFCO(anOwner);
            return collection;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveCollection((Collection)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.LinkedHashSet SCO.
     */
    private static final class Java_util_LinkedHashSet_Serializer implements Serializer
    {

        Java_util_LinkedHashSet_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_LinkedHashSet_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.LinkedHashSet.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilLinkedHashSetSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.LinkedHashSet aValue = (java.util.LinkedHashSet)anObject;
            aContext.mSerializer.writeCollection(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            Collection collection = new JavaUtilLinkedHashSetSCO(size + (size / 3), null);
            aContext.mSerializer.readCollection(collection, size, anOwner);
            ((SCOTracker)collection).setOwnerFCO(anOwner);
            return collection;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveCollection((Collection)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.HashMap SCO.
     */
    private static final class Java_util_HashMap_Serializer implements Serializer
    {

        Java_util_HashMap_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_HashMap_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.HashMap.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilHashMapSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.HashMap aValue = (java.util.HashMap)anObject;
            aContext.mSerializer.writeMap(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            Map map = new JavaUtilHashMapSCO(size + (size / 3), null);
            aContext.mSerializer.readMap(map, size, anOwner);
            ((SCOTracker)map).setOwnerFCO(anOwner);
            return map;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveMap((Map)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.Hashtable SCO.
     */
    private static final class Java_util_Hashtable_Serializer implements Serializer
    {

        Java_util_Hashtable_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_Hashtable_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.Hashtable.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilHashtableSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.Hashtable aValue = (java.util.Hashtable)anObject;
            aContext.mSerializer.writeMap(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            Map map = new JavaUtilHashtableSCO(size + (size / 3), null);
            aContext.mSerializer.readMap(map, size, anOwner);
            ((SCOTracker)map).setOwnerFCO(anOwner);
            return map;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveMap((Map)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.LinkedHashMap SCO.
     */
    private static final class Java_util_LinkedHashMap_Serializer implements Serializer
    {

        Java_util_LinkedHashMap_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_LinkedHashMap_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.LinkedHashMap.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilLinkedHashMapSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.LinkedHashMap aValue = (java.util.LinkedHashMap)anObject;
            aContext.mSerializer.writeMap(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            Map map = new JavaUtilLinkedHashMapSCO(size + (size / 3), null);
            aContext.mSerializer.readMap(map, size, anOwner);
            ((SCOTracker)map).setOwnerFCO(anOwner);
            return map;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveMap((Map)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.Properties SCO.
     */
    private static final class Java_util_Properties_Serializer implements Serializer
    {

        Java_util_Properties_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_Properties_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.Properties.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilPropertiesSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.Properties aValue = (java.util.Properties)anObject;
            aContext.mSerializer.writeMap(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            Map map = new JavaUtilPropertiesSCO(null);
            aContext.mSerializer.readMap(map, size, anOwner);
            ((SCOTracker)map).setOwnerFCO(anOwner);
            return map;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveMap((Map)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.TreeMap SCO.
     */
    private static final class Java_util_TreeMap_Serializer implements Serializer
    {

        Java_util_TreeMap_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_TreeMap_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.TreeMap.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilTreeMapSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.TreeMap aValue = (java.util.TreeMap)anObject;
            Comparator comparator = aValue.comparator();
            if (comparator == null) {
                aContext.mStream.writeUTF("");
            }
            else {
                aContext.mStream.writeUTF( comparator.getClass().getName() );
            }
            
            aContext.mSerializer.writeMap(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            String comparatorClassName = aContext.mStream.readUTF();
            Comparator comparator = null;
            if (comparatorClassName.length() > 0) {
                try {
                    comparator = (Comparator)Class.forName(comparatorClassName).newInstance();
                }
                catch (Exception e) {
                    throw new org.odmg.ClassNotPersistenceCapableException("Cannot access a public no-arg constructor on " + comparatorClassName + ": " + e);
                }
            }

            int size = aContext.mStream.readInt();
            Map map = new JavaUtilTreeMapSCO(comparator, anOwner);
            aContext.mSerializer.readMap(map, size, null);
            ((SCOTracker)map).setOwnerFCO(anOwner);
            return map;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveMap((Map)anObject, shouldDisassociate);
        }
    }


    /** Internal serializer for a java.util.IdentityHashMap SCO.
     */
    private static final class Java_util_IdentityHashMap_Serializer implements Serializer
    {

        Java_util_IdentityHashMap_Serializer()
        {
        }


        public byte getTypeId()
        {
            return sJava_util_IdentityHashMap_TypeId;
        }


        public Class getRepresentingClass()
        {
            return java.util.IdentityHashMap.class;
        }
        

        public Class getProxyClass()
        {
            return JavaUtilIdentityHashMapSCO.class;
        }


        public void write(WriteContext aContext, Object anObject, Persistable anOwner) throws IOException
        {
            java.util.IdentityHashMap aValue = (java.util.IdentityHashMap)anObject;
            aContext.mSerializer.writeMap(aValue, anOwner);
        }


        public Object read(ReadContext aContext, Persistable anOwner) throws IOException
        {
            int size = aContext.mStream.readInt();
            Map map = new JavaUtilIdentityHashMapSCO(size + (size / 3), null);
            aContext.mSerializer.readMap(map, size, anOwner);
            ((SCOTracker)map).setOwnerFCO(anOwner);
            return map;
        }

        public void resolve(ObjectSerializer anObjectSerializer, Object anObject, boolean shouldDisassociate) throws IOException
        {
            anObjectSerializer.resolveMap((Map)anObject, shouldDisassociate);
        }
    }

}

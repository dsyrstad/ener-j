/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2000,2007 Oracle.  All rights reserved.
 *
 * $Id: SerialBinding.java,v 1.28.2.1 2007/02/01 14:49:38 cwl Exp $
 */

package com.sleepycatje.bind.serial;

import java.io.IOException;

import com.sleepycatje.bind.EntryBinding;
import com.sleepycatje.je.DatabaseEntry;
import com.sleepycatje.util.FastInputStream;
import com.sleepycatje.util.FastOutputStream;
import com.sleepycatje.util.RuntimeExceptionWrapper;

/**
 * A concrete <code>EntryBinding</code> that treats a key or data entry as
 * a serialized object.
 *
 * <p>This binding stores objects in serialized object format.  The
 * deserialized objects are returned by the binding, and their
 * <code>Class</code> must implement the <code>Serializable</code>
 * interface.</p>
 *
 * <p>For key bindings, a tuple binding is usually a better choice than a
 * serial binding.  A tuple binding gives a reasonable sort order, and works
 * with comparators in all cases -- see below.</p>
 *
 * <p><em>WARNING:</em> SerialBinding should not be used with Berkeley DB Java
 * Edition for key bindings, when a custom comparator is used.  In JE,
 * comparators are instantiated and called internally at times when databases
 * are not accessible.  Because serial bindings depend on the class catalog
 * database, a serial binding cannot be used during these times.  An attempt
 * to use a serial binding with a custom comparator will result in a
 * NullPointerException during environment open or close.</p>
 *
 * @author Mark Hayes
 */
public class SerialBinding extends SerialBase implements EntryBinding {

    private ClassCatalog classCatalog;
    private Class baseClass;

    /**
     * Creates a serial binding.
     *
     * @param classCatalog is the catalog to hold shared class information and
     * for a database should be a {@link StoredClassCatalog}.
     *
     * @param baseClass is the base class for serialized objects stored using
     * this binding -- all objects using this binding must be an instance of
     * this class.
     */
    public SerialBinding(ClassCatalog classCatalog, Class baseClass) {

        if (classCatalog == null) {
            throw new NullPointerException("classCatalog must be non-null");
        }
        this.classCatalog = classCatalog;
        this.baseClass = baseClass;
    }

    /**
     * Returns the base class for this binding.
     *
     * @return the base class for this binding.
     */
    public final Class getBaseClass() {

        return baseClass;
    }

    /**
     * Returns the class loader to be used during deserialization, or null if
     * a default class loader should be used.  The default implementation of
     * this method returns null.
     *
     * <p>This method may be overriden to return a dynamically determined class
     * loader.  For example,
     * <code>Thread.currentThread().getContextClassLoader()</code> could be
     * called to use the context class loader for the curren thread.  Or
     * <code>getBaseClass().getClassLoader()</code> could be called to use the
     * class loader for the base class, assuming that a base class has been
     * specified.</p>
     *
     * <p>If this method returns null, a default class loader will be used as
     * determined by the <code>java.io.ObjectInputStream.resolveClass</code>
     * method.</p>
     */
    public ClassLoader getClassLoader() {

        return null;
    }

    /**
     * Deserialize an object from an entry buffer.  May only be called for data
     * that was serialized using {@link #objectToEntry}, since the fixed
     * serialization header is assumed to not be included in the input data.
     * {@link SerialInput} is used to deserialize the object.
     *
     * @param entry is the input serialized entry.
     *
     * @return the output deserialized object.
     */
    public Object entryToObject(DatabaseEntry entry) {

        int length = entry.getSize();
        byte[] hdr = SerialOutput.getStreamHeader();
        byte[] bufWithHeader = new byte[length + hdr.length];

        System.arraycopy(hdr, 0, bufWithHeader, 0, hdr.length);
        System.arraycopy(entry.getData(), entry.getOffset(),
                         bufWithHeader, hdr.length, length);

        try {
            SerialInput jin = new SerialInput(
                new FastInputStream(bufWithHeader, 0, bufWithHeader.length),
                classCatalog,
                getClassLoader());
            return jin.readObject();
        } catch (IOException e) {
            throw new RuntimeExceptionWrapper(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeExceptionWrapper(e);
        }
    }

    /**
     * Serialize an object into an entry buffer.  The fixed serialization
     * header is not included in the output data to save space, and therefore
     * to deserialize the data the complementary {@link #entryToObject} method
     * must be used.  {@link SerialOutput} is used to serialize the object.
     *
     * <p>Note that this method sets the DatabaseEntry offset property to a
     * non-zero value and the size property to a value less than the length of
     * the byte array.</p>
     *
     * @param object is the input deserialized object.
     *
     * @param entry is the output serialized entry.
     *
     * @throws IllegalArgumentException if the object is not an instance of the
     * base class for this binding.
     */
    public void objectToEntry(Object object, DatabaseEntry entry) {

        if (baseClass != null && !baseClass.isInstance(object)) {
            throw new IllegalArgumentException(
                        "Data object class (" + object.getClass() +
                        ") not an instance of binding's base class (" +
                        baseClass + ')');
        }
        FastOutputStream fo = getSerialOutput(object);
        try {
            SerialOutput jos = new SerialOutput(fo, classCatalog);
            jos.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeExceptionWrapper(e);
        }

        byte[] hdr = SerialOutput.getStreamHeader();
        entry.setData(fo.getBufferBytes(), hdr.length,
                     fo.getBufferLength() - hdr.length);
    }
}

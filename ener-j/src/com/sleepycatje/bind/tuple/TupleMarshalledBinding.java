/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2000,2007 Oracle.  All rights reserved.
 *
 * $Id: TupleMarshalledBinding.java,v 1.23.2.1 2007/02/01 14:49:39 cwl Exp $
 */

package com.sleepycatje.bind.tuple;

import com.sleepycatje.util.RuntimeExceptionWrapper;

/**
 * A concrete <code>TupleBinding</code> that delegates to the
 * <code>MarshalledTupleEntry</code> interface of the data or key object.
 * 
 * <p>This class works by calling the methods of the {@link
 * MarshalledTupleEntry} interface, which must be implemented by the key or
 * data class, to convert between the key or data entry and the object.</p>
 *
 * @author Mark Hayes
 */
public class TupleMarshalledBinding extends TupleBinding {

    private Class cls;

    /**
     * Creates a tuple marshalled binding object.
     *
     * <p>The given class is used to instantiate key or data objects using
     * {@link Class#forName}, and therefore must be a public class and have a
     * public no-arguments constructor.  It must also implement the {@link
     * MarshalledTupleEntry} interface.</p>
     *
     * @param cls is the class of the key or data objects.
     */
    public TupleMarshalledBinding(Class cls) {

        this.cls = cls;

        /* The class will be used to instantiate the object.  */
        if (!MarshalledTupleEntry.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException(cls.toString() +
                        " does not implement MarshalledTupleEntry");
        }
    }

    // javadoc is inherited
    public Object entryToObject(TupleInput input) {

        try {
            MarshalledTupleEntry obj =
                (MarshalledTupleEntry) cls.newInstance();
            obj.unmarshalEntry(input);
            return obj;
        } catch (IllegalAccessException e) {
            throw new RuntimeExceptionWrapper(e);
        } catch (InstantiationException e) {
            throw new RuntimeExceptionWrapper(e);
        }
    }

    // javadoc is inherited
    public void objectToEntry(Object object, TupleOutput output) {

        MarshalledTupleEntry obj = (MarshalledTupleEntry) object;
        obj.marshalEntry(output);
    }
}

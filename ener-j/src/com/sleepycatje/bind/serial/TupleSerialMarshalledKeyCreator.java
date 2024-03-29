/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2000,2007 Oracle.  All rights reserved.
 *
 * $Id: TupleSerialMarshalledKeyCreator.java,v 1.25.2.1 2007/02/01 14:49:38 cwl Exp $
 */

package com.sleepycatje.bind.serial;

import com.sleepycatje.bind.tuple.MarshalledTupleKeyEntity;
import com.sleepycatje.bind.tuple.TupleInput;
import com.sleepycatje.bind.tuple.TupleOutput;

/**
 * A concrete key creator that works in conjunction with a {@link
 * TupleSerialMarshalledBinding}.  This key creator works by calling the
 * methods of the {@link MarshalledTupleKeyEntity} interface to create and
 * clear the index key fields.
 *
 * @author Mark Hayes
 */
public class TupleSerialMarshalledKeyCreator extends TupleSerialKeyCreator {

    private TupleSerialMarshalledBinding binding;
    private String keyName;

    /**
     * Creates a tuple-serial marshalled key creator.
     *
     * @param binding is the binding used for the tuple-serial entity.
     *
     * @param keyName is the key name passed to the {@link
     * MarshalledTupleKeyEntity#marshalSecondaryKey} method to identify the
     * index key.
     */
    public TupleSerialMarshalledKeyCreator(TupleSerialMarshalledBinding
                                           binding,
                                           String keyName) {

        super(binding.dataBinding);
        this.binding = binding;
        this.keyName = keyName;

        if (dataBinding == null) {
            throw new NullPointerException("dataBinding may not be null");
        }
    }

    // javadoc is inherited
    public boolean createSecondaryKey(TupleInput primaryKeyInput,
                                      Object dataInput,
                                      TupleOutput indexKeyOutput) {

        /*
         * The primary key is unmarshalled before marshalling the index key, to
         * account for cases where the index key includes fields taken from the
         * primary key.
         */
        MarshalledTupleKeyEntity entity = (MarshalledTupleKeyEntity)
            binding.entryToObject(primaryKeyInput, dataInput);

        return entity.marshalSecondaryKey(keyName, indexKeyOutput);
    }

    // javadoc is inherited
    public Object nullifyForeignKey(Object dataInput) {

        MarshalledTupleKeyEntity entity = (MarshalledTupleKeyEntity)
            binding.entryToObject(null, dataInput);

        return entity.nullifyForeignKey(keyName) ? dataInput : null;
    }
}

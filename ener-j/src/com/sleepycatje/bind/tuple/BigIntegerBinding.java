/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2000,2007
 *      Oracle Corporation.  All rights reserved.
 *
 * $Id: BigIntegerBinding.java,v 1.1.2.1 2007/02/01 14:49:39 cwl Exp $
 */

package com.sleepycatje.bind.tuple;

import java.math.BigInteger;

import com.sleepycatje.je.DatabaseEntry;

/**
 * A concrete <code>TupleBinding</code> for a <code>BigInteger</code> value.
 */
public class BigIntegerBinding extends TupleBinding {

    // javadoc is inherited
    public Object entryToObject(TupleInput input) {

        return input.readBigInteger();
    }

    // javadoc is inherited
    public void objectToEntry(Object object, TupleOutput output) {

        output.writeBigInteger((BigInteger) object);
    }

    // javadoc is inherited
    protected TupleOutput getTupleOutput(Object object) {

        return sizedOutput((BigInteger) object);
    }

    /**
     * Converts an entry buffer into a <code>BigInteger</code> value.
     *
     * @param entry is the source entry buffer.
     *
     * @return the resulting value.
     */
    public static BigInteger entryToBigInteger(DatabaseEntry entry) {

        return entryToInput(entry).readBigInteger();
    }

    /**
     * Converts a <code>BigInteger</code> value into an entry buffer.
     *
     * @param val is the source value.
     *
     * @param entry is the destination entry buffer.
     */
    public static void bigIntegerToEntry(BigInteger val, DatabaseEntry entry) {

        outputToEntry(sizedOutput(val).writeBigInteger(val), entry);
    }

    /**
     * Returns a tuple output object of the exact size needed, to avoid
     * wasting space when a single primitive is output.
     */
    private static TupleOutput sizedOutput(BigInteger val) {

        int len = TupleOutput.getBigIntegerByteLength(val);
        return new TupleOutput(new byte[len]);
    }
}

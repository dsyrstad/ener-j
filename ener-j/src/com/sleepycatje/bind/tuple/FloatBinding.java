/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2000,2007 Oracle.  All rights reserved.
 *
 * $Id: FloatBinding.java,v 1.11.2.1 2007/02/01 14:49:39 cwl Exp $
 */

package com.sleepycatje.bind.tuple;

import com.sleepycatje.je.DatabaseEntry;

/**
 * A concrete <code>TupleBinding</code> for a <code>Float</code> primitive
 * wrapper or a <code>float</code> primitive.
 *
 * <p><em>Note:</em> This class produces byte array values that by default
 * (without a custom comparator) do <em>not</em> sort correctly for negative
 * values.  Only non-negative values are sorted correctly by default.  To sort
 * all values correctly by default, use {@link SortedFloatBinding}.</p>
 *
 * <p>There are two ways to use this class:</p>
 * <ol>
 * <li>When using the {@link com.sleepycatje.je} package directly, the static
 * methods in this class can be used to convert between primitive values and
 * {@link DatabaseEntry} objects.</li>
 * <li>When using the {@link com.sleepycatje.collections} package, an instance of
 * this class can be used with any stored collection.  The easiest way to
 * obtain a binding instance is with the {@link
 * TupleBinding#getPrimitiveBinding} method.</li>
 * </ol>
 */
public class FloatBinding extends TupleBinding {

    private static final int FLOAT_SIZE = 4;

    // javadoc is inherited
    public Object entryToObject(TupleInput input) {

        return new Float(input.readFloat());
    }

    // javadoc is inherited
    public void objectToEntry(Object object, TupleOutput output) {

        output.writeFloat(((Number) object).floatValue());
    }

    // javadoc is inherited
    protected TupleOutput getTupleOutput(Object object) {

        return sizedOutput();
    }

    /**
     * Converts an entry buffer into a simple <code>float</code> value.
     *
     * @param entry is the source entry buffer.
     *
     * @return the resulting value.
     */
    public static float entryToFloat(DatabaseEntry entry) {

        return entryToInput(entry).readFloat();
    }

    /**
     * Converts a simple <code>float</code> value into an entry buffer.
     *
     * @param val is the source value.
     *
     * @param entry is the destination entry buffer.
     */
    public static void floatToEntry(float val, DatabaseEntry entry) {

        outputToEntry(sizedOutput().writeFloat(val), entry);
    }

    /**
     * Returns a tuple output object of the exact size needed, to avoid
     * wasting space when a single primitive is output.
     */
    static TupleOutput sizedOutput() {

        return new TupleOutput(new byte[FLOAT_SIZE]);
    }
}

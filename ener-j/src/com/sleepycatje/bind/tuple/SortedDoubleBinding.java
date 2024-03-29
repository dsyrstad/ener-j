/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2000,2007 Oracle.  All rights reserved.
 *
 * $Id: SortedDoubleBinding.java,v 1.4.2.1 2007/02/01 14:49:39 cwl Exp $
 */

package com.sleepycatje.bind.tuple;

import com.sleepycatje.je.DatabaseEntry;

/**
 * A concrete <code>TupleBinding</code> for a <code>Double</code> primitive
 * wrapper or a <code>double</code> primitive.
 *
 * <p>This class produces byte array values that by default (without a custom
 * comparator) sort correctly, including sorting of negative values.
 * Therefore, this class should normally be used instead of {@link
 * DoubleBinding} which does not by default support sorting of negative values.
 * Please note that:</p>
 * <ul>
 * <li>The byte array (stored) formats used by {@link DoubleBinding} and
 * {@link SortedDoubleBinding} are different and incompatible.  They are not
 * interchangable once data has been stored.</li>
 * <li>An instance of {@link DoubleBinding}, not {@link SortedDoubleBinding},
 * is returned by {@link TupleBinding#getPrimitiveBinding} method.  Therefore,
 * to use {@link SortedDoubleBinding}, {@link TupleBinding#getPrimitiveBinding}
 * should not be called.</li>
 * </ul>
 *
 * <p>There are two ways to use this class:</p>
 * <ol>
 * <li>When using the {@link com.sleepycatje.je} package directly, the static
 * methods in this class can be used to convert between primitive values and
 * {@link DatabaseEntry} objects.</li>
 * <li>When using the {@link com.sleepycatje.collections} package, an instance of
 * this class can be used with any stored collection.</li>
 * </ol>
 */
public class SortedDoubleBinding extends TupleBinding {

    /* javadoc is inherited */
    public Object entryToObject(TupleInput input) {

        return new Double(input.readSortedDouble());
    }

    /* javadoc is inherited */
    public void objectToEntry(Object object, TupleOutput output) {

        output.writeSortedDouble(((Number) object).doubleValue());
    }

    /* javadoc is inherited */
    protected TupleOutput getTupleOutput(Object object) {

        return DoubleBinding.sizedOutput();
    }

    /**
     * Converts an entry buffer into a simple <code>double</code> value.
     *
     * @param entry is the source entry buffer.
     *
     * @return the resulting value.
     */
    public static double entryToDouble(DatabaseEntry entry) {

        return entryToInput(entry).readSortedDouble();
    }

    /**
     * Converts a simple <code>double</code> value into an entry buffer.
     *
     * @param val is the source value.
     *
     * @param entry is the destination entry buffer.
     */
    public static void doubleToEntry(double val, DatabaseEntry entry) {

        outputToEntry(DoubleBinding.sizedOutput().writeSortedDouble(val),
                      entry);
    }
}

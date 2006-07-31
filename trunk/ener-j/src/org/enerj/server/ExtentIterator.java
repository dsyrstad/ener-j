// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/ExtentIterator.java,v 1.5 2006/01/17 02:41:09 dsyrstad Exp $

package org.enerj.server;

import org.odmg.ODMGRuntimeException;

import java.util.NoSuchElementException;


/**
 * Ener-J ExtentIterator interface. Instances of this type are created and maintained
 * by a MetaObjectServerSession.
 *
 * @version $Id: ExtentIterator.java,v 1.5 2006/01/17 02:41:09 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface ExtentIterator
{
    //----------------------------------------------------------------------
    /**
     * Determines if more objects are available from this iterator.
     *
     * @return true if more objects are available, otherwise false.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public boolean hasNext() throws ODMGRuntimeException;

    //----------------------------------------------------------------------
    /**
     * Gets, at most, the next N objects from the iterator, where N is aMaxNumObjects.
     *
     * @param aMaxNumObjects the maximum number of objects to be retrieved.
     *
     * @return an array of OIDs. This array may be from 1 to aMaxNumObjects elements in length.
     *
     * @throws ODMGRuntimeException if an error occurs.
     * @throws NoSuchElementException if there are no more objects available from the iterator.
     */
    public long[] next(int aMaxNumObjects) throws ODMGRuntimeException, NoSuchElementException;

    //----------------------------------------------------------------------
    /**
     * Closes this iterator.
     */
    public void close();

}


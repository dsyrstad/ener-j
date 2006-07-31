// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/LargeCollection.java,v 1.1 2005/08/16 04:24:45 dsyrstad Exp $

package org.enerj.core;

import java.io.*;
import java.util.*;

import org.odmg.*;

/**
 * Represents additional Collection methods for a "large" collection. A large collection
 * is one which contain more than 2 billion items.
 *
 * @version $Id: LargeCollection.java,v 1.1 2005/08/16 04:24:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface LargeCollection extends java.util.Collection
{
    //----------------------------------------------------------------------
    /**
     * Like Collection.size(), but returns the size of the array as a long, which is
     * necessary for very large arrays.
     *
     * @return the size of the array.
     */
    public long sizeAsLong();

    //----------------------------------------------------------------------
    /**
     * Gets the modification count for this collection. This is used primarily
     * for testing for concurrent modification on iterators. The modification
     * count is incremented when there is a structural change to the collection.
     *
     * @return the modification count.
     */
    public int getModificationCount();
}


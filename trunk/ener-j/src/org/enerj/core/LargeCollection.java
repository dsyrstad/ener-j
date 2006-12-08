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
public interface LargeCollection<E> extends java.util.Collection<E>
{

    /**
     * Like Collection.size(), but returns the size of the array as a long, which is
     * necessary for very large arrays.
     *
     * @return the size of the array.
     */
    public long sizeAsLong();


    /**
     * Gets the modification count for this collection. This is used primarily
     * for testing for concurrent modification on iterators. The modification
     * count is incremented when there is a structural change to the collection.
     *
     * @return the modification count.
     */
    public int getModificationCount();
}


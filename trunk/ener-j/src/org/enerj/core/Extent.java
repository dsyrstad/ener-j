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
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/Extent.java,v 1.4 2006/02/09 03:42:24 dsyrstad Exp $

package org.enerj.core;

import java.util.Collection;
import java.util.Iterator;


/**
 * Represents an Extent of a class (all of the instances of a class).
 *
 * @version $Id: Extent.java,v 1.4 2006/02/09 03:42:24 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface Extent extends Collection  // TODO implements JDO Extent when we implement JDO
{
    //----------------------------------------------------------------------
    /** 
     * Returns an immutable iterator over all the instances in the Extent.
     * If any mutating method, including the remove method, is called on the Iterator 
     * returned by this method, a UnsupportedOperationException is thrown.
     *
     * @return an iterator over all instances in the Extent.
     */
    public Iterator iterator();

    //----------------------------------------------------------------------
    /** 
     * Returns whether this Extent was defined to contain subclasses.
     *
     * @return true if this Extent was defined to contain instances
     * that are subclasses of the candidate class.
     */    
    public boolean hasSubclasses();

    //----------------------------------------------------------------------
    /** 
     * An Extent contains all instances of a particular class in the data
     * store; this method returns the Class of the instances.
     *
     * @return the Class of instances of this Extent.
     */
    public Class getCandidateClass();

    //----------------------------------------------------------------------
    /** 
     * Close all Iterators associated with this Extent instance.
     * Iterators closed by this method will return false
     * from hasNext() and will throw
     * NoSuchElementException on next().
     */    
    public void closeAll();
    
    //----------------------------------------------------------------------
    /** 
     * Close an Iterator associated with this Extent instance.
     * Iterators closed by this method will return false
     * from hasNext() and will throw NoSuchElementException on next().
     *
     * @param an Iterator obtained by the method iterator() on this Extent instance.
     */    
     public void close(Iterator anIterator);
}

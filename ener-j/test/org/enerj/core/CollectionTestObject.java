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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/CollectionTestObject.java,v 1.1 2005/08/16 04:29:00 dsyrstad Exp $

package org.enerj.core;

import java.util.*;

import junit.framework.*;

import org.odmg.*;
import org.enerj.enhancer.*;
import org.enerj.core.*;

/**
 * Generic persistable object used by the collection tests. Simply a wrapper for String.
 *
 * @version $Id: CollectionTestObject.java,v 1.1 2005/08/16 04:29:00 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class CollectionTestObject implements Cloneable, Comparable
{
    private String mValue;
    

    public CollectionTestObject(String aValue) 
    {
        assert aValue != null;
        mValue = aValue;
    }
    

    public boolean equals(Object anOther)
    {
        if ( !(anOther instanceof CollectionTestObject)) {
            return false;
        }
        
        CollectionTestObject target = (CollectionTestObject)anOther;
        return mValue.equals(target.mValue);
    }


    public int hashCode()
    {
        return mValue.hashCode();
    }
    

    public Object clone()
    {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            // Can't happen
            return null;
        }
    }


    public String toString()
    {
        return mValue;
    }


    public int compareTo(Object anObject)
    {
        return mValue.compareTo(((CollectionTestObject)anObject).mValue);
    }
    
}

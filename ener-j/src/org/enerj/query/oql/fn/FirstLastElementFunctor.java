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
//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/FirstLastElementFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.lang.reflect.Array;
import java.util.List;

import org.enerj.jga.fn.UnaryFunctor;

/**
 * Returns either the first element of the argument, or the last. The argument must be a List or an array.
 * Returns null if the argument is null or the list size is zero.  
 * <p>
 * 
 * @version $Id: FirstLastElementFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class FirstLastElementFunctor extends UnaryFunctor
{
    private static final long serialVersionUID = 6982314760936377434L;

    /** Instance that performs the first() operation. */
    public static final FirstLastElementFunctor FIRST_INSTANCE = new FirstLastElementFunctor(true);
    /** Instance that performs the last() operation. */
    public static final FirstLastElementFunctor LAST_INSTANCE = new FirstLastElementFunctor(false);

    private boolean mIsFirst;
    

    /**
     * Construct a FirstLastElementFunctor functor.
     * 
     * @param isFirst if true, the first element is returned by fn(Object). Otherwise the last
     *  element is returned.
     */
    private FirstLastElementFunctor(boolean isFirst) 
    {
        super();
        mIsFirst = isFirst;
    }


    public Object fn(Object arg)
    {
        if (arg == null) {
            return null;
        }
        
        int size;
        boolean isArray = arg.getClass().isArray();
        if (isArray) {
            size = Array.getLength(arg);
        }
        else {
            size = ((List)arg).size();
        }
        
        if (size == 0) {
            return null;
        }
        
        if (mIsFirst) {
            if (isArray) {
                return Array.get(arg, 0);
            }
            
            return ((List)arg).get(0);
        }
        
        // last()
        if (isArray) {
            return Array.get(arg, size - 1);
        }
        
        return ((List)arg).get(size - 1);
    }


    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(FirstLastElementFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof FirstLastElementFunctor.Visitor)
            ((FirstLastElementFunctor.Visitor)v).visit(this);
    }
    

    public String toString() {
        return "FirstLastElementFunctor( " + (mIsFirst ? "first" : "last") + ')';
    }
    
    // AcyclicVisitor
    

    /**
     * Interface for classes that may interpret a <b>FirstLastElementFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(FirstLastElementFunctor host);
    }
}

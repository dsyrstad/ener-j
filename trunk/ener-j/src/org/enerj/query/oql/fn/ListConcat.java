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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/ListConcat.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.enerj.jga.fn.BinaryFunctor;

/**
 * Concatenates two Lists or two arrays. If either argument is null, null is returned. Arrays must have
 * compatiable component types.  
 * <p>
 * 
 * @version $Id: ListConcat.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ListConcat extends BinaryFunctor
{
    private static final long serialVersionUID = 9066802725117441138L;
    /** Singleton instance of this functor. */
    public static final ListConcat INSTANCE = new ListConcat();
    

    /**
     * Construct a ListConcat functor.
     */
    private ListConcat() 
    {
    }


    public Object fn(Object arg0, Object arg1)
    {
        if (arg0 == null || arg1 == null) {
            return null;
        }
        
        Class arg0Type = arg0.getClass();
        Class arg1Type = arg1.getClass();
        if (arg0Type.isArray() && arg1Type.isArray() && arg0Type.getComponentType() == arg1Type.getComponentType()) {
            int arg0Size = Array.getLength(arg0);
            int arg1Size = Array.getLength(arg1);
            Object array = Array.newInstance(arg0Type.getComponentType(), arg0Size + arg1Size);
            System.arraycopy(arg0, 0, array, 0, arg0Size);
            System.arraycopy(arg1, 0, array, arg0Size, arg1Size);
            return array;
        }
        
        if (List.class.isAssignableFrom(arg0Type) && List.class.isAssignableFrom(arg1Type)) {
            List arg0List = (List)arg0;
            List arg1List = (List)arg1;
            
            List list = new ArrayList( arg0List.size() + arg1List.size() );
            list.addAll(arg0List);
            list.addAll(arg1List);
            return list;
        }
        
        throw new IllegalArgumentException("Both arguments must be a List or an array");
    }


    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(ListConcat)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ListConcat.Visitor)
            ((ListConcat.Visitor)v).visit(this);
    }
    

    public String toString() {
        return "ListConcat";
    }
    
    // AcyclicVisitor
    

    /**
     * Interface for classes that may interpret a <b>ListConcat</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ListConcat host);
    }
}

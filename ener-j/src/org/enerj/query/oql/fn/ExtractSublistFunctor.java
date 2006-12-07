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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/ExtractSublistFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.lang.reflect.Array;
import java.util.List;

import org.enerj.core.PersistentArrayList;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Extracts a sub-list. If the indexed functor's result is a List or array, a sub List is extracted.
 * If the argument is not a List or array, a substring is extracted from the result of Object.toString(). 
 * The arguments to fn(x,y) must evaluate to Numbers that are convertible to integers.   
 * <p>
 * 
 * @version $Id: ExtractSublistFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ExtractSublistFunctor extends BinaryFunctor
{
    private static final long serialVersionUID = -3198297470889224152L;

    private UnaryFunctor mIndexedFunctor;
    

    /**
     * Construct a ExtractSublistFunctor functor.
     * 
     * @param anIndexedFunctor the functor whose result will be indexed.
     */
    public ExtractSublistFunctor(UnaryFunctor anIndexedFunctor) 
    {
        super();
        mIndexedFunctor = anIndexedFunctor;
    }


    public Object fn(Object x, Object y)
    {
        Object expr = mIndexedFunctor.fn(null);
        if (expr == null || x == null || y == null) {
            return null;
        }
        
        Class exprClass = expr.getClass();
        int start = ((Number)x).intValue();
        int end = ((Number)y).intValue();
        
        // Be forgiving.
        if (end < start || end < 0) {
            return null;
        }

        if (start < 0) {
            start = 0;
        }
        
        if (exprClass.isArray()) {
            int length = Array.getLength(expr);
            if (end >= length) {
                end = length - 1;
            }

            List result = new PersistentArrayList( (end - start) + 1 );
            for (int i = start; i < end; i++) { 
                result.add( Array.get(expr, i) );
            }
            
            return result;
        }
        
        else if (List.class.isAssignableFrom(exprClass)) {
            List exprList = (List)expr;
            int size = exprList.size();
            if (end >= size) {
                end = size - 1;
            }

            return exprList.subList(start, end + 1);
        }
        
        String string = expr.toString();
        int length = string.length();
        if (end >= length) {
            end = length - 1;
        }
        
        return string.substring(start, end + 1);
    }


    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(ExtractSublistFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ExtractSublistFunctor.Visitor)
            ((ExtractSublistFunctor.Visitor)v).visit(this);
    }
    

    public String toString() {
        return "ExtractSublistFunctor[" + mIndexedFunctor + ']';
    }
    
    // AcyclicVisitor
    

    /**
     * Interface for classes that may interpret a <b>ExtractSublistFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ExtractSublistFunctor host);
    }
}

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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/ConvertToCollection.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.odmg.DArray;
import org.odmg.DSet;
import org.enerj.core.RegularDArray;
import org.enerj.core.RegularDSet;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Converts a the results of an ApplyUnary functor (or the like) to the specified Collection type.  
 * <p>
 * 
 * @version $Id: ConvertToCollection.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ConvertToCollection extends UnaryFunctor
{
    private static final long serialVersionUID = -4320835180809092201L;

    private Class mCollectionType;
    private UnaryFunctor mApplyFunctor;
    

    /**
     * Construct a ConvertToCollection functor.
     * 
     * @param aCollectionType the type of the desired collection. Must be one of the
     *  interface types: Collection, List, or Set.
     * @param anApplyFunctor a UnaryFunctor that will create an array of values 
     *  for the collection.
     */
    public ConvertToCollection(Class aCollectionType, UnaryFunctor anApplyFunctor) 
    {
        super();
        assert aCollectionType == List.class || aCollectionType == Set.class || aCollectionType == Collection.class;
        mCollectionType = aCollectionType;
        if (mCollectionType == Collection.class) {
            mCollectionType = List.class; // We're really going to create a List for Collection.
        }
        
        mApplyFunctor = anApplyFunctor;
    }


    public Object fn(Object arg)
    {
        Object[] values = (Object[])mApplyFunctor.fn(arg);
        List valueList = Arrays.asList(values);
        if (mCollectionType == List.class) {
            DArray dArray = new RegularDArray(values.length); 
            dArray.addAll(valueList);
            return dArray;
        }
        
        // if (mCollectionType == Set.class)
        DSet dSet = new RegularDSet(values.length);
        dSet.addAll(valueList);
        return dSet;
    }


    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(ConvertToCollection)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConvertToCollection.Visitor)
            ((ConvertToCollection.Visitor)v).visit(this);
    }
    

    public String toString() {
        return "ConvertToCollection( " + mApplyFunctor + ')';
    }
    
    // AcyclicVisitor
    

    /**
     * Interface for classes that may interpret a <b>ConvertToCollection</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConvertToCollection host);
    }
}

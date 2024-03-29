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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/CastFunctor.java,v 1.5 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.odmg.DArray;
import org.odmg.DSet;
import org.enerj.core.PersistentArrayList;
import org.enerj.core.PersistentHashSet;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.util.TypeUtil;

/**
 * Casts the argument to the given type. A null arguments casts to null.
 * Note that this really doesn't provide any functionality other than to
 * do type checking at runtime for casts.
 * <p>
 * 
 * @version $Id: CastFunctor.java,v 1.5 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class CastFunctor extends UnaryFunctor
{
    private static final long serialVersionUID = 1571059761565887259L;

    private Class mType;
    private boolean mTypeIsNumeric;
    

    /**
     * Construct a CastFunctor functor.
     * 
     * @param aType the desired type.
     */
    public CastFunctor(Class aType) 
    {
        super();
        mType = aType;
        mTypeIsNumeric = TypeUtil.isNumericType(mType);
    }


    public Object fn(Object arg)
    {
        if (arg == null) {
            return null;
        }
        
        // Convert Numbers naturally as a cast.
        if (mTypeIsNumeric && TypeUtil.isNumericType(arg.getClass())) {
        	return TypeUtil.getNumberPromotionFunctor(mType).fn(arg);
        }
        
        return mType.cast(arg);
    }


    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(CastFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof CastFunctor.Visitor)
            ((CastFunctor.Visitor)v).visit(this);
    }
    

    public String toString() {
        return "CastFunctor( " + mType + ')';
    }
    
    // AcyclicVisitor
    

    /**
     * Interface for classes that may interpret a <b>CastFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(CastFunctor host);
    }
}

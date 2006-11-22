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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/AbsoluteValueOf.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.arithmetic.Negate;
import org.enerj.jga.fn.arithmetic.ValueOf;

/**
 * Gets the absolute value of the numeric argument. Returns null if the value is null.  
 * <p>
 * 
 * @version $Id: AbsoluteValueOf.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class AbsoluteValueOf extends UnaryFunctor
{
    private static final long serialVersionUID = -3197813337740896447L;

    private Negate mNegateFunctor;
    private Number mZero;
    

    /**
     * Construct a AbsoluteValueOf functor.
     */
    public AbsoluteValueOf(Class aType) 
    {
        super();
        mNegateFunctor = new Negate(aType);
        // Get a Zero of the proper type.
        mZero = new ValueOf(aType).fn( Integer.valueOf(0) );
    }


    public Object fn(Object arg)
    {
        if (arg == null) {
            return null;
        }

        return ((Comparable)arg).compareTo(mZero) < 0 ? mNegateFunctor.fn((Number)arg) : arg;
    }


    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(AbsoluteValueOf)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof AbsoluteValueOf.Visitor)
            ((AbsoluteValueOf.Visitor)v).visit(this);
    }
    

    public String toString() {
        return "AbsoluteValueOf";
    }
    
    // AcyclicVisitor
    

    /**
     * Interface for classes that may interpret a <b>AbsoluteValueOf</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(AbsoluteValueOf host);
    }
}

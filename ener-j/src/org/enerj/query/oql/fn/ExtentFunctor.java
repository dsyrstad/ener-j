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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/ExtentFunctor.java,v 1.6 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import org.enerj.core.Extent;
import org.enerj.core.EnerJDatabase;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.EvaluatorContext;

/**
 * Functor that returns a Collection (bag) representing an Extent for a class. The class is given as an
 * argument to fn().     
 * <p>
 * 
 * @version $Id: ExtentFunctor.java,v 1.6 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ExtentFunctor extends UnaryFunctor
{
    private static final long serialVersionUID = -7023610246811874224L;

    //--------------------------------------------------------------------------------
    /**
     * Construct a ExtentFunctor functor.
     */
    public ExtentFunctor() 
    {
        super();
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object arg)
    {
        Class extentClass = (Class)arg;
        EnerJDatabase db = EvaluatorContext.getContext().getDatabase();
        Extent extent = db.getExtent(extentClass, true);
        EvaluatorContext.getContext().trackExtent(extent);
        return extent; 
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(ExtentFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ExtentFunctor.Visitor)
            ((ExtentFunctor.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "ExtentFunctor";
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>ExtentFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ExtentFunctor host);
    }
}

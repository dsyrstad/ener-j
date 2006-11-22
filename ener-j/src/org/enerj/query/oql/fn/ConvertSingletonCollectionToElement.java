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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/ConvertSingletonCollectionToElement.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Collection;

import org.enerj.jga.fn.UnaryFunctor;

/**
 * Converts the given singleton Collection parameter to its only element.  
 * <p>
 * 
 * @version $Id: ConvertSingletonCollectionToElement.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ConvertSingletonCollectionToElement extends UnaryFunctor
{
    private static final long serialVersionUID = 8657175538708835699L;
    public static final ConvertSingletonCollectionToElement INSTANCE = new ConvertSingletonCollectionToElement();
    

    /**
     * Construct a ConvertSingletonCollectionToElement functor.
     */
    private ConvertSingletonCollectionToElement() 
    {
    }


    public Object fn(Object arg)
    {
        // If null, return the argument.
        if (arg == null) {
            return arg;
        }
        
        if (!(arg instanceof Collection)) {
            throw new IllegalArgumentException("Argument is not a Collection");
        }
        
        Collection collection = (Collection)arg;
        if (collection.size() != 1) {
            throw new IllegalArgumentException("Argument is not a singleton Collection");
        }
        
        return collection.iterator().next();
    }


    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(ConvertSingletonCollectionToElement)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConvertSingletonCollectionToElement.Visitor)
            ((ConvertSingletonCollectionToElement.Visitor)v).visit(this);
    }
    

    public String toString() {
        return "ConvertSingletonCollectionToElement()";
    }
    
    // AcyclicVisitor
    

    /**
     * Interface for classes that may interpret a <b>ConvertSingletonCollectionToElement</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConvertSingletonCollectionToElement host);
    }
}

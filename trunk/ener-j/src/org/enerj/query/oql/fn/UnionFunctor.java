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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/UnionFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Collection;
import java.util.Set;

import org.enerj.core.PersistentBag;
import org.enerj.core.PersistentHashSet;
import org.enerj.jga.fn.BinaryFunctor;

/**
 * Computes the union of two Sets or Collections. If an argument is null, it is treated as an empty collection. 
 * <p>
 * 
 * @version $Id: UnionFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class UnionFunctor extends BinaryFunctor
{
    private static final long serialVersionUID = 3847391156082386623L;
    /** Singleton instance of this functor. */
    public static final UnionFunctor INSTANCE = new UnionFunctor();
    

    /**
     * Construct a UnionFunctor functor.
     */
    private UnionFunctor() 
    {
    }


    public Object fn(Object arg0, Object arg1)
    {
        if (arg0 == null) {
            return arg1; // null union x = x
        }
        
        if (arg1 == null) {
            return arg0; // x union null = x 
        }
        
        Class arg0Type = arg0.getClass();
        Class arg1Type = arg1.getClass();

        if (Set.class.isAssignableFrom(arg0Type) && Set.class.isAssignableFrom(arg1Type)) {
            PersistentHashSet arg0DSet = new PersistentHashSet( (Set)arg0 );
            return arg0DSet.union((Set)arg1);
        }
        
        if (Collection.class.isAssignableFrom(arg0Type) && Collection.class.isAssignableFrom(arg1Type)) {
            PersistentBag arg0DBag = new PersistentBag( (Collection)arg0 );
            return arg0DBag.union((Collection)arg1);
        }
        
        throw new IllegalArgumentException("Both arguments must be a Collection or a Set");
    }


    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(UnionFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof UnionFunctor.Visitor)
            ((UnionFunctor.Visitor)v).visit(this);
    }
    

    public String toString() {
        return "UnionFunctor";
    }
    
    // AcyclicVisitor
    

    /**
     * Interface for classes that may interpret a <b>UnionFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(UnionFunctor host);
    }
}

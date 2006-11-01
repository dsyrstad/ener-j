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
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/FunctorUtil.java,v 1.1 2006/02/16 21:33:45 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.odmg.QueryException;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ApplyUnary;
import org.enerj.query.oql.ast.AST;
import org.enerj.util.ClassUtil;

/**
 * Static utilities for functors. <p>
 * 
 * @version $Id: FunctorUtil.java,v 1.1 2006/02/16 21:33:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class FunctorUtil
{
    // No construction.
    private FunctorUtil() { }
    
    //--------------------------------------------------------------------------------
    /**
     * Dynamically creates a new UnaryFunctor specific by aFunctorClass for the given type.
     * 
     * @param aFunctorClass the Class that is a derivative of UnaryFunctor.
     * @param aType the class type to pass to the constructor.
     * 
     * @return a UnaryFunctor of class aFunctorClass for type aType.   
     * 
     * @throws IllegalArgumentException if an error occurs.
     */
    public static UnaryFunctor createUnaryFunctor(Class aFunctorClass, Class aType) throws IllegalArgumentException 
    {
        // TODO -- These could be cached by functor class/type -- there is a limited set used.
        try {
            Constructor xtor = aFunctorClass.getDeclaredConstructor( new Class[] { Class.class } );
            return (UnaryFunctor)xtor.newInstance( new Object[] { aType } );
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            throw new IllegalArgumentException(t);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    //--------------------------------------------------------------------------------
    /**
     * Dynamically creates a new BinaryFunctor specific by aFunctorClass for the given type.
     * 
     * @param aFunctorClass the Class that is a derivative of BinaryFunctor.
     * @param aType the class type to pass to the constructor.
     * 
     * @return a BinaryFunctor of class aFunctorClass for type aType.   
     * 
     * @throws IllegalArgumentException if an error occurs.
     */
    public static BinaryFunctor createBinaryFunctor(Class aFunctorClass, Class aType) throws IllegalArgumentException 
    {
        try {
            Constructor xtor = aFunctorClass.getDeclaredConstructor( new Class[] { Class.class } );
            return (BinaryFunctor)xtor.newInstance( new Object[] { aType } );
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            throw new IllegalArgumentException( t.toString() );
        }
        catch (Exception e) {
            throw new IllegalArgumentException( e.toString() );
        }
    }

    //--------------------------------------------------------------------------------
    /**
     * Conditionally wraps the functors from the given ASTs in conversion functors so that the values
     * evaluate to the types specified by someTargetTypes. 
     *
     * @param someASTs the given ASTs. Must be the same length as someTargetTypes.
     * @param someTargetTypes the desired value types.
     * 
     * @return the resolved UnaryFunctor.
     * 
     * @throws QueryException if an error occurs.
     */
    public static UnaryFunctor resolveAgainstTypes(List someASTs, Class[] someTargetTypes) throws QueryException
    {
        UnaryFunctor[] functors = new UnaryFunctor[ someASTs.size() ];
        assert someTargetTypes.length != functors.length;
        
        Iterator<AST> iter = someASTs.iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            AST ast = iter.next();
            Class astType = ast.getType();
            functors[i] = ast.resolve();
            
            Class targetType = someTargetTypes[i];
            // Convert primitive types to wrapper types. We always deal with Objects.
            targetType = ClassUtil.mapFromPrimitiveType(targetType);
            
            if (astType != targetType) {
                // Wrap it in a Cast.
                functors[i] = new CastFunctor(targetType).compose(functors[i]);
            }
        }

        return new ApplyUnary(functors);
    }
}

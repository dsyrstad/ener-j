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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/CollectionPredicate.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Collection;

import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.UnaryPredicate;
import org.enerj.util.TypeUtil;

/**
 * Performs "any" or "all" operation between a scalar value and a collection.  
 * <p>
 * 
 * @version $Id: CollectionPredicate.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class CollectionPredicate extends UnaryPredicate
{
    private static final long serialVersionUID = 5293053656958672330L;

    private UnaryFunctor mScalarFunctor;
    private BinaryFunctor mComparisonPredicate;
    private boolean mIsExists;
    private UnaryFunctor mCollectionFunctor;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a CollectionPredicate functor.
     * 
     * @param aScalarFunctor a unary functor that returns a scalar value. The functor is given the argument passed to fn(Object). 
     * @param aComparisonPredicate a binary predicate that compares the result of aScalarFunctor to each element of
     *  of the result of aCollectionFunctor.
     * @param isExists if true, only one comparison using aComparisonFunctor must return true. Otherwise, every
     *  comparison must return true.
     * @param aCollectionFunctor a unary functor that returns a Collection.  The functor is given the argument passed to fn(Object).
     * 
     */
    public CollectionPredicate(UnaryFunctor aScalarFunctor, BinaryFunctor aComparisonPredicate, boolean isExists, UnaryFunctor aCollectionFunctor) 
    {
        super();
        mScalarFunctor = aScalarFunctor;
        mComparisonPredicate = aComparisonPredicate;
        mIsExists = isExists;
        mCollectionFunctor = aCollectionFunctor;
    }

    //--------------------------------------------------------------------------------
    public Boolean fn(Object arg)
    {
        Object scalar = mScalarFunctor.fn(arg);
        Class<?> scalarType = Object.class;
        boolean isScalarNumeric = false;
        int scalarRank = -1;
        if (scalar != null) {
            scalarType = scalar.getClass();
            isScalarNumeric = TypeUtil.isNumericType(scalarType);
            if (isScalarNumeric) {
                scalarRank = TypeUtil.getRank(scalarType);
            }
        }
        
        
        Collection collection = (Collection)mCollectionFunctor.fn(arg);
        for (Object element : collection) {
            Object scalarTmp = scalar;
            if (isScalarNumeric && element != null && TypeUtil.isNumericType( element.getClass() ) ) {
                // Determine the rank of the type to determine promotion.
                Class<?> elementType = element.getClass();
                int elementRank = TypeUtil.getRank(elementType);
                Class<?> promotionType = (scalarRank > elementRank) ? scalarType : elementType; 
                
                if (scalarType != promotionType) {
                    scalarTmp = TypeUtil.getNumberPromotionFunctor(promotionType).fn(scalar);
                }
                else if (elementType != promotionType) {
                    element = TypeUtil.getNumberPromotionFunctor(promotionType).fn(element);
                }
            }
    
            boolean result = ((Boolean)mComparisonPredicate.fn(scalarTmp, element)).booleanValue();
            
            if (result && mIsExists) {
                return true;
            }
            else if (!result && !mIsExists) {
                return false;
            }
        }
        
        return !mIsExists;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(CollectionPredicate)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof CollectionPredicate.Visitor)
            ((CollectionPredicate.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "CollectionPredicate( " + mScalarFunctor + ',' + mComparisonPredicate + ", exists=" + mIsExists + ',' + mCollectionFunctor + ')';
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>CollectionPredicate</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(CollectionPredicate host);
    }
}

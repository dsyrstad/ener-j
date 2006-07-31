//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/CartesianProductCollectionFunctor.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Collection;

import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.query.oql.CartesianProductCollection;

/**
 * Wraps the collections passed as arguments to fn() in a CartesianProductCollection.  
 * <p>
 * 
 * @version $Id: CartesianProductCollectionFunctor.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class CartesianProductCollectionFunctor extends BinaryFunctor
{
    private static final long serialVersionUID = -4050152182465280159L;

    //--------------------------------------------------------------------------------
    /**
     * Construct a CartesianProductCollectionFunctor.
     */
    public CartesianProductCollectionFunctor() 
    {
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object arg1, Object arg2)
    {
        if (arg1 == null || arg2 == null || !(arg1 instanceof Collection) || !(arg2 instanceof Collection)) {
            throw new IllegalArgumentException("CartesianProductCollectionFunctor.fn() expected Collections, got: " + arg1.getClass() + ", " + arg2.getClass());
        }
        
        return new CartesianProductCollection((Collection)arg1, (Collection)arg2);
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(CartesianProductCollectionFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof CartesianProductCollectionFunctor.Visitor)
            ((CartesianProductCollectionFunctor.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "CartesianProductCollectionFunctor";
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>CartesianProductCollectionFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(CartesianProductCollectionFunctor host);
    }
}

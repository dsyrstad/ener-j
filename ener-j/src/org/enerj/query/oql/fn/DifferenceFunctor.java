//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/DifferenceFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Collection;
import java.util.Set;

import org.enerj.core.RegularDBag;
import org.enerj.core.RegularDSet;
import org.enerj.jga.fn.BinaryFunctor;

/**
 * Computes the difference between two Sets or Collections. If either argument is null, null is returned. 
 * <p>
 * 
 * @version $Id: DifferenceFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class DifferenceFunctor extends BinaryFunctor
{
    private static final long serialVersionUID = -2420585104847984188L;

    /** Singleton instance of this functor. */
    public static final DifferenceFunctor INSTANCE = new DifferenceFunctor();
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a DifferenceFunctor functor.
     */
    private DifferenceFunctor() 
    {
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object arg0, Object arg1)
    {
        if (arg0 == null || arg1 == null) {
            return null;
        }
        
        Class arg0Type = arg0.getClass();
        Class arg1Type = arg1.getClass();

        if (Set.class.isAssignableFrom(arg0Type) && Set.class.isAssignableFrom(arg1Type)) {
            RegularDSet arg0DSet = new RegularDSet( (Set)arg0 );
            return arg0DSet.difference((Set)arg1);
        }
        
        if (Collection.class.isAssignableFrom(arg0Type) && Collection.class.isAssignableFrom(arg1Type)) {
            RegularDBag arg0DBag = new RegularDBag( (Collection)arg0 );
            return arg0DBag.difference((Collection)arg1);
        }
        
        throw new IllegalArgumentException("Both arguments must be a Collection or a Set");
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(DifferenceFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof DifferenceFunctor.Visitor)
            ((DifferenceFunctor.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "DifferenceFunctor";
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>DifferenceFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(DifferenceFunctor host);
    }
}

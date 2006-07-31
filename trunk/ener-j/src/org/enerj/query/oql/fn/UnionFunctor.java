//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/UnionFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Collection;
import java.util.Set;

import org.enerj.core.RegularDBag;
import org.enerj.core.RegularDSet;
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
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a UnionFunctor functor.
     */
    private UnionFunctor() 
    {
    }

    //--------------------------------------------------------------------------------
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
            RegularDSet arg0DSet = new RegularDSet( (Set)arg0 );
            return arg0DSet.union((Set)arg1);
        }
        
        if (Collection.class.isAssignableFrom(arg0Type) && Collection.class.isAssignableFrom(arg1Type)) {
            RegularDBag arg0DBag = new RegularDBag( (Collection)arg0 );
            return arg0DBag.union((Collection)arg1);
        }
        
        throw new IllegalArgumentException("Both arguments must be a Collection or a Set");
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(UnionFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof UnionFunctor.Visitor)
            ((UnionFunctor.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "UnionFunctor";
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>UnionFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(UnionFunctor host);
    }
}

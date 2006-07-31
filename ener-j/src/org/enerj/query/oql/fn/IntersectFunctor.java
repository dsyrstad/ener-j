//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/IntersectFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Collection;
import java.util.Set;

import org.enerj.core.RegularDBag;
import org.enerj.core.RegularDSet;
import org.enerj.jga.fn.BinaryFunctor;

/**
 * Computes the intersection of two Sets or Collections. If either argument is null, null is returned. 
 * <p>
 * 
 * @version $Id: IntersectFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class IntersectFunctor extends BinaryFunctor
{
    private static final long serialVersionUID = 4547094531656036055L;
    /** Singleton instance of this functor. */
    public static final IntersectFunctor INSTANCE = new IntersectFunctor();
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a IntersectFunctor functor.
     */
    private IntersectFunctor() 
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
            return arg0DSet.intersection((Set)arg1);
        }
        
        if (Collection.class.isAssignableFrom(arg0Type) && Collection.class.isAssignableFrom(arg1Type)) {
            RegularDBag arg0DBag = new RegularDBag( (Collection)arg0 );
            return arg0DBag.intersection((Collection)arg1);
        }
        
        throw new IllegalArgumentException("Both arguments must be a Collection or a Set");
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(IntersectFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof IntersectFunctor.Visitor)
            ((IntersectFunctor.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "IntersectFunctor";
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>IntersectFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(IntersectFunctor host);
    }
}

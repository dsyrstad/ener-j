//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/ConvertCollectionToSet.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.enerj.jga.fn.UnaryFunctor;

/**
 * Converts the given Collection parameter to a Set.  
 * <p>
 * 
 * @version $Id: ConvertCollectionToSet.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ConvertCollectionToSet extends UnaryFunctor
{
    private static final long serialVersionUID = -5104309872066182843L;

    public static final ConvertCollectionToSet INSTANCE = new ConvertCollectionToSet();
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a ConvertCollectionToSet functor.
     */
    private ConvertCollectionToSet() 
    {
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object arg)
    {
        // If null or already a Set, return the argument.
        if (arg == null || arg instanceof Set) {
            return arg;
        }
        
        if (!(arg instanceof Collection)) {
            throw new IllegalArgumentException("Argument is not a Collection");
        }
        
        return new HashSet((Collection)arg);
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(ConvertCollectionToSet)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConvertCollectionToSet.Visitor)
            ((ConvertCollectionToSet.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "ConvertCollectionToSet()";
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>ConvertCollectionToSet</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConvertCollectionToSet host);
    }
}

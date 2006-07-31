//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/ElementExistsPredicate.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.enerj.jga.fn.UnaryPredicate;

/**
 * Performs "exists(query)" or "unique(query)" operation on the argument, which must be a Collection,
 * Map, or array. Returns false if the argument is null.  
 * <p>
 * 
 * @version $Id: ElementExistsPredicate.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ElementExistsPredicate extends UnaryPredicate
{
    private static final long serialVersionUID = -1645644055765651950L;

    /** Element that returns performs the exists() operation. */
    public static ElementExistsPredicate EXISTS_INSTANCE = new ElementExistsPredicate(true);
    /** Element that returns performs the unique() operation. */
    public static ElementExistsPredicate UNIQUE_INSTANCE = new ElementExistsPredicate(false);
    
    private boolean mIsExists;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a ElementExistsPredicate functor.
     * 
     * @param isExists if true, fn(Object) returns true if the argument contains at least one element (exists()). 
     *  Otherwise, fn(Object) returns true if the argument contains exactly one element (unique()).
     */
    private ElementExistsPredicate(boolean isExists) 
    {
        mIsExists = isExists;
    }

    //--------------------------------------------------------------------------------
    public Boolean fn(Object arg)
    {
        if (arg == null) {
            return Boolean.FALSE;
        }
        
        int size;
        if (arg.getClass().isArray()) {
            size = Array.getLength(arg);
        }
        else if (arg instanceof Collection) {
            size = ((Collection)arg).size();
        }
        else if (arg instanceof Map) {
            size = ((Map)arg).size();
        }
        else {
            throw new IllegalArgumentException("Expected array, Collection, or Map");
        }
        
        if (mIsExists) {
            return size >= 1;
        }
        
        return size == 1;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(ElementExistsPredicate)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ElementExistsPredicate.Visitor)
            ((ElementExistsPredicate.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "ElementExistsPredicate(" + (mIsExists ? "exists" : "unique") + ')';
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>ElementExistsPredicate</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ElementExistsPredicate host);
    }
}

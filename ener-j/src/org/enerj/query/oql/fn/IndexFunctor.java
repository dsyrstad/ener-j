//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/IndexFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import org.enerj.jga.fn.UnaryFunctor;

/**
 * Indexes the given functor. If the result of the functor is an array or List, the element at the
 * specified index is returned. If the result of the functor is a Map, the index is used as a key to
 * the Map and the corresponding value is returned. Otherwise, the result of the functor is treated as a String
 * (via Object.toString()) and the
 * Character at the specified index is returned. For Lists, arrays, and Strings, the argument passed to
 * fn(Object) must be a Number that is convertable to an integer.   
 * <p>
 * 
 * @version $Id: IndexFunctor.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class IndexFunctor extends UnaryFunctor
{
    private static final long serialVersionUID = 232590174110545868L;

    private UnaryFunctor mIndexedFunctor;

    //--------------------------------------------------------------------------------
    /**
     * Construct a IndexFunctor functor.
     * 
     * @param anIndexedFunctor the functor whose result will be indexed.
     */
    public IndexFunctor(UnaryFunctor anIndexedFunctor) 
    {
        super();
        mIndexedFunctor = anIndexedFunctor;
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object arg)
    {
        Object expr = mIndexedFunctor.fn(null);
        if (expr == null) {
            return null;
        }
        
        Class exprClass = expr.getClass();

        if (Map.class.isAssignableFrom(exprClass)) {
            // arg == null is a valid key to a Map.
            return ((Map)expr).get(arg);
        }
        
        if (arg == null) {
            return null;
        }

        int index = ((Number)arg).intValue();

        if (index < 0) {
            return null;
        }
        
        if (exprClass.isArray()) {
            try {
                return Array.get(expr, index);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                return null;
            }
        }

        if (List.class.isAssignableFrom(exprClass)) {
            try {
                return ((List)expr).get(index);
            }
            catch (IndexOutOfBoundsException e) {
                return null;
            }
        }

        try {
            return expr.toString().charAt(index);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(IndexFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof IndexFunctor.Visitor)
            ((IndexFunctor.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "IndexFunctor[" + mIndexedFunctor + ']';
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>IndexFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(IndexFunctor host);
    }
}

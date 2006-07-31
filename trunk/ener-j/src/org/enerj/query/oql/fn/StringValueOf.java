//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/StringValueOf.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import org.enerj.jga.fn.UnaryFunctor;

/**
 * Converts the argument to a String. Returns "<null>" if the value is null.  
 * <p>
 * 
 * @version $Id: StringValueOf.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class StringValueOf extends UnaryFunctor
{
    private static final long serialVersionUID = 4385482694765915968L;

    //--------------------------------------------------------------------------------
    /**
     * Construct a StringValueOf functor.
     */
    public StringValueOf() 
    {
        super();
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object arg)
    {
        if (arg == null) {
            return null;
        }

        return arg.toString();
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(StringValueOf)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof StringValueOf.Visitor)
            ((StringValueOf.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "StringValueOf";
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>StringValueOf</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(StringValueOf host);
    }
}

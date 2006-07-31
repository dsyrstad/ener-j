//Ener-J
//Copyright 2001-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/ParameterValue.java,v 1.2 2006/03/05 03:37:28 dsyrstad Exp $

package org.enerj.query.oql.fn;

import org.enerj.jga.fn.UnaryFunctor;

/**
 * Returns the value set by setValue(). The argument to fn() is ignored.  
 * <p>
 * 
 * @version $Id: ParameterValue.java,v 1.2 2006/03/05 03:37:28 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ParameterValue extends UnaryFunctor
{
    private static final long serialVersionUID = -3697298062559140904L;

    private Object mValue = null;
    private Class mType;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a ParameterValue.
     */
    public ParameterValue(Class aType) 
    {
        mType = aType;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the parameter's type.
     *
     * @return the type.
     */
    public Class getType()
    {
        return mType;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the parameter's value.
     *
     * @return the value, or null if it is not set.
     */
    public Object getValue()
    {
        return mValue;
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Sets the parameter's value.
     *
     * @param aValue the value to be set. May be null to clear it. 
     * 
     * @throws IllegalArgumentException if aValue is not of the type given at construction.
     */
    public void setValue(Object aValue)
    {
        if (aValue == null || mType.isAssignableFrom(aValue.getClass())) {
            mValue = aValue;
        }
        else {
            throw new IllegalArgumentException("Parameter is not of type " + mType.getName()); 
        }
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object notused)
    {
        return mValue;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(ParameterValue)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ParameterValue.Visitor)
            ((ParameterValue.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "ParameterValue(" + mType + ':' + mValue + ')';
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>ParameterValue</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ParameterValue host);
    }
}

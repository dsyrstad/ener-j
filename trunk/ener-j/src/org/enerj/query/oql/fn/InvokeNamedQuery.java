//Ener-J
//Copyright 2001-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/InvokeNamedQuery.java,v 1.3 2006/05/23 03:37:00 dsyrstad Exp $

package org.enerj.query.oql.fn;

import org.enerj.jga.fn.UnaryFunctor;

/**
 * Invokes a defined (named) query. The argument to fn() is null (for no parameters) or an array of Objects
 * representing the parameter values.  
 * <p>
 * 
 * @version $Id: InvokeNamedQuery.java,v 1.3 2006/05/23 03:37:00 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class InvokeNamedQuery extends UnaryFunctor
{
    private static final long serialVersionUID = -8407128497387130130L;

    private String mDefName;
    private ParameterValue[] mParameterValues;
    private UnaryFunctor mQueryFunctor;
    private Class mResultType;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a InvokeNamedQuery.
     */
    public InvokeNamedQuery(String aDefName, UnaryFunctor aQueryFunctor, ParameterValue[] aParameterValues, Class aResultType) 
    {
        mDefName = aDefName;
        mQueryFunctor = aQueryFunctor;
        mParameterValues = aParameterValues;
        mResultType = aResultType;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the defined name.
     *
     * @return a String.
     */
    public String getDefinedName()
    {
        return mDefName;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the Parameter Values.
     *
     * @return a ParameterValue[].
     */
    public ParameterValue[] getParameterValues()
    {
        return mParameterValues;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the Query Functor.
     *
     * @return a UnaryFunctor.
     */
    public UnaryFunctor getQueryFunctor()
    {
        return mQueryFunctor;
    }

    public Class getResultType()
    {
        return mResultType;
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object arg)
    {
        if (arg != null && !(arg instanceof Object[]) ) {
            throw new IllegalArgumentException("Expected Object[] of parameters to named query '" + mDefName + "'");
        }
        
        Object[] params = (Object[])arg;
        if ((params == null && mParameterValues.length > 0) ||
            (params != null && params.length != mParameterValues.length) ) {
            throw new IllegalArgumentException("Expected " + mParameterValues.length + " parameter(s) to named query '" + mDefName + "'");
        }

        // Set the parameter values.
        for (int i = 0; i < mParameterValues.length; i++) {
            mParameterValues[i].setValue(params[i]);
        }
        
        return mQueryFunctor.fn(null);
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(InvokeNamedQuery)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof InvokeNamedQuery.Visitor)
            ((InvokeNamedQuery.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "InvokeNamedQuery(" + mDefName + ':' + mQueryFunctor + ')';
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>InvokeNamedQuery</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(InvokeNamedQuery host);
    }

}

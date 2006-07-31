// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/ValueListAST.java,v 1.5 2006/02/16 21:33:44 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.Iterator;
import java.util.List;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.ApplyUnary;
import org.enerj.query.oql.fn.FunctorUtil;


/**
 * The ValueList AST. <p>
 * 
 * @version $Id: ValueListAST.java,v 1.5 2006/02/16 21:33:44 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ValueListAST extends BaseAST
{
    private List<AST> mValues;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a ValueListAST. 
     *
     * @param someValues a List of ASTs.
     */
    public ValueListAST(List<AST> someValues)
    {
        mValues = someValues;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the mValues.
     *
     * @return a List of ASTs.
     */
    public List<AST> getValues()
    {
        return mValues;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see org.enerj.query.oql.ast.BaseAST#getType0()
     */
    protected Class getType0() throws QueryException
    {
        return List.class;
    }

    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * <p>
     * Note: The return type of the returned functor's fn() method is an Object[] of values.
     * </p>
     * @see org.enerj.query.oql.ast.BaseAST#resolve0()
     */
    protected UnaryFunctor resolve0() throws QueryException
    {
        return new ApplyUnary( getFunctors() );
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the array of UnaryFunctors representing the argument list.
     * 
     * @return an array of UnaryFunctor.
     * 
     * @throws QueryException if an error occurs.
     */
    private UnaryFunctor[] getFunctors() throws QueryException
    {
        UnaryFunctor[] functors = new UnaryFunctor[ mValues.size() ];
        Iterator<AST> iter = mValues.iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            AST ast = iter.next();
            functors[i] = ast.resolve();
        }
        return functors;
    }

    
    //--------------------------------------------------------------------------------
    /**
     * Like resolve(), only adds additional conversion functors so that the values
     * evaluate to the types specified by someTargetTypes. 
     *
     * @param someTargetTypes the desired value types.
     * 
     * @return the resolved UnaryFunctor.
     * 
     * @throws QueryException if an error occurs.
     */
    public UnaryFunctor resolveAgainstTypes(Class[] someTargetTypes) throws QueryException
    {
        return FunctorUtil.resolveAgainstTypes(mValues, someTargetTypes);
    }
}

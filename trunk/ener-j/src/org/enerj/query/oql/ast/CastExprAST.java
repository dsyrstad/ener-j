// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/CastExprAST.java,v 1.4 2005/10/29 21:21:34 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.fn.CastFunctor;
import org.enerj.util.TypeUtil;



/**
 * The CastExpr AST. <p>
 * 
 * @version $Id: CastExprAST.java,v 1.4 2005/10/29 21:21:34 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class CastExprAST extends BaseAST
{
    private TypeAST mType; 
    private AST mExpr;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a CastExprAST. 
     *
     * @param someType a TypeAST.
     * @param anExpr the expression whose result type is to be cast.
     */
    public CastExprAST(TypeAST aType, AST anExpr)
    {
        mType = aType;
        mExpr = anExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the expression to be cast.
     *
     * @return an AST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Type.
     *
     * @return a TypeAST.
     */
    public TypeAST getTypeAST()
    {
        return mType;
    }
    
    //--------------------------------------------------------------------------------
    protected Class getType0() throws QueryException
    {
        Class castToType = mType.getType();
        Class exprType = mExpr.getType();
        
        // Eval compatibility, either same, upcast (cast to more general type) or 
        // downcast (cast to more specific type). Upcasts really aren't necessary.
        if (castToType != exprType &&                 // not the same 
            !castToType.isAssignableFrom(exprType) && // not upcast
            !exprType.isAssignableFrom(castToType) && // not downcast
            (!TypeUtil.isNumericType(castToType) || !TypeUtil.isNumericType(exprType)) ) { // not Number -> Number cast
            throw new QueryException("Cannot cast from " + exprType.getName() + " to " + castToType.getName());
        }
        
        return castToType;
    }
    
    //--------------------------------------------------------------------------------
    protected UnaryFunctor resolve0() throws QueryException
    {
    	getType(); // Make sure validation has been performed.
    	
        Class castToType = mType.getType();
        Class exprType = mExpr.getType();
        UnaryFunctor exprFunctor = mExpr.resolve();

        // Cast is not necessary if they are the same or upcasting.
        if (castToType == exprType ||                // the same 
            castToType.isAssignableFrom(exprType)) { // upcast
            return exprFunctor;
        }
        
        return new CastFunctor(castToType).compose(exprFunctor);
    }
}

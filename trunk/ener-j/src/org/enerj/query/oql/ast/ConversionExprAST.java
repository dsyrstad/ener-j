// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/ConversionExprAST.java,v 1.4 2006/02/14 21:13:17 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.odmg.QueryException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.OQLTokens;
import org.enerj.query.oql.TokenType;
import org.enerj.query.oql.fn.ConvertCollectionToSet;
import org.enerj.query.oql.fn.ConvertSingletonCollectionToElement;
import org.enerj.query.oql.fn.FlattenCollection;


/**
 * The ConversionExpr AST. <p>
 * 
 * @version $Id: ConversionExprAST.java,v 1.4 2006/02/14 21:13:17 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ConversionExprAST extends BaseAST
{
    private TokenType mOp;
    private AST mExpr;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a ConversionExprAST. 
     *
     * @param anOp one of TOK_LISTTOSET, TOK_ELEMENT, TOK_DISTINCT, or TOK_FLATTEN.
     * @param anExpr the expression to be operated upon.
     */
    public ConversionExprAST(TokenType anOp, AST anExpr)
    {
        assert  anOp == OQLTokens.TOK_LISTTOSET || anOp == OQLTokens.TOK_ELEMENT || 
                anOp == OQLTokens.TOK_DISTINCT || anOp == OQLTokens.TOK_FLATTEN;
        mOp = anOp;
        mExpr = anExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the Expr that is a parameter to this conversion.
     *
     * @return an AST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the operation.
     *
     * @return a TokenType, one of TOK_LISTTOSET, TOK_ELEMENT, TOK_DISTINCT, or TOK_FLATTEN.
     */
    public TokenType getOp()
    {
        return mOp;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        Class type = mExpr.getType();
        if (mOp == OQLTokens.TOK_LISTTOSET) {
            // We accept either a List or a Collection (which may be a Set) as a parameter. 
            if (Collection.class.isAssignableFrom(type)) {
                return Set.class;
            }

            throw new QueryException("Cannot convert a " + type.getName() + " to a Set.");
        }
        
        if (mOp == OQLTokens.TOK_ELEMENT) {
            // Convert a singleton collection to its only element. We don't know the
            // type of the element, so return Object.
            if (Collection.class.isAssignableFrom(type)) {
                return Object.class;
            }

            throw new QueryException("Cannot convert a " + type.getName() + " to an element.");
        }

        if (mOp == OQLTokens.TOK_DISTINCT) { 
            // Convert a collection (which may already be a Set) to a Set of distinct elements.
            // TODO How is this different from listtoset? Is it? The OQL doc says something about
            // TODO returning the same collection. But we don't want to modify persistent collections
            // TODO and some collections are not modifiable.
            if (Collection.class.isAssignableFrom(type)) {
                return Set.class;
            }

            throw new QueryException("Cannot convert a " + type.getName() + " to distinct elements.");
        }
        
        if (mOp == OQLTokens.TOK_FLATTEN) {
            // Flatten the first level of a collection of collections.
            if (Collection.class.isAssignableFrom(type)) {
                if (Set.class.isAssignableFrom(type)) {
                    return Set.class;
                }
                
                if (List.class.isAssignableFrom(type)) {
                    return List.class;
                }
                
                return Collection.class;
            }

            throw new QueryException("Cannot flatten a " + type.getName());
        }

        throw new QueryException("Invalid operator '" + mOp.getName() + "'");
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        Class resultType = getType();
        
        UnaryFunctor functor = mExpr.resolve();

        if (mOp == OQLTokens.TOK_LISTTOSET || mOp == OQLTokens.TOK_DISTINCT /* TODO - see getType() */) {
            Class exprType = mExpr.getType();
            if (Set.class.isAssignableFrom(exprType)) {
                // It's already a Set.
                return functor;
            }
            
            return ConvertCollectionToSet.INSTANCE.compose(functor); 
        }
        
        if (mOp == OQLTokens.TOK_ELEMENT) {
            // Convert a singleton collection to its only element. We don't know the
            // type of the element, so return Object.
            return ConvertSingletonCollectionToElement.INSTANCE.compose(functor);
        }

        if (mOp == OQLTokens.TOK_FLATTEN) {
            return FlattenCollection.getInstance(resultType).compose(functor);
        }
           
        throw new QueryException("Invalid operator."); // Shouldn't really get here if getType0() does it's job.
    }
}

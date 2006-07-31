// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/IndexedExprAST.java,v 1.4 2006/02/21 02:37:47 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.List;
import java.util.Map;

import org.odmg.QueryException;
import org.odmg.QueryInvalidException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.query.oql.ParserException;
import org.enerj.query.oql.fn.ExtractSublistFunctor;
import org.enerj.query.oql.fn.IndexFunctor;
import org.enerj.util.TypeUtil;




/**
 * The IndexedExpr AST. <p>
 * 
 * @version $Id: IndexedExprAST.java,v 1.4 2006/02/21 02:37:47 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class IndexedExprAST extends BaseAST
{
    private AST mExpr;  // The indexable expression.
    private AST mIndex; // an index expression or IndexRangeAST.
    
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a IndexedExprAST. 
     *
     * @param anExpr the left-hand expression (the indexable expression).
     * @param anIndex an index expression or an IndexRangeAST.
     */
    public IndexedExprAST(AST anExpr, AST anIndex) throws ParserException
    {
        assert !(anIndex instanceof IndexListAST);
        
        mExpr = anExpr;
        mIndex = anIndex;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the left-hand indexable expression.
     *
     * @return an AST.
     */
    public AST getExpr()
    {
        return mExpr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the index AST.
     *
     * @return an AST.
     */
    public AST getIndex()
    {
        return mIndex;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     */
    protected Class getType0() throws QueryException
    {
        Class exprType = mExpr.getType();
        
        // OQL BNF allows expr[1,2,3], but it means nothing!
        // expr[1] expr[2:3] on lists, arrays, or Strings.
        // expr["value"] on maps.
        
        if (exprType.isArray()) {
            if (mIndex instanceof IndexRangeAST) {
                return exprType;
            }
            
            return exprType.getComponentType();
        }

        if (List.class.isAssignableFrom(exprType) || Map.class.isAssignableFrom(exprType)) {
            if (mIndex instanceof IndexRangeAST) {
                if (Map.class.isAssignableFrom(exprType)) {
                    throw new QueryInvalidException("Cannot specify a range index on a map");
                }
                
                return List.class;
            }
            
            // Use generic parameter type, if available.
            return TypeUtil.getCollectionGenericType(exprType);
        }
        
        if (TypeUtil.isStringType(exprType)) {
            return String.class;
        }
        
        throw new QueryInvalidException("Invalid type for indexed expression. Expected a String, array, Map, or Collection, but type was " + exprType.getName());
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    protected UnaryFunctor resolve0() throws QueryException 
    {
        Class exprType = mExpr.getType();
        UnaryFunctor exprFunctor = mExpr.resolve();

        if (mIndex instanceof IndexRangeAST) {
            IndexRangeAST rangeAST = (IndexRangeAST)mIndex;
            UnaryFunctor startFunctor = rangeAST.getStartExpr().resolve();
            UnaryFunctor endFunctor = rangeAST.getEndExpr().resolve();
            
            return new ExtractSublistFunctor(exprFunctor).compose(startFunctor, endFunctor);
        }

        return new IndexFunctor(exprFunctor).compose( mIndex.resolve() );
    }
}

// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/TypeAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import static org.enerj.query.oql.OQLTokens.TOK_ARRAY;
import static org.enerj.query.oql.OQLTokens.TOK_BAG;
import static org.enerj.query.oql.OQLTokens.TOK_BOOLEAN;
import static org.enerj.query.oql.OQLTokens.TOK_CHAR;
import static org.enerj.query.oql.OQLTokens.TOK_DATE;
import static org.enerj.query.oql.OQLTokens.TOK_DICTIONARY;
import static org.enerj.query.oql.OQLTokens.TOK_DOUBLE;
import static org.enerj.query.oql.OQLTokens.TOK_ENUM;
import static org.enerj.query.oql.OQLTokens.TOK_FLOAT;
import static org.enerj.query.oql.OQLTokens.TOK_IDENT;
import static org.enerj.query.oql.OQLTokens.TOK_INTERVAL;
import static org.enerj.query.oql.OQLTokens.TOK_LIST;
import static org.enerj.query.oql.OQLTokens.TOK_LONG;
import static org.enerj.query.oql.OQLTokens.TOK_LONG_LONG;
import static org.enerj.query.oql.OQLTokens.TOK_OCTET;
import static org.enerj.query.oql.OQLTokens.TOK_SET;
import static org.enerj.query.oql.OQLTokens.TOK_SHORT;
import static org.enerj.query.oql.OQLTokens.TOK_STRING;
import static org.enerj.query.oql.OQLTokens.TOK_TIME;
import static org.enerj.query.oql.OQLTokens.TOK_TIMESTAMP;
import static org.enerj.query.oql.OQLTokens.TOK_UNSIGNED_LONG;
import static org.enerj.query.oql.OQLTokens.TOK_UNSIGNED_SHORT;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.odmg.DBag;
import org.odmg.QueryException;
import org.enerj.query.oql.EvaluatorContext;
import org.enerj.query.oql.TokenType;

/**
 * The Type AST. <p>
 * 
 * @version $Id: TypeAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class TypeAST extends BaseAST
{
    /** The token type reflecting the actual type. */
    private TokenType mTokenType;
    /** First identifier for enum, or user-defined type name. May be null. */
    private String mIdent1;
    /** Second identifier for enum. May be null. */
    private String mIdent2;
    /** Subtype for collections, key type for dictionary.  May be null. */
    private TypeAST mSubType1;
    /** Value type for dictionary.  May be null. */
    private TypeAST mSubType2;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new TypeAST.
     *  
     * @param aTokenType the token type reflecting the actual type.
     * @param anIdent1 first identifier for enum, or user-defined type name. May be null.
     * @param anIdent2 second identifier for enum. May be null.
     * @param aSubType1 subtype for collections, key type for dictionary.  May be null.
     * @param aSubType2 value type for dictionary.  May be null.
     */
    public TypeAST(TokenType aTokenType, String anIdent1, String anIdent2, TypeAST aSubType1,
                    TypeAST aSubType2)
    {
        mTokenType = aTokenType;
        mIdent1 = anIdent1;
        mIdent2 = anIdent2;
        mSubType1 = aSubType1;
        mSubType2 = aSubType2;
    }

    //--------------------------------------------------------------------------------
    /**
     * First identifier for enum, or user-defined type name. May be null. 
     * 
     * @return the ident1.
     */
    public String getIdent1()
    {
        return mIdent1;
    }

    //--------------------------------------------------------------------------------
    /**
     * Second identifier for enum. May be null. 
     * 
     * @return the ident2.
     */
    public String getIdent2()
    {
        return mIdent2;
    }

    //--------------------------------------------------------------------------------
    /**
     * Subtype for collections, key type for dictionary.  May be null.
     * 
     * @return the subType1.
     */
    public TypeAST getSubType1()
    {
        return mSubType1;
    }
    /**

    //--------------------------------------------------------------------------------
    /** 
     * Gets the value type for dictionary.  May be null.
     * 
     * @return the subType2.
     */
    public TypeAST getSubType2()
    {
        return mSubType2;
    }

    //--------------------------------------------------------------------------------
    /**
     * The token type reflecting the actual type. 
     * 
     * @return the tokenType.
     */
    public TokenType getTokenType()
    {
        return mTokenType;
    }

    //--------------------------------------------------------------------------------
    protected Class getType0() throws QueryException
    {
        if (mTokenType == TOK_UNSIGNED_SHORT) {
            return Integer.class;
        }
        
        if (mTokenType == TOK_UNSIGNED_LONG) {
            return Long.class;
        }
        
        if (mTokenType == TOK_LONG) {
            return Integer.class;
        }

        if (mTokenType == TOK_LONG_LONG) {
            return Long.class;
        }
        
        if (mTokenType == TOK_SHORT) {
            return Short.class;
        }
        
        if (mTokenType == TOK_FLOAT) {
            return Float.class;
        }
        
        if (mTokenType == TOK_DOUBLE) {
            return Double.class;
        }

        if (mTokenType == TOK_CHAR) {
            return Character.class;
        }
        
        if (mTokenType == TOK_STRING) {
            return String.class;
        }
        
        if (mTokenType == TOK_BOOLEAN) {
            return Boolean.class;
        }
        
        if (mTokenType == TOK_DATE) {
            return java.sql.Date.class;
        }
        
        if (mTokenType == TOK_TIME) {
            return java.sql.Time.class;
        }
        
        if (mTokenType == TOK_INTERVAL) {
            throw new UnsupportedOperationException("Interval type not supported");
        }
        
        if (mTokenType == TOK_TIMESTAMP) {
            return Timestamp.class;
        }
        
        if (mTokenType == TOK_OCTET) {
            return Byte.class;
        }
        
        if (mTokenType == TOK_ENUM) {
            throw new UnsupportedOperationException("Enum typecast not supported. Cast to java enum type class instead.");
        }

        // TODO Handle parameterized types for collections.
        // Note: ODMG says in 7.2.1 that these should map to the ODMG "D" collection types.
        // That's kind of stupid though. How does one cast to a java.util.Set?
        if (mTokenType == TOK_SET) {
            return Set.class;
        }
        
        if (mTokenType == TOK_BAG) {
            return DBag.class;
        }
        
        if (mTokenType == TOK_LIST) {
            return List.class;
        }
        
        if (mTokenType == TOK_ARRAY) {
            return List.class;
        }
    
        if (mTokenType == TOK_DICTIONARY) {
            return Map.class;
        }

        if (mTokenType == TOK_IDENT) {
            // mIdent1 is the class name. 
            // Check imports. java.lang is automatic.
            Class type = EvaluatorContext.getContext().resolveClass(mIdent1);
            if (type != null) {
                return type;
            }
            
            throw new QueryException("Unknown cast type: " + mIdent1); 
        }
        
        throw new QueryException("Unknown cast type: " + mTokenType);
    }
}

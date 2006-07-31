// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/Token.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $

package org.enerj.query.oql;

/**
 * A Token returned from a lexer.
 *
 * @version $Id: Token.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class Token {
    private TokenType mType;
    private String mValue;
    private TextPosition mPosition;
    
    //--------------------------------------------------------------------------------
    public Token(TokenType aType, String aValue, TextPosition aPosition) 
    {
        mType = aType;
        mValue = aValue;
        mPosition = aPosition;
    }
    
    //--------------------------------------------------------------------------------
    public TokenType getTokenType()
    {
        return mType;
    }
    
    //--------------------------------------------------------------------------------
    public String getValue()
    {
        return mValue;
    }
    
    //--------------------------------------------------------------------------------
    public TextPosition getTextPosition()
    {
        return mPosition;
    }
    
    //--------------------------------------------------------------------------------
    public boolean isTokenType(TokenType aType)
    {
        return mType.equals(aType);
    }
    
    //--------------------------------------------------------------------------------
    public String toString()
    {
        return mType.toString() + "[value=" + mValue + " position=" + mPosition + ']';
    }

}

// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/TokenType.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $

package org.enerj.query.oql;

/**
 * A type of Token returned from a lexer. Serves as an enumerated type.
 *
 * @version $Id: TokenType.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class TokenType {
    private String mName;
    
    //--------------------------------------------------------------------------------
    public TokenType(String aName) 
    {
        mName = aName;
    }
    
    //--------------------------------------------------------------------------------
    public String getName()
    {
        return mName;
    }

    //--------------------------------------------------------------------------------
    public String toString()
    {
        return mName;
    }

    //--------------------------------------------------------------------------------
    public boolean equals(Object anObject)
    {
        if (!(anObject instanceof TokenType)) {
            return false;
        }
        
        return ((TokenType)anObject).mName.equals(mName);
    }

    //--------------------------------------------------------------------------------
    public int hashCode()
    {
        return mName.hashCode();
    }
}

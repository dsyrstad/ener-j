// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ParserException.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $

package org.enerj.query.oql;


/**
 * Exception used by the parser.
 *
 * @version $Id: ParserException.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class ParserException extends Exception {
    private TextPosition mPosition;
    
    //--------------------------------------------------------------------------------
    /**
     * Constructs a ParserException using optional cause and TextPosition.
     *
     * @param aMsg the message.
     * @param aPosition a TextPosition. May be null.
     * @param aCause a nested exception. May be null.
     */
    public ParserException(String aMsg, TextPosition aPosition, Exception aCause) 
    {
        super(aMsg, aCause);
        mPosition = aPosition;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the TextPosition of the exception.
     *
     * @return the TextPosition of the exception.
     */
    public TextPosition getTextPosition()
    {
        return mPosition;
    }
    
    //--------------------------------------------------------------------------------
    public String getMessage()
    {
        return super.getMessage() + (mPosition != null ? (": " + mPosition.toString()) : "");
    }
}

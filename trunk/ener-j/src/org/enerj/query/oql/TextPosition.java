// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/TextPosition.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $

package org.enerj.query.oql;


/**
 * A line and column position in a text stream. Also contains the text of the entire line.
 *
 * @version $Id: TextPosition.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class TextPosition
{
    private String mFileName;
    private int mLineNumber;
    private int mColumn;
    private String mLineText;

    //--------------------------------------------------------------------------------
    public TextPosition(String aFileName, int aLineNumber, int aColumn, String aLineText)
    {
        mFileName = aFileName;
        mLineNumber = aLineNumber;
        mColumn = aColumn;
        mLineText = aLineText;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the 1-based line number.
     */
    public int getLineNumber()
    {
        return mLineNumber;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the 1-based column position.
     */
    public int getColumn()
    {
        return mColumn;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the text of the line, including the text ahead of the column position.
     */
    public String getLineText()
    {
        return mLineText;
    }

    //--------------------------------------------------------------------------------
    public String toString()
    {
        return (mFileName == null ? "" : (mFileName + ": ")) + "line " + mLineNumber + " column " + mColumn + " line=" + mLineText;
    }
}

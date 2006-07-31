//Ener-J
//Copyright 2000-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/ToStringTextColumn.java,v 1.1 2005/11/29 03:55:49 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

/**
 * Displays the columns value as the result of object.toString(). <p>
 * 
 * @version $Id: ToStringTextColumn.java,v 1.1 2005/11/29 03:55:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ToStringTextColumn extends TextColumn
{

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is not editable.
     * Heading alignment is leading.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     */
    public ToStringTextColumn(String aColumnTitle)
    {
        super(aColumnTitle, false);
    }

    //--------------------------------------------------------------------------------
    protected String getColumnValue(Object anObject)
    {
        if (anObject == null) {
            return "null";
        }
        
        return anObject.toString();
    }

    //--------------------------------------------------------------------------------
    protected boolean setColumnValue(Object anObject, String aValue)
    {
        return false;
    }

}

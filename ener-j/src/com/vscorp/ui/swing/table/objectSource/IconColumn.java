// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/IconColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;


/**
 * Icon Column for an ObjectSource-based JTable.
 */
abstract public class IconColumn extends TextColumn
{
    private static final JLabel sRenderer = new JLabel();

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     */
    public IconColumn()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public IconColumn(String aColumnTitle, boolean anEditingFlag)
    {
        super(aColumnTitle, anEditingFlag);
    }

    //----------------------------------------------------------------------
    /**
     * Get the column's icon based on the specified object.
     *
     * @param anObject an Object returned from an ObjectSource, or null to
     * get a default icon for sizing purposes.
     *
     * @return The icon, or null if there is no icon.
     */
    protected abstract Icon getColumnIcon(Object anObject);

    //----------------------------------------------------------------------
    // From TableCellRenderer
    public Component getTableCellRendererComponent(JTable aTable,
                Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        String stringValue = getColumnValueAlways(aValue);
        Icon icon = null;
        if (aValue instanceof String) {
            icon = getColumnIcon(null);
        }
        else {
            icon = getColumnIcon(aValue);
        }

        sRenderer.setIcon(icon);
        sRenderer.setText(stringValue);

        configureRenderer(sRenderer, aTable, aSelectedFlag, aFocusFlag, aRow, aColumn);
        return sRenderer;
    }
}

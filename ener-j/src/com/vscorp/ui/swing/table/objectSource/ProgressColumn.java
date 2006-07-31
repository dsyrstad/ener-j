// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/ProgressColumn.java,v 1.2 2006/05/05 13:47:14 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.JTable;

/**
 * Progress meter Column for an ObjectSource-based JTable.
 */
abstract public class ProgressColumn extends ObjectSourceTableColumn
{
    private static final JProgressBar sRenderer = new JProgressBar();

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     */
    public ProgressColumn()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public ProgressColumn(String aColumnTitle, boolean anEditingFlag)
    {
        super(aColumnTitle, false);
    }

    //----------------------------------------------------------------------
    /**
     * Get the progress value for the cell.
     *
     * @param anObject an object returned by an ObjectSource
     *
     * @return the cell's value as 0..100 (%), or null if there is no value.
     */
    protected abstract Integer getColumnValue(Object anObject);

    //----------------------------------------------------------------------
    /**
     * Set the value for the cell.
     *
     * @param anObject an object returned by an ObjectSource
     * @param aValue a value from 0..100 (%) to be set on the object.
     *
     * @return true if the value was set, or false if the value was not set because
     *  it is invalid.
     */
    protected abstract boolean setColumnValue(Object anObject, Integer aValue);

    //----------------------------------------------------------------------
    /**
     * Calculates the preferred width of the TableColumn using the specified JTable.
     * Modifies the TableColumn's preferredWidth.
     *
     * @param aTable the JTable to be used for font information, etc.
     */
    protected void calculatePreferredWidth(JTable aTable)
    {
        // Allow for 100% plus 100 pixels
        JComponent component = (JComponent)getTableCellRendererComponent(aTable,
                                    new Integer(100), false, false, 0, 0);
        calculatePreferredWidth(aTable, component);
    }

    //----------------------------------------------------------------------
    // From TableCellRenderer
    public Component getTableCellRendererComponent(JTable aTable,
                Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        int percent = 0;
        if (aValue instanceof Integer) {
            percent = ((Integer)aValue).intValue();
        }
        else if (aValue != null) {
            Integer value = getColumnValue(aValue);
            if (value != null) {
                percent = value.intValue();
            }
        }

        configureRenderer(sRenderer, aTable, aSelectedFlag, aFocusFlag, aRow, aColumn);

        // Don't use black which is the default foreground
        sRenderer.setForeground(Color.blue);
        sRenderer.setBackground(aTable.getBackground());
        sRenderer.setBorderPainted(aSelectedFlag);
        if (aSelectedFlag) {
            sRenderer.setBorder(BorderFactory.createLineBorder(aTable.getSelectionBackground()));
        }

        sRenderer.setStringPainted(true);
        sRenderer.setFont(aTable.getFont());
        sRenderer.setOpaque(true);
        sRenderer.setValue(percent);

        return sRenderer;
    }

    //----------------------------------------------------------------------
    // From TableCellEditor...
    public Component getTableCellEditorComponent(JTable aTable, Object aValue,
                        boolean isSelected, int aRow, int aColumn)
    {
        return null;    /**  TODO  not implemented */
    }

    //----------------------------------------------------------------------
    // From CellEditor...
    public boolean isCellEditable(EventObject anEvent)
    {
        return false;    /**  TODO  not implemented, remove when done */
    }

    //----------------------------------------------------------------------
    // From CellEditor...
    public boolean shouldSelectCell(EventObject anEvent)
    {
        return false;    /**  TODO  not implemented, remove when done */
    }

    //----------------------------------------------------------------------
    // From CellEditor...
    public boolean stopCellEditing()
    {
        return true;
    }

}

// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/CheckBoxColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $


package com.vscorp.ui.swing.table.objectSource;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.text.*;


/**
 * CheckBox Column for an ObjectSource-based JTable.
 */
abstract public class CheckBoxColumn extends ObjectSourceTableColumn
{
    /** Common JCheckBox for editing. */
    private JCheckBox mEditorJCheckBox = null;
    private JCheckBox mRendererJCheckBox = null;
    /** The JTable that is currently using this column to edit */
    private JTable mEditingJTable = null;

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     */
    public CheckBoxColumn()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column that may be edited with a JCheckBox.
     * Column is optionally editable.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public CheckBoxColumn(String aColumnTitle, boolean anEditingFlag)
    {
        super(aColumnTitle, anEditingFlag);
    }

    //----------------------------------------------------------------------
    /**
     * Get the value for the cell.
     *
     * @param anObject an object returned by an ObjectSource
     *
     * @return the cell's value, or null if there is no value.
     */
    protected abstract Boolean getColumnValue(Object anObject);

    //----------------------------------------------------------------------
    /**
     * Set the value for the cell.
     *
     * @param anObject an object returned by an ObjectSource
     * @param aValue a value to be set on the object.
     *
     * @return true if the value was set, or false if the value was not set because
     *  it is invalid.
     */
    protected abstract boolean setColumnValue(Object anObject, Boolean aValue);

    //----------------------------------------------------------------------
    /**
     * Get the column's Boolean value from the specified object. A valid Boolean
     * is always returned regarless if anObject or the column's value is null.
     *
     * @param anObject an Object returned from an ObjectSource, or a Boolean
     *
     * @return A Boolean value representing the column
     */
    protected Boolean getColumnValueAlways(Object anObject)
    {
        Boolean value = null;
        if (anObject == null || anObject instanceof Boolean) {
            value = (Boolean)anObject;
        }
        else {
            value = getColumnValue(anObject);
        }

        if (value == null) {
            return Boolean.FALSE;
        }

        return value;
    }

    //----------------------------------------------------------------------
    /**
     * Calculates the preferred width of the TableColumn using the specified JTable.
     * Modifies the TableColumn's preferredWidth.
     *
     * @param aTable the JTable to be used for font information, etc.
     */
    protected void calculatePreferredWidth(JTable aTable)
    {
        JComponent component = (JComponent)getTableCellRendererComponent(aTable,
                                    new Boolean(true), false, false, 0, 0);
        calculatePreferredWidth(aTable, component);
    }

    //----------------------------------------------------------------------
    // From TableCellRenderer
    public Component getTableCellRendererComponent(JTable aTable,
                Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        Boolean value = getColumnValueAlways(aValue);

        if (mRendererJCheckBox == null) {
            mRendererJCheckBox = new JCheckBox();
            mRendererJCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        }

        mRendererJCheckBox.setSelected( value.booleanValue() );
        // Paint the border flat if not editable
        mRendererJCheckBox.setBorderPaintedFlat( !isEditable() );
        //mRendererJCheckBox.setEnabled( isEditable() );

        configureRenderer(mRendererJCheckBox, aTable, aSelectedFlag, aFocusFlag, aRow, aColumn);

        return mRendererJCheckBox;
    }

    //----------------------------------------------------------------------
    // From TableCellEditor...
    public Component getTableCellEditorComponent(JTable aTable, Object aValue,
                        boolean isSelected, int aRow, int aColumn)
    {
        if (mEditorJCheckBox == null) {
            mEditorJCheckBox = new JCheckBox();
            mEditorJCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
            mEditorJCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent anEvent)
                {
                    updateTableWithFieldChange();
                }
            } );
        }

        mEditingJTable = aTable;
        setEditorValue(aValue);
        Boolean value = getColumnValueAlways(aValue);
        mEditorJCheckBox.setSelected( value.booleanValue() );
        mEditorJCheckBox.setOpaque(false);

        configureRenderer(mEditorJCheckBox, aTable, true, true, aRow, aColumn);

        return mEditorJCheckBox;
    }

    //----------------------------------------------------------------------
    /**
     * Called when the editor field changes value. We update the table's
     * object immediately so that we can work around the focus bug 4249803.
     */
    private void updateTableWithFieldChange()
    {
        // Update the column value
        setColumnValue( getEditorValue(), new Boolean( mEditorJCheckBox.isSelected() ) );
        if (mEditingJTable != null) {
            mEditingJTable.removeEditor();
            mEditingJTable = null;
        }
    }

    //----------------------------------------------------------------------
    // From CellEditor...
    public boolean stopCellEditing()
    {
        setColumnValue( getEditorValue(), new Boolean( mEditorJCheckBox.isSelected() ) );
        fireEditingStopped();
        return true;
    }
}

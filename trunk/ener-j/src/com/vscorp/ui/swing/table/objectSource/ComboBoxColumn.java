// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/ComboBoxColumn.java,v 1.2 2006/05/05 13:47:14 dsyrstad Exp $


package com.vscorp.ui.swing.table.objectSource;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.text.*;


/**
 * ComboBox Column for an ObjectSource-based JTable.
 */
abstract public class ComboBoxColumn extends ObjectSourceTableColumn
{
    /** Common JComboBox for editing. */
    private JComboBox mEditorJComboBox = null;

    /** Common JComboBox for rendering. */
    private JComboBox mRendererJComboBox = null;

    private Object[] mValues;
    private DefaultComboBoxModel mEditorComboModel;
    private DefaultComboBoxModel mRendererComboModel;

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     */
    public ComboBoxColumn()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column that may be edited with a JComboBox.
     * Column is optionally editable.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     * @param someValues values for the combo box. The Object's toString method
     *   should return a reasonable display value.
     */
    public ComboBoxColumn(String aColumnTitle, boolean anEditingFlag,
                Object[] someValues)
    {
        super(aColumnTitle, anEditingFlag);
        setValues(someValues);
        setHeaderTextAlignment(HEADER_LEADING);
        if (mRendererJComboBox == null) {
            mRendererJComboBox = new JComboBox();
            /**  TODO  hmmm... put this in a panel with a one pixel empty border so select highlight works? */
        }
    }

    //----------------------------------------------------------------------
    /**
     * Sets the possible values for the combo box.
     *
     * @param someValues values for the combo box. The Object's toString method
     *   should return a reasonable display value.
     */
    public void setValues(Object[] someValues)
    {
        mValues = someValues;
        mEditorComboModel = new DefaultComboBoxModel(mValues);
        mRendererComboModel = new DefaultComboBoxModel(mValues);
    }

    //----------------------------------------------------------------------
    /**
     * Get the value for the cell.
     *
     * @param anObject an object returned by an ObjectSource
     *
     * @return the cell's value, or null if there is no value.
     * The returned object should be of a type compatible
     * with the Object array supplied for the combo box.
     */
    protected abstract Object getColumnValue(Object anObject);

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
    protected abstract boolean setColumnValue(Object anObject, Object aValue);

    //----------------------------------------------------------------------
    /**
     * Calculates the preferred width of the TableColumn using the specified JTable.
     * Modifies the TableColumn's preferredWidth.
     * Based on the width of 5 X's in the table's font
     * (kind of punting here, subclass could override).
     *
     * @param aTable the JTable to be used for font information, etc.
     */
    protected void calculatePreferredWidth(JTable aTable)
    {
        JComboBox combo = new JComboBox(new Object[] { "XX" } );
        combo.setSelectedIndex(0);
        configureRenderer(combo, aTable, true, true, 0, 0);
        calculatePreferredWidth(aTable, combo);
    }

    //----------------------------------------------------------------------
    // From TableCellRenderer
    public Component getTableCellRendererComponent(JTable aTable,
                Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        JComponent component;
        Object columnValue = getColumnValue(aValue);
        if (isEditable()) {
            mRendererJComboBox.setModel(mRendererComboModel);
            mRendererComboModel.setSelectedItem(columnValue);

            // Don't use configureRenderer, it kills the combo box colors
            mRendererJComboBox.setFont( aTable.getFont() );
            mRendererJComboBox.setOpaque(true);
            mRendererJComboBox.setEditable( isEditable() );

            if (aFocusFlag) {
                mRendererJComboBox.setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
            }
            else {
                mRendererJComboBox.setBorder(NO_FOCUS_BORDER);
            }

            component = mRendererJComboBox;
        }
        else {
            TableCellRenderer renderer = aTable.getDefaultRenderer(String.class);
            component = (JComponent)renderer.getTableCellRendererComponent(aTable,
                                    columnValue, aSelectedFlag, aFocusFlag,
                                    aRow, aColumn);
            configureRenderer(component, aTable, aSelectedFlag, aFocusFlag, aRow, aColumn);
        }

        return component;
    }

    //----------------------------------------------------------------------
    // From TableCellEditor...
    public Component getTableCellEditorComponent(JTable aTable, Object aValue,
                        boolean isSelected, int aRow, int aColumn)
    {
        if (mEditorJComboBox == null) {
            mEditorJComboBox = new JComboBox();
            mEditorJComboBox.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent anEvent)
                {
                    stopCellEditing();
                }
            } );
        }

        setEditorValue(aValue);
        Object value = getColumnValue(aValue);
        if (value == null) {
            // Default to empty string if no value
            value = "";
        }

        mEditorJComboBox.setFont( aTable.getFont() );
        mEditorJComboBox.setModel(mEditorComboModel);
        mEditorComboModel.setSelectedItem(value);

        return mEditorJComboBox;
    }

    //----------------------------------------------------------------------
    // From CellEditor...
    public boolean stopCellEditing()
    {
        if (setColumnValue( getEditorValue(),
                            mEditorJComboBox.getSelectedItem() )) {
            fireEditingStopped();
            return true;
        }

        return false;
    }

}

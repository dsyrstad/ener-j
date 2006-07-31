// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/TextColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;

import com.vscorp.ui.swing.TextChooserField;

/**
 * Text Column for an ObjectSource-based JTable.
 * <p>
 */
abstract public class TextColumn extends ObjectSourceTableColumn
{
    private TextChooserField mRendererTextField = new TextChooserField("", "", null);
    private TextChooserField mEditorTextField = new TextChooserField("", "", null);
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
    public TextColumn()
    {
        this("", true);
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable.
     * Heading alignment is leading.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public TextColumn(String aColumnTitle, boolean anEditingFlag)
    {
        super(aColumnTitle, anEditingFlag);
        setHeaderTextAlignment(HEADER_LEADING);
        mEditorTextField.getTextField().setEditable(false);
        mEditorTextField.addChangeListener( new ChangeListener() {
            public void stateChanged(ChangeEvent aChangeEvent) {
                updateTableWithFieldChange(aChangeEvent);
            }
        } );
    }

    //----------------------------------------------------------------------
    /**
     * Gets the title for the editor dialog box. 
     *
     * @return the dialog's title or null if not set.
     */
    public String getTitle() 
    {
        return mEditorTextField.getTitle();
    }

    //----------------------------------------------------------------------
    /**
     * Sets the title for the editor's dialog box. If not set, this will default
     * to the title of the owner Frame/Dialog.
     *
     * @param aTitle the dialog's title.
     */
    public void setTitle(String aTitle) 
    {
        mEditorTextField.setTitle(aTitle);
    }

    //----------------------------------------------------------------------
    /**
     * Get the column's string value from the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     *
     * @return The value, or null if the value was not found.
     */
    protected abstract String getColumnValue(Object anObject);

    //----------------------------------------------------------------------
    /**
     * Set the column's string value on the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     * @param aValue the string value to be set.
     *
     * @return true if the value was set, or false if the value was not set because
     *  it is invalid.
     */
    protected abstract boolean setColumnValue(Object anObject, String aValue);

    //----------------------------------------------------------------------
    /**
     * Get the column's string value from the specified object. A valid string
     * is always returned regarless if anObject or the column's value is null.
     *
     * @param anObject an Object returned from an ObjectSource, or a String
     *
     * @return A string value representing the column
     */
    protected String getColumnValueAlways(Object anObject)
    {
        String stringValue = null;

        if (anObject != null) {
            if (anObject instanceof String) {
                stringValue = (String)anObject;
            }
            else {
                stringValue = getColumnValue(anObject);
            }
        }

        if (stringValue == null) {
            stringValue = "";
        }

        return stringValue;
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
        // Default to 20 X's in the table's font.
        JComponent component = (JComponent)getTableCellRendererComponent(aTable,
                                    "XXXXXXXXXXXXXXXXXXXX", false, false, 0, 0);
        calculatePreferredWidth(aTable, component);
    }

    //----------------------------------------------------------------------
    // From TableCellRenderer
    public Component getTableCellRendererComponent(JTable aTable,
                Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        String stringValue = getColumnValueAlways(aValue);
        JComponent component;
        if (isEditable()) {
            mRendererTextField.setText(stringValue);
            component = mRendererTextField;
        }
        else {
            TableCellRenderer renderer = aTable.getDefaultRenderer(String.class);
            component = (JComponent)renderer.getTableCellRendererComponent(aTable,
                                    stringValue, aSelectedFlag, aFocusFlag,
                                    aRow, aColumn);
        }

        configureRenderer(component, aTable, aSelectedFlag, aFocusFlag, aRow, aColumn);

        return component;
    }

    //----------------------------------------------------------------------
    // From TableCellEditor...
    public Component getTableCellEditorComponent(JTable aTable, Object aValue,
                        boolean isSelected, int aRow, int aColumn)
    {
        mEditingJTable = aTable;
        setEditorValue(aValue);
        String stringValue = getColumnValueAlways(aValue);
        mEditorTextField.setText(stringValue);
        mEditorTextField.setFont( aTable.getFont() );
        return mEditorTextField;
    }

    //----------------------------------------------------------------------
    /**
     * Called when the editor field changes value. We update the table's
     * object immediately so that we can work around the focus bug 4249803.
     *
     * @param anEvent the ChangeEvent.
     */
    private void updateTableWithFieldChange(ChangeEvent anEvent)
    {
        String text = mEditorTextField.getText();
        // Update the column value
        setColumnValue( getEditorValue(), text);
        if (mEditingJTable != null) {
            mEditingJTable.removeEditor();
            mEditingJTable = null;
        }
    }

    //----------------------------------------------------------------------
    // From CellEditor...
    public boolean stopCellEditing()
    {
        String stringValue = mEditorTextField.getText();
        if (setColumnValue( getEditorValue(), stringValue )) {
            fireEditingStopped();
            return true;
        }

        return false;
    }

}

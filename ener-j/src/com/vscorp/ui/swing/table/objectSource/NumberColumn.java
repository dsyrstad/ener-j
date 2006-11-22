// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/NumberColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.Component;
import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.vscorp.ui.swing.TextChooserField;

/**
 * Number Column for an ObjectSource-based JTable. Represents a formatted Number column for an ObjectSource.
 * <p>
 */

abstract public class NumberColumn extends ObjectSourceTableColumn
{
    private static NumberFormat sDefaultNumberFormat = null;

    private JLabel mRendererTextLabel = new JLabel();
    private TextChooserField mRendererTextField = new TextChooserField("", "", "");
    private TextChooserField mEditorTextField = new TextChooserField("", "", "");
    /** The JTable that is currently using this column to edit */
    private JTable mEditingJTable = null;
    private NumberFormat mNumberFormat;


    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     */
    public NumberColumn()
    {
        this("", true);
    }


    /**
     * Construct a new column. Column is optionally editable. Default integer
     * format is used.
     * Heading alignment is trailing.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public NumberColumn(String aColumnTitle, boolean anEditingFlag)
    {
        this(aColumnTitle, anEditingFlag, null);
    }


    /**
     * Construct a new column. Column is optionally editable. Default integer
     * format is used.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     * @param aNumberFormat a NumberFormat to use for formatting. May be null
     *  to use the default integer format.
     */
    public NumberColumn(String aColumnTitle, boolean anEditingFlag,
            NumberFormat aNumberFormat)
    {
        super(aColumnTitle, anEditingFlag);
        setHeaderTextAlignment(HEADER_TRAILING);
        if (aNumberFormat == null) {
            if (sDefaultNumberFormat == null) {
                sDefaultNumberFormat = NumberFormat.getNumberInstance();
                sDefaultNumberFormat.setMaximumFractionDigits(0);
                sDefaultNumberFormat.setMinimumFractionDigits(0);
            }

            mNumberFormat = sDefaultNumberFormat;
        }
        else {
            mNumberFormat = aNumberFormat;
        }

        mRendererTextField.getTextField().setHorizontalAlignment(SwingConstants.TRAILING);
        mEditorTextField.getTextField().setHorizontalAlignment(SwingConstants.TRAILING);
        mEditorTextField.getDialogTextField().setHorizontalAlignment(SwingConstants.TRAILING);
        mEditorTextField.getTextField().setEditable(false);
        mEditorTextField.addChangeListener( new ChangeListener() {
            public void stateChanged(ChangeEvent aChangeEvent) {
                updateTableWithFieldChange(aChangeEvent);
            }
        } );
    }


    /**
     * Gets the NumberFormat set on this column.
     */
    public NumberFormat getNumberFormat()
    {
        return mNumberFormat;
    }


    /**
     * Get the column's value from the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     *
     * @return The value, or null if the value was not found.
     */
    protected abstract Number getColumnValue(Object anObject);


    /**
     * Set the column's value on the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     * @param aValue the value to be set. Must be the same type of Number as returned
     *  from getColumnValue.
     *
     * @return true if the value was set, or false if the value was not set because
     *  it is invalid.
     */
    protected abstract boolean setColumnValue(Object anObject, Number aValue);


    /**
     * Calculates the preferred width of the TableColumn using the specified JTable.
     * Modifies the TableColumn's preferredWidth.
     *
     * @param aTable the JTable to be used for font information, etc.
     */
    protected void calculatePreferredWidth(JTable aTable)
    {
        // Default to 999,999,999 with the NumberFormat in the table's font.
        JComponent component = (JComponent)getTableCellRendererComponent(aTable,
                                    new Integer(999999999), false, false, 0, 0);
        calculatePreferredWidth(aTable, component);
    }


    // From TableCellRenderer
    public Component getTableCellRendererComponent(JTable aTable,
                Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        Number number = null;

        if (aValue != null) {
            if (aValue instanceof Number) {
                number = (Number)aValue;
            }
            else {
                number = getColumnValue(aValue);
            }
        }

        String stringValue = null;
        if (number == null) {
            stringValue = "";
        }
        else {
            stringValue = mNumberFormat.format(number);
        }

        JComponent component;
        if (isEditable()) {
            mRendererTextField.setText(stringValue);
            component = mRendererTextField;
        }
        else {
            mRendererTextLabel.setText(stringValue);
            mRendererTextLabel.setHorizontalAlignment(SwingConstants.TRAILING);
            component = mRendererTextLabel;
        }

        configureRenderer(component, aTable, aSelectedFlag, aFocusFlag, aRow, aColumn);

        return component;
    }


    // From TableCellEditor...
    public Component getTableCellEditorComponent(JTable aTable, Object aValue,
                        boolean isSelected, int aRow, int aColumn)
    {
        mEditingJTable = aTable;
        setEditorValue(aValue);
        Number number = getColumnValue(aValue);
        if (number == null) {
            number = new Integer(0);
        }

        String stringValue = mNumberFormat.format(number);

        mEditorTextField.setText(stringValue);
        mEditorTextField.setFont( aTable.getFont() );
        return mEditorTextField;
    }


    /**
     * Sets the column value from the editor's value.
     *
     * @return true if the column could be set, else false;
     */
    protected boolean setColumnFromEditor()
    {
        Object editorValue = getEditorValue();
        if (editorValue == null) {
            return true;
        }

        String stringValue = mEditorTextField.getText();
        Number number = null;
        try {
            number = mNumberFormat.parse(stringValue);
        }
        catch (ParseException e) {
            Toolkit.getDefaultToolkit().beep();
            return false;
        }

        // Parse returns either a Long or a Double. Convert it to the correct
        // Number type if necessary.
        Number prevNumber = getColumnValue(editorValue);
        if (prevNumber instanceof Integer && !(number instanceof Integer)) {
            number = new Integer( number.intValue() );
        }
        else if (prevNumber instanceof Short && !(number instanceof Short)) {
            number = new Short( number.shortValue() );
        }
        else if (prevNumber instanceof Byte && !(number instanceof Byte)) {
            number = new Byte( number.byteValue() );
        }
        else if (prevNumber instanceof Long && !(number instanceof Long)) {
            number = new Long( number.longValue() );
        }
        else if (prevNumber instanceof Float && !(number instanceof Float)) {
            number = new Float( number.floatValue() );
        }
        else if (prevNumber instanceof Double && !(number instanceof Double)) {
            number = new Double( number.doubleValue() );
        }
        // else number is already the correct type, or prevNumber is null.

        // Update the column value
        return setColumnValue( getEditorValue(), number);
    }


    /**
     * Called when the editor field changes value. We update the table's
     * object immediately so that we can work around the focus bug 4249803.
     *
     * @param anEvent the ChangeEvent.
     */
    private void updateTableWithFieldChange(ChangeEvent anEvent)
    {
        setColumnFromEditor();
        if (mEditingJTable != null) {
            mEditingJTable.removeEditor();
            mEditingJTable = null;
        }
    }


    // From CellEditor...
    public boolean stopCellEditing()
    {
        boolean returnValue = setColumnFromEditor();
        fireEditingStopped();
        return returnValue;
    }

}

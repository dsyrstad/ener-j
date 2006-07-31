// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/DateTimeColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;

import com.vscorp.ui.swing.DateChooser;

/**
 * Date/Time Column for an ObjectSource-based JTable.
 */
abstract public class DateTimeColumn extends ObjectSourceTableColumn
{
    /** The default date format - date and time in the default locale */
    private static final SimpleDateFormat sDefaultDateTimeFormat = new SimpleDateFormat();

    /** Common JTextField for editing. */
    private DateChooser mEditorDateChooser = null;
    /** Common JTextField for rendering. */
    private DateChooser mRendererDateChooser = null;

    private DateFormat mDateTimeFormat;

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     */
    public DateTimeColumn()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column with the default date/time format. The default
     * format is one created by new SimpleDateFormat().
     * Column is optionally editable.
     * Heading alignment is leading.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public DateTimeColumn(String aColumnTitle, boolean anEditingFlag)
    {
        this(aColumnTitle, anEditingFlag, null);
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable.
     * Heading alignment is leading.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     * @param aDateTimeFormat the date/time format used for displaying and editing.
     *  If this is null, a default format is used (from new SimpleDateFormat()).
     */
    public DateTimeColumn(String aColumnTitle, boolean anEditingFlag, DateFormat aDateTimeFormat)
    {
        super(aColumnTitle, anEditingFlag);
        setHeaderTextAlignment(HEADER_LEADING);
        if (aDateTimeFormat == null) {
            aDateTimeFormat = sDefaultDateTimeFormat;
        }

        mDateTimeFormat = aDateTimeFormat;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the date/time format used to format and parse the field contents.
     *
     * @return a DateFormat
     */
    public DateFormat getDateFormat()
    {
        return mDateTimeFormat;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the date/time format used to format and parse the field contents.
     *
     * @param aDateTimeFormat a DateFormat
     */
    public void setDateFormat(DateFormat aDateTimeFormat)
    {
        mDateTimeFormat = aDateTimeFormat;
    }

    //----------------------------------------------------------------------
    /**
     * Get the Date/Time value for the cell.
     *
     * @param anObject an object returned by an ObjectSource
     *
     * @return the cell's value, or null if there is no value.
     */
    protected abstract Date getColumnValue(Object anObject);

    //----------------------------------------------------------------------
    /**
     * Set the Date/Time value for the cell.
     *
     * @param anObject an object returned by an ObjectSource
     * @param aDate a date to be set on the object.
     *
     * @return true if the value was set, or false if the value was not set because
     *  it is invalid.
     */
    protected abstract boolean setColumnValue(Object anObject, Date aDate);

    //----------------------------------------------------------------------
    /**
     * Format the date and time attributes of a java.util.Date based on locale.
     *
     * @param aDate a Date to be formatted
     *
     * @return a String representing the formatted date and time.
     */
    protected String formatDateTime(Date aDate)
    {
        if (aDate == null || aDate.getTime() == 0) {
            return "";
        }

        return mDateTimeFormat.format(aDate);
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
        // Get the size of "2000/10/28 23:59:59 PM" which is probably the largest
        // width date in a proportional font.
        Date date = new GregorianCalendar(2000, 9, 28, 23, 59, 59).getTime();
        String dateString = formatDateTime(date);
        // "XXX" is for the button width. Hack.
        if (isEditable()) {
            dateString += "XXX";
        }

        JLabel label = new JLabel(dateString);
        configureRenderer(label, aTable, true, true, 0, 0);
        calculatePreferredWidth(aTable, label);
    }

    //----------------------------------------------------------------------
    // From TableCellRenderer
    public Component getTableCellRendererComponent(JTable aTable,
                Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        Date value = getColumnValue(aValue);
        Component component;

        if (isEditable()) {
            if (mRendererDateChooser == null) {
                mRendererDateChooser = new DateChooser();
            }

            mRendererDateChooser.setDateFormat(mDateTimeFormat);
            if (value == null) {
                mRendererDateChooser.setCalendar(null);
            }
            else {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(value);
                mRendererDateChooser.setCalendar(calendar);
            }

            component = mRendererDateChooser;
        }
        else {
            TableCellRenderer renderer = aTable.getDefaultRenderer(String.class);
            String stringValue = formatDateTime(value);
            component = renderer.getTableCellRendererComponent(aTable,
                                        stringValue, aSelectedFlag, aFocusFlag,
                                        aRow, aColumn);
        }

        configureRenderer((JComponent)component, aTable, aSelectedFlag,
                aFocusFlag, aRow, aColumn);
        return component;
    }

    //----------------------------------------------------------------------
    // From TableCellEditor...
    public Component getTableCellEditorComponent(JTable aTable, Object aValue,
                        boolean isSelected, int aRow, int aColumn)
    {
        setEditorValue(null);
        if (mEditorDateChooser == null) {
            mEditorDateChooser = new DateChooser();
            mEditorDateChooser.addChangeListener( new ChangeListener() {
                public void stateChanged(ChangeEvent anEvent) {
                    stopCellEditing();
                }
            } );
        }


        mEditorDateChooser.setDateFormat(mDateTimeFormat);
        Date dateValue = getColumnValue(aValue);
        if (dateValue == null) {
            mEditorDateChooser.setCalendar(null);
        }
        else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(dateValue);
            mEditorDateChooser.setCalendar(calendar);
        }

        mEditorDateChooser.setFont( aTable.getFont() );
        setEditorValue(aValue);
        return mEditorDateChooser;
    }

    //----------------------------------------------------------------------
    // From CellEditor...
    public boolean stopCellEditing()
    {
        Object editorValue = getEditorValue();
        if (editorValue == null) {
            return true;
        }

        if (setColumnValue(editorValue, mEditorDateChooser.getCalendar().getTime() )) {
            fireEditingStopped();
            setEditorValue(null);
            return true;
        }

        return false;
    }
}

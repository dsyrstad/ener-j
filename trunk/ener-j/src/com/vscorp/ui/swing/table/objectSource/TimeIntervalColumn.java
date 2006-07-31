// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/TimeIntervalColumn.java,v 1.2 2006/05/05 13:47:14 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Time interval column for an ObjectSource-based JTable.
 * <p>
 */
abstract public class TimeIntervalColumn extends ObjectSourceTableColumn
{
    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     */
    public TimeIntervalColumn()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable.
     * Heading alignment is trailing.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public TimeIntervalColumn(String aColumnTitle, boolean anEditingFlag)
    {
        super(aColumnTitle, false);
        setHeaderTextAlignment(HEADER_TRAILING);
    }

    //----------------------------------------------------------------------
    /**
     * Get the interval value for the cell.
     *
     * @param anObject an object returned by an ObjectSource
     *
     * @return the cell's value in milliseconds, or null if there is no value.
     */
    protected abstract Long getColumnValue(Object anObject);

    //----------------------------------------------------------------------
    /**
     * Set the interval value for the cell.
     *
     * @param anObject an object returned by an ObjectSource
     * @param aValue a value in milliseconds to be set on the object.
     *
     * @return true if the value was set, or false if the value was not set because
     *  it is invalid.
     */
    protected abstract boolean setColumnValue(Object anObject, Long aValue);

    //----------------------------------------------------------------------
    /**
     * Get the column's string value from the specified object. A valid string
     * is always returned regarless if anObject or the column's value is null.
     *
     * @param anObject an Object returned from an ObjectSource, or a Date
     *
     * @return A String value representing the column
     */
    protected String getColumnValueAlways(Object anObject)
    {
        Long intervalValue = null;

        if (anObject != null) {
            if (anObject instanceof Long) {
                intervalValue = (Long)anObject;
            }
            else {
                intervalValue = getColumnValue(anObject);
            }
        }

        if (intervalValue == null) {
            return "";
        }

        return formatInterval(intervalValue);
    }

    //----------------------------------------------------------------------
    /**
     * Format an interval time in milliseconds. Result is HHH:MM:SS.
     *
     * @param aTime interval time in milliseconds.
     *
     * @return a String with the displayable interval
     */
    protected String formatInterval(Long aTime)
    {
        if (aTime == null) {
            return "";
        }

        long millis = aTime.longValue();

        // NOTE: Don't want to use SimpleDateFormat here because it is an _interval_
        // time, not a clock time. Otherwise, the timezone and implied date may adjust the setting.
        millis = (millis + 500) / 1000; // Round to seconds
        int seconds = (int)(millis % 60);
        millis /= 60;
        int minutes = (int)(millis % 60);
        int hours = (int)(millis / 60);

        StringBuffer buf = new StringBuffer();
        buf.append(hours);
        buf.append(':');
        if (minutes < 10)
            buf.append('0');

        buf.append(minutes);
        buf.append(':');
        if (seconds < 10)
            buf.append('0');

        buf.append(seconds);
        return buf.toString();
    }

    //----------------------------------------------------------------------
    /**
     * Parse a string in interval time format (HHH:MM:SS).
     *
     * @param aString a interval time string.
     *
     * @return interval time in milliseconds, returned as a Long. Returns null
     *  if string is not parseable.
     */
    protected Long parseInterval(String aString)
    {
        int firstColonIdx = aString.indexOf(':');
        int secondColonIdx = aString.indexOf(':', firstColonIdx + 1);

        // If we didn't have a first or second colon, or we had a third colon.
        if (firstColonIdx == -1 || secondColonIdx == -1 ||
            aString.indexOf(':', secondColonIdx + 1) != -1) {
            return null;
        }

        String hhhString = aString.substring(0, firstColonIdx);
        String mmString = aString.substring(firstColonIdx + 1, secondColonIdx);
        String ssString = aString.substring(secondColonIdx + 1);
        try {
            long result =
                Long.parseLong(hhhString) * (60 * 60 * 1000L) +
                Long.parseLong(mmString) * (60 * 1000L) +
                Long.parseLong(ssString) * 1000L;
            return new Long(result);
        }
        catch (NumberFormatException e) {
            return null;
        }
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
        // Allow for 100 hours
        JComponent component = (JComponent)getTableCellRendererComponent(aTable,
                                    new Long(100 * 3600000), false, false, 0, 0);
        calculatePreferredWidth(aTable, component);
    }

    //----------------------------------------------------------------------
    // From TableCellRenderer
    public Component getTableCellRendererComponent(JTable aTable,
                Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        String stringValue = getColumnValueAlways(aValue);
        TableCellRenderer renderer = aTable.getDefaultRenderer(Integer.class);
        Component component = renderer.getTableCellRendererComponent(aTable,
                                    stringValue, aSelectedFlag, aFocusFlag,
                                    aRow, aColumn);

        configureRenderer((JComponent)component, aTable, aSelectedFlag,
                aFocusFlag, aRow, aColumn);
        return component;
    }

    //----------------------------------------------------------------------
    // From TableCellEditor...
    public Component getTableCellEditorComponent(JTable aTable, Object aValue,
                        boolean isSelected, int aRow, int aColumn)
    {
        /*
        setEditorValue(aValue);
        Long intervalValue = getColumnValue(aValue);
        */
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
        /*
        if (setColumnValue( getEditorValue(),
                            sEditorDateChooser.getCalendar().getTime() )) {
            fireEditingStopped();
            return true;
        }

        return false;
        */
    }

}

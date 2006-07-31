// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/ReflectedDateTimeColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.AWTError;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a date/time column for an ObjectSource.
 * The column's value is derived via
 * reflection from the object's attribute name. Reflection is used to eliminate
 * the need for a sub-class for each field of the object.
 * <p>
 */
public class ReflectedDateTimeColumn extends DateTimeColumn
{
    /** The reflected field to use as a value */
    private Field mField;

    //----------------------------------------------------------------------
    /**
     * Construct a new column configured by the specified arguments. This
     * constructor form is common to all of the "reflected" column types and is
     * here to support the EditableSortableTableColumn.createColumn factory
     * method.
     * The column is editable. A single click is
     * required to start editing. Column is sortable, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     * @param anObjectClass the class of the object returned from the ObjectSource
     * @param anAttributeName a String representing the name of the object's attribute.
     *  The attribute must be either a Date or an object that can be converted to a date String.
     * @param someArgs an array of Strings used to configure this specific column.
     *  It may be null or empty to use the default settings.
     *  The arguments used for this specific column are:<p>
     *  someArgs[0] - the date format string, as specified by java.text.SimpleDateFormat.
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedDateTimeColumn(Class anObjectClass, String anAttributeName, String[] someArgs)
    {
        this("", true,
            (someArgs == null || someArgs.length == 0) ? null : new SimpleDateFormat(someArgs[0]),
            anObjectClass, anAttributeName);
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string. This method
     * fails fast by throwing an AWTError if anAttributeName does not exist.
     * This usually happens due to a programming error, and shouldn't normally
     * happen at runtime.
     *
     * @param anObjectClass the class of the object returned from the ObjectSource
     * @param anAttributeName a String representing the name of the object's attribute.
     *  The attribute must be either a Date or an object that can be converted to a date String.
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedDateTimeColumn(Class anObjectClass, String anAttributeName)
    {
        this("", true, anObjectClass, anAttributeName);
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable. This method
     * fails fast by throwing an AWTError if anAttributeName does not exist.
     * This usually happens due to a programming error, and shouldn't normally
     * happen at runtime.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     * @param anObjectClass the class of the object returned from the ObjectSource
     * @param anAttributeName a String representing the name of the object's attribute.
     *  The attribute must be either a Date or an object that can be converted to a date String.
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedDateTimeColumn(String aColumnTitle, boolean anEditingFlag,
                Class anObjectClass, String anAttributeName)
    {
        this(aColumnTitle, anEditingFlag, null, anObjectClass, anAttributeName);
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable. This method
     * fails fast by throwing an AWTError if anAttributeName does not exist.
     * This usually happens due to a programming error, and shouldn't normally
     * happen at runtime.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     * @param aDateTimeFormat the date/time format used for displaying and editing.
     *  If this is null, a default format is used (from new SimpleDateFormat()).
     * @param anObjectClass the class of the object returned from the ObjectSource
     * @param anAttributeName a String representing the name of the object's attribute.
     *  The attribute must be either a Date or an object that can be converted to a date String.
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedDateTimeColumn(String aColumnTitle, boolean anEditingFlag,
                DateFormat aDateTimeFormat, Class anObjectClass, String anAttributeName)
    {
        super(aColumnTitle, anEditingFlag, aDateTimeFormat);
        setFieldName(anAttributeName);
        try {
            mField = anObjectClass.getDeclaredField(anAttributeName);
            // So we can get private fields...
            mField.setAccessible(true);
        }
        catch (Exception e) {
            throw new AWTError( e.toString() );
        }
    }

    //----------------------------------------------------------------------
    /**
     * Get the column's value from the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     *
     * @return The value, or null if the value was not found.
     */
    protected Date getColumnValue(Object anObject)
    {
        try {
            Object obj = mField.get(anObject);
            if (obj == null || obj instanceof Date) {
                return (Date)obj;
            }

            // Assume it's an object whose String can be converted to a Date.
            String dateString = obj.toString();
            try {
                return getDateFormat().parse(dateString);
            }
            catch (ParseException e) {
                return null;
            }
        }
        catch (Exception e) {
            throw new AWTError("Cannot convert field " + mField.getName() +
                " into a Date: " + e);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Set the column's value on the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     * @param aValue the value to be set.
     *
     * @return true if the value was set, or false if the value was not set because
     *  it is invalid.
     */
    protected boolean setColumnValue(Object anObject, Date aValue)
    {
        try {

            if (mField.getType() == String.class) {
                mField.set(anObject, formatDateTime(aValue) );
            }
            else {
                // Assume it's a Date object
                mField.set(anObject, aValue);
            }
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }
}

// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/ReflectedNumberColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.AWTError;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Represents a formatted Number column for an ObjectSource.
 * The column's value is derived via
 * reflection from the object's attribute name. Reflection is used to eliminate
 * the need for a sub-class for each field of the object.
 * <p>
 */
public class ReflectedNumberColumn extends NumberColumn
{
    /** The reflected field to use as a value */
    private Field mField;


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
     * @param anAttributeName a String representing the name of the object's attribute
     * @param someArgs an array of Strings used to configure this specific column.
     *  It may be null or empty to use the default settings.
     *  The arguments used for this specific column are:<p>
     *  someArgs[0] - a format string defined by java.text.DecimalFormat
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedNumberColumn(Class anObjectClass, String anAttributeName, String[] someArgs)
    {
        // I have to use a trinary condition here because "this()" must be the
        // first statement in the constructor.
        this("", true,
            (someArgs == null || someArgs.length == 0) ? null : new DecimalFormat(someArgs[0]),
            anObjectClass, anAttributeName);
    }


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
     * @param anAttributeName a String representing the name of the object's attribute
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedNumberColumn(Class anObjectClass, String anAttributeName)
    {
        this("", true, anObjectClass, anAttributeName);
    }


    /**
     * Construct a new column. Column is optionally editable. This method
     * fails fast by throwing an AWTError if anAttributeName does not exist.
     * This usually happens due to a programming error, and shouldn't normally
     * happen at runtime.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     * @param anObjectClass the class of the object returned from the ObjectSource
     * @param anAttributeName a String representing the name of the object's attribute
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedNumberColumn(String aColumnTitle, boolean anEditingFlag,
                Class anObjectClass, String anAttributeName)
    {
        this(aColumnTitle, anEditingFlag, null, anObjectClass, anAttributeName);
    }


    /**
     * Construct a new column. Column is optionally editable. This method
     * fails fast by throwing an AWTError if anAttributeName does not exist.
     * This usually happens due to a programming error, and shouldn't normally
     * happen at runtime.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     * @param aNumberFormat a NumberFormat to use for formatting. May be null
     *  to use the default integer format.
     * @param anObjectClass the class of the object returned from the ObjectSource
     * @param anAttributeName a String representing the name of the object's attribute
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedNumberColumn(String aColumnTitle, boolean anEditingFlag,
                NumberFormat aNumberFormat, Class anObjectClass, String anAttributeName)
    {
        super(aColumnTitle, anEditingFlag, aNumberFormat);
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


    /**
     * Get the column's value from the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     *
     * @return The value, or null if the value was not found.
     */
    protected Number getColumnValue(Object anObject)
    {
        try {
            Object obj = mField.get(anObject);
            if (obj == null || obj instanceof Number) {
                return (Number)obj;
            }

            // Assume it's an object whose String can be converted to a Number
            try {
                return getNumberFormat().parse( obj.toString() );
            }
            catch (ParseException e) {
                return null;
            }
        }
        catch (Exception e) {
            throw new AWTError("Cannot convert field " + mField.getName() +
                " into a Number: " + e);
        }
    }


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
    protected boolean setColumnValue(Object anObject, Number aValue)
    {
        try {
            Class typeClass = mField.getType();
            if (typeClass == Integer.TYPE) {
                mField.setInt(anObject, aValue.intValue() );
            }
            else if (typeClass == Short.TYPE) {
                mField.setShort(anObject, aValue.shortValue() );
            }
            else if (typeClass == Byte.TYPE) {
                mField.setByte(anObject, aValue.byteValue() );
            }
            else if (typeClass == Long.TYPE) {
                mField.setLong(anObject, aValue.longValue() );
            }
            else if (typeClass == Float.TYPE) {
                mField.setFloat(anObject, aValue.floatValue() );
            }
            else if (typeClass == Double.TYPE) {
                mField.setDouble(anObject, aValue.doubleValue() );
            }
            else if (typeClass == String.class) {
                mField.set(anObject, aValue.toString() );
            }
            else if (Integer.class.isAssignableFrom(typeClass) && !(aValue instanceof Integer)) {
                mField.set(anObject, new Integer( aValue.intValue() ) );
            }
            else if (Short.class.isAssignableFrom(typeClass) && !(aValue instanceof Short)) {
                mField.set(anObject, new Short( aValue.shortValue() ) );
            }
            else if (Byte.class.isAssignableFrom(typeClass) && !(aValue instanceof Byte)) {
                mField.set(anObject, new Byte( aValue.byteValue() ) );
            }
            else if (Long.class.isAssignableFrom(typeClass) && !(aValue instanceof Long)) {
                mField.set(anObject, new Long( aValue.longValue() ) );
            }
            else if (Float.class.isAssignableFrom(typeClass) && !(aValue instanceof Float)) {
                mField.set(anObject, new Float( aValue.floatValue() ) );
            }
            else if (Double.class.isAssignableFrom(typeClass) && !(aValue instanceof Double)) {
                mField.set(anObject, new Double( aValue.doubleValue() ) );
            }
            else {
                // Assume it's a Number object of the correct type
                mField.set(anObject, aValue);
            }
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }
}

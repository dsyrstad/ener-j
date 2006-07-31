// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/ReflectedComboBoxColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.*;
import java.lang.reflect.*;

/**
 * Represents a combo box (enumerated) column for an ObjectSource.
 * The column's value is derived via
 * reflection from the object's attribute name. Reflection is used to eliminate
 * the need for a sub-class for each field of the object.
 * <p>
 */
public class ReflectedComboBoxColumn extends ComboBoxColumn
{
    /** The reflected field to use as a value */
    private Field mField;
    /** This will be null if internal/mapped values are not used. */
    private Object[] mDisplayValues = null;
    /** This will be null if internal/mapped values are not used. */
    private Object[] mInternalValues = null;

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
     * @param anAttributeName a String representing the name of the object's attribute
     * @param someArgs an array of Strings used to configure this specific column.
     *  It may be null or empty to use the default settings.
     *  The arguments used for this specific column are:<p>
     *  someArgs[0..n] - the values for the combo box; or alternately if someArgs[0] 
     *  is the string "@MAPPED", arguments at odd indexes are display values and
     *  arguments at even indexes are internal values. For example:<br>
     *  @MAPPED, In Progress, _kInProgress, Released, _kReleased, Unreleased, _kUnreleased,
     *  Held, _kHeld<p>
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedComboBoxColumn(Class anObjectClass, String anAttributeName, String[] someArgs)
    {
        this("", true, new Object[0], anObjectClass, anAttributeName);
        convertArgs(someArgs);
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
     * @param anAttributeName a String representing the name of the object's attribute
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedComboBoxColumn(Class anObjectClass, String anAttributeName)
    {
        this("", true, new String[0], anObjectClass, anAttributeName);
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
     * @param someValues values for the combo box. The Object's toString method
     *   should return a reasonable display value.
     * @param anObjectClass the class of the object returned from the ObjectSource
     * @param anAttributeName a String representing the name of the object's attribute
     *
     * @throws AWTError (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public ReflectedComboBoxColumn(String aColumnTitle, boolean anEditingFlag,
                Object[] someValues, Class anObjectClass, String anAttributeName)
    {
        super(aColumnTitle, anEditingFlag, someValues);
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
     * Convert the combo box values into appropriate object types for the attribute.
     */
    private void convertArgs(String[] someArgs)
    {
        if (someArgs == null || someArgs.length == 0) {
            setValues(new Object[0]);
            return;
        }

        Class fieldClass = mField.getType();
        if (someArgs[0].equals("@MAPPED")) {
            // Number of arguments should be ODD (includes @MAPPED)
            if (someArgs.length % 2 != 1) {
                throw new AWTError("@MAPPED number of arguments is not even");
            }
            
            int numValues = (someArgs.length - 1) / 2;
            mDisplayValues = new Object[numValues];
            mInternalValues = new Object[numValues];

            int valuesIdx = 0;
            for (int i = 1; i < someArgs.length; i++, valuesIdx++) {
                mDisplayValues[valuesIdx] = convertArg(someArgs[i], fieldClass);
                ++i;
                mInternalValues[valuesIdx] = convertArg(someArgs[i], fieldClass);
            }

            setValues(mDisplayValues);
        }
        else {
            Object[] newArgs = new Object[ someArgs.length ];

            for (int i = 0; i < someArgs.length; i++) {
                newArgs[i] = convertArg(someArgs[i], fieldClass);
            }

            setValues(newArgs);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Helper for convertArgs to convert a single argument.
     *
     * @param anArg the String argument.
     * @param aFieldClass the class type of the field.
     *
     * @return the converted argument, suitable for use in setValues().
     */
    private Object convertArg(String anArg, Class aFieldClass)
    {
        Object result = anArg;
        try {
            // Convert the value from a String to a limited range of appropriate types.
            if (String.class.isAssignableFrom(aFieldClass) ) {
                // Field is a String - no conversion needed
            }
            else if(Integer.class.isAssignableFrom(aFieldClass) ||
                Integer.TYPE.isAssignableFrom(aFieldClass)) {
                result = new Integer(anArg);
            }
            else if (Short.class.isAssignableFrom(aFieldClass) ||
                     Short.TYPE.isAssignableFrom(aFieldClass)) {
                result = new Short(anArg);
            }
            else if (Long.class.isAssignableFrom(aFieldClass) ||
                     Long.TYPE.isAssignableFrom(aFieldClass)) {
                result = new Long(anArg);
            }
            else if (Double.class.isAssignableFrom(aFieldClass) ||
                     Double.TYPE.isAssignableFrom(aFieldClass)) {
                result = new Double(anArg);
            }
        }
        catch (NumberFormatException e) {
            // Ignore - don't convert
        }
        
        return result;
    }
    
    //----------------------------------------------------------------------
    /**
     * Get the column's value from the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     *
     * @return The value, or null if the value was not found.
     */
    protected Object getColumnValue(Object anObject)
    {
        Object value;
        try {
            value = mField.get(anObject);
        }
        catch (Exception e) {
            throw new AWTError("Cannot convert field " + mField.getName() +
                " into a object: " + e);
        }
        
        // Map value if necessary
        if (mInternalValues != null) {
            for (int i = 0; i < mInternalValues.length; i++) {
                if (mInternalValues[i].equals(value)) {
                    return mDisplayValues[i];
                }
            }
        }

        return value;
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
    protected boolean setColumnValue(Object anObject, Object aValue)
    {
        // Unmap value if necessary
        if (mDisplayValues != null) {
            for (int i = 0; i < mDisplayValues.length; i++) {
                if (mDisplayValues[i].equals(aValue)) {
                    aValue = mInternalValues[i];
                }
            }
        }

        try {
            mField.set(anObject, aValue);
        }
        catch (Exception e) {
            throw new AWTError("Cannot set field " + mField.getName() +
                " from an object: " + e);
        }

        return true;
    }
}

//Ener-J
//Copyright 2000-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/PropertyTextColumn.java,v 1.2 2005/12/07 03:11:23 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Represents a text column for an ObjectSource. The column's value is derived via
 * reflection from the object's attribute. Reflection is used to eliminate
 * the need for a sub-class for each field of the object.
 * <p>
 * 
 * @version $Id: PropertyTextColumn.java,v 1.2 2005/12/07 03:11:23 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class PropertyTextColumn extends TextColumn
{
    private static final Object[] NO_ARGS = new Object[0];
    
    private PropertyDescriptor mDescriptor;
    private Method mReadMethod;


    /**
     * Construct a new column. Column is optionally editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string. This constructor
     * fails fast by throwing an RuntimeException if anAttributeName does not exist.
     * This usually happens due to a programming error, and shouldn't normally
     * happen at runtime.
     *
     * @param aDescriptor the PropertyDescriptor describing the column's name and value.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     *
     * @throws RuntimeException (unchecked exception) if the field doesn't exist on
     *  anObjectClass.
     */
    public PropertyTextColumn(PropertyDescriptor aDescriptor, boolean anEditingFlag)
    {
        super(aDescriptor.getDisplayName(), anEditingFlag);

        mDescriptor = aDescriptor;
        setFieldName( aDescriptor.getDisplayName() );
        mReadMethod = mDescriptor.getReadMethod();
        if (mReadMethod == null) {
            throw new RuntimeException("Cannot get read method for " + mDescriptor);
        }
    }


    /**
     * Get the column's string value from the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     *
     * @return The value, or null if the value was not found.
     */
    protected String getColumnValue(Object anObject)
    {
        Object ret;
        try {
            ret = mReadMethod.invoke(anObject, NO_ARGS);
        }
        catch (Exception e) {
            return "<error : " + e.toString() + '>';
        }

        if (ret == null) {
            return "null";
        }

        return ret.toString();
    }


    /**
     * Set the column's string value on the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     * @param aValue the string value to be set.
     *
     * @return true if the value was set, or false if the value was not set because
     *  it is invalid.
     */
    protected boolean setColumnValue(Object anObject, String aValue)
    {
        try {
            Method writeMethod = mDescriptor.getWriteMethod();
            if (writeMethod != null) {
                writeMethod.invoke(anObject, new Object[] { aValue });
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot set field " + mDescriptor.getDisplayName() + " from a String: " + e);
        }

        return true;
    }
}

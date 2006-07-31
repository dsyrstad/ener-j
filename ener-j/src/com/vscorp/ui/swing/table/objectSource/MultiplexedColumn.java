// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/MultiplexedColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import com.vscorp.ui.swing.table.EditableSortableTableColumn;

/**
 * A column which multiplexes several other columns. This column multiplexes several other columns to provide for cell-specific
 * rendering and editing within a column.
 * The multiplexing based on some condition determined by the getDelegateColumn method.
 * The subclass must provide the implementation of getDelegateColumn and
 * calculatePreferredWidth.
 * <p>
 */
abstract public class MultiplexedColumn extends EditableSortableTableColumn
{
    /** The column which is currently editing, or null if editing is not active. */
    private EditableSortableTableColumn mEditorColumn = null;

    /** Listener that listens to Cell editing event from the delegate column and
     *  propagates them to the listeners of this column.
     */
    private CellEditorListener mCellEditorListener = new CellEditorPropagator();

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     */
    public MultiplexedColumn()
    {
        this("", true);
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable.
     * Heading alignment HEADER_CENTER by default.
     *
     * @param aColumnTitle the column's heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public MultiplexedColumn(String aColumnTitle, boolean anEditingFlag)
    {
        super(aColumnTitle, anEditingFlag);
    }

    //----------------------------------------------------------------------
    /**
     * Gets the delegate column based on the specified object.
     * The method usually returns the same column for a particular object regardless
     * of anEditingFlag, but it is not required to (i.e., the same column is
     * used for editing and rendering). If two or more columns are returned, they
     * should return consistent values for methods such as "isEditable", etc.
     *
     * @param aValue the object representing the row value for the table.
     * @param aRow the row index for aValue.
     * @param anEditingFlag if true, the multiplexer wants a column suitable for
     *  editing, otherwise the column must be suitable for renderering.
     */
    protected abstract EditableSortableTableColumn getDelegateColumn(Object aValue,
                        int aRow, boolean anEditingFlag);

    //----------------------------------------------------------------------
    // From TableCellRenderer
    public Component getTableCellRendererComponent(JTable aTable,
                Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        EditableSortableTableColumn column = getDelegateColumn(aValue, aRow, false);
        return column.getTableCellRendererComponent(aTable, aValue, aSelectedFlag,
                    aFocusFlag, aRow, aColumn);
    }

    //----------------------------------------------------------------------
    // From TableCellEditor...
    public Component getTableCellEditorComponent(JTable aTable, Object aValue,
                        boolean aSelectedFlag, int aRow, int aColumn)
    {
        mEditorColumn = getDelegateColumn(aValue, aRow, true);
        if (!mEditorColumn.isEditable()) {
            mEditorColumn = null;
            return null;
        }

        // Remove then add to make sure we don't add a duplicate.
        mEditorColumn.removeCellEditorListener(mCellEditorListener);
        mEditorColumn.addCellEditorListener(mCellEditorListener);
        setEditorValue(aValue);
        return mEditorColumn.getTableCellEditorComponent(aTable, aValue, aSelectedFlag,
                        aRow, aColumn);
    }

    //----------------------------------------------------------------------
    // From CellEditor...
    public void cancelCellEditing()
    {
        if (mEditorColumn == null) {
            this.fireEditingCanceled();
        }
        else {
            mEditorColumn.cancelCellEditing();
        }
    }

    //----------------------------------------------------------------------
    // From CellEditor...
    public boolean stopCellEditing()
    {
        if (mEditorColumn == null) {
            this.fireEditingStopped();
            return true;
        }

        boolean returnValue = mEditorColumn.stopCellEditing();

        mEditorColumn.removeCellEditorListener(mCellEditorListener);
        mEditorColumn = null;

        return returnValue;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * A CellEditorListener that propagates events from the delegate to
     * our listeners.
     */
    private final class CellEditorPropagator implements CellEditorListener
    {
        //----------------------------------------------------------------------
        // From CellEditorListener...
        public void editingCanceled(ChangeEvent anEvent)
        {
            MultiplexedColumn.this.fireEditingCanceled();
        }

        //----------------------------------------------------------------------
        // From CellEditorListener...
        public void editingStopped(ChangeEvent anEvent)
        {
            MultiplexedColumn.this.fireEditingStopped();
        }
    }
}

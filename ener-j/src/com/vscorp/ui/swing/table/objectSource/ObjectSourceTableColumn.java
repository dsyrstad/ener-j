// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/ObjectSourceTableColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import com.vscorp.ui.swing.table.EditableSortableTableColumn;

/**
 * TableColumn functionality for classes using ObjectSourceTableModel. Superclass of all ObjectSource-based TableColumns for the ObjectSourceTableModel.
 * Also implements the TableCellRenderer and Editor for the column.
 * <p>
 */
abstract public class ObjectSourceTableColumn extends EditableSortableTableColumn
{

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     */
    public ObjectSourceTableColumn()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable. A single click is
     * required to start editing.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public ObjectSourceTableColumn(String aColumnTitle, boolean anEditingFlag)
    {
        super(aColumnTitle, anEditingFlag);
    }

}

// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/ObjectSourceSortableTableColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

/**
 * Interface for getting a generic Object out of a ObjectSourceTableColumn.
 * This will allow other methods to use a generic ObjectSourceTableColumn object 
 * to get the value out of the subclassed ObjectSourceTableColumn object.
 * Specifically, this is needed for the ObjectSourceTableSorter class which
 * handles sorting of ObjectSourceJTable objects.
 * <p>
 */
public interface ObjectSourceSortableTableColumn 
{
    
    //-------------------------------------------------------------------------
    /**
     * Interface for getting a generic Object out of a TableColumn.  This will allow
     * other methods to use a generic ObjectSourceTableColumn object to get the value
     * out of the ObjectSourceTableColumn.
     * <p>
     */
     public Object getColumnObject(Object anObject);
}


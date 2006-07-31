// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/ObjectSourceJTable.java,v 1.2 2005/11/29 03:55:49 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.vscorp.ui.model.ObjectSource;
import com.vscorp.ui.swing.table.EditableSortableTableColumn;

/**
 * JTable for ObjectSources. It is intended to be used with ObjectSourceColumn
 * and ObjectSourceTableModel.
 * <p>
 */
public class ObjectSourceJTable extends JTable
{
    private EditableSortableTableColumn[] mColumns;

    //----------------------------------------------------------------------
    /** Constructs the ObjectSourceTable and initializes the columns to the ones
     * specified in someColumns.
     *
     * @param anObjectSource the object source that backs the JTable
     * @param someColumns An array of EditableSortableTableColumns
     */
    public ObjectSourceJTable(ObjectSource anObjectSource,
                      EditableSortableTableColumn[] someColumns)
	{
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.setAutoCreateColumnsFromModel(false);
        this.setColumns(someColumns);
        this.setModel( new ObjectSourceTableModel(anObjectSource) );
	}

    //----------------------------------------------------------------------
    /**
     * Gets the current EditableSortableTableColumns used in the table.
     *
     * @return an array of EditableSortableTableColumn
     */
    public EditableSortableTableColumn[] getColumns()
    {
        return mColumns;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the current EditableSortableTableColumns used in the table.
     *
     * @return an array of EditableSortableTableColumn
     */
    public void setColumns(EditableSortableTableColumn[] someColumns)
    {
        TableColumnModel columnModel = this.createDefaultColumnModel();
        this.setColumnModel(columnModel);
        mColumns = someColumns;
        for (int i = 0; i < someColumns.length; i++) {
            columnModel.addColumn(someColumns[i]);
            someColumns[i].calculatePreferredWidth(this, null);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Gets the ObjectSource associated with the model of this table.
     * The model must be descended from ObjectSourceTableModel.
     *
     * @return an ObjectSource.
     */
    public ObjectSource getObjectSource()
    {
        return ((ObjectSourceTableModel)this.getModel()).getObjectSource();
    }

    //----------------------------------------------------------------------
    /**
     * Sets the ObjectSource associated with the model of this table.
     * The model must be descended from ObjectSourceTableModel.
     * The model is refreshed.
     *
     * @param anObjectSource the new ObjectSource
     */
    public void setObjectSource(ObjectSource anObjectSource)
    {
        ((ObjectSourceTableModel)this.getModel()).setObjectSource(anObjectSource);
    }

    //----------------------------------------------------------------------
    /**
     * Capture font changes and resize columns
     */
    public void setFont(Font aFont)
    {
        super.setFont(aFont);
        resizeColumns();
    }

    //----------------------------------------------------------------------
    /** Resize the columns to their preferred widths based on the JTable settings
     * such as font, etc.
     */
    void resizeColumns()
    {
        if (mColumns != null) {
            for (int i = 0; i < mColumns.length; i++) {
                mColumns[i].calculatePreferredWidth(this, null);
            }
        }
    }
}

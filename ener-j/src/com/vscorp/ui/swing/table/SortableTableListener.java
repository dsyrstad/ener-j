// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/SortableTableListener.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/**
 * The MouseListener for sort clicks in a JTable header.
 * Listens for clicks in a JTable header which indicate a sort order change.
 * Dispatches the change to all columns which are of the EditableSortableTableColumn type.
 * The setSortDirection method is called on these columns if the column that is
 * clicked is sortable.<p>
 * <p>
 *
 * @author Daniel A. Syrstad
 */
public class SortableTableListener extends MouseAdapter
{
    private JTable mJTable;

    //----------------------------------------------------------------------
    /**
     * Construct a new Listener for the specified JTable.
     * Adds an instance of this class as a listener to the JTable's header.
     *
     * @param aJTable the JTable to be configured for sorting.
     */
    public SortableTableListener(JTable aJTable)
    {
        mJTable = aJTable;

        mJTable.setColumnSelectionAllowed(false);
        JTableHeader header = mJTable.getTableHeader();
        header.addMouseListener(this);
    }

    //----------------------------------------------------------------------
    // From MouseAdapater/Listener...
    public void mouseClicked(MouseEvent anEvent)
    {
        // Button 1 must have been pressed.
        if ((anEvent.getModifiers() & MouseEvent.BUTTON1_MASK) != MouseEvent.BUTTON1_MASK) {
            return;
        }
        
        // Find the column that was clicked.
        TableColumnModel columnModel = mJTable.getColumnModel();
        int viewColumn = columnModel.getColumnIndexAtX( anEvent.getX() );
        TableColumn clickedColumn = columnModel.getColumn(viewColumn);

        if (anEvent.getClickCount() == 1 &&
            clickedColumn != null &&
            clickedColumn instanceof EditableSortableTableColumn &&
            ((EditableSortableTableColumn)clickedColumn).isSortable() ) {

            // Turn off sorting on all other columns first
            for (int i = 0; i < columnModel.getColumnCount(); i++) {
                TableColumn column = columnModel.getColumn(i);
                if (column != clickedColumn &&
                    column instanceof EditableSortableTableColumn) {
                    ((EditableSortableTableColumn)column).setSortDirection(
                            EditableSortableTableColumn.SORT_NONE);
                }
            }

            // Now set the sort on this column, reversing it if necessary.
            ((EditableSortableTableColumn)clickedColumn).flipSortDirection();
        }
    }
}

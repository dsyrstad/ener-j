// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/objectSource/ObjectSourceTableModel.java,v 1.2 2006/05/05 13:47:14 dsyrstad Exp $

package com.vscorp.ui.swing.table.objectSource;

import javax.swing.table.AbstractTableModel;

import com.vscorp.ui.model.ObjectSource;
import com.vscorp.ui.model.ObjectSourceEvent;
import com.vscorp.ui.model.ObjectSourceException;
import com.vscorp.ui.model.ObjectSourceListener;

/**
 * Table model for ObjectSource. Rows and cells are served-up as objects. It
 * is intended to be used with ObjectSourceColumn and ObjectSourceTable.
 * <p>
 */
public class ObjectSourceTableModel extends AbstractTableModel
        implements ObjectSourceListener
{
    private ObjectSource mSource;


    /**
     * Constructs a new table model with the specified ObjectSource.
     *
     * @param aSource the source to create the model with.
     */
    public ObjectSourceTableModel(ObjectSource aSource)
    {
        mSource = aSource;
        // Add ourself as a listener for changes.
        mSource.addObjectSourceListener(this);
    }


    /**
     * Gets the ObjectSource associated with this model.
     *
     * @return an ObjectSource.
     */
    public ObjectSource getObjectSource()
    {
        return mSource;
    }


    /**
     * Sets the ObjectSource associated with this model. The model is refreshed.
     *
     * @param anObjectSource the new ObjectSource
     */
    public void setObjectSource(ObjectSource anObjectSource)
    {
        // remove ourself as a listener from the current source.
        mSource.removeObjectSourceListener(this);
        mSource = anObjectSource;
        
        // Add ourself as a listener for changes.  We may have already been
        // added as a listener to this object source.  There is no way to check
        // if we are already a listener, so just remove ourselves.  This doesn't
        // seem to cause any problems even if we are not currently a listener.
        mSource.removeObjectSourceListener(this);
        mSource.addObjectSourceListener(this);
        
        // refresh data
        this.fireTableDataChanged();
    }


    // From ObjectSourceListener...
    public void notifyObjectSourceChanged(ObjectSourceEvent anEvent)
    {
        int eventType = anEvent.getType();
        switch (eventType) {
        case ObjectSourceEvent.CONTENTS_CHANGED:
        case ObjectSourceEvent.OBJECT_ADDED:
        case ObjectSourceEvent.OBJECT_CHANGED:
        case ObjectSourceEvent.OBJECT_DELETED:
            this.fireTableDataChanged();
            break;

        case ObjectSourceEvent.RANGE_ADDED:
            this.fireTableRowsInserted(anEvent.getStartIndex(), anEvent.getEndIndex());
            break;

        case ObjectSourceEvent.RANGE_CHANGED:
            this.fireTableRowsUpdated(anEvent.getStartIndex(), anEvent.getEndIndex());
            break;

        case ObjectSourceEvent.RANGE_DELETED:
            this.fireTableRowsDeleted(anEvent.getStartIndex(), anEvent.getEndIndex());
            break;
        }
    }



    // Methods from javax.swing.table.TableModel...



    // From javax.swing.table.TableModel...
    public int getColumnCount()
    {
        return 0;   // Handled by ObjectSourceTableColumn
    }


    // From javax.swing.table.TableModel...
    public int getRowCount()
    {
        try {
            return mSource.size();
        }
        catch (ObjectSourceException e) {
            return 0;
        }
    }


    // From javax.swing.table.TableModel...
    public String getColumnName(int aColIndex)
    {
        return null;   // Handled by ObjectSourceTableColumn
    }


    // From javax.swing.table.TableModel...
    public boolean isCellEditable(int aRowIndex, int aColIndex)
    {
        // The ObjectSourceTableColumn determines if the cell is editable
        return true;
    }


    // From javax.swing.table.TableModel...
    public Object getValueAt(int aRowIndex, int aColIndex)
    {
        // We just return the object corresponding to the row.
        // It is up to the specific ObjectSourceColumn
        // type to extract the correct value (via the Renderer).
        try {
            return mSource.get(aRowIndex);
        }
        catch (ObjectSourceException e) {
            /**  TODO  Handle differently?? */
            throw new java.awt.AWTError("Couldn't get object: " + e);
        }
    }


    // From javax.swing.table.TableModel...
    public Class getColumnClass(int aColumnIndex)
    {
        return Object.class;   // Handled by ObjectSourceTableColumn
    }


    // From javax.swing.table.TableModel...
    public void setValueAt(Object aValue, int aRowIndex, int aColumnIndex)
    {
        // aValue here is an updated version of an object delivered from an ObjectSource.
        // We ignore column index because the ObjectSourceTableColumn would have updated
        // the correct object attribute.
        try {
            mSource.update(aValue, aRowIndex);
        }
        catch (ObjectSourceException e) {
            /**  TODO  Handle differently?? */
            throw new java.awt.AWTError("Couldn't update object: " + e);
        }

        this.fireTableRowsUpdated(aRowIndex, aRowIndex);
    }


    // ...End of methods from javax.swing.table.TableModel

}

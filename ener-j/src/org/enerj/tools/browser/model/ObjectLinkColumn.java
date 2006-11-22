/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
// Ener-J
// Copyright 2006 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/tools/enerjbrowser/model/ObjectLinkColumn.java,v 1.7 2006/02/14 02:39:36 dsyrstad Exp $

package org.enerj.tools.browser.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.EventObject;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.enerj.core.Structure;

import com.vscorp.ui.swing.VSSwingUtil;
import com.vscorp.ui.swing.table.objectSource.ObjectSourceTableColumn;

/**
 * Links to the object on the current row of an ObjectSource-based JTable.
 */
public class ObjectLinkColumn extends ObjectSourceTableColumn
{
    private static final Object[] NO_ARGS = new Object[0];
    //private static final String DRILL_DOWN_IMAGE_NAME = "../images/drilldown.png";
    private static final String DRILL_DOWN_IMAGE_NAME = "../images/arrow-rit-sharp.gif";

    private static JLabel sRenderer = null;
    private static ImageIcon sIcon = null;
    
    private EnerJBrowserModel mModel;
    private ActionListener mLinkListener;
    private PropertyDescriptor mDescriptor;
    private Method mReadMethod;
    private int mStructIndex = -1;


    /**
     * Construct a new column. 
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param aLinkListener an ActionListener which is invoked when the link is clicked. This listener will
     *  receive the linked object as its source.
     * @param aModel the EnerJBrowserModel associated with the column.
     * @param aDescriptor the PropertyDescriptor describing the column's name and value. This may be null
     *  to link to the object that represents the entire row.
     */
    public ObjectLinkColumn(String aColumnTitle, ActionListener aLinkListener, EnerJBrowserModel aModel, PropertyDescriptor aDescriptor)
    {
        super(aColumnTitle, true);
        mLinkListener = aLinkListener;
        mModel = aModel;
        mDescriptor = aDescriptor;
        if (mDescriptor != null) {
            setFieldName( aDescriptor.getDisplayName() );
            mReadMethod = mDescriptor.getReadMethod();
            if (mReadMethod == null) {
                throw new RuntimeException("Cannot get read method for " + mDescriptor);
            }
            else {
                mReadMethod.setAccessible(true);
            }
        }

        // Double-click required to drill-down.
        setNumClicksToEdit(2);
        init();
    }


    /**
     * Construct a new column for viewing a member of a Structure.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param aLinkListener an ActionListener which is invoked when the link is clicked. This listener will
     *  receive the linked object as its source.
     * @param aModel the EnerJBrowserModel associated with the column.
     * @param aStructIndex the index of the Structure member to be viewed.
     */
    public ObjectLinkColumn(String aColumnTitle, ActionListener aLinkListener, EnerJBrowserModel aModel, int aStructIndex)
    {
        this(aColumnTitle, aLinkListener, aModel, null);
        mStructIndex = aStructIndex;
    }
    

    private void init()
    {
        if (sRenderer == null) {
            sIcon = VSSwingUtil.getImageIconResource(this.getClass(), DRILL_DOWN_IMAGE_NAME);
            sRenderer = new JLabel();
            sRenderer.setHorizontalAlignment(JLabel.LEFT);
        }
    }
    

    /**
     * Calculates the preferred width of the TableColumn using the specified JTable.
     * Modifies the TableColumn's preferredWidth.
     *
     * @param aTable the JTable to be used for font information, etc.
     */
    protected void calculatePreferredWidth(JTable aTable)
    {
        JComponent component = new JLabel("MMMMMMMMMMMMMMM"); // 15 em
        calculatePreferredWidth(aTable, component);
    }


    /**
     * Get the column's string value from the specified object.
     *
     * @param anObject an Object returned from an ObjectSource
     *
     * @return The value, or null if the value was not found.
     * 
     * @throws Exception if there was an error retrieving the value.
     */
    protected Object getColumnValue(Object anObject) throws Exception
    {
        if (mReadMethod == null) {
            return null;
        }
        
        return mReadMethod.invoke(anObject, NO_ARGS);
    }


    public Component getTableCellRendererComponent(JTable aTable,
                Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        Object obj = aValue;
        boolean gray = false;
        if (mDescriptor != null) {
            try {
                obj = getColumnValue(aValue);
            }
            catch (Exception e) {
                obj = "";
                gray = true;
            }
        }
        else if (mStructIndex >= 0) {  // Show a Structure member.
            // Default if we cannot resolve
            obj = "";
            gray = true;
            if (aValue instanceof Structure) {
                Object[] values = ((Structure)aValue).getMemberValues();
                if (mStructIndex < values.length) {
                    obj = values[mStructIndex];
                    gray = false;
                }
            }
        }
        
        if (mModel.isLinkable(obj)) {
            sRenderer.setIcon(sIcon);
            sRenderer.setText( mModel.getObjectName(obj) );
        }
        else {
            sRenderer.setIcon(null);
            sRenderer.setText( obj == null ? "null" : obj.toString() );
        }
        
        configureRenderer(sRenderer, aTable, aSelectedFlag, aFocusFlag, aRow, aColumn);
        
        if (gray) {
            sRenderer.setBackground(Color.LIGHT_GRAY);
        }
        
        return sRenderer;
    }


    public Component getTableCellEditorComponent(JTable aTable, Object aValue,
                        boolean isSelected, int aRow, int aColumn)
    {
        Object obj = aValue;
        if (mDescriptor != null) {
            try {
                obj = getColumnValue(aValue);
            }
            catch (Exception e) {
                return null;
            }
        }
        
        if (obj == null) {
            return null;
        }

        mLinkListener.actionPerformed( new ActionEvent(obj, aColumn, "") );
        return new JLabel("Selecting..."); 
    }


    public boolean shouldSelectCell(EventObject anEvent)
    {
        return true;
    }


    public boolean stopCellEditing()
    {
        fireEditingStopped();
        return true;
    }

}

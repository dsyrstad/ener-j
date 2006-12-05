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
/**
 * 
 */
package org.enerj.tools.browser.model;

import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJImplementation;
import org.enerj.core.EnerJOQLQuery;
import org.enerj.core.LogicalClassSchema;
import org.enerj.core.ObjectSerializer;
import org.enerj.core.Persistable;
import org.enerj.core.Schema;
import org.enerj.core.Structure;
import org.enerj.util.ClassUtil;
import org.odmg.ODMGException;
import org.odmg.QueryException;

import com.vscorp.ui.model.CollectionObjectSource;
import com.vscorp.ui.model.ObjectSource;
import com.vscorp.ui.model.ObjectSourceException;
import com.vscorp.ui.swing.VSSwingUtil;
import com.vscorp.ui.swing.table.objectSource.ObjectSourceTableColumn;
import com.vscorp.ui.swing.table.objectSource.ToStringTextColumn;

/**
 * Browser model for a Ener-J database. <p>
 * 
 * @version $Id: EnerJBrowserModel.java,v 1.17 2006/02/19 01:20:33 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class EnerJBrowserModel 
{
    private static final Object[] NO_ARGS = new Object[0];

    private static TableInfo sEmptyTableInfo = null;
    
    private String mDBURI;
    private EnerJDatabase mDB;
    private DefaultListModel mHistoryListModel;
    /** List of class names. */
    private List<String> mExtents = null;
    private ActionListener mLinkListener;


    public EnerJBrowserModel(String aDBURI, ActionListener aLinkListener) 
    {
        mDBURI = aDBURI;
        mLinkListener = aLinkListener;
        mDB = new EnerJDatabase();
        try {
            mDB.open(aDBURI, EnerJDatabase.OPEN_READ_WRITE);
            mDB.setAllowNontransactionalReads(true);
        }
        catch (ODMGException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets the database behind the model. 
     *
     * @return the EnerJDatabase.
     */
    public EnerJDatabase getDB()
    {
        return mDB;
    }


    public ListModel getHistoryListModel() 
    {
        if (mHistoryListModel == null) {
            mHistoryListModel = new DefaultListModel();
        }
        
        return mHistoryListModel;
    }
    

    public Object getObjectForHistoryEntry(int anIndex)
    {
        return ((PropertyRow)getHistoryListModel().getElementAt(anIndex)).getValue();
    }


    public String getObjectName(Object anObject)
    {
        if (anObject == null) {
            return "null";
        }

        String baseClassName = ClassUtil.getBaseClassName( anObject.getClass() );
        if (anObject instanceof Persistable) {
            long oid = EnerJImplementation.getEnerJObjectId(anObject);
            if (oid != ObjectSerializer.NULL_OID) {
                return baseClassName + ':' + oid;
            }
        }
        
        // Regular object
        return baseClassName + ":[" + System.identityHashCode(anObject) + ']';
    }


    public List<String> getExtents()
    {
        if (mExtents == null) {
            Schema schema; 
            try {
                schema = getDB().getSchema();
            }
            catch (ODMGException e) {
                // TODO
                e.printStackTrace();
                return null;
            }

            Collection<LogicalClassSchema> classSchemas = schema.getLogicalClasses();
            mExtents = new ArrayList( classSchemas.size() );
            for (Iterator<LogicalClassSchema> iter = classSchemas.iterator(); iter.hasNext(); ) {
                LogicalClassSchema classSchema = iter.next();
                mExtents.add( classSchema.getClassName() );
            }
        }
        
        return mExtents;
    }
    

    public TableInfo getTableInfoForExtents()
    {
        Object obj = getExtents();
        addHistoryEntry(obj, "Extents");
        return getTableInfoWithoutHistory(obj);
    }
    

    public TableInfo getTableInfoForSchema()
    {
        try {
            Object obj = getDB().getSchema();
            addHistoryEntry(obj, "Schema");
            return getTableInfoWithoutHistory(obj);
        }
        catch (ODMGException e) {
            // TODO
            e.printStackTrace();
            return null;
        }
    }
    

    public TableInfo getTableInfoForBindery()
    {
        Object obj = null; // TODO getDB().getDatabaseRoot().getBindery();
        addHistoryEntry(obj, "Named Objects");
        return getTableInfoWithoutHistory(obj);
    }
    

    public TableInfo getTableInfoForQuery(String aQuery) throws QueryException
    {
        Object result = new EnerJOQLQuery(aQuery).execute();
        addHistoryEntry( new QueryDef(aQuery, result), "Query: " + aQuery);
        return getTableInfoWithoutHistory(result);
    }


    /**
     * Adds an object to the history list. 
     *
     * @param anObj the object.
     * @param aHistoryName a name for this object in the history list.
     */
    public void addHistoryEntry(Object anObj, String aHistoryName)
    {
        VSSwingUtil.invokeLater(this, new PropertyRow(aHistoryName, anObj), "addHistorySafely");
    }
    

    /**
     * Called via VSSwingUtil.invokeLater to safely add a history entry thru the event thread.
     */
    private void addHistorySafely(Object aValue)
    {
        PropertyRow row = (PropertyRow)aValue;
        if ( !mHistoryListModel.isEmpty()) {
            // Don't add duplicates of the same object in a row.
            PropertyRow lastRow = (PropertyRow)mHistoryListModel.getElementAt( mHistoryListModel.getSize() - 1 );
            if (lastRow.getValue() == row.getValue()) {
                return;
            }
        }
        
        mHistoryListModel.addElement(row);
    }
    

    /**
     * Gets the TableInfo for the object. 
     *
     * @param anObj the object.
     * 
     * @return the TableInfo for the given object. 
     */
    public TableInfo getTableInfo(Object anObj)
    {
        addHistoryEntry(anObj, getObjectName(anObj));
        return getTableInfoWithoutHistory(anObj);
    }


    /**
     * Gets the TableInfo for the object without adding to the history list. 
     *
     * @param anObj the object.
     * 
     * @return the TableInfo for the given object. 
     */
    public TableInfo getTableInfoWithoutHistory(Object anObj)
    {
        if (anObj instanceof QueryDef) {
            anObj = ((QueryDef)anObj).getResult();
        }
        
        if (anObj instanceof NamedObject) {
            anObj = ((NamedObject)anObj).getObject();        
        }

        return getTableInfo(anObj, anObj);
    }
    

    /**
     * Gets the TableInfo for the object. 
     *
     * @param anObj the object.
     * @param aRealRefObject the actual reference object to return on the TableInfo.
     * 
     * @return the TableInfo for the given object. 
     */
    private TableInfo getTableInfo(Object anObj, Object aRealRefObject)
    {
        if (anObj == null) {
            return getEmptyTableInfo();
        }
        
        // TODO: Handle Iterator 
        if (anObj.getClass().isArray()) {
            // This handles primitive and Object arrays, converting the primitive
            // type to a wrapper type. Arrays.asList() would only work for object arrays.
            int len = Array.getLength(anObj);
            List list = new ArrayList(len);
            for (int i = 0 ; i < len; i++) {
                list.add( Array.get(anObj, i) );
            }
            
            anObj = list;
        }
        
        if (anObj instanceof Collection) {
            // A collection of size 1 is treated like a single object.
            Collection collection = (Collection)anObj;
            Iterator iter = collection.iterator();
            if (!iter.hasNext()) {
                return getEmptyTableInfo();
            }
            
            Object firstElement = iter.next();
            if (!iter.hasNext() && anObj == aRealRefObject) {
                return getTableInfo(firstElement);
            }
            
            // Determine the Collection type from the first element.
            ObjectSourceTableColumn[] columns;
            if (firstElement instanceof Structure) {
                String[] memberNames = ((Structure)firstElement).getMemberNames();
                columns = new ObjectSourceTableColumn[ memberNames.length + 1 ];
                columns[0] = new ObjectLinkColumn("<Object>", mLinkListener, this, null); 
                int colIdx = 1;
                for (int i = 0; i < memberNames.length; i++, colIdx++) {
                    columns[colIdx] = new ObjectLinkColumn(memberNames[i], mLinkListener, this, i);
                }
            }
            else if (firstElement == null) {
                columns = new ObjectSourceTableColumn[] {  new ObjectLinkColumn("<Object>", mLinkListener, this, null) };
            }
            else {
                PropertyDescriptor[] descriptors = ClassUtil.getPropertyDescriptors(firstElement);
                columns = new ObjectSourceTableColumn[ descriptors.length + 1 ];
                columns[0] = new ObjectLinkColumn("<Object>", mLinkListener, this, null); 
                int colIdx = 1;
                for (int i = 0; i < descriptors.length; i++, colIdx++) {
                    columns[colIdx] = new ObjectLinkColumn(descriptors[i].getDisplayName(), mLinkListener, this, descriptors[i]);
                }
            }

            try {
                return new TableInfo( new CollectionObjectSource(collection), columns, aRealRefObject);
            }
            catch (ObjectSourceException e) {
                // Ignore - shouldn't happen.
            }
        } 
        
        if (anObj instanceof Map) {
            // Map is turned into a Set of Map.Entry
            return getTableInfo( ((Map)anObj).entrySet(), aRealRefObject);
        }

        // Default: Treat as a single object.
        if ( !isLinkable(anObj)) {
            try {
                return new TableInfo( new CollectionObjectSource(Collections.singletonList(anObj)),
                                new ObjectSourceTableColumn[] { new ToStringTextColumn("<value>") },
                                aRealRefObject);
            }
            catch (ObjectSourceException e) {
                // Ignore - shouldn't happen.
            }
        }

        // Map properties as rows.
        PropertyDescriptor[] descriptors = ClassUtil.getPropertyDescriptors(anObj);
        List props = new ArrayList(descriptors.length);
        for (int i = 0; i < descriptors.length; i++) {
            try {
                Method readMethod = descriptors[i].getReadMethod();
                Object value = null;
                if (readMethod != null) {
                    value = readMethod.invoke(anObj, NO_ARGS);
                }
                
                props.add( new PropertyRow(descriptors[i].getDisplayName(), value) );
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        return getTableInfo( Collections.singleton(anObj), aRealRefObject);
    }
    

    /**
     * Determine if the object's type is "linkable" (i.e., can it be clicked on to
     * recursively explore the object).
     * A simple object like a String, Integer, Number, Date, etc. is not linkable.
     */
    public boolean isLinkable(Object anObj)
    {
        return anObj != null && isLinkable( anObj.getClass() );
    }
    

    /**
     * Determine if the given type is "linkable" (i.e., can it be clicked on to
     * recursively explore the object).
     * A simple type like a String, Integer, Number, Date, etc. is not linkable.
     */
    public boolean isLinkable(Class aClass)
    {
        // Convert primitive types to their wrapper types.
        aClass = ClassUtil.mapFromPrimitiveType(aClass);
        return !(aClass == null || 
                 Class.class.isAssignableFrom(aClass) || 
                 Boolean.class.isAssignableFrom(aClass) || 
                 String.class.isAssignableFrom(aClass) || 
                 Number.class.isAssignableFrom(aClass) || 
                 Date.class.isAssignableFrom(aClass) || 
                 Timestamp.class.isAssignableFrom(aClass)); 
    }


    /**
     * Gets a TableInfo with no elements in it. 
     *
     * @return a TableInfo.
     */
    public TableInfo getEmptyTableInfo()
    {
        if (sEmptyTableInfo == null) {
            try {
                ObjectSource source = new CollectionObjectSource(Collections.EMPTY_LIST);
                sEmptyTableInfo = new TableInfo(source, new ObjectSourceTableColumn[0], new Object());
            }
            catch (ObjectSourceException e) {
                // Ignore - shouldn't happen.
            }
        }
        
        return sEmptyTableInfo;
    }
    


    public static final class TableInfo
    {
        private ObjectSource mSource;
        private ObjectSourceTableColumn[] mTableColumns;
        private Object mObject;
        
        private TableInfo(ObjectSource aSource, ObjectSourceTableColumn[] someTableColumns, Object anObj)
        {
            mSource = aSource;
            mTableColumns = someTableColumns;
            mObject = anObj;
        }


        /**
         * Gets the Object.
         *
         * @return a Object.
         */
        public Object getObject()
        {
            return mObject;
        }


        /**
         * Gets the Source.
         *
         * @return an ObjectSource.
         */
        public ObjectSource getSource()
        {
            return mSource;
        }


        /**
         * Gets the Table Columns.
         *
         * @return an ObjectSourceTableColumn[].
         */
        public ObjectSourceTableColumn[] getTableColumns()
        {
            return mTableColumns;
        }
    }
    


    private static final class PropertyRow
    {
        private String mName;
        private Object mValue;
        

        PropertyRow(String aName, Object aValue)
        {
            mName = aName;
            mValue = aValue;
        }


        String getName()
        {
            return mName;
        }


        Object getValue()
        {
            return mValue;
        }
        

        public String toString() 
        {
            return mName;
        }
    }
}

// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/examples/TestObjectSource.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table.examples;

import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.vscorp.ui.model.BaseObjectSource;
import com.vscorp.ui.model.ObjectSourceException;
import com.vscorp.ui.swing.table.objectSource.IconColumn;

/**
 * Test source for Example of an ObjectSource-based JTable.
 * 
 * @version $Id: TestObjectSource.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
class TestObjectSource extends BaseObjectSource
{
    private TestObject[] mObjects;


    static class TestObject
    {
        String mObjectId;
        String mName;
        boolean mFlag = false;
        int mIntValue = 5;
        Date mDate = new Date();
        long mTimeInterval;
        Double mDouble;
        int mProgress;

        TestObject(String anObjectId, String aName)
        {
            mObjectId = anObjectId;
            mName = aName;
            mTimeInterval = (long)(Math.random() * 50000000);
            if ((int)(Math.random() * 6) == 0) {
                mDouble = null;
            }
            else {
                mDouble = new Double( Math.random() * 323211332 );
            }

            mProgress = (int)(Math.random() * 100.) + 1;
        }
    }


    public static class NameIconColumn extends IconColumn
    {
        // JLF icons
        private ImageIcon[] mIcons = {
                        new ImageIcon(),
                        new ImageIcon(),
                        new ImageIcon(),
                        new ImageIcon(),
                        new ImageIcon(),
                        new ImageIcon(),
                        new ImageIcon(),
                        /*new ImageIcon( this.getClass().getResource("/toolbarButtonGraphics/general/Print16.gif") ),
                        new ImageIcon( this.getClass().getResource("/toolbarButtonGraphics/general/Cut16.gif") ),
                        new ImageIcon( this.getClass().getResource("/toolbarButtonGraphics/general/Copy16.gif") ),
                        new ImageIcon( this.getClass().getResource("/toolbarButtonGraphics/general/Paste16.gif") ),
                        new ImageIcon( this.getClass().getResource("/toolbarButtonGraphics/general/Help16.gif") ),
                        new ImageIcon( this.getClass().getResource("/toolbarButtonGraphics/general/Information16.gif") ),
                        new ImageIcon( this.getClass().getResource("/toolbarButtonGraphics/general/Find16.gif") ),*/
                    null
        };


        public NameIconColumn(Class anObjectClass, String anAttributeName, String[] someArgs)
        {
            super();
        }


        NameIconColumn(String aHeading, boolean anEditingFlag)
        {
            super(aHeading, anEditingFlag);
        }


        public String getColumnValue(Object anObject)
        {
            return ((TestObject)anObject).mName;
        }


        public Icon getColumnIcon(Object anObject)
        {
            String string;
            if (anObject == null) {
                string = "";
            }
            else {
                string = ((TestObject)anObject).mName;
            }

            int sum = 0;
            for (int i = 0; i < string.length(); i++) {
                sum += (int)string.charAt(i);
            }

            return mIcons[ sum % 8 ];
        }


        public boolean setColumnValue(Object anObject, String aValue)
        {
            ((TestObject)anObject).mName = aValue;
            return true;
        }
    }


    private class ChangeTimerTask extends java.util.TimerTask
    {

        ChangeTimerTask()
        {
        }


        public void run()
        {
            int index = (int)(Math.random() * mObjects.length );
            if (mObjects[index].mProgress < 100) {
                mObjects[index].mProgress += 2;
                if (mObjects[index].mProgress > 100) {
                    mObjects[index].mProgress = 100;
                }

                TestObjectSource.this.fireObjectSourceRangeChanged(index, index);
            }
        }
    }


    TestObjectSource()
    {
        mObjects = new TestObject[100];
        for (int i = 0; i < mObjects.length; i++) {
            mObjects[i] = new TestObject("Obj:" + i, "Object " + i);
        }

        java.util.Timer timer = new java.util.Timer();
        for (int i = 0; i < 10; i++) {
            timer.schedule(new ChangeTimerTask(), 50, 1);
        }
    }


    public int size() throws ObjectSourceException
    {
        return mObjects.length;
    }


    public Object get(int anIndex)
        throws ObjectSourceException, java.lang.ArrayIndexOutOfBoundsException
    {
        return mObjects[anIndex];
    }


    public void get(int anIndex, int aLength, Object[] anObjectArray)
        throws ObjectSourceException, java.lang.ArrayIndexOutOfBoundsException
    {
        System.arraycopy(mObjects, anIndex, anObjectArray, 0, aLength);
    }


    public Object get(Object anObjectId) throws ObjectSourceException
    {
        for (int i = 0; i < mObjects.length; i++) {
            if (getObjectId(mObjects[i]).equals(anObjectId)) {
                return mObjects[i];
            }
        }

        return null;
    }


    public void update(Object anObject, int anIndex)
            throws ObjectSourceException
    {
        if (!(anObject instanceof TestObject)) {
            throw new ObjectSourceException("Object is not a TestObject");
        }

        mObjects[anIndex] = (TestObject)anObject;
        this.fireObjectSourceRangeChanged(anIndex, anIndex);
    }


    public void update(Object anObject) throws ObjectSourceException
    {
        if (!(anObject instanceof TestObject)) {
            throw new ObjectSourceException("Object is not a TestObject");
        }

        Object objectId = getObjectId(anObject);
        if (objectId == null) {
            throw new ObjectSourceException("Object id " + objectId + " not found");
        }

        for (int i = 0; i < mObjects.length; i++) {
            if (getObjectId(mObjects[i]).equals(objectId)) {
                mObjects[i] = (TestObject)anObject;
                this.fireObjectSourceObjectChanged(objectId);
                return;
            }
        }

    }


    public Object getObjectId(Object anObject)
    {
        if (anObject instanceof TestObject) {
            return ((TestObject)anObject).mObjectId;
        }

        return null;
    }
}
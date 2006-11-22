// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/model/CollectionObjectSource.java,v 1.5 2006/02/10 03:51:14 dsyrstad Exp $
package com.vscorp.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/**
 * An ObjectSource for Collections. <p>
 * 
 * @version $Id: CollectionObjectSource.java,v 1.5 2006/02/10 03:51:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class CollectionObjectSource extends BaseObjectSource
{
    private Collection mCollection;
    /** Current iterator used while building mList from mCollection */
    private Iterator mIterator = null;
    private List mList;
    private int mSize = -1; // -1 = not initialized.
    

    public CollectionObjectSource(Collection aCollection) throws ObjectSourceException
    {
        mCollection = aCollection;
        if (aCollection instanceof List && aCollection instanceof RandomAccess) {
            mList = (List)aCollection;
        }
        else {
            mList = new ArrayList(1000);
        }
    }
    

    public int size() throws ObjectSourceException
    {
        if (mSize < 0) {
            mSize = mCollection.size();
        }

        return mSize;
    }


    public Object get(int anIndex) throws ObjectSourceException, ArrayIndexOutOfBoundsException
    {
        if (mList != mCollection) {
            int listSize = mList.size(); 
            if (listSize <= anIndex) {
                // Populate the list up to anIndex. Iterator stays properly positioned because it is 
                // only accessed by this method.
                if (mIterator == null) {
                    mIterator = mCollection.iterator();
                }
                
                for (int i = listSize; mIterator.hasNext() && i <= anIndex; i++) {
                    mList.add(i, mIterator.next() );
                }
            }
        }
        
        return mList.get(anIndex);
    }


    public void get(int anIndex, int aLength, Object[] anObjectArray) throws ObjectSourceException,
                    ArrayIndexOutOfBoundsException
    {
        for (int i = 0; i < aLength; ++i) { 
            anObjectArray[i] = get(i + anIndex);
        }
    }


    public Object get(Object anObjectId) throws ObjectSourceException
    {
        return get( (Integer)anObjectId );
    }


    public void update(Object anObject, int anIndex) throws ObjectSourceException
    {
    }


    public void update(Object anObject) throws ObjectSourceException
    {
    }


    public Object getObjectId(Object anObject)
    {
        int size;
        try {
            size = size();
        }
        catch (ObjectSourceException e) {
            return null;
        }
        
        for (int i = 0; i < size; ++i) {
            try {
                Object targetObj = get(i); 
                if (targetObj == anObject || 
                    (anObject != null && anObject.equals(targetObj))) {
                    return (Integer)i;
                }
            }
            catch (ObjectSourceException e) {
                throw new RuntimeException(e);
            }
        }
        
        return null;
    }
}

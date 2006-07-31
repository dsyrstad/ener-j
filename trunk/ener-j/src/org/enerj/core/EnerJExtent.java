// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/EnerJExtent.java,v 1.8 2006/02/09 03:42:24 dsyrstad Exp $

package org.enerj.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.odmg.ODMGRuntimeException;
import org.enerj.server.ExtentIterator;
import org.enerj.server.MetaObjectServerSession;


/**
 * Ener-J implementation of Extent.
 *
 * @version $Id: EnerJExtent.java,v 1.8 2006/02/09 03:42:24 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class EnerJExtent implements Extent
{
    private EnerJDatabase mDatabase;
    private Class mCandidateClass;
    private boolean mHasSubclasses;
    private Collection mCollection = null;
    private Set mOpenIterators = new HashSet(5);
    private int mSize = -1; // -1 = not initialized.

    //----------------------------------------------------------------------
    EnerJExtent(EnerJDatabase aDatabase, Class aCandidateClass, boolean hasSubclasses)
    {
        mDatabase = aDatabase;
        mCandidateClass = aCandidateClass;
        mHasSubclasses = hasSubclasses;
    }

    //----------------------------------------------------------------------
    /**
     * Ensures that the extent iterator has been converted to a Collection in mCollection. 
     */
    public void ensureCollection()
    {
        if (mCollection == null) {
            mCollection = new ArrayList( size() );
            Iterator iter = iterator();
            try {
                while (iter.hasNext()) {
                    mCollection.add( iter.next() ); 
                }
            } finally {
                close(iter);
            }
        }
    }

    //----------------------------------------------------------------------
    // Collection interface...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    public boolean add(Object o)
    {
        throw new UnsupportedOperationException("Extent collection is immutable");
    }

    //----------------------------------------------------------------------
    public boolean addAll(Collection c)
    {
        throw new UnsupportedOperationException("Extent collection is immutable");
    }

    //----------------------------------------------------------------------
    public void clear()
    {
        throw new UnsupportedOperationException("Extent collection is immutable");
    }

    //----------------------------------------------------------------------
    public boolean contains(Object anObject)
    {
        Iterator iter = this.iterator();
        try {
            while (iter.hasNext()) {
                Object obj = iter.next(); 
                if ((obj == null && anObject == null) ||
                    (obj != null && obj.equals(anObject))) {
                    return true;
                }
            }
            
            return false;
        } finally {
            close(iter);
        }
    }

    //----------------------------------------------------------------------
    public boolean containsAll(Collection aCollection)
    {
        ensureCollection();
        return mCollection.containsAll(aCollection);
    }

    //----------------------------------------------------------------------
    public boolean isEmpty()
    {
        return size() == 0;
    }

    //----------------------------------------------------------------------
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException("Extent collection is immutable");
    }

    //----------------------------------------------------------------------
    public boolean removeAll(Collection c)
    {
        throw new UnsupportedOperationException("Extent collection is immutable");
    }

    //----------------------------------------------------------------------
    public boolean retainAll(Collection c)
    {
        throw new UnsupportedOperationException("Extent collection is immutable");
    }

    //----------------------------------------------------------------------
    public int size()
    {
        if (mSize < 0) {
            long size = mDatabase.getMetaObjectServerSession().getExtentSize(mCandidateClass.getName(), mHasSubclasses);
            if (size > Integer.MAX_VALUE) {
                throw new ArrayIndexOutOfBoundsException("Extent size of " + size + " exceeds size of int");
            }
            
            mSize = (int)size;
        }
        
        return mSize;
    }

    //----------------------------------------------------------------------
    public Object[] toArray()
    {
        ensureCollection();
        return mCollection.toArray();
    }

    //----------------------------------------------------------------------
    public Object[] toArray(Object[] a)
    {
        ensureCollection();
        return mCollection.toArray(a);
    }

    //----------------------------------------------------------------------
    // ...Collection interface.
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // Extent interface...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    public java.util.Iterator iterator()
    {
        EnerJExtentIterator iterator = new EnerJExtent.EnerJExtentIterator();
        mOpenIterators.add(iterator);
        return iterator;
    }

    //----------------------------------------------------------------------
    public boolean hasSubclasses()
    {
        return mHasSubclasses;
    }

    //----------------------------------------------------------------------
    public Class getCandidateClass()
    {
        return mCandidateClass;
    }

    //----------------------------------------------------------------------
    public void closeAll()
    {
        Iterator iter = mOpenIterators.iterator();
        while (iter.hasNext()) {
            EnerJExtentIterator extentIterator = (EnerJExtentIterator)iter.next();
            if (extentIterator.isOpen()) {
                extentIterator.close();
                iter.remove();
            }
        }
    }
    
    //----------------------------------------------------------------------
     public void close(java.util.Iterator anIterator)
     {
        if (anIterator instanceof EnerJExtentIterator) {
            EnerJExtentIterator extentIterator = (EnerJExtentIterator)anIterator;
            extentIterator.close();
            mOpenIterators.remove(anIterator);
        }
     }

    //----------------------------------------------------------------------
    // ...Extent interface.
    //----------------------------------------------------------------------
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private final class EnerJExtentIterator implements java.util.Iterator
    {
        private static final int DEFAULT_CHUNK_SIZE = 2000;

        /** ExtentIterator returned from the session. */
        private ExtentIterator mExtentIterator;
        /** Queue of OIDs represent the chunk of objects we got back from next(). */
        private long[] mOIDs = null;
        /** Queue position. */
        private int mOIDIdx = 0; 
        private boolean mIsOpen = true;

        //----------------------------------------------------------------------
        EnerJExtentIterator()
        {
            MetaObjectServerSession session = mDatabase.getMetaObjectServerSession();
            mExtentIterator = session.createExtentIterator(mCandidateClass.getName(), mHasSubclasses);
        }
        
        //----------------------------------------------------------------------
        /**
         * Closes this iterator.
         */
        void close()
        {
            mExtentIterator.close();
            mIsOpen = false;
        }

        //----------------------------------------------------------------------
        /**
         * Checks if the iterator is open.
         *
         * @return true if it's open, else false.
         */
        boolean isOpen()
        {
            return mIsOpen;
        }

        //----------------------------------------------------------------------
        /**
         * Verifies that the iterator is open.
         */
        private void checkOpen() throws ODMGRuntimeException
        {
            if (!mIsOpen) {
                throw new ODMGRuntimeException("Iterator is closed.");
            }
        }

        //----------------------------------------------------------------------
        public boolean hasNext() 
        {
            checkOpen();
            if (mOIDs == null || mOIDIdx >= mOIDs.length) {
                if (!mExtentIterator.hasNext()) {
                    return false;
                }

                mOIDs = mExtentIterator.next(DEFAULT_CHUNK_SIZE);
                mOIDIdx = 0;
            }

            return true;
        }
        
        //----------------------------------------------------------------------
        public Object next()
        {
            checkOpen();
            if (!hasNext()) {
                throw new NoSuchElementException("Attempted to go past the end of the Extent iterator.");
            }

            return mDatabase.getObjectForOID( mOIDs[mOIDIdx++] );
        }
        
        //----------------------------------------------------------------------
        public void remove() 
        {
            throw new UnsupportedOperationException("Cannot remove from an Extent iterator");
        }
        
    }
}
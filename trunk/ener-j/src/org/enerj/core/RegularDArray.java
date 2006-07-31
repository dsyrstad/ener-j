// Ener-J
// Copyright 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/RegularDArray.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import java.util.*;

import org.odmg.*;
import org.enerj.annotations.Persist;

/**
 * Ener-J implementation of org.odmg.DArray which supports persistable dynamic arrays
 * as first-class objects (FCOs). This type of array is useful when the array itself can
 * fit entirely in memory at one time.  If you have an array that cannot fit
 * reasonably in memory, you will have more than 2 billion objects in your collection,
 * or you want to conserve disk storage space, should use VeryLargeDArray.
 * <p>
 * The array is implemented as a container of java.util.ArrayList. However,
 * if you reference this type of collection in your object, it is treated as a FCO,
 * meaning that it will be loaded only when directly referenced (demand loaded).
 * It also allows the collection to be referenced from several different persistable
 * objects, all sharing the same instance of the collection. 
 * <p>
 * If you were to reference a java.util.ArrayList directly in your object (rather than this class), the
 * collection would be treated as an SCO (second-class object) and be loaded when
 * your object is loaded. Also, any changes to the collection would cause your object
 * to also be written to the database. 
 *
 * @version $Id: RegularDArray.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DArray
 * @see VeryLargeDArray
 */
@Persist
public class RegularDArray implements org.odmg.DArray, Cloneable
{
    /** The delegate array. This is treated as an SCO when this FCO is persisted. */
    private ArrayList mArrayList;
    
    //----------------------------------------------------------------------
    /**
     * Constructs a new RegularDArray with the specified initial capacity. 
     * 
     * @param anInitialCapacity the initially allocated capacity of the array.
     *  This does not affect the size of the array.
     */
    public RegularDArray(int anInitialCapacity)
    {
        mArrayList = new ArrayList(anInitialCapacity);
    }
    
    //----------------------------------------------------------------------
    /**
     * Constructs a new RegularDArray with an initial capacity of 10. 
     */
    public RegularDArray()
    {
        this(10);
    }
    
    //----------------------------------------------------------------------
    // Start of Interfaces: org.odmg.DArray, java.util.Collection, java.util.List...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    public boolean add(Object o)
    {
        return mArrayList.add(o);
    }
    
    //----------------------------------------------------------------------
    public void add(int index, Object element)
    {
        mArrayList.add(index, element);
    }
    
    //----------------------------------------------------------------------
    public boolean addAll(Collection c)
    {
        return mArrayList.addAll(c);
    }
    
    //----------------------------------------------------------------------
    public boolean addAll(int index, Collection c)
    {
        return mArrayList.addAll(index, c);
    }
    
    //----------------------------------------------------------------------
    public void clear()
    {
        mArrayList.clear();
    }
    
    //----------------------------------------------------------------------
    public boolean contains(Object o)
    {
        return mArrayList.contains(o);
    }
    
    //----------------------------------------------------------------------
    public boolean containsAll(Collection c)
    {
        return mArrayList.containsAll(c);
    }
    
    //----------------------------------------------------------------------
    public Object get(int index) 
    {
        return mArrayList.get(index);
    }
    
    //----------------------------------------------------------------------
    public int indexOf(Object o) 
    {
        return mArrayList.indexOf(o);
    }
    
    //----------------------------------------------------------------------
    public boolean isEmpty() 
    {
        return mArrayList.isEmpty();
    }
    
    //----------------------------------------------------------------------
    public Iterator iterator() 
    {
        return mArrayList.iterator();
    }
    
    //----------------------------------------------------------------------
    public int lastIndexOf(Object o) 
    {
        return mArrayList.lastIndexOf(o);
    }
    
    //----------------------------------------------------------------------
    public ListIterator listIterator() 
    {
        return mArrayList.listIterator();
    }
    
    //----------------------------------------------------------------------
    public ListIterator listIterator(int index) 
    {
        return mArrayList.listIterator(index);
    }
    
    //----------------------------------------------------------------------
    public boolean remove(Object o) 
    {
        return mArrayList.remove(o);
    }
    
    //----------------------------------------------------------------------
    public Object remove(int index) 
    {
        return mArrayList.remove(index);
    }
    
    //----------------------------------------------------------------------
    public boolean removeAll(Collection c) 
    {
        return mArrayList.removeAll(c);
    }
    
    //----------------------------------------------------------------------
    public void resize(int aNewSize) 
    {
        // ODMG v3.0 2.3.6.4 says resize changes the maximum number of elements
        // the array can contain. It also says if aNewSize is smaller than
        // the actual number of elements, an exception is thrown. The
        // org.odmg javadoc says nothing about the behavior. Poet allows shrinking
        // or growing. IIOSS conforms to the spec.
        if (aNewSize < mArrayList.size()) {
            throw new IllegalArgumentException("resize(" + aNewSize + ") is smaller than current size: " + mArrayList.size());
        }
        
        int count = aNewSize - mArrayList.size();
        for (; count > 0; --count) {
            mArrayList.add(null);
        }
    }
    
    //----------------------------------------------------------------------
    public boolean retainAll(Collection c) 
    {
        return mArrayList.retainAll(c);
    }
    
    //----------------------------------------------------------------------
    public Object set(int index, Object element) 
    {
        return mArrayList.set(index, element);
    }
    
    //----------------------------------------------------------------------
    public int size() 
    {
        return mArrayList.size();
    }
    
    //----------------------------------------------------------------------
    public List subList(int fromIndex, int toIndex) 
    {
        return mArrayList.subList(fromIndex, toIndex);
    }
    
    //----------------------------------------------------------------------
    public Object[] toArray() 
    {
        return mArrayList.toArray();
    }
    
    //----------------------------------------------------------------------
    public Object[] toArray(Object[] a) 
    {
        return mArrayList.toArray(a);
    }
    
    //----------------------------------------------------------------------
    public java.util.Iterator select(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    
    //----------------------------------------------------------------------
    public boolean existsElement(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    
    //----------------------------------------------------------------------
    public org.odmg.DCollection query(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    
    //----------------------------------------------------------------------
    public Object selectElement(String str) throws org.odmg.QueryInvalidException 
    {
        /**  TODO  finish */
        throw new QueryInvalidException("Not implemented yet");
    }
    
    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public int hashCode() 
    {
        return mArrayList.hashCode();
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object anObject) 
    {
        return mArrayList.equals(anObject);
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public String toString() 
    {
        return mArrayList.toString();
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public Object clone() throws CloneNotSupportedException
    {
        RegularDArray clone = (RegularDArray)super.clone();
        clone.mArrayList = (ArrayList)mArrayList.clone();
        return clone;
    }
    
    //----------------------------------------------------------------------
    // ...End of Interfaces: org.odmg.DArray, java.util.Collection, java.util.List.
    //----------------------------------------------------------------------
}


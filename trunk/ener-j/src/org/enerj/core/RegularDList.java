// Ener-J
// Copyright 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/RegularDList.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import java.util.*;

import org.odmg.*;
import org.enerj.annotations.Persist;

/**
 * Ener-J implementation of org.odmg.DList which supports persistable linked lists
 * as first-class objects (FCOs). This type of list is useful when the list nodes can
 * fit entirely in memory at one time.  If you have an list that cannot fit
 * reasonably in memory, you will have more than 2 billion objects in your collection,
 * or you want to conserve disk storage space, should use  TODO  VeryLargeDList.
 * <p>
 * The list is implemented as a container of java.util.LinkedList. However,
 * if you reference this type of collection in your object, it is treated as a FCO,
 * meaning that it will be loaded only when directly referenced (demand loaded).
 * It also allows the collection to be referenced from several different persistable
 * objects, all sharing the same instance of the collection. 
 * <p>
 * If you were to reference a java.util.LinkedList directly in your object (rather than this class), the
 * collection would be treated as an SCO (second-class object) and be loaded when
 * your object is loaded. Also, any changes to the collection would cause your object
 * to also be written to the database. 
 *
 * @version $Id: RegularDList.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DList
 * @see VeryLargeDList
 */
@Persist
public class RegularDList implements org.odmg.DList, Cloneable
{
    /** The delegate list. This is treated as an SCO when this FCO is persisted. */
    private LinkedList mLinkedList;
    
    //----------------------------------------------------------------------
    /**
     * Constructs an empty RegularDList. 
     */
    public RegularDList()
    {
        mLinkedList = new LinkedList();
    }
    
    //----------------------------------------------------------------------
    // Start of Interfaces: org.odmg.DList, java.util.Collection, java.util.List...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    public boolean add(Object o)
    {
        return mLinkedList.add(o);
    }
    
    //----------------------------------------------------------------------
    public void add(int index, Object element)
    {
        mLinkedList.add(index, element);
    }
    
    //----------------------------------------------------------------------
    public boolean addAll(Collection c)
    {
        return mLinkedList.addAll(c);
    }
    
    //----------------------------------------------------------------------
    public boolean addAll(int index, Collection c)
    {
        return mLinkedList.addAll(index, c);
    }
    
    //----------------------------------------------------------------------
    public void clear()
    {
        mLinkedList.clear();
    }
    
    //----------------------------------------------------------------------
    public boolean contains(Object o)
    {
        return mLinkedList.contains(o);
    }
    
    //----------------------------------------------------------------------
    public boolean containsAll(Collection c)
    {
        return mLinkedList.containsAll(c);
    }
    
    //----------------------------------------------------------------------
    public Object get(int index) 
    {
        return mLinkedList.get(index);
    }
    
    //----------------------------------------------------------------------
    public int indexOf(Object o) 
    {
        return mLinkedList.indexOf(o);
    }
    
    //----------------------------------------------------------------------
    public boolean isEmpty() 
    {
        return mLinkedList.isEmpty();
    }
    
    //----------------------------------------------------------------------
    public Iterator iterator() 
    {
        return mLinkedList.iterator();
    }
    
    //----------------------------------------------------------------------
    public int lastIndexOf(Object o) 
    {
        return mLinkedList.lastIndexOf(o);
    }
    
    //----------------------------------------------------------------------
    public ListIterator listIterator() 
    {
        return mLinkedList.listIterator();
    }
    
    //----------------------------------------------------------------------
    public ListIterator listIterator(int index) 
    {
        return mLinkedList.listIterator(index);
    }
    
    //----------------------------------------------------------------------
    public boolean remove(Object o) 
    {
        return mLinkedList.remove(o);
    }
    
    //----------------------------------------------------------------------
    public Object remove(int index) 
    {
        return mLinkedList.remove(index);
    }
    
    //----------------------------------------------------------------------
    public boolean removeAll(Collection c) 
    {
        return mLinkedList.removeAll(c);
    }
    
    //----------------------------------------------------------------------
    public boolean retainAll(Collection c) 
    {
        return mLinkedList.retainAll(c);
    }
    
    //----------------------------------------------------------------------
    public Object set(int index, Object element) 
    {
        return mLinkedList.set(index, element);
    }
    
    //----------------------------------------------------------------------
    public int size() 
    {
        return mLinkedList.size();
    }
    
    //----------------------------------------------------------------------
    public List subList(int fromIndex, int toIndex) 
    {
        return mLinkedList.subList(fromIndex, toIndex);
    }
    
    //----------------------------------------------------------------------
    public Object[] toArray() 
    {
        return mLinkedList.toArray();
    }
    
    //----------------------------------------------------------------------
    public Object[] toArray(Object[] a) 
    {
        return mLinkedList.toArray(a);
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
        return mLinkedList.hashCode();
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object anObject) 
    {
        return mLinkedList.equals(anObject);
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public String toString() 
    {
        return mLinkedList.toString();
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public Object clone() throws CloneNotSupportedException
    {
        RegularDList clone = (RegularDList)super.clone();
        clone.mLinkedList = (LinkedList)mLinkedList.clone();
        return clone;
    }
    
    //----------------------------------------------------------------------
    /** 
     * Creates a new <code>DList</code> object that contains the contents of this
     * <code>DList</code> object concatenated
     * with the contents of the <code>otherList</code> object.
     *
     * @param anOtherList the list whose elements are placed at the end of the list
     *  returned by this method.
     *
     * @return a new <code>DList</code> that is the concatenation of this list and
     * the list referenced by <code>anOtherList</code>.
     */
    public DList concat(DList anOtherList)
    {
        try {
            DList newList = (DList)this.clone();
            newList.addAll(anOtherList);
            return newList;
        }
        catch (CloneNotSupportedException e) {
            // Should happen...
            throw new RuntimeException(e);
        }
    }
    
    //----------------------------------------------------------------------
    // ...End of Interfaces: org.odmg.DList, java.util.Collection, java.util.List.
    //----------------------------------------------------------------------
}


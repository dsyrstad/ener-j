// Ener-J
// Copyright 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/RegularDBag.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.core;

import java.util.*;

import org.odmg.*;
import org.enerj.annotations.Persist;

/**
 * Ener-J implementation of org.odmg.DBag which supports persistable bags
 * as first-class objects (FCOs). This type of bag is useful when the bag itself can
 * fit entirely in memory at one time.  If you have an bag that cannot fit
 * reasonably in memory, you will have more than 2 billion objects in your collection,
 * or you want to conserve disk storage space, should use VeryLargeDArray.
 * <p>
 * The bag is implemented as a container of java.util.Collection. However,
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
 * @version $Id: RegularDBag.java,v 1.3 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.DBag
 * @see VeryLargeDArray
 */
@Persist
public class RegularDBag implements org.odmg.DBag, Cloneable
{
    /** The delegate bag. This is treated as an SCO when this FCO is persisted. */
    private Collection mDelegateBag;
    
    //----------------------------------------------------------------------
    /**
     * Constructs a RegularDBag backed by the given Collection. Changes to this DBag
     * are reflected in the delegate, and vice versa.
     *
     * @param aCollection the delegate "bag".
     */
    public RegularDBag(Collection aCollection)
    {
        mDelegateBag = aCollection;
    }

    //----------------------------------------------------------------------
    /**
     * Constructs an empty RegularDBag with the specified initial capacity. 
     *
     * @param anInitialCapacity the initial capacity of the bag. This does not 
     *  affect the bag size().
     */
    public RegularDBag(int anInitialCapacity)
    {
        mDelegateBag = new ArrayList(anInitialCapacity);
    }

    //----------------------------------------------------------------------
    /**
     * Constructs an empty RegularDBag with an initial capacity of 30. 
     */
    public RegularDBag()
    {
        this(30);
    }

    //----------------------------------------------------------------------
    // Start of Interfaces: org.odmg.DBag, java.util.Collection
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    public boolean add(Object o)
    {
        return mDelegateBag.add(o);
    }
    
    //----------------------------------------------------------------------
    public boolean addAll(Collection c)
    {
        return mDelegateBag.addAll(c);
    }
    
    //----------------------------------------------------------------------
    public void clear()
    {
        mDelegateBag.clear();
    }
    
    //----------------------------------------------------------------------
    public boolean contains(Object o)
    {
        return mDelegateBag.contains(o);
    }
    
    //----------------------------------------------------------------------
    public boolean containsAll(Collection c)
    {
        return mDelegateBag.containsAll(c);
    }
    
    //----------------------------------------------------------------------
    public boolean isEmpty() 
    {
        return mDelegateBag.isEmpty();
    }
    
    //----------------------------------------------------------------------
    public Iterator iterator() 
    {
        return mDelegateBag.iterator();
    }
    
    //----------------------------------------------------------------------
    public boolean remove(Object o) 
    {
        return mDelegateBag.remove(o);
    }
    
    //----------------------------------------------------------------------
    public boolean removeAll(Collection c) 
    {
        return mDelegateBag.removeAll(c);
    }
    
    //----------------------------------------------------------------------
    public boolean retainAll(Collection c) 
    {
        return mDelegateBag.retainAll(c);
    }
    
    //----------------------------------------------------------------------
    public int size() 
    {
        return mDelegateBag.size();
    }
    
    //----------------------------------------------------------------------
    public Object[] toArray() 
    {
        return mDelegateBag.toArray();
    }
    
    //----------------------------------------------------------------------
    public Object[] toArray(Object[] a) 
    {
        return mDelegateBag.toArray(a);
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
        return mDelegateBag.hashCode();
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object anObject) 
    {
        return mDelegateBag.equals(anObject);
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public String toString() 
    {
        return mDelegateBag.toString();
    }

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public Object clone() throws CloneNotSupportedException
    {
        RegularDBag clone = (RegularDBag)super.clone();
        Collection bag = new ArrayList( mDelegateBag.size() );
        bag.addAll(mDelegateBag);
        clone.mDelegateBag = bag;
        return clone;
    }
    
    //----------------------------------------------------------------------
    /** 
     * A new <code>DBag</code> instance is created that contains the difference of
     * this object and <code>anOtherBag</code>.
     * This method is similar to the <code>removeAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>removeAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag the other bag to use in creating the difference.
     *
     * @return a <code>DBag</code> instance that contains the elements of this object
     * minus the elements in <code>anOtherBag</code>.
     */
    public DBag difference(DBag anOtherBag)
    {
        return difference((Collection)anOtherBag);
    }
    
    //----------------------------------------------------------------------
    /** 
     * A new <code>DBag</code> instance is created that contains the difference of
     * this object and <code>anOtherBag</code>.
     * This method is similar to the <code>removeAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>removeAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag the other Collection ("bag") to use in creating the difference.
     *
     * @return a <code>DBag</code> instance that contains the elements of this object
     *  minus the elements in <code>otherBag</code>.
     */
    public DBag difference(Collection anOtherBag) 
    {
        int bag1Size = size();
        int bag2Size = anOtherBag.size();
        // Worst case: all items from both bags will be in the result.
        DBag result = new RegularDBag(bag1Size + bag2Size);
        
        Iterator iterator1 = this.iterator();
        while (iterator1.hasNext()) {
            Object obj = iterator1.next();
            if ( !anOtherBag.contains(obj) ) {
                result.add(obj);
            }
        }
        
        return result;
    }
    
    //----------------------------------------------------------------------
    /** 
     * A new <code>DBag</code> instance is created that contains the intersection of
     * this object and <code>anOtherBag</code>.
     * This method is similar to the <code>retainAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>retainAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag the other bag to use in creating the intersection.
     *
     * @return a <code>DBag</code> instance that contains the intersection of this
     * object and <code>anOtherBag</code>.
     */
    public DBag intersection(DBag anOtherBag)
    {
        return intersection((Collection)anOtherBag);
    }
    
    //----------------------------------------------------------------------
    /** 
     * A new <code>DBag</code> instance is created that contains the intersection of
     * this object and <code>anOtherBag</code>.
     * This method is similar to the <code>retainAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>retainAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag the other Collection ("bag") to use in creating the intersection.
     *
     * @return a <code>DBag</code> instance that contains the intersection of this
     * object and <code>otherBag</code>.
     */
    public DBag intersection(Collection anOtherBag)
    {
        int bag1Size = size();
        int bag2Size = anOtherBag.size();
        // Worst case: all items from both bags will be in the result.
        DBag result = new RegularDBag(bag1Size + bag2Size);
        
        Iterator iterator1;
        Collection bag2;
        // Iterate over the smaller bag
        if (bag1Size > bag2Size) {
            iterator1 = anOtherBag.iterator();
            bag2 = this;
        }
        else {
            iterator1 = this.iterator();
            bag2 = anOtherBag;
        }

        while (iterator1.hasNext()) {
            Object obj = iterator1.next();
            if ( anOtherBag.contains(obj) ) {
                result.add(obj);
            }
        }
        
        return result;
    }
    
    //----------------------------------------------------------------------
    /** 
     * A new <code>DBag</code> instance is created that is the union of this object
     * and <code>anOtherBag</code>.
     * This method is similar to the <code>addAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>addAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag The other bag to use in the union operation.
     * @return a <code>DBag</code> instance that contains the union of this object
     * and <code>otherBag</code>.
     */
    public DBag union(DBag anOtherBag)
    {
        return union((Collection)anOtherBag);
    }
    
    //----------------------------------------------------------------------
    /** 
     * A new <code>DBag</code> instance is created that is the union of this object
     * and <code>anOtherBag</code>.
     * This method is similar to the <code>addAll</code> method in <code>Collection</code>,
     * except that this method creates a new collection and <code>addAll</code>
     * modifies the object to contain the result.
     *
     * @param anOtherBag The other Collection ("bag") to use in the union operation.
     * 
     * @return a <code>DBag</code> instance that contains the union of this object
     * and <code>anOtherBag</code>.
     */
    public DBag union(Collection anOtherBag) 
    {
        int bag1Size = size();
        int bag2Size = anOtherBag.size();
        // All items from both bags will be in the result.
        DBag result = new RegularDBag(bag1Size + bag2Size);
        
        result.addAll(this);
        result.addAll(anOtherBag);
        return result;
    }
    
    //----------------------------------------------------------------------
    /** 
     * This method returns the number of occurrences of the object <code>obj</code>
     * in the <code>DBag</code> collection. Occurance comparisons are based on equals(),
     * unless anObject is null, in which case item == null is used.
     *
     * @param anObject the value that may have elements in the collection. May be null
     *  to count the number of null items in the bag.
     *
     * @return The number of occurrences of <code>obj</code> in this collection.
     */
    public int occurrences(Object anObject) 
    {
        int count = 0;
        if (anObject == null) {
            for (Object obj : mDelegateBag) {
                if (obj == null) {
                    ++count;
                }
            }
        }
        else {
            for (Object obj: mDelegateBag) {
                if (anObject.equals(obj) ) {
                    ++count;
                }
            }
        }
        
        return count;
    }
    
    //----------------------------------------------------------------------
    // ...End of Interfaces: org.odmg.DBag, java.util.Collection, java.util.List.
    //----------------------------------------------------------------------
}


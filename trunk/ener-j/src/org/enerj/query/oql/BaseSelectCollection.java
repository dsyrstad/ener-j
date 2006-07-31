//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/BaseSelectCollection.java,v 1.2 2005/11/21 02:06:47 dsyrstad Exp $

package org.enerj.query.oql;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Base Collection for OQL select statements. <p>
 * 
 * @version $Id: BaseSelectCollection.java,v 1.2 2005/11/21 02:06:47 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
abstract public class BaseSelectCollection implements Collection
{
    //--------------------------------------------------------------------------------
    /**
     * Construct a BaseSelectCollection that is unfiltered. 
     */
    protected BaseSelectCollection()
    {
    }
    
    //--------------------------------------------------------------------------------
    // Collection Interface...
    //--------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------
    public boolean add(Object anObject)
    {
        throw new UnsupportedOperationException("Collection is immutable");
    }

    //--------------------------------------------------------------------------------
    public boolean addAll(Collection aCollection)
    {
        throw new UnsupportedOperationException("Collection is immutable");
    }

    //--------------------------------------------------------------------------------
    public void clear()
    {
        throw new UnsupportedOperationException("Collection is immutable");
    }

    //--------------------------------------------------------------------------------
    public boolean contains(Object anObject)
    {
        for (Object target : this) {
            if (target == null && anObject == null) {
                return true;
            }
            
            if (target == null || anObject == null) {
                return false;
            }
            
            if (target.equals(anObject)) {
                return true;
            }
        }
        
        return false;
    }

    //--------------------------------------------------------------------------------
    public boolean containsAll(Collection aCollection)
    {
        for (Object target : aCollection) {
            if (!contains(target)) {
                return false;
            }
        }
        
        return true;
    }

    //--------------------------------------------------------------------------------
    public boolean isEmpty()
    {
        return !iterator().hasNext();
    }

    //--------------------------------------------------------------------------------
    public boolean remove(Object anO)
    {
        throw new UnsupportedOperationException("Collection is immutable");
    }

    //--------------------------------------------------------------------------------
    public boolean removeAll(Collection anC)
    {
        throw new UnsupportedOperationException("Collection is immutable");
    }

    //--------------------------------------------------------------------------------
    public boolean retainAll(Collection anC)
    {
        throw new UnsupportedOperationException("Collection is immutable");
    }

    //--------------------------------------------------------------------------------
    public Object[] toArray()
    {
        Collection coll = new ArrayList();
        coll.addAll(this); // Filters
        return coll.toArray();
    }

    //--------------------------------------------------------------------------------
    public Object[] toArray(Object[] a)
    {
        Collection coll = new ArrayList();
        coll.addAll(this); // Filters
        return coll.toArray(a);
    }

    //--------------------------------------------------------------------------------
    // ...Collection Interface.
    //--------------------------------------------------------------------------------

}

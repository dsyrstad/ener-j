/*
 * 
 */
package org.enerj.server.bdb;

import java.util.Comparator;

import org.enerj.core.GenericKey;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.enerj.core.Persister;
import org.enerj.core.PersisterRegistry;


/**
 * BDB Comparator for GenericKey in indexes.
 * NOTE: This is constructed by BDB, it cannot have any state.
 * 
 * @author Dan Syrstad
 */
public class GenericKeyBDBComparator implements Comparator
{
    /**
     * Construct a new GenericKeyBDBComparator.
     */
    public GenericKeyBDBComparator()
    {
    }

    /**
     * {@inheritDoc}
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2)
    {
        byte[] b1 = (byte[])o1;
        byte[] b2 = (byte[])o2;
        
        Persister persister = PersisterRegistry.getCurrentPersisterForThread();
        GenericKey key1 = new GenericKey();
        PersistableHelper.loadSerializedImage(persister, (Persistable)key1, b1);
        GenericKey key2 = new GenericKey();
        PersistableHelper.loadSerializedImage(persister, (Persistable)key2, b2);
        
        return key1.compareTo(key2);
    }

}

/*
 * 
 */
package org.enerj.server.bdb;

import java.util.Collections;
import java.util.Set;

import org.enerj.core.GenericKey;
import org.enerj.core.IndexSchema;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.enerj.core.Persister;
import org.enerj.core.PersisterRegistry;

import com.sleepycatje.je.DatabaseEntry;
import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.SecondaryDatabase;
import com.sleepycatje.je.SecondaryKeyCreator;


/**
 * A SecondaryKeyCreator that generates a GenericKey for BDB JE.
 * 
 * @author Dan Syrstad
 */
class BDBJEKeyCreator implements SecondaryKeyCreator
{
    // This set is synchronized.
    private Set<Integer> validCIDXs;
    private IndexSchema indexSchema;

    /**
     * Construct a new BDBJEKeyCreator.
     *
     */
    public BDBJEKeyCreator(Set<Integer> validCIDXs, IndexSchema indexSchema)
    {
        this.validCIDXs = Collections.synchronizedSet(validCIDXs);
        this.indexSchema = indexSchema;
    }
    
    /**
     * Adds a valid class index for this index's key creator.
     *
     * @param cidx the new Class Index to be added.
     */
    void addValidClassIndex(int cidx)
    {
        validCIDXs.add(cidx);
    }

    /**
     * {@inheritDoc}
     * @see com.sleepycatje.je.SecondaryKeyCreator#createSecondaryKey(com.sleepycatje.je.SecondaryDatabase, com.sleepycatje.je.DatabaseEntry, com.sleepycatje.je.DatabaseEntry, com.sleepycatje.je.DatabaseEntry)
     */
    //@Override
    public boolean createSecondaryKey(SecondaryDatabase secondary, DatabaseEntry key, DatabaseEntry data,
        DatabaseEntry result) throws DatabaseException
    {
        OIDKeyTupleBinding binding = new OIDKeyTupleBinding(true);
        OIDKey oidKey = (OIDKey)binding.entryToObject(key);
        if (!validCIDXs.contains(oidKey.cidx)) {
            // Object not for this class - don't add to index.
            return false;
        }
        
        Persister persister = PersisterRegistry.getCurrentPersisterForThread();
        Persistable obj = persister.getObjectForOID(oidKey.getOID());
        PersistableHelper.loadSerializedImage(persister, obj, data.getData());
        Object idxKey = GenericKey.createKey(indexSchema, obj);
        byte[] keyBytes = PersistableHelper.createSerializedImage((Persistable)idxKey);
        result.setData(keyBytes);

        return true;
    }

}

/*
 * 
 */
package org.enerj.server.bdb;

import org.enerj.core.ClassSchema;
import org.enerj.core.IndexSchema;

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
    private ClassSchema classSchema;
    private IndexSchema indexSchema;

    /**
     * Construct a new BDBJEKeyCreator.
     *
     */
    public BDBJEKeyCreator(ClassSchema classSchema, IndexSchema indexSchema)
    {
        this.classSchema = classSchema;
        this.indexSchema = indexSchema;
    }

    /**
     * {@inheritDoc}
     * @see com.sleepycatje.je.SecondaryKeyCreator#createSecondaryKey(com.sleepycatje.je.SecondaryDatabase, com.sleepycatje.je.DatabaseEntry, com.sleepycatje.je.DatabaseEntry, com.sleepycatje.je.DatabaseEntry)
     */
    @Override
    public boolean createSecondaryKey(SecondaryDatabase secondary, DatabaseEntry key, DatabaseEntry data,
        DatabaseEntry result) throws DatabaseException
    {
        // TODO Auto-generated method stub
        return false;
    }

}

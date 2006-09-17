// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/EnerJDatabase.java,v 1.18 2006/05/31 01:58:22 dsyrstad Exp $

package org.enerj.core;

import static org.enerj.server.MetaObjectServer.ENERJ_ACCESS_MODE_PROP;
import static org.enerj.server.MetaObjectServer.ENERJ_DBNAME_PROP;
import static org.enerj.server.MetaObjectServer.ENERJ_HOSTNAME_PROP;
import static org.enerj.server.MetaObjectServer.ENERJ_PASSWORD_PROP;
import static org.enerj.server.MetaObjectServer.ENERJ_PORT_PROP;
import static org.enerj.server.MetaObjectServer.ENERJ_USERNAME_PROP;
import gnu.trove.TLongHashSet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import org.odmg.ClassNotPersistenceCapableException;
import org.odmg.Database;
import org.odmg.DatabaseClosedException;
import org.odmg.DatabaseOpenException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.ObjectNameNotUniqueException;
import org.odmg.TransactionInProgressException;
import org.odmg.TransactionNotInProgressException;
import org.enerj.annotations.SchemaAnnotation;
import org.enerj.server.DefaultMetaObjectServer;
import org.enerj.server.MetaObjectServer;
import org.enerj.server.MetaObjectServerSession;
import org.enerj.server.ObjectServer;
import org.enerj.server.PluginHelper;
import org.enerj.server.SerializedObject;
import org.enerj.util.ClassUtil;
import org.enerj.util.URIUtil;

/**
 * Ener-J implementation of org.odmg.Database.
 *
 * @version $Id: EnerJDatabase.java,v 1.18 2006/05/31 01:58:22 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see org.odmg.Database
 */
public class EnerJDatabase implements Database
{
    private static final Class[] sEnerJDatabaseArgType = { EnerJDatabase.class };
    private static final Object[] sEnerJDatabaseArg = { null };

    /** If true, EnerJDatabase.open() has been performed and you are in the client's JVM. */
    private static boolean sIsThisTheClientJVM = false;
    
    /** Current Open Database for JVM. Used when no curent open thread database exists. (i.e.,
     * Database was opened in a thread, but now the thread is gone). Entry exists until
     * the database is closed, even if the app has no reference to it.
     */
    private static EnerJDatabase sCurrentProcessDatabase = null;

    /** HashMap keyed by Thread object. Value a EnerJDatabase. Entries are added when 
     * a database is opened. Entries are removed when a database is closed.
     */
    private static IdentityHashMap sCurrentThreadDatabaseMap = new IdentityHashMap();

    /** Client-side object cache. */
    private ClientCache mClientCache = null;
    
    /** Cache of CIDs known to be in the database. Used so that we can avoid
     * grabbing a DatabaseRoot and read-locking it. */
    private TLongHashSet mKnownSchemaCIDs = new TLongHashSet(127); 

    /** True if the database is open. */
    private boolean mIsOpen = false;
    
    /** Current transaction that this database is bound to. */
    private EnerJTransaction mBoundToTransaction = null;
    
    /** True if this instance allows non-transactional reads. */
    private boolean mAllowNontransactionalReads = false;
    
    /** MetaObjectServerSession we're bound to. */
    private MetaObjectServerSession mMetaObjectServerSession = null;

    /** Streams/Context used to serialize an object to bytes. */
    private ByteArrayOutputStream mByteOutputStream = new ByteArrayOutputStream(1000);
    private DataOutputStream mDataOutput = new DataOutputStream(mByteOutputStream);
    private ObjectSerializer.WriteContext mWriteContext = new ObjectSerializer.WriteContext(mDataOutput);
    
    /** 
     * Stream/Context used to unserialize bytes to an object. We need a pool of them
     * because unserialization can be lightly recursive. This is a LinkedList of 
     * of DBByteArrayInputStream. Entries in this pool are available for use.
     * while an entry is being used, it is removed from the pool. This pool is
     * not used by more than one thread at a time.
     */
    private LinkedList mInputStreamPool = new LinkedList();
    
    /** Cache of new OIDs to be used. Only available during a transaction. */
    private long[] mOIDCache = null;
    private int mOIDCachePosition = 0;
    private boolean mIsServerSideDB = false;

    //----------------------------------------------------------------------
    /**
     * Construct a unopened EnerJDatabase.
     */
    public EnerJDatabase()
    {
        init();
    }
    
    //----------------------------------------------------------------------
    /**
     * Ener-J Server use only. Construct a EnerJDatabase that is connected to
     * an opened session and MetaObjectServer. Exists primarly for servers
     * that want to use the API and participate in the client's transaction.
     *
     * @param aSession a MetaObjectServerSession.
     * @param aServer a MetaObjectServer.
     */
    public EnerJDatabase(MetaObjectServerSession aSession, MetaObjectServer aServer)
    {
        mMetaObjectServerSession = aSession;
        mIsServerSideDB = true;
        init();
        initOpenDatabase();
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Common constructor initialization. 
     */
    private void init()
    {
        mClientCache = new DefaultClientCache(5000);

        // Initialize CID map with known system CIDs.
        // This is so we don't try to update the schema with system CIDs, since they
        // must exist to update it. During DB creation, checking for these
        // in the schema will cause all sorts of problems. The Schema class
        // takes care of adding the system classes to the schema.
        Iterator<Long> iter = SystemCIDMap.getSystemCIDs();
        while (iter.hasNext()) {
            mKnownSchemaCIDs.add( iter.next() );
        }
    }
    
    //----------------------------------------------------------------------
    // Start of truly public EnerJ-specific methods...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * Determines whether this is the JVM used by the client. Primarily used
     * by servers to determine if they're running locally.
     *
     * @return true if this is the client JVM (i.e., EnerJDatabse.open() has been performed).
     */
    public static boolean isThisTheClientJVM()
    {
        return sIsThisTheClientJVM;
    }
    
    //----------------------------------------------------------------------
    /** 
     * Gets the current open database for the process. This is initially set to
     * the first database opened by the process. When this database is closed, it
     * is set null. When the next database is opened and this is null, it is set to 
     * the newly opened database.
     *
     * @return a EnerJDatabase, or null if the process does not have a current database.
     */
    public static EnerJDatabase getCurrentDatabaseForProcess()
    {
        return sCurrentProcessDatabase;
    }
    
    //----------------------------------------------------------------------
    /** 
     * Gets the current open database for the caller's thread. This is initially set to
     * the first database opened by the thread. When this database is closed, it
     * is set null. When the next database is opened and this is null, it is set to 
     * the newly opened database.
     *
     * @return a EnerJDatabase, or null if the caller's thread does not have a current database.
     */
    public static EnerJDatabase getCurrentDatabaseForThread()
    {
        return (EnerJDatabase)sCurrentThreadDatabaseMap.get( Thread.currentThread() );
    }

    //----------------------------------------------------------------------
    /** 
     * Gets the current open database. This method attempts to get the current
     * database via getCurrentDatabaseForThread(). Failing that, getCurrentDatabaseForProcess
     * is called.
     *
     * @return a EnerJDatabase, or null if there is no current database.
     */
    public static EnerJDatabase getCurrentDatabase()
    {
        EnerJDatabase db = getCurrentDatabaseForThread();
        if (db == null) {
            db = getCurrentDatabaseForProcess();
        }
        
        return db;
    }
    
    //----------------------------------------------------------------------
    /**
     * Determines if this database is open.
     *
     * @return true if it is open, false if it is closed.
     */
    public boolean isOpen()
    {
        return mIsOpen;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the database root for the database. Must be called 
     * from within a transaction.
     *
     * @return the DatabaseRoot.
     */
    public DatabaseRoot getDatabaseRoot()
    {
        // The following method calls checkBoundTransaction().
        return (DatabaseRoot)getObjectForOID(ObjectServer.DATABASE_ROOT_OID);
    }
    
    //----------------------------------------------------------------------
    // ... End of truly public EnerJ-specific methods.
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * Loads the contents of aPersistable from a serialized image. 
     * Assumes checkBoundTransaction() has already been called.
     *
     * @param aPersistable the persistable to be loaded.
     * @param anImage the serialized image of the persistable.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public void loadSerializedImage(Persistable aPersistable, byte[] anImage)
    {
        // A Database can't be shared between threads at the same time, checkBoundTransaction enforces this.
        // However, enerj_ReadObject can cause getObjectForOID to get called again (due to an FCO referencing 
        // another FCO, which may cause this
        // method to be invoked again recursively to load schema objects (but NOT the referenced FCO). So
        // we use a pool of InputStreams/ReadContexts (DBByteArrayInputStream). These are reused
        // so we don't create tons of objects. This pool shouldn't normally get larger than a couple
        // of entries.
        DBByteArrayInputStream byteInputStream;
        ObjectSerializer.ReadContext readContext;
        if (mInputStreamPool.isEmpty()) {
            // Create a new pool entry.
            byteInputStream = new DBByteArrayInputStream();
            readContext = new ObjectSerializer.ReadContext( new DataInputStream(byteInputStream) );
            byteInputStream.setReadContext(readContext);
        }
        else {
            byteInputStream = (DBByteArrayInputStream)mInputStreamPool.removeFirst();
            readContext = byteInputStream.getReadContext();
        }
        
        try {
            readContext.reset();
            byteInputStream.setByteArray(anImage);
            aPersistable.enerj_ReadObject(readContext);

            aPersistable.enerj_SetDatabase(this);
            aPersistable.enerj_SetLoaded(true);
            aPersistable.enerj_SetModified(false);
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Error loading object for OID " + getOID(aPersistable), e);
        }
        finally {
            // Put the stream back into the pool
            mInputStreamPool.add(byteInputStream);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Loads the contents of aPersistable from the database. 
     * <p>
     * This method is intended only for Ener-J internal use.
     *
     * @param aPersistable the persistable to be loaded.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public void loadObject(Persistable aPersistable)
    {
        checkBoundTransaction(true);
        long oid = getOID(aPersistable);
        
        if ( !isNontransactionalReadMode()) {
            // getObjectForOID should have READ-locked this object. Make sure we have the lock.
            EnerJTransaction.isAtLockLevel(aPersistable, EnerJTransaction.READ);
        }

        // Look it up in the DB.
        byte[] objectBytes;
        try {
            objectBytes = mMetaObjectServerSession.loadObject(oid);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Could not load object", e);
        }

        loadSerializedImage(aPersistable, objectBytes);
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the Persistable object associated with anOID and aCID.
     * The Persistable returned is hollow (not loaded yet).
     *
     * @param anOID the database Object ID.
     * @param aCID the CID of anOID. If this is ObjectServer.NULL_CID, the CID is
     *  found by its OID from the server.
     *
     * @return a hollow Persistable. Returns null if the OID doesn't exist.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    private Persistable createObjectForOIDAndCID(long anOID, long aCID)
    {
        checkBoundTransaction(true);

        Persistable persistable;
        // Note: If mIsServerSideDB is true, the cache will be empty.
        persistable = (Persistable)mClientCache.get(anOID);
        if (persistable != null) {
            if (isNontransactionalReadMode()) {
                // If we're in non-transactional read mode and this object was
                // never loaded, make sure that we can load it later by setting it
                // to be non-transactional.
                PersistableHelper.setNonTransactional(persistable);
            }

            return persistable;
        }
        
        // Lookup class for OID in the DB.
        if (!isNontransactionalReadMode()) {
            // We must have a READ lock (minimum) on OID at this point - get a READ lock if we don't have it.
            // Do read lock _before_ getting class id because the class id can change for an OID.
            // Note: We don't use Transaction.lock() here because we don't have an object to work with yet.
            //  TODO  -1L waits forever, use database or transaction configurable timeout
            mMetaObjectServerSession.getLock(anOID, EnerJTransaction.READ,  -1L);
        }

        if (aCID == ObjectServer.NULL_CID) {
            try {
                aCID = mMetaObjectServerSession.getCIDForOID(anOID);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ODMGRuntimeException("Could not get CID for OID " + anOID, e);
            }

            if (aCID == ObjectServer.NULL_CID) {
                throw new ODMGRuntimeException("Cannot find CID for OID " + anOID + " in database");
            }
        }

        // Is it a system persistable?
        String className = SystemCIDMap.getSystemClassNameForCID(aCID);
        if (className == null) {
            // Nope. Try to look it up in the schema.
            // TODO Maybe this could come back with the getCIDForOID call? 
            ClassVersionSchema classVersion = getDatabaseRoot().getSchema().findClassVersion(aCID);
            if (classVersion == null) {
                throw new ODMGRuntimeException("Cannot find class for CID " + aCID);
            }

            className = classVersion.getLogicalClassSchema().getClassName();
        }        

        Class oidClass;
        try {
            // TODO load enhanced class.
            oidClass = Class.forName(className);
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Cannot find class " + className + " for OID " + anOID);
        }
        
        // Create a hollow (non-loaded) object. PersistableHelper.checkLoaded() will
        // actually load the contents (via EnerJDatabase.loadObject) when a field is accessed.
        try {
            Constructor constructor = oidClass.getDeclaredConstructor(sEnerJDatabaseArgType);
            constructor.setAccessible(true);
            persistable = (Persistable)constructor.newInstance(sEnerJDatabaseArg);

            persistable.enerj_SetDatabase(this);
            persistable.enerj_SetPrivateOID(anOID);
            if (isNontransactionalReadMode()) {
                PersistableHelper.setNonTransactional(persistable);
            }
            else {
                // Lock level was set already on OID above.
                persistable.enerj_SetLockLevel(EnerJTransaction.READ);
            }
            
            persistable.enerj_SetNew(false);
            persistable.enerj_SetModified(false);
            persistable.enerj_SetLoaded(false);

            // Cache it
            mClientCache.add(anOID, persistable);
        }
        catch (Exception e) {
            throw new org.odmg.ODMGRuntimeException("Error creating object: " + e);
        }

        return persistable;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets whether this database instance allows non-transactional (dirty) reads.  
     *
     * @param isNontransactional true if non-transactional reads are allowed, other false if
     *  reads must occur within a transaction.
     *  
     * @throws ODMGException if an error occurs.
     */
    public void setAllowNontransactionalReads(boolean isNontransactional) throws ODMGException
    {
        if (!mIsOpen) {
            throw new DatabaseClosedException("Database has not been opened");
        }
        
        if (!mIsServerSideDB) {
            mMetaObjectServerSession.setAllowNontransactionalReads(isNontransactional);
        }
        
        mAllowNontransactionalReads = isNontransactional;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Determines whether this database instance allows non-transactional (dirty) reads.  
     *
     * @return true if non-transactional reads are allowed, other false if
     *  reads must occur within a transaction.
     *  
     * @throws ODMGException if an error occurs.
     */
    public boolean getAllowNontransactionalReads() throws ODMGException
    {
        return mAllowNontransactionalReads;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the Persistable object associated with anOID.
     * <p>
     * This method is intended only for Ener-J internal use.
     *
     * @param anOID the database Object ID.
     *
     * @return a Persistable. Returns null if the OID doesn't exist.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public Persistable getObjectForOID(long anOID)
    {
        return createObjectForOIDAndCID(anOID, ObjectServer.NULL_CID);
    }

    //----------------------------------------------------------------------
    /**
     * Creates a loaded Persistable object associated based on aSerializedObject.
     * <p>
     * This method is intended only for Ener-J internal use.
     *
     * @param aSerializedObject the serialized image of the object.
     *
     * @return a loaded Persistable.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public Persistable createPersistable(SerializedObject aSerializedObject)
    {
        Persistable persistable = (Persistable)createObjectForOIDAndCID(aSerializedObject.getOID(), aSerializedObject.getCID() );
        loadSerializedImage(persistable, aSerializedObject.getImage());
        return persistable;
    }

    //----------------------------------------------------------------------
    /**
     * Creates a serialized image of the object. Assumes checkBoundTransaction
     * has already been called. Note that this can cause new objects to be added 
     * to the transaction's modified list.
     *
     * @param aPersistable a persistable object.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    byte[] createSerializedImage(Persistable aPersistable)
    {
        // A Database can't be shared between threads at the same time, checkBoundTransaction enforces this.
        // So it is ok to reuse mByteOutputStream and mDataOutput.
        try {
            mByteOutputStream.reset();
            mWriteContext.reset();
            aPersistable.enerj_WriteObject(mWriteContext);
            mDataOutput.flush();
        }
        catch (IOException e) {
            throw new org.odmg.ODMGRuntimeException("Error writing object: " + e);
        }
        
        return mByteOutputStream.toByteArray();
    }
    
    //----------------------------------------------------------------------
    /**
     * Store a Persistable object whose modified or new flags are set to true. 
     * Afterwards, the object's new and modified flags are set to
     * false. The loaded flag is set to true (in case the object was new). Also ensures
     * that the object is WRITE-locked. 
     * <p>
     * This method is intended only for Ener-J internal use.
     *
     * @param aPersistable a persistable object.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public void storePersistable(Persistable aPersistable)
    {
        checkBoundTransaction();
        
        if (!aPersistable.enerj_IsModified() && !aPersistable.enerj_IsNew()) {
            return;
        }

        long cid = aPersistable.enerj_GetClassId();
        long oid = getOID(aPersistable);

        //Logger.global.finest("Storing object: " + aPersistable.getClass().getName() + " oid=" + oid + " cid=" + cid);

        // Make sure we have a write-lock.
        mBoundToTransaction.lock(aPersistable, EnerJTransaction.WRITE);
        
        // Error if object is not loaded or not new at this point.
        if ( !aPersistable.enerj_IsLoaded() && !aPersistable.enerj_IsNew()) {
            throw new ODMGRuntimeException("INTERNAL: Attempted to store a persistable object that is not loaded or not new. OID=" + oid + " CID=" + cid);
        }


        // Force database to be set if it currently null. If it's not null, it must match.
        Database currentDatabase = aPersistable.enerj_GetDatabase();
        if (currentDatabase != this) {
            if (currentDatabase == null) {
                aPersistable.enerj_SetDatabase(this);
            }
            else {
                throw new ODMGRuntimeException("A persistable object jumped between owner databases. OID=" + oid + " CID=" + cid);
            }
        }
        
        if (oid == ObjectServer.NULL_OID) {
            throw new ODMGRuntimeException("OID for object " + aPersistable.getClass() + " is null. CID=" + cid);
        }

        byte[] objectBytes = createSerializedImage(aPersistable);
        try {
            mMetaObjectServerSession.storeObject(cid, oid, objectBytes, aPersistable.enerj_IsNew() );
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Could not store object. OID=" + oid + " CID=" + cid, e);
        }

        // It's not modified or new anymore (to the client's view), but it is loaded.
        aPersistable.enerj_SetNew(false);
        aPersistable.enerj_SetModified(false);
        aPersistable.enerj_SetLoaded(true);
    }

    
    //--------------------------------------------------------------------------------
    /**
     * Updates the database with the schema for aPersistable, if necessary.
     *
     * @param aPersistable the persistable.
     */
    void updateSchema(Persistable aPersistable)
    {
        long cid = aPersistable.enerj_GetClassId();
        //Logger.global.warning("Checking schema for cid " + cid + ' ' + aPersistable.getClass());

        // Check if we already know that the database has this CID in the schema.
        if (mKnownSchemaCIDs.contains(cid)) {
            return;
        }
        
        DatabaseRoot root = getDatabaseRoot();
        Schema schema = root.getSchema();
        // Does it exist in the schema already?
        if (schema.findClassVersion(cid) == null) {
            // Not in schema, add it.
            String className = aPersistable.getClass().getName();

            //Logger.global.warning("Adding schema for cid " + cid + ' ' + aPersistable.getClass());
    
            try {
                // Add the logical class if it doesn't exist yet.
                LogicalClassSchema logicalClass = schema.findLogicalClass(className);
                if (logicalClass == null) {
                    logicalClass = new LogicalClassSchema(schema, className, "");
                    schema.addLogicalClass(logicalClass);
                }
        
                SchemaAnnotation schemaAnn = aPersistable.getClass().getAnnotation(SchemaAnnotation.class);
                if (schemaAnn == null) {
                    throw new ODMGRuntimeException("Cannot find SchemaAnnotation on " + aPersistable.getClass() + ". Class was not previously enhanced.");
                }
    
                String[] persistentFieldNames = schemaAnn.persistentFieldNames();
                String[] transientFieldNames = schemaAnn.transientFieldNames();
                String[] superTypeNames = ClassUtil.getAllSuperTypeNames(aPersistable.getClass());
                byte[] originalClassBytes = schemaAnn.originalByteCodes();
        
                ClassVersionSchema classVersion = 
                    new ClassVersionSchema(logicalClass, cid, superTypeNames, originalClassBytes,
                                null/*someEnhancedClassBytes*/, persistentFieldNames, transientFieldNames);
                logicalClass.addVersion(classVersion);
            }
            catch (ODMGException e) {
                throw new ODMGRuntimeException("Error adding new ClassVersionSchema", e);
            }
        }
        
        // Cache the fact that it is known in the schema.
        mKnownSchemaCIDs.add(cid);
    }
    
    //----------------------------------------------------------------------
    /**
     * Saves a serialized image of the Persistable in the cache.
     *
     * @param aPersistable the Persistable to be saved.
     */
    void savePersistableImage(Persistable aPersistable)
    {
        checkBoundTransaction();
        
        byte[] objectBytes = createSerializedImage(aPersistable);
        long oid = getOID(aPersistable);
        if (oid == ObjectServer.NULL_OID) {
            throw new org.odmg.ODMGRuntimeException("OID for object " + aPersistable.getClass() + " is null");
        }
        
        mClientCache.setSavedImage(oid, objectBytes);
    }
    
    //----------------------------------------------------------------------
    /**
     * Restores the serialized image previously saved by savePersistableImage and
     * clears the saved image from the cache.
     *
     * @param aPersistable the Persistable to be restored.
     */
    void restoreAndClearPersistableImage(Persistable aPersistable)
    {
        checkBoundTransaction();
        
        long oid = getOID(aPersistable);
        if (oid == ObjectServer.NULL_OID) {
            throw new org.odmg.ODMGRuntimeException("OID for object " + aPersistable.getClass() + " is null");
        }
        
        byte[] image = mClientCache.getAndClearSavedImage(oid);
        if (image != null) {
            loadSerializedImage(aPersistable, image);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Clears the serialized image previously saved by savePersistableImage.
     *
     * @param aPersistable the Persistable whose saved image will be cleared.
     */
    void clearPersistableImage(Persistable aPersistable)
    {
        checkBoundTransaction();
        
        long oid = getOID(aPersistable);
        if (oid == ObjectServer.NULL_OID) {
            throw new org.odmg.ODMGRuntimeException("OID for object " + aPersistable.getClass() + " is null");
        }
        
        mClientCache.setSavedImage(oid, null);
    }
    
    //----------------------------------------------------------------------
    /**
     * Evicts all cached objects from the local cache.
     */
    public void evictAll()
    {
        mClientCache.evictAll();
    }
    
    //----------------------------------------------------------------------
    /**
     * Evicts the specified cached object from the local cache.
     * 
     * @param anOID the OID to be evicted.
     */
    public void evict(long anOID)
    {
        mClientCache.evict(anOID);
    }
    
    //----------------------------------------------------------------------
    /** 
     * Gets the client-side cache for this database.
     *
     * @return a ClientCache.
     */
    ClientCache getClientCache()
    {
        return mClientCache;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the OID for a persistable object. All Ener-J 
     * code should call this method. Application code should use EnerJImplementation.getEnerJObjectId or
     * org.odmg.Implementation.getObjectId.
     * to get the OID. The OID for new and cloned persistable objects is lazily
     * initialized. A call to this method implies that a new or cloned object has been 
     * tied to the persistable object graph and hence an OID should be assigned to it.
     * <p>
     * Code must NOT call Persistable.enerj_GetPrivateOID.
     *
     * @param anObject an Object that is a Persistable (a FCO).
     *
     * @return an OID, or ObjectServer.NULL_OID if the object is not persistable, null, or
     *  somehow otherwise transient. ObjectServer.NULL_OID is also returned for new/cloned objects
     *  that have a ObjectServer.NULL_OID OID if a transaction is not active.
     */
    public long getOID(Object anObject)
    {
        if ( !(anObject instanceof Persistable)) {
            return ObjectServer.NULL_OID;
        }
        
        Persistable persistable = (Persistable)anObject;
        Database persistableDatabase = persistable.enerj_GetDatabase();
        
        // Get the real current transaction here - not the one associated with this
        // database. Its database should match this database if the object is not being used
        // out of context.
        EnerJTransaction currentTxn = EnerJTransaction.getCurrentTransaction();
        EnerJDatabase currentTxnDatabase = null;
        if (currentTxn != null) {
            currentTxnDatabase = currentTxn.getDatabase();
        }
        
        if (persistableDatabase != null && 
            (persistableDatabase != this || 
             (currentTxnDatabase != null && currentTxnDatabase != persistableDatabase)) ) {
            throw new ODMGRuntimeException("Persistable object does not belong to this database");
        }
        
        long oid = persistable.enerj_GetPrivateOID();
        if (oid == ObjectServer.NULL_OID && persistable.enerj_IsNew() && mBoundToTransaction != null) {
            oid = addNewPersistable(persistable);
        }
        
        return oid;
    }

    
    //--------------------------------------------------------------------------------
    /**
     * Gets the open transaction that is bound to this database.
     *
     * @return the EnerJTransaction, or null if no transaction is bound. 
     */
    public EnerJTransaction getTransaction()
    {
        return mBoundToTransaction;
    }
    
    //----------------------------------------------------------------------
    /**
     * Sets the current transaction for the database.
     *
     * @param aTransaction a EnerJTransaction. This may be null to clear the current
     *  transaction.
     *
     * @throws TransactionInProgressException if a transaction is already bound
     *  to this database.
     */
    void setTransaction(EnerJTransaction aTransaction)
    {
        if (mBoundToTransaction != null && aTransaction != null) {
            throw new TransactionInProgressException("Transaction already in progress and bound to this database");
        }

        mBoundToTransaction = aTransaction;
        // Reset the OID cache.
        mOIDCache = null;
        mOIDCachePosition = 0;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the MetaObjectServerSession associated with this database.
     *
     * @return the MetaObjectServerSession, or null if the database is closed.
     */
    MetaObjectServerSession getMetaObjectServerSession()
    {
        return mMetaObjectServerSession;
    }
    
    //----------------------------------------------------------------------
    /**
     * Adds a new Persistable to the database (locally).
     * The Persistable's database and new OID are set. The EnerJTransaction's
     * modified list is updated. The Persistable is added to the local database cache.
     * 
     *
     * @param aPersistable the new Persistable.
     *
     * @return returns the object's new OID.
     */
    private long addNewPersistable(Persistable aPersistable)
    {
        checkBoundTransaction();

        aPersistable.enerj_SetDatabase(this);
        long oid = getNewOID();
        //Logger.global.finest("Assigning OID " + oid + " to " + aPersistable.getClass() +  " cid " + aPersistable.enerj_GetClassId());
        aPersistable.enerj_SetPrivateOID(oid);

        // Make sure that the schema has this persistable's CID.
        updateSchema(aPersistable);

        // Add it to Transaction modified list. Must be done _after_ OID is set.
        mBoundToTransaction.addToModifiedList(aPersistable, false);
        
        // Cache it
        mClientCache.add(oid, aPersistable);
        
        return oid;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the DatabaseRoot. 
     * For Ener-J internal use only. Operates like addNewPersistable.
     * DatabaseRoot is rooted at OID DATABASE_ROOT_OID.
     * The Persistable's database and new OID are set. The EnerJTransaction's
     * modified list is updated. The Persistable is added to the local database cache.
     * Used when initializing the database.
     *
     * @param aRoot a DatabaseRoot object.
     *
     * @return returns the object's new OID.
     */
    public void setDatabaseRoot(DatabaseRoot aRoot)
    {
        //  TODO  No way! User could wipe out schema enforce this in MetaObjectServer. Only allow DatabaseRoot OID to be set if it hasn't been stored yet.
        checkBoundTransaction();
        
        Persistable persistable = (Persistable)aRoot;

        persistable.enerj_SetDatabase(this);
        persistable.enerj_SetPrivateOID(ObjectServer.DATABASE_ROOT_OID);

        // Add it to Transaction modified list. Must be done _after_ OID is set.
        mBoundToTransaction.addToModifiedList(persistable, false);
        
        // Cache it
        mClientCache.add(ObjectServer.DATABASE_ROOT_OID, persistable);
    }

    //----------------------------------------------------------------------
    /**
     * Checks that the database is open, it currently bound to a transaction,
     * and that the transaction is current for the caller's thread.
     *
     * @throws TransactionNotInProgressException if a transaction is not bound,
     * or the transaction belongs to a different thread.
     * @throws DatabaseClosedException if the database has not been opened yet.
     */
    private void checkBoundTransaction()
    {
        checkBoundTransaction(false);
    }

    //----------------------------------------------------------------------
    /**
     * Checks that the database is open, it currently bound to a transaction,
     * and that the transaction is current for the caller's thread.
     * 
     * @param readOnly true if the operation will be read-only.
     *
     * @throws TransactionNotInProgressException if a transaction is not bound,
     * or the transaction belongs to a different thread.
     * @throws DatabaseClosedException if the database has not been opened yet.
     */
    private void checkBoundTransaction(boolean readOnly)
    {
        if (!mIsOpen) {
            throw new DatabaseClosedException("Database has not been opened");
        }
        
        if (mBoundToTransaction == null) {
            if (readOnly && mAllowNontransactionalReads) {
                return; // That's all we need to check
            }

            throw new TransactionNotInProgressException("Transaction not in progress on the database");
        }
        
        if (EnerJTransaction.getCurrentTransaction() != mBoundToTransaction) {
            throw new TransactionNotInProgressException("Database bound to a Transaction in another thread");
        }
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Determines whether we're in non-transactional read mode. Non-transaction read mode
     * exists when the database is not bound to a transaction and it is set to allow non-transactional reads. 
     *
     * @return true if in non-transactional read mode, else false.
     */
    private boolean isNontransactionalReadMode()
    {
        if (mBoundToTransaction != null) {
            return false;
        }
        
        return mAllowNontransactionalReads;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets a new OID.
     *
     * @return a new OID.
     *
     * @throws TransactionNotInProgressException if a transaction is not bound,
     * or the transaction belongs to a different thread.
     */
    private long getNewOID()
    {
        if (mOIDCache == null || mOIDCachePosition >= mOIDCache.length) {
            try {
                mOIDCache = mMetaObjectServerSession.getNewOIDBlock();
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ODMGRuntimeException("Could not load object", e);
            }

            mOIDCachePosition = 0;
        }
        
        long oid = mOIDCache[mOIDCachePosition];
        ++mOIDCachePosition;
        return oid;
    }
    
    //----------------------------------------------------------------------
    /**
     * Common initialization used when opening a database.
     */
    private void initOpenDatabase()
    {
        // After successful open, check if we need to make it current.
        if (sCurrentProcessDatabase == null) {
            sCurrentProcessDatabase = this;
        }
        
        if (!sCurrentThreadDatabaseMap.containsKey( Thread.currentThread() )) {
            sCurrentThreadDatabaseMap.put( Thread.currentThread(), this);
        }
        
        mIsOpen = true;
    }
    
    //----------------------------------------------------------------------
    // Start of org.odmg.Database interface methods...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * @param name The name of the database as a URI. Currently, three forms
     * of URIs are possible:
     * <p>
     * <i>enerj[.subprotocol]://[username[:password]@]hostname[:port]/dbname[?parameters]</i> -- connects to the
     * database server at 'hostname' serving database 'dbname'. If hostname is '-', a server
     * is instantiated in the client's JVM and the database is opened locally. If subprotocol is 
     * not specified, the default Ener-J plug-ins are used.<p> 
     * <i>dbname</i> -- a server
     * is instantiated in the client's JVM using the default Ener-J plug-ins and the database is opened locally.
     * However, if the <code>vo.dburi</code> system property is set, it is used as the base URI and dbname is
     * appended to it.<p> 
     * <i>dbname@hostname[:port]</i> -- connects to the
     * database server at 'hostname' serving database 'dbname' using the default Ener-J plug-ins.<p> 
     * </code><p>
     * 
     * The system property <code>vo.plugins</code>, if set, represents a colon-separated list of 
     * plug-in class names which register subprotocols. If set, these classes are loaded and their static initializers 
     * are called to register new subprotocols. This allows new plug-ins to be referenced via the subprotocol on
     * the URI.<p>
     */
    public void open(String name, int accessMode) throws ODMGException 
    {
        if (mIsOpen) {
            throw new DatabaseOpenException("Database is already open");
        }

        // Set this so that servers can determine whether they were started within the client JVM.
        sIsThisTheClientJVM = true;

        // Make properties from the URI
        // Copy the system properties as defaults.
        Properties props = new Properties( System.getProperties() );
        String uriString;
        if (name.indexOf("//") < 0) {
            int atIdx = name.indexOf('@'); 
            if (atIdx > 0 && (atIdx + 1) > name.length()) {
                // "dbname@host[:port]"
                String dbname = name.substring(0, atIdx);
                String host = name.substring(atIdx + 1);
                uriString = "enerj://" + host + '/' + dbname;
            }
            else {
                // "dbname"
                uriString = System.getenv("vo.dburi");
                if (uriString == null) {
                    uriString = "enerj://-";
                }
                
                uriString += '/' + name;
            }
        }
        else {
            // Full URI: "enerj[:subprotocol]://[username[:password]@]hostname[:port]/dbname"
            uriString = name;
        }
        
        URI uri;
        try {
            uri = new URI(uriString);
        }
        catch (URISyntaxException e) {
            throw new ODMGException("Malformed database name URI: " + uriString, e);
        }
        
        String host = uri.getHost();
        int port = uri.getPort();  // -1 if undefined.
        String userinfo = uri.getUserInfo();
        String username = null;
        String password = null;

        if (userinfo != null) {
            // Has password?
            int idx = userinfo.indexOf(':');
            if (idx > 0 && (idx + 1) > userinfo.length()) {
                username = userinfo.substring(0, idx);
                password = userinfo.substring(idx + 1);
            }
            else {
                username = userinfo;
            }
        }
        
        // Path begins with a '/', so strip it.
        String dbname = uri.getPath();
        if (dbname != null && dbname.length() > 1) {
            dbname = dbname.substring(1);
        }

        if (dbname == null || dbname.length() == 0) {
            throw new ODMGException("Malformed URI, must a database name: " + uriString);
        }
        
        String scheme = uri.getScheme();
        String pluginClassName;
        if (scheme.equals("enerj")) {
            if (host == null || host.length() == 0 || host.equals("-")) {
                // Local connection.
                host = null;
                pluginClassName = DefaultMetaObjectServer.class.getName();
            }
            else {
                // TODO --  no remote plug-in yet...
                pluginClassName = DefaultMetaObjectServer.class.getName();
            }
        }
        else if (scheme.startsWith("enerj.")) {
            // TODO handle sub-protocols. Use PluginHelper to resolve. PluginHelper registers plugins via system prop vo.plugins
            String subprotocol = scheme.substring("enerj.".length() );
            throw new ODMGException("Unknown subprotocol '" + subprotocol + "': " + uriString);
        }
        else {
            throw new ODMGException("Malformed URI, must have scheme of 'enerj': " + uriString);
        }

        // Set MetaObjectServer properties
        props.setProperty(ENERJ_DBNAME_PROP, dbname);
        props.setProperty(ENERJ_ACCESS_MODE_PROP, String.valueOf(accessMode) );

        if (username != null) {
            props.setProperty(ENERJ_USERNAME_PROP, username);
        }
        
        if (password != null) {
            props.setProperty(ENERJ_PASSWORD_PROP, password);
        }
        
        if (host != null) {
            props.setProperty(ENERJ_HOSTNAME_PROP, host);
        }
        
        if (port != -1) {
            props.setProperty(ENERJ_PORT_PROP, String.valueOf(port) );
        }

        // Add query parameters
        String query = uri.getQuery();
        if (query != null) {
            URIUtil.parseQuery(query, false, props);
        }
        
        mMetaObjectServerSession = (MetaObjectServerSession)PluginHelper.connect(pluginClassName, props);

        initOpenDatabase();
    }
    
    //----------------------------------------------------------------------
    public void close() throws ODMGException 
    {
        if (!mIsOpen) {
            throw new DatabaseClosedException("Database is not open");
        }
        
        if (mBoundToTransaction != null) {
            throw new TransactionInProgressException("Cannot close, transaction is in progress");
        }
        
        try {
            mMetaObjectServerSession.disconnect();
        }
        finally {
            mMetaObjectServerSession = null;
            mIsOpen = false;
        
            // Clear any current database.
            if (sCurrentProcessDatabase == this) {
                sCurrentProcessDatabase = null;
            }

            if (sCurrentThreadDatabaseMap.containsKey( Thread.currentThread() )) {
                sCurrentThreadDatabaseMap.remove( Thread.currentThread() );
            }
        } // End finally
    }
    
    //----------------------------------------------------------------------
    public void bind(Object object, String name) throws ObjectNameNotUniqueException 
    {
        checkBoundTransaction();
        
        if ( !(object instanceof Persistable)) {
            throw new ClassNotPersistenceCapableException("Object is not persistable");
        }
        
        mMetaObjectServerSession.bind(getOID(object), name);
    }
    
    //----------------------------------------------------------------------
    public void unbind(String name) throws ObjectNameNotFoundException 
    {
        checkBoundTransaction();
        mMetaObjectServerSession.unbind(name);
    }
    
    //----------------------------------------------------------------------
    public Object lookup(String name) throws ObjectNameNotFoundException 
    {
        checkBoundTransaction(true);

        long oid = mMetaObjectServerSession.lookup(name);
        return getObjectForOID(oid);
    }
    
    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * <p>
     * Note: Only has an effect if the object is new within this transaction.<p>
     *
     * Note: Ener-J implements persistence by reachability, as defined by
     * the ODMG specification. Hence in Ener-J, this method adds the object
     * to its class extent and marks it as not collectable. 
     * To make the object eligible for garbage collection, you must call deletePersistent().
     */
    public void makePersistent(Object object) 
    {
        checkBoundTransaction();
        if ( !(object instanceof Persistable)) {
            throw new ClassNotPersistenceCapableException("Object of class " + object.getClass().getName() + " is not persistable");
        }
        
        Persistable persistable = (Persistable)object;

        if (persistable.enerj_IsNew()) {
            // If this causes an OID to be allocated, the object will
            // be added to the modified list.
            getOID(persistable);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * <p>
     * Note: Ener-J implements persistence by reachability, as defined by
     * the ODMG specification. Hence you normally do not need to call this method.
     * Ener-J's instant garbage collection algorithm takes care of removing an
     * object from the database once it is fully dereferenced. This preserves
     * referential integrity and the integrity of any extents defined for the class.
     * However, if the object was explicitly added to the database using makePersistent(), you must call
     * this method to make it eligible for collection.
     */
    public void deletePersistent(Object object) 
    {
        checkBoundTransaction();
        // Force remove from extent, update comment
        if ( !(object instanceof Persistable)) {
            throw new ODMGRuntimeException("Object is not persistable");
        }
        
        Persistable persistable = (Persistable)object;

        //  TODO  decr GC count.
        
        /* // Handle case if it was added then deleted in same transaction.
        if (persistable.enerj_IsNew()) {
            // ??? maybe its new AND referenced elsewhere. Only really delete if GC count has reached zero.
            // ??? maybe GC stuff can just take care of this...
            mBoundToTransaction.removeFromModifiedList(persistable);
        }
        else {
            
        }
        */
        // For now... Just remove from extent. Not quite the correct semantics, but it'll do.
        mMetaObjectServerSession.removeFromExtent( getOID(persistable) );
    }
    
    //----------------------------------------------------------------------
    
    //--------------------------------------------------------------------------------
    /**
     * Gets an Extent for the given class.
     *
     * @param aPersistentCapableClass the class to retrieve the extent for.
     * @param wantSubClassInstances true if subclasses should be included in the extent.
     * 
     * @return the Extent.
     */
    public Extent getExtent(Class aPersistentCapableClass, boolean wantSubClassInstances)
    {
        return new EnerJExtent(this, aPersistentCapableClass, wantSubClassInstances);
    }
    
    //----------------------------------------------------------------------
    // ...End of org.odmg.Database interface methods.
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * Subclass ByteArrayInputStream so that we can substitute a new array
     * of bytes without creating a new object each time.
     */
    private static final class DBByteArrayInputStream extends ByteArrayInputStream
    {
        private ObjectSerializer.ReadContext mReadContext;
        
        //----------------------------------------------------------------------
        DBByteArrayInputStream()
        {
            super(new byte[0]);
        }

        //----------------------------------------------------------------------
        void setByteArray(byte[] aByteArray)
        {
            buf = aByteArray;
            pos = 0;
            count = buf.length;
        }

        //----------------------------------------------------------------------
        ObjectSerializer.ReadContext getReadContext()
        {
            return mReadContext;
        }

        //----------------------------------------------------------------------
        void setReadContext(ObjectSerializer.ReadContext aReadContext)
        {
            mReadContext = aReadContext;
        }
    }
}

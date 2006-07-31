// Ener-J
// Copyright 2001-2005 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/DefaultMetaObjectServer.java,v 1.16 2006/06/05 01:17:08 dsyrstad Exp $

package org.enerj.server;

import gnu.trove.TLongArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.odmg.DatabaseNotFoundException;
import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.ObjectNameNotUniqueException;
import org.odmg.ObjectNotPersistentException;
import org.enerj.core.ClassVersionSchema;
import org.enerj.core.DatabaseRoot;
import org.enerj.core.LogicalClassSchema;
import org.enerj.core.Schema;
import org.enerj.core.SparseBitSet;
import org.enerj.core.SystemCIDMap;
import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJTransaction;
import org.enerj.util.FileUtil;
import org.enerj.util.RequestProcessor;
import org.enerj.util.RequestProcessorProxy;

/** 
 * Default Ener-J MetaObjectServer. Provides the standard Ener-J implementation and
 * can delegate to any other ObjectServer for persistence.
 * This class is thread-safe.<p>
 *
 * This server is referenced by the properties described in the {@link connect} method.
 *
 * @version $Id: DefaultMetaObjectServer.java,v 1.16 2006/06/05 01:17:08 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see ObjectServer
 */
public class DefaultMetaObjectServer implements MetaObjectServer
{
    /** HashMap of config file pathnames to DefaultMetaObjectServers. */
    private static HashMap<String, DefaultMetaObjectServer> sCurrentServers = new HashMap<String, DefaultMetaObjectServer>(20);
    
    /** If we're running locally in the client, this will be non-null. It represents the
     * RequestProcessor used to handle server and session related calls.
     */
    private RequestProcessor mRequestProcessor = null;
    /** Set of active sessions. */
    private HashSet mActiveSessions = new HashSet();
    /** True if this server is shutting down. */
    private boolean mIsInShutdown = false;

    //----------------------------------------------------------------------
    /**
     * Construct a DefaultMetaObjectServer.
     *
     * @throws ODMGException if an error occurs.
     */
    private DefaultMetaObjectServer() throws ODMGException  
    {
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets a required property from the specified properties.
     *
     * @param someProperties the properties.
     * @param aKey the property key.
     *
     * @return the String value.
     *
     * @throws ODMGException if the property was not found.
     */
    private static String getRequiredProperty(Properties someProperties, String aKey) throws ODMGException
    {
        // TODO refactor out - PagedObjectServer uses these too
        String value = someProperties.getProperty(aKey);
        if (value == null) {
            throw new ODMGException(aKey + " not specified");
        }
        
        return value;
    }
    
    //----------------------------------------------------------------------
    // Start of MetaObjectServer interface methods...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * Connects to a database.
     *
     * @param someProperties the connect properties. See {@link MetaObjectServer}. The "vo.dbname" property
     *  is expected to resolve to a file named "{vo.dbname}/{vo.dbname}.properties" somewhere in vo.dbpath. In this properties file,
     *  the following properties must be set:<p>
     *  <ul>
     *  <li><i>DefaultMetaObjectServer.ObjectServerClass</i> - the class name of the ObjectServer plug-in.</li>
     *  </ul>
     *  
     *  The property "vo.dbdir" is added the properties for subsequent plug-ins, if the "{vo.dbname}.properties"
     *  file is found. "vo.dbdir" will contain the directory name where the properties file was found. This
     *  can be used for later macro expansion in file names. E.g., a filename property could say something
     *  like:<p>
     *  
     *   FilePageServer.volume=${vo.dbname}/GeneralDB.enerj<p>
     *   
     *  to specify that the volume can be found in the same directory as the properties file.
     *
     * @return a MetaObjectServerSession.
     *  
     * @throws ODMGException in the event of an error. These errors include, but are not limited to:
     *  DatabaseNotFoundException if the database doesn't exist;  DatabaseIsReadOnlyException if the 
     *  database is read-only (e.g., on a read-only filesystem), but OPEN_READ_ONLY was not specified
     *  (note that this is really an ODMGRuntimeException).
     */
    public static MetaObjectServerSession connect(Properties someProperties) throws ODMGException 
    {
        // Look up {dbname}.properties on vo.dbpath.  
        String dbPath = someProperties.getProperty(EnerJ_DBPATH_PROP);
        if (dbPath == null) {
            dbPath = ".";
        }
        
        String dbname = getRequiredProperty(someProperties, EnerJ_DBNAME_PROP);

        Properties dbConfigProps = new Properties(someProperties);

        String propFileName = dbname + File.separatorChar + dbname + ".properties";
        File propFile = FileUtil.findFileOnPath(propFileName, dbPath);
        // NOTE: It's OK not to find the property file. In this case, we just try to use URI/System properties.
        if (propFile != null) {
            // Combine the given properties and the ones from the config file, with the latter overriding the former.
            try {
                FileInputStream inPropFile = new FileInputStream(propFile);
                dbConfigProps.load(inPropFile);
                dbConfigProps.setProperty(EnerJ_DBDIR_PROP, propFile.getParent() );
            }
            catch (IOException e) {
                throw new ODMGException("Error reading " + propFile, e);
            }
        } 
        else if ( dbConfigProps.getProperty("DefaultMetaObjectServer.ObjectServerClass") == null) {
            // If there was no physical properties file, then DefaultMetaObjectServer.ObjectServerClass 
            // must have been defined in the properties.
            throw new DatabaseNotFoundException("Cannot find database: " + dbname);
        }

        // See if there is already a MetaObjectServer for this database.
        // Synchronize on sCurrentServers during the get/put process so that another thread
        // cannot create one at the same time.
        DefaultMetaObjectServer server;
        synchronized (sCurrentServers) {
            server = (DefaultMetaObjectServer)sCurrentServers.get(dbname);
            if (server == null || server.mIsInShutdown) {
                server = new DefaultMetaObjectServer();
                sCurrentServers.put(dbname, server);
            }
        } // End synchronized (sCurrentServers)

        String objectServerClassName = getRequiredProperty(dbConfigProps, "DefaultMetaObjectServer.ObjectServerClass"); 
        ObjectServerSession delegateSession;
        try {
            delegateSession = (ObjectServerSession)PluginHelper.connect(objectServerClassName, dbConfigProps);
        }
        catch (ODMGRuntimeException e) {
            throw e;    // Don't remap
        }
        catch (ODMGException e) {
            throw e;    // Don't remap
        }
        catch (Exception e) {
            throw new ODMGException("Cannot connect to ObjectServer " + objectServerClassName, e);
        }

        MetaObjectServer metaObjectServer = server;
        DefaultMetaObjectServer unproxiedMetaObjectServer = server;
        if (EnerJDatabase.isThisTheClientJVM()) {
            // Server is running locally. We need to run session in another thread. Create a proxy per session.
            // TODO pool these?
            server.mRequestProcessor = new RequestProcessor("Local MetaObjectServer Thread", true);
            metaObjectServer = (MetaObjectServer)RequestProcessorProxy.newInstance(server, server.mRequestProcessor);
        }
        
        PrivilegedSession session = new PrivilegedSession(metaObjectServer, unproxiedMetaObjectServer, delegateSession);
        synchronized (server.mActiveSessions) {
            server.mActiveSessions.add(session);
        }
        
        // TODO - validate username/password here. Hash password with SHA1.
        // TODO - write connect/session info in log.
        MetaObjectServerSession clientSession = new UnprivilegedSession(session);
        if (server.mRequestProcessor != null) {
            // Server is running locally. Proxy this session to the same RequestProcessor as the server.
            clientSession = (MetaObjectServerSession)RequestProcessorProxy.newInstance(clientSession, server.mRequestProcessor);
        }

        return clientSession;
    }

    //----------------------------------------------------------------------
    // ...End of MetaObjectServer interface methods.
    //----------------------------------------------------------------------


    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * The main, privileged, session. Wraps delegate ObjectServerSession.
     */
    private static final class PrivilegedSession implements MetaObjectServerSession 
    {
        /** Our shutdown hook. */
        private static Thread mShutdownHook = null;

        private MetaObjectServer mProxiedMetaObjectServer;
        private DefaultMetaObjectServer mMetaObjectServer;
        private ObjectServerSession mDelegateSession;
        private ObjectServer mDelegateObjectServer;
        private EnerJDatabase mDatabase = null;
        private EnerJTransaction mTransaction = null;
        private boolean mAllowNontransactionalReads = false;
        /** New OIDs that need to be added to their extents on commit. Key is CID, value is a list of OIDs. */
        private HashMap<Long, TLongArrayList> mPendingNewOIDs = null;
        
        //----------------------------------------------------------------------
        PrivilegedSession(MetaObjectServer aProxiedMetaObjectServer, DefaultMetaObjectServer aMetaObjectServer, ObjectServerSession aDelegateSession)
        {
            mProxiedMetaObjectServer = aProxiedMetaObjectServer;
            mMetaObjectServer = aMetaObjectServer;
            mDelegateSession = aDelegateSession;
            mDelegateObjectServer = aDelegateSession.getObjectServer();

            // Register a shutdown hook...
            mShutdownHook = new ShutdownHook(this);
            Runtime.getRuntime().addShutdownHook(mShutdownHook);
        }

        //----------------------------------------------------------------------
        private EnerJDatabase getClientDatabase()
        {
            if (mDatabase == null) {
                mDatabase = new EnerJDatabase(this, mMetaObjectServer);
            }

            return mDatabase;
        }

        //----------------------------------------------------------------------
        private EnerJTransaction setupClientTransaction()
        {
            mTransaction = new EnerJTransaction( getClientDatabase() );
            return mTransaction;
        }

        //----------------------------------------------------------------------
        private void clearClientTransaction()
        {
            if (mTransaction != null) {
                mTransaction.clear();
                mTransaction = null;
            }
        }

        //----------------------------------------------------------------------
        
        //--------------------------------------------------------------------------------
        /**
         * Flush pending extent updates out to the extents.
         *
         */
        private void updateExtents()
        {
            // Add new objects to extents.
            EnerJDatabase db = getClientDatabase();
            DatabaseRoot root = (DatabaseRoot)db.getObjectForOID(DATABASE_ROOT_OID);
            Schema schema = root.getSchema();
            for (long cid : mPendingNewOIDs.keySet()) {
                TLongArrayList oids = mPendingNewOIDs.get(cid);
                if (oids != null) {
                    ClassVersionSchema classVersion = schema.findClassVersion(cid);
                    if (classVersion != null) {
                        SparseBitSet extent = classVersion.getLogicalClassSchema().getExtentBitSet();
                        int size = oids.size();
                        for (int i = 0; i < size; i++) {
                            // TODO We need to track the extent size. Do this with a delta that is updated right
                            // TODO at commit time.
                            extent.set(oids.get(i), true);
                        }
                    }
                }
            }
            
            mPendingNewOIDs.clear();
        }

        //----------------------------------------------------------------------
        // Start of MetaObjectServerSession interface methods...
        //----------------------------------------------------------------------

        //----------------------------------------------------------------------
        public MetaObjectServer getMetaObjectServer()
        {
            return mProxiedMetaObjectServer;
        }
        
        //----------------------------------------------------------------------
        public void bind(long anOID, String aName) throws ObjectNameNotUniqueException
        {
            DatabaseRoot root = (DatabaseRoot)getClientDatabase().getObjectForOID(DATABASE_ROOT_OID);
            // TODO this is nasty because we read lock and write lock the bindery here. this needs better concurrency
            Map bindery = root.getBindery();
            if (bindery.containsKey(aName)) {
                throw new ObjectNameNotUniqueException(aName);
            }

            bindery.put(aName, new Long(anOID));
        }

        //----------------------------------------------------------------------
        public long lookup(String aName) throws ObjectNameNotFoundException
        {
            EnerJDatabase db = getClientDatabase();
            DatabaseRoot root = (DatabaseRoot)db.getObjectForOID(DATABASE_ROOT_OID);
            // TODO this is nasty because we read lock and write lock the bindery here. this needs better concurrency
            Map bindery = root.getBindery();
            Long oid = (Long)bindery.get(aName);
            if (oid == null) {
                throw new ObjectNameNotFoundException(aName);
            }

            return oid.longValue();
        }

        //----------------------------------------------------------------------
        public void unbind(String aName) throws ObjectNameNotFoundException
        {
            EnerJDatabase db = getClientDatabase();
            DatabaseRoot root = (DatabaseRoot)db.getObjectForOID(DATABASE_ROOT_OID);
            // TODO this is nasty because we read lock and write lock the bindery here. this needs better concurrency
            Map bindery = root.getBindery();
            if (!bindery.containsKey(aName)) {
                throw new ObjectNameNotFoundException(aName);
            }

            bindery.remove(aName);
        }

        //----------------------------------------------------------------------
        public void removeFromExtent(long anOID) throws ObjectNotPersistentException
        {
            EnerJDatabase db = getClientDatabase();
            DatabaseRoot root = (DatabaseRoot)db.getObjectForOID(DATABASE_ROOT_OID);
            ClassVersionSchema classVersion;
            try {
                long cid = getCIDForOID(anOID);
                classVersion = root.getSchema().findClassVersion(cid);
            }
            catch (ODMGException e) {
                throw new ObjectNotPersistentException("Object is not persistent", e);
            }

            if (classVersion == null) {
                throw new ObjectNotPersistentException("Object is not persistent");
            }
            
            SparseBitSet extent = classVersion.getLogicalClassSchema().getExtentBitSet();
            extent.set(anOID, false);

            // TODO remove from indexes
        }

        //----------------------------------------------------------------------
        public long getExtentSize(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
        {
            long result = 0;
            Schema schema = mDatabase.getDatabaseRoot().getSchema();
            LogicalClassSchema candidateClassSchema = schema.findLogicalClass(aClassName);
            if (candidateClassSchema != null) {
                result += candidateClassSchema.getExtentBitSet().getNumBitsSet();
            }

            if (wantSubclasses) {
                Set<ClassVersionSchema> subclasses = schema.getPersistableSubclasses(aClassName);
                for (ClassVersionSchema classVersion : subclasses) {
                    result += classVersion.getLogicalClassSchema().getExtentBitSet().getNumBitsSet();
                }
            }
            
            return result;
        }

        //----------------------------------------------------------------------
        public ExtentIterator createExtentIterator(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
        {
        	// TODO What about objects added during txn? Flush from client first?
            Schema schema = mDatabase.getDatabaseRoot().getSchema();
            ExtentIterator extentIterator = new DefaultExtentIterator(aClassName, wantSubclasses, schema, this);
            // Proxy iterator if running locally on client.
            if (mMetaObjectServer.mRequestProcessor != null) {
                extentIterator = (ExtentIterator)RequestProcessorProxy.newInstance(extentIterator, mMetaObjectServer.mRequestProcessor);
            }

            return extentIterator;
        }

        //----------------------------------------------------------------------
        // ...End of MetaObjectServerSession interface methods.
        //----------------------------------------------------------------------

        //----------------------------------------------------------------------
        // Start of ObjectServerSesson interface methods...
        //----------------------------------------------------------------------

        //----------------------------------------------------------------------
        public ObjectServer getObjectServer()
        {
            return mProxiedMetaObjectServer;
        }
        
        //----------------------------------------------------------------------
        public void disconnect() throws ODMGException 
        {
            if (mTransaction != null && mTransaction.isOpen()) {
                mTransaction.abort();
                mTransaction = null;
            }

            mDelegateSession.disconnect();
            synchronized (mMetaObjectServer.mActiveSessions) {
                mMetaObjectServer.mActiveSessions.remove(this);
            }

            // If disconnecting last session and this is running locally in the client, shutdown.
            // TODO there is a race condition here accessing mActiveSessions that needs to be resolved.
            if (mMetaObjectServer.mRequestProcessor != null && mMetaObjectServer.mActiveSessions.isEmpty()) {
                shutdown();
            }
        }

        //----------------------------------------------------------------------
        public void shutdown() throws ODMGException
        {
            if (mTransaction != null && mTransaction.isOpen()) {
                // Join to transaction in case we're called from the shutdown hook.
                mTransaction.join();
                mTransaction.abort();
                clearClientTransaction();
            }

            mMetaObjectServer.mRequestProcessor.shutdown();
            mDelegateSession.shutdown();

            try {
                Runtime.getRuntime().removeShutdownHook(mShutdownHook);
            }
            catch (Exception e) {
                // Ignore - shutdown may be in progress.
            }
        }

        //----------------------------------------------------------------------
        public boolean getAllowNontransactionalReads() throws ODMGException
        {
            return mAllowNontransactionalReads;
        }

        //----------------------------------------------------------------------
        public void setAllowNontransactionalReads(boolean isNontransactional) throws ODMGException
        {
            mDelegateSession.setAllowNontransactionalReads(isNontransactional);
            getClientDatabase().setAllowNontransactionalReads(isNontransactional);
            mAllowNontransactionalReads = isNontransactional;
        }

        //----------------------------------------------------------------------
        public long getCIDForOID(long anOID) throws ODMGException
        {
            return mDelegateSession.getCIDForOID(anOID);
        }

        //----------------------------------------------------------------------
        public void storeObject(long aCID, long anOID, byte[] aSerializedObject, boolean isNew)
             throws ODMGException
        {
            mDelegateSession.storeObject(aCID, anOID, aSerializedObject, isNew);
            
            // Evict this from our cache because the client has changed it.
            getClientDatabase().evict(anOID);
            
            if (isNew && !SystemCIDMap.isSystemCID(aCID)) {
                // Queue this object to be added to its extent on commit - only after delegate stores successfully.
                TLongArrayList oids = mPendingNewOIDs.get(aCID);
                if (oids == null) {
                    // First instance of the CID to be stored in this txn, create new list.
                    oids = new TLongArrayList(1000);
                    mPendingNewOIDs.put(aCID, oids);
                }
                
                oids.add(anOID);
            }
        }

        //----------------------------------------------------------------------
        public byte[] loadObject(long anOID) throws ODMGException
        {
            return mDelegateSession.loadObject(anOID);
        }

        //----------------------------------------------------------------------
        public long[] getNewOIDBlock() throws ODMGException
        {
            return mDelegateSession.getNewOIDBlock();
        }

        //----------------------------------------------------------------------
        public void beginTransaction() throws ODMGRuntimeException 
        {
            mDelegateSession.beginTransaction();
            setupClientTransaction();
            if (mPendingNewOIDs == null) {
                mPendingNewOIDs = new HashMap<Long, TLongArrayList>(128);
            }
            else {
                mPendingNewOIDs.clear();
            }
        }

        //----------------------------------------------------------------------
        public void checkpointTransaction() throws ODMGRuntimeException 
        {
            mTransaction.flush();
            mDelegateSession.checkpointTransaction();
        }

        //----------------------------------------------------------------------
        public void commitTransaction() throws ODMGRuntimeException 
        {
            updateExtents();

            // TODO add to indexes
            // TODO else update indexes if key change.

            // We need to flush our objects and not actually commit. Our objects will become part of client's
            // transaction and commit.
            mTransaction.flush();
            mDelegateSession.commitTransaction();
            clearClientTransaction();
        }

        //----------------------------------------------------------------------
        public void rollbackTransaction() throws ODMGRuntimeException 
        {
            mDelegateSession.rollbackTransaction();
            // On client abort, we must "abort" too by clearing, but not calling ObjectServer.
            clearClientTransaction();
            mPendingNewOIDs.clear();
        }

        //----------------------------------------------------------------------
        public void getLock(long anOID, int aLockLevel, long aWaitTime) throws LockNotGrantedException 
        {
            mDelegateSession.getLock(anOID, aLockLevel, aWaitTime);
        }

        //----------------------------------------------------------------------
        // ...End of ObjectServerSession interface methods.
        //----------------------------------------------------------------------
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * The unprivileged session sent back to the client. Wraps the PrivilegedSession.
     */
    private static final class UnprivilegedSession implements MetaObjectServerSession 
    {
		private PrivilegedSession mPrivilegedSession;
        
        //----------------------------------------------------------------------
        UnprivilegedSession(PrivilegedSession aSession)
        {
            mPrivilegedSession = aSession;
        }

        //----------------------------------------------------------------------
        // Start of MetaObjectServerSession interface methods...
        //----------------------------------------------------------------------

        //----------------------------------------------------------------------
        public MetaObjectServer getMetaObjectServer()
        {
            return mPrivilegedSession.getMetaObjectServer();
        }
        
        //----------------------------------------------------------------------
        public void bind(long anOID, String aName) throws ObjectNameNotUniqueException
        {
            mPrivilegedSession.bind(anOID, aName);
        }

        //----------------------------------------------------------------------
        public long lookup(String aName) throws ObjectNameNotFoundException
        {
            return mPrivilegedSession.lookup(aName);
        }

        //----------------------------------------------------------------------
        public void unbind(String aName) throws ObjectNameNotFoundException
        {
            mPrivilegedSession.unbind(aName);
        }

        //----------------------------------------------------------------------
        public void removeFromExtent(long anOID) throws ObjectNotPersistentException
        {
            mPrivilegedSession.removeFromExtent(anOID);
        }

        //----------------------------------------------------------------------
        public long getExtentSize(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
        {
            return mPrivilegedSession.getExtentSize(aClassName, wantSubclasses);
        }

        //----------------------------------------------------------------------
        public ExtentIterator createExtentIterator(String aClassName, boolean wantSubclasses) throws ODMGRuntimeException
        {
            return mPrivilegedSession.createExtentIterator(aClassName, wantSubclasses);
        }

        //----------------------------------------------------------------------
        // ...End of MetaObjectServerSession interface methods.
        //----------------------------------------------------------------------

        //----------------------------------------------------------------------
        // Start of ObjectServerSesson interface methods...
        //----------------------------------------------------------------------

        //----------------------------------------------------------------------
        public ObjectServer getObjectServer()
        {
            return mPrivilegedSession.getObjectServer();
        }
        
        //----------------------------------------------------------------------
        public void disconnect() throws ODMGException 
        {
            mPrivilegedSession.disconnect();
        }

        //----------------------------------------------------------------------
        public void shutdown() throws ODMGException
        {
            mPrivilegedSession.shutdown();
        }

        //----------------------------------------------------------------------
        public boolean getAllowNontransactionalReads() throws ODMGException
        {
            return mPrivilegedSession.getAllowNontransactionalReads();
        }

        //----------------------------------------------------------------------
        public void setAllowNontransactionalReads(boolean isNontransactional) throws ODMGException
        {
            mPrivilegedSession.setAllowNontransactionalReads(isNontransactional);
        }

        //----------------------------------------------------------------------
        public long getCIDForOID(long anOID) throws ODMGException
        {
            return mPrivilegedSession.getCIDForOID(anOID);
        }

        //----------------------------------------------------------------------
        public void storeObject(long aCID, long anOID, byte[] aSerializedObject, boolean isNew)
             throws ODMGException
        {
            // TODO check user privs based on CID.
            /*
            if (SystemCIDMap.getSystemClassNameForCID(aCID) && !isPrivileged()) {
                throw new ODMGException("No permission to update objects of system classes.");
            }
            */

            mPrivilegedSession.storeObject(aCID, anOID, aSerializedObject, isNew);
        }

        //----------------------------------------------------------------------
        public byte[] loadObject(long anOID) throws ODMGException
        {
            return mPrivilegedSession.loadObject(anOID);
        }

        //----------------------------------------------------------------------
        public long[] getNewOIDBlock() throws ODMGException
        {
            return mPrivilegedSession.getNewOIDBlock();
        }

        //----------------------------------------------------------------------
        public void beginTransaction() throws ODMGRuntimeException 
        {
            mPrivilegedSession.beginTransaction();
        }

        //----------------------------------------------------------------------
        public void checkpointTransaction() throws ODMGRuntimeException 
        {
            mPrivilegedSession.checkpointTransaction();
        }

        //----------------------------------------------------------------------
        public void commitTransaction() throws ODMGRuntimeException 
        {
            mPrivilegedSession.commitTransaction();
        }

        //----------------------------------------------------------------------
        public void rollbackTransaction() throws ODMGRuntimeException 
        {
            mPrivilegedSession.rollbackTransaction();
        }

        //----------------------------------------------------------------------
        public void getLock(long anOID, int aLockLevel, long aWaitTime) throws LockNotGrantedException 
        {
            mPrivilegedSession.getLock(anOID, aLockLevel, aWaitTime);
        }

        //----------------------------------------------------------------------
        // ...End of ObjectServerSession interface methods.
        //----------------------------------------------------------------------
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Our JVM ShutdownHook thread.
     */
    private static final class ShutdownHook extends Thread
    {
        private MetaObjectServerSession mSession;
        
        //----------------------------------------------------------------------
        ShutdownHook(MetaObjectServerSession aSession)
        {
            mSession = aSession;
        }
        
        //----------------------------------------------------------------------
        public void run()
        {
            // TODO log shutdown hook invoked.
            //System.err.println("DefaultMetaObjectServer Shutdown hook invoked.");
            try {
                mSession.shutdown();
            }
            catch (ODMGException e) {
                // TODO log...
                System.err.println("Shutdown problem:");
                e.printStackTrace();
            }
        }
    }
}



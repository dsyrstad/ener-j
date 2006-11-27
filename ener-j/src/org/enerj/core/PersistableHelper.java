/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/core/PersistableHelper.java,v 1.6 2006/06/06 21:29:38 dsyrstad Exp $

package org.enerj.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;

import org.enerj.server.ClassInfo;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.TransactionNotInProgressException;

/**
 * Static helper methods for Persistable.
 *
 * @version $Id: PersistableHelper.java,v 1.6 2006/06/06 21:29:38 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class PersistableHelper
{
    private static final Class[] sPeristerArgType = { Persister.class };
    private static final Object[] sPersisterArg = { null };

    /**
     * Initializes a new Persistable. Called from the top-level 
     * Persistable's constructor. NOTE: This can also get called indirectly
     * when Persister.getObjectByOID() is constructing the object using the
     * &lt;init>(Persister) constructor. Persister.getObjectByOID() must
     * always undo the actions of this method. 
     * <p>
     * So if you change the behavior of this method, you MUST also undo the
     * actions in Persister.getObjectByOID().
     *
     * @param aPersistable the Persistable being initialized.
     */
    public static final void initPersistable(Persistable aPersistable)
    {
        aPersistable.enerj_SetNew(true);
        aPersistable.enerj_SetModified(false);
        aPersistable.enerj_SetLoaded(false);
        aPersistable.enerj_SetPrivateOID(ObjectSerializer.NULL_OID);
        aPersistable.enerj_SetPersister(null);
        aPersistable.enerj_SetLockLevel(EnerJTransaction.NO_LOCK);

        if (!isTransactionActive(aPersistable)) {
            // No transaction active. Set the object to nontransactional.
            setNonTransactional(aPersistable);
        }
        // Else: Note: object is only assigned an OID and added to the transaction modified list when
        // its OID is requested by Persister.getOID().
    }
    

    /**
     * Initializes a cloned Persistable. Behaves as if the Persistable had been
     * newed. Note that if a non-top level Persistable 
     * clone's without calling super clone, it will have to "new" the object,
     * in which case it will be initialized via initPersistable.
     *
     * @param aPersistable the Persistable being initialized.
     */
    public static final void initPersistableClone(Persistable aPersistable)
    {
        // This should act just as if the object was newed
        initPersistable(aPersistable);
    }

    /**
     * Determines if a transaction is active for the given persistable or thread if the persistable
     * is not associated with a Persister.
     *
     * @param aPersistable the Persistable.
     * 
     * @return true if a transaction is active, else false.
     */
    private static final boolean isTransactionActive(Persistable aPersistable)
    {
        Persister persister = getPersister(aPersistable);
        return persister != null && persister.isTransactionActive();
    }
    
    /**
     * Gets the Persister for the given persistable or thread if the persistable
     * is not associated with a Persister.
     *
     * @param aPersistable the Persistable.
     * 
     * @return the Persister, or null if no Persister exists.
     */
    private static final Persister getPersister(Persistable aPersistable)
    {
        Persister persister = aPersistable.enerj_GetPersister();
        if (persister == null) {
            persister = PersisterRegistry.getCurrentPersisterForThread();
        }

        return persister;
    }

    /**
     * Verify that the specified object is loaded from the Persister. If it 
     * isn't loaded, it will be loaded.
     *
     * @param aPersistable the Persistable to be checked.
     * @param aSetFlag true if the intent is to update the object's attributes. 
     */
    public static final void checkLoaded(Persistable aPersistable, boolean aSetFlag)
    {
        Persister persister = getPersister(aPersistable);
        
        // Are we in a transaction?
        if (!isTransactionActive(aPersistable)) {
            if ((aSetFlag && aPersistable.enerj_AllowsNonTransactionalWrite()) ||
                (!aSetFlag && aPersistable.enerj_AllowsNonTransactionalRead()) ) {
                // Not in transaction, but stale operation is allowed.
                // The object simply isn't refreshed from the Persister until a
                // transaction starts.
                // If the Persister allows non-transactional reads, fall-thru and
                // load the object from the Persister if necessary.
                try {
                    if ( !persister.getAllowNontransactionalReads()) {
                        return;
                    }
                } catch (ODMGException e) {
                    throw new ODMGRuntimeException("Cannot check non-transactional read state", e);
                }
            }
            else {
                throw new TransactionNotInProgressException();
            }
        }

        // At this point, we know we're in a transaction.
        // If anInstance not loaded from DB and not New, load it now.
        if (!aPersistable.enerj_IsLoaded() && !aPersistable.enerj_IsNew()) {
            // Actually Load the object.
            persister.loadObject(aPersistable);
        } // end if not loaded or new
    }


    /**
     * This method is called by a Persistable.enerj_Set_* method just prior to the
     * first modification of the object.
     * Adds an object to its transaction modified object list, if it's not
     * on it already. Also marks aPersistable as modified. If 
     * object is marked "new", it is mark modified, but it is not added to the
     * modified object list. It is marked modified in this case so that 
     * new objects don't repeatedly call this method from enerj_Set_*.
     * If the object is not "new" and the current Transaction's "RestoreValues"
     * flag is set, a serialized image of the object (prior to modification) is
     * saved in the cache.
     *
     * @param aPersistable the Persistable that was modified.
     */
    public static final void addModified(Persistable aPersistable)
    {
        if (!aPersistable.enerj_IsNew() && !aPersistable.enerj_IsModified()) {
            // If no Persister active or already modified, just ignore this.
            Persister persister = aPersistable.enerj_GetPersister();
            if (persister != null) {
                // Add to the persister's modified object list.
                persister.addToModifiedList(aPersistable);
            }
        }

        aPersistable.enerj_SetModified(true);
    }
    

    /**
     * Sets an object so that it can be accessed and updated outside of a transaction.
     *
     * @param anObject an Object that is Persistable.
     */
    public static final void setNonTransactional(Object anObject)
    {
        if (anObject instanceof Persistable) {
            Persistable persistable = (Persistable)anObject;
            persistable.enerj_SetAllowNonTransactionalRead(true);
            persistable.enerj_SetAllowNonTransactionalWrite(true);
            persistable.enerj_SetLockLevel(EnerJTransaction.NO_LOCK);
        }
    }


    /**
     * Complete the process of hollowing a Persistable.
     *
     * @param aPersistable the Persistable that is being hollowed.
     */
    public static final void completeHollow(Persistable aPersistable)
    {
        aPersistable.enerj_SetModified(false);
        aPersistable.enerj_SetLoaded(false);
        aPersistable.enerj_SetNew(false);
        aPersistable.enerj_SetLockLevel(EnerJTransaction.NO_LOCK);
        // Leave the OID set in case we need to reload this object.
    }
    
    /**
     * Determines if aPersister is in a non-transactional read mode.  
     *
     * @param aPersister
     * 
     * @return true if aPersister is not in a transaction and {@link Persister#getAllowNontransactionalReads()}
     *  is true.
     */
    private static final boolean isNonTransactionalReadMode(Persister aPersister)
    {
        try {
            // A transaction must not be active and the Persister must be set for non-transaction reads.
            return !aPersister.isTransactionActive() &&
                aPersister.getAllowNontransactionalReads();
        }
        catch (ODMGException e) {
            throw new ODMGRuntimeException(e);
        }
    }
    
    /**
     * Creates a hollow object initialized by a ClassInfo definition.
     *
     * @param aClassInfo the Class information for anOID.
     * @param anOID the oid being loaded.
     * @param aPersister a Persister responsible for the object.
     * 
     * @return the Persistable.
     * 
     * @throws ODMGRuntimeException if an error occurs.
     */
    public static final Persistable createHollowPersistable(ClassInfo aClassInfo, long anOID, Persister aPersister) 
    {
        Class objClass;
        try {
            // TODO load class from ClassInfo bytes if CID does not match the loaded class' CID.
            objClass = Class.forName(aClassInfo.getClassName());
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Cannot find class " + aClassInfo + " for OID " + anOID, e);
        }
        
        
        // Create a hollow (non-loaded) object. PersistableHelper.checkLoaded() will
        // actually load the contents (via Persister.loadObject) when a field is accessed.
        try {
            Constructor constructor = objClass.getDeclaredConstructor(sPeristerArgType);
            constructor.setAccessible(true);
            Persistable persistable = (Persistable)constructor.newInstance(sPersisterArg);

            persistable.enerj_SetPersister(aPersister);
            persistable.enerj_SetPrivateOID(anOID);
            persistable.enerj_SetNew(false);
            persistable.enerj_SetModified(false);
            persistable.enerj_SetLoaded(false);
            if (isNonTransactionalReadMode(aPersister)) {
                setNonTransactional(persistable);
            }
            
            return persistable;
        }
        catch (Exception e) {
            throw new org.odmg.ODMGRuntimeException("Error creating object for OID " + anOID, e);
        }
    }

    /**
     * Loads the contents of aPersistable from a serialized image. 
     *
     * @param aPersister the persister responsible for this Persistable.
     * @param aPersistable the persistable to be loaded.
     * @param anImage the serialized image of the persistable.
     *
     * @throws ODMGRuntimeException if an error occurs.
     */
    public static void loadSerializedImage(Persister aPersister, Persistable aPersistable, byte[] anImage)
    {
        ObjectSerializer readContext = new ObjectSerializer( new DataInputStream( new ByteArrayInputStream(anImage) ) );

        try {
            aPersistable.enerj_SetPersister(aPersister);

            aPersistable.enerj_ReadObject(readContext);

            aPersistable.enerj_SetLoaded(true);
            aPersistable.enerj_SetModified(false);
        }
        catch (ODMGRuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ODMGRuntimeException("Error loading object for OID " + aPersistable.enerj_GetPrivateOID(), e);
        }
    }
    
    /**
     * Creates a serialized image of the object. Note that this can cause new objects to be added 
     * to the Persister's modified list.
     *
     * @param aPersistable a persistable object.
     *
     * @return a newly allocated byte array representing the serialized image of aPersistable.
     * 
     * @throws ODMGRuntimeException if an error occurs.
     */
    public static byte[] createSerializedImage(Persistable aPersistable)
    {
        // TODOLOW Maybe have a shared pool of these later?
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(1000);
        DataOutputStream dataOutput = new DataOutputStream(byteOutputStream);
        
        try {
            byteOutputStream.reset();
            ObjectSerializer writeContext = new ObjectSerializer(dataOutput);
            aPersistable.enerj_WriteObject(writeContext);
            dataOutput.flush();
        }
        catch (IOException e) {
            throw new ODMGRuntimeException("Error writing object: " + e);
        }
        
        return byteOutputStream.toByteArray();
    }
    
    /**
     * Resolve the object's entire object graph recursively until all instances are fully loaded.
     * This allows the object's entire graph to be used without a dependence on the persister (i.e.,
     * {@link Persistable#enerj_GetPersister()} will return null.
     *
     * @param anObject the object to be resolved (a FCO).
     * @param shouldDisassociate if true, the object tree will be disassociated from 
     *  its Persister.
     *
     * @throws IOException if an error occurs
     */
    public static void resolveObject(Persistable anObject, boolean shouldDisassociate) throws IOException
    {
        resolveObject(new ObjectSerializer(), anObject, shouldDisassociate);
    }
    
    /**
     * Resolve the object's entire object graph recursively until all instances are fully loaded.
     * This allows the object's entire graph to be used without a dependence on the persister (i.e.,
     * {@link Persistable#enerj_GetPersister()} will return null.
     *
     * @param anObjectSerializer the object serializer.
     * @param anObject the object to be resolved (a FCO).
     * @param shouldDisassociate if true, the object tree will be disassociated from 
     *  its Persister.
     *
     * @throws IOException if an error occurs
     */
    public static void resolveObject(ObjectSerializer anObjectSerializer, Persistable anObject, boolean shouldDisassociate) throws IOException
    {
        checkLoaded(anObject, false);
        anObject.enerj_ResolveObject(anObjectSerializer, shouldDisassociate);
        if (shouldDisassociate) {
            anObject.enerj_SetPersister(null);
            setNonTransactional(anObject);
        }
    }
    
    
    /**
     * Set the OID and persister for aPersistable.
     *
     * @param aPersister the Persister managing the Persistable.
     * @param anOID the OID to set.
     * @param aPersistable the object to be updated.
     */
    public static void setOID(Persister aPersister, long anOID, Persistable aPersistable)
    {
        aPersistable.enerj_SetPrivateOID(anOID);
        aPersistable.enerj_SetPersister(aPersister);
    }
}

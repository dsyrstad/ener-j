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

import org.odmg.*;

import org.enerj.server.*;

/**
 * Static helper methods for Persistable.
 *
 * @version $Id: PersistableHelper.java,v 1.6 2006/06/06 21:29:38 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class PersistableHelper
{
    //----------------------------------------------------------------------
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

        EnerJTransaction txn = EnerJTransaction.getCurrentTransaction();
        if (txn == null) {
            // No transaction active. Set the object to nontransactional.
            setNonTransactional(aPersistable);
        }
        // Else: Note: object is only assigned an OID and added to the transaction modified list when
        // its OID is requested by Persister.getOID().
    }
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
    /**
     * Verify that the specified object is loaded from the Persister. If it 
     * isn't loaded, it will be loaded.
     *
     * @param aPersistable the Persistable to be checked.
     * @param aSetFlag true if the intent is to update the object's attributes. 
     */
    public static final void checkLoaded(Persistable aPersistable, boolean aSetFlag)
    {
        // Are we in a transaction?
        if (EnerJTransaction.getCurrentTransaction() == null) {
            if ((aSetFlag && aPersistable.enerj_AllowsNonTransactionalWrite()) ||
                (!aSetFlag && aPersistable.enerj_AllowsNonTransactionalRead()) ) {
                // Not in transaction, but stale operation is allowed.
                // The object simply isn't refreshed from the database until a
                // transaction starts.
                // If the database allows non-transactional reads, fall-thru and
                // load the object from the database if necessary.
                try {
                    if ( !aPersistable.enerj_GetPersister().getAllowNontransactionalReads()) {
                        return;
                    }
                } catch (ODMGException e) {
                    throw new ODMGRuntimeException("Can't check nontransaction read state", e);
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
            aPersistable.enerj_GetPersister().loadObject(aPersistable);
        } // end if not loaded or new
    }

    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
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

    //----------------------------------------------------------------------
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
}

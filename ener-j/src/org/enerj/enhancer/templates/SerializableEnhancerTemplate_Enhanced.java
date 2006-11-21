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
// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/templates/SerializableEnhancerTemplate_Enhanced.java,v 1.1 2006/06/06 21:29:36 dsyrstad Exp $

package org.enerj.enhancer.templates;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.enerj.core.ObjectSerializer;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.enerj.core.Persister;

/**
 * Class file enhancer template for Ener-J. This is a "top-level" persistable.
 * This class provides a bytecode prototype for developement of the enhancer.
 * This is the class after enhancement. Ignore the _Enhanced extension, it wouldn't normally exist.
 * This is the class stored in the database and loaded on the client via the PersisterClassLoader.
 *
 * @version $Id: SerializableEnhancerTemplate_Enhanced.java,v 1.1 2006/06/06 21:29:36 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class SerializableEnhancerTemplate_Enhanced implements Serializable, Persistable
{
    private byte mByte;
    
    transient protected boolean enerj_mModified;
    transient protected boolean enerj_mNew;
    transient protected boolean enerj_mLoaded;
    transient protected boolean enerj_mAllowNonTransactionalReads;
    transient protected boolean enerj_mAllowNonTransactionalWrites;

    // Maybe version should be in the cache?
    transient private long enerj_mVersion;
    transient private long enerj_mOID;
    transient private Persister enerj_mPersister;
    /** EnerJTransaction lock level: NO_LOCK, READ, UPGRADE, or WRITE */
    transient private int enerj_mLockLevel;
    
    transient private static long enerj_sClassId;
    
    static {
        enerj_sClassId = 55834L;
    }

    //----------------------------------------------------------------------
    public SerializableEnhancerTemplate_Enhanced()
    {
        super();
        // This is inserted right after the super call.
        PersistableHelper.initPersistable(this);
    }

    //----------------------------------------------------------------------
    // Generated methods for top-level persistable only. These come from the 
    // Persistable interface...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // May be null if Persister not assigned yet (it must be New in this case).
    public final Persister enerj_GetPersister()
    {
        return enerj_mPersister;
    }

    //----------------------------------------------------------------------
    public final void enerj_SetPersister(Persister aPersister)
    {
        enerj_mPersister = aPersister;
    }

    //----------------------------------------------------------------------
    /**
     * Get the modification version number associated with this object.
     *
     * @return the Version.
     */
    public long enerj_GetVersion()
    {
        return enerj_mVersion;
    }

    //----------------------------------------------------------------------
    /**
     * Set the modification version for this object.
     *
     * @param aVersion the modification version number.
     */
    public void enerj_SetVersion(long aVersion)
    {
        enerj_mVersion = aVersion;
    }

    //----------------------------------------------------------------------
    /**
     * Get the object ID this object.
     *
     * @return the OID.
     */
    public long enerj_GetPrivateOID()
    {
        return enerj_mOID;
    }

    //----------------------------------------------------------------------
    public void enerj_SetPrivateOID(long anOID)
    {
        enerj_mOID = anOID;
    }

    //----------------------------------------------------------------------
    public boolean enerj_IsModified() 
    {
        return enerj_mModified;
    }
    
    //----------------------------------------------------------------------
    /** Determines if this object allows Non-Transactional Reads.
     *
     * @return true if it allows Non-Transactionals Reads, else false.
     */
    public boolean enerj_AllowsNonTransactionalRead() 
    {
        return enerj_mAllowNonTransactionalReads;
    }
    
    //----------------------------------------------------------------------
    /** Determines if this object allows Non-Transactional Writes.
     *
     * @return true if it allows Non-Transactionals Writes, else false.
     */
    public boolean enerj_AllowsNonTransactionalWrite() 
    {
        return enerj_mAllowNonTransactionalWrites;
    }
    
    //----------------------------------------------------------------------
    /** Determines if this object has been Loaded.
     *
     * @return true if it has been Loaded, else false.
     */
    public boolean enerj_IsLoaded() 
    {
        return enerj_mLoaded;
    }
    
    //----------------------------------------------------------------------
    /** Determines if this object is New.
     *
     * @return true if it is New, else false.
     */
    public boolean enerj_IsNew() 
    {
        return enerj_mNew;
    }
    
    //----------------------------------------------------------------------
    /** Sets whether this object allows Non-Transactional Reads.
     */
    public void enerj_SetAllowNonTransactionalRead(boolean anAllowFlag) 
    {
        enerj_mAllowNonTransactionalReads = anAllowFlag;
    }
    
    //----------------------------------------------------------------------
    /** Sets whether this object allows Non-Transactional Writes.
     */
    public void enerj_SetAllowNonTransactionalWrite(boolean anAllowFlag) 
    {
        enerj_mAllowNonTransactionalWrites = anAllowFlag;
    }
    
    //----------------------------------------------------------------------
    /** Sets whether this object is Loaded.
     */
    public void enerj_SetLoaded(boolean aLoadedFlag) 
    {
        enerj_mLoaded = aLoadedFlag;
    }
    
    //----------------------------------------------------------------------
    /** Sets whether this object is Modified.
     */
    public void enerj_SetModified(boolean aModifiedFlag) 
    {
        enerj_mModified = aModifiedFlag;
    }
    
    //----------------------------------------------------------------------
    /** Sets whether this object is New.
     */
    public void enerj_SetNew(boolean aNewFlag) 
    {
        enerj_mNew = aNewFlag;
    }
    
    //----------------------------------------------------------------------
    /** Gets the lock level of this object. This is merely the local status of the lock.
     *
     * @return one of the EnerJTransaction lock levels: NO_LOCK, READ,
     *  UPGRADE, or WRITE.
     *
     */
    public int enerj_GetLockLevel() 
    {
        return enerj_mLockLevel;
    }
    
    //----------------------------------------------------------------------
    /** Sets the lock level of this object. This does not actually lock the object,
     * it merely maintains a local status of the lock.
     *
     * @param aLockLevel one of the EnerJTransaction lock levels: NO_LOCK, READ,
     *  UPGRADE, or WRITE.
     *
     */
    public void enerj_SetLockLevel(int aLockLevel) 
    {
        enerj_mLockLevel = aLockLevel;
    }
    
    //----------------------------------------------------------------------
    // If user doesn't define clone() in this, one is generated like this
    // for top-level Persistables. Not that it does NOT throw CloneNotSupportedException.
    public Object clone_Generated() 
    {
        // Object.clone is declared to throw CloneNotSupportedException, while
        // super classes like java.util.Date don't. Just catch it here. It
        // should never happen because we declare Cloneable.
        
        try {
            Object clone = super.clone();
            PersistableHelper.initPersistableClone((Persistable)clone);
            return clone;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    //----------------------------------------------------------------------
    // If user doesn't define clone() in this, one is generated like this
    // for top-level Persistables. Not that it does NOT throw CloneNotSupportedException.
    // Version if super-class cloine is declared to throw - e.g., Object.
    public Object clone_Generated2() 
    {
        try {
            // Object.clone is declared to throw CloneNotSupportedException, while
            // super classes like java.util.Date don't. Just catch it here. It
            // should never happen because we declare Cloneable.
            Object clone = /*super.clone()*/null; 
            PersistableHelper.initPersistableClone((Persistable)clone);
            return clone;
        }
        catch (/*CloneNotSupported*/Exception e) {
            return null;    // Should never happen.
        }
    }

    //----------------------------------------------------------------------
    // Generated readObject() method. If one already exists, initPersistable()
    // is inserted as the first statement.
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        PersistableHelper.initPersistable(this);
        in.defaultReadObject();
    }
    
    //----------------------------------------------------------------------
    // ... End of Generated methods for top-level persistable only.
    //----------------------------------------------------------------------


    //----------------------------------------------------------------------
    // Generated methods for any persistable...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // So we can get a class id when we only have a class and not an object.
    public static long enerj_GetClassIdStatic()
    {
        return enerj_sClassId;
    }
    
    //----------------------------------------------------------------------
    // So we can get a class id via the Persistable interface.
    public long enerj_GetClassId()
    {
        return enerj_sClassId;
    }
    

    //----------------------------------------------------------------------
    public void enerj_ReadObject(ObjectSerializer aContext) throws IOException
    {
        DataInput stream = aContext.getDataInput();

        mByte = stream.readByte();
   }

    //----------------------------------------------------------------------
    public void enerj_WriteObject(ObjectSerializer aContext) throws IOException
    {
        DataOutput stream = aContext.getDataOutput();

        stream.writeByte(mByte);
    }

    //----------------------------------------------------------------------
    public void enerj_ResolveObject(ObjectSerializer aContext, boolean shouldDisassociate) throws IOException
    {
    }
    
    //----------------------------------------------------------------------
    /** Clear the object's persistent fields. Only persistent fields which
     * refer to Objects are cleared (i.e., primitive field values are not touched).
     */
    public void enerj_Hollow() 
    {
        PersistableHelper.completeHollow(this);
    }
    
    //----------------------------------------------------------------------
    // ... End of Generated methods for any persistable.
    //----------------------------------------------------------------------

}

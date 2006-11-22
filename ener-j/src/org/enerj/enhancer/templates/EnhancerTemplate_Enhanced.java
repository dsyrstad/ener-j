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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/templates/EnhancerTemplate_Enhanced.java,v 1.3 2005/08/12 02:56:53 dsyrstad Exp $

package org.enerj.enhancer.templates;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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
 * @version $Id: EnhancerTemplate_Enhanced.java,v 1.3 2005/08/12 02:56:53 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class EnhancerTemplate_Enhanced extends java.util.Date implements Persistable, Cloneable
{
    static int mPackageStaticInt;
    transient int mPackageTransientInt;
    
    private byte mByte;
    private Byte mByteObj;
    private boolean mBoolean;
    private Boolean mBooleanObj;
    private char mChar;
    private Character mCharObj;
    private short mShort;
    private Short mShortObj;
    private int mInt;
    private Integer mIntObj;
    private long mLong;
    private Long mLongObj;
    private float mFloat;
    private Float mFloatObj;
    private double mDouble;
    private Double mDoubleObj;
    private String mString;
    private Object mObject;

    private int[] mIntArray;
    private int[][] m2dArray;
    private Object[] mObjArray;
    
    // The ALLOW_STALE_ flags above must be set on construction. We could get
    // these from the Persister, but on "new XXXClass()" we may not know the Persister
    // that the object is associated with.
    // xtors also have to initialize Transaction to the current one.

    // Added fields:
    // Maybe these should move up to the least-derived persistable class as private - add
    // accessor/mutator methods for them.
    // Note that there may be superclasses which are not persistable. All of their
    // fields are considered transient.

    // Persistent Version number so optimistic concurrency can be done. Automatically
    // updated when modified object is written back to the database.
    // E.g., txn-begin read-obj1 txn-commit, operate on obj1 (outside of txn),
    // txn-begin re-read-obj1 version number, if same update, if not, exception-abort,
    // txn-commit. Persister and Persistable have method to obtain version number.
    // How the re-read to test the version number happens is a bit of a mystery since
    // the updates happen outside of the txn. Something has to bring this object
    // back into the txn. Poet has ObjectServices.awaken(obj) to awaken it again
    // in a transaction.
    // 7/17 - This is not persisted with the object, the DB server must increment this
    // on commit

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

    // Later we need these
    // transient protected EnerJClass enerj_mClass; // The particular version of this class used by this object
        // EnerJClass should contain transient private long voEnhancerVersion;


    // Generated constructor. Persister is simply used for a unique signature.
    public EnhancerTemplate_Enhanced(Persister aPersister)
    {
        super(); // To Date.<init>()
    }


    // User-defined constructor.
    public EnhancerTemplate_Enhanced(int aParam)
    {
        super(34L); // To Date.<init>(long)
        // This is inserted right after the super call.
        PersistableHelper.initPersistable(this);
        
        enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mInt(this, aParam);
    }


    // User-defined methods ...



    public void runOn(int aParam)
    {
        System.out.println("mLong is " + enerj_Get_org_enerj_enhancer_templates_EnhancerTemplate_mLong(this));
        enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mInt(this, aParam);
    }


    // Enhanced version of user-defined clone.
    public Object clone() 
    {
        EnhancerTemplate_Enhanced clone = (EnhancerTemplate_Enhanced)super.clone();
        enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mDouble(clone, 0.);
        enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mIntArray(clone, 
            (int[])enerj_Get_org_enerj_enhancer_templates_EnhancerTemplate_mIntArray(this).clone() );

        // This is inserted just before any return. Note that clone does not
        // require that you call super.clone() to make a copy. So we cannot
        // just insert it after a super.clone() call.
        PersistableHelper.initPersistableClone(clone);
        return clone;
    }


    // Enhanced version of user-defined clone. Version 2 - no super.clone();
    public Object clone2() 
    {
        EnhancerTemplate_Enhanced clone = new EnhancerTemplate_Enhanced(5);
        enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mDouble(clone, 0.);
        enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mIntArray(clone, 
            (int[])enerj_Get_org_enerj_enhancer_templates_EnhancerTemplate_mIntArray(this).clone() );

        // This is inserted just before any return. Note that clone does not
        // require that you call super.clone() to make a copy. So we cannot
        // just insert it after a super.clone() call.
        PersistableHelper.initPersistableClone(clone);
        return clone;
    }


    /**
     * User callback.
     */
    private void enerjPreHollow()
    {
    }
    

    // ...End of User-defined methods.



    // Generated methods for top-level persistable only. These come from the 
    // Persistable interface...



    // May be null if Persister not assigned yet (it must be New in this case).
    public final Persister enerj_GetPersister()
    {
        return enerj_mPersister;
    }


    public final void enerj_SetPersister(Persister aPersister)
    {
        enerj_mPersister = aPersister;
    }


    /**
     * Get the modification version number associated with this object.
     *
     * @return the Version.
     */
    public long enerj_GetVersion()
    {
        return enerj_mVersion;
    }


    /**
     * Set the modification version for this object.
     *
     * @param aVersion the modification version number.
     */
    public void enerj_SetVersion(long aVersion)
    {
        enerj_mVersion = aVersion;
    }


    /**
     * Get the object ID this object.
     *
     * @return the OID.
     */
    public long enerj_GetPrivateOID()
    {
        return enerj_mOID;
    }


    public void enerj_SetPrivateOID(long anOID)
    {
        enerj_mOID = anOID;
    }


    public boolean enerj_IsModified() 
    {
        return enerj_mModified;
    }
    

    /** Determines if this object allows Non-Transactional Reads.
     *
     * @return true if it allows Non-Transactionals Reads, else false.
     */
    public boolean enerj_AllowsNonTransactionalRead() 
    {
        return enerj_mAllowNonTransactionalReads;
    }
    

    /** Determines if this object allows Non-Transactional Writes.
     *
     * @return true if it allows Non-Transactionals Writes, else false.
     */
    public boolean enerj_AllowsNonTransactionalWrite() 
    {
        return enerj_mAllowNonTransactionalWrites;
    }
    

    /** Determines if this object has been Loaded.
     *
     * @return true if it has been Loaded, else false.
     */
    public boolean enerj_IsLoaded() 
    {
        return enerj_mLoaded;
    }
    

    /** Determines if this object is New.
     *
     * @return true if it is New, else false.
     */
    public boolean enerj_IsNew() 
    {
        return enerj_mNew;
    }
    

    /** Sets whether this object allows Non-Transactional Reads.
     */
    public void enerj_SetAllowNonTransactionalRead(boolean anAllowFlag) 
    {
        enerj_mAllowNonTransactionalReads = anAllowFlag;
    }
    

    /** Sets whether this object allows Non-Transactional Writes.
     */
    public void enerj_SetAllowNonTransactionalWrite(boolean anAllowFlag) 
    {
        enerj_mAllowNonTransactionalWrites = anAllowFlag;
    }
    

    /** Sets whether this object is Loaded.
     */
    public void enerj_SetLoaded(boolean aLoadedFlag) 
    {
        enerj_mLoaded = aLoadedFlag;
    }
    

    /** Sets whether this object is Modified.
     */
    public void enerj_SetModified(boolean aModifiedFlag) 
    {
        enerj_mModified = aModifiedFlag;
    }
    

    /** Sets whether this object is New.
     */
    public void enerj_SetNew(boolean aNewFlag) 
    {
        enerj_mNew = aNewFlag;
    }
    

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
    

    // If user doesn't define clone() in this, one is generated like this
    // for top-level Persistables. Not that it does NOT throw CloneNotSupportedException.
    public Object clone_Generated() 
    {
        // Object.clone is declared to throw CloneNotSupportedException, while
        // super classes like java.util.Date don't. Just catch it here. It
        // should never happen because we declare Cloneable.
        
        // Version if Super-class not declared to throw.
        Object clone = super.clone();
        PersistableHelper.initPersistableClone((Persistable)clone);
        return clone;
    }
    

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


    // ... End of Generated methods for top-level persistable only.



    // Generated methods for any persistable...



    // So we can get a class id when we only have a class and not an object.
    public static long enerj_GetClassIdStatic()
    {
        return enerj_sClassId;
    }
    

    // So we can get a class id via the Persistable interface.
    public long enerj_GetClassId()
    {
        return enerj_sClassId;
    }
    

    // Modifier matches original field modifier (none - package)
    static int enerj_Get_org_enerj_enhancer_templates_EnhancerTemplate_mPackageStaticInt()
    {
        return mPackageStaticInt;
    }


    // Modifier matches original field modifier (none - package)
    static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mPackageStaticInt(int aValue)
    {
        mPackageStaticInt = aValue;
    }


    // Modifier matches original field modifier (none - package)
    static int enerj_Get_org_enerj_enhancer_templates_EnhancerTemplate_mPackageTransientInt(EnhancerTemplate_Enhanced anInstance)
    {
        return anInstance.mPackageTransientInt;
    }


    // Modifier matches original field modifier (none - package)
    static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mPackageTransientInt(EnhancerTemplate_Enhanced anInstance,
                int aValue)
    {
        anInstance.mPackageTransientInt = aValue;
    }


    private // Modifier matches original field modifier
        static long
            enerj_Get_org_enerj_enhancer_templates_EnhancerTemplate_mLong(EnhancerTemplate_Enhanced  anInstance)
    {
        // Must be in a transaction... Unless we allow stale reads
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, false);
        }
        return anInstance.mLong;
    }


    private // Modifier matches original field modifier
        static int[]
            enerj_Get_org_enerj_enhancer_templates_EnhancerTemplate_mIntArray(EnhancerTemplate_Enhanced  anInstance)
    {
        // Must be in a transaction... Unless we allow stale reads
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, false);
        }
        return anInstance.mIntArray;
    }


    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mLong(EnhancerTemplate_Enhanced  anInstance,
                long  aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mLong) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mLong = aValue;
        }
    }


    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mDouble(EnhancerTemplate_Enhanced  anInstance,
                double  aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mDouble) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mDouble = aValue;
        }
    }


    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mInt(EnhancerTemplate_Enhanced  anInstance,
                int  aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mInt) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mInt = aValue;
        }
    }


    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mFloat(EnhancerTemplate_Enhanced  anInstance,
                float  aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mFloat) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mFloat = aValue;
        }
    }


    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mShort(EnhancerTemplate_Enhanced  anInstance,
                short  aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mShort) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mShort = aValue;
        }
    }


    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mIntArray(EnhancerTemplate_Enhanced  anInstance,
                int[] aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mIntArray) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mIntArray = aValue;
        }
    }


    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplate_mString(EnhancerTemplate_Enhanced  anInstance,
                String aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }

        if ((aValue == null && anInstance.mString != null) || (aValue != null && !aValue.equals(anInstance.mString))) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mString = aValue;
        }
    }


    public void enerj_ReadObject(ObjectSerializer aContext) throws IOException
    {
        DataInput stream = aContext.getDataInput();

        mByte = stream.readByte();
        mByteObj = (java.lang.Byte)aContext.readObject(this);
        mBoolean = stream.readBoolean();
        mBooleanObj = (java.lang.Boolean)aContext.readObject(this);
        mChar = stream.readChar();
        mCharObj = (java.lang.Character)aContext.readObject(this);
        mShort = stream.readShort();
        mShortObj = (java.lang.Short)aContext.readObject(this);
        mInt = stream.readInt();
        mIntObj = (java.lang.Integer)aContext.readObject(this);
        mLong = stream.readLong();
        mLongObj = (java.lang.Long)aContext.readObject(this);
        mFloat = stream.readFloat();
        mFloatObj = (java.lang.Float)aContext.readObject(this);
        mDouble = stream.readDouble();
        mDoubleObj = (java.lang.Double)aContext.readObject(this);
        mString = (String)aContext.readObject(this);
        mObject = (Object)aContext.readObject(this);
        mIntArray = (int[])aContext.readObject(this);
        m2dArray = (int[][])aContext.readObject(this);
        mObjArray = (Object[])aContext.readObject(this);
   }


    public void enerj_WriteObject(ObjectSerializer aContext) throws IOException
    {
        DataOutput stream = aContext.getDataOutput();

        stream.writeByte(mByte);
        aContext.writeObject(mByteObj, this);
        stream.writeBoolean(mBoolean);
        aContext.writeObject(mBooleanObj, this);
        stream.writeChar(mChar);
        aContext.writeObject(mCharObj, this);
        stream.writeShort(mShort);
        aContext.writeObject(mShortObj, this);
        stream.writeInt(mInt);
        aContext.writeObject(mIntObj, this);
        stream.writeLong(mLong);
        aContext.writeObject(mLongObj, this);
        stream.writeFloat(mFloat);
        aContext.writeObject(mFloatObj, this);
        stream.writeDouble(mDouble);
        aContext.writeObject(mDoubleObj, this);
        aContext.writeObject(mString, this);
        aContext.writeObject(mObject, this);
        aContext.writeObject(mIntArray, this);
        aContext.writeObject(m2dArray, this);
        aContext.writeObject(mObjArray, this);
    }


    public void enerj_ResolveObject(ObjectSerializer aContext, boolean shouldDisassociate) throws IOException
    {
        aContext.resolveObject(mByteObj, shouldDisassociate);
        aContext.resolveObject(mBooleanObj, shouldDisassociate);
        aContext.resolveObject(mCharObj, shouldDisassociate);
        aContext.resolveObject(mShortObj, shouldDisassociate);
        aContext.resolveObject(mIntObj, shouldDisassociate);
        aContext.resolveObject(mLongObj, shouldDisassociate);
        aContext.resolveObject(mFloatObj, shouldDisassociate);
        aContext.resolveObject(mDoubleObj, shouldDisassociate);
        aContext.resolveObject(mString, shouldDisassociate);
        aContext.resolveObject(mObject, shouldDisassociate);
        aContext.resolveObject(mIntArray, shouldDisassociate);
        aContext.resolveObject(m2dArray, shouldDisassociate);
        aContext.resolveObject(mObjArray, shouldDisassociate);
    }

    

    /** Clear the object's persistent fields. Only persistent fields which
     * refer to Objects are cleared (i.e., primitive field values are not touched).
     */
    public void enerj_Hollow() 
    {
        enerjPreHollow();
        
        mByteObj = null;
        mBooleanObj = null;
        mCharObj = null;
        mShortObj = null;
        mIntObj = null;
        mLongObj = null;
        mFloatObj = null;
        mDoubleObj = null;
        mString = null;
        mObject = null;
        mIntArray = null;
        m2dArray = null;
        mObjArray = null;
        
        PersistableHelper.completeHollow(this);
    }
    

    // ... End of Generated methods for any persistable.


}

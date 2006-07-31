// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/LockMode.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

/**
 * Type-safe enumeration of lock modes.
 *
 * @version $Id: LockMode.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public final class LockMode
{
    /** The lock mode for Read. */
    public static final LockMode READ = new LockMode("READ", 0);
    /** The lock mode for Upgrade. */
    public static final LockMode UPGRADE = new LockMode("UPGRADE", 1);
    /** The lock mode for Write. */
    public static final LockMode WRITE = new LockMode("WRITE", 2);

    /** The compatibility matrix. Indexed by mExclusivity. */
    private static final boolean[][] mCompatibilityMatrix = new boolean[][] {
        /* Granted | Requested: READ   UPGRADE WRITE */
        /* READ */            { true,  true,   false },
        /* UPGRADE */         { true,  false,  false, },
        /* WRITE */           { false, false,  false },
    };

    private String mName;
    private int mExclusivity;

    //----------------------------------------------------------------------
    private LockMode(String aName, int aExclusivity)
    {
        mName = aName;
        mExclusivity = aExclusivity;
    }

    //----------------------------------------------------------------------
    /** 
     * Checks if this LockMode is more exclusive than the given mode.
     *
     * @param aLockMode the LockMode to be compared.
     *
     * @return true if this LockMode more exclusive than the given mode, or
     *  false if this mode is the same or less exclusive than aLockMode.
     */
    boolean isMoreExclusiveThan(LockMode aLockMode)
    {
        return (mExclusivity - aLockMode.mExclusivity) > 0;
    }

    //----------------------------------------------------------------------
    /**
     * Checks to see if this lock mode (considered the "granted" mode) is
     * compatible with (i.e., does not conflict with) the given requested mode.
     *
     * @param aRequestedMode the lock mode being requested.
     *
     * @return true if this mode is compatible with aRequestedMode or false if
     *  it conflicts with it.
     */
    boolean isCompatible(LockMode aRequestedMode)
    {
        return mCompatibilityMatrix[mExclusivity][aRequestedMode.mExclusivity];
    }

    //----------------------------------------------------------------------
    public String toString()
    {
        return mName;
    }
}

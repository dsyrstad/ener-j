// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/DeadlockAlgorithm.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

/**
 * Type-safe enumeration of deadlock algorithms.
 *
 * @version $Id: DeadlockAlgorithm.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 * @see LockScheduler
 */
public final class DeadlockAlgorithm
{
    /** Use the accurate, but more time consuming "waits-for" algorithm. */
    public static final DeadlockAlgorithm WAITS_FOR = new DeadlockAlgorithm("Waits-For");
    /** Use the less accurate, but much less time consuming timestamp algorithm. */
    public static final DeadlockAlgorithm TIMESTAMP = new DeadlockAlgorithm("Timestamp");

    private String mName;

    //----------------------------------------------------------------------
    private DeadlockAlgorithm(String aName)
    {
        mName = aName;
    }

    //----------------------------------------------------------------------
    public String toString()
    {
        return mName;
    }
}

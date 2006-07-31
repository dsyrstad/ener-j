// Ener-J
// Copyright 2001 - 2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/VolumeNeedsRecoveryException.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;



/**
 * Exception thrown from a Ener-J Page Server if a volume was not closed properly.
 * Indicates that the ObjectServer should perform recovery.
 *
 * @version $Id: VolumeNeedsRecoveryException.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class VolumeNeedsRecoveryException extends PageServerException
{
    //----------------------------------------------------------------------
    public VolumeNeedsRecoveryException()
    {
        super();
    }

    //----------------------------------------------------------------------
    public VolumeNeedsRecoveryException(String aMessage)
    {
        super(aMessage);
    }

    //----------------------------------------------------------------------
    public VolumeNeedsRecoveryException(String aMessage, Throwable aCause)
    {
        super(aMessage, aCause);
    }

    //----------------------------------------------------------------------
    public VolumeNeedsRecoveryException(Throwable aCause)
    {
        super(aCause);
    }

}


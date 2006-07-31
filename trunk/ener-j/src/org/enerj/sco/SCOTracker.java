// Ener-J 
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/sco/SCOTracker.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.sco;

import org.enerj.core.*;

/**
 * Interface for all Second Class Object subclasses.
 *
 * All trackers must also implement clone(), but they must disassociate
 * the owner FCO from the clone.
 *
 * @version $Id: SCOTracker.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface SCOTracker 
{
    //----------------------------------------------------------------------
    /**
     * Gets the owner FCO.
     *
     * @return the owner FCO.
     */
    public Persistable getOwnerFCO();

    //----------------------------------------------------------------------
    /**
     * Sets the owner FCO.
     *
     * @param anOwner the owner FCO.
     */
    public void setOwnerFCO(Persistable anOwner);

    //----------------------------------------------------------------------
    /**
     * Sets the owner FCO, if non-null, to a modified state.
     */
    public void setOwnerModified();
}

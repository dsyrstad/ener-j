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

    /**
     * Gets the owner FCO.
     *
     * @return the owner FCO.
     */
    public Persistable getOwnerFCO();


    /**
     * Sets the owner FCO.
     *
     * @param anOwner the owner FCO.
     */
    public void setOwnerFCO(Persistable anOwner);


    /**
     * Sets the owner FCO, if non-null, to a modified state.
     */
    public void setOwnerModified();
}

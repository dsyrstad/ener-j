// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/model/ObjectSourceListener.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.model;


/**
 * Interface to listen for changes to an ObjectSource.
 * <p>
 * 
 * @version $Id: ObjectSourceListener.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public interface ObjectSourceListener extends java.util.EventListener
{

    /**
     * Invoked when a change to an ObjectSource occurs.
     *
     * @param anEvent The ObjectSourceEvent describing the change.
     */
    public void notifyObjectSourceChanged(ObjectSourceEvent anEvent);
}

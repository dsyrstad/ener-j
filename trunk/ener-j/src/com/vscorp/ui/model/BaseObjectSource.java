// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/model/BaseObjectSource.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.model;


/**
 * Abstract base class for classes wanting to implement ObjectSource.
 *
 * @version $Id: BaseObjectSource.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
abstract public class BaseObjectSource implements ObjectSource
{
    private javax.swing.event.EventListenerList mListenerList =
                new javax.swing.event.EventListenerList();

    //----------------------------------------------------------------------
    // From ObjectSource...
    public void addObjectSourceListener(ObjectSourceListener aListener)
    {
        mListenerList.add(ObjectSourceListener.class, aListener);
    }

    //----------------------------------------------------------------------
    // From ObjectSource...
    public void removeObjectSourceListener(ObjectSourceListener aListener)
    {
        mListenerList.remove(ObjectSourceListener.class, aListener);
    }

    //----------------------------------------------------------------------
    /**
     * Fires the specified event to all ObjectSourceListeners in the list.
     * Listeners are fired starting with the last one added.
     *
     * @param anEvent the event to be fired.
     */
    public void fireObjectSourceChanged(ObjectSourceEvent anEvent)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = mListenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            ((ObjectSourceListener)listeners[ i + 1 ]).notifyObjectSourceChanged(anEvent);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tells all ObjectSourceListeners in the list that the ObjectSource's
     * contents have changed.
     */
    public void fireObjectSourceContentsChanged()
    {
        fireObjectSourceChanged( new ObjectSourceEvent(this) );
    }

    //----------------------------------------------------------------------
    /**
     * Tells all ObjectSourceListeners in the list that a range of objects in the
     * ObjectSource have changed.
     *
     * @param aStartIndex the starting index of the range
     *
     * @param anEndIndex the ending index of the range
     */
    public void fireObjectSourceRangeChanged(int aStartIndex, int anEndIndex)
    {
        fireObjectSourceChanged( new ObjectSourceEvent(this,
                                    ObjectSourceEvent.RANGE_CHANGED, aStartIndex,
                                    anEndIndex) );
    }

    //----------------------------------------------------------------------
    /**
     * Tells all ObjectSourceListeners in the list that a range of objects in the
     * ObjectSource have been added.
     *
     * @param aStartIndex the starting index of the range
     *
     * @param anEndIndex the ending index of the range
     */
    public void fireObjectSourceRangeAdded(int aStartIndex, int anEndIndex)
    {
        fireObjectSourceChanged( new ObjectSourceEvent(this,
                                    ObjectSourceEvent.RANGE_ADDED, aStartIndex,
                                    anEndIndex) );
    }

    //----------------------------------------------------------------------
    /**
     * Tells all ObjectSourceListeners in the list that a range of objects in the
     * ObjectSource have been removed.
     *
     * @param aStartIndex the starting index of the range
     *
     * @param anEndIndex the ending index of the range
     */
    public void fireObjectSourceRangeDeleted(int aStartIndex, int anEndIndex)
    {
        fireObjectSourceChanged( new ObjectSourceEvent(this,
                                    ObjectSourceEvent.RANGE_DELETED, aStartIndex,
                                    anEndIndex) );
    }

    //----------------------------------------------------------------------
    /**
     * Tells all ObjectSourceListeners in the list that an object in the
     * ObjectSource has changed.
     *
     * @param anObjectId an object id returned by getObjectId
     */
    public void fireObjectSourceObjectChanged(Object anObjectId)
    {
        fireObjectSourceChanged( new ObjectSourceEvent(this,
                                    ObjectSourceEvent.OBJECT_CHANGED, anObjectId) );
    }

    //----------------------------------------------------------------------
    /**
     * Tells all ObjectSourceListeners in the list that an object in the
     * ObjectSource has been added.
     *
     * @param anObjectId an object id returned by getObjectId
     */
    public void fireObjectSourceObjectAdded(Object anObjectId)
    {
        fireObjectSourceChanged( new ObjectSourceEvent(this,
                                    ObjectSourceEvent.OBJECT_ADDED, anObjectId) );
    }

    //----------------------------------------------------------------------
    /**
     * Tells all ObjectSourceListeners in the list that an object in the
     * ObjectSource has been deleted.
     *
     * @param anObjectId an object id returned by getObjectId
     */
    public void fireObjectSourceObjectDeleted(Object anObjectId)
    {
        fireObjectSourceChanged( new ObjectSourceEvent(this,
                                    ObjectSourceEvent.OBJECT_DELETED, anObjectId) );
    }
}

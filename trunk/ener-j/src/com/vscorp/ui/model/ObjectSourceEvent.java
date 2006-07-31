// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/model/ObjectSourceEvent.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.model;


/**
 * Event for changes to an ObjectSource. Declares a common event sent on all ObjectSource changes.
 * 
 * @version $Id: ObjectSourceEvent.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ObjectSourceEvent extends java.util.EventObject
{
    /**
     * Event type indicating that the specified Object Id has changed.
     * See getObjectId.
     */
    public static final int OBJECT_CHANGED = 0;
    /**
     * Event type indicating that the specified Object Id has been added.
     * See getObjectId.
     */
    public static final int OBJECT_ADDED = 1;
    /**
     * Event type indicating that the specified Object Id has been deleted.
     * See getObjectId.
     */
    public static final int OBJECT_DELETED = 2;
    /**
     * Event type indicating that the specified range of objects have changed.
     * See getStartIndex, getEndIndex
     */
    public static final int RANGE_CHANGED = 3;
    /**
     * Event type indicating that the specified range of objects have been added.
     * See getStartIndex, getEndIndex
     */
    public static final int RANGE_ADDED = 4;
    /**
     * Event type indicating that the specified range of objects have been deleted.
     * See getStartIndex, getEndIndex
     */
    public static final int RANGE_DELETED = 5;
    /**
     * Event type indicating that the entire contents have changed.
     */
    public static final int CONTENTS_CHANGED = 6;

    private int mType;
    private Object mObjectId = null;
    private int mStartIndex = -1;
    private int mEndIndex = -1;

    //----------------------------------------------------------------------
    /**
     * Constructs an event specify that the entire contents have changed
     * (CONTENTS_CHANGED type).
     *
     * @param aSource the ObjectSource generating the event.
     *
     */
    public ObjectSourceEvent(ObjectSource aSource)
    {
        super(aSource);
        mType = CONTENTS_CHANGED;
    }

    //----------------------------------------------------------------------
    /**
     * Constructs an event representing an add/change/deletion of a specific
     * object id.
     *
     * @param aSource the ObjectSource generating the event.
     *
     * @param aType an event type. One of: OBJECT_ADDED, OBJECT_CHANGED,
     *  or OBJECT_DELETED.
     *
     * @param anObjectId the object id, returned by ObjectSource.getObjectId,
     *  that is the target of the event.
     */
    public ObjectSourceEvent(ObjectSource aSource, int aType, Object anObjectId)
    {
        super(aSource);
        mType = aType;
        mObjectId = anObjectId;
    }

    //----------------------------------------------------------------------
    /**
     * Constructs an event representing an add/change/deletion of a range of
     * objects.
     *
     * @param aSource the ObjectSource generating the event.
     *
     * @param aType an event type. One of: OBJECT_ADDED, OBJECT_CHANGED,
     *  or OBJECT_DELETED.
     *
     * @param aStartIndex The starting index of the update
     *
     * @param anEndIndex The ending index of the update
     */
    public ObjectSourceEvent(ObjectSource aSource, int aType, int aStartIndex,
                             int anEndIndex)
    {
        super(aSource);
        mType = aType;
        mStartIndex = aStartIndex;
        mEndIndex = anEndIndex;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the event type.
     *
     * @return the event type. One of OBJECT_*, RANGE_*, or CONTENTS_CHANGED.
     */
    public int getType()
    {
        return mType;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the object Id associated with the event.
     *
     * @return the object id originally generated from ObjectSource.getObjectId.
     */
    public Object getObjectId()
    {
        return mObjectId;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the start of the range.
     *
     * @return the index representing the start of the update range. May return
     * -1 if the event does not represent a range update.
     */
    public int getStartIndex()
    {
        return mStartIndex;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the end of the range.
     *
     * @return the index representing the end of the update range. May return
     * -1 if the event does not represent a range update.
     */
    public int getEndIndex()
    {
        return mEndIndex;
    }
}

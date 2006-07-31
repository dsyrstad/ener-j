// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/model/ObjectSource.java,v 1.2 2005/11/29 03:55:50 dsyrstad Exp $

package com.vscorp.ui.model;


/**
 * Object Source Interface. Provides a common interface for all object sources so that common services
 * may be built on top of them. An object source is something that provides
 * a collection of Objects that may be accessed by index or by an object ID.
 * The intent of ObjectSource is to abstract the access
 * to objects, although it may also be used for
 * other purposes.
 * <p>
 * 
 * @version $Id: ObjectSource.java,v 1.2 2005/11/29 03:55:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public interface ObjectSource
{
    //----------------------------------------------------------------------
    /**
     * Adds a listener to the list that is notified each time a change to
     * the object source occurs.
     *
     * @param aListener the listener to be added.
     */
    public void addObjectSourceListener(ObjectSourceListener aListener);

    //----------------------------------------------------------------------
    /**
     * Removes a listener from the list that is notified each time a change to
     * the object source occurs.
     *
     * @param aListener the listener to be removed.
     */
    public void removeObjectSourceListener(ObjectSourceListener aListener);

    //----------------------------------------------------------------------
    /**
     * Gets the number of objects represented by this object source.
     *
     * @return the number of objects in the object source
     *
     * @throws ObjectSourceException if an access exception occurs
     */
    public int size() throws ObjectSourceException;

    //----------------------------------------------------------------------
    /**
     * Gets the object at the specified index.
     *
     * @param anIndex the index of the desired object. Must be between zero and
     *   (size() - 1) inclusive.
     *
     * @return the desired object.
     *
     * @throws ObjectSourceException if an access exception occurs
     * @throws java.lang.ArrayIndexOutOfBoundsException if anIndex is out of range.
     */
    public Object get(int anIndex)
        throws ObjectSourceException, java.lang.ArrayIndexOutOfBoundsException;

    //----------------------------------------------------------------------
    /**
     * Gets an array of objects starting at the specified index.
     *
     * @param anIndex the index of the desired object. Must be between zero and
     *   (size() - 1) inclusive.
     * @param aLength the number of objects to retrieve. Must be between zero and
     *   (size() - anIndex) inclusive.
     * @param anObjectArray an array of at least aLength elements that will
     *   receive the results. The array should actually be of the type that
     *   is proper for your object. For example, new MyObject[aLength].
     *
     * @return void. anObjectArray is filled in with the desired objects.
     *
     * @throws ObjectSourceException if an access exception occurs
     * @throws java.lang.ArrayIndexOutOfBoundsException if anIndex and/or
     * aLength is out of range.
     */
    public void get(int anIndex, int aLength, Object[] anObjectArray)
        throws ObjectSourceException, java.lang.ArrayIndexOutOfBoundsException;

    //----------------------------------------------------------------------
    /**
     * Gets the object at with the specified object Id.
     *
     * @param anObjectId the object Id of the desired object.
     *
     * @return the desired object, or null if the object does not exist.
     *
     * @throws ObjectSourceException if an access exception occurs
     */
    public Object get(Object anObjectId)
        throws ObjectSourceException;

    //----------------------------------------------------------------------
    /**
     * Updates the object at the specified index. Fires an ObjectSourceEvent of
     * ObjectSourceEvent.RANGE_CHANGED to the registered listeners.
     *
     * @param anObject an updated version of the object originally delivered by
     *  this source. Must contain the original object id.
     *
     * @param anIndex the index in the ObjectSource of the object.
     *
     * @throws ObjectSourceException if an access exception occurs
     */
    public void update(Object anObject, int anIndex)
        throws ObjectSourceException;

    //----------------------------------------------------------------------
    /**
     * Updates the object when the index is not known.  Fires an ObjectSourceEvent of
     * ObjectSourceEvent.OBJECT_CHANGED to the registered listeners.
     *
     * @param anObject an updated version of the object originally delivered by
     *  this source. Must contain the original object id.
     *
     * @param anIndex the index in the ObjectSource of the object.
     *
     * @throws ObjectSourceException if an access exception occurs
     */
    public void update(Object anObject)
        throws ObjectSourceException;

    //----------------------------------------------------------------------
    /**
     * Gets the object Id associated with the specified object. The id can be any type of
     * object (e.g., a String or an Integer), but it must follow these rules:
     * <p>
     * 1. For any given type of object, the same type of object id
     * must be returned. This ensures that the ids are comparable.<p>
     * 2. The object id must implement the equals() method and return
     * true if the two ids are equivalent. It must return false if they are not.<p>
     * 3. The object id must implement the hashCode() method.
     * <p>
     * This call should performly quickly and locally. It's assumed that the object id is stored on
     * the local object itself.
     *
     * @param anObject the object who's id is returned
     *
     * @return the object's id, or null if the object is not recognized or
     * the Id is not known.
     */
    public Object getObjectId(Object anObject);
}

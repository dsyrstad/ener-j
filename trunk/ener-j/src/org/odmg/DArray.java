
package org.odmg;

/**
* The interface that defines the operations of an ODMG array.
* Nearly all of its operations are defined by the JavaSoft <code>List</code> interface.
* All of the operations defined by the JavaSoft <code>List</code>
* interface are supported by an ODMG implementation of <code>DArray</code>,
* the exception <code>UnsupportedOperationException</code> is not thrown when a
* call is made to any of the <code>List</code> methods.
* An instance of a class implementing this interface can be obtained
* by calling the method <code>Implementation.newDArray</code>.
* @author	David Jordan (as Java Editor of the Object Data Management Group)
* @version ODMG 3.0
*/
// @see java.lang.UnsupportedOperationException

public interface DArray<E> extends org.odmg.DCollection<E>, java.util.List<E>,  java.util.RandomAccess
{
    /**
     * Resize the array to have <code>aNewSize</code> elements.
     * aNewSize must be greater than or equal to the current array size.
     * 
     * @param	newSize	The new size of the array.
     *
     * @throws IllegalArgumentException if aNewSize is less than the current
     *  array size.
     */
	public void resize(int aNewSize);
}


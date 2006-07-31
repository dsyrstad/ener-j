
package org.odmg;

/**
* The base interface for all ODMG collections.
* The ODMG collections are based on JavaSoft’s collection interfaces.
* All of the operations defined by the JavaSoft <code>Collection</code>
* interface are supported by an ODMG implementation of <code>DCollection</code>;
* the exception <code>UnsupportedOperationException</code> is not thrown when a
* call is made to any of the <code>Collection</code> methods.
* <p>
* <code>DCollection</code> contains methods used to perform queries on the collection.
* The OQL query predicate is given as a string with the syntax of the
* <code>where</code> clause of OQL. The predefined OQL variable <code>this</code>
* is used inside the predicate to denote the current element of the collection.
* @author	David Jordan (as Java Editor of the Object Data Management Group)
* @version ODMG 3.0
*/
// * @see com.sun.java.util.collections.UnsupportedOperationException

public interface DCollection extends java.util.Collection, QueryableCollection
{
}


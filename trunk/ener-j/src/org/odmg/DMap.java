
package org.odmg;

/**
* The ODMG Map collection interface.
* All of the operations defined by the JavaSoft <code>Map</code>
* interface are supported by an ODMG implementation of <code>DMap</code>,
* the exception <code>UnsupportedOperationException</code> is not thrown when a
* call is made to any of the <code>Map</code> methods.
* @author	David Jordan (as Java Editor of the Object Data Management Group)
* @version ODMG 3.0
*/
public interface DMap<K,V> extends java.util.Map<K,V>, QueryableCollection<V>
{
}


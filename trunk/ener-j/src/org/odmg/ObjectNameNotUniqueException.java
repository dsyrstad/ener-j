
package org.odmg;

/**
 * This exception is thrown when attempting to bind a name to an object
 * when the name is already bound to another object.
 * @author	David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 * @see org.odmg.Database#bind
 */

public class ObjectNameNotUniqueException extends ODMGException {
    /**
     * Construct an instance of the exception.
     */
    public ObjectNameNotUniqueException() {
        super();
    }
    
    /**
     * Construct an instance of the exception with a descriptive message.
     * @param	msg	A message containing a description of the exception.
     */
    public ObjectNameNotUniqueException(String msg) {
        super(msg);
    }


    /**
     * Construct with an error message and a cause.
     * @param msg	The error message associated with this exception.
     * @param cause the original cause.
     */
    public ObjectNameNotUniqueException(String msg, Throwable cause) 
    {
        super(msg, cause);
    }
}


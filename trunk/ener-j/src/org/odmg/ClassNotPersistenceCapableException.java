

package org.odmg;

/**
 * This exception is thrown when the implementation cannot make an object persistent because of the type of the object.
 * @author	David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 */

public class ClassNotPersistenceCapableException extends ODMGRuntimeException {
    
    /**
     * Construct an instance of the exception.
     */
    public ClassNotPersistenceCapableException() {
        super();
    }
    
    /**
     * Construct an instance of the exception.
     * @param	msg	A string providing a description of the exception.
     */
    public ClassNotPersistenceCapableException(String msg) {
        super(msg);
    }

    //--------------------------------------------------------------------------
    /**
     * Construct with an error message and a cause.
     * @param msg	The error message associated with this exception.
     * @param cause the original cause.
     */
    public ClassNotPersistenceCapableException(String msg, Throwable cause) 
    {
        super(msg, cause);
    }
}
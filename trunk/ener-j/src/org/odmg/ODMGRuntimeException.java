
package org.odmg;

/**
 * This is the base class for all RuntimeExceptions thrown by an ODMG implementation.
 * @author	David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 */

public class ODMGRuntimeException extends RuntimeException {
    
    /**
     * Construct an instance of the exception.
     */
    public ODMGRuntimeException() {
        super();
    }
    
    /**
     * Construct an instance of the exception with the specified message.
     * @param	msg	The message associated with the exception.
     */
    public ODMGRuntimeException(String msg) {
        super(msg);
    }


    /**
     * Construct with an error message and a cause.
     * @param msg	The error message associated with this exception.
     * @param cause the original cause.
     */
    public ODMGRuntimeException(String msg, Throwable cause) 
    {
        super(msg, cause);
    }


    /**
     * Construct with an error message and a cause.
     * 
     * @param cause the original cause.
     */
    public ODMGRuntimeException(Throwable cause) 
    {
        super(cause);
    }
}


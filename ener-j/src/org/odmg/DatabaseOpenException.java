
package org.odmg;

/**
 * This exception is thrown when attempting to open a database that is already open.
 * @author	David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 * @see org.odmg.Database#open
 */

public class DatabaseOpenException extends ODMGException {
    /**
     * Construct an instance of the exception.
     */
    public DatabaseOpenException() {
        super();
    }
    
    /**
     * Construct an instance of the exception with a descriptive message.
     * @param	msg	A message indicating why the exception occurred.
     */
    public DatabaseOpenException(String msg) {
        super(msg);
    }


    /**
     * Construct with an error message and a cause.
     * @param msg	The error message associated with this exception.
     * @param cause the original cause.
     */
    public DatabaseOpenException(String msg, Throwable cause) 
    {
        super(msg, cause);
    }
}


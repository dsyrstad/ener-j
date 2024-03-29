
package org.odmg;

/**
 * This exception is thrown when attempting to open a database that does not exist.
 * This could be caused by the name provided to <code>Database.open</code> being incorrect.
 * @author	David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 * @see org.odmg.Database#open
 */

public class DatabaseNotFoundException extends ODMGException {
    /**
     * Construct an instance of the exception.
     */
    public DatabaseNotFoundException() {
        super();
    }
    
    
    /**
     * Construct an instance of the exception with a descriptive message.
     * @param	msg	A message indicating why the exception occurred.
     */
    public DatabaseNotFoundException(String msg) {
        super(msg);
    }


    /**
     * Construct with an error message and a cause.
     * @param msg	The error message associated with this exception.
     * @param cause the original cause.
     */
    public DatabaseNotFoundException(String msg, Throwable cause) 
    {
        super(msg, cause);
    }
}


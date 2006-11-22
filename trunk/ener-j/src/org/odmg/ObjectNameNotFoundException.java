
package org.odmg;

/**
 * An attempt to get a object via its name using <code>Database.lookup</code>
 * and the name is not associated with an object in the database.
 * @author	David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 * @see org.odmg.Database#lookup
 */

public class ObjectNameNotFoundException extends ODMGException {
    /**
     * Construct an instance of the exception.
     */
    public ObjectNameNotFoundException() {
        super();
    }
    
    /**
     * Construct an instance of the exception with a descriptive message.
     * @param msg	A message describing the exception.
     */
    public ObjectNameNotFoundException(String msg) {
        super(msg);
    }


    /**
     * Construct with an error message and a cause.
     * @param msg	The error message associated with this exception.
     * @param cause the original cause.
     */
    public ObjectNameNotFoundException(String msg, Throwable cause) 
    {
        super(msg, cause);
    }
}



package org.odmg;

/**
 * This exception is thrown when the database asynchronously and explicitly
 * aborts the user's transaction due to some failure, the user's data is reset
 * just as if the user had directly called <code>Transaction.abort</code>.
 * @author	David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 */

public class TransactionAbortedException extends ODMGRuntimeException {
    /**
     * Constructs an instance of the exception.
     */
    public TransactionAbortedException() {
        super();
    }
    
    /**
     * Constructs an instance of the exception with the provided message.
     * @param	msg	The message that describes the exception.
     */
    public TransactionAbortedException(String msg) {
        super(msg);
    }


    /**
     * Construct with an error message and a cause.
     * @param msg	The error message associated with this exception.
     * @param cause the original cause.
     */
    public TransactionAbortedException(String msg, Throwable cause) 
    {
        super(msg, cause);
    }
}


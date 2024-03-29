
package org.odmg;

/**
 * This exception is thrown when attempting to perform an operation that
 * must be performed when there is a transaction is in progress, but no
 * such transaction is in progress.
 * @author	David Jordan (as Java Editor of the Object Data Management Group)
 * @version ODMG 3.0
 * @see ODMGRuntimeException
 */

public class TransactionNotInProgressException extends ODMGRuntimeException {
    /**
     * Constructs an instance of the exception.
     */
    public TransactionNotInProgressException() {
        super();
    }
    
    /**
     * Constructs an instance of the exception with the provided message.
     * @param msg	A message that describes the exception.
     */
    public TransactionNotInProgressException(String msg) {
        super(msg);
    }


    /**
     * Construct with an error message and a cause.
     * @param msg	The error message associated with this exception.
     * @param cause the original cause.
     */
    public TransactionNotInProgressException(String msg, Throwable cause) 
    {
        super(msg, cause);
    }
}


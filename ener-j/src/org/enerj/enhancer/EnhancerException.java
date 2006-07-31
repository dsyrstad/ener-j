//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/EnhancerException.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.enhancer;

/**
 * An exception thrown from the ASM Visitors when an unrecoverable error occurs. <p>
 * 
 * @version $Id: EnhancerException.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
class EnhancerException extends RuntimeException
{
    
    //----------------------------------------------------------------------
    /**
     * Constructs a new EnhancerException.
     * 
     * @param aMsg a message.
     * @param aCause the offending throwable, may be null.
     */
    EnhancerException(String aMsg, Throwable aCause) 
    {
        super(aMsg, aCause);
    }

}

//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/AlreadyEnhancedException.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.enhancer;

/**
 * Thrown if class has already been enhanced. <p>
 * 
 * @version $Id: AlreadyEnhancedException.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
class AlreadyEnhancedException extends EnhancerException
{
    
    //----------------------------------------------------------------------
    /**
     * Constructs a new AlreadyEnhancedException.
     * 
     * @param aMsg a message.
     * @param aCause the offending throwable, may be null.
     */
    AlreadyEnhancedException(String aMsg, Throwable aCause) 
    {
        super(aMsg, aCause);
    }

}

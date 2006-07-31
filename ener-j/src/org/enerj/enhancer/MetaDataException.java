//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/MetaDataException.java,v 1.2 2006/05/01 22:18:22 dsyrstad Exp $

package org.enerj.enhancer;

/**
 * Thrown if there is an error with the meta data. <p>
 * 
 * @version $Id: MetaDataException.java,v 1.2 2006/05/01 22:18:22 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
class MetaDataException extends Exception
{
    
    //----------------------------------------------------------------------
    /**
     * Constructs a new MetaDataException.
     * 
     * @param aMsg a message.
     * @param aCause the offending throwable, may be null.
     */
    MetaDataException(String aMsg, Throwable aCause) 
    {
        super(aMsg, aCause);
    }

    //----------------------------------------------------------------------
    /**
     * Constructs a new MetaDataException.
     * 
     * @param aMsg a message.
     */
    MetaDataException(String aMsg) 
    {
        super(aMsg);
    }

    //----------------------------------------------------------------------
    /**
     * Constructs a new MetaDataException.
     * 
     * @param aCause the offending throwable, may be null.
     */
    MetaDataException(Throwable aCause) 
    {
        super(aCause);
    }

}

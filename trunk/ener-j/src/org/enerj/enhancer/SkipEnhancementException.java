//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/SkipEnhancementException.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.enhancer;

/**
* Thrown if class should not be enhanced. <p>
* 
* @version $Id: SkipEnhancementException.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
* @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
*/
class SkipEnhancementException extends EnhancerException
{
  
  //----------------------------------------------------------------------
  /**
   * Constructs a new SkipEnhancementException.
   * 
   * @param aMsg a message.
   * @param aCause the offending throwable, may be null.
   */
    SkipEnhancementException() 
  {
      super(null, null);
  }

}

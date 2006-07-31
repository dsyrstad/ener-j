// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/templates/SerializableEnhancerTemplate_Orig.java,v 1.1 2006/06/06 21:29:36 dsyrstad Exp $

package org.enerj.enhancer.templates;

import java.io.Serializable;
import java.util.Date;

/**
 * Class file enhancer template for Ener-J. This is a "top-level" persistable.
 * This class provides a bytecode prototype for developement of the enhancer.
 * This is the class prior to enhancement. Ignore the _Orig extension, it wouldn't normally exist.
 *
 * @version $Id: SerializableEnhancerTemplate_Orig.java,v 1.1 2006/06/06 21:29:36 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class SerializableEnhancerTemplate_Orig implements Serializable
{
    private byte mByte;
    
    //----------------------------------------------------------------------
    public SerializableEnhancerTemplate_Orig()
    {
    }

}

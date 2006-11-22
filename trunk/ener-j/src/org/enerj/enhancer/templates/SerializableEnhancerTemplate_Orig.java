/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
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
    

    public SerializableEnhancerTemplate_Orig()
    {
    }

}

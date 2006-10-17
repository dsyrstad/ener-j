// ============================================================================
// $Id: SampleBinaryFunctor.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
// Copyright (c) 2002  David A. Hall
// ============================================================================
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.Visitable;
import org.enerj.jga.fn.Visitor;

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

public class SampleBinaryFunctor<Arg1Type,Arg2Type,ReturnType>
    extends BinaryFunctor<Arg1Type,Arg2Type,ReturnType>
{
    public Arg1Type _x, _gotX;
    public Arg2Type _y, _gotY;
    public ReturnType _ret;
    public SampleBinaryFunctor(Arg1Type expX, Arg2Type expY, ReturnType ret) {
        _x = expX;
        _y = expY;
        _ret = ret;
    }
    
    public ReturnType fn(Arg1Type arg1, Arg2Type arg2) {
//         System.out.println("fn(\""+arg1+"\", \""+arg2+"\"): returning " +_ret);
        Assert.assertEquals(_x, arg1);
        Assert.assertEquals(_y, arg2);
        _gotX = arg1;
        _gotY = arg2;
        return _ret;
    }

    public String toString() {
        return "SampleBinary("+_x+","+_y+","+_ret+")";
    }
}


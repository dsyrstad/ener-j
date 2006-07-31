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

/**
 * asserts that it received the correct arguments, and returns the given result
 *
 * Created: Sun Apr 14 01:37:38 2002
 *
 * <p>Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

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


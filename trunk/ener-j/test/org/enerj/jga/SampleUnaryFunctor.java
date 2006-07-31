// ============================================================================
// $Id: SampleUnaryFunctor.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.AbstractVisitor;
import org.enerj.jga.fn.Visitable;
import org.enerj.jga.fn.Visitor;

/**
 * asserts that it received the correct argument, and returns the given result
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class SampleUnaryFunctor<ArgType,ReturnType>
    extends UnaryFunctor<ArgType,ReturnType>
{
    public ArgType _exp, _got;
    public ReturnType _ret;
    
    public SampleUnaryFunctor(ArgType expected, ReturnType returned) {
        _exp = expected;
        _ret = returned;
    }
    public ReturnType fn(ArgType parm) {
        Assert.assertEquals(_exp, parm);
        _got = parm;
        return _ret;
    }
}

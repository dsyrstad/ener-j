// ============================================================================
// $Id: Arrays.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
// ============================================================================
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.util;

/**
 * Miscellaneous utilities for working with arrays.
 * <p>
 * Copyright &copy; 2004  David A. Hall
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class Arrays {
    private  Arrays (){}
    
    static public String toString(Object[] arr) {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        for (int i = 0; i < arr.length; ++i) {
            if (i > 0)
                buf.append(',');
            
            buf.append(arr[i]);
        }

        buf.append("]");
        return buf.toString();
    }
}

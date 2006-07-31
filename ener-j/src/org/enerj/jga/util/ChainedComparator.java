// ============================================================================
// $Id: ChainedComparator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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

import java.io.Serializable;
import java.util.Comparator;

// UNTESTED

/**
 * Comparator wrapper that uses a pair of comparators internally.  The primary
 * comparator is evaluated first, and if it returns 0 (equal), then this returns
 * the result of the secondary comparator.
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class ChainedComparator<T> implements Comparator<T>, Serializable
{
    static final long serialVersionUID = -8015644817201268699L;

    private Comparator<T> _primary;
    private Comparator<T> _secondary;
    
    public ChainedComparator(Comparator<T> primary, Comparator<T> secondary) {
        if (primary == null || secondary == null) {
            String msg = "Two Comparators are required: at least one was null";
            throw new IllegalArgumentException(msg);
        }

        _primary = primary;
        _secondary = secondary;
    }

    public int compare(T x, T y) {
        int result = _primary.compare(x,y);
        return (result == 0) ? _secondary.compare(x,y) : result;
    }
}

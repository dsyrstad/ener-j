// ============================================================================
// $Id: ComparableComparator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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
import java.lang.Comparable;
import java.util.Comparator;

/**
 * Comparator used for objects that extend Comparable.  It delegates to the
 * object's Comparable interface of the object whose class is &quot;least&quot;
 * derived; ie, if the first object's class is a subclass of the second object's
 * class, then the second object's comparable interface is used; otherwise the
 * first object's comparable interface is used.
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class ComparableComparator<T extends Comparable/*@*/<? super T>/*@*/>
    implements Comparator<T>, Serializable
{
    static final long serialVersionUID = -3155796069331714963L;

    public int compare(T x, T y) {
            return x.compareTo(y);
   }
}

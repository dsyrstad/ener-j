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
// ============================================================================
// $Id: IteratorComparator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.Comparator;
import java.util.Iterator;
import org.enerj.jga.fn.comparison.Equality;

/**
 * Comparator used to compare iterations lexically.
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class IteratorComparator<T> implements Comparator<Iterator<? extends T>> {
    private Comparator<? super T> _comp;

    /**
     * Builds an IteratorComparator that uses the given Comparator to compare
     * the elements of an iteration.
     */
    public IteratorComparator(Comparator<? super T> comp) {
        _comp = comp;
    }
        
    /**
     * Compares the contents of the two iterations.  An element from each
     * iteration is passed to the given comparator, and if they are not equal,
     * the iteration that produced the lesser of the two elements is determined
     * to be the lesser of the two iterations.  If the elements are equal, then
     * the next pair of elements if passed.  If one of the two iterations is
     * exhausted before a pair of unequal elements is found, then it is
     * considered to be the lesser.  If both iterations are exhausted before a
     * pair of unequal elements is found, then the iterations are determined to
     * be equal.
     * <p>
     * Neither iteration will be advanced past the point where an unequal pair
     * is found, but the elements that were unequal will have been consumed.
     * 
     * @return -1 if x < y, 1 if x > y, 0 if x == y
     */
    public int compare(Iterator<? extends T> x, Iterator<? extends T> y)
    {
        while (true) {
//             if (x.hasNext() && y.hasNext()) {
//                 int i = _comp.compare(x.next(),y.next());
//                 if( i != 0)
//                     return i;
//             }
//             else if (x.hasNext())
//                 return 1;
//             else if (y.hasNext())
//                 return -1;
//             else
//                 return 0;


            if (x.hasNext()) {
                if (y.hasNext()) {
                    int i = _comp.compare(x.next(),y.next());
                    if( i != 0) {
                        return i;
                    }
                }
                else
                    return 1;
            }
            else 
                return y.hasNext() ? -1 : 0;
        }
    }
}

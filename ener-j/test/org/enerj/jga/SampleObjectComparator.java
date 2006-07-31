// ============================================================================
// $Id: SampleObjectComparator.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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

import java.util.Comparator;


/**
 * Compares SampleObjects
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class SampleObjectComparator implements Comparator<SampleObject> {
    public int compare(SampleObject obj1, SampleObject obj2) {
        int i = obj1.getName().compareTo(obj2.getName());
        if (i != 0)
            return i;
        
        i = obj1.getCount().compareTo(obj2.getCount());
        if (i != 0)
            return i;
        
        i = obj1.getPrice().compareTo(obj2.getPrice());
        if (i != 0)
            return i;
        
        return obj1.getDate().compareTo(obj2.getDate());
    }
}



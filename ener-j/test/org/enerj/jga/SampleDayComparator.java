// ============================================================================
// $Id: SampleDayComparator.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Comparator for use in testing various functor primitives.  Compares two Date
 * objects using only the year, month, and day portions (ie, ignores time of day
 * differences).
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class SampleDayComparator<T extends Date> implements Comparator<T> {
    public int compare(T arg1, T arg2) {
        GregorianCalendar c1 = new GregorianCalendar();
        c1.setTime(arg1);
        c1.clear(Calendar.HOUR_OF_DAY);
        c1.clear(Calendar.MINUTE);
        c1.clear(Calendar.SECOND);
        c1.clear(Calendar.MILLISECOND);
        long t1 = c1.getTimeInMillis();
        
        GregorianCalendar c2 = new GregorianCalendar();
        c2.setTime(arg2);
        c2.clear(Calendar.HOUR_OF_DAY);
        c2.clear(Calendar.MINUTE);
        c2.clear(Calendar.SECOND);
        c2.clear(Calendar.MILLISECOND);
        long t2 = c2.getTimeInMillis();
        
        return (t1 < t2) ? -1 : (t1 > t2) ? 1 : 0;
    }
}

// ============================================================================
// $Id: Samples.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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
package org.enerj.jga;

import java.util.Comparator;
import java.util.Vector;
import java.io.Serializable;

/**
 * Static class containing common values and functors used throughout the
 * test framework.
 * <p>
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class Samples {
    static public final String FOO = "_foo_";
    static public final String BAR = "_bar_";
    static public final String BAZ = "_baz_";
    static public final String QLX = "_qlx_";
 
    static public class TestComparator implements Comparator<String>,Serializable
    {
        private Vector<String> _order = new Vector<String>();

        public TestComparator() {
            _order.add(FOO);
            _order.add(BAZ);
            _order.add(QLX);
            _order.add(BAR);
        }
     
        public int compare(String s1, String s2) {
            int n1 = _order.indexOf(s1);
            int n2 = _order.indexOf(s2);
            return (n1 < n2) ? -1 : (n1 == n2) ? 0 : 1;
        }
    }
    
    private Samples() {}
}

// ============================================================================
// $Id: SampleStringComparator.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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

import java.io.Serializable;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for use in testing various functor primitives.  Wraps a Comparator
 * that is obtained from the standard java environment.
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class SampleStringComparator implements Comparator<String>,Serializable {
    private transient RuleBasedCollator collator;

    public int compare(String arg1, String arg2) {
        return getCollator().compare(arg1, arg2);
    }

    private RuleBasedCollator getCollator() {
        if (collator == null)
            collator = (RuleBasedCollator)
                Collator.getInstance(new Locale("en","US",""));
        
        return collator;
                
    }
}

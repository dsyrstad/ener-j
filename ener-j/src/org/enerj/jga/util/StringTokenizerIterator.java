// ============================================================================
// $Id: StringTokenizerIterator.java,v 1.3 2005/08/12 02:56:51 dsyrstad Exp $
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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Iterator;

/**
 * Adapts a StringTokenizer to the Iterator interface.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class StringTokenizerIterator
    implements Iterator<String>, Iterable<String>
{
    private StringTokenizer _base;
    
    public StringTokenizerIterator (StringTokenizer tok) {
        if (tok == null)
            throw new IllegalArgumentException();
        
        _base = tok;
    }
    
    // - - - - - - - - - - - - - -
    // Iterable<String> interface
    // - - - - - - - - - - - - - -

    public Iterator<String> iterator() { return this; }
    
    // - - - - - - - - - - - - - -
    // Iterator<String> interface
    // - - - - - - - - - - - - - -
    
    /**
     * Returns true if the base tokenizer has tokens remaining.
     */

    public boolean hasNext() {
        return _base.hasMoreTokens();
    }
    
    /**
     * Returns the next string in the base tokenizer.
     * @throws NoSuchElementException if there are no more tokens in the base
     * tokenizer's string
     */

    public String next() {
        return _base.nextToken();
    }

    public void remove() { throw new UnsupportedOperationException(); }
}

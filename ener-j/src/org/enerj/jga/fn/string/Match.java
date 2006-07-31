// ============================================================================
// $Id: Match.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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
package org.enerj.jga.fn.string;

import java.util.regex.Pattern;
import org.enerj.jga.fn.UnaryPredicate;

/**
 * Unary Functor that tests a string argument against a given regular
 * expression.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class Match extends UnaryPredicate<String> {
    
    static final long serialVersionUID = 4842042451096017684L;

    // The regular expression to be matched (in String form for convenience
    // at construction and for reporting)
    private String _regex;

    // The regular expression to be matched (in Pattern form for execution)
    private Pattern _pattern;

    /**
     * Builds a Match with an empty string pattern
     */
    public Match () { this(""); }
    
    /**
     * Builds a Match with a given regular expression
     */
    public Match (String regex){
        _regex = (regex != null) ? regex : "";
        _pattern = Pattern.compile(_regex);
    }

    /**
     * Builds a Match with a given Pattern
     */
    public Match (Pattern pattern) {
        _pattern = pattern;
        _regex = pattern.pattern();
    }

    /**
     * Returns the format object used to present values in formatted form.
     * @return the format used to present values
     */
    
    public String getRegex() {
        return _regex;
    }
    
    // UnaryPredicate interface
    
    /**
     * Tests a string against the regular expression given at construction
     * <p>
     * @param arg the value to tested
     * @return true if the string matches the regular expression given at
     * construction
     */

    public Boolean fn(String arg) {
        if (arg == null)
            throw new NullPointerException();

        return _pattern.matcher(arg).matches();
    }
    
    /**
     * Calls the Visitor's <code>visit(Match)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof Match.Visitor)
            ((Match.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "Match("+_regex+")";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>Match</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(Match host);
    }
}

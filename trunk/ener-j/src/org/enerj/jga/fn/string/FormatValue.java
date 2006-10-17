// ============================================================================
// $Id: FormatValue.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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

import java.text.Format;
import org.enerj.jga.fn.UnaryFunctor;

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

public class FormatValue<T> extends UnaryFunctor<T,String> {
    
    static final long serialVersionUID = -6545061527457884949L;

    private Format _format;
    
    public FormatValue (Format format){
        if (format == null) {
            String msg = "Format must be specified";
            throw new IllegalArgumentException(msg);
        }
        
        _format = format;
    }

    /**
     * Returns the format object used to present values in formatted form.
     * @return the format used to present values
     */
    
    public Format getFormat() {
        return _format;
    }
    
    // UnaryFunctor interface
    
    /**
     * Formats the argument using the java.text.Format given at construction.
     * <p>
     * @param arg the value to formatted
     * @return the formatted value
     */

    public String fn(T arg) {
        return _format.format(arg);
    }
    
    /**
     * Calls the Visitor's <code>visit(FormatValue)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof FormatValue.Visitor)
            ((FormatValue.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "FormatValue["+_format+"]";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>FormatValue</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(FormatValue host);
    }
}

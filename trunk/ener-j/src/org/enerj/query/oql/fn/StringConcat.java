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
//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/StringConcat.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import org.enerj.jga.fn.BinaryFunctor;

/**
 * Concatenates two strings.  
 * <p>
 * 
 * @version $Id: StringConcat.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class StringConcat extends BinaryFunctor
{
    private static final long serialVersionUID = 4824869661889510689L;


    /**
     * Construct a StringConcat functor.
     */
    public StringConcat() 
    {
        super();
    }


    public Object fn(Object arg0, Object arg1)
    {
        if (arg0 == null) {
            arg0 = "<null>";
        }
        
        if (arg1 == null) {
            arg1 = "<null>";
        }
        
        return (String)arg0 + (String)arg1;
    }


    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(StringConcat)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof StringConcat.Visitor)
            ((StringConcat.Visitor)v).visit(this);
    }
    

    public String toString() {
        return "StringConcat";
    }
    
    // AcyclicVisitor
    

    /**
     * Interface for classes that may interpret a <b>StringConcat</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(StringConcat host);
    }
}

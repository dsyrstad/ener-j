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
// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/QualifiedNameAST.java,v 1.4 2005/10/23 22:08:52 dsyrstad Exp $

package org.enerj.query.oql.ast;

import java.util.List;

/**
 * The Qualified Name AST. <p>
 * 
 * @version $Id: QualifiedNameAST.java,v 1.4 2005/10/23 22:08:52 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class QualifiedNameAST extends BaseAST
{
    /** List of String - name components. */ 
    private List mComponents;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new QualifiedNameAST. 
     * 
     * @param someNameComponents a List of name component Strings. Must have at least one.
     */
    public QualifiedNameAST(List someNameComponents)
    {
        assert someNameComponents != null && someNameComponents.size() > 0;
        
        mComponents = someNameComponents;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the components of the qualified name.
     * 
     * @return a List of String. There will be at least one element in this List.
     */
    public List<String> getNameComponents()
    {
        return (List<String>)mComponents;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the fully-qualified name as a String.
     *
     * @return the fully-qualified name as a String.
     */
    public String getQualifiedName()
    {
        StringBuilder buf = new StringBuilder(60);
        boolean first = true;
        for (String comp : getNameComponents()) {
            if (!first) {
                buf.append('.');
            }
            
            first = false;
            buf.append(comp);
        }
        
        return buf.toString();
    }
    
}

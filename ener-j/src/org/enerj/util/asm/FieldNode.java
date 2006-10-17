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
//Copyright 2000-2006 Visual Systems Corporation
//$Header: $

package org.enerj.util.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;

/**
 * Represents an ASM field Node. Used by ClassReflector <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@vitamin-o.org">Dan Syrstad </a>
 */
public class FieldNode implements FieldVisitor
{
    private String mName;
    private List<AnnotationNode> mAnnotations = new ArrayList<AnnotationNode>();

    public FieldNode(String aName) 
    { 
        mName = aName;
    }

    public AnnotationVisitor visitAnnotation(String aDesc, boolean anVisible)
    {
        AnnotationNode anno = new AnnotationNode(aDesc);
        mAnnotations.add(anno);
        return anno;
    }

    public void visitAttribute(Attribute attr)
    {
    }

    public void visitEnd()
    {
    }

    public List<AnnotationNode> getAnnotations()
    {
        return mAnnotations;
    }

    public String getName()
    {
        return mName;
    }
}

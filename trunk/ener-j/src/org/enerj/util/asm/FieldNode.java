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
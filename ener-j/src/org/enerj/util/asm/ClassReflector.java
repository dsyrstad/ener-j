//Ener-J
//Copyright 2006 Visual Systems Corporation
//$Header:  $

package org.enerj.util.asm;

import java.util.ArrayList;
import java.util.List;

import org.enerj.util.ClassUtil;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * A simple ASM ClassVisitor that gathers information about a class.
 *  
 * @version $Id: TypeUtil.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ClassReflector implements ClassVisitor
{
    private String mSuperClass = null;
    private String[] mSuperInterfaces = null;
    private List<AnnotationNode> mClassAnnotations = new ArrayList<AnnotationNode>();
    private List<FieldNode> mFields = new ArrayList<FieldNode>();

    public ClassReflector()
    {
    }

    public ClassReflector(byte[] someClassBytecodes)
    {
        ClassReader classReader = new ClassReader(someClassBytecodes);
        classReader.accept(this, false);        
    }
    
    public ClassReflector(String aClassName) throws ClassNotFoundException
    {
        byte[] someClassBytecodes = ClassUtil.getBytecode(aClassName);
        ClassReader classReader = new ClassReader(someClassBytecodes);
        classReader.accept(this, false);        
    }
    
    public void visit(int someVersion, int access, String someName, String someSignature, String someSuperName, String[] someInterfaces)
    {
        if (someSuperName != null) {
            mSuperClass = someSuperName.replace('/', '.');
        }
        
        if (someInterfaces != null) {
            mSuperInterfaces = new String[ someInterfaces.length ];
            for (int i = 0; i < someInterfaces.length; i++) {
                mSuperInterfaces[i] = someInterfaces[i].replace('/', '.');
            }
        }
    }

    public AnnotationVisitor visitAnnotation(String aDesc, boolean isVisible)
    {
        AnnotationNode v = new AnnotationNode(aDesc);
        mClassAnnotations.add(v);
        return v;
    }

    public void visitAttribute(Attribute attr)
    {
    }

    public void visitEnd()
    {
    }

    public FieldVisitor visitField(int access, String aName, String aDesc, String aSignature, Object aValue)
    {
        FieldNode fieldNode = new FieldNode(aName);
        mFields.add(fieldNode);
        return fieldNode;
    }

    public void visitInnerClass(String someName, String someOuterName, String someInnerName, int access)
    {
    }

    public MethodVisitor visitMethod(int access, String someName, String someDesc, String someSignature, String[] someExceptions)
    {
        return null;
    }

    public void visitOuterClass(String someOwner, String someName, String someDesc)
    {
    }

    public void visitSource(String someSource, String someDebug)
    {
    }

    public String getSuperClass()
    {
        return mSuperClass;
    }

    public String[] getSuperInterfaces()
    {
        return mSuperInterfaces;
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Determines if the class implements the specified super-interface.
     *
     * @param superInterfaceName the super-interface name.
     * 
     * @return true if the class implements the specified super-interface, else false.
     */
    public boolean containsSuperInterface(String superInterfaceName)
    {
        for (String intfName : getSuperInterfaces()) {
            if (intfName.equals(superInterfaceName)) {
                return true;
            }
        }
        
        return false;
    }

    public List<AnnotationNode> getClassAnnotations()
    {
        return mClassAnnotations;
    }

    public List<FieldNode> getFields()
    {
        return mFields;
    }
}
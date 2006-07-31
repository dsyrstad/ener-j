/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2005 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.enerj.util.asm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;

/**
 * A node that represents an annotationn.
 * 
 * @author Eric Bruneton
 */
public class AnnotationNode implements AnnotationVisitor {

    /**
     * The class descriptor of the annotation class.
     */
    public String desc;

    /**
     * The name value pairs of this annotation. Each name value pair is stored
     * as two consecutive elements in the list. The name is a {@link String},
     * and the value may be a {@link Byte}, {@link Boolean}, {@link Character},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Float},
     * {@link Double}, {@link String} or {@link org.objectweb.asm.Type}, or an
     * two elements String array (for enumeration values), a
     * {@link AnnotationNode}, or a {@link List} of values of one of the
     * preceding types. The list may be <tt>null</tt> if there is no name
     * value pair.
     */
    public List values;

    /**
     * Constructs a new {@link AnnotationNode}.
     * 
     * @param desc the class descriptor of the annotation class.
     */
    public AnnotationNode(final String desc) {
        this.desc = desc;
    }

    /**
     * Constructs a new {@link AnnotationNode} to visit an array value.
     * 
     * @param values where the visited values must be stored.
     */
    AnnotationNode(final List values) {
        this.values = values;
    }

    // ------------------------------------------------------------------------
    // Implementation of the AnnotationVisitor interface
    // ------------------------------------------------------------------------

    public void visit(final String name, final Object value) {
        if (values == null) {
            values = new ArrayList(this.desc != null ? 2 : 1);
        }
        if (this.desc != null) {
            values.add(name);
        }
        values.add(value);
    }

    public void visitEnum(
        final String name,
        final String desc,
        final String value)
    {
        if (values == null) {
            values = new ArrayList(this.desc != null ? 2 : 1);
        }
        if (this.desc != null) {
            values.add(name);
        }
        values.add(new String[] { desc, value });
    }

    public AnnotationVisitor visitAnnotation(
        final String name,
        final String desc)
    {
        if (values == null) {
            values = new ArrayList(this.desc != null ? 2 : 1);
        }
        if (this.desc != null) {
            values.add(name);
        }
        AnnotationNode annotation = new AnnotationNode(desc);
        values.add(annotation);
        return annotation;
    }

    public AnnotationVisitor visitArray(final String name) {
        if (values == null) {
            values = new ArrayList(this.desc != null ? 2 : 1);
        }
        if (this.desc != null) {
            values.add(name);
        }
        List array = new ArrayList();
        values.add(array);
        return new AnnotationNode(array);
    }

    public void visitEnd() {
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the value associated with the specified annotation parameter.
     *
     * @param aName the parameter name.
     * 
     * @return the value, which may be null or another AnnotationNode.
     */
    public Object getValue(String aName)
    {
        if (values != null) {
            Iterator iter = values.iterator();
            for (int i = 0; iter.hasNext(); i++) {
                // Even entries are names.
                Object value = iter.next();
                if ((i & 1) == 0 && value.equals(aName)) {
                    value = iter.next();
                    return value; 
                }
            }
        }
        
        return null;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the array associated with the specified annotation parameter.
     *
     * @param aName the parameter name.
     * @param anElementType the element type of the array.
     * 
     * @return the array, which may be null if aName does not exist. The
     *  array may be cast to an array of anElementType by the caller.
     */
    public Object getArray(String aName, Class anElementType)
    {
        Object value = getValue(aName);
        if (value == null) {
            return null;
        }
        
        // Singleton value?
        List values;
        if (!(value instanceof List)) {
            values = Collections.singletonList(value);
        }
        else {
            values = (List)value;
        }
        
        Object array = Array.newInstance(anElementType, values.size() );
        int i = 0;
        for (Object obj : values) {
            Array.set(array, i++, value);
        }
        
        return array;
    }
}

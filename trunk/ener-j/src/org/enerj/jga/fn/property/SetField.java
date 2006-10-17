// ============================================================================
// $Id: SetField.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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

package org.enerj.jga.fn.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.EvaluationException;

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

// NOTE: compiling this class yields one unchecked cast warning.  It is really
// up to the user to declare this class properly (the return type must be
// correctly specified)

public class SetField<T,R> extends BinaryFunctor<T,R,R> {

    static final long serialVersionUID = -5051261348109487737L;
    
    // The field class, used to find the correct Field using reflection
    private Class<R> _fieldType;

    // The name of the field
    private String _fieldName;

    // The field to set
    private transient Field _field;

    /**
     * Builds a SetField that will update the value of the named field
     * of an instance of type argType.
     * @throws IllegalArgumentException if either argument is omitted, or if
     * there is no such setter field in type argType.
     */
    public SetField(Class<T> argType, Field field, Class<R> fieldType) {
        if (field == null) {
            throw new IllegalArgumentException("Must supply field");
        }
        if (field.getDeclaringClass().isAssignableFrom(argType)) {
            throw new IllegalArgumentException(buildNoSuchFieldMessage(field.getName(), argType));
        }
        if (!fieldType.isAssignableFrom(field.getType())) {
            String msg = "Field {0} is of the wrong type: is {1}, expected {2}";
            Object[] args = new Object[] { field.getName(), field.getType().getName(),
                                               fieldType.getName() };
            throw new IllegalArgumentException(MessageFormat.format(msg, args));                
        }            
        
        _fieldName = field.getName();
        _fieldType = fieldType;
        _field = field;
    }
    

    /**
     * Builds a SetField that will update the value of the named field
     * of an instance of type argType.  The field will be of type propType.
     * @throws IllegalArgumentException if either argument is omitted, or if
     * there is no such setter field in type argType.
     */
    public SetField(Class<T> argType, String fieldName, Class<R> fieldType) {
        if (fieldName == null || fieldName.length() == 0) {
            throw new IllegalArgumentException("Must supply field name");
        }
        if (fieldType == null) {
            throw new IllegalArgumentException("Must supply field type");
        }
        
        _fieldName = fieldName;
        _fieldType = fieldType;
        
        try {
            _field = argType.getField(_fieldName);
        }
        catch (NoSuchFieldException x) {
            IllegalArgumentException iax =
                new IllegalArgumentException(buildNoSuchFieldMessage(fieldName, argType));
            iax.initCause(x);
            throw iax;
        }
    }
    

    /**
     * Returns the name of the field that this functor sets.
     */
    public String getFieldName() {
        return _fieldName;
    }

    // Binary interface
    
    /**
     * Sets the designated field of the argument to the given value and returns
     * null.
     * <p>
     * @return null
     * @throws EvaluationException if the argument does not have the designated
     *         public field, or if it is accept the given value. 
     */
    public R fn(T arg, R val) {
        try {
            // @SuppressWarnings
            // There's nothing we can do about this other than warn the users
            // to make sure that they don't use an inappropriate return type
            getField(arg).set(arg, val);
            return null;
        }
        catch (ClassCastException x) { 
            String msg = "{0}.{1} is of type {2}";
            Field f = getField(arg);
            Object[] args = new Object[]{ arg.getClass().getName(), f.getName(),
                                          f.getType().getName() };
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (IllegalAccessException x) {
            String msg = "{0}.{1} is not accessible";
            Object[] args = new Object[]{ arg.getClass().getName(), getField(arg).getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
    }

    private Field getField(T arg) {
        if (_field == null) {
            try {
                _field = arg.getClass().getField(_fieldName);
            }
            catch (NoSuchFieldException x) {
                throw new EvaluationException(x);}
        }

        return _field;
    }

    private String buildNoSuchFieldMessage(String fieldname, Class clasz) {
        String msg = "class {0} does not have field {1}";
        Object[] args = new Object[]{ clasz.getName(), fieldname };
        return MessageFormat.format(msg,args);
    }

    /**
     * Calls the Visitor's <code>visit(SetField)</code> field, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof SetField.Visitor)
            ((SetField.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "SetField("+_fieldName+")";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>SetField</b>
     * function.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(SetField host);
    }
}

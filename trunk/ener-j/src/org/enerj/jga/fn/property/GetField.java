// ============================================================================
// $Id: GetField.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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
import org.enerj.jga.fn.EvaluationException;
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

public class GetField<T,R> extends UnaryFunctor<T,R> {
    
    static final long serialVersionUID = 5733484457689881481L;

    // The name of the field to be retrieved.
    private String _fieldName;

    // The class from which the field is retrieved.
    private Class<T> _argtype;
    
    // The Field whose valus is returned
    private transient Field _field;

    /**
     * Builds a GetField for the given field
     * @throws IllegalArgumentException if the field is null, or is not a field
     * of the given class
     */
    public GetField(Class<T> argclass, Field field) {
        if (field == null) {
            throw new IllegalArgumentException("Must supply field");
        }

        if (!field.getDeclaringClass().isAssignableFrom(argclass)) {
            throw new IllegalArgumentException(buildNoSuchFieldMessage(field.getName(), argclass));
        }
        
        _field = field;
        _fieldName = field.getName();
        _argtype = argclass;
    }

    /**
     * Builds a GetField for the given field, using the given class to
     * find the desired field.
     * @throws IllegalArgumentException if the field name is null or empty,
     *    
     */
    public GetField(Class<T> argclass, String fieldName) {
        if (fieldName == null || fieldName.length() == 0) {
            throw new IllegalArgumentException("Must supply field name");
        }

        _fieldName = fieldName;
        _argtype = argclass;
        
        try {
            _field = argclass.getField(_fieldName);
        }
        catch (NoSuchFieldException x) {
            IllegalArgumentException iax =
                new IllegalArgumentException(buildNoSuchFieldMessage(fieldName, argclass));
            
            iax.initCause(x);
            throw iax;
        }
    }

    /**
     * Returns the name of the field that this functor retrieves.
     */
    public String getFieldName() {
        return _fieldName;
    }

    /**
     * Returns the type of field that this functor retrieves.  
     */

    public Class<R> getFieldType() {
        return (Class<R>) _field.getType();
    }

    // Unary interface

    /**
     * Returns the value of the designated field of the argument
     * @return the value of the designated field of the argument
     * @throws EvaluationException if the argument does not have the designated
     *         public field, or if it is not of the correct type. 
     */
    public R fn(T arg) {
        try {
            // @SuppressWarnings
            // There's nothing we can do about this other than warn the users
            // to make sure that they don't use an inappropriate return type
            R val = (R) getField().get(arg);
            return val; 
        }
        catch (ClassCastException x) { 
            String msg = "{0}.{1} is of type {2}";
            Field f = getField();
            Object[] args = new Object[]{ arg.getClass().getName(), f.getName(),
                                          f.getType().getName() };
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (IllegalAccessException x) {
            String msg = "{0}.{1} is not accessible";
            Object[] args = new Object[]{ _argtype.getName(), getField().getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
    }

    /**
     * Lazily loads the reflective Field object.  Instances of Reflection classes are
     * not serializable, so this must be loaded lazily in case this object was read
     * from a stream (the first time the functor is invoked, the field object will
     * be instantiated)
     */
    private Field getField() {
        if (_field == null) {
            try {
                _field = _argtype.getField(_fieldName);
            }
            catch (NoSuchFieldException x) {
                throw new EvaluationException(buildNoSuchFieldMessage(_fieldName, _argtype));
            }
        }

        return _field;
    }

    private String buildNoSuchFieldMessage(String fieldname, Class clasz) {
        String msg = "class {0} does not have field {1}";
        Object[] args = new Object[]{ clasz.getName(), fieldname };
        return MessageFormat.format(msg,args);
    }

    /**
     * Calls the Visitor's <code>visit(GetField)</code> field, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof GetField.Visitor)
            ((GetField.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "GetField["+_fieldName+"]";
    }
    
   // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>GetField</b>
     * function.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(GetField host);
    }
}

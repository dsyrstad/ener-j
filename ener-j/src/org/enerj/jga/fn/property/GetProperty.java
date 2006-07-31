// ============================================================================
// $Id: GetProperty.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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
import java.lang.reflect.Method;
import java.text.MessageFormat;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Unary Functor that returns the value of the named property for the argument.
 * The property name and type are set at construction.
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class GetProperty<T,R> extends UnaryFunctor<T,R> {
    
    static final long serialVersionUID = -6249123644692001840L;

    // The name of the property to be returned
    private String _propName;

    // The name of the method corresponding to the property
    private String _methName;

    // The class from which the property is retrieved.
    private Class<T> _argtype;

    // The method called to get the property
    private transient Method _meth;

    /**
     * Builds a GetProperty for the given property, using the given class to
     * find the desired method.
     * @throws IllegalArgumentException if the property name is null or empty,
     *     or if there is no getter method for the given property name.
     */
    public GetProperty(Class<T> argclass, String propName) {
        if (propName == null || propName.length() == 0) {
            throw new IllegalArgumentException("Must supply property name");
        }

        _argtype = argclass;
        
        if (propName.startsWith("get")) {
            _methName = propName;
            _propName = propName.substring(3);
        }
        else if (argclass.equals(Boolean.class) && propName.startsWith("is")) {
            _methName = propName;
            _propName = propName.substring(2);
        }
        else {
            _propName = propName;
            _methName = "get" + propName;
        }
        
        try {
            _meth = argclass.getMethod(_methName, new Class[0]);
        }
        catch (NoSuchMethodException x) {
            String msg = "class {0} does not have property \"{1}\"";
            Object[] args = new Object[]{ argclass.getName(), propName };
            IllegalArgumentException iax =
                new IllegalArgumentException(MessageFormat.format(msg,args));
            iax.initCause(x);
            throw iax;
        }
    }

    /**
     * Returns the name of the property that this functor retrieves.
     */
    public String getPropertyName() {
        return _propName;
    }

    // Unary interface

    /**
     * Returns the value of the designated property of the argument
     * @return the value of the designated property of the argument
     * @throws EvaluationException if the argument does not have the designated
     *         public property, or if it is not of the correct type. 
     */
    public R fn(T arg) {
        try {
            // @SuppressWarnings
            // There's nothing we can do about this other than warn the users
            // to make sure that they don't use an inappropriate return type
            R val = (R) getMethod().invoke(arg, new Object[0]);
            return val; 
        }
        catch (ClassCastException x) { 
            String msg = "{0}.{1} returns type {2}";
            Method m = getMethod();
            Object[] args = new Object[]{ _argtype.getName(), m.getName(),
                                          m.getReturnType().getName() };
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (IllegalAccessException x) {
            String msg = "{0}.{1} is not accessible";
            Object[] args = new Object[]{ _argtype.getName(), getMethod().getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (InvocationTargetException x) {
            String msg = "{0}.{1} failed : "+x.getMessage();
            Object[] args = new Object[]{ _argtype.getName(), getMethod().getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
    }

    private Method getMethod() {
        if (_meth == null) {
            try {
                _meth = _argtype.getMethod(_methName, new Class[0]);
            }
            catch (NoSuchMethodException x) {
                String msg = "class {0} does not have method {1}";
                Object[] args = new Object[]{ _argtype.getName(), _methName};
                throw new EvaluationException(MessageFormat.format(msg,args), x);
            }
        }

        return _meth;
    }

    /**
     * Calls the Visitor's <code>visit(GetProperty)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof GetProperty.Visitor)
            ((GetProperty.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "GetProperty["+_propName+"]";
    }
    
   // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>GetProperty</b>
     * function.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(GetProperty host);
    }
}

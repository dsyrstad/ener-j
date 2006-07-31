// ============================================================================
// $Id: SetProperty.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
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
import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.EvaluationException;

/**
 * Binary Functor that sets the named property of the first argument to the
 * value.  The property name and type are set at construction.  The return
 * value will be that which the argument's property setter method returns
 * (generally either null or the old value).
 * <p>
 * Note that declaring the return type incorrectly can result in
 * ClassCastExceptions being thrown when the functor is invoked: the compiler
 * cannot check the return type of a reflectively loaded method.
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

// NOTE: compiling this class yields one unchecked cast warning.  It is really
// up to the user to declare this class properly (the return type must be
// correctly specified)

public class SetProperty<T,R> extends BinaryFunctor<T,R,R> {

    static final long serialVersionUID = 5305970242256716550L;
    
    // The property class, used to find the correct Method using reflection
    private Class<R> _propClass;

    // The name of the property (without the leading 'set').  
    private String _propName;

    // The name of the setter method (same as _propName, but with 'set' prefix)
    private String _methName;

    // The method to invoke
    private transient Method _meth;

    /**
     * Builds a SetProperty that will return the value of the named property
     * of an instance of type argType.  The property will be of type propType.
     * @throws IllegalArgumentException if either argument is omitted, or if
     * there is no such setter method in type argType.
     */
    public SetProperty(Class<T> argType, String propName, Class<R> propType) {
        if (propName == null || propName.length() == 0) {
            throw new IllegalArgumentException("Must supply property name");
        }
        if (propType == null) {
            throw new IllegalArgumentException("Must supply property type");
        }
        
        if (propName.startsWith("set")) {
            _methName = propName;
            _propName = propName.substring(3);
        }
        else {
            _propName = propName;
            // Convert the first character to upper case.
            _methName = "set" + propName.substring(0, 1).toUpperCase() + (propName.length() > 1 ? propName.substring(1) : "");
        }
        
        _propClass = propType;
        
        try {
            Class[] car = new Class[]{propType}; 
            _meth = argType.getMethod(_methName, car);
        }
        catch (NoSuchMethodException x) {
            String msg = "class {0} does not have property \"{1}\" with a parameter of type {2}";
            Object[] args = new Object[]{ argType.getName(), propName, propType.getName() };
            IllegalArgumentException iax =
                new IllegalArgumentException(MessageFormat.format(msg,args));
            iax.initCause(x);
            throw iax;
        }
    }
    

    /**
     * Returns the name of the property that this functor sets.
     */
    public String getPropertyName() {
        return _propName;
    }

    // Binary interface
    
    /**
     * Sets the designated property of the argument to the given value.  When
     * the property's setter method returns a value, then this functor will
     * return it (otherwise it will return null).
     * <p>
     * @return the value returned by the designated property's setter method:
     *         generally it is null, but in some cases it might be the old value
     * @throws EvaluationException if the argument does not have the designated
     *         public property, or if it is accept the given value. 
     */
    public R fn(T arg, R val) {
        try {
            // @SuppressWarnings
            // There's nothing we can do about this other than warn the users
            // to make sure that they don't use an inappropriate return type
            R ret = (R) getMethod(arg).invoke(arg, new Object[] {val});
            return ret;
        }
        catch (ClassCastException x) { 
            String msg = "{0}.{1} returns type {2}";
            Method m = getMethod(arg);
            Object[] args = new Object[]{ arg.getClass().getName(), m.getName(),
                                          m.getReturnType().getName() };
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (IllegalAccessException x) {
            String msg = "{0}.{1} is not accessible";
            Object[] args = new Object[]{ arg.getClass().getName(), getMethod(arg).getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (InvocationTargetException x) {
            String msg = "{0}.{1}({2}) failed : "+x.getMessage();
            Object[] args = new Object[]{ arg.getClass().getName(), getMethod(arg).getName(), val};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
    }

    private Method getMethod(T arg) {
        if (_meth == null) {
            try {
                Class[] car = new Class[]{_propClass}; //new Class[]{R}
                _meth = arg.getClass().getMethod(_methName, car);
            }
            catch (NoSuchMethodException x) {
                throw new EvaluationException(x);}
        }

        return _meth;
    }

    /**
     * Calls the Visitor's <code>visit(SetProperty)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof SetProperty.Visitor)
            ((SetProperty.Visitor)v).visit(this);
    }
    
    // Object overrides

    public String toString() {
        return "SetProperty("+_methName+")";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>SetProperty</b>
     * function.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(SetProperty host);
    }
}

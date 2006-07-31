// ============================================================================
// $Id: ConstructDefault.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.Generator;

/**
 * Generator that constructs an object of the given class using its default
 * constructor.
 * <p>
 * Copyright &copy; 2004  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class ConstructDefault<R> extends Generator<R> {

   static final long serialVersionUID = 9150185598879755334L;

    private Class<R> _objclass;
    
    private transient Constructor<R> _ctor;

    /**
     * Builds a generator that will return instances of the class
     * <code>ctorclass</code>, built using the default constructor.   
     * @throws IllegalArgumentException if the constructor cannot be found
     * @throws IllegalArgumentException if the class argument is omitted or does not
     * define an accessible default constructor.
     */
    
    public ConstructDefault(Class<R> ctorclass) {
        if (ctorclass == null) {
            String msg = "Class to be constructed must be specified";
            throw new IllegalArgumentException(msg);
        }
        
        Class[] argclasses = new Class[0];

        _objclass = ctorclass;
        
        try {
            _ctor = ctorclass.getConstructor(argclasses);
        }
        catch (NoSuchMethodException x) {
            
            IllegalArgumentException x1 = new IllegalArgumentException();
            x1.initCause(x);
            throw x1;
        }
    }

    /**
     * Builds an object via the default constructor
     * <p>
     * @return the object built by the constructor
     */
    public R gen() {
        try {
            R val = (R) getConstructor().newInstance();
            return val;
        }
        catch (NoSuchMethodException x) { 
            String msg = "No default constructor for class {0}";
            Object[] args = new Object[]{_objclass.getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (InstantiationException x) { 
            String msg = "class {0} is abstract: cannot be constructed";
            Object[] args = new Object[]{_objclass.getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (IllegalAccessException x) { 
            String msg = "default ctor in class {0} is not accessible";
            Object[] args = new Object[]{_objclass.getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (InvocationTargetException x) { 
            String msg = "default ctor in class {0} failed: "+x.getMessage();
            Object[] args = new Object[]{_objclass.getName()};
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
    }
    
    /**
     * Lazily loads the constructor (used if the functor is called after
     * deserialization)
     */
    private Constructor<R> getConstructor() throws NoSuchMethodException {
        if (_ctor == null) 
            _ctor = _objclass.getConstructor(new Class[0]);

        return _ctor;
    }

    /**
     * Calls the Visitor's <code>visit(ConstructDefault)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConstructDefault.Visitor)
            ((ConstructDefault.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "new "+_objclass.getName()+"()";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>ConstructDefault</b>
     * functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConstructDefault host);
    }
}

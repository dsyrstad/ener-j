// ============================================================================
// $Id: Property.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
// Copyright (c) 2004  David A. Hall
// ============================================================================
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================
package org.enerj.jga.fn.property;

import org.enerj.jga.fn.BinaryFunctor;

/**
 * Facade containing static factory methods for the functors in this package.
 * <p>
 * Copyright &copy; 2004  David A. Hall
 */

public class Property {
    // T can't be inferred
    static public <T> ArrayUnary<T> arrayUnary() {
        return new ArrayUnary<T>();
    }
    
    // T1,T2 can't be inferred
    static public <T1,T2> ArrayBinary<T1,T2> arrayBinary() {
        return new ArrayBinary<T1,T2>();
    }
    
    static public <R> Cast<R> cast(Class<R> type) {
        return new Cast<R>(type);
    }
    
    static public <T,V> CompareProperty<T,V> compareProperty(Class<T> type, String name, V value) {
        return new CompareProperty(type, name, value);
    }
    
    static public <T,V> CompareProperty<T,V>
    compareProperty(Class<T> type, String name, BinaryFunctor<V,V,Boolean> bf, V value) {
        return new CompareProperty(type, name, bf, value);
    }

    static public <R> Construct<R> construct(Class[] args, Class<R> type) {
        return new Construct<R>(args, type);
    }

    static public <R> ConstructDefault<R> construct(Class<R> type) {
        return new ConstructDefault<R>(type);
    }

    static public <T,R> ConstructUnary<T,R> construct(Class<T> arg, Class<R> type) {
        return new ConstructUnary<T,R>(arg, type);
    }

    // R can't be inferred
    static public <T,R> GetProperty<T,R> getProperty(Class<T> type, String name) {
        return new GetProperty<T,R>(type, name);
    }

    static public <T,R> SetProperty<T,R> getProperty(Class<T> type, String name, Class<R> prop) {
        return new SetProperty<T,R>(type, name, prop);
    }

    static public <T> InstanceOf<T> instanceOf(Class<T> type) {
        return new InstanceOf<T>(type);
    }

    // R can't be inferred
    static public <T,R> InvokeNoArgMethod<T,R> invoke(Class<T> type, String name) {
        return new InvokeNoArgMethod<T,R>(type, name);
    }

    // R can't be inferred
    static public <T,R> InvokeMethod<T,R> invoke(Class<T> type, String name, Class arg) {
        return new InvokeMethod<T,R>(type, name, arg);
    }

    // R can't be inferred
    static public <T,R> InvokeMethod<T,R> invoke(Class<T> type, String name, Class[] args) {
        return new InvokeMethod<T,R>(type, name, args);
    }
}

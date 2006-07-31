// ============================================================================
// $Id: CompareProperty.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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

import org.enerj.jga.fn.BinaryFunctor;
import org.enerj.jga.fn.UnaryPredicate;
import org.enerj.jga.fn.comparison.EqualTo;

/**
 * Unary Predicate that compares the value of the named property to the
 * given value.  The type of comparison is a binary predicate: the two
 * arguments passed to the predicate are the value of the argument's property
 * in the first position and the constant value passed at construction in the
 * second position.
 * <p>
 * The test returns
 * <code>bp(getProperty(name).fn(arg), value)</code>.  This is also
 * equivalent to the following:<br>
 * <pre>
 * UnaryPredicate CompareProperty =
 *     new UnaryCompose(new Binder2nd(bp, value),
 *                      new GetProperty(propName))
 * </pre>
 * with one less call to <code>fn</code> at evaluation and somewhat clearer
 * construction syntax.
 * <p>
 * To Serialize a CompareProperty, the generic parameter V must be serializable.
 * <p>
 * Copyright &copy; 2003  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class CompareProperty<T,V> extends UnaryPredicate<T> {
    
    static final long serialVersionUID = 8734296101969960336L;

    private BinaryFunctor<V,V,Boolean> _comp;
    private GetProperty<T,V> _gpf;
    private String _prop;
    private V _value;

    /**
     * Builds the CompareProperty predicate that will compare the named
     * property of an instance of type argType to the given value using
     * an EqualTo predicate.
     */
    public CompareProperty(Class<T> argType, String propName, V val) {
        this(argType, propName, new EqualTo<V>(), val);
    }
    
    /**
     * Builds the CompareProperty predicate that will compare the named
     * property of an instance of type argType to the given value.  The
     * comparison can be any type of BinaryFunctor where both arguments
     * are of the same type.
     */
    public CompareProperty(Class<T> argType, String propName,
                           BinaryFunctor<V,V,Boolean> pred,
                           V val)
    {
        _prop = propName;
        _comp = pred;
        _value = val;
        _gpf = new GetProperty<T, V>(argType, propName);
    }
    
    /**
     * Returns the name of the property to be compared
     * @return the name of the property to be compared
     */
    public String getPropName() { return _prop; }
    
    /**
     * Returns the constant value to which properties are compared
     * @return the constant value to which properties are compared
     */
    public V getValue() { return _value; }
    
    /**
     * Returns the predicate used to compare property values
     * @return the predicate used to compare property values.
     */
    public BinaryFunctor<V,V,Boolean> getPredicate() {
        return _comp;
    }
    
    // UnaryPredicate interface
    
    /**
     * Tests the designated property of the argument against the value given at
     * construction.
     * <p>
     * @return the boolean value of the comparison
     */
    public Boolean fn(T arg) {
        return _comp.fn(_gpf.fn(arg), _value);
    }

    /**
     * Calls the Visitor's <code>visit(CompareProperty)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof CompareProperty.Visitor)
            ((CompareProperty.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "CompareProperty["+_prop+" "+_comp+" "+_value+"]";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>CompareProperty</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(CompareProperty host);
    }
}
    



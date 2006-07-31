// ============================================================================
// $Id: BitwiseNot.java,v 1.3 2005/08/12 02:56:44 dsyrstad Exp $
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

package org.enerj.jga.fn.arithmetic;

import java.text.MessageFormat;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.EvaluationException;

/**
 * Unary Functor that returns the bitwise not of its argument
 * <p>
 * Copyright &copy; 2002 David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class BitwiseNot<T extends Number> extends UnaryFunctor<T,T> {

    static final long serialVersionUID = 9034926672034158292L;
    
    // Utility class that performs the addition as appropriate for the
    // class given at construction
    
    private IntegerArithmetic<T> _math;
     
    /**
     * Builds BitwiseNot functor for the given class.  The class argument must
     * be the same as the generic class argument (when generics are in use)
     * or else a ClassCastException will be thrown when the functor is used.
     *
     * @throws IllegalArgumentException if the given class has no Arithmetic
     *      implementation registered with the ArithmeticFactory
     */
    public BitwiseNot(Class<T> c) {
         _math = ArithmeticFactory.getIntegralArithmetic(c);
         if (_math == null) {
             String msg = "No implementation of IntegerArithmetic registered for {0}";
             throw new IllegalArgumentException(MessageFormat.format(msg, new Object[]{c}));
         }
    }
    
    // UnaryFunctor interface
    
    /**
     * Given argument <b>x</b>, return ~x
     * @return ~x
     */
    public T fn(T x) {
        try {
            return _math.not(x);
        }
        catch (ClassCastException ex) {
            String msg = "ClassCastException: Cannot compute ~{0}[{1}]";
            String err = MessageFormat.format(msg, new Object[]{ x.getClass(), x});
            throw new EvaluationException(err, ex);
        }
    }

    /**
     * Calls the Visitor's <code>visit(BitwiseNot)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof BitwiseNot.Visitor)
            ((BitwiseNot.Visitor)v).visit(this);
    }
    

    // Object overrides

    public String toString() {
        return "BitwiseNot";
    }
    
    // AcyclicVisitor
    
    /**
     * Interface for classes that may interpret a <b>BitwiseNot</b>
     * functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(BitwiseNot host);
    }

}

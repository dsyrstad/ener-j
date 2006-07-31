// ============================================================================
// $Id: ParseFormat.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
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

package org.enerj.jga.fn.string;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import org.enerj.jga.fn.EvaluationException;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.adaptor.Identity;
import org.enerj.jga.fn.arithmetic.ValueOf;
import org.enerj.jga.fn.property.Cast;
import org.enerj.jga.fn.property.ConstructUnary;
import org.enerj.jga.fn.property.GetProperty;

/**
 * Unary Functor that parses a given Format.
 * <p>
 * <p>
 * Copyright &copy; 2003  David A. Hall
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public class ParseFormat<R> extends UnaryFunctor<String,R> {

    static final long serialVersionUID = 6590747028326789147L;

    // The java.text.Format object that controls parsing.
    private Format _format;

    // A functor that converts the object from the type returned by the format's
    // parse() method to the parameter type
    private UnaryFunctor<Object, R> _converter;

    /**
     * Builds the ParseFormat given a text Format and a functor that can convert
     * Objects returned by the Format's parse(Object) method and the desired
     * type of this functor.
     */
    protected ParseFormat(Format format, UnaryFunctor<Object,R> conv) {
        if (format == null) {
            String msg = "Format must be specified";
            throw new IllegalArgumentException(msg);
        }
        
        _format = format;
        _converter = conv;
    }

    /**
     * @return the format used to parse values
     */

    public Format getFormat() {
        return _format;
    }
    
    // UnaryFunctor interface
    
    /**
     * Parses the value from the given string, using the java.text.Format object
     * passed at construction.
     * <p>
     * @param arg formatted string to be parsed
     * @throws java.text.ParseException when the string cannot be parsed to the
     * correct type
     * @return the value that the string represented
     */

    public R fn(String arg) {
        
        try {
            Object obj =  _format.parseObject(arg);
            // @SuppressWarnings
            // This line causes an unchecked cast warning: we can't do anything
            // about this other than warn the user not to use improper formats.
            // It would help if java.text.Format was generic, but it isn't.  If
            // the user gives us a converter, we're OK, otherwise all we can do
            // is cast and hope for the best
            R val = (_converter == null) ? (R) obj : _converter.fn(obj);
            return val;
        }
        catch (ParseException x) {
            String msg = "Unable to parse \"{0}\"";
            Object[] args = new Object[]{ arg };
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
        catch (ClassCastException x) {
            String msg = "Unable to convert \"{0}\" to correct type";
            Object[] args = new Object[]{ arg };
            throw new EvaluationException(MessageFormat.format(msg,args), x);
        }
    }
    
    /**
     * Calls the Visitor's <code>visit(ParseFormat)</code> method, if it
     * implements the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ParseFormat.Visitor)
            ((ParseFormat.Visitor)v).visit(this);
    }

    // Object overrides

    public String toString() {
        return "ParseFormat["+_format+"]";
    }
    
    // Acyclic Visitor
    
    /**
     * Interface for classes that may interpret a <b>ParseFormat</b>
     * predicate.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ParseFormat host);
    }

    /**
     * ParseFormat functor for use with Dates.  This class exists
     * as an implementation detail that works around a limit in the javac
     * inferencer -- in all substantive ways, this is simply a ParseFormat functor.
     */

    static public class Date<T extends java.util.Date> extends ParseFormat<T> {
        static final long serialVersionUID = 6959869794482290044L;
        public Date(Class<T> type, DateFormat format) {
            super(format, (UnaryFunctor<Object,T>)
                  // If we want to return java.util.Date, then we can simply cast it.
                  // Otherwise, we return [ new D(((Date)arg).getTime()) ]
                  //    since we know that All Date subclasses have a constructor that
                  //    takes a long (and this will break if a Date subclass is used that
                  //    does not have such a constructor.
                  // The other possibility is to build a date using a default constructor
                  // and set its time property [ new D().setTime(((Date)arg).getTime()) ]
                  // but that functor is even scarier

                  // @SupressWarnings
                  //
                  ((type.equals(java.util.Date.class))
                     ? new Cast<java.util.Date>(java.util.Date.class)
                     : new ConstructUnary<Long,T>(Long.TYPE, type)
                         .compose(new GetProperty<java.util.Date,Long>(java.util.Date.class,"Time"))
                           .compose(new Cast<java.util.Date>(java.util.Date.class))));
        }
    }

    /**
     * ParseFormat functor for use with Numbers.  This class exists
     * as an implementation detail that works around a limit in the javac
     * inferencer -- in all substantive ways, this is simply a ParseFormat functor.
     */

    static public class Number<T extends java.lang.Number> extends ParseFormat<T> {
        static final long serialVersionUID = -5365953713327551298L;
        public Number(Class<T> type, NumberFormat format) {
            super(format, //(UnaryFunctor<Object,R>)
                  new ValueOf<java.lang.Number,T>(type)
                     .compose(new Cast<java.lang.Number>(java.lang.Number.class)));
        }
    }
}

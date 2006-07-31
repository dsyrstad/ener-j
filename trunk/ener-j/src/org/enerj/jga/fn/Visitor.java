// ============================================================================
// $Id: Visitor.java,v 1.3 2005/08/12 02:56:47 dsyrstad Exp $
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

package org.enerj.jga.fn;

/**
 * Defines an interface for classes that may interpret functors or predicates.
 * <p>
 * jga uses the <a href="http://www.objectmentor.com/resources/articles/acv.pdf">AcyclicVisitor</a>
 * pattern to provide a structure for implementing Visitor.  Within jga, each
 * class that implements Visitable does so with the following boilerplate:<br>
 * <pre>
 * public class Foo implements Visitable
 *    public void accept(org.enerj.jga.fn.Visitor v) {
 *        if (v instanceof Foo.Visitor)
 *            ((Foo.Visitor)v).visit(this);
 *    }
 *
 *    public interface Visitor extends org.enerj.jga.fn.Visitor {
 *        public void visit(Foo host); 
 *    }
 * </pre>
 * Implementations of Visitor will declare suport for a given class by
 * implementing that class' Visitor interface.<br>
 * <pre>
 * public class FooBarCounter implements Foo.Visitor, Bar.Visitor {
 *     private int count = 0;
 *     public int getCount() { return count; }
 *     public void visit(Visitable host) {}
 *     public void visit(Foo host) { ++count; }
 *     public void visit(Bar host) { ++count; }
 * }
 * </pre>
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public interface Visitor {
    /**
     * Call-back method, called by the Visitable object in response to a
     * call to its <code>accept(Visitor)</code> method.
     */
    public void visit(Visitable visitable);
}

    

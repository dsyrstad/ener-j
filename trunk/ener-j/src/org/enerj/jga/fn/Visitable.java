// ============================================================================
// $Id: Visitable.java,v 1.3 2005/08/12 02:56:47 dsyrstad Exp $
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
 * Defines an interface for classes that may be interpreted by a Visitor.
 * <p>
 * jga uses the <a href="http://www.objectmentor.com/resources/articles/acv.pdf">AcyclicVisitor</a>
 * pattern to provide a structure for implementing Visitor.  Within jga, each
 * class that implements Visitable provides a nested interface called Visitor
 * that defines a <code>visit</code> method for the class.  This is typically
 * implemented with the following boilerplate:<br>
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
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 **/

public interface Visitable {
    /**
     * Determine if the visitor is appropriate (typically by testing against
     * a specific interface) and if so, call pass the implementing object to
     * the visitor's visit method.
     */
    public void accept(Visitor visitor);
}

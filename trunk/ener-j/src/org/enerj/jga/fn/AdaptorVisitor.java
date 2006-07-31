// ============================================================================
// $Id: AdaptorVisitor.java,v 1.3 2005/08/12 02:56:47 dsyrstad Exp $
// Copyright (c) 2005  David A. Hall
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

package org.enerj.jga.fn;

import org.enerj.jga.fn.adaptor.*;
import org.enerj.jga.fn.logical.All;
import org.enerj.jga.fn.logical.Any;
import java.util.Iterator;


/**
 * Visitor that performs a walk of compound functor structures.  This visitor
 * implements the Visitor interface associated with all of the compounding
 * functors in the <a href="adaptor/package-summary.html">org.enerj.jga.fn.adaptor</a>
 * package, as well as the two that are in the
 * <a href="logical/package-summary.html">org.enerj.jga.fn.logical</a> package.
 * <p>
 * Basing visitors on this base class will allow most implementations to ignore
 * the tree structure, and implement visiting the leaf node functors that are
 * of interest.  When used in this way, the tree nodes will be ignored by the
 * visitor (exception that the visit walks through them).  If the tree nodes
 * are to be considered during the visit, then the implementation can override
 * methods contained in this class: depending on where in the overridden
 * implementation the call to super() occurs, either breadth-first, depth-first,
 * or in-line traversal can be supported.
 * <p>
 * Copyright &copy; 2005  David A. Hall
 * @author <a href="mailto:davidahall@users.sf.net">David A. Hall</a>
 */

public class AdaptorVisitor extends AbstractVisitor
        implements
            All.Visitor,
            AndBinary.Visitor,
            AndGenerator.Visitor,
            AndUnary.Visitor,
            Any.Visitor,
            ApplyBinary.Visitor,
            ApplyGenerator.Visitor,
            ApplyUnary.Visitor,
            Bind.Visitor,
            Bind1st.Visitor,
            Bind2nd.Visitor,
            ChainBinary.Visitor,
            ChainUnary.Visitor,
            ComposeBinary.Visitor,
            ComposeUnary.Visitor,
            ConditionalBinary.Visitor,
            ConditionalGenerator.Visitor,
            ConditionalUnary.Visitor,
            Distribute.Visitor,
            Generate.Visitor,
            Generate1st.Visitor,
            Generate2nd.Visitor,
            GenerateBinary.Visitor,
            GenerateUnary.Visitor,
            OrBinary.Visitor,
            OrGenerator.Visitor,
            OrUnary.Visitor
{
    public void visit(All host) {
        for(Iterator iter = host.branches(); iter.hasNext(); ) {
            ((UnaryFunctor) iter.next()).accept(this);
        }
    }
    
    public void visit(AndBinary host) {
        host.getFirstFunctor().accept(this);
        host.getSecondFunctor().accept(this);
    }
        
    public void visit(AndGenerator host) {
        host.getFirstFunctor().accept(this);
        host.getSecondFunctor().accept(this);
    }
        
    public void visit(AndUnary host) {
        host.getFirstFunctor().accept(this);
        host.getSecondFunctor().accept(this);
    }
        
    public void visit(Any host) {
        for(Iterator iter = host.branches(); iter.hasNext(); ) {
            ((UnaryFunctor) iter.next()).accept(this);
        }
    }
    
    public void visit(ApplyBinary host) {
        BinaryFunctor[] fns = host.getFunctors();
        for (int i = 0; i < fns.length; ++i)
            fns[i].accept(this);
    }

    public void visit(ApplyGenerator host) {
        Generator[] fns = host.getGenerators();
        for (int i = 0; i < fns.length; ++i)
            fns[i].accept(this);
    }
        
    public void visit(ApplyUnary host) {
        UnaryFunctor[] fns = host.getFunctors();
        for (int i = 0; i < fns.length; ++i)
            fns[i].accept(this);
    }
        
    public void visit(Bind host) {
        host.getFunctor().accept(this);
    }
        
    public void visit(Bind1st host) {
        host.getFunctor().accept(this);
    }
        
    public void visit(Bind2nd host) {
        host.getFunctor().accept(this);
    }
        
    public void visit(ChainBinary host) {
        host.getInnerFunctor().accept(this);
        host.getOuterFunctor().accept(this);
    }
        
    public void visit(ChainUnary host) {
        host.getInnerFunctor().accept(this);
        host.getOuterFunctor().accept(this);
    }
        
    public void visit(ComposeBinary host) {
        host.getFirstInnerFunctor().accept(this);
        host.getSecondInnerFunctor().accept(this);
        host.getOuterFunctor().accept(this);
    }
        
    public void visit(ComposeUnary host) {
        host.getFirstInnerFunctor().accept(this);
        host.getSecondInnerFunctor().accept(this);
        host.getOuterFunctor().accept(this);
    }
        
    public void visit(ConditionalBinary host) {
        host.getCondition().accept(this);
        host.getTrueFunctor().accept(this);
        host.getFalseFunctor().accept(this);
    }
        
    public void visit(ConditionalGenerator host) {
        host.getCondition().accept(this);
        host.getTrueFunctor().accept(this);
        host.getFalseFunctor().accept(this);
    }
        
    public void visit(ConditionalUnary host) {
        host.getCondition().accept(this);
        host.getTrueFunctor().accept(this);
        host.getFalseFunctor().accept(this);
    }
        
    public void visit(Distribute host) {
        host.getFirstInnerFunctor().accept(this);
        host.getSecondInnerFunctor().accept(this);
        host.getOuterFunctor().accept(this);
    }
        
    public void visit(Generate host) {
        host.getGenerator().accept(this);
        host.getFunctor().accept(this);
    }
        
    public void visit(Generate1st host) {
        host.getGenerator().accept(this);
        host.getFunctor().accept(this);
    }
        
    public void visit(Generate2nd host) {
        host.getGenerator().accept(this);
        host.getFunctor().accept(this);
    }
        
    public void visit(GenerateUnary host) {
        host.getGenerator().accept(this);
    }
        
    public void visit(GenerateBinary host) {
        host.getGenerator().accept(this);
    }

    public void visit(OrBinary host) {
        host.getFirstFunctor().accept(this);
        host.getSecondFunctor().accept(this);
    }
        
    public void visit(OrGenerator host) {
        host.getFirstFunctor().accept(this);
        host.getSecondFunctor().accept(this);
    }
        
    public void visit(OrUnary host) {
        host.getFirstFunctor().accept(this);
        host.getSecondFunctor().accept(this);
    }
        
}

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


/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/

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

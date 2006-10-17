// ============================================================================
// $Id: TestGenericComparator.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
// Copyright (c) 2003  David A. Hall
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
package org.enerj.jga.util;

import junit.framework.TestCase;
import org.enerj.jga.DerivedObject;
import org.enerj.jga.SampleObject;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.jga.fn.property.GetProperty;

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
public class TestGenericComparator extends TestCase {
    public TestGenericComparator (String name){
        super(name);
    }

    public void testGenericComparator() {
        SampleObject widgets = new SampleObject("widgets",42);
        SampleObject mumbles = new SampleObject("mumbles",65);
        SampleObject gizmos  = new SampleObject("gizmos",0);
        SampleObject moreWidgets = new SampleObject("widgets",100);
        
        DerivedObject doodads = new DerivedObject("doodads", -32);
        DerivedObject doohickeys = new DerivedObject("doohickeys",Integer.MAX_VALUE);
        DerivedObject duds = new DerivedObject("duds", Integer.MIN_VALUE);
        SampleObject sample_dud = new SampleObject("duds", Integer.MIN_VALUE);

        UnaryFunctor<SampleObject,String> getname =
            new GetProperty<SampleObject,String>(SampleObject.class,"Name");
        UnaryFunctor<SampleObject,Integer>getcount =
            new GetProperty<SampleObject,Integer>(SampleObject.class,"Count");
        
        GenericComparator<SampleObject,String> gc1 =
            new GenericComparator<SampleObject,String>(getname);

        assertTrue(0 == gc1.compare(widgets, widgets));
        assertTrue(0 == gc1.compare(widgets, moreWidgets));
        assertTrue(0 > gc1.compare(doodads, gizmos));
        assertTrue(0 < gc1.compare(gizmos,  doodads));

        GenericComparator<SampleObject,Integer> gc2 =
            new GenericComparator<SampleObject,Integer>(getcount);

        assertTrue(0 == gc2.compare(duds, duds));
        assertTrue(0 == gc2.compare(duds, sample_dud));
        assertTrue(0 >  gc2.compare(gizmos, doohickeys));
        assertTrue(0 <  gc2.compare(doohickeys, gizmos));
    }

    /** 
     * Entry point 
     */ 
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestGenericComparator.class);
    }
}

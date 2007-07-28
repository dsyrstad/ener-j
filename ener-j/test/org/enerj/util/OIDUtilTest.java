/*******************************************************************************
 * Copyright 2000, 2007 Visual Systems Corporation.
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

package org.enerj.util;

import junit.framework.TestCase;

/**
 * Tests OIDUtil. <p>
 * 
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class OIDUtilTest extends TestCase
{
    /**
     * Construct a OIDUtilTest. 
     *
     * @param name
     */
    public OIDUtilTest(String name)
    {
        super(name);
    }

    public void testNormalRange()
    {
        long oidx = 289;
        int cidx = 781;
        
        long oid = OIDUtil.createOID(cidx, oidx);
        
        assertEquals(13739497300689185L, oid);
        assertEquals(cidx, OIDUtil.getCIDX(oid));
        assertEquals(oidx, OIDUtil.getOIDX(oid));
    }
    
    public void testMin()
    {
        long oidx = 0;
        int cidx = 0;
        
        long oid = OIDUtil.createOID(cidx, oidx);
        
        assertEquals(0L, oid);
        assertEquals(cidx, OIDUtil.getCIDX(oid));
        assertEquals(oidx, OIDUtil.getOIDX(oid));
    }

    public void testMax()
    {
        long oidx = (long)Math.pow(2, 44) - 1;
        int cidx = (int)Math.pow(2, 20) - 1;
        
        long oid = OIDUtil.createOID(cidx, oidx);
        
        assertEquals(0xFFFFFFFFFFFFFFFFL, oid);
        assertEquals(cidx, OIDUtil.getCIDX(oid));
        assertEquals(oidx, OIDUtil.getOIDX(oid));
    }
}

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

package org.enerj.server.bdb;


import com.sleepycatje.bind.tuple.TupleBinding;
import com.sleepycatje.bind.tuple.TupleInput;
import com.sleepycatje.bind.tuple.TupleOutput;

/**
 * Serialize and deserialize the internal OID key entry. 
 * Split the oid into cidx and oidx so we can do partial key searches.
 */
final class OIDKeyTupleBinding extends TupleBinding
{
    private boolean serializeOIDX;
    
    OIDKeyTupleBinding(boolean serializeOIDX)
    {
        this.serializeOIDX = serializeOIDX;
    }
    
    @Override
    public Object entryToObject(TupleInput input)
    {
        int cidx = input.readShort();
        long oidx = input.readLong(); 
        return new OIDKey(cidx, oidx);
    }

    @Override
    public void objectToEntry(Object object, TupleOutput output)
    {
        OIDKey key = (OIDKey)object;
        output.writeShort(key.cidx);
        if (serializeOIDX) {
            output.writeLong(key.oidx);
        }
    }
}
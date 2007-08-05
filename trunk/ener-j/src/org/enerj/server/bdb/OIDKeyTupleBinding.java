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
 * Serialize and de-serialize the internal OID key entry. 
 * Split the oid into cidx and oidx so we can do partial key searches.
 */
final class OIDKeyTupleBinding extends TupleBinding
{
    private boolean serializeOIDX;
    
    OIDKeyTupleBinding(boolean serializeOIDX)
    {
        this.serializeOIDX = serializeOIDX;
    }
    
    int getSerializedSize()
    {
        return serializeOIDX ? 9 : 3;
    }
    
    @Override
    public Object entryToObject(TupleInput input)
    {
        byte[] data = input.getBufferBytes();
        int cidx =  ((data[0] & 0xff) << 16) |
                    ((data[1] & 0xff) <<  8) |
                     (data[2] & 0xff);
        
        long oidx = ((long)(data[3] & 0xff) << 40) |
                    ((long)(data[4] & 0xff) << 32) |
                    ((long)(data[5] & 0xff) << 24) |
                    ((data[6] & 0xff) << 16) |
                    ((data[7] & 0xff) <<  8) |
                     (data[8] & 0xff);
        
        return new OIDKey(cidx, oidx);
    }

    @Override
    public void objectToEntry(Object object, TupleOutput output)
    {
        OIDKey key = (OIDKey)object;

        // Serialize cidx as 3 bytes, and oidx as 6 bytes
        long oidx = key.oidx;
        int cidx = key.cidx;
        
        int len = getSerializedSize();
        output.makeSpace(len);
        byte[] data = output.getBufferBytes();
        
        data[0] = (byte)(cidx >>> 16);
        data[1] = (byte)(cidx >>>  8);
        data[2] = (byte)cidx;

        if (serializeOIDX) {
            data[3] = (byte)(oidx >>> 40);
            data[4] = (byte)(oidx >>> 32);
            data[5] = (byte)(oidx >>> 24);
            data[6] = (byte)(oidx >>> 16);
            data[7] = (byte)(oidx >>>  8);
            data[8] = (byte)oidx;
        }
        
        output.addSize(len);
    }
}
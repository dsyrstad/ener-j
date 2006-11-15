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
// Ener-J
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/CalcCID.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.util;

import java.io.*;

import org.enerj.core.*;
import org.enerj.server.*;

/**
 * Utility to calculate a CID for class files. This is the same calculation used in the 
 * enhancer.
 *
 * @version $Id: CalcCID.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class CalcCID 
{
    
    //----------------------------------------------------------------------
    public static void main(String[] args) throws Exception
    {
        if (args.length == 0) {
            System.out.println("Usage: " + CalcCID.class.getName() + " class-files...");
            System.exit(1);
        }
        
        for (int i = 0; i < args.length; i++) {
            File classFile = new File(args[i]);
            FileInputStream ins = new FileInputStream(classFile);
            byte[] bytes = new byte[ (int)classFile.length() ];
            ins.read(bytes);
            ins.close();
            System.out.println( Long.toHexString( generateClassId(bytes) ) + ' ' + args[i]);
        }

        System.exit(0);
    }
    
    //----------------------------------------------------------------------
    /**
     * Generate class Id.
     *
     * @param someBytesCodes the unenhanced bytecodes of the class.
     *
     * @return a class Id that does not conflict with the system class Id range
     *  of [ObjectServer.NULL_CID..ObjectServer.FIRST_USER_CID).
     *
     * @throws Exception if an error occurs (e.g., java.security.NoSuchAlgorithmException).
     */
    private static long generateClassId(byte[] someByteCodes) throws Exception 
    {
        java.security.MessageDigest sha1Digest = java.security.MessageDigest.getInstance("SHA-1");
        byte[] sha1 = sha1Digest.digest(someByteCodes);
        long cid =   (long)(sha1[0] & 0xff)         |
                    ((long)(sha1[1] & 0xff) <<  8) |
                    ((long)(sha1[2] & 0xff) << 16) |
                    ((long)(sha1[3] & 0xff) << 24) |
                    ((long)(sha1[4] & 0xff) << 32) |
                    ((long)(sha1[5] & 0xff) << 40) |
                    ((long)(sha1[6] & 0xff) << 48) |
                    ((long)(sha1[7] & 0xff) << 56);
        if (cid >= ObjectSerializer.NULL_CID && cid <= ObjectSerializer.LAST_SYSTEM_CID) {
            // Shift it up to make it a valid user CID.
            cid += ObjectSerializer.LAST_SYSTEM_CID;
        }
        
        return cid;
    }
}

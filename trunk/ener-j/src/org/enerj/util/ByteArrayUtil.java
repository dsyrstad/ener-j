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
//$Header: $

package org.enerj.util;



/**
 * Utilities to manipulate byte arrays. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ByteArrayUtil
{
    // No construction
    private ByteArrayUtil()
    {
    }
    
    /**
     * Gets a long value from the byte array buf starting at idx.
     * The byte array buf must be at least (idx+8) bytes in length.
     *
     * @param buf the byte array.
     * @param idx the index to start reading at.
     * 
     * @return the value.
     */
    public static long getLong(byte[] buf, int idx)
    {
        return  
                ((long)buf[idx++] & 0xff) << 56 |
                ((long)buf[idx++] & 0xff) << 48 |
                ((long)buf[idx++] & 0xff) << 40 |
                ((long)buf[idx++] & 0xff) << 32 |
                ((long)buf[idx++] & 0xff) << 24 |
                ((long)buf[idx++] & 0xff) << 16 |
                ((long)buf[idx++] & 0xff) << 8  |
                ((long)buf[idx] & 0xff);
    }

    /**
     * Gets a int value from the byte array buf starting at idx.
     * The byte array buf must be at least (idx+4) bytes in length.
     *
     * @param buf the byte array.
     * @param idx the index to start reading at.
     * 
     * @return the value.
     */
    public static int getInt(byte[] buf, int idx)
    {
        return  
                ((int)buf[idx++] & 0xff) << 24 |
                ((int)buf[idx++] & 0xff) << 16 |
                ((int)buf[idx++] & 0xff) << 8  |
                ((int)buf[idx] & 0xff);
    }

    /**
     * Gets a short value from the byte array buf starting at idx.
     * The byte array buf must be at least (idx+2) bytes in length.
     *
     * @param buf the byte array.
     * @param idx the index to start reading at.
     * 
     * @return the value.
     */
    public static short getShort(byte[] buf, int idx)
    {
        return  (short)(
                (buf[idx++] & 0xff) << 8  |
                (buf[idx] & 0xff));
    }

    /**
     * Gets an unsigned short value from the byte array buf starting at idx.
     * The byte array buf must be at least (idx+2) bytes in length.
     *
     * @param buf the byte array.
     * @param idx the index to start reading at.
     * 
     * @return the value.
     */
    public static int getUnsignedShort(byte[] buf, int idx)
    {
        return  
                (buf[idx++] & 0xff) << 8  |
                (buf[idx] & 0xff);
    }

    /**
     * Puts a long value to the byte array buf starting at idx. 
     * The byte array buf must be at least (idx+8) bytes in length.
     *
     * @param buf the byte array.
     * @param idx the index to start writing at.
     * @param value the value to write.
     * 
     * @return the number of bytes put in buf.
     */
    public static int putLong(byte[] buf, int idx, long value)
    {
        buf[idx++] = (byte)(value >>> 56);
        buf[idx++] = (byte)(value >>> 48);
        buf[idx++] = (byte)(value >>> 40);
        buf[idx++] = (byte)(value >>> 32);
        buf[idx++] = (byte)(value >>> 24);
        buf[idx++] = (byte)(value >>> 16);
        buf[idx++] = (byte)(value >>>  8);
        buf[idx] = (byte)value;
        return 8;
    }

    /**
     * Puts an int value to the byte array buf starting at idx. 
     * The byte array buf must be at least (idx+4) bytes in length.
     *
     * @param buf the byte array.
     * @param idx the index to start writing at.
     * @param value the value to write.
     * 
     * @return the number of bytes put in buf.
     */
    public static int putInt(byte[] buf, int idx, int value)
    {
        buf[idx++] = (byte)(value >>> 24);
        buf[idx++] = (byte)(value >>> 16);
        buf[idx++] = (byte)(value >>>  8);
        buf[idx] = (byte)value;
        return 4;
    }

    /**
     * Puts a short value to the byte array buf starting at idx. 
     * The byte array buf must be at least (idx+2) bytes in length.
     *
     * @param buf the byte array.
     * @param idx the index to start writing at.
     * @param value the value to write.
     * 
     * @return the number of bytes put in buf.
     */
    public static int putShort(byte[] buf, int idx, short value)
    {
        buf[idx++] = (byte)(value >>>  8);
        buf[idx] = (byte)value;
        return 2;
    }


    /**
     * Puts an unsigned short value to the byte array buf starting at idx. 
     * The byte array buf must be at least (idx+2) bytes in length.
     *
     * @param buf the byte array.
     * @param idx the index to start writing at.
     * @param value the value to write.
     * 
     * @return the number of bytes put in buf.
     */
    public static int putUnsignedShort(byte[] buf, int idx, int value)
    {
        buf[idx++] = (byte)(value >>  8);
        buf[idx] = (byte)value;
        return 2;
    }

    /**
     * Puts an UTF8 string into this byte vector. The byte vector is
     * automatically enlarged if necessary.<p>
     * 
     * Note: This was derived from ASM ByteVector.
     * 
     * @param s a String.
     */
    /*public static void putUTF8(byte[] data, int idx, final String s) {
        int charLength = s.length();

        int len = length;
        // optimistic algorithm: instead of computing the byte length and then
        // serializing the string (which requires two loops), we assume the byte
        // length is equal to char length (which is the most frequent case), and
        // we start serializing the string right away. During the serialization,
        // if we find that this assumption is wrong, we continue with the
        // general method.
        data[len++] = (byte) (charLength >>> 8);
        data[len++] = (byte) charLength;
        for (int i = 0; i < charLength; ++i) {
            char c = s.charAt(i);
            if (c >= '\001' && c <= '\177') {
                data[len++] = (byte) c;
            } else {
                int byteLength = i;
                for (int j = i; j < charLength; ++j) {
                    c = s.charAt(j);
                    if (c >= '\001' && c <= '\177') {
                        byteLength++;
                    } else if (c > '\u07FF') {
                        byteLength += 3;
                    } else {
                        byteLength += 2;
                    }
                }
                data[length] = (byte) (byteLength >>> 8);
                data[length + 1] = (byte) byteLength;
                if (length + 2 + byteLength > data.length) {
                    length = len;
                    enlarge(2 + byteLength);
                    data = this.data;
                }
                for (int j = i; j < charLength; ++j) {
                    c = s.charAt(j);
                    if (c >= '\001' && c <= '\177') {
                        data[len++] = (byte) c;
                    } else if (c > '\u07FF') {
                        data[len++] = (byte) (0xE0 | c >> 12 & 0xF);
                        data[len++] = (byte) (0x80 | c >> 6 & 0x3F);
                        data[len++] = (byte) (0x80 | c & 0x3F);
                    } else {
                        data[len++] = (byte) (0xC0 | c >> 6 & 0x1F);
                        data[len++] = (byte) (0x80 | c & 0x3F);
                    }
                }
                break;
            }
        }
    }*/
}

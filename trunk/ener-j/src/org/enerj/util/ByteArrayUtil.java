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

import java.io.IOException;
import java.io.UTFDataFormatException;



/**
 * Utilities to manipulate byte arrays. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ByteArrayUtil
{
    private static final String UTF_MSG = "badly formed modified UTF-8 input";

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
     * Puts a string into the byte array buf encoded as modified UTF-8.<p>
     * 
     * Note: This was derived from ASM ByteVector.
     * 
     * @param buf the buffer to be written to. Its length should be 3 times the length of str plus idx.
     * @param idx the index to start writing at.
     * @param str a String.
     * 
     * @return the length written to buf.
     */
    public static int putModifiedUTF8(byte[] buf, int idx, String str) 
    {
        int charLength = str.length();

        int start = idx;
        for (int i = 0; i < charLength; ++i) {
            char c = str.charAt(i);
            if (c >= '\001' && c <= '\177') {
                buf[idx++] = (byte) c;
            } else if (c > '\u07FF') {
                buf[idx++] = (byte) (0xE0 | c >> 12 & 0xF);
                buf[idx++] = (byte) (0x80 | c >> 6 & 0x3F);
                buf[idx++] = (byte) (0x80 | c & 0x3F);
            } else {
                buf[idx++] = (byte) (0xC0 | c >> 6 & 0x1F);
                buf[idx++] = (byte) (0x80 | c & 0x3F);
            }
        }
        
        return idx - start;
    }
    
    /**
     * Gets a string from the byte array buf encoded as modified UTF-8.<p>
     * 
     * @param buf the buffer to be read. 
     * @param idx the index to start reading at.
     * @param length the length to be read.
     * 
     * @return the resulting string.
     * 
     * @throws IOException if an encoding error occurs.
     */
    public static String getModifiedUTF8(byte[] buf, int idx, int length) throws IOException
    {
        // In the worst case there is one char for every UTF byte. 
        char[] chars = new char[length];
        int charsIdx = 0;
        
        while (idx < length) {
            int byte1 = (int) buf[idx] & 0xff;
            switch (byte1 >> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                // 0xxxxxxx
                idx++;
                chars[charsIdx++] = (char) byte1;
                break;
                
            case 12:
            case 13:
                // 110x xxxx, 10xx xxxx
                idx += 2;
                if (idx > length) {
                    throw new UTFDataFormatException(UTF_MSG);
                }
                
                int byte2 = (int)buf[idx - 1];
                if ((byte2 & 0xc0) != 0x80) {
                    throw new UTFDataFormatException(UTF_MSG);
                }
                
                chars[charsIdx++] = (char)(((byte1 & 0x1f) << 6) | 
                                            (byte2 & 0x3f));
                break;
                
            case 14:
                // 1110 xxxx, 10xx xxxx, 10xx xxxx 
                idx += 3;
                if (idx > length) {
                    throw new UTFDataFormatException(UTF_MSG);
                }
                
                byte2 = (int) buf[idx - 2];
                int byte3 = (int) buf[idx - 1];
                if (((byte2 & 0xc0) != 0x80) || ((byte3 & 0xc0) != 0x80)) {
                    throw new UTFDataFormatException(UTF_MSG);
                }
                
                chars[charsIdx++] = (char)(((byte1 & 0x0f) << 12) | 
                                           ((byte2 & 0x3f) << 6) | 
                                           ((byte3 & 0x3f) << 0));
                break;
                
            default:
                // 10xx xxxx, 1111 xxxx
                throw new UTFDataFormatException(UTF_MSG);
            }
        }

        return new String(chars, 0, charsIdx);
    }
}

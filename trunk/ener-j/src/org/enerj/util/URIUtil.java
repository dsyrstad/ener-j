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
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/URIUtil.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.util;

import java.net.*;
import java.util.*;

/**
 * Utilities methods for dealing with URIs.
 *
 * @version $Id: URIUtil.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class URIUtil 
{
    private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };


    // Don't allow construction
    private URIUtil() 
    {
    }


    /**
     * Parse the query of a URI into a set of properties. Escaped octets are decoded.
     *
     * @param aURI the URI.
     *
     * @return a map of the Properties.
     */
    public static Properties parseQuery(URI aURI)
    {
        return parseQuery(aURI, true);
    }
    

    /**
     * Parse the query of a URI into a set of properties.
     *
     * @param aURI the URI.
     * @param shouldDecode true if escaped octets should be decoded.
     *
     * @return a map of the Properties.
     */
    public static Properties parseQuery(URI aURI, boolean shouldDecode)
    {
        return parseQuery( aURI.getRawQuery(), shouldDecode, new Properties() );
    }


    /**
     * Parse the query of a URI into a set of properties. Escaped octets are decoded.
     *
     * @param aQueryString the URI query String.
     *
     * @return a map of the Properties.
     */
    public static Properties parseQuery(String aQueryString)
    {
        return parseQuery(aQueryString, true, new Properties() );
    }
    

    /**
     * Parse the query of a URI into a set of properties.
     *
     * @param aQueryString the URI query String.
     * @param shouldDecode true if escaped octets should be decoded.
     * @param someProperties a map of Properties that will be amended by this method.
     *
     * @return a map of the Properties.
     */
    public static Properties parseQuery(String aQueryString, boolean shouldDecode, Properties someProperties)
    {
        StringTokenizer tokenizer = new StringTokenizer(aQueryString, "&+");
        while ( tokenizer.hasMoreTokens() ) {
            String nv = tokenizer.nextToken();
            String name;
            String value = "";
            int idx = nv.indexOf('=');
            if (idx > 0) {
                name = nv.substring(0, idx);
                value = nv.substring(idx + 1);
            }
            else {
                name = nv;
            }
            
            someProperties.setProperty(name, shouldDecode ? unescape(value) : value );
        }
        
        return someProperties;
    }


    /**
     * Format query parameters back into a query string.
     *
     * @param someParams the name/value pairs. 
     *
     * @return a URI query string (without the leading '?').
     */
    public static String formatQuery(Properties someParams)
    {
        //  TODO  encode name/value
        StringBuffer buf = new StringBuffer();
        Iterator iter = someParams.keySet().iterator();
        String delim = "";
        while (iter.hasNext()) {
            buf.append(delim);
            String name = (String)iter.next();
            String value = someParams.getProperty(name);
            buf.append(name);
            if (value != null && value.length() > 0) {
                buf.append('=');
                buf.append( escape(value) );
            }
            delim = "&";
        }
        
        return buf.toString();
    }


    /**
     * Escapes special characters in a URI query value to hex values (e.g., '%AB').
     *
     * @param aValue the string to be escaped. May be null.
     *
     * @return the escaped string, or null if aValue was null.
     */
    public static String escape(String aValue)
    {
        if (aValue == null) {
            return aValue;
        }

        char[] chars = aValue.toCharArray();
        StringBuffer buf = new StringBuffer(chars.length);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '=' ||
                chars[i] == '\\' ||
                chars[i] == '&' ||
                chars[i] == '+' ||
                chars[i] == '%' ||
                chars[i] == '&') {
                buf.append('%');
                buf.append( HEX[(chars[i] & 0xf0) >> 4] );
                buf.append( HEX[chars[i] & 0x0f ]);
            }
            else {
                buf.append(chars[i]);
            }
        }
        
        return buf.toString();
    }


    /**
     * Unescapes hex values in a URI query value.
     *
     * @param aValue the string to be unescaped. May be null.
     *
     * @return the unescaped string, or null if aValue was null.
     */
    public static String unescape(String aValue)
    {
        if (aValue == null) {
            return aValue;
        }

        char[] chars = aValue.toCharArray();
        StringBuffer buf = new StringBuffer(chars.length);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '%') {
                ++i;
                buf.append( (char)((charToNybble(chars[i++]) << 4) | charToNybble(chars[i])) );
            }
            else {
                buf.append(chars[i]);
            }
        }
        
        return buf.toString();
    }


    private static int charToNybble(char c)
    {
        if (Character.isDigit(c)) {
            return c - '0';
        }
        
        return (Character.toUpperCase(c) - 'A') + 10;
    }
}



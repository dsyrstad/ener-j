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
//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/StringUtil.java,v 1.4 2005/10/31 01:12:35 dsyrstad Exp $

package org.enerj.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Static String utilities. 
 * 
 * @version $Id: StringUtil.java,v 1.4 2005/10/31 01:12:35 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class StringUtil 
{
    
    //--------------------------------------------------------------------------------
    // Don't allow construction
    private StringUtil() 
    {
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Appends aValue's string representation to aBuffer. Recursively calls #toString(Object, boolean, boolean).
     * 
     * @param aValue the value to be converted to a String and appended to aBuffer. May be null.
     * @param aBuffer the StringBuffer to appended to.
     * @param includeHashCode true if the aValue's identity hash code should be appended to aValue's class name.
     * @param useObjToString true if aValue.toString() should be appended. Otherwise, the result of a recursive call
     *  to #toString(Object, boolean, boolean) is used.
     */
    private static void emitValue(Object aValue, StringBuffer aBuffer, boolean includeHashCode, boolean useObjToString)
    {
        if (useObjToString) {
            aBuffer.append(aValue);
        }
        else {
            aBuffer.append( StringUtil.toString(aValue, includeHashCode, useObjToString) );
        }
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Returns aValue's string representation via reflection. All fields of aValue are dumped, including
     * superclass fields. Each field name of a superclass field is prefix by one or more '.' indicating its
     * distance from aValue's class. This method may recursively call itself.
     * 
     * @param aValue the value to be converted to a String. May be null.
     * @param includeHashCode true if the aValue's identity hash code should be appended to aValue's class name.
     * @param useObjToString true if aValue.toString() should be appended. Otherwise, the result of a recursive call
     *  to #toString(Object, boolean, boolean) is used.
     * 
     * @return aValue's detailed String representation.
     */
    public static String toString(Object aValue, boolean includeHashCode, boolean useObjToString)
    {
        if (aValue == null) {
            return "null";
        }
        
        try {
	        // Special handling for dumping primitives (as java.lang objects), arrays, Collections, and Maps
	        if (aValue instanceof Number || aValue instanceof Boolean ||
	            aValue instanceof Character || aValue instanceof Class || aValue.getClass() == Object.class) {
	            return aValue.toString();
	        }
	        
	        StringBuffer buf = new StringBuffer(100);
	        if (aValue instanceof String) {
	            buf.append('"');
	            buf.append( aValue.toString() );
	            buf.append('"');
	            return buf.toString();
	        }
	        
	        if (aValue.getClass().isArray()) {
	            int length = Array.getLength(aValue);
	            buf.append('[');
	            for (int i = 0; i < length; i++) {
	                if (i > 0) {
	                    buf.append(", ");
	                }

	                emitValue( Array.get(aValue, i), buf, includeHashCode, useObjToString);
	            }
	            
	            buf.append(']');
	            return buf.toString();
	        }

	        buf.append( aValue.getClass().getName() );
	        if (includeHashCode) {
	            buf.append('@');
	            buf.append( System.identityHashCode(aValue) );
	        }
	        
	        if (aValue instanceof Collection) {
	            buf.append('[');
	            Iterator iter = ((Collection)aValue).iterator();
	            for (int i = 0; iter.hasNext(); i++) {
	                if (i > 0) {
	                    buf.append(", ");
	                }

	                emitValue( iter.next(), buf, includeHashCode, useObjToString);
	            }
	            
	            buf.append(']');
	            return buf.toString();
	        }
	        else if (aValue instanceof Map) {
	            buf.append('{');
	            Iterator iter = ((Map)aValue).entrySet().iterator();
	            for (int i = 0; iter.hasNext(); i++) {
	                if (i > 0) {
	                    buf.append(", ");
	                }

	                Map.Entry entry = (Map.Entry)iter.next();
	                buf.append("[key=");
	                emitValue( entry.getKey(), buf, includeHashCode, useObjToString);
	                buf.append(", value=");
	                emitValue( entry.getValue(), buf, includeHashCode, useObjToString);
	                buf.append(']');
	            }
	            
	            buf.append('}');
	            return buf.toString();
	        }
        
            buf.append('{');
            String prefix = "";
            int numFields = 0;
            for (Class declClass = aValue.getClass(); declClass != null && declClass != Object.class; 
            		declClass = declClass.getSuperclass(), prefix += ".") {

                Field[] fields = declClass.getDeclaredFields();
	            for (int i = 0; i < fields.length; i++, ++numFields) {
	                fields[i].setAccessible(true);
                    if ((fields[i].getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) == 0) {
                        String name = fields[i].getName();
                        // Don't dump field names containing '$'
                        if (name.indexOf('$') < 0) {
                            if (numFields > 0) {
                                buf.append(", ");
                            }
                            
                            buf.append(prefix);
        	                buf.append(name);
        	                buf.append('=');
        	                emitValue( fields[i].get(aValue), buf, includeHashCode, useObjToString);
                        }
                    }
	            }
            }
            
            buf.append('}');

            return buf.toString();
        }
        catch (Exception e) {
            return "Error while converting to String: " + e;
        }
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Substitutes macros of the form "${key}" in aString to the corresponding value of
     * "key" in someProperties. 
     *
     * @param aString the input string containing the macros.
     * @param someProperties a map of key/value pairs.
     * 
     * @return a String with the macros expanded. If a macro key cannot be found in 
     *  someProperties, the macro is left unexpanded.
     */
    public static String substituteMacros(String aString, Properties someProperties) 
    {
        StringBuilder buf = new StringBuilder(aString);
        int idx = 0;
        while ((idx = buf.indexOf("${", idx)) >= 0) {
            int endIdx = buf.indexOf("}", idx + 2);
            if (endIdx > 0) {
                String key = buf.substring(idx + 2, endIdx);
                String value = someProperties.getProperty(key);
                if (value != null) {
                    buf.replace(idx, endIdx + 1, value);
                }
                else {
                    // Nothing found, skip macro.
                    idx += 2;
                }
            }
        }
        
        return buf.toString();
    }
}

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
// Copyright 2001 - 2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/PluginHelper.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import org.odmg.*;
import org.enerj.server.bdb.BDBObjectServer;
import org.enerj.util.*;

/**
 * Helper class for connecting to plug-ins.
 * <p>
 *
 * @version $Id: PluginHelper.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class PluginHelper 
{
    private static String DEFAULT_LOCAL_OBJECT_SERVER = BDBObjectServer.class.getName();
    private static String DEFAULT_REMOTE_OBJECT_SERVER = BDBObjectServer.class.getName();
    
    private static final Class[] PARAMETERS = { Properties.class };
    
    /**
     * Given a plug-in class name and properties, resolve and connect to a plug-in.
     *
     * @param aClassName the plug-in class name. If null, the default object server plug-in is used.
     * @param someProperties the properties to provide to the plug-in's static connect(Properties) method. 
     *
     * @return an Object returned from the plugin's connect method.
     *
     * @throws ODMGException in the event of an error. <p>
     */
    public static Object connect(String aClassName, Properties someProperties) throws ODMGException
    {
        return invoke("connect", aClassName, someProperties);
    }
    
    /**
     * Given an ObjectServer plug-in class name and properties, resolve and call createDatabase on the plug-in.
     *
     * @param aClassName the plug-in class name. If null, the default object server plug-in is used.
     * @param someProperties the properties to provide to the plug-in's static connect(Properties) method. 
     *
     * @return an Object returned from the plugin's connect method.
     *
     * @throws ODMGException in the event of an error. <p>
     */
    public static void createDatabase(String aClassName, Properties someProperties) throws ODMGException
    {
        invoke("createDatabase", aClassName, someProperties);
    }
    
    /**
     * Given a plug-in class name and properties, resolve and call method on a plug-in.
     *
     * @param methodName the static method on the plug-in. Must take a single Properties parameter.
     * @param aClassName the plug-in class name. If null, the default object server plug-in is used.
     * @param someProperties the properties to provide to the plug-in's static connect(Properties) method. 
     *
     * @return an Object returned from the plugin's connect method.
     *
     * @throws ODMGException in the event of an error. <p>
     */
    private static Object invoke(String methodName, String aClassName, Properties someProperties) throws ODMGException
    {
        if (aClassName == null) {
            aClassName = someProperties.getProperty(ObjectServer.ENERJ_OBJECT_SERVER);
            if (aClassName == null) {
                if (Boolean.valueOf(someProperties.getProperty(ObjectServer.ENERJ_CLIENT_LOCAL, "false"))) {
                    aClassName = DEFAULT_LOCAL_OBJECT_SERVER;
                }
                else {
                    aClassName = DEFAULT_REMOTE_OBJECT_SERVER;
                }
            }
        }
        
        try {
            Class pluginClass = Class.forName(aClassName);
            Method method = pluginClass.getMethod(methodName, PARAMETERS);
            return method.invoke(null, new Object[] { someProperties } );
        }
        catch (Exception e) {
            // Re-map InvocationTargetException's cause
            if (e instanceof InvocationTargetException) {
                Throwable t = e.getCause();
                if (t instanceof Exception) {
                    e = (Exception)t;
                }
            }
            
            if (e instanceof ODMGException) {
                throw (ODMGException)e;
            }

            if (e instanceof ODMGRuntimeException) {
                throw (ODMGRuntimeException)e;
            }
            
            throw new ODMGException("Exception instantiating plugin " + aClassName + ": " + e, e);
        }
    }

}


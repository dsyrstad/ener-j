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
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/RequestProcessorProxy.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.util;

import java.util.*;
import java.lang.reflect.*;



/**
 * Handles proxied interface requests to a RequestProcessor. Allows interface methods to be
 * executed in another thread while appearing to be executed in the caller's thread.
 *
 * @version $Id: RequestProcessorProxy.java,v 1.4 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class RequestProcessorProxy implements InvocationHandler
{
    private Object mObject;
    private RequestProcessor mRequestProcessor;
    

    /**
     * Constructs a new RequestProcessorProxy.
     *
     * @param aThreadName the name for the RequestProcessor thread.
     * @param anObject the object being proxied.
     * @param useDaemonThread if true, a daemon thread is used, otherwise a 
     *  shutdown-blocking thread is used.
     */
    private RequestProcessorProxy(String aThreadName, Object anObject, boolean useDaemonThread)
    {
        mObject = anObject;
        //  TODO  pool these? Would need daemon/non-daemon pool....
        mRequestProcessor = new RequestProcessor(aThreadName, useDaemonThread);
    }


    /**
     * Constructs a new RequestProcessorProxy using an existing RequestProcessor.
     *
     * @param anObject the object being proxied.
     * @param aRequestProcessor an existing RequestProcessor.
     */
    private RequestProcessorProxy(Object anObject, RequestProcessor aRequestProcessor)
    {
        mObject = anObject;
        mRequestProcessor = aRequestProcessor;
    }


    /**
     * Creates a new RequestProcessorProxy using a non-daemon thread.
     *
     * @param aThreadName the name for the RequestProcessor thread.
     * @param anObject the object being proxied.
     */
    public static Object newInstance(String aThreadName, Object anObject) 
    {
        return newInstance(aThreadName, anObject, false);
    }
    

    /**
     * Creates a new RequestProcessorProxy.
     *
     * @param aThreadName the name for the RequestProcessor thread.
     * @param anObject the object being proxied.
     * @param useDaemonThread if true, a daemon thread is used, otherwise a 
     *  shutdown-blocking thread is used.
     */
    public static Object newInstance(String aThreadName, Object anObject, boolean useDaemonThread) 
    {
        return Proxy.newProxyInstance(
            anObject.getClass().getClassLoader(),
            anObject.getClass().getInterfaces(),
            new RequestProcessorProxy(aThreadName, anObject, useDaemonThread) );
    }


    /**
     * Constructs a new RequestProcessorProxy an existing RequestProcessor.
     *
     * @param anObject the object being proxied.
     * @param aRequestProcessor an existing RequestProcessor.
     */
    public static Object newInstance(Object anObject, RequestProcessor aRequestProcessor)
    {
        return Proxy.newProxyInstance(
            anObject.getClass().getClassLoader(),
            anObject.getClass().getInterfaces(),
            new RequestProcessorProxy(anObject, aRequestProcessor) );
    }


    /**
     * Gets the object proxied by this proxy.
     *
     * @return the proxied object.
     */
    public Object getProxiedObject()
    {
        return mObject;
    }
    

    public Object invoke(Object aProxy, Method aMethod, Object[] someArgs) throws Throwable
    {
        // Create request and send it.
        ProxyRequest request = new ProxyRequest(mObject, aMethod, someArgs);
        mRequestProcessor.queueRequestAndWait(request);
        
        // Handle result/exception
        Exception exception = request.getException();
        if (exception != null) {
            throw exception;
        }
        
        return request.getResult();
    }



    /**
     * Represents the proxied method invocation.
     */
    private static final class ProxyRequest extends RequestProcessor.Request
    {
        private Object mObject;
        private Method mMethod; 
        private Object[] mArgs;
        private Object mResult = null;
        

        /**
         * Constructs a new ProxyRequest.
         *
         * @param anObject the object whose method referenced by aMethod will be invoked.
         * @param aMethod the Method from Proxy.invoke().
         * @param someArgs the arguments from Proxy.invoke().
         */
        ProxyRequest(Object anObject, Method aMethod, Object[] someArgs)
        {
            mObject = anObject;
            mMethod = aMethod;
            mArgs = someArgs;
        }


        /**
         * Gets the result of the call. Will be null if getException() is not null.
         */
        Object getResult()
        {
            return mResult;
        }
        

        /**
         * Called from the RequestProcessor thread to handle the method invocation.
         */
        public void run() 
        {
            try {
                mResult = mMethod.invoke(mObject, mArgs);
                complete(null);
            }
            catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                if (t instanceof Exception) {
                    complete((Exception)t);
                }
                else {
                    complete( new RuntimeException("Unknown Throwable: " + t.getMessage(), t) );
                }
            }
            catch (Exception e) {
                complete(e);
            } 
            catch (Throwable t) {
                complete( new RuntimeException("Unknown Throwable from invoke: " + t.getMessage(), t) );
            }
        }
    }
}


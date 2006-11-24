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

package org.enerj.core;

import java.util.Stack;

/**
 * Registers Persisters by thread. Multiple Persisters can be registered for a thread via a stack, but only one
 * is active at a time. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class PersisterRegistry
{
    /** Stack of Persisters for current thread. */
    private static ThreadLocal<Stack<Persister>> sPersisterStackForThread = new ThreadLocal<Stack<Persister>>();

    // No construction.
    private PersisterRegistry() { }
    
    /**
     * Gets the Persister stack for the current thread, initializing it if necessary. 
     *
     * @return the Persister stack.
     */
    private static Stack<Persister> getStackForThread()
    {
        Stack<Persister> stack = sPersisterStackForThread.get();
        if (stack == null) {
            stack = new Stack<Persister>();
            sPersisterStackForThread.set(stack);
        }
        
        return stack;
    }
    
    /**
     * Pushes the Persister onto the stack for the given Thread. aPersister becomes the active
     * Persister for the thread. 
     *
     * @param aPersister the Persister to be pushed.
     */
    public static void pushPersisterForThread(Persister aPersister)
    {
        getStackForThread().push(aPersister);
    }
    
    /**
     * Pops the current Persister from the stack for the given Thread. The next Persister in the
     * stack, if any, becomes the active Persister for the thread. 
     *
     * @return the Persister that was popped.
     */
    public static Persister popPersisterForThread()
    {
        return getStackForThread().pop();
    }
    
    /**
     * Gets the current the Persister for the given Thread. 
     *
     * @return the current Persister, or null if there is no current Persister.
     */
    public static Persister getCurrentPersisterForThread()
    {
        Stack<Persister> stack = getStackForThread();
        if (stack.isEmpty()) {
            return null;
        }
        
        return stack.peek();
    }
}

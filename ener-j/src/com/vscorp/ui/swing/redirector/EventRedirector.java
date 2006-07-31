/*+
 * Copyright 1998 Visual Systems Corporation
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Neither the name "Visual Systems" nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY VISUAL SYSTEMS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL VISUAL SYSTEMS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *-
 */

package com.vscorp.ui.swing.redirector;

import java.awt.AWTError;
import java.lang.reflect.Method;
import java.util.EventObject;

import com.vscorp.ui.swing.VSSwingUtil;

/** 
 * Implements the base event redirector for commonly used AWT and Swing listeners.
 * Its purpose is to avoid creating many inner classes to handle events that
 * are simply redirected back to methods on the outer class. This class may
 * be extended internally or via subclassing to support additional listeners.
 *
 * @author Daniel A. Syrstad
 */
public class EventRedirector {
	private Object eventReceiver;

	//----------------------------------------------------------------------
	protected EventRedirector(Object anEventReceiver) {
		eventReceiver = anEventReceiver;
	}

	//----------------------------------------------------------------------
	protected final Method getEventMethod(String anEventMethodName, Class aParamType) throws AWTError {
		if (anEventMethodName == null)
		   return null;

		try {
			Method method = VSSwingUtil.resolveMethod(eventReceiver, anEventMethodName, new Class[]{aParamType});
			method.setAccessible(true);
			return method;
		}
		catch (Exception e) {
			throw new AWTError("Method exception " + anEventMethodName + ": " + e);
		}
	}

	//----------------------------------------------------------------------
	protected final void callEventMethod(Method anEventMethod, EventObject anEvent) {
	   VSSwingUtil.invokeMethod(anEventMethod, eventReceiver, new Object[]{anEvent});
	}
}

/*
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
 * THIS SOFTWARE IS PROVIDED BY VISUAL SYSTEMS "AS IS" AND
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
*/

package com.vscorp.ui.swing.redirector;

import java.lang.reflect.Method;

/** Redirector for javax.swing.event.CellEditorListener.
 * Automatically generated by GenRedirectors. Do not modify directly.
 */

public class CellEditorRedirector extends EventRedirector implements javax.swing.event.CellEditorListener {
	private Method editingCanceledMethod;
	private Method editingStoppedMethod;

	public CellEditorRedirector(Object anEventReceiver, 
			String editingCanceledMethodName, 
			String editingStoppedMethodName) {
		super(anEventReceiver);
		editingCanceledMethod = getEventMethod(editingCanceledMethodName, javax.swing.event.ChangeEvent.class);
		editingStoppedMethod = getEventMethod(editingStoppedMethodName, javax.swing.event.ChangeEvent.class);
	}

	// Interface Methods:
	public void editingCanceled(javax.swing.event.ChangeEvent anEvent) {
		callEventMethod(editingCanceledMethod, anEvent);
	}
	public void editingStopped(javax.swing.event.ChangeEvent anEvent) {
		callEventMethod(editingStoppedMethod, anEvent);
	}
}

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

/** Redirector for java.awt.event.MouseListener.
 * Automatically generated by GenRedirectors. Do not modify directly.
 */

public class MouseRedirector extends EventRedirector implements java.awt.event.MouseListener {
	private Method mouseClickedMethod;
	private Method mouseEnteredMethod;
	private Method mouseExitedMethod;
	private Method mousePressedMethod;
	private Method mouseReleasedMethod;

	public MouseRedirector(Object anEventReceiver, 
			String mouseClickedMethodName, 
			String mouseEnteredMethodName, 
			String mouseExitedMethodName, 
			String mousePressedMethodName, 
			String mouseReleasedMethodName) {
		super(anEventReceiver);
		mouseClickedMethod = getEventMethod(mouseClickedMethodName, java.awt.event.MouseEvent.class);
		mouseEnteredMethod = getEventMethod(mouseEnteredMethodName, java.awt.event.MouseEvent.class);
		mouseExitedMethod = getEventMethod(mouseExitedMethodName, java.awt.event.MouseEvent.class);
		mousePressedMethod = getEventMethod(mousePressedMethodName, java.awt.event.MouseEvent.class);
		mouseReleasedMethod = getEventMethod(mouseReleasedMethodName, java.awt.event.MouseEvent.class);
	}

	// Interface Methods:
	public void mouseClicked(java.awt.event.MouseEvent anEvent) {
		callEventMethod(mouseClickedMethod, anEvent);
	}
	public void mouseEntered(java.awt.event.MouseEvent anEvent) {
		callEventMethod(mouseEnteredMethod, anEvent);
	}
	public void mouseExited(java.awt.event.MouseEvent anEvent) {
		callEventMethod(mouseExitedMethod, anEvent);
	}
	public void mousePressed(java.awt.event.MouseEvent anEvent) {
		callEventMethod(mousePressedMethod, anEvent);
	}
	public void mouseReleased(java.awt.event.MouseEvent anEvent) {
		callEventMethod(mouseReleasedMethod, anEvent);
	}
}

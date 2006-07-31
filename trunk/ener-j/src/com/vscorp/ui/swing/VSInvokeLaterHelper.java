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

package com.vscorp.ui.swing;

import java.awt.AWTError;
import java.lang.reflect.Method;

/** Allows invocation of a method on a class like SwingUtilities.invokeLater()
 * except that it doesn't require a Runnable inner class and the complications
 * which go with it. This is a helper class for VSSwingUtil.invokeLater().
 *
 * @author Daniel A. Syrstad
 */
class VSInvokeLaterHelper implements Runnable {
   private Object targetObj;
   private Object parameter;
   private Method method;

	//----------------------------------------------------------------------
   /** Construct a new runnable helper.
    *
    * @param aTargetObj the object on which to invoke aMethodName
    * @param aParameter a parameter to be passed to aMethodName, may be null
    * @param aMethodName the method on aTargetObj to invoke
    */
   VSInvokeLaterHelper(Object aTargetObj, Object aParameter, 
                       String aMethodName) {

      targetObj = aTargetObj;
      parameter = aParameter;
		try {
			method = VSSwingUtil.resolveMethod(aTargetObj, aMethodName, new Class[]{Object.class});
			method.setAccessible(true);
		}
		catch (Exception e) {
			throw new AWTError("Method exception:" + e); // Unchecked exception
		}
   }		

	//----------------------------------------------------------------------
	/** Implements Runnable.run() and executes the method.
	 */
	public void run() {
	   VSSwingUtil.invokeMethod(method, targetObj, new Object[]{parameter});
    }
}

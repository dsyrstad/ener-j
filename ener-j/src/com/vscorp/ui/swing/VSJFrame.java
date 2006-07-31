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

import java.awt.AWTEvent;
import java.awt.Cursor;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.vscorp.ui.swing.redirector.KeyRedirector;
import com.vscorp.ui.swing.redirector.MouseRedirector;

/** 
 * Extension of JFrame to provide a frame "busy" interface and other useful
 * functions. See com.vscorp.ui.swing.VSBackgroundTask for an example.
 *
 * @see com.vscorp.ui.swing.VSBackgroundTask
 *
 * @author Daniel A. Syrstad
 */
public class VSJFrame extends JFrame {
	protected JPanel glassPane;
	private boolean busy;
    private int savedCloseOp;

	//----------------------------------------------------------------------
	/** Same as javax.swing.JFrame()
	 */
	public VSJFrame() {
		super();
		initVSJFrame();
	}

	//----------------------------------------------------------------------
	/** Same as javax.swing.JFrame(title)
	 */
	public VSJFrame(String title) {
		super(title);
		initVSJFrame();
	}

	//----------------------------------------------------------------------
	/** Do common constructor initialization
	 */
	protected void initVSJFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		// Create our own glass pane which says it manages focus. This is
		// part of the key to capturing keyboard events.
		glassPane = new JPanel() {
			public boolean isManagingFocus() {
				return true;
			}
		};

		// Add a no-op MouseListener so that we enable mouse events
		glassPane.addMouseListener(new MouseRedirector(this, null, null, null, null, null));

		// Eat keystrokes so they don't go to other components
		glassPane.addKeyListener(new KeyRedirector(this, "consumeKey", null, null));

		glassPane.setOpaque(false);
		this.setGlassPane(glassPane);
	}

	//----------------------------------------------------------------------
	private void consumeKey(KeyEvent anEvent) {
		anEvent.consume();
	}

	//----------------------------------------------------------------------
	/** Returns true if the frame is "busy" (via setBusy), otherwise false.
	 */
   public boolean isBusy() {
      return busy;
   }

	//----------------------------------------------------------------------
	/** If aBusyFlag is true, sets the wait cursor on the frame and disable components
	 * by showing the GlassPane. If aBusyFlag is false, the GlassPane is hidden
	 * and the cursor is restored.
	 */
	public void setBusy(boolean aBusyFlag) {
	   busy = aBusyFlag;
		if (busy) {
			// Setting the frame cursor AND glass pane cursor in this order
			// works around the Win32 problem where you have to move the mouse 1
			// pixel to get the Cursor to change.
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
			// Force glass pane to get focus so that we consume KeyEvents
			glassPane.requestFocus();
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// Only allow listeners to perform close action while busy
            savedCloseOp = getDefaultCloseOperation();
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		}
		else {
			glassPane.setCursor(Cursor.getDefaultCursor());
			glassPane.setVisible(false);
            this.getContentPane().requestFocus();
            this.getContentPane().transferFocus();
			this.setCursor(Cursor.getDefaultCursor());
			setDefaultCloseOperation(savedCloseOp);
		}
	}
}

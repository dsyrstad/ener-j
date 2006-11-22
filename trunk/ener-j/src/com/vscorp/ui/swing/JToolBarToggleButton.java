/*+
 * Copyright 2000 Visual Systems Corporation
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

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

/**
 * A toggle button with toolbar support.
 * A toolbar button requires special features such as:<p>
 * - it shouldn't get keyboard focus, nor show the focus border.<p>
 * - it should be sized minimally, i.e. it shouldn't take a lot of extra real
 * estate.<p>
 * <p>
 *
 * @author Daniel A. Syrstad
 */
public class JToolBarToggleButton extends JToggleButton implements ToolBarButton
{
    private static final Insets sOnePixelInsets = new Insets(1, 1, 1, 1);

    private boolean mLabelEnabled = false;


    /**
     * Construct a toolbar button from an Action.
     *
     * @param anAction the action with which the button will be configured
     */
    public JToolBarToggleButton(Action anAction)
    {
        super(anAction);
        configure();
    }


    /**
     * Construct a toolbar button from an Action with optional label
     *
     * @param anAction the action with which the button will be configured
     * @param aLabelFlag if true, the button will be displayed with a label beneath
     * it.
     */
    public JToolBarToggleButton(Action anAction, boolean aLabelFlag)
    {
        super(anAction);
        mLabelEnabled = aLabelFlag;
        configure();
    }


    /**
     * Construct a toolbar button from an Icon.
     *
     * @param anIcon the Icon with which the button will be configured
     */
    public JToolBarToggleButton(Icon anIcon)
    {
        super(anIcon);
        configure();
    }


    /**
     * Sets whether the text Action.NAME property is displayed from the
     * associated Action.
     *
     * @param aLabelFlag if true, the button will be displayed with a label beneath
     * it.
     */
    public void setLabelEnabled(boolean aLabelFlag)
    {
        mLabelEnabled = aLabelFlag;
        configure();
    }


    /**
     * Determines whether the text Action.NAME property is displayed from the
     * associated Action.
     *
     * @return if true, the button is displayed with a label beneath
     * it, otherwise it's not.
     */
    public boolean isLabelEnabled()
    {
        return mLabelEnabled;
    }


    /** Configure the button.
     */
    private void configure()
    {
        // Turn off the focus.
        this.setRequestFocusEnabled(false);
        this.setFocusPainted(false);
        this.setText(null);
        Action action = this.getAction();
        if (action != null) {
            if (mLabelEnabled) {
                this.setText( (String)action.getValue(Action.NAME) );
            }

            Integer mnemonic = (Integer)action.getValue(Action.MNEMONIC_KEY);
            if (mnemonic != null) {
                this.setMnemonic( mnemonic.intValue() );
            }
        }

        this.setMargin(sOnePixelInsets);
        this.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
    }
}

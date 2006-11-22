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

//package com.vscorp.swing;
package com.vscorp.ui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFileChooser;

/**
 * Combination of a file name text field and a file chooser pop-up.
 *
 * @author Daniel A. Syrstad
 */
public class FileChooserField extends ChooserField
{
    private JFileChooser mJFileChooser;
    private Component mOwnerComponent;


    /**
     * Create a new FileChooserField with no text and the default FileChooser
     * created by "new JFileChooser()".
     */
    public FileChooserField(Component anOwnerComponent)
    {
       this(anOwnerComponent, "", new JFileChooser() );
    }


    /**
     * Create a new FileChooserField using the specified text (a pathname) and
     * JFileChooser.
     *
     * @param anOwnerComponent the owner of the dialog, may be null if no owner
     *  is desired.
     * @param aPathName the pathname to use for the text of the field.
     * @param aJFileChooser the JFileChooser to use for selection of the path.
     */
    public FileChooserField(Component anOwnerComponent, String aPathName,
                JFileChooser aJFileChooser)
    {
        super();

        mOwnerComponent = anOwnerComponent;
        setText(aPathName);
        mJFileChooser = aJFileChooser;

        JButton button = new JButton("...");
        button.setDefaultCapable(false);
        button.setMargin( new Insets(1, 1, 1, 1) );
        // Make the button no taller than the text field
        int buttonWidth = button.getPreferredSize().width;
        int textHeight = getTextField().getPreferredSize().height;
        button.setMaximumSize( new Dimension(buttonWidth, textHeight) );
        setPopupButton(button);
    }


    /**
     * Gets the text of this field. Equivalent to getTextField().getText().
     *
     * @return the text of the field.
     */
    public String getText()
    {
        return super.getText();
    }


    /**
     * Sets the text of this field. Equivalent to getTextField().setText(s).
     *
     * @param aString the text of the field.
     */
    public void setText(String aString)
    {
        super.setText(aString);
    }


    /**
     * Verifies the content of the field. Default implementation returns true.
     * Also fires a ChangeEvent if field has changed.
     *
     * @return true
     */
    protected boolean verify()
    {
        setText( getText() );
        return true;
    }


    /**
     * Opens the popup if it is closed.
     */
    public void openPopup()
    {
        if (mJFileChooser.showDialog(mOwnerComponent, null) ==
            JFileChooser.APPROVE_OPTION) {
            setText( mJFileChooser.getSelectedFile().getAbsolutePath() );
        }
    }


    /**
     * Closes the popup if it is open.
     */
    public void closePopup()
    {
        // JFileChooser doesn't allow this.
    }

}


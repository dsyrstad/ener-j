// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/TextChooserField.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Combination of a field and a text field editor pop-up.
 *
 * @author Daniel A. Syrstad
 */
public class TextChooserField extends ChooserField
{
    private String mPrompt;
    private String mTitle;
    private JTextField mDialogTextField = new JTextField();


    /**
     * Create a new TextChooserField using the specified text.
     *
     * @param anOwnerComponent the owner of the dialog, may be null if no owner
     *  is desired.
     * @param aTextValue the initial text for the field.
     * @param aPrompt a localized prompt string for the dialog, may be null
     * @param aTitle a title for the popup dialog, may be null
     * @deprecated anOwnerComponent is no longer used, use alternate form of
     * the constructor.
     */
    public TextChooserField(Component anOwnerComponent, String aTextValue,
                String aPrompt, String aTitle)
    {
        this(aTextValue, aPrompt, aTitle);
    }


    /**
     * Create a new TextChooserField using the specified text.
     *
     * @param aTextValue the initial text for the field.
     * @param aPrompt a localized prompt string for the dialog, may be null
     * @param aTitle a title for the popup dialog, may be null
     */
    public TextChooserField(String aTextValue, String aPrompt, String aTitle)
    {
        super();

        mPrompt = aPrompt;
        if (mPrompt == null) {
            mPrompt = "";
        }

        mTitle = aTitle;

        setText(aTextValue);

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
     * Gets the text field used on the dialog.
     *
     * @return the JTextField used on the pop-up dialog.
     */
    public JTextField getDialogTextField()
    {
        return mDialogTextField;
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
     * Sets the title for the editor dialog box. If not set, this will default
     * to the title of the owner Frame/Dialog.
     *
     * @param aTitle the dialog's title.
     */
    public void setTitle(String aTitle) 
    {
        mTitle = aTitle;
    }


    /**
     * Gets the title for the editor dialog box. 
     *
     * @return the dialog's title or null if not set.
     */
    public String getTitle() 
    {
        return mTitle;
    }


    /**
     * Opens the popup if it is closed.
     */
    public void openPopup()
    {
        String title = mTitle;
        if (title == null || title.length() == 0) {
            Window window = (Window)SwingUtilities.getAncestorOfClass(Window.class, this);
            if (window != null) {
                if (window instanceof Frame) {
                    title = ((Frame)window).getTitle();
                }
                else if (window instanceof Dialog) {
                    title = ((Dialog)window).getTitle();
                }
            }
        }
        
        mDialogTextField.setText( this.getText() );
        // Set the preferred size to be at least as big as the current field.
        mDialogTextField.setPreferredSize( this.getSize() );

        Object[] components = new Object[] { mPrompt, mDialogTextField };

        Component ownerComponent = SwingUtilities.getRoot( getPopupButton() );
        int result = JOptionPane.showConfirmDialog(ownerComponent, components, title,
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            setText( mDialogTextField.getText() );
        }

        mDialogTextField.setText("");
    }


    /**
     * Closes the popup if it is open.
     */
    public void closePopup()
    {
        // We don't need this. The dialog is modal.
    }

}


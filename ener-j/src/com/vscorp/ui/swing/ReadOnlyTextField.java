// Ener-J
// Copyright 1998 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/ReadOnlyTextField.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing;

import javax.swing.JTextField;

/**
 * A field that allows its contents to be selected and copied to the clipboard,
 * but otherwise acts like a JLabel.
 *
 * @author Daniel A. Syrstad
 */
public class ReadOnlyTextField extends JTextField
{
    //----------------------------------------------------------------------
    /**
     * Create a new ReadOnlyTextField using the specified text.
     *
     * @param aTextString the initial text for the field.
     */
    public ReadOnlyTextField(String aTextString)
    {
        super(aTextString);
        configure();
    }

    //----------------------------------------------------------------------
    /**
     * Create a new ReadOnlyTextField using the specified number of columns
     *
     * @param aColumnWidth  the number of columns to use to calculate
     *   the preferred width.  If columns is set to zero, the
     *   preferred width will be whatever naturally results from
     *   the component implementation.
     */
    public ReadOnlyTextField(int aColumnWidth)
    {
        super(aColumnWidth);
        configure();
    }

    //----------------------------------------------------------------------
    /**
     * Configures the text field to be selectable/read-only. Called by constructors
     * to complete initialization.
     */
    protected void configure()
    {
        setEditable(false);
        setBorder( javax.swing.border.LineBorder.createGrayLineBorder() );
        setOpaque(false);
    }
}


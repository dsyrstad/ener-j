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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Combination of a date text field and a pop-up calendar.
 *
 * @author Daniel A. Syrstad
 */
public class DateChooser extends ChooserField
{
    private DateChooserPopup mDateChooserPopup = null;
    private DateFormat mDateFormat;
    private GregorianCalendar mCalendar;
    private boolean mEditTimeFlag = false;
    private boolean mEditDateFlag = true;
    private boolean mNullAllowed = false;


    /**
     * Create a new DateChooser using the current date.
     */
    public DateChooser()
    {
       this( new GregorianCalendar() );
    }


    /**
     * Create a new DateChooser using the specified date.
     *
     * @param aCalendar the calendar to use as a starting point
     */
    public DateChooser(GregorianCalendar aCalendar)
    {
        this(aCalendar, DateFormat.getDateInstance(DateFormat.SHORT), true, true, false );
    }


    /**
     * Create a new DateChooser using the specified date and DateFormat.
     *
     * @param aCalendar the calendar to use as a starting point
     * @param aDateFormat a DateFormat to format the field with
     * @param aTextEditableFlag if true, the text of the date is directly editable,
     *  otherwise the popup must be used.
     * @param anEditDateFlag if true, the date should be editable in the popup.
     * @param anEditTimeFlag if true, the time should be editable in the popup.
     *  TODO  anEditTimeFlag has no effect.
     */
    public DateChooser(GregorianCalendar aCalendar, DateFormat aDateFormat,
                        boolean aTextEditableFlag, boolean anEditDateFlag,
                        boolean anEditTimeFlag)
    {
        super();

        setDateFormat(aDateFormat);
        mEditTimeFlag = anEditTimeFlag;
        mEditDateFlag = anEditDateFlag;

        JTextField dateField = getTextField();
        dateField.setEditable(aTextEditableFlag);

        // Right now, Only add the calendar popup if editing dates.
        if (mEditDateFlag) {
            JButton popupButton = new javax.swing.plaf.basic.BasicArrowButton(SwingConstants.SOUTH) {
                public boolean isFocusTraversable() { return false; }
            };

            popupButton.setMargin( new Insets(0, 0, 0 ,0) );
            popupButton.setFocusPainted(false);
            setPopupButton(popupButton);
        }

        setCalendar(aCalendar);
    }


    /**
     * Refreshes the text field to be in sync with the calendar.
     */
    protected void refreshText()
    {
        if (mCalendar == null) {
            setText("");
        }
        else {
            setText( mDateFormat.format( mCalendar.getTime() ) );
        }
    }


    /**
     * Sets whether the field may be empty or cleared by the user, resulting
     * in a null Calendar. By default, this setting is false.
     *
     * @param aNullAllowedFlag if true, the field may be empty, otherwise it
     * the user must enter a date.
     */
    public void setNullAllowed(boolean aNullAllowedFlag)
    {
        mNullAllowed = aNullAllowedFlag;
    }


    /**
     * Gets the date format.
     *
     * @return a DateFormat representing the format of the date used in the component.
     */
    public DateFormat getDateFormat()
    {
        return mDateFormat;
    }


    /**
     * Sets the date format.
     *
     * @param a DateFormat representing the format of the date used in the component.
     */
    public void setDateFormat(DateFormat aDateFormat)
    {
        mDateFormat = aDateFormat;
        mDateFormat.setLenient(false);
        // Make sure the date field is set to a usable size --
        // A wide date of 10/30/2000 23:00:00 (formatted to mDateFormat).
        JTextField dateField = getTextField();
        String origText = dateField.getText();

        GregorianCalendar bigCalendar = new GregorianCalendar(2000, 9, 30, 23, 0, 0);
        String formattedDate = mDateFormat.format( bigCalendar.getTime() );
        dateField.setText(formattedDate);
        Dimension preferredSize = dateField.getPreferredSize();
        // Add 10% to the width for slack
        preferredSize.width += (preferredSize.width + 9) / 10;

        // Reset to original text
        dateField.setText(origText);
        // Now set the size
        dateField.setMinimumSize(preferredSize);
        dateField.setPreferredSize(preferredSize);
    }


    /**
     * Gets the calendar associated with field
     *
     * @return the GregorianCalendar, or null if the field is empty/calendar
     * is null.
     */
    public GregorianCalendar getCalendar()
    {
        // Must reparse from the text because the InputVerifier doesn't
        // always get invoked from a JTable (only gets called on tab).
        parseTextToCalendar();
        if (mCalendar == null) {
            return null;
        }

        return (GregorianCalendar)mCalendar.clone();
    }


    /**
     * Reparses the text field to mCalendar.
     *
     * @return true if the date in the text is valid, else false.
     */
    protected boolean parseTextToCalendar()
    {
        String dateText = getText();
        if (mNullAllowed && dateText.trim().length() == 0) {
            setCalendar(null);
            return true;
        }

        try {
            Date newDate = mDateFormat.parse(dateText);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(newDate);
            setCalendar(calendar);
        }
        catch (ParseException e) {
            refreshText();
            Toolkit.getDefaultToolkit().beep();
            return false;
        }

        return true;
    }


    /**
     * Sets the calendar associated with the field.
     *
     * @param aCalendar the calendar to set on the field. May be null.
     */
    public void setCalendar(GregorianCalendar aCalendar)
    {
        if (aCalendar == null) {
            mCalendar = null;
        }
        else {
            mCalendar = (GregorianCalendar)aCalendar.clone();
            mCalendar.setLenient(false);
        }

        refreshText();
    }


    /**
     * Verifies the content of the field.
     *
     * @return true if the field contents are valid, else false.
     */
    public boolean verify()
    {
        return parseTextToCalendar();
    }


    /**
     * Opens the popup if it is closed.
     */
    public void openPopup()
    {
        if (mDateChooserPopup == null) {
            Window window = (Window)SwingUtilities.getAncestorOfClass(Window.class, this);
            mDateChooserPopup = new DateChooserPopup(window, getPopupButton(), mCalendar, this);
        }

        GregorianCalendar popupCalendar = getCalendar();
        if (popupCalendar == null) {
            popupCalendar = new GregorianCalendar();
        }

        mDateChooserPopup.setCalendar(popupCalendar);
        getTextField().requestFocus();
        mDateChooserPopup.open();
    }


    /**
     * Closes the popup if it is open.
     */
    public void closePopup()
    {
        if (mDateChooserPopup != null) {
            mDateChooserPopup.close();
        }
    }

}


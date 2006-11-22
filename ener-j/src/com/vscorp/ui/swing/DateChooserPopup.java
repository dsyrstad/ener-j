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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.vscorp.util.GregorianCalendarUtils;

/**
 * Pop-up Date Chooser Calendar
 */
public class DateChooserPopup
        implements WindowListener, ActionListener, KeyListener
{

    private static final String MONTH_DECR = "MD";
    private static final String MONTH_INCR = "MI";
    private static final String YEAR_DECR = "YD";
    private static final String YEAR_INCR = "YI";
    private static final String CANCEL = "Cancel";

    private static String[] sMonths;

    private GregorianCalendar mCalendar;
    private JLabel mMonthLabel;
    private JLabel mYearLabel;
    private JButton[] mDayButtons;
    private Color mDefaultButtonColor;
    private Component mUnderComponent;
    private DateChooser mDateChooser;
    private Window mOwner;
    private Window mWindow;
    private JPanel mCalendarPanel;


    /**
     * Construct a date chooser popup with a owner frame and appearing underneath
     * the specified component.
     *
     * @param anOwner the owner window
     * @param anUnderComponent the component that the popup should appear under.
     * @param aStartDate the calendar that represents date that the popup starts at
     * @param aDateChooser if not null, this is the DateChooser component to set
     *  the date on.
     */
    public DateChooserPopup(Window anOwner, Component anUnderComponent,
                            GregorianCalendar aStartDate, DateChooser aDateChooser)
    {
        mOwner = anOwner;
        mUnderComponent = anUnderComponent;
        mCalendar = aStartDate;
        mDateChooser = aDateChooser;
        mCalendarPanel = createCalendarPanel();
    }


    protected JPanel createCalendarPanel()
    {
        DateFormatSymbols dateSymbols = new DateFormatSymbols();
        sMonths = dateSymbols.getMonths();
        JPanel calendarPanel = new JPanel(null);
        calendarPanel.setLayout( new BoxLayout(calendarPanel, BoxLayout.Y_AXIS) );
        calendarPanel.setBorder( BorderFactory.createRaisedBevelBorder() );

        // Create the month selector
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        calendarPanel.add(monthPanel);

        Insets smallInsets = new Insets(1, 1, 1, 1);
        JButton monthDecr = new JButton("<<");
        monthDecr.setName(MONTH_DECR);
        monthDecr.setMargin(smallInsets);
        monthDecr.addActionListener(this);
        monthPanel.add(monthDecr);

        mMonthLabel = new JLabel("X");
        mMonthLabel.setHorizontalAlignment(JLabel.CENTER);
        //mMonthLabel.setBorder(BorderFactory.createEtchedBorder());
        monthPanel.add(Box.createHorizontalGlue());
        monthPanel.add(mMonthLabel);
        monthPanel.add(Box.createHorizontalGlue());


        // Resize the month label to be the maximum needed
        int maxWidth = 0;
        for (int i = 0; i < 12; i++) {
            mMonthLabel.setText(sMonths[i]);
            Dimension size = mMonthLabel.getPreferredSize();
            if (size.width > maxWidth)
                maxWidth = size.width;
        }

        int maxHeight = mMonthLabel.getPreferredSize().height;
        Dimension size = monthDecr.getPreferredSize();
        if (size.height > maxHeight)
            maxHeight = size.height;

        Dimension maxSize = new Dimension(maxWidth, maxHeight);
        mMonthLabel.setPreferredSize(maxSize);
        mMonthLabel.setMinimumSize(maxSize);

        JButton monthIncr = new JButton(">>");
        monthIncr.setMargin(smallInsets);
        monthIncr.setName(MONTH_INCR);
        monthIncr.addActionListener(this);
        monthPanel.add(monthIncr);

        // Create the year selector
        JPanel yearPanel = new JPanel( new FlowLayout(FlowLayout.CENTER, 1, 1) );
        calendarPanel.add(yearPanel);

        JButton yearDecr = new JButton("<<");
        yearDecr.setMargin(smallInsets);
        yearDecr.setName(YEAR_DECR);
        yearDecr.addActionListener(this);
        yearPanel.add(yearDecr);

        mYearLabel = new JLabel("X");
        mYearLabel.setHorizontalAlignment(JLabel.CENTER);
        mYearLabel.setPreferredSize(maxSize);
        mYearLabel.setMinimumSize(maxSize);
        yearPanel.add(Box.createHorizontalGlue());
        yearPanel.add(mYearLabel);
        yearPanel.add(Box.createHorizontalGlue());

        JButton yearIncr = new JButton(">>");
        yearIncr.setMargin(smallInsets);
        yearIncr.setName(YEAR_INCR);
        yearIncr.addActionListener(this);
        yearPanel.add(yearIncr);

        // Create the day selector
        JPanel dayPanel = new JPanel(new GridLayout(7, 7, 0, 0));
        calendarPanel.add(dayPanel);

        // Do Day of Week labels. This is an array of 8, first day at index 1!!!
        String[] weekdays = dateSymbols.getShortWeekdays();
        for (int dow = 1; dow <= 7; ++dow) {
            JLabel day = new JLabel(weekdays[dow].substring(0, 2));
            day.setHorizontalAlignment(JLabel.CENTER);
            dayPanel.add(day);
        }

        int numDayButtons = 6 * 7;
        mDayButtons = new JButton[numDayButtons];
        for (int dom = 0; dom < numDayButtons; ++dom) {
            JButton button = new JButton("23");
            button.addActionListener(this);
            button.setMargin(smallInsets);
            button.setHorizontalAlignment(JButton.RIGHT);
            mDayButtons[dom] = button;
            dayPanel.add(button);
        }

        mDefaultButtonColor = mDayButtons[0].getForeground();
        return calendarPanel;
    }


    /**
     * Sets the view to match the calendar
     */
    protected void setToCalendar()
    {
        // 0 is January
        int monthIdx = mCalendar.get(Calendar.MONTH);
        mMonthLabel.setText(sMonths[monthIdx]);

        int year = mCalendar.get(Calendar.YEAR);
        mYearLabel.setText( String.valueOf(year) );

        int firstDowInMonth = GregorianCalendarUtils.getFirstOfMonthDayOfWeek(mCalendar) - 1;
        int daysInMonth = GregorianCalendarUtils.getNumDaysInMonth(mCalendar);

        // Hide first unused buttons
        int nbutton = 0;
        for (; nbutton < firstDowInMonth; ++nbutton) {
            mDayButtons[nbutton].setVisible(false);
        }

        // Set days of month
        for (int dom = 1; dom <= daysInMonth; ++dom, ++nbutton) {
            JButton b = mDayButtons[nbutton];
            b.setVisible(true);
            b.setText(Integer.toString(dom));
            if (monthIdx == mCalendar.get(Calendar.MONTH) &&
                year == mCalendar.get(Calendar.YEAR) &&
                dom == mCalendar.get(Calendar.DATE) ) {
                b.setForeground(Color.red);
            }
            else {
                b.setForeground(mDefaultButtonColor);
            }
        }

        // Hide the rest of the buttons
        for (; nbutton < mDayButtons.length; ++nbutton) {
            mDayButtons[nbutton].setVisible(false);
        }

    }


    /**
     * Open this popup.
     */
    public void open()
    {
        if (mWindow != null) {
            return;
        }

        mWindow = new Window(mOwner);
        mWindow.add(mCalendarPanel);
        setToCalendar();
        mWindow.pack();

        mOwner.addWindowListener(this);
        mWindow.addKeyListener(this);
        //mWindow.addFocusListener(this);

        Point pt = mUnderComponent.getLocationOnScreen();
        Dimension size = mUnderComponent.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension popupSize = mWindow.getSize();
        pt.x = (pt.x + size.width) - popupSize.width;
        pt.y += size.height;
        if (pt.x < 0) {
            pt.x = 0;
        }
        else if ((pt.x + popupSize.width) > screenSize.width) {
            pt.x = screenSize.width - popupSize.width;
        }

        if (pt.y < 0) {
            pt.y = 0;
        }
        else if ((pt.y + popupSize.height) > screenSize.height) {
            pt.y = screenSize.height - popupSize.height;
        }

        mWindow.setLocation(pt);

        /* Window activation/deactivation events don't work -- we
        really need them. Broken in JDK 1.2 as well.
        mWindow.addWindowListener(new WindowAdapter() {
            public void windowDeactivated() {
                System.out.println("popup Window deactivated");
                System.out.flush();
            }
        });
        */


        mWindow.setVisible(true);
        mWindow.requestFocus();
    }


    /**
     * Close this popup.
     */
    public void close()
    {
        if (mWindow != null) {
            mOwner.removeWindowListener(this);
            mWindow.setVisible(false);
            mWindow.dispose();
            mWindow = null;
        }
    }


    /**
     * Gets the Calendar associated with the popup
     *
     * @return the GregorianCalendar representing the selected date
     */
    public GregorianCalendar getCalendar()
    {
        return mCalendar;
    }


    /**
     * Sets the Calendar associated with the popup
     *
     * @param aCalendar the GregorianCalendar representing the selected date
     */
    public void setCalendar(GregorianCalendar aCalendar)
    {
        mCalendar = aCalendar;
        setToCalendar();
    }


    /** Called when one of the calendar buttons are clicked.
     */
    public void actionPerformed(ActionEvent e)
    {
        JButton comp = (JButton)e.getSource();
        String name = comp.getName();

        if (name == null) {
            // Day field - set the date and get out
            mCalendar.set(Calendar.DATE, Integer.parseInt(comp.getText()));
            if (mDateChooser != null) {
                mDateChooser.setCalendar(mCalendar);
            }

            close();
            return;
        }
        else if (name.equals(MONTH_INCR)) {
            mCalendar.add(Calendar.MONTH, 1);
        }
        else if (name.equals(MONTH_DECR)) {
            mCalendar.add(Calendar.MONTH, -1);
        }
        else if (name.equals(YEAR_INCR)) {
            mCalendar.add(Calendar.YEAR, 1);
        }
        else if (name.equals(YEAR_DECR)) {
            mCalendar.add(Calendar.YEAR, -1);
        }
        else if (name.equals(CANCEL)) {
            close();
            return;
        }

        setToCalendar();
    }


    // Listen for the Escape key and close down.
    public void keyTyped(KeyEvent e)
    {
        if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
            close();
    }

    // Defined by KeyListener interface, but we don't need them.
    public void keyPressed(KeyEvent e)
    {
    }

    public void keyReleased(KeyEvent e)
    {
    }


    // Called when owner window deactivates, we requestFocus.
    public void windowDeactivated(WindowEvent e)
    {
        if (mWindow != null) {
            mWindow.requestFocus();
        }
    }

    // Close if owner frame is activated
    public void windowActivated(WindowEvent e)
    {
        close();
    }

    // Close if owner window closes.
    public void windowClosing(WindowEvent e)
    {
        close();
    }

    public void windowIconified(WindowEvent e)
    {
        close();
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowOpened(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }
}


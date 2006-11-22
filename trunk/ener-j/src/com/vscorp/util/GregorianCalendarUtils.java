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

package com.vscorp.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Utilities for GregorianCalendar to provide additional date information.
 */
public class GregorianCalendarUtils  {
    //                                          J   F   M   A   M   Ju  Jl  A   S   O   N   D
    private static final int[] mDaysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };



    /**
     * Gets the day of the week for the first day of the month.
     *
     * @param aCalendar a GregorianCalendar to query
     *
     * @return a value like get(DAY_OF_WEEK) where the first day of the week
     * (E.g. Sunday) is 1.
     */
    public static int getFirstOfMonthDayOfWeek(GregorianCalendar aCalendar)
    {
        int currDom = aCalendar.get(Calendar.DAY_OF_MONTH);
        aCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // 1 is Sunday (Hmmm... 0 is January, 1 is Sunday ??????)
        int firstDowInMonth = aCalendar.get(Calendar.DAY_OF_WEEK);
        aCalendar.set(Calendar.DAY_OF_MONTH, currDom);
        return firstDowInMonth;
    }



    /**
     * Gets the number of days in the current month.
     *
     * @param aCalendar a GregorianCalendar to query
     *
     * @return the number of days in the current month taking leap years into
     * consideration.
     */
    public static int getNumDaysInMonth(GregorianCalendar aCalendar)
    {
        // Figure out how many days in month. Calendar has no good way of
        // doing this. So we build our own and use Calendar to check leap years.
        int month = aCalendar.get(Calendar.MONTH);
        int monthDays = mDaysInMonth[month];
        if (month == Calendar.FEBRUARY &&
            aCalendar.isLeapYear( aCalendar.get(Calendar.YEAR) ) ) {
            ++monthDays;
        }

        return monthDays;
    }
}
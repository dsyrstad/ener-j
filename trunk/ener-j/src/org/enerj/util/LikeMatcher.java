/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/


package org.enerj.util;


/**
 * Handles OQL LIKE pattern matching.
 * '%' and '*' means match zero or more characters.
 * '_' and '?' means match exactly one character.
 * Optional escape character is allowed.
 */
public class LikeMatcher {
    // TODO the javadoc on this really needs to be cleaned up. HSQLDB may work, but this is really sloppy...
    private char[]   cLike;
    private int[]    wildCardType;
    private int      iLen;
    private boolean  isIgnoreCase;
    private int      iFirstWildCard;
    private boolean  isNull;
    private char     escapeChar;
    boolean          optimised;
    static final int UNDERSCORE_CHAR = 1;
    static final int PERCENT_CHAR    = 2;

    /**
     * Construct a new LikeMatcher.
     * 
     * @param pattern OQL LIKE pattern. '%' and '*' means match zero or more characters.
     *  '_' and '?' means match exactly one character.
     * @param ignoreCase if case should be ignored when matching.
     * @param escape an optional escape character. Should be zero if no escape is desired.
     */
    public LikeMatcher(String pattern, boolean ignoreCase, char escape) {
        this(escape);
        setParams(pattern, ignoreCase);
    }

    public LikeMatcher(char escape) {
        escapeChar   = escape;
    }

    /**
     * param setter
     *
     * @param pattern
     * @param ignoreCase
     */
    public void setParams(String pattern, boolean ignoreCase) {

        isIgnoreCase = ignoreCase;

        normalize(pattern, true);

        optimised = true;
    }

    /**
     * Resets the search pattern.
     */
    public void resetPattern(String s) {
        normalize(s, true);
    }

    private String getStartsWith() {

        if (iLen == 0) {
            return "";
        }

        StringBuffer s = new StringBuffer();
        int          i = 0;

        for (; (i < iLen) && (wildCardType[i] == 0); i++) {
            s.append(cLike[i]);
        }

        if (i == 0) {
            return null;
        }

        return s.toString();
    }

    /**
     * Method declaration
     *
     *
     * @param o
     *
     * @return
     */
    public Boolean compare(Object o) {

        if (o == null) {
            return null;
        }

        String s = o.toString();

        if (isIgnoreCase) {
            s = s.toUpperCase();
        }

        return compareAt(s, 0, 0, s.length()) ? Boolean.TRUE
                                              : Boolean.FALSE;
    }

    /**
     * Method declaration
     *
     *
     * @param s
     * @param i
     * @param j
     * @param jLen
     *
     * @return
     */
    private boolean compareAt(String s, int i, int j, int jLen) {

        for (; i < iLen; i++) {
            switch (wildCardType[i]) {

                case 0 :                  // general character
                    if ((j >= jLen) || (cLike[i] != s.charAt(j++))) {
                        return false;
                    }
                    break;

                case UNDERSCORE_CHAR :    // underscore: do not test this character
                    if (j++ >= jLen) {
                        return false;
                    }
                    break;

                case PERCENT_CHAR :       // percent: none or any character(s)
                    if (++i >= iLen) {
                        return true;
                    }

                    while (j < jLen) {
                        if ((cLike[i] == s.charAt(j))
                                && compareAt(s, i, j, jLen)) {
                            return true;
                        }

                        j++;
                    }

                    return false;
            }
        }

        if (j != jLen) {
            return false;
        }

        return true;
    }

    /**
     * Method declaration
     *
     *
     * @param pattern
     * @param b
     */
    private void normalize(String pattern, boolean b) {

        isNull = pattern == null;

        if (!isNull && isIgnoreCase) {
            pattern = pattern.toUpperCase();
        }

        iLen           = 0;
        iFirstWildCard = -1;

        int l = pattern == null ? 0
                                : pattern.length();

        cLike        = new char[l];
        wildCardType = new int[l];

        boolean bEscaping = false,
                bPercent  = false;

        for (int i = 0; i < l; i++) {
            char c = pattern.charAt(i);

            if (bEscaping == false) {
                if (b && (escapeChar != 0 && escapeChar == c)) {
                    bEscaping = true;

                    continue;
                } else if (c == '_' || c == '?') {
                    wildCardType[iLen] = UNDERSCORE_CHAR;

                    if (iFirstWildCard == -1) {
                        iFirstWildCard = iLen;
                    }
                } else if (c == '%' || c == '*') {
                    if (bPercent) {
                        continue;
                    }

                    bPercent           = true;
                    wildCardType[iLen] = PERCENT_CHAR;

                    if (iFirstWildCard == -1) {
                        iFirstWildCard = iLen;
                    }
                } else {
                    bPercent = false;
                }
            } else {
                bPercent  = false;
                bEscaping = false;
            }

            cLike[iLen++] = c;
        }

        for (int i = 0; i < iLen - 1; i++) {
            if ((wildCardType[i] == PERCENT_CHAR)
                    && (wildCardType[i + 1] == UNDERSCORE_CHAR)) {
                wildCardType[i]     = UNDERSCORE_CHAR;
                wildCardType[i + 1] = PERCENT_CHAR;
            }
        }
    }

    public boolean hasWildcards() {
        return iFirstWildCard != -1;
    }

    public boolean isEquivalentToFalsePredicate() {
        return isNull;
    }

    public boolean isEquivalentToEqualsPredicate() {
        return iFirstWildCard == -1;
    }

    public boolean isEquivalentToNotNullPredicate() {

        if (isNull ||!hasWildcards()) {
            return false;
        }

        for (int i = 0; i < wildCardType.length; i++) {
            if (wildCardType[i] != PERCENT_CHAR) {
                return false;
            }
        }

        return true;
    }

    public boolean isEquivalentToBetweenPredicate() {

        return iFirstWildCard > 0
               && iFirstWildCard == wildCardType.length - 1
               && (cLike[iFirstWildCard] == '%' || cLike[iFirstWildCard] == '*');
    }

    public boolean isEquivalentToBetweenPredicateAugmentedWithLike() {
        return iFirstWildCard > 0 && (cLike[iFirstWildCard] == '%' || cLike[iFirstWildCard] == '*');
    }

    public String getRangeLow() {
        return getStartsWith();
    }

    public String getRangeHigh() {

        String s = getStartsWith();

        return s == null ? null
                         : s.concat("\uffff");
    }

    public String toString() {

        StringBuffer sb = new StringBuffer();

        sb.append(super.toString()).append("[\n");
        sb.append("escapeChar=").append(escapeChar).append('\n');
        sb.append("isNull=").append(isNull).append('\n');
        sb.append("optimised=").append(optimised).append('\n');
        sb.append("isIgnoreCase=").append(isIgnoreCase).append('\n');
        sb.append("iLen=").append(iLen).append('\n');
        sb.append("iFirstWildCard=").append(iFirstWildCard).append('\n');
        sb.append("cLike=");
        sb.append(new String(cLike));
        sb.append('\n');
        sb.append("wildCardType=");
        sb.append(wildCardType.length);
        sb.append(']');

        return sb.toString();
    }
}

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
// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/OQLLexer.java,v 1.4 2005/10/31 01:12:35 dsyrstad Exp $

package org.enerj.query.oql;

import java.io.*;
import java.util.*;

/**
 * This grammar was derived from "The Object Data Standard ODMG 3.0", R.G.G. Cattell, et.al., ISBN 1-55860-647-4.
 *
 * @version $Id: OQLLexer.java,v 1.4 2005/10/31 01:12:35 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class OQLLexer 
{
    private TrackedPositionReader mReader;
    private Token mEOF = null;
    private StringBuffer mValueBuf = new StringBuffer(100);
    /** List of Token representing tokens that have already been visited for look ahead. */
    private LinkedList mLookAheadBuf = new LinkedList();
    /** A buffer of recorded tokens used by mark()/reset(). If null, we're not recording. */
    private LinkedList mRecordedTokens = null;
    /** Stack of mRecordedTokens -- used if mark()/reset() nest. */
    private LinkedList mRecordedTokensStack = new LinkedList();
    /** True if debugging is on. */
    private boolean mDebug = false;
    
    //--------------------------------------------------------------------------------
    public OQLLexer(TrackedPositionReader aReader) 
    {
        mReader = aReader;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Logs a debug message if debug is on.
     * 
     * @param aMsg the message.
     */
    private void debug(String aMsg)
    {
        if (mDebug) {
            System.err.println(aMsg);
        }
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets whether debugging is on or off.
     * 
     * @param aDebugFlag true if debugging is on.
     */
    public void setDebug(boolean aDebugFlag)
    {
        mDebug = aDebugFlag;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Looks ahead to the Nth token without changing the lexer's position.
     *
     * @param anIndex the index to the token. Must be greater than 0.
     * 
     * @return a Token. OQLTokens.TOK_EOF is return on end of input.
     *
     * @throws ParserException if an error occurs.
     */
    public Token lookAhead(int anIndex) throws ParserException
    {
        assert anIndex > 0;

        Token[] tokens = new Token[anIndex];
        // Scan forward.
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = getNextTokenInternal();
        }

        // Push the tokens back on head of list, in reverse order.
        for (int i = tokens.length - 1; i >= 0 ; i--) {
            mLookAheadBuf.addFirst(tokens[i]);
        }

        if (mDebug) debug("lookAhead(" + anIndex + "): " + tokens[anIndex - 1]);
        return tokens[anIndex - 1];
    }

    //--------------------------------------------------------------------------------
    /**
     * Looks ahead a single token without changing the lexer's position.
     *
     * @return a Token. OQLTokens.TOK_EOF is return on end of input.
     *
     * @throws ParserException if an error occurs.
     */
    public Token lookAhead() throws ParserException
    {
        Token token = getNextTokenInternal();
        mLookAheadBuf.addFirst(token);
        if (mDebug) debug("lookAhead(): " + token);
        return token;
    }

    
    //--------------------------------------------------------------------------------
    /**
     * Determines whether the next token, via look-ahead, is is of the specified type.
     * 
     * @param aTokenType the desired token type.
     * 
     * @return true if the next token is of the specified type.
     * 
     * @throws ParserException if an error occurs.
     */
    public boolean isNextToken(TokenType aTokenType) throws ParserException
    {
        return lookAhead().getTokenType().equals(aTokenType);
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Determines if the next token matches the given keyword. To be a keyword, the 
     * token must be a TOK_IDENT and the identifier string must match aKeyword exactly.
     *
     * @param aKeyword the keyword.
     * 
     * @return true if it matches, else false.
     * 
     * @throws ParserException if an error occurs.
     */
    public boolean isNextTokenKeyword(String aKeyword) throws ParserException
    {
        Token token = lookAhead();
        return (token.getTokenType().equals(OQLTokens.TOK_IDENT) && token.getValue().equals(aKeyword));
    }

    //--------------------------------------------------------------------------------
    /**
     * Marks the current point in the lexer stream. Tokens will be recorded for possible later
     * pushback. Calling resetToMark() will stop recording and reset the lexer stream back
     * to the mark. Calling clearMark() will stop recording and clear the recorded tokens.
     * One of these two methods must be used to end the marking process.
     */
    public void mark()
    {
        if (mRecordedTokens != null) {
            // Push the current set and create a new one.
            mRecordedTokensStack.addFirst(mRecordedTokens);
        }
        
        mRecordedTokens = new LinkedList();
        if (mDebug) debug("mark(), stack size: " + mRecordedTokensStack.size());
    }
    
    //--------------------------------------------------------------------------------
    private void popMarkStack()
    {
        if (mRecordedTokensStack.isEmpty()) {
            mRecordedTokens = null;
        }
        else {
            mRecordedTokens = (LinkedList)mRecordedTokensStack.removeFirst();
        }
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Stops recording and resets the lexer stream back to the mark.
     */
    public void resetToMark()
    {
        assert mRecordedTokens != null;
        
        // Recorded tokens get pushed to the front of the look ahead list.
        mLookAheadBuf.addAll(0, mRecordedTokens);
        if (mDebug) debug("resetToMark(), stack size: " + mRecordedTokensStack.size());
        popMarkStack();
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Stops recording and clears the recorded tokens.
     */
    public void clearMark()
    {
        assert mRecordedTokens != null;
        popMarkStack();
        debug("clearToMark()");
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the next token.
     *
     * @return the next token, or TOK_EOF on end of stream.
     *
     * @throws ParserException if an error occurs.
     */
    public Token getNextToken() throws ParserException 
    {
        Token token = getNextTokenInternal();
        if (mRecordedTokens != null && token.getTokenType() != OQLTokens.TOK_EOF) {
            mRecordedTokens.addLast(token);
        }
        
        if (mDebug) debug("Read token " + token);
        return token;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * For internal use: gets the next token.
     *
     * @return the next token, or TOK_EOF on end of stream.
     *
     * @throws ParserException if an error occurs.
     */
    private Token getNextTokenInternal() throws ParserException 
    {
        // If we have a look ahead token available, return it.
        if (!mLookAheadBuf.isEmpty()) {
            return (Token)mLookAheadBuf.removeFirst();
        }

        // EOF previously hit
        if (mEOF != null) {
            return mEOF;
        }

        try {
            int in;
            while ( (in = mReader.read()) != -1) {
                // Ignore white space
                if (Character.isWhitespace((char)in)) {
                    continue;
                }

                // For most of the rest of the tokens, we'll have to look ahead, so get it now.
                int in2 = mReader.lookAhead();

                // Comment "//" or "--" ?
                if ((in == '/' && in2 == '/') || (in == '-' && in2 == '-')) {
                    // Gobble until \n or EOF
                    while (true) {
                        in = mReader.read();
                        if (in == '\n' || in == -1) {
                            break;
                        }
                    }

                    // Try for another token
                    continue;
                }

                // Multi-line comment /* --- */
        		if (in == '/' && in2 == '*') {
                    // Eat '*'
        		    mReader.read();
                    while ((in = mReader.read()) != -1) {
                        if (in == '*' && mReader.lookAhead() == '/') {
                            mReader.read(); // Eat '/'
                            break;
                        }
        		    }

                    if (in == -1) {
                        throw new ParserException("Unexpected EOF in comment", mReader.getTextPosition(), null);
                    }
		    
                    // Try for another token
                    continue;
            	}


                // String or character?
                if (in == '\'' || in == '"') {
                    int quote = in;

                    mValueBuf.setLength(0);
                    while (true) {
                        in2 = mReader.lookAhead();
                        if (in2 == '\n' || in2 == '\r' || in2 == -1) {
                            throw new ParserException("No matching quote found", mReader.getTextPosition(), null);
                        }
                        
                        in = mReader.read();
                        if (in == quote) {
                            break;
                        }
                        
                        if (in=='\\') {
                            mValueBuf.append((char)in);
                            if ((in = mReader.read()) == -1) {
                                break;
                            }
                       }

                        mValueBuf.append((char)in);
                    }

                    // Handle escape sequences.
                    String value = convertEscapes(mValueBuf, mReader.getTextPosition() );
                    if (quote == '\'') {
                        if (value.length() != 1) {
                            throw new ParserException("Character literal is not one character in length", mReader.getTextPosition(), null);
                        }

                        return new Token(OQLTokens.TOK_CHAR_LITERAL, value, mReader.getTextPosition());
                    }

                    return new Token(OQLTokens.TOK_STRING_LITERAL, value, mReader.getTextPosition());
                }

                // Some type of Number?
                if (Character.isDigit((char)in) || (in=='.' && Character.isDigit((char)in2)) ) {
                    TokenType tokenType;
                    mValueBuf.setLength(0);
                    mValueBuf.append((char)in);
                    boolean hasDecimal = copyDigits(true);
                    if (in == '.' || hasDecimal) {
                        tokenType = OQLTokens.TOK_EXACT_NUM_LITERAL;
                    }
                    else {
                        tokenType = OQLTokens.TOK_INTEGER_LITERAL;
                    }

                    in2 = mReader.lookAhead();

                    if (in2 == 'E' || in2 == 'e') {
                        // Scientific Exponent
                        mValueBuf.append( (char)mReader.read() );
                        in2 = mReader.lookAhead();
                        if (in2 == '+' || in2 == '-') {
                            mValueBuf.append( (char)mReader.read() );
                            in2 = mReader.lookAhead();
                        }

                        if (Character.isDigit((char)in2)) {
                            copyDigits(false);
                            tokenType = OQLTokens.TOK_APPROX_NUM_LITERAL;
                        }
                        else {
                            throw new ParserException("Expected digit or +/- to follow exponent in numeric literal", mReader.getTextPosition(), null);
                        }

                    }

                    return new Token(tokenType, mValueBuf.toString(), mReader.getTextPosition());
                }

                // --- Two character operators. 
                // '=' or '==' - equal?
                if (in == '=') {
                    if (in2 == '=') {
                        mReader.read();
                    }

                    return new Token(OQLTokens.TOK_EQ, null, mReader.getTextPosition());
                }

                // `<=' Less than or equal?
                if (in == '<' && in2 == '=') {
                    mReader.read();
                    return new Token(OQLTokens.TOK_LE, null, mReader.getTextPosition());
                }

                // `>=' Greater than or equal?
                if (in == '>' && in2 == '=') {
                    mReader.read();
                    return new Token(OQLTokens.TOK_GE, null, mReader.getTextPosition());
                }

                // `!=' not equal
                if (in == '!' && in2 == '=') {
                    mReader.read();
                    return new Token(OQLTokens.TOK_NE, null, mReader.getTextPosition());
                }

                // `||' concatenation operator?	
                if (in == '|' && in2 == '|') {
                    mReader.read();
                    return new Token(OQLTokens.TOK_CONCAT, null, mReader.getTextPosition());
                }

                // `..' range operator?	
                if (in == '.' && in2 == '.') {
                    mReader.read();
                    return new Token(OQLTokens.TOK_DOTDOT, null, mReader.getTextPosition());
                }

                // `->' indirect operator?	
                if (in == '-' && in2 == '>') {
                    mReader.read();
                    return new Token(OQLTokens.TOK_INDIRECT, null, mReader.getTextPosition());
                }


                // --- Single Character operators.
                TokenType tokenType = null;
                switch (in) {
                case '.':   tokenType = OQLTokens.TOK_DOT;      break;
                case ',':   tokenType = OQLTokens.TOK_COMMA;    break;
                case ')':   tokenType = OQLTokens.TOK_RPAREN;   break;
                case '(':   tokenType = OQLTokens.TOK_LPAREN;   break;
                case ';':   tokenType = OQLTokens.TOK_SEMI;     break;
                case ':':   tokenType = OQLTokens.TOK_COLON;    break;
                case '+':   tokenType = OQLTokens.TOK_PLUS;     break;
                case '-':   tokenType = OQLTokens.TOK_MINUS;    break;
                case '*':   tokenType = OQLTokens.TOK_ASTERISK; break;
                case '/':   tokenType = OQLTokens.TOK_DIV;      break;
                case '<':   tokenType = OQLTokens.TOK_LT;       break;
                case '>':   tokenType = OQLTokens.TOK_GT;       break;
                case '[':   tokenType = OQLTokens.TOK_LBRACK;   break;
                case ']':   tokenType = OQLTokens.TOK_RBRACK;   break;
                case '$':   tokenType = OQLTokens.TOK_DOLLAR;   break;
                }

                if (tokenType != null) {
                    return new Token(tokenType, null, mReader.getTextPosition());
                }

                // Identifier or Keyword. We treat keywords as identifiers (terminals) until
                // we get into the parser and expect a keyword. This prevents keywords from 
                // clashing with identifiers and method names.
                if (Character.isLetter((char)in) || in == '_') {
                    mValueBuf.setLength(0);
                    mValueBuf.append((char)in);
                    while (true) {
                        in2 = mReader.lookAhead();
                        if (Character.isLetter((char)in2) || in2 == '_' || Character.isDigit((char)in2)) {
                            mValueBuf.append( (char)mReader.read() );
                        }
                        else {
                            break;
                        }
                    }

                    String word = mValueBuf.toString();
                    return new Token(OQLTokens.TOK_IDENT, word, mReader.getTextPosition());
                }

                throw new ParserException("Unexpected character: " + (char)in, mReader.getTextPosition(), null);
            } // End while

            // If we got here, we have EOF. Close the reader and return TOK_EOF.
            mReader.close();
            mEOF = new Token(OQLTokens.TOK_EOF, null, mReader.getTextPosition());
            mReader = null;
            return mEOF;
        }
        catch (IOException e) {
            throw new ParserException("I/O error on input stream", null, e);
        }
    }

    //--------------------------------------------------------------------------------
    /**
     * Copy all digits from input to mValueBuf.
     *
     * @param allowDecimal if true, a single decimal point is allowed.
     *
     * @return true if a decimal point was copied to mValueBuf.
     *
     * @throws IOException if an I/O error occurs.
     */
    private boolean copyDigits(boolean allowDecimal) throws IOException
    {
        boolean foundDecimal = false;
        while (true) {
            int c = (char)mReader.lookAhead();
            if ((c == '.' && allowDecimal && !foundDecimal) || Character.isDigit((char)c)) {
                if (c == '.') {
                    foundDecimal = true;
                }
                
                mValueBuf.append( (char)mReader.read());
            }
            else {
                break;
            }
        }
        
        return foundDecimal;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Convert escape sequences to literal characters.
     *
     * @param aStringBuffer the characters that may contain escape sequences.
     * @param aTextPosition the TextPosition of the string.
     *
     * @return the resulting string.
     *
     * @throws ParserException if an error occurs due to an invalid escape sequence.
     */
    private String convertEscapes(StringBuffer aStringBuffer, TextPosition aTextPosition) throws ParserException
    {
        char[] someChars = new char[ mValueBuf.length() ];
        mValueBuf.getChars(0, someChars.length, someChars, 0);

        StringBuffer result = new StringBuffer(someChars.length);
        for (int i = 0; i < someChars.length; i++) {
            if (someChars[i] == '\\' && (i + 1) < someChars.length) {
                ++i;
                char c;
                switch (someChars[i]) {
                case 'r':   c = '\r';   break;
                case 'n':   c = '\n';   break;
                case 't':   c = '\t';   break;
                case 'f':   c = '\f';   break;
                case '\\':   c = '\\';   break;
                case 'u':
                    if ((i + 4) < someChars.length) {
                        ++i;
                        String hex = String.valueOf(someChars[i]) + someChars[i + 1] + someChars[i + 2] + someChars[i + 3];
                        i += 3;
                        try {
                            c = (char)Integer.parseInt(hex, 16);
                        }
                        catch (NumberFormatException e) {
                            throw new ParserException("Invalid hex sequence in \\u character", aTextPosition, null);
                        }
                    }
                    else {
                        throw new ParserException("Not enough digits in \\u character", aTextPosition, null);
                    }
                    break;

                default:
                    throw new ParserException("Invalid escape sequence '\\" + someChars[i] + "'", aTextPosition, null);
                }

                result.append(c);
            }
            else {
                result.append(someChars[i]);
            }
        }
        
        return result.toString();
    }
}

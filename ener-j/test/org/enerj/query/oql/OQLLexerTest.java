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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/query/oql/OQLLexerTest.java,v 1.4 2005/10/31 01:12:35 dsyrstad Exp $

package org.enerj.query.oql;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests OQLLexer. <p>
 *
 * @version $Id: OQLLexerTest.java,v 1.4 2005/10/31 01:12:35 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class OQLLexerTest extends TestCase
{
    private OQLLexer mLexer;
    

    public OQLLexerTest(String aTestName) 
    {
        super(aTestName);
    }

    public static Test suite() 
    {
        return new TestSuite(OQLLexerTest.class);
    }
    

    /**
     * Asserts that the next token from mLexer meets the given criteria.
     *
     * @param aTokenType the token type.
     * @param aValue the token value, may be null if the token's value should be null.
     * @param aLineNumber the line number to be matched. May be -1 to ignore.
     * @param aColumn the column number to be matched. May be -1 to ignore.
     *
     * @throws Exception in the event of an error, or if the assertion does not hold.
     */
    private void assertTokenEquals(Token aToken, TokenType aTokenType, String aValue, int aLineNumber, int aColumn) throws Exception
    {
        assertEquals("token type", aTokenType, aToken.getTokenType());
        assertEquals("value", aValue, aToken.getValue());

        TextPosition position = aToken.getTextPosition();
        if (aLineNumber != -1) {
            assertEquals("line number", aLineNumber, position.getLineNumber());
        }
        
        if (aColumn != -1) {
            assertEquals("column", aColumn, position.getColumn());
        }
    }
    

    /**
     * Asserts that the next token from mLexer meets the given criteria.
     *
     * @param aTokenType the token type.
     * @param aValue the token value, may be null if the token's value should be null.
     * @param aLineNumber the line number to be matched. May be -1 to ignore.
     * @param aColumn the column number to be matched. May be -1 to ignore.
     *
     * @throws Exception in the event of an error, or if the assertion does not hold.
     */
    private void assertNextTokenEquals(TokenType aTokenType, String aValue, int aLineNumber, int aColumn) throws Exception
    {
        Token token = mLexer.getNextToken();
        assertTokenEquals(token, aTokenType, aValue, aLineNumber, aColumn);
    }
    

    /**
     * Tests all valid tokens.
     */
    public void testValidTokens() throws Exception
    {
        String query = 
          // 1234567890123456789012345678901234567890123456789012
            "( ) , ; : . || = == + - * / <= >= != < > [ ] $ -> .. \n" +
          // 1234 5678901 23456789012345678901234
            "'x' \"string\" 10 123.22 .59 566E+10 \n" +
          // 1234567890123456789012345678901234567890123456789012
            "888e-11 999.e22 .9e+33 9.9e-12 identifier \n" +
          // 123
            "abs";

        Reader reader = new StringReader(query);
        // 8 = intentionally small buffer size.
        mLexer = new OQLLexer( new TrackedPositionReader("string", reader, 8) );
        
        assertNextTokenEquals(OQLTokens.TOK_LPAREN, null, 1, 1);
        assertNextTokenEquals(OQLTokens.TOK_RPAREN, null, 1, 3);
        assertNextTokenEquals(OQLTokens.TOK_COMMA, null, 1, 5);
        assertNextTokenEquals(OQLTokens.TOK_SEMI, null, 1, 7);
        assertNextTokenEquals(OQLTokens.TOK_COLON, null, 1, 9);
        assertNextTokenEquals(OQLTokens.TOK_DOT, null, 1, 11);
        assertNextTokenEquals(OQLTokens.TOK_CONCAT, null, 1, 14);
        assertNextTokenEquals(OQLTokens.TOK_EQ, null, 1, 16);
        assertNextTokenEquals(OQLTokens.TOK_EQ, null, 1, 19);
        assertNextTokenEquals(OQLTokens.TOK_PLUS, null, 1, 21);
        assertNextTokenEquals(OQLTokens.TOK_MINUS, null, 1, 23);
        assertNextTokenEquals(OQLTokens.TOK_ASTERISK, null, 1, 25);
        assertNextTokenEquals(OQLTokens.TOK_DIV, null, 1, 27);
        assertNextTokenEquals(OQLTokens.TOK_LE, null, 1, 30);
        assertNextTokenEquals(OQLTokens.TOK_GE, null, 1, 33);
        assertNextTokenEquals(OQLTokens.TOK_NE, null, 1, 36);
        assertNextTokenEquals(OQLTokens.TOK_LT, null, 1, 38);
        assertNextTokenEquals(OQLTokens.TOK_GT, null, 1, 40);
        assertNextTokenEquals(OQLTokens.TOK_LBRACK, null, 1, 42);
        assertNextTokenEquals(OQLTokens.TOK_RBRACK, null, 1, 44);
        assertNextTokenEquals(OQLTokens.TOK_DOLLAR, null, 1, 46);
        assertNextTokenEquals(OQLTokens.TOK_INDIRECT, null, 1, 49);
        assertNextTokenEquals(OQLTokens.TOK_DOTDOT, null, 1, 52);

        assertNextTokenEquals(OQLTokens.TOK_CHAR_LITERAL, "x", 2, 3);
        assertNextTokenEquals(OQLTokens.TOK_STRING_LITERAL, "string", 2, 12);
        assertNextTokenEquals(OQLTokens.TOK_INTEGER_LITERAL, "10", 2, 15);
        assertNextTokenEquals(OQLTokens.TOK_EXACT_NUM_LITERAL, "123.22", 2, 22);
        assertNextTokenEquals(OQLTokens.TOK_EXACT_NUM_LITERAL, ".59", 2, 26);
        assertNextTokenEquals(OQLTokens.TOK_APPROX_NUM_LITERAL, "566E+10", 2, 34);

        assertNextTokenEquals(OQLTokens.TOK_APPROX_NUM_LITERAL, "888e-11", 3, 7);
        assertNextTokenEquals(OQLTokens.TOK_APPROX_NUM_LITERAL, "999.e22", 3, 15);
        assertNextTokenEquals(OQLTokens.TOK_APPROX_NUM_LITERAL, ".9e+33", 3, 22);
        assertNextTokenEquals(OQLTokens.TOK_APPROX_NUM_LITERAL, "9.9e-12", 3, 30);

        assertNextTokenEquals(OQLTokens.TOK_IDENT, "identifier", 3, 41);
        
        // Check keyword look ahead
        assertTrue( mLexer.isNextTokenKeyword("abs") );
        mLexer.getNextToken();

        // Should be at EOF
        assertNextTokenEquals(OQLTokens.TOK_EOF, null, 4, 4);
        // Should get EOF for subsequent calls.
        assertNextTokenEquals(OQLTokens.TOK_EOF, null, 4, 4);
    }


    /**
     * Tests ignored items like whitespace and comments.
     */
    public void testIgnoredItems() throws Exception
    {
        String query = 
            " \t\f\u0009\u001c\r\n" +  // Whitespace
            "   //Single line comment\n" + // "//" comment
            "-- sql style comment\n" +   // SQL style -- comment
            "  /* multi line comment\n" +
            "  more \n" +
            "  lines \n " +
            "  ending here */   end\n";
  

        Reader reader = new StringReader(query);
        mLexer = new OQLLexer( new TrackedPositionReader("string", reader, 8192) );
        
        assertNextTokenEquals(OQLTokens.TOK_IDENT, "end", 7, 23);
        assertNextTokenEquals(OQLTokens.TOK_EOF, null, 8, 1);
    }


    /**
     * Tests look ahead.
     */
    public void testLookAhead() throws Exception
    {
        String query = "tok1 tok2 tok3 tok4 tok5";
  

        Reader reader = new StringReader(query);
        mLexer = new OQLLexer( new TrackedPositionReader("string", reader, 8192) );
        
        // Test lookahead without reading any tokens first.
        Token token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_IDENT, "tok1", -1, -1);
        
        token = mLexer.lookAhead(3);
        assertTokenEquals(token, OQLTokens.TOK_IDENT, "tok3", -1, -1);

        // Read tok1 and tok2 off 
        assertNextTokenEquals(OQLTokens.TOK_IDENT, "tok1", -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_IDENT, "tok2", -1, -1);
        
        token = mLexer.lookAhead(1);
        assertTokenEquals(token, OQLTokens.TOK_IDENT, "tok3", -1, -1);

        token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_IDENT, "tok3", -1, -1);
        
        token = mLexer.lookAhead(3);
        assertTokenEquals(token, OQLTokens.TOK_IDENT, "tok5", -1, -1);

        token = mLexer.lookAhead(4);
        assertTokenEquals(token, OQLTokens.TOK_EOF, null, -1, -1);

        token = mLexer.lookAhead(10);
        assertTokenEquals(token, OQLTokens.TOK_EOF, null, -1, -1);

        assertNextTokenEquals(OQLTokens.TOK_IDENT, "tok3", -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_IDENT, "tok4", -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_IDENT, "tok5", -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_EOF, null, -1, -1);
    }


    /**
     * Tests ParserExceptions.
     */
    public void testParserExceptions() throws Exception
    {
        String query = "{ xyzzy\n" +    // { is invalid
            "\"bad string\n" +
            "foo 'bad char lit' bar \n" +
            "122.22.aa\n" +   // This should be 12.22 . aa - no error
            "12E+ 12E abc\n" +
            " \"invalid escape \\h\" efg\n" +
            " \"invalid escape \\u00A\" hij\n" +
            " \"invalid escape \\u00XF\" klm\n" +
            "/* multi-line comment with no end";
        
        Reader reader = new StringReader(query);
        mLexer = new OQLLexer( new TrackedPositionReader(null, reader, 8192) );

        try {
            mLexer.getNextToken();
            fail("Expected exception");
        }
        catch (ParserException e) {
            // Expected
            System.out.println("OK(token): " + e);
            TextPosition pos = e.getTextPosition();
            assertEquals(1, pos.getLineNumber());
            assertEquals(1, pos.getColumn());
            assertEquals("{ xyzzy", pos.getLineText());
        }

        assertNextTokenEquals(OQLTokens.TOK_IDENT, "xyzzy", -1, -1);
        
        try {
            mLexer.getNextToken();
            fail("Expected exception");
        }
        catch (ParserException e) {
            // Expected
            System.out.println("OK(string): " + e);
        }

        assertNextTokenEquals(OQLTokens.TOK_IDENT, "foo", -1, -1);

        try {
            mLexer.getNextToken();
            fail("Expected exception");
        }
        catch (ParserException e) {
            // Expected
            System.out.println("OK(char): " + e);
        }

        assertNextTokenEquals(OQLTokens.TOK_IDENT, "bar", -1, -1);
        
        assertNextTokenEquals(OQLTokens.TOK_EXACT_NUM_LITERAL, "122.22", -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_DOT, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_IDENT, "aa", -1, -1);

        try {
            mLexer.getNextToken();
            fail("Expected exception");
        }
        catch (ParserException e) {
            // Expected
            System.out.println("OK(bad exp1): " + e);
        }

        try {
            mLexer.getNextToken();
            fail("Expected exception");
        }
        catch (ParserException e) {
            // Expected
            System.out.println("OK(bad exp2): " + e);
        }

        assertNextTokenEquals(OQLTokens.TOK_IDENT, "abc", -1, -1);

        try {
            mLexer.getNextToken();
            fail("Expected exception");
        }
        catch (ParserException e) {
            // Expected
            System.out.println("OK(bad escape\\h): " + e);
        }

        assertNextTokenEquals(OQLTokens.TOK_IDENT, "efg", -1, -1);

        try {
            mLexer.getNextToken();
            fail("Expected exception");
        }
        catch (ParserException e) {
            // Expected
            System.out.println("OK(bad escape\\u00A): " + e);
        }

        assertNextTokenEquals(OQLTokens.TOK_IDENT, "hij", -1, -1);

        try {
            mLexer.getNextToken();
            fail("Expected exception");
        }
        catch (ParserException e) {
            // Expected
            System.out.println("OK(bad escape\\u00XF): " + e);
        }

        assertNextTokenEquals(OQLTokens.TOK_IDENT, "klm", -1, -1);

        // EOF in multi line comment
        try {
            mLexer.getNextToken();
            fail("Expected exception");
        }
        catch (ParserException e) {
            // Expected
            System.out.println("OK(comment): " + e);
        }

        // Test I/O error on stream -- stream closed.
        reader = new StringReader("");
        mLexer = new OQLLexer( new TrackedPositionReader(null, reader, 8192) );
        reader.close();

        try {
            mLexer.getNextToken();
            fail("Expected exception");
        }
        catch (ParserException e) {
            // Expected
            System.out.println("OK(I/O error): " + e + " cause: " + e.getCause() );
        }

    }


    /**
     * Tests valid escape sequences in strings and characters.
     */
    public void testEscapes() throws Exception
    {
        String query = "\"\\n\\t\\f\\r\" \n" +
            "\"unicode \\u004e\\u004F \"\n" +
            "'\\u0040'\n";

        Reader reader = new StringReader(query);
        mLexer = new OQLLexer( new TrackedPositionReader("string", reader, 8192) );
        
        assertNextTokenEquals(OQLTokens.TOK_STRING_LITERAL, "\n\t\f\r", -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_STRING_LITERAL, "unicode \u004e\u004F ", -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_CHAR_LITERAL, "\u0040", -1, -1);
    }


    /**
     * Tests non-nested mark()/resetToMark().
     */
    public void testNonNestedMark() throws Exception
    {
        String query = "* + - / = ( ) [ ]";

        Reader reader = new StringReader(query);
        mLexer = new OQLLexer( new TrackedPositionReader("string", reader, 8192) );
        
        assertNextTokenEquals(OQLTokens.TOK_ASTERISK, null, -1, -1);
        Token token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_PLUS, null, -1, -1);

        mLexer.mark();
        assertNextTokenEquals(OQLTokens.TOK_PLUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_MINUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_DIV, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_EQ, null, -1, -1);
        token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_LPAREN, null, -1, -1);
        mLexer.resetToMark();
        
        token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_PLUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_PLUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_MINUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_DIV, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_EQ, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_LPAREN, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_RPAREN, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_LBRACK, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_RBRACK, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_EOF, null, -1, -1);
    }


    /**
     * Tests nested mark()/resetToMark().
     */
    public void testNestedMark() throws Exception
    {
        String query = "* + - / = ( ) [ ]";

        Reader reader = new StringReader(query);
        mLexer = new OQLLexer( new TrackedPositionReader("string", reader, 8192) );
        
        assertNextTokenEquals(OQLTokens.TOK_ASTERISK, null, -1, -1);
        Token token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_PLUS, null, -1, -1);

        mLexer.mark();
        assertNextTokenEquals(OQLTokens.TOK_PLUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_MINUS, null, -1, -1);
        
        mLexer.mark();
        assertNextTokenEquals(OQLTokens.TOK_DIV, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_EQ, null, -1, -1);
        token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_LPAREN, null, -1, -1);
        mLexer.resetToMark();

        token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_DIV, null, -1, -1);
        token = mLexer.lookAhead(2);
        assertTokenEquals(token, OQLTokens.TOK_EQ, null, -1, -1);
        mLexer.resetToMark();
        
        token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_PLUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_PLUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_MINUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_DIV, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_EQ, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_LPAREN, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_RPAREN, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_LBRACK, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_RBRACK, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_EOF, null, -1, -1);
    }


    /**
     * Tests mark()/clearMark().
     */
    public void testclearMark() throws Exception
    {
        String query = "* + - / = ( ) [ ]";

        Reader reader = new StringReader(query);
        mLexer = new OQLLexer( new TrackedPositionReader("string", reader, 8192) );
        
        assertNextTokenEquals(OQLTokens.TOK_ASTERISK, null, -1, -1);
        Token token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_PLUS, null, -1, -1);

        mLexer.mark();
        assertNextTokenEquals(OQLTokens.TOK_PLUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_MINUS, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_DIV, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_EQ, null, -1, -1);
        token = mLexer.lookAhead();
        assertTokenEquals(token, OQLTokens.TOK_LPAREN, null, -1, -1);
        mLexer.clearMark();
        
        token = mLexer.lookAhead();
        assertNextTokenEquals(OQLTokens.TOK_LPAREN, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_RPAREN, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_LBRACK, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_RBRACK, null, -1, -1);
        assertNextTokenEquals(OQLTokens.TOK_EOF, null, -1, -1);
    }
}

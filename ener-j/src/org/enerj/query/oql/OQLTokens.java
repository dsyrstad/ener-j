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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/OQLTokens.java,v 1.6 2006/02/16 21:33:45 dsyrstad Exp $

package org.enerj.query.oql;


/**
 * Tokens for OQL.
 *
 * @version $Id: OQLTokens.java,v 1.6 2006/02/16 21:33:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class OQLTokens {
    public static final TokenType TOK_EOF = new TokenType("end of input");

    // Operators
    public static final TokenType TOK_RPAREN = new TokenType(")");
    public static final TokenType TOK_LPAREN = new TokenType("(");
    public static final TokenType TOK_COMMA = new TokenType(",");
    public static final TokenType TOK_SEMI = new TokenType(";");
    public static final TokenType TOK_COLON = new TokenType(":");
    public static final TokenType TOK_DOT = new TokenType(".");
    public static final TokenType TOK_CONCAT = new TokenType("||");
    public static final TokenType TOK_EQ = new TokenType("==");
    public static final TokenType TOK_PLUS = new TokenType("+");
    public static final TokenType TOK_MINUS = new TokenType("-");
    public static final TokenType TOK_DIV = new TokenType("/");
    public static final TokenType TOK_ASTERISK = new TokenType("*");
    public static final TokenType TOK_LE = new TokenType("<=");
    public static final TokenType TOK_GE = new TokenType(">=");
    public static final TokenType TOK_NE = new TokenType("!=");
    public static final TokenType TOK_LT = new TokenType("<");
    public static final TokenType TOK_GT = new TokenType(">");
    public static final TokenType TOK_LBRACK = new TokenType("[");
    public static final TokenType TOK_RBRACK = new TokenType("]");
    public static final TokenType TOK_DOLLAR = new TokenType("$");
    public static final TokenType TOK_INDIRECT = new TokenType("->");
    public static final TokenType TOK_DOTDOT = new TokenType("..");
    
    // Literals
    public static final TokenType TOK_CHAR_LITERAL = new TokenType("character literal");
    public static final TokenType TOK_STRING_LITERAL = new TokenType("string literal");
    public static final TokenType TOK_INTEGER_LITERAL = new TokenType("integer literal");
    public static final TokenType TOK_EXACT_NUM_LITERAL = new TokenType("exact numeric literal");
    public static final TokenType TOK_APPROX_NUM_LITERAL = new TokenType("approximate numeric literal");
    
    public static final TokenType TOK_IDENT = new TokenType("identifier");

    // Pseudo-tokens - not actually parsed by OQLLexer, but emitted by OQLParser.
    public static final TokenType TOK_CHAR = new TokenType("char");
    public static final TokenType TOK_OCTET = new TokenType("octet");
    public static final TokenType TOK_SHORT = new TokenType("short");
    public static final TokenType TOK_LONG = new TokenType("long");
    public static final TokenType TOK_FLOAT = new TokenType("float");
    public static final TokenType TOK_BOOLEAN = new TokenType("boolean");
    public static final TokenType TOK_DOUBLE = new TokenType("double");
    public static final TokenType TOK_UNSIGNED_SHORT = new TokenType("unsigned short");
    public static final TokenType TOK_UNSIGNED_LONG = new TokenType("unsigned long");
    public static final TokenType TOK_LONG_LONG = new TokenType("long long");
    public static final TokenType TOK_STRING = new TokenType("string");
    public static final TokenType TOK_DATE = new TokenType("date");
    public static final TokenType TOK_TIME = new TokenType("time");
    public static final TokenType TOK_TIMESTAMP = new TokenType("timestamp");
    public static final TokenType TOK_INTERVAL = new TokenType("interval");
    public static final TokenType TOK_ENUM = new TokenType("enum");
    public static final TokenType TOK_DICTIONARY = new TokenType("dictionary");
    public static final TokenType TOK_SET = new TokenType("set");
    public static final TokenType TOK_ARRAY = new TokenType("array");
    public static final TokenType TOK_LIST = new TokenType("list");
    public static final TokenType TOK_BAG = new TokenType("bag");
    public static final TokenType TOK_OR = new TokenType("or");
    public static final TokenType TOK_ORELSE = new TokenType("orelse");
    public static final TokenType TOK_AND = new TokenType("and");
    public static final TokenType TOK_ANDTHEN = new TokenType("andthen");
    public static final TokenType TOK_LIKE = new TokenType("like");
    public static final TokenType TOK_ALL = new TokenType("all");
    public static final TokenType TOK_ANY = new TokenType("any");
    public static final TokenType TOK_SOME = new TokenType("some");
    public static final TokenType TOK_UNION = new TokenType("union");
    public static final TokenType TOK_EXCEPT = new TokenType("except");
    public static final TokenType TOK_MOD = new TokenType("mod");
    public static final TokenType TOK_INTERSECT = new TokenType("intersect");
    public static final TokenType TOK_ABS = new TokenType("abs");
    public static final TokenType TOK_NOT = new TokenType("not");
    public static final TokenType TOK_DISTINCT = new TokenType("distinct");
    public static final TokenType TOK_LISTTOSET = new TokenType("listtoset");
    public static final TokenType TOK_ELEMENT = new TokenType("element");
    public static final TokenType TOK_FLATTEN = new TokenType("flatten");
    public static final TokenType TOK_FIRST = new TokenType("first");
    public static final TokenType TOK_LAST = new TokenType("last");
    public static final TokenType TOK_UNIQUE = new TokenType("unique");
    public static final TokenType TOK_EXISTS = new TokenType("exists");
    public static final TokenType TOK_SUM = new TokenType("sum");
    public static final TokenType TOK_MAX = new TokenType("max");
    public static final TokenType TOK_MIN = new TokenType("min");
    public static final TokenType TOK_AVG = new TokenType("avg");
    public static final TokenType TOK_COUNT = new TokenType("count");
    public static final TokenType TOK_IS_DEFINED = new TokenType("is_defined");
    public static final TokenType TOK_IS_UNDEFINED = new TokenType("is_undefined");
    public static final TokenType TOK_NEW = new TokenType("new");
    

    /**
     * Do not allow construction.
     */
    private OQLTokens() {
    }
    
}

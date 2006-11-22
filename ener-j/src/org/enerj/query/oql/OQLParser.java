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
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/OQLParser.java,v 1.14 2006/03/05 03:37:28 dsyrstad Exp $

package org.enerj.query.oql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.enerj.query.oql.ast.AST;
import org.enerj.query.oql.ast.AggregateExprAST;
import org.enerj.query.oql.ast.BigDecimalLiteralAST;
import org.enerj.query.oql.ast.BinOpExprAST;
import org.enerj.query.oql.ast.BooleanLiteralAST;
import org.enerj.query.oql.ast.CastExprAST;
import org.enerj.query.oql.ast.CharLiteralAST;
import org.enerj.query.oql.ast.CollectionConstructionAST;
import org.enerj.query.oql.ast.CollectionExprAST;
import org.enerj.query.oql.ast.ConversionExprAST;
import org.enerj.query.oql.ast.DateLiteralAST;
import org.enerj.query.oql.ast.DefineQueryAST;
import org.enerj.query.oql.ast.DoubleLiteralAST;
import org.enerj.query.oql.ast.FieldAST;
import org.enerj.query.oql.ast.FieldListAST;
import org.enerj.query.oql.ast.FromClauseAST;
import org.enerj.query.oql.ast.GroupClauseAST;
import org.enerj.query.oql.ast.IdentifierExprAST;
import org.enerj.query.oql.ast.IdentifierWithArgumentsAST;
import org.enerj.query.oql.ast.ImportAST;
import org.enerj.query.oql.ast.InClauseAST;
import org.enerj.query.oql.ast.InExprAST;
import org.enerj.query.oql.ast.IndexListAST;
import org.enerj.query.oql.ast.IndexRangeAST;
import org.enerj.query.oql.ast.IndexedExprAST;
import org.enerj.query.oql.ast.IteratorDefAST;
import org.enerj.query.oql.ast.ListRangeAST;
import org.enerj.query.oql.ast.LongLiteralAST;
import org.enerj.query.oql.ast.MethodCallExprAST;
import org.enerj.query.oql.ast.NilLiteralAST;
import org.enerj.query.oql.ast.ParameterListAST;
import org.enerj.query.oql.ast.ProjectionAST;
import org.enerj.query.oql.ast.ProjectionAttributesAST;
import org.enerj.query.oql.ast.ProjectionListAST;
import org.enerj.query.oql.ast.QualifiedNameAST;
import org.enerj.query.oql.ast.QuantifierExprAST;
import org.enerj.query.oql.ast.QueryAST;
import org.enerj.query.oql.ast.QueryParamAST;
import org.enerj.query.oql.ast.QueryProgramAST;
import org.enerj.query.oql.ast.RelationalExprAST;
import org.enerj.query.oql.ast.SelectExprAST;
import org.enerj.query.oql.ast.SortCriteriaAST;
import org.enerj.query.oql.ast.SortCriterionAST;
import org.enerj.query.oql.ast.StringLiteralAST;
import org.enerj.query.oql.ast.StructConstructionAST;
import org.enerj.query.oql.ast.TimeLiteralAST;
import org.enerj.query.oql.ast.TimestampLiteralAST;
import org.enerj.query.oql.ast.TypeAST;
import org.enerj.query.oql.ast.UnaryExprAST;
import org.enerj.query.oql.ast.UndefineQueryAST;
import org.enerj.query.oql.ast.UndefinedExprAST;
import org.enerj.query.oql.ast.ValueListAST;

/**
 * Ener-J OQL Parser. This grammar was derived from "The Object Data Standard ODMG 3.0", R.G.G.
 * Cattell, et.al., ISBN 1-55860-647-4.
 * 
 * @version $Id: OQLParser.java,v 1.14 2006/03/05 03:37:28 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class OQLParser
{
    private OQLLexer mLexer;
    /** True if debugging is on. */
    private boolean mDebug = false;


    /**
     * Constructs a new OQLParser.
     * 
     * @param aLexer the OQLLexer to use.
     */
    public OQLParser(OQLLexer aLexer)
    {
        mLexer = aLexer;
    }


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


    /**
     * Sets whether debugging is on or off.
     * 
     * @param aDebugFlag true if debugging is on.
     */
    public void setDebug(boolean aDebugFlag)
    {
        mDebug = aDebugFlag;
    }


    /**
     * Parses the input via the OQLLexer and returns an ASTNode.
     * 
     * @return an ASTNode representing the full parse tree.
     * 
     * @throws ParserException if a parsing error occurs.
     */
    public AST parse() throws ParserException
    {
        try {
            AST node = parseQueryProgram();
            if (node == null) {
                throw new ParserException("Expected a declaration or a query", mLexer.lookAhead().getTextPosition(), null);
            }
           
            if (!mLexer.isNextToken(OQLTokens.TOK_EOF)) {
                throw new ParserException("Extra text following end of query", mLexer.lookAhead().getTextPosition(), null);
            }

            return node;
        }
        catch (ParserException e) {
            // Gobble until ; (error recovery)
            debug("Error encountered - Gobbling...");
            TokenType token;
            while ((token = mLexer.getNextToken().getTokenType()) != OQLTokens.TOK_SEMI && token != OQLTokens.TOK_EOF) {
            }
            
            throw e;
        }
    }
    

    /**
     * Create a parser exception based on the offending token.
     * 
     * @param aMsg a message passed to the ParserException constructor.
     */
    private ParserException createParserException(String aMsg)
    {
        return createParserException(aMsg, null);
    }


    /**
     * Create a parser exception based on the offending token.
     * 
     * @param aMsg a message passed to the ParserException constructor.
     * @param aCause a Exception that caused this one. Passed to the
     *        ParserException constructor. May be null.
     */
    private ParserException createParserException(String aMsg, Exception aCause)
    {
        try {
            Token token = mLexer.lookAhead();
            return new ParserException(aMsg, token.getTextPosition(), aCause);
        }
        catch (ParserException e) {
            return e; // Not really the one we wanted to create, but oh well...
        }
    }


    /**
     * Expects anAST to be non-null. If so, anAST is returned. Otherwise,
     * ParserException is thrown with the given error message.
     * 
     * @param anAST an AST to verify.
     * @param aMsg a message for the exception.
     * 
     * @throws ParserException if anAST is null.
     */
    private AST expect(AST anAST, String aMsg) throws ParserException
    {
        if (anAST == null) {
            throw createParserException(aMsg);
        }

        return anAST;
    }


    /**
     * Expects aTokenType to be the next token in the stream. If so, the token
     * is removed off the stream. Otherwise, ParserException is thrown with the
     * given error message.
     * 
     * @param aTokenType the token type.
     * @param aMsg a message for the exception.
     * 
     * @return the token that was expected.
     * 
     * @throws ParserException if aTokenType is not found.
     */
    private Token expect(TokenType aTokenType, String aMsg) throws ParserException
    {
        if (!mLexer.isNextToken(aTokenType)) {
            throw createParserException(aMsg);
        }

        return mLexer.getNextToken();
    }
    
    

    /**
     * Expects aKeyword to be the next token in the stream. If so, the token
     * is removed off the stream. Otherwise, ParserException is thrown with the
     * given error message.
     * 
     * @param aKeyword the keyword.
     * @param aMsg a message for the exception.
     * 
     * @return the token that was expected.
     * 
     * @throws ParserException if aTokenType is not found.
     */
    private Token expect(String aKeyword, String aMsg) throws ParserException
    {
        if (!mLexer.isNextTokenKeyword(aKeyword)) {
            throw createParserException(aMsg);
        }

        return mLexer.getNextToken();
    }


    /**
     * Trys parsing the rule contained in the RuleParser functor.
     * 
     * @param aRuleParser the RuleParser functor.
     * 
     * @return an AST or null if the rule didn't match.
     */
    private AST tryRule(RuleParser aRuleParser)
    {
        AST result = null;
        try {
            mLexer.mark();
            result = aRuleParser.parse();
        }
        catch (ParserException e) {
            // Ignore - treat result as null
        }
        finally {
            if (result == null) {
                mLexer.resetToMark();
            }
            else {
                mLexer.clearMark();
            }
        }
        
        return result;
    }


    // Rules...



    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * queryProgram: <br>( declaration | query ) ( TOK_SEMI ( declaration | query ) )* <br>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseQueryProgram() throws ParserException
    {
        List queries = new ArrayList(10);
        while (true) {
            AST node = parseDeclaration();
            if (node != null) {
                queries.add(node);
            }
            else {
                node = parseQuery();
                if (node == null) {
                    break;
                }

                queries.add(node);
            }


            if (!mLexer.isNextToken(OQLTokens.TOK_SEMI)) {
                break;
            }

            mLexer.getNextToken(); // TOK_SEMI
        }

        if (queries.isEmpty()) {
            return null;
        }

        return new QueryProgramAST(queries);
    }

    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * declaration: <br>
     * import <br>| defineQuery <br>| undefineQuery <br>
     * 
     *
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseDeclaration() throws ParserException
    {
        AST node = parseImport();

        if (node == null) {
            node = parseDefineQuery();
        }

        if (node == null) {
            node = parseUndefineQuery();
        }
        
        if (node == null) {
            return null;
        }

        return node;
    }

    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p/>
     *
     * import: "import" qualifiedName ("as" Identifier)? <p/>
     * 
     * Note: "as" Indentifier is not allowed if qualified name ends with ".*".<p/>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseImport() throws ParserException
    {
        if (!mLexer.isNextTokenKeyword("import")) {
            return null;
        }

        mLexer.getNextToken(); // IMPORT

        QualifiedNameAST qualifiedName = (QualifiedNameAST)expect(parseQualifiedName(), "Expected qualified name");
        String alias = null;

        if (mLexer.isNextTokenKeyword("as")) {
            mLexer.getNextToken(); // AS
            Token ident = expect(OQLTokens.TOK_IDENT, "Expected identifier");
            alias = ident.getValue();
            if (qualifiedName.getQualifiedName().endsWith(".*")) {
            	throw createParserException("Alias not allowed with wildcard qualified name.");
            }
        }

        return new ImportAST(qualifiedName, alias);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * defineQuery: "define" ("persistent")? ("query")? Identifier ( TOK_LPAREN ( parameterList )?
     * TOK_RPAREN )? "as" query <br>
     * 
     *
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseDefineQuery() throws ParserException
    {
        if (!mLexer.isNextTokenKeyword("define")) {
            return null;
        }

        mLexer.getNextToken(); // DEFINE
        
        boolean isPersistent = false;
        if (mLexer.isNextTokenKeyword("persistent")) {
            mLexer.getNextToken(); // persistent - optional
            isPersistent = true;
        }

        if (mLexer.isNextTokenKeyword("query")) {
            mLexer.getNextToken(); // QUERY - optional
        }

        String name = expect(OQLTokens.TOK_IDENT, "Expected identifier").getValue();

        ParameterListAST params = null;
        if (mLexer.isNextToken(OQLTokens.TOK_LPAREN)) {
            mLexer.getNextToken(); // '('
            params = parseParameterList();
            expect(OQLTokens.TOK_RPAREN, "Expected ')'");
        }

        expect("as", "Expected \"as\"");

        QueryAST query = (QueryAST)expect(parseQuery(), "Expected query");

        return new DefineQueryAST(name, params, query, isPersistent);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * parameterList: type Identifier ( TOK_COMMA type Identifier )*
     * <p>
     * 
     * @return a ParameterListAST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private ParameterListAST parseParameterList() throws ParserException
    {
        TypeAST type = parseType();
        if (type == null) {
            return null;
        }

        List<TypeAST> types = new ArrayList<TypeAST>(10);
        List<String> names = new ArrayList<String>(10);

        types.add(type);
        names.add( expect(OQLTokens.TOK_IDENT, "Expected identifier").getValue() );

        while (mLexer.isNextToken(OQLTokens.TOK_COMMA)) {
            mLexer.getNextToken(); // TOK_COMMA
            types.add( (TypeAST)expect(parseType(), "Expected type") );
            names.add( expect(OQLTokens.TOK_IDENT, "Expected identifier").getValue() );
        }

        return new ParameterListAST(types, names);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * undefineQuery: "undefine" ("query")? Identifier
     * <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseUndefineQuery() throws ParserException
    {
        if (!mLexer.isNextTokenKeyword("undefine")) {
            return null;
        }

        mLexer.getNextToken(); // UNDEFINE

        if (mLexer.isNextTokenKeyword("query")) {
            mLexer.getNextToken(); // QUERY
        }

        return new UndefineQueryAST( expect(OQLTokens.TOK_IDENT, "Expected Identifier").getValue() );
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * qualifiedName: Identifier ( TOK_DOT Identifier )* (TOK_DOT TOK_ASTERISK)?
     * <p/>
     * 
     *  NOTE: As an extension to OQL: We allow Java-like imports, e.g. "import java.util.*".<p/>
     *
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseQualifiedName() throws ParserException
    {
        if (!mLexer.isNextToken(OQLTokens.TOK_IDENT)) {
            return null;
        }

        List names = new ArrayList(10);
        while (true) {
        	if (mLexer.isNextToken(OQLTokens.TOK_ASTERISK)) {
        		if (mLexer.lookAhead(2).equals(OQLTokens.TOK_DOT)) {
        			throw createParserException("'*' must come at the end of a qualifed name.");
        		}
        		
                mLexer.getNextToken(); // TOK_ASTERISK
                names.add("*");
            }
        	else {
        		names.add( expect(OQLTokens.TOK_IDENT, "Expected identifier").getValue() );
        	}

            if (!mLexer.isNextToken(OQLTokens.TOK_DOT)) {
                break;
            }

            mLexer.getNextToken(); // TOK_DOT
        }

        return new QualifiedNameAST(names);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * query: <br>
     *   selectExpr <br>
     * | expr <p>
     * 
     *
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseQuery() throws ParserException
    {
        AST node = parseSelectExpr(); 
        if (node == null) {
            node = parseExpr();
        }
        
        if (node == null) {
            return null;
        }
        
        return new QueryAST(node);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * selectExpr: <br>
     *  "select" ("distinct")? projectionAttributes <br>
     * | fromClause <br>
     * | (whereClause)? <br>
     * | (groupClause)? <br>
     * | (orderClause)? <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseSelectExpr() throws ParserException
    {
        if ( !mLexer.isNextTokenKeyword("select")) {
            return null;
        }
        
        mLexer.getNextToken(); // SELECT
        
        boolean isDistinct = false;
        if (mLexer.isNextTokenKeyword("distinct")) {
            isDistinct = true;
            mLexer.getNextToken(); // DISTINCT
        }
        
        ProjectionAttributesAST projectionAttributes = 
            (ProjectionAttributesAST)expect(parseProjectionAttributes(), "Expected projection attributes");
        FromClauseAST from = (FromClauseAST)expect(parseFromClause(), "Expected 'from' clause");
        AST where = parseWhereClause();
        AST group = parseGroupClause();
        AST order = parseOrderClause();
        
        return new SelectExprAST(isDistinct, projectionAttributes, from, where, group, order);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * projectionAttributes: projectionList | TOK_ASTERISK <p>
     * 
     * @return a ProjectionAttributesAST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private ProjectionAttributesAST parseProjectionAttributes() throws ParserException
    {
        ProjectionListAST list = parseProjectionList();
        if (list == null) {
            if ( !mLexer.isNextToken(OQLTokens.TOK_ASTERISK)) {
                return null;
            }
            
            mLexer.getNextToken(); // TOK_ASTERISK
        }
        
        return new ProjectionAttributesAST(list);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * projectionList: projection ( TOK_COMMA projection )* <p>
     * 
     * @return a ProjectionListAST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private ProjectionListAST parseProjectionList() throws ParserException
    {
        ProjectionAST projection = parseProjection();
        if (projection == null) {
            return null;
        }

        List<ProjectionAST> projections = new ArrayList(10);
        projections.add(projection);

        while (mLexer.isNextToken(OQLTokens.TOK_COMMA)) {
            mLexer.getNextToken(); // TOK_COMMA
            projections.add( (ProjectionAST)expect(parseProjection(), "Expected projection") );
        }

        return new ProjectionListAST(projections);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * projection: 
     *  field
     * | expr ( "as" Identifier )?<p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private ProjectionAST parseProjection() throws ParserException
    {
        
        FieldAST field = (FieldAST)tryRule( new RuleParser() { public AST parse() throws ParserException { return parseField(); } } );
        AST expr = null;
        String alias = null;
        if (field == null) {
            expr = parseExpr();
            if (expr == null) {
                return null;
            }
            
            if (mLexer.isNextTokenKeyword("as")) {
                mLexer.getNextToken(); // AS
                alias = expect(OQLTokens.TOK_IDENT, "Expected identifier").getValue();
            }
        }
        else {
            // We have a field which is just "identifer : expr". Make them look the same here.
            alias = field.getFieldName();
            expr = field.getExpr();
        }
        
        return new ProjectionAST(expr, alias);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * fromClause: "from" iteratorDef ( TOK_COMMA iteratorDef )* <p>
     * 
     *
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private FromClauseAST parseFromClause() throws ParserException
    {
        if ( !mLexer.isNextTokenKeyword("from")) {
            return null;
        }
        
        mLexer.getNextToken(); // FROM
        
        List iterDefs = new ArrayList(10);
        iterDefs.add( expect(parseIteratorDef(), "Expected iterator definition") );
        
        while (mLexer.isNextToken(OQLTokens.TOK_COMMA)) {
            mLexer.getNextToken(); // TOK_COMMA
            iterDefs.add( expect(parseIteratorDef(), "Expected iterator definition") );
        }
        
        return new FromClauseAST(iterDefs);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * iteratorDef: 
     *   expr ( ( "as" )? Identifier )?
     * | Identifier "in" expr <p>  
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseIteratorDef() throws ParserException
    {
        AST expr;
        String alias = null;
        Token lookAhead2 = mLexer.lookAhead(2);
        if (mLexer.isNextToken(OQLTokens.TOK_IDENT) && lookAhead2.getTokenType() == OQLTokens.TOK_IDENT && 
            lookAhead2.getValue().equals("in")) {
            alias = mLexer.getNextToken().getValue();
            expect("in", "Expected 'in'" );
            expr = expect(parseExpr(), "Expected expression");
        }
        else {
            expr = parseExpr();
            if (expr == null) {
                return null;
            }

            if (mLexer.isNextTokenKeyword("as")) {
                mLexer.getNextToken(); // AS
                alias = expect(OQLTokens.TOK_IDENT, "Expected identifier").getValue();
            }
            else if (mLexer.isNextToken(OQLTokens.TOK_IDENT)) {
                // Could be next keyword. Check.
                String value = mLexer.lookAhead().getValue();
                if (!value.equals("where") && !value.equals("group") && !value.equals("order") && 
                    !value.equals("having")) { 
                    alias = mLexer.getNextToken().getValue();
                }
            }
        }
        
        return new IteratorDefAST(expr, alias);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * whereClause: "where" expr <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseWhereClause() throws ParserException
    {
        if ( !mLexer.isNextTokenKeyword("where")) {
            return null;
        }
        
        mLexer.getNextToken(); // WHERE
        return expect(parseExpr(), "Expected expression");
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * groupClause: "group" "by" fieldList ( havingClause )? <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseGroupClause() throws ParserException
    {
        if ( !mLexer.isNextTokenKeyword("group")) {
            return null;
        }
        
        mLexer.getNextToken(); // GROUP
        expect("by", "Expected 'by'");
        
        AST fieldList = expect(parseFieldList(), "Expected field list");
        AST having = parseHavingClause(); // optional.

        return new GroupClauseAST(fieldList, having);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * havingClause: "having" expr <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseHavingClause() throws ParserException
    {
        if ( !mLexer.isNextTokenKeyword("having")) {
            return null;
        }
        
        mLexer.getNextToken(); // HAVING
        return expect(parseExpr(), "Expected expression");
    }
   

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * orderClause: "order" "by" sortCriteria <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseOrderClause() throws ParserException
    {
        if ( !mLexer.isNextTokenKeyword("order")) {
            return null;
        }
        
        mLexer.getNextToken(); // ORDER
        expect("by", "Expected 'by'");
     
        return expect(parseSortCriteria(), "Expected sort criteriea");
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * sortCriteria: sortCriterion ( TOK_COMMA sortCriterion )* <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseSortCriteria() throws ParserException
    {
        AST criterion = parseSortCriterion();
        if (criterion == null) {
            return null;
        }

        List criteria = new ArrayList(10);
        criteria.add(criterion);

        while (mLexer.isNextToken(OQLTokens.TOK_COMMA)) {
            mLexer.getNextToken(); // TOK_COMMA
            criteria.add( expect(parseSortCriterion(), "Expected sort criterion") );
        }

        return new SortCriteriaAST(criteria);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * sortCriterion: expr ( "asc" | "desc" )? <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseSortCriterion() throws ParserException
    {
        AST expr = parseExpr();
        if (expr == null) {
            return null;
        }
        
        boolean ascending = true;
        if (mLexer.isNextTokenKeyword("asc")) {
            mLexer.getNextToken();
        }
        else if (mLexer.isNextTokenKeyword("desc")) {
            mLexer.getNextToken();
            ascending = false;
        }
        
        return new SortCriterionAST(expr, ascending);
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * type: <br>
     * ("unsigned")? "short" <br>
     * | ("unsigned")? "long" <br>
     * | "long" "long" <br>
     * | "float" <br>
     * | "double" <br>
     * | "char" <br>
     * | "string" <br>
     * | "boolean" <br>
     * | "octet" <br>
     * | "byte" // Extension <br>
     * | "enum" (Identifier TOK_DOT )? Identifier <br>
     * | "date" <br>
     * | "time" <br>
     * | "interval" <br>
     * | "timestamp" <br>
     * | "set" TOK_LT type TOK_GT <br>
     * | "bag" TOK_LT type TOK_GT <br>
     * | "list" TOK_LT type TOK_GT <br>
     * | "array" TOK_LT type TOK_GT <br>
     * | ( "dictionary" | "map" ) TOK_LT type TOK_COMMA type TOK_GT // map is extension <br>
     * | Identifier
     * <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private TypeAST parseType() throws ParserException
    {
        String ident1 = null;
        String ident2 = null;
        TypeAST subType1 = null;
        TypeAST subType2 = null;

        Token token = mLexer.lookAhead();
        if (token.getTokenType() != OQLTokens.TOK_IDENT) {
            return null; // No match.
        }

        String tokenValue = token.getValue();
        TokenType tokenType;
        
        if (tokenValue.equals("unsigned")) {
            mLexer.getNextToken(); // UNSIGNED
            if (mLexer.isNextTokenKeyword("short")) {
                mLexer.getNextToken(); // SHORT
                tokenType = OQLTokens.TOK_UNSIGNED_SHORT;
            }
            else {
                expect("long", "Expected either unsigned short or unsigned long");
                tokenType = OQLTokens.TOK_UNSIGNED_LONG;
            }
        }
        else if (tokenValue.equals("long")) {
            mLexer.getNextToken();
            if (mLexer.isNextTokenKeyword("long")) {
                mLexer.getNextToken();
                tokenType = OQLTokens.TOK_LONG_LONG;
            }
            else {
                tokenType = OQLTokens.TOK_LONG;
            }
        }
        else if (tokenValue.equals("short")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_SHORT;
        }
        else if (tokenValue.equals("float")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_FLOAT;
        }
        else if (tokenValue.equals("double")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_DOUBLE;
        }
        else if (tokenValue.equals("char")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_CHAR;
        }
        else if (tokenValue.equals("string")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_STRING;
        }
        else if (tokenValue.equals("boolean")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_BOOLEAN;
        }
        else if (tokenValue.equals("date")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_DATE;
        }
        else if (tokenValue.equals("time")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_TIME;
        }
        else if (tokenValue.equals("interval")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_INTERVAL;
        }
        else if (tokenValue.equals("timestamp")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_TIMESTAMP;
        }
        else if (tokenValue.equals("octet") || tokenValue.equals("byte")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_OCTET;
        }
        else if (tokenValue.equals("enum")) {
            mLexer.getNextToken(); // enum
            tokenType = OQLTokens.TOK_ENUM;
            ident1 = expect(OQLTokens.TOK_IDENT, "Expected identifier").getValue();
            if (mLexer.isNextToken(OQLTokens.TOK_DOT)) {
                mLexer.getNextToken(); // TOK_DOT
                ident2 = expect(OQLTokens.TOK_IDENT, "Expected identifier").getValue();
            }
        }
        else if (tokenValue.equals("set") ||
                 tokenValue.equals("bag") ||
                 tokenValue.equals("list") ||
                 tokenValue.equals("array")) {

            if (tokenValue.equals("set")) {
                tokenType = OQLTokens.TOK_SET;
            }
            else if (tokenValue.equals("bag")) {
                tokenType = OQLTokens.TOK_BAG;
            }
            else if (tokenValue.equals("list")) {
                tokenType = OQLTokens.TOK_LIST;
            }
            else {
                tokenType = OQLTokens.TOK_ARRAY;
            }

            mLexer.getNextToken();
            expect(OQLTokens.TOK_LT, "Expected '<'");
            subType1 = (TypeAST)expect( parseType(), "Expected type");
            expect(OQLTokens.TOK_GT, "Expected '>'");
        }
        else if (tokenValue.equals("dictionary") || tokenValue.equals("map")) {
            mLexer.getNextToken();
            tokenType = OQLTokens.TOK_DICTIONARY;
            
            expect(OQLTokens.TOK_LT, "Expected '<'");
            subType1 = (TypeAST)expect( parseType(), "Expected type");
            expect(OQLTokens.TOK_COMMA, "Expected ','");
            subType2 = (TypeAST)expect( parseType(), "Expected type");
            expect(OQLTokens.TOK_GT, "Expected '>'");
        }
        else {
            ident1 = expect(OQLTokens.TOK_IDENT, "Expected type").getValue();
            tokenType = OQLTokens.TOK_IDENT;
        }
        
        return new TypeAST(tokenType, ident1, ident2, subType1, subType2);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * expr: castExpr <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseExpr() throws ParserException
    {
        return parseCastExpr();
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * castExpr: 
     *   TOK_LPAREN type TOK_RPAREN castExpr
     * | orExpr <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseCastExpr() throws ParserException
    {
        // There's an ambiguity here that may take quite a bit of lookahead to resolve.
        // Basically, a cast to a user-defined type of "(MyClass)expr" can look similar
        // to the parenthesized expression "(identifier)". We resolve this by trying to
        // process casts first, and if that fails, back-up and treat it like an expr.
        AST expr = tryRule( new RuleParser() { public AST parse() throws ParserException { return parseSoleCastExpr(); } } );

        if (expr == null) {
            expr = parseOrExpr();
        }
        
        return expr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * soleCastExpr: 
     *   TOK_LPAREN type TOK_RPAREN castExpr
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseSoleCastExpr() throws ParserException
    {
        AST type = null;
        AST expr = null;
        if (mLexer.isNextToken(OQLTokens.TOK_LPAREN)) {
            mLexer.getNextToken(); // TOK_LPAREN
            type = expect(parseType(), "Expected type");
            expect(OQLTokens.TOK_RPAREN, "Expected ')'");
            expr = parseCastExpr();
            return new CastExprAST((TypeAST)type, expr);
        }
        
        return null;
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * orExpr: orelseExpr ( "or" orelseExpr )* <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseOrExpr() throws ParserException
    {
        AST leftExpr = parseOrelseExpr();
        if (leftExpr == null) {
            return null;
        }
        
        while (mLexer.isNextTokenKeyword("or")) {
            mLexer.getNextToken(); // OR
            AST rightExpr = expect( parseOrelseExpr(), "Expected an expression.");
            leftExpr = new BinOpExprAST(leftExpr, rightExpr, OQLTokens.TOK_OR);
        }
        
        
        return leftExpr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * orelseExpr: andExpr ( "orelse" andExpr)* <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseOrelseExpr() throws ParserException
    {
        AST leftExpr = parseAndExpr();
        if (leftExpr == null) {
            return null;
        }
        
        while (mLexer.isNextTokenKeyword("orelse")) {
            mLexer.getNextToken(); // ORELSE
            AST rightExpr = expect( parseAndExpr(), "Expected an expression.");
            leftExpr = new BinOpExprAST(leftExpr, rightExpr, OQLTokens.TOK_ORELSE);
        }
        
        
        return leftExpr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * andExpr: quantifierExpr ( "and" quantifierExpr )*<p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseAndExpr() throws ParserException
    {
        AST leftExpr = parseQuantifierExpr();
        if (leftExpr == null) {
            return null;
        }
        
        while (mLexer.isNextTokenKeyword("and")) {
            mLexer.getNextToken(); // AND
            AST rightExpr = expect( parseQuantifierExpr(), "Expected an expression.");
            leftExpr = new BinOpExprAST(leftExpr, rightExpr, OQLTokens.TOK_AND);
        }
        
        
        return leftExpr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * quantifierExpr: 
     *   andthenExpr <p>
     * | "for" "all" inClause TOK_COLON andthenExpr
     * | "exists" inClause TOK_COLON andthenExpr
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseQuantifierExpr() throws ParserException
    {
        boolean isUniversal;
        if (mLexer.isNextTokenKeyword("for")) {
            mLexer.getNextToken(); // FOR
            expect("all", "Expected 'all'");
            isUniversal = true;
        }
        else if (mLexer.isNextTokenKeyword("exists") && !mLexer.lookAhead(2).isTokenType(OQLTokens.TOK_LPAREN)) {
            mLexer.getNextToken(); // EXISTS
            isUniversal = false;
        }
        else {
            // Note ambiguity exists between exists() and exists ident in expr...
            // This is handled above and in parseCollectionExpr() to look two tokens ahead and return null if no match.
            return parseAndthenExpr();  
        }
        
        AST inClause = expect( parseInClause(), "Expected 'in' clause");
        expect(OQLTokens.TOK_COLON, "Expected ':'");
        AST expr = expect( parseAndthenExpr(), "Expected expression");
        return new QuantifierExprAST(inClause, expr, isUniversal);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * inClause: Identifier "in" expr <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseInClause() throws ParserException
    {
        if ( !mLexer.isNextToken(OQLTokens.TOK_IDENT)) {
            return null;
        }
        
        String ident = mLexer.getNextToken().getValue();
        expect("in", "Expected 'in'");
        AST expr = expect( parseExpr(), "Expected expression");
        
        return new InClauseAST(ident, expr);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * andthenExpr: equalityExpr ( "andthen" equalityExpr )* <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseAndthenExpr() throws ParserException
    {
        AST leftExpr = parseEqualityExpr();
        if (leftExpr == null) {
            return null;
        }
        
        while (mLexer.isNextTokenKeyword("andthen")) {
            mLexer.getNextToken(); // ANDTHEN
            AST rightExpr = expect( parseEqualityExpr(), "Expected an expression.");
            leftExpr = new BinOpExprAST(leftExpr, rightExpr, OQLTokens.TOK_ANDTHEN);
        }
        
        
        return leftExpr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * equalityExpr: 
     *   realtionalExpr ( ( TOK_EQ | TOK_NE ) (compositePredicate)? relationalExpr )+
     * | realtionalExpr ( "like" relationalExpr )*<p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseEqualityExpr() throws ParserException
    {
        AST leftExpr = parseRelationalExpr();
        if (leftExpr == null) {
            return null;
        }
        
        while (mLexer.isNextTokenKeyword("like") || mLexer.isNextToken(OQLTokens.TOK_EQ) || mLexer.isNextToken(OQLTokens.TOK_NE)) {
            TokenType type = mLexer.getNextToken().getTokenType(); // LIKE, EQ, NE
            TokenType compositePred = null;
            if (type == OQLTokens.TOK_EQ || type == OQLTokens.TOK_NE) {
                compositePred = parseCompositePredicate();
            }
            else {
                type = OQLTokens.TOK_LIKE;
            }
            
            AST rightExpr = expect( parseRelationalExpr(), "Expected an expression.");
            leftExpr = new RelationalExprAST(leftExpr, rightExpr, compositePred, type);
        }
        
        return leftExpr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * relationalExpr: <br>
     *   additiveExpr ( ( TOK_LT | TOK_LE | TOK_GT | TOK_GE ) (compositePredicate)? additiveExpr )* <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseRelationalExpr() throws ParserException
    {
        AST leftExpr = parseAdditiveExpr();
        if (leftExpr == null) {
            return null;
        }
        
        while (mLexer.isNextToken(OQLTokens.TOK_LT) || mLexer.isNextToken(OQLTokens.TOK_LE) || 
               mLexer.isNextToken(OQLTokens.TOK_GT) || mLexer.isNextToken(OQLTokens.TOK_GE)) {
            TokenType type = mLexer.getNextToken().getTokenType(); // TOK_LT, LE, GT, GE
            TokenType compositePred = parseCompositePredicate();
            
            AST rightExpr = expect( parseAdditiveExpr(), "Expected an expression.");
            leftExpr = new RelationalExprAST(leftExpr, rightExpr, compositePred, type);
        }
        
        return leftExpr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns a TokenType. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * compositePredicate: ( "some" | "any" | "all" ) <p>
     * 
     * @return a TokenType, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private TokenType parseCompositePredicate() throws ParserException
    {
        if (mLexer.isNextTokenKeyword("some")) {
            mLexer.getNextToken();
            return OQLTokens.TOK_SOME;
        }
        
        if (mLexer.isNextTokenKeyword("any")) {
            mLexer.getNextToken();
            return OQLTokens.TOK_ANY;
        }

        if (mLexer.isNextTokenKeyword("all")) {
            mLexer.getNextToken();
            return OQLTokens.TOK_ALL;
        }
        
        return null;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * additiveExpr: 
     *   multiplicativeExpr
     * | multiplicativeExpr ( TOK_PLUS multiplicativeExpr )+
     * | multiplicativeExpr ( TOK_MINUS multiplicativeExpr )+
     * | multiplicativeExpr ( "union" multiplicativeExpr )+
     * | multiplicativeExpr ( "except" multiplicativeExpr )+
     * | multiplicativeExpr ( TOK_CONCAT multiplicativeExpr )+ <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseAdditiveExpr() throws ParserException
    {
        AST leftExpr = parseMultiplicativeExpr();
        if (leftExpr == null) {
            return null;
        }
        
        while (mLexer.isNextToken(OQLTokens.TOK_PLUS) || mLexer.isNextToken(OQLTokens.TOK_MINUS) || 
               mLexer.isNextTokenKeyword("union") || mLexer.isNextTokenKeyword("except") ||
               mLexer.isNextToken(OQLTokens.TOK_CONCAT)) {
            Token token = mLexer.getNextToken();
            TokenType type = token.getTokenType();
            if (type == OQLTokens.TOK_IDENT) {
                type = (token.getValue().equals("union") ? OQLTokens.TOK_UNION : OQLTokens.TOK_EXCEPT);
            }
            
            AST rightExpr = expect( parseMultiplicativeExpr(), "Expected an expression.");
            leftExpr = new BinOpExprAST(leftExpr, rightExpr, type);
        }
        
        return leftExpr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * multiplicativeExpr: 
     *   inExpr
     * | inExpr ( TOK_ASTERISK inExpr )+
     * | inExpr ( TOK_DIV inExpr )+
     * | inExpr ( "mod" inExpr )+
     * | inExpr ( "intersect" inExpr )+ <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseMultiplicativeExpr() throws ParserException
    {
        AST leftExpr = parseInExpr();
        if (leftExpr == null) {
            return null;
        }
        
        while (mLexer.isNextToken(OQLTokens.TOK_ASTERISK) || mLexer.isNextToken(OQLTokens.TOK_DIV) || 
               mLexer.isNextTokenKeyword("mod") || mLexer.isNextTokenKeyword("intersect")) {
            Token token = mLexer.getNextToken();
            TokenType type = token.getTokenType();
            if (type == OQLTokens.TOK_IDENT) {
                type = (token.getValue().equals("mod") ? OQLTokens.TOK_MOD : OQLTokens.TOK_INTERSECT);
            }
            
            AST rightExpr = expect( parseInExpr(), "Expected an expression.");
            leftExpr = new BinOpExprAST(leftExpr, rightExpr, type);
        }
        
        return leftExpr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * inExpr: unaryExpr ( "in" unaryExpr )* <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseInExpr() throws ParserException
    {
        AST leftExpr = parseUnaryExpr();
        if (leftExpr == null) {
            return null;
        }
        
        while (mLexer.isNextTokenKeyword("in")) {
            mLexer.getNextToken(); // IN
            AST rightExpr = expect( parseUnaryExpr(), "Expected an expression.");
            leftExpr = new InExprAST(leftExpr, rightExpr);
        }
        
        
        return leftExpr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * unaryExpr: 
     *   TOK_PLUS unaryExpr
     * | TOK_MINUS unaryExpr
     * | "abs" unaryExpr
     * | "not" unaryExpr
     * | postfixExpr <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseUnaryExpr() throws ParserException
    {
        if (mLexer.isNextToken(OQLTokens.TOK_PLUS) ||
            mLexer.isNextToken(OQLTokens.TOK_MINUS) ||
            mLexer.isNextTokenKeyword("abs") ||
            mLexer.isNextTokenKeyword("not")) {

            Token token = mLexer.getNextToken();
            TokenType type = token.getTokenType();
            if (type == OQLTokens.TOK_IDENT) {
                type = (token.getValue().equals("abs") ? OQLTokens.TOK_ABS : OQLTokens.TOK_NOT);
            }

            AST expr = expect(parseUnaryExpr(), "Expected expression");

            return new UnaryExprAST(expr, type); 
        }
        
        return parsePostfixExpr();
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * postfixExpr:
     *  primaryExpr ( ( TOK_LBRACK index TOK_RBRACK ) | (( TOK_DOT | TOK_INDIRECT ) Identifier (argList)?) )* <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parsePostfixExpr() throws ParserException
    {
        AST expr = parsePrimaryExpr();
        if (expr == null) {
            return null;
        }
        
        
        for (TokenType la = mLexer.lookAhead().getTokenType(); 
             la == OQLTokens.TOK_LBRACK || la == OQLTokens.TOK_DOT || la == OQLTokens.TOK_INDIRECT;
             la = mLexer.lookAhead().getTokenType()) {

            mLexer.getNextToken(); // pop the lookahead token.
            if (la == OQLTokens.TOK_LBRACK) {
                AST index = expect( parseIndex(), "Expected index");
                if (index instanceof IndexListAST) {
                    throw createParserException("List index must contain exactly one index");
                }
                
                expect(OQLTokens.TOK_RBRACK, "Expected ']'");
                expr = new IndexedExprAST(expr, index);
            }
            else {
                // Method call
                String methodName = expect(OQLTokens.TOK_IDENT, "Expected identifier").getValue();
                ValueListAST argList = parseArgList();
                expr = new MethodCallExprAST(expr, methodName, argList);
            }
        }
        
        return expr; 
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * index:
     *   expr ( TOK_COMMA expr )*
     * | expr TOK_COLON expr <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseIndex() throws ParserException
    {
        AST expr = parseExpr();
        if (expr == null) {
            return null;
        }

        
        if (mLexer.isNextToken(OQLTokens.TOK_COMMA)) {
            List indicies = new ArrayList(2);
            indicies.add(expr);
            while (mLexer.isNextToken(OQLTokens.TOK_COMMA)) {
                mLexer.getNextToken(); // TOK_COMMA
                AST index = expect( parseExpr(), "Expected expression");
                indicies.add(index);
            }
            
            return new IndexListAST(indicies);
        }
        else if (mLexer.isNextToken(OQLTokens.TOK_COLON)) {
            mLexer.getNextToken(); // TOK_COLON
            AST expr2 = expect( parseExpr(), "Expected expression");
            return new IndexRangeAST(expr, expr2);
        }

        return expr;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * argList: TOK_LPAREN (valueList)? TOK_RPAREN <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private ValueListAST parseArgList() throws ParserException
    {
        if ( !mLexer.isNextToken(OQLTokens.TOK_LPAREN)) {
            return null;
        }
        
        mLexer.getNextToken(); // TOK_LPAREN
        ValueListAST valueList = parseValueList();
        // "()" is a valid empty list.
        if (valueList == null) {
            valueList = new ValueListAST(Collections.EMPTY_LIST);
        }
        
        expect(OQLTokens.TOK_RPAREN, "Expected ')'");
        
        return valueList;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * primaryExpr:
     *   conversionExpr
     * | collectionExpr
     * | aggregateExpr
     * | undefinedExpr
     * | (TOK_NEW)? objectConstruction
     * | structConstruction
     * | collectionConstruction
     * | queryParam
     * | literal
     * | (TOK_NEW)? Identifier (argList)?
     * | TOK_LPAREN query TOK_RPAREN <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parsePrimaryExpr() throws ParserException
    {
        AST expr;

        if ((expr = parseConversionExpr()) != null ||
            (expr = parseCollectionExpr()) != null ||
            (expr = parseAggregateExpr()) != null ||
            (expr = parseUndefinedExpr()) != null ||
            (expr = parseStructConstruction()) != null ||
            (expr = parseCollectionConstruction()) != null ||
            (expr = parseQueryParam()) != null ||
            (expr = parseLiteral()) != null) {
            return expr;
        }

        boolean isObjectConstruction = false;
        // Do we have "new ident("?
        if (mLexer.isNextTokenKeyword("new") && 
            mLexer.lookAhead(2).isTokenType(OQLTokens.TOK_IDENT) &&
            mLexer.lookAhead(3).isTokenType(OQLTokens.TOK_LPAREN)) {
            mLexer.getNextToken(); // TOK_NEW
            isObjectConstruction = true;
        }
        
        // There's some ambiguity between a defined query call and object construction with a FieldList.
        // Try the object construction rule first. If that fails, check for a defined query.
        expr = tryRule( new RuleParser() { public AST parse() throws ParserException { return parseObjectConstructionWithFieldList(); } } );
        if (expr != null) {
            return expr;
        }

        // It's defined query call or object constructor. Could also just be an identifier.
        if (mLexer.isNextToken(OQLTokens.TOK_IDENT)) {
            String ident = mLexer.getNextToken().getValue(); // TOK_IDENT
            ValueListAST argList = parseArgList();
            if (argList == null) {
                return new IdentifierExprAST(ident);
            }

            if (isObjectConstruction) {
                return new IdentifierWithArgumentsAST(ident, argList, isObjectConstruction);
            }

            // We're not sure if it's a constructor or defined query call.
            return new IdentifierWithArgumentsAST(ident, argList);
        }
 
        return parseParenthesizedQuery(); // May return null which indicates our rule didn't match.
    }


    /**
     * Parse a parenthesized query.<p>
     * 
     * parenQuery: '(' query ')' <p>
     * 
     * @return an AST of the query, or null if the rule was not matched. 
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseParenthesizedQuery() throws ParserException
    {
        if (mLexer.isNextToken(OQLTokens.TOK_LPAREN)) {
            mLexer.getNextToken(); // TOK_LPAREN
            AST query = expect( parseQuery(), "Expected a query or expression");
            expect(OQLTokens.TOK_RPAREN, "Expected ')'");
            return query;
        }

        // Nothing matched.
        return null;
    }
    

    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * conversionExpr:
     *   "listtoset" TOK_LPAREN query TOK_RPAREN
     * | "element" TOK_LPAREN query TOK_RPAREN
     * | "distinct" TOK_LPAREN query TOK_RPAREN
     * | "flatten" TOK_LPAREN query TOK_RPAREN <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseConversionExpr() throws ParserException
    {
        Token token = mLexer.lookAhead();
        String value = token.getValue();
        if (token.getTokenType() == OQLTokens.TOK_IDENT) {
            TokenType type = null;
            if (value.equals("listtoset")) {
                type = OQLTokens.TOK_LISTTOSET; 
            }
            else if (value.equals("element")) {
                type = OQLTokens.TOK_ELEMENT; 
            }
            else if (value.equals("distinct")) {
                type = OQLTokens.TOK_DISTINCT; 
            }
            else if (value.equals("flatten")) {
                type = OQLTokens.TOK_FLATTEN; 
            }

            if (type != null) {
                mLexer.getNextToken();
                AST query = expect( parseParenthesizedQuery(), "Expected '('");
                return new ConversionExprAST(type, query);
            }
        }
        
        // No match.
        return null;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * collectionExpr:
     *   "first" TOK_LPAREN query TOK_RPAREN
     * | "last" TOK_LPAREN query TOK_RPAREN
     * | "unique" TOK_LPAREN query TOK_RPAREN
     * | "exists" TOK_LPAREN query TOK_RPAREN <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseCollectionExpr() throws ParserException
    {
        Token token = mLexer.lookAhead();
        String value = token.getValue();
        // Testing for '(' here and returning null if it isn't eliminates ambiguity between
        // exists(...) and exist ident in expr...
        if (token.getTokenType() == OQLTokens.TOK_IDENT && mLexer.lookAhead(2).getTokenType() == OQLTokens.TOK_LPAREN) {
            TokenType type = null;
            if (value.equals("first")) {
                type = OQLTokens.TOK_FIRST; 
            }
            else if (value.equals("last")) {
                type = OQLTokens.TOK_LAST; 
            }
            else if (value.equals("unique")) {
                type = OQLTokens.TOK_UNIQUE; 
            }
            else if (value.equals("exists")) {
                type = OQLTokens.TOK_EXISTS; 
            }

            if (type != null) {
                mLexer.getNextToken();
                AST query = expect( parseParenthesizedQuery(), "Expected '('");
                return new CollectionExprAST(type, query);
            }
        }
        
        // No match.
        return null;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * aggregateExpr:
     *   "sum" TOK_LPAREN query TOK_RPAREN
     * | "min" TOK_LPAREN query TOK_RPAREN
     * | "max" TOK_LPAREN query TOK_RPAREN
     * | "avg" TOK_LPAREN query TOK_RPAREN
     * | "count" TOK_LPAREN ( query | TOK_ASTERISK ) TOK_RPAREN <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseAggregateExpr() throws ParserException
    {
        Token token = mLexer.lookAhead();
        String value = token.getValue();
        if (token.getTokenType() == OQLTokens.TOK_IDENT) {
            TokenType type = null;
            if (value.equals("sum")) {
                type = OQLTokens.TOK_SUM; 
            }
            else if (value.equals("min")) {
                type = OQLTokens.TOK_MIN; 
            }
            else if (value.equals("max")) {
                type = OQLTokens.TOK_MAX; 
            }
            else if (value.equals("avg")) {
                type = OQLTokens.TOK_AVG; 
            }

            if (type != null) {
                mLexer.getNextToken();
                AST query = expect( parseParenthesizedQuery(), "Expected '('");
                return new AggregateExprAST(type, query);
            }
            else if (value.equals("count")) {
                type = OQLTokens.TOK_COUNT;
                mLexer.getNextToken();
                expect(OQLTokens.TOK_LPAREN, "Expected '('");
                AST query = null;
                if (mLexer.isNextToken(OQLTokens.TOK_ASTERISK)) {
                    mLexer.getNextToken();
                }
                else {
                    query = expect( parseQuery(), "Expected expression");
                }
                
                expect(OQLTokens.TOK_RPAREN, "Expected ')'");
                return new AggregateExprAST(type, query);
            }
        }
        
        // No match.
        return null;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * undefinedExpr:
     *   TOK_UNDEFINED TOK_LPAREN query TOK_RPAREN
     * | TOK_DEFINED  TOK_LPAREN query TOK_RPAREN <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseUndefinedExpr() throws ParserException
    {
        Token token = mLexer.lookAhead();
        String value = token.getValue();
        if (token.getTokenType() == OQLTokens.TOK_IDENT) {
            TokenType type = null;
            if (value.equals("is_undefined")) {
                type = OQLTokens.TOK_IS_UNDEFINED; 
            }
            else if (value.equals("is_defined")) {
                type = OQLTokens.TOK_IS_DEFINED; 
            }

            if (type != null) {
                mLexer.getNextToken();
                AST query = expect( parseParenthesizedQuery(), "Expected '('");
                return new UndefinedExprAST(type, query);
            }
        }
        
        // No match.
        return null;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * objectConstruction: Identifier TOK_LPAREN fieldList TOK_RPAREN <p> 
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseObjectConstructionWithFieldList() throws ParserException
    {
        if ( !mLexer.isNextToken(OQLTokens.TOK_IDENT)) {
            return null;
        }
        
        String className = mLexer.getNextToken().getValue();
        expect(OQLTokens.TOK_LPAREN, "Expected '('");
        FieldListAST fieldList = (FieldListAST)expect( parseFieldList(), "Expected field list");
        expect(OQLTokens.TOK_RPAREN, "Expected ')'");
        return new IdentifierWithArgumentsAST(className, fieldList);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * structConstruction: "struct" TOK_LPAREN fieldList TOK_RPAREN <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseStructConstruction() throws ParserException
    {
        if ( !mLexer.isNextTokenKeyword("struct")) {
            return null;
        }
        
        mLexer.getNextToken(); // STRUCT
        expect(OQLTokens.TOK_LPAREN, "Expected '('");
        AST fieldList = expect( parseFieldList(), "Expected field list");
        expect(OQLTokens.TOK_RPAREN, "Expected ')'");
        return new StructConstructionAST(fieldList);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * fieldList: field ( TOK_COMMA field )* <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseFieldList() throws ParserException
    {
        AST field = parseField();
        if (field == null) {
            return null;
        }
        
        List fields = new ArrayList(20);
        fields.add(field);

        while (mLexer.isNextToken(OQLTokens.TOK_COMMA)) {
            mLexer.getNextToken(); // TOK_COMMA
            field = expect( parseField(), "Expected field");
            fields.add(field);
        }
        
        return new FieldListAST(fields);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * field: Identifier TOK_COLON expr <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseField() throws ParserException
    {
        if ( !mLexer.isNextToken(OQLTokens.TOK_IDENT)) {
            return null;
        }

        String fieldName = mLexer.getNextToken().getValue(); // TOK_IDENT
        expect(OQLTokens.TOK_COLON, "Expected ':'");
        AST expr = expect( parseExpr(), "Expected expression");
        
        return new FieldAST(fieldName, expr);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * collectionConstruction:
     *   "array" TOK_LPAREN (valueList)? TOK_RPAREN 
     * | "set" TOK_LPAREN (valueList)? TOK_RPAREN 
     * | "bag" TOK_LPAREN (valueList)? TOK_RPAREN 
     * | "list" TOK_LPAREN (expr ( 
     *      (TOK_DOTDOT expr) | // List Range
     *      (TOK_COMMA expr)* )? TOK_RPAREN 
     * | "list" TOK_LPAREN listRange TOK_RPAREN  <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseCollectionConstruction() throws ParserException
    {
        Token token = mLexer.lookAhead();
        if (token.getTokenType() == OQLTokens.TOK_IDENT) {
            String value = token.getValue();
            TokenType type = null;
            if (value.equals("array")) {
                type = OQLTokens.TOK_ARRAY;
            }
            else if (value.equals("set")) {
                type = OQLTokens.TOK_SET;
            }
            else if (value.equals("bag")) {
                type = OQLTokens.TOK_BAG;
            }
            else if (value.equals("list")) {
                type = OQLTokens.TOK_LIST;
            }

            if (type != null) {
                mLexer.getNextToken(); // Collection type token.
                expect(OQLTokens.TOK_LPAREN, "Expected '('");
                AST values = null;
                if (type == OQLTokens.TOK_LIST) {
                    // listRange sub-rule - some ambiguity.
                    values = tryRule( new RuleParser() { public AST parse() throws ParserException { return parseListRange(); } } );
                }
    
                if (values == null) {
                    values = parseValueList();
                }
                
                expect(OQLTokens.TOK_RPAREN, "Expected ')'");
    
                return new CollectionConstructionAST(type, values);
            }
        }

        return null;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * valueList: expr ( TOK_COMMA expr )* <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private ValueListAST parseValueList() throws ParserException
    {
        AST expr = parseExpr();
        if (expr == null) {
            return null;
        }
        
        List values = new ArrayList(30);
        values.add(expr);
        
        while (mLexer.isNextToken(OQLTokens.TOK_COMMA)) {
            mLexer.getNextToken(); // TOK_COMMA
            expr = expect( parseExpr(), "Expected expression");
            values.add(expr);
        }
        
        return new ValueListAST(values);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * listRange: expr ".." expr <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseListRange() throws ParserException
    {
        AST startExpr = parseExpr();
        if (startExpr == null) {
            return null;
        }
        
        expect(OQLTokens.TOK_DOTDOT, "Expected '..'");
        AST endExpr = expect( parseExpr(), "Expected expression");
        
        return new ListRangeAST(startExpr, endExpr);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * queryParam: TOK_DOLLAR longLiteral <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseQueryParam() throws ParserException
    {
        if ( !mLexer.isNextToken(OQLTokens.TOK_DOLLAR)) {
            return null;
        }
        
        mLexer.getNextToken(); // TOK_DOLLAR
        AST index = expect( parseLongLiteral(), "Expected parameter number");
        return new QueryParamAST(index);
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * literal:
     *   booleanLiteral
     * | longLiteral
     * | doubleLiteral
     * | dateLiteral
     * | timeLiteral
     * | timestampLiteral
     * | CharLiteral
     * | StringLiteral
     * | "nil" 
     * | "null"  // Extension
     * | "undefined" <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseLiteral() throws ParserException
    {
        AST literal;
        if ((literal = parseBooleanLiteral()) != null) {
            return literal;
        }
        
        if ((literal = parseLongLiteral()) != null) {
            return literal;
        }
        
        if ((literal = parseExactNumLiteral()) != null) {
            return literal;
        }
        
        if ((literal = parseDoubleLiteral()) != null) {
            return literal;
        }

        if ((literal = parseDateLiteral()) != null) {
            return literal;
        }

        if ((literal = parseTimeLiteral()) != null) {
            return literal;
        }

        if ((literal = parseTimestampLiteral()) != null) {
            return literal;
        }

        TokenType la = mLexer.lookAhead().getTokenType();
        if (la == OQLTokens.TOK_CHAR_LITERAL) {
            return new CharLiteralAST( mLexer.getNextToken().getValue().charAt(0) );
        }
        
        if (la == OQLTokens.TOK_STRING_LITERAL) {
            return new StringLiteralAST( mLexer.getNextToken().getValue() );
        }
        
        // NOTE: Treat 'undefined' like null.
        if (mLexer.isNextTokenKeyword("nil") ||
            mLexer.isNextTokenKeyword("null") ||
            mLexer.isNextTokenKeyword("undefined")) {
            mLexer.getNextToken();
            return new NilLiteralAST();
        }
        
        return null;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * booleanLiteral: "true" | "false" <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseBooleanLiteral() throws ParserException
    {
        if (mLexer.isNextTokenKeyword("true") || mLexer.isNextTokenKeyword("false")) {
            Token token = mLexer.getNextToken();
            return new BooleanLiteralAST(token.getValue().equals("true"));
        }
        
        return null;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * longLiteral: TOK_UNSIGNED_INTEGER  <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseLongLiteral() throws ParserException
    {
        if ( !mLexer.isNextToken(OQLTokens.TOK_INTEGER_LITERAL)) {
            return null;
        }
        
        Token valueToken = mLexer.getNextToken(); // TOK_INTEGER_LITERAL
        String valueStr = valueToken.getValue();
        try {
            long value = Long.valueOf(valueStr).longValue(); 
            return new LongLiteralAST(value);
        }
        catch (NumberFormatException e) {
            throw new ParserException("Invalid number: " + valueStr, valueToken.getTextPosition(), e);
        }
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * exactNumLiteral: TOK_EXACT_NUMERIC_LITERAL <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseExactNumLiteral() throws ParserException
    {
        if (mLexer.lookAhead().getTokenType() != OQLTokens.TOK_EXACT_NUM_LITERAL) {
            return null;
        }
        
        Token valueToken = mLexer.getNextToken();
        String valueStr = valueToken.getValue();
        try {
            BigDecimal value = new BigDecimal(valueStr);  
            return new BigDecimalLiteralAST(value);
        }
        catch (NumberFormatException e) {
            throw new ParserException("Invalid number: " + valueStr, valueToken.getTextPosition(), e);
        }
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * doubleLiteral: TOK_APPROXIMATE_NUMERIC_LITERAL 
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseDoubleLiteral() throws ParserException
    {
        if (mLexer.lookAhead().getTokenType() != OQLTokens.TOK_APPROX_NUM_LITERAL) {
            return null;
        }
        
        Token valueToken = mLexer.getNextToken();
        String valueStr = valueToken.getValue();
        try {
            double value = Double.valueOf(valueStr).doubleValue(); 
            return new DoubleLiteralAST(value);
        }
        catch (NumberFormatException e) {
            throw new ParserException("Invalid number: " + valueStr, valueToken.getTextPosition(), e);
        }
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * dateLiteral: "date" StringLiteral <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseDateLiteral() throws ParserException
    {
        if (mLexer.isNextToken(OQLTokens.TOK_DATE)) {
            mLexer.getNextToken();
            String value = expect(OQLTokens.TOK_STRING_LITERAL, "Expected date string").getValue();
            return new DateLiteralAST(value);
        }
        
        return null;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * timeLiteral: "time" StringLiteral <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseTimeLiteral() throws ParserException
    {
        if (mLexer.isNextToken(OQLTokens.TOK_TIME)) {
            mLexer.getNextToken();
            String value = expect(OQLTokens.TOK_STRING_LITERAL, "Expected time string").getValue();
            return new TimeLiteralAST(value);
        }
        
        return null;
    }


    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * timestampLiteral: "timestamp" StringLiteral <p>
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    private AST parseTimestampLiteral() throws ParserException
    {
        if (mLexer.isNextToken(OQLTokens.TOK_TIMESTAMP)) {
            mLexer.getNextToken();
            String value = expect(OQLTokens.TOK_STRING_LITERAL, "Expected timestamp string").getValue();
            return new TimestampLiteralAST(value);
        }
        
        return null;
    }



    /**
     * A functor for calling rules. 
     */
    private interface RuleParser
    {
        public AST parse() throws ParserException;
    }
}

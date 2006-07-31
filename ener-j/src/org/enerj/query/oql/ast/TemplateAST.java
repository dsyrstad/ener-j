// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/TemplateAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.query.oql.OQLLexer;
import org.enerj.query.oql.ParserException;


/**
 * The Template AST. <p>
 * 
 * @version $Id: TemplateAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class TemplateAST extends BaseAST
{
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a new TemplateAST. 
     */
    public TemplateAST()
    {
    }
    
    //--------------------------------------------------------------------------------
    /**
     * This method parses the rule from the current point in the lexer and returns an AST. If the
     * rule is not matched, null is returned. If an error occurs while parsing the rule, ParserException
     * is thrown. The rule is:<p>
     *
     * <p>
     * 
     * @param aLexer the OQLLexer to parser from.
     * 
     * @return an AST, or null if the rule is not matched.
     * 
     * @throw ParserException if an error occurs. 
     */
    public static TemplateAST parse(OQLLexer aLexer) throws ParserException
    {
        return null;
    }

    //--------------------------------------------------------------------------------
}

//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/ast/AggregateExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $

package org.enerj.query.oql.ast;

import org.enerj.query.oql.TokenType;


/**
* The AggregateExpr AST. <p>
* 
* @version $Id: AggregateExprAST.java,v 1.3 2005/08/12 02:56:49 dsyrstad Exp $
* @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
*/
public class AggregateExprAST extends BaseAST
{
 private TokenType mOp;
 private AST mExpr;
 
 //--------------------------------------------------------------------------------
 /**
  * Construct a AggregateExprAST. 
  *
  * @param anOp
  * @param anExpr the expression. May be null to count all distinct attributes of TOK_COUNT.
  */
 public AggregateExprAST(TokenType anOp, AST anExpr)
 {
     mOp = anOp;
     mExpr = anExpr;
 }
 
 //--------------------------------------------------------------------------------
 /**
  * Gets the Expr.
  *
  * @return a AST.
  */
 public AST getExpr()
 {
     return mExpr;
 }
 
 //--------------------------------------------------------------------------------
 /**
  * Gets the Op.
  *
  * @return a TokenType.
  */
 public TokenType getOp()
 {
     return mOp;
 }
}

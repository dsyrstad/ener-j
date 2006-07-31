// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/query/oql/OQLParserTest.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $

package org.enerj.query.oql;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.enerj.query.oql.ast.AST;

/**
 * Tests OQLParser. <p>
 *
 *
 * @version $Id: OQLParserTest.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class OQLParserTest extends TestCase
{
    private OQLParser mParser;
    
    //----------------------------------------------------------------------
    public OQLParserTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(OQLParserTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(OQLParserTest.class);
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests Queries.Driven by external file OQLParserTest.properties.
     */
    public void testQueries() throws Exception
    {
        List queries = new ArrayList(300);
        String props = "/" + this.getClass().getPackage().getName().replace('.', '/') + "/OQLParserTest.properties";
        InputStream in = this.getClass().getResourceAsStream(props);
        if (in == null) {
            throw new Exception("Couldn't load " + props);
        }
        
        BufferedReader reader = new BufferedReader( new InputStreamReader(in) );
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(">") || line.startsWith("<")) {
                queries.add( line.substring(1) );
            }
            else if (line.startsWith("!")) {
                queries.add(line);
            }
        }
        
        Iterator iter = queries.iterator();
        for (int i = 1; iter.hasNext(); ++i) {
            String query = (String)iter.next();
            String expectedResult = (String)iter.next();
            
            Reader queryReader = new StringReader(query);
            OQLLexer lexer = new OQLLexer( new TrackedPositionReader("string", queryReader, 8192) );
            lexer.setDebug(false);
            try {
                mParser = new OQLParser(lexer);
                mParser.setDebug(false);
                AST ast = mParser.parse();
                
                if (expectedResult.startsWith("!")) {
                    System.out.println(">" + query);
                    System.out.println("<" + ast.toString() + "\n");
                }
                else {
                    assertEquals("Query #" + i + " didn't match. Query: " + query, expectedResult, ast.toString());
                }
            }
            catch (ParserException e) {
                fail("Failed to parse query: " + query + " Exception: " + e.getMessage());
            }
        }
        
        System.out.println("Parsed " + (queries.size() / 2) + " queries.");
    }
}

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
//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/EnerJQLCmdLine.java,v 1.7 2006/02/21 02:37:47 dsyrstad Exp $

package org.enerj.util;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;

import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.OQLQuery;
import org.odmg.QueryException;
import org.enerj.core.Persistable;
import org.enerj.core.Structure;
import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJImplementation;
import org.enerj.core.EnerJOQLQuery;
import org.enerj.query.oql.EvaluatorContext;



/**
* Ener-J OQL command-line utility. "enerjoql" is pronounced like "vocal".   
* 
* @version $Id: EnerJQLCmdLine.java,v 1.7 2006/02/21 02:37:47 dsyrstad Exp $
* @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
*/
public class EnerJQLCmdLine
{
    private static int sIndentLevel = 0;
    
    //--------------------------------------------------------------------------------
    public static void main(String[] args) throws Exception
    {
        Reader inReader;
        if (args.length == 0) {
            usage();
            System.exit(1);
        }
        
        String dbURI = args[0];
        
        Implementation impl = EnerJImplementation.getInstance(); 
        EnerJDatabase db = (EnerJDatabase)impl.newDatabase();
        db.open(dbURI, Database.OPEN_READ_WRITE);
        db.setAllowNontransactionalReads(true);

        try {
            OQLQuery query;
            if (args.length > 1) {
                query = new EnerJOQLQuery(args[1]);
            }
            else {
                query = new EnerJOQLQuery( new InputStreamReader(System.in) );
            }

            long start = System.currentTimeMillis();
            printResult( query.execute() );
            System.out.println("Time: " + (System.currentTimeMillis() - start) + "ms");
        }
        catch (QueryException e) {
            System.err.println("Query Error: " + e.getMessage() );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            EvaluatorContext.getContext().dispose();
            db.close();
        }
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Prints the result object to System.out. 
     *
     * @param obj the object to be printed.
     */
    private static void printResult(Object obj)
    {
        String indent = "                                                       ".substring(0, sIndentLevel);
        System.out.print(indent);
        ++sIndentLevel;

        if (obj == null) {
            System.out.println("Result is null");
        } 
        else if (obj instanceof Iterator) {
            System.out.println("Result (" + obj.getClass().getName() + "):");
            for (Iterator iter = ((Iterator)obj); iter.hasNext(); ) {
                printResult( iter.next() );
            }
        }
        else if (obj instanceof Collection) {
            System.out.println("Result (" + obj.getClass().getName() + "):");
            for (Object subObj : (Collection)obj) {
                printResult(subObj);
            }
        }
        else if (obj instanceof Structure) {
            System.out.println("Result (struct):");
            Structure struct = (Structure)obj;
            String[] names = struct.getMemberNames();
            Object[] values = struct.getMemberValues();
            for (int i = 0; i < names.length; i++) {
                System.out.print(indent + "struct member " + names[i] + ":");
                printResult(values[i]);
            }
        }
        else {
            if (obj instanceof Persistable) {
                // Ensure that object is loaded because we use reflection.
                EnerJImplementation.getEnerJDatabase(obj).loadObject((Persistable)obj);
            }
            
            System.out.println("Result: (" + obj.getClass().getName() + ')' + StringUtil.toString(obj, false, true) );
        }
        
        --sIndentLevel;
    }

    
    //--------------------------------------------------------------------------------
    private static void usage() 
    {
        System.err.println("Usage: EnerJQLCmdLine database-uri [ OQL-expression ]");
    }
}

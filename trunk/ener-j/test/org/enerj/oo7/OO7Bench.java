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
package org.enerj.oo7;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.odmg.DBag;
import org.odmg.DMap;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.ODMGException;
import org.odmg.Transaction;
import org.enerj.core.Extent;
import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJImplementation;

/**
 * OO7 Benchmark traversals and queries. <p>
 * 
 * @version $Id: OO7Bench.java,v 1.1 2005/11/12 03:44:13 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class OO7Bench
{
    private static final int TINY = 0;
    private static final int SMALL = 1;
    private static final int MEDIUM = 2;
    private static final int LARGE = 3;
    
    private static String sDBURL;

    private Implementation mImpl;
    private Random mRandom;
    private EnerJDatabase mDB;

    private int traversal(AtomicPart a, int op, Set s)
    {
        int count = 0;
        switch (op) {
        case 1:
            count++;
            break;
        case 2:
            if (s.isEmpty()) {
                int x = a.getX();
                a.setX( a.getY() );
                a.setY(x);
                count++;
            }
            break;
        case 3: {
            int x = a.getX();
            a.setX( a.getY() );
            a.setY(x);
            count++;
        }
            break;
        case 4:
            for (int i = 0; i < 4; i++) {
                int x = a.getX();
                a.setX( a.getY() );
                a.setY(x);
                count++;
            }
            break;
        case 5:
            a.setBuildDate( 10000 - a.getBuildDate() );
            count++;
            break;
        case 6:
            for (int i = 0; i < 4; i++) {
                a.setBuildDate( 10000 - a.getBuildDate() );
                count++;
            }
            break;
        case 10:
            count++;
            return count;
        }
        s.add(a);
        for (Iterator iter = a.getTo().iterator(); iter.hasNext();) {
            Connection con = (Connection)iter.next();
            if (!s.contains(con.getTo())) {
                count += traversal(con.getTo(), op, s);
            }
        }
        return count;
    }

    private int traversal(CompositePart p, int op)
    {
        if ((op >= 1) && (op <= 6)) {
            return traversal(p.getRootPart(), op, new HashSet());
        }
        else if (op == 7) {
            return (p.getDocument().getText().indexOf('I') >= 0 ? 1 : 0);
        }
        else if (op == 8) {
            return (p.getDocument().getText().indexOf('I') >= 0 ? 1 : 0); // FIXME
        }
        else if (op == 9) {
            return (p.getDocument().getText().indexOf('I') >= 0 ? 1 : 0); // FIXME
        }
        else {
            return traversal(p.getRootPart(), op, new HashSet());
        }
    }

    private int traversal(BaseAssembly a, int op)
    {
        int count = 0;
        for (Iterator iter = a.getComponentsPrivate().iterator(); iter.hasNext();)
            count += traversal((CompositePart)iter.next(), op);
        return count;
    }

    private int traversal(ComplexAssembly a, int op)
    {
        int count = 0;
        for (Iterator iter = a.getSubAssemblies().iterator(); iter.hasNext();) {
            Assembly b = (Assembly)iter.next();
            if (b instanceof ComplexAssembly)
                count += traversal((ComplexAssembly)b, op);
            else
                count += traversal((BaseAssembly)b, op);
        }
        return count;
    }

    private void traversal(Module mod, int op)
    {
        int count = traversal((ComplexAssembly)mod.getDesignRoot(), op);
        System.out.println("Touched " + count + " object");
    }

    private void traversal(int op) throws ODMGException
    {
        DBag mods = (DBag)mDB.lookup("Modules");
        for (Iterator iter = mods.iterator(); iter.hasNext();)
            traversal((Module)iter.next(), op);
    }

    private void query1() throws ODMGException
    {
        DMap map = (DMap)mDB.lookup("AtomicPartsByID");
        int numParts = map.size();
        int numFound = 0;
        for (int i = 0; i < 10; i++) {
            int id = mRandom.nextInt(numParts) + 1000;
            AtomicPart part = (AtomicPart)map.get(id);
            if (part != null) {
                numFound++;
            }
        }

        System.out.println("Found " + numFound + " (0 is ok here)");
    }

    private void query2() throws ODMGException
    {
        Extent extent = mDB.getExtent(AtomicPart.class, false);
        int numFound = 0;
        int dateLower = 10000 - (int)(10000 * .01);
        for (int Index = 0; Index < 10; Index++) {
            for (Iterator iter = extent.iterator(); iter.hasNext();) {
                AtomicPart current = (AtomicPart)iter.next();
                if (current.getBuildDate() >= dateLower) {
                    ++numFound;
                }
            }
        }
        System.out.println("Found " + numFound);
    }

    private void query3() throws ODMGException
    {
        Extent extent = mDB.getExtent(AtomicPart.class, false);
        int numFound = 0;
        int dateLower = 10000 - (int)(10000 * .10);
        for (int Index = 0; Index < 10; Index++) {
            for (Iterator iter = extent.iterator(); iter.hasNext();) {
                AtomicPart current = (AtomicPart)iter.next();
                if (current.getBuildDate() >= dateLower) {
                    ++numFound;
                }
            }
        }
        System.out.println("Found " + numFound);
    }

    private void queryDB(int startnum, int endnum) throws Exception
    {
        mImpl = EnerJImplementation.getInstance();
        mRandom = new Random();

        for (int step = startnum; step <= endnum; step++) {
            if (step == 0) {
                System.out.println("Connect test");
            }
            else if (step < 11) {
                System.out.println("Traversal " + step);
            }
            else {
                System.out.println("Query " + (step - 10));
            }
            
            long start = System.currentTimeMillis();

            // Create a new DB
            mDB = new EnerJDatabase();
            mDB.open(sDBURL, Database.OPEN_READ_ONLY);

            // Create a transaction
            Transaction trans = mImpl.newTransaction();
            trans.begin();

            // Perform query
            if (step >= 1 && step <= 10) {
                traversal(step);
            }
            else if (step == 11) {
                query1();
            }
            else if (step == 12) {
                query2();
            }
            else if (step == 13) {
                query3();
            }

            // Abort
            trans.abort();

            // Close DB
            mDB.close();
            mDB = null;

            // Stop time
            long stop = System.currentTimeMillis();
            System.out.println("Duration: " + (stop - start) + "ms");
        }
    }

    public static final void main(String[] args) throws Exception
    {
        if (args.length >= 1) {
            sDBURL = args[0];
            int startnum = 0;
            int endnum = 13;
            if (args.length == 2) {
                startnum = Integer.parseInt(args[1]);
                endnum = startnum;
            }
            else if (args.length == 3) {
                startnum = Integer.parseInt(args[1]);
                endnum = Integer.parseInt(args[2]);
            }
            
            new OO7Bench().queryDB(startnum, endnum);
        }
        else {
            System.out.println("Usage: OO7Bench dburl [test#]");
        }
    }

}

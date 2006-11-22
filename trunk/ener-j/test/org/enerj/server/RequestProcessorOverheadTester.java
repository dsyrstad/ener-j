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
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/RequestProcessorOverheadTester.java,v 1.1 2006/01/12 04:39:45 dsyrstad Exp $

package org.enerj.server;

import org.enerj.util.*;


/**
 * Tests RequestProcessor overhead - NOT JUNIT!<p>
 *
 * @version $Id: RequestProcessorOverheadTester.java,v 1.1 2006/01/12 04:39:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class RequestProcessorOverheadTester
{
    

    public static void main(String[] args) throws Exception
    {
        int numRequests = 1000000;
        RequestProcessor processor = new RequestProcessor("blah");
        long start = System.currentTimeMillis();
        for (int i = 0; i < numRequests; i++) {
            Request request = new Request();
            processor.queueRequestAndWait(request);
        }
        
        long end = System.currentTimeMillis();
        
        System.out.println("Processed " + numRequests + ". Took " + (end - start) + "ms");
        System.exit(1);
    }
    


    static final class Request extends RequestProcessor.Request
    {
        

        Request()
        {
        }


        public void run()
        {
            complete(null);
        }
    }

}

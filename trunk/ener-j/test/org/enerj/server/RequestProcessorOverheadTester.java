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
    
    //----------------------------------------------------------------------
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
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    static final class Request extends RequestProcessor.Request
    {
        
        //----------------------------------------------------------------------
        Request()
        {
        }

        //----------------------------------------------------------------------
        public void run()
        {
            complete(null);
        }
    }

}

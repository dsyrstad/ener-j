// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/RequestProcessor.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.util;

import java.io.*;
import java.nio.*;
import java.util.*;



/**
 * Generic class that allows a client to queue requests (RequestProcessor.Request)
 * for processing in another thread. This class creates a daemon thread to process
 * the requests. The client submits the request and waits for the result.
 *
 * @version $Id: RequestProcessor.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class RequestProcessor
{
    private boolean mShutdown = false;
    private Thread mRequestThread = null;
    /** The request queue. This is a list of Runnable objects. */
    private LinkedList mRequestQueue = new LinkedList();

    //----------------------------------------------------------------------
    /**
     * Constructs a new RequestProcessor and starts a processing thread with 
     * the given name. The thread is created as a non-daemon thread.
     *
     * @param aThreadName the name for the thread.
     */
    public RequestProcessor(String aThreadName)
    {
        this (aThreadName, false);
    }

    //----------------------------------------------------------------------
    /**
     * Constructs a new RequestProcessor and starts a processing thread with 
     * the given name. The thread is created as a non-daemon thread.
     *
     * @param aThreadName the name for the thread.
     * @param useDaemonThread if true, a daemon thread is used, otherwise a 
     *  shutdown-blocking thread is used.
     */
    public RequestProcessor(String aThreadName, boolean useDaemonThread)
    {
        mRequestThread = new Thread( new Processor(), aThreadName);
        mRequestThread.setDaemon(useDaemonThread);
        mRequestThread.start();
    }

    //----------------------------------------------------------------------
    /**
     * Sends a request to the processing thread to shutdown. After all requests
     * have finished processing, the thread shuts down and this method returns.
     */
    public void shutdown()
    {
        mShutdown = true;
        // If shutdown called from the request processing thread, just set the flag.
        if (Thread.currentThread().equals(mRequestThread)) {
            return;
        }

        synchronized (mRequestQueue) {
            mRequestQueue.notify();
        }

        if (mRequestThread != null && mRequestThread.isAlive()) {
            try {
                // Wait for thread to finish.
                mRequestThread.join();
            }
            catch (InterruptedException e) {
                // Ingore
            }
        }
    }

    //----------------------------------------------------------------------
    /**
     * Queues a Request to be processed by the RequestProcessor.
     * Returns when the request has been processed.
     *
     * @param aRequest a RequestProcessor.Request.
     *
     * @throws RuntimeException if the scheduler is shutdown.
     */
    public void queueRequestAndWait(Request aRequest)
    {
        queueRequest(aRequest);
        waitForRequest(aRequest);
    }

    //----------------------------------------------------------------------
    /**
     * Queues a Request to be processed by the RequestProcessor.
     * Returns after the request has been queued, but not yet processed.
     *
     * @param aRequest a RequestProcessor.Request.
     *
     * @throws RuntimeException if the scheduler is shutdown.
     */
    public void queueRequest(Request aRequest)
    {
        if (mShutdown) {
            throw new RuntimeException("Processor is shutdown");
        }

        // Queue the request
        synchronized (mRequestQueue) {
            mRequestQueue.addLast(aRequest);
            // Wake up the request queue if necessary.
            mRequestQueue.notify();
        }
    }

    //----------------------------------------------------------------------
    /**
     * Wait for a previously queued Request to be processed by the RequestProcessor.
     * Returns when the request has been processed.
     *
     * @param aRequest a RequestProcessor.Request.
     *
     * @throws RuntimeException if the scheduler is shutdown.
     */
    public void waitForRequest(Request aRequest)
    {
        // Wait for the request to complete.
        synchronized (aRequest) {
            while (!aRequest.mRequestComplete) {
                try {
                    aRequest.wait();
                }
                catch (InterruptedException e) {
                    // Ignore
                }
            } // ...end while.
        } // ... end synchronized.
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * The main Runnable for the request thread.
     *
     * @version $Id: RequestProcessor.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
     * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
     * @see RequestProcessor
     */
    private final class Processor implements Runnable
    {
        //----------------------------------------------------------------------
        public void processRequest(Request aRequest)
        {
            try {
                aRequest.run();
            }
            catch (Exception e) {
                aRequest.complete(e);
            }
        }

        //----------------------------------------------------------------------
        public void run()
        {
            while (true) {
                while (true) {
                    Request request;
                    synchronized (mRequestQueue) {
                        if (mRequestQueue.isEmpty()) {
                            // If no more requests and shutdown received, end now.
                            if (mShutdown) {
                                return;
                            }

                            break; // Drop thru to wait().
                        }

                        request = (Request)mRequestQueue.removeFirst();
                    }

                    processRequest(request);
                } // end while (true) - inner loop.

                synchronized (mRequestQueue) {
                    // Queue is now empty - wait for more requests.
                    // Double check that queue is empty and not shutdown, because we dropped the lock.
                    if (mShutdown) {
                        return;
                    }
                    
                    if (mRequestQueue.isEmpty()) {
                        try {
                            mRequestQueue.wait();
                        }
                        catch (InterruptedException e) {
                            // Ignore
                        }
                    }
                } // .. end synchronized.
            } // ... end while (true) - outer loop.

            // Not reached.
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Represents the base class for requests to the RequestProcessor.
     * Subclasses implement the run() method.
     *
     * @version $Id: RequestProcessor.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
     * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
     * @see RequestProcessor
     */
    abstract public static class Request implements Runnable
    {
        /** This is set to true when the request processing is complete - regardless 
         * of whether an exception was thrown. 
         */
        private boolean mRequestComplete = false;
        /** If non-null, an exception that was thrown while processing the request. */
        private Exception mRequestException = null;
        /** True if another thread is monitoring the status of this request.
         */
        private boolean mMonitored = true;

        //----------------------------------------------------------------------
        /**
         * Constructs an empty request.
         */
        protected Request()
        {
        }
        
        //----------------------------------------------------------------------
        /**
         * Determines whether this request is monitored. If the request is not
         * monitored, it will not be notified when the request completes.
         *
         * @return true if it is monitored, else false.
         */
        public boolean isMonitored()
        {
            return mMonitored;
        }
        
        //----------------------------------------------------------------------
        /**
         * Sets whether this request is monitored. If the request is not
         * monitored, it will not be notified when the request completes.
         *
         * @param aMonitoredFlag true if it is monitored, else false.
         */
        public void setMonitored(boolean aMonitoredFlag)
        {
            mMonitored = aMonitoredFlag;
        }
        
        //----------------------------------------------------------------------
        /**
         * Determines if the request is complete.
         *
         * @return true if it is complete, false if it is still pending.
         */
        public boolean isComplete()
        {
            return mRequestComplete;
        }
        
        //----------------------------------------------------------------------
        /**
         * Gets the Exception for the request, if it is complete.
         *
         * @return the Exception if there was one, otherwise null. Also returns
         *  null if isComplete is false.
         */
        public Exception getException()
        {
            return mRequestException;
        }
        
        //----------------------------------------------------------------------
        /**
         * Completes a request and notifies the requester.
         *
         * @param anException an Exception which caused the request to fail. May be
         *  null if the request did not encounter an exception.
         */
        public void complete(Exception anException)
        {
            mRequestException = anException;
            if (mMonitored) {
                synchronized (this) {
                    mRequestComplete = true;
                    // Wake up the requester.
                    this.notify();
                }
            }
            else {
                mRequestComplete = true;
            }
        }
    }

}



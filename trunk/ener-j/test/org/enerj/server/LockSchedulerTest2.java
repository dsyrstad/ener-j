// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/LockSchedulerTest2.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $

package org.enerj.server;

import java.util.Properties;



/**
 * Tests a lot of locks LockServer. NOT A JUNIT TEST!<p>
 *
 * @version $Id: LockSchedulerTest2.java,v 1.3 2005/08/12 02:56:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class LockSchedulerTest2
{
    
    //----------------------------------------------------------------------
    public static void main(String[] args) throws Exception
    {
        Properties props = new Properties( System.getProperties() );
        props.setProperty("LockScheduler.initialNumObjs", "1000");
        props.setProperty("LockScheduler.deadlockAlgorithm", "Waits-For");
        LockServer lockServer = (LockServer)PluginHelper.connect(LockScheduler.class.getName(), props);

        Object[] objs = new Object[10000];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = new Object();
        }
        
        long start = System.currentTimeMillis();
        LockServerTransaction txn = lockServer.startTransaction();
        LockServerTransaction txn2 = lockServer.startTransaction();
        for (int i = 0; i < objs.length; i++) {
            if (!txn.lock(objs[i], LockMode.READ, 10000L)) {
                throw new Exception("Lock err at " + i);
            }
            if (!txn2.lock(objs[i], LockMode.READ, 10000L)) {
                throw new Exception("Lock err2 at " + i);
            }
        }

        long lockEnd = System.currentTimeMillis();
        System.out.println("-------------------------------");
        System.out.println( lockServer.dumpMetrics() );
        txn.end();
        String metrics = lockServer.dumpMetrics();
        txn2.end();
        long txnEnd = System.currentTimeMillis();
        System.out.println("-------------------------------");
        System.out.println( metrics );
        System.out.println("-------------------------------");
        System.out.println( lockServer.dumpMetrics() );
        
        System.out.println("lockEnd=" + (lockEnd - start) + " txnEnd=" + (txnEnd - lockEnd));
        lockServer.disconnect();
    }
    
}

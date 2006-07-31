// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/LockSchedulerTest.java,v 1.4 2006/01/12 04:39:45 dsyrstad Exp $

package org.enerj.server;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Tests LockScheduler. <p>
 *
 * @version $Id: LockSchedulerTest.java,v 1.4 2006/01/12 04:39:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class LockSchedulerTest extends AbstractLockServerTest
{
    
    //----------------------------------------------------------------------
    public LockSchedulerTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(LockSchedulerTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(LockSchedulerTest.class);
    }
    
    //----------------------------------------------------------------------
    protected Properties getWaitsForProperties()
    {
        Properties props = new Properties( System.getProperties() );
        props.setProperty("LockScheduler.initialNumObjs", "1000");
        props.setProperty("LockScheduler.deadlockAlgorithm", "Waits-For");
        return props;
    }
        
    //----------------------------------------------------------------------
    protected Properties getTimestampProperties()
    {
        Properties props = new Properties( System.getProperties() );
        props.setProperty("LockScheduler.initialNumObjs", "1000");
        props.setProperty("LockScheduler.deadlockAlgorithm", "Timestamp");
        return props;
    }
    
    //----------------------------------------------------------------------
    protected String getLockServerClassName()
    {
        return LockScheduler.class.getName();
    }
}

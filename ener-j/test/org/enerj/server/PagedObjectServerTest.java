// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/PagedObjectServerTest.java,v 1.4 2006/01/12 04:39:45 dsyrstad Exp $

package org.enerj.server;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Tests PagedObjectServer. <p>
 *
 * @version $Id: PagedObjectServerTest.java,v 1.4 2006/01/12 04:39:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class PagedObjectServerTest extends AbstractObjectServerTest
{
    
    //----------------------------------------------------------------------
    public PagedObjectServerTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(PagedObjectServerTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(PagedObjectServerTest.class);
    }
    
    //----------------------------------------------------------------------
    protected Properties getObjectServerProperties()
    {
        Properties props = new Properties( System.getProperties() );
        props.setProperty("vo.dbname", "PageObjectServerTest");
        props.setProperty("PagedObjectServer.PageServerClass", CachedPageServer.class.getName() );
        props.setProperty("PagedObjectServer.LockServerClass", LockScheduler.class.getName() );
        props.setProperty("PagedObjectServer.RedoLogServerClass", ArchivingRedoLogServer.class.getName() );
        props.setProperty("PagedObjectServer.MaxUpdateCacheSize", "8192000");
        props.setProperty("PagedObjectServer.UpdateCacheInitialHashSize", "80000");
        props.setProperty("ArchivingRedoLogServer.logName", "/tmp/PagedObjectServerTest.log");
        props.setProperty("ArchivingRedoLogServer.shouldArchive", "true");
        props.setProperty("ArchivingRedoLogServer.requestedLogSize", "0");
        props.setProperty("CachedPageServer.delegatePageServerClass", FilePageServer.class.getName() );
        props.setProperty("CachedPageServer.numberOfCachedPages", "1000");
        props.setProperty("FilePageServer.volume", "/tmp/ObjectServer-volume");
        props.setProperty("FilePageServer.pageSize", "8192");
        props.setProperty("LockScheduler.initialNumObjs", "20000");
        props.setProperty("LockScheduler.deadlockAlgorithm", "Waits-For");
        return props;
    }
        
    //----------------------------------------------------------------------
    protected String getObjectServerClassName()
    {
        return PagedObjectServer.class.getName();
    }
}

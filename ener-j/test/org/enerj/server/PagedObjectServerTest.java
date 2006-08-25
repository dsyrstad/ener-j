// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/PagedObjectServerTest.java,v 1.4 2006/01/12 04:39:45 dsyrstad Exp $

package org.enerj.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
	private static final String DB_NAME = "PagedObjectServerTest";
    private File mTmpPageFile = null;
    private File mTmpLogFile = null;
    
    //----------------------------------------------------------------------
    public PagedObjectServerTest(String aTestName) 
    {
        super(aTestName);

    	try {
    		mTmpLogFile = File.createTempFile(DB_NAME, ".log");
    		mTmpLogFile.delete();
    		mTmpLogFile.deleteOnExit();
    		mTmpPageFile = File.createTempFile(DB_NAME, ".enerj");
    		mTmpPageFile.delete();
    		mTmpLogFile.deleteOnExit();
    	}
    	catch (IOException e) {
    		// Shouldn't happen
    	}
    }
    
    //----------------------------------------------------------------------
    private File getPageFile()
    {
    	return mTmpPageFile;
    }
    
    //----------------------------------------------------------------------
    private File getLogFile()
    {
    	return mTmpPageFile;
    }
    
    //----------------------------------------------------------------------
    /**
     * Creates a new page server volume.
     */
    protected void createDB() throws Exception
    {
        getPageFile().delete();
        if (getPageFile().exists()) {
            throw new Exception("Huh? " + getPageFile().getAbsolutePath() + " still exists. Possibly last instance of PageServer didn't shutdown.");
        }
        
        // Delete the log file
        getLogFile().delete();
        
        Properties props = getObjectServerProperties();
        props.setProperty("DefaultMetaObjectServer.ObjectServerClass", getObjectServerClassName());
        
        File dbdir = getPageFile().getParentFile();
        System.setProperty(MetaObjectServer.ENERJ_DBPATH_PROP, dbdir.getAbsolutePath());
        dbdir = new File(dbdir, DB_NAME);
        dbdir.mkdir();
        
        FileOutputStream propStream = new FileOutputStream(new File(dbdir, DB_NAME + ".properties"));
        props.store(propStream, null);
        propStream.close();
        PagedObjectServer.createDatabase("test", DB_NAME, 0L, 0L);
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
        props.setProperty(MetaObjectServer.ENERJ_DBNAME_PROP, DB_NAME);
        props.setProperty("PagedObjectServer.PageServerClass", CachedPageServer.class.getName() );
        props.setProperty("PagedObjectServer.LockServerClass", LockScheduler.class.getName() );
        props.setProperty("PagedObjectServer.RedoLogServerClass", ArchivingRedoLogServer.class.getName() );
        props.setProperty("PagedObjectServer.MaxUpdateCacheSize", "8192000");
        props.setProperty("PagedObjectServer.UpdateCacheInitialHashSize", "80000");
        props.setProperty("ArchivingRedoLogServer.logName", getLogFile().getAbsolutePath());
        props.setProperty("ArchivingRedoLogServer.shouldArchive", "true");
        props.setProperty("ArchivingRedoLogServer.requestedLogSize", "0");
        props.setProperty("CachedPageServer.delegatePageServerClass", FilePageServer.class.getName() );
        props.setProperty("CachedPageServer.numberOfCachedPages", "1000");
        props.setProperty("FilePageServer.volume", getPageFile().getAbsolutePath());
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

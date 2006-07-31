// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/CallbackTest.java,v 1.4 2006/06/03 20:31:01 dsyrstad Exp $

package org.enerj.enhancer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.enerj.annotations.Persist;
import org.enerj.core.ObjectSerializer;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;

/**
 * Tests enerj_PostLoad, enerj_PreStore, enerj_PostStore callback functionality.
 *
 * @version $Id: CallbackTest.java,v 1.4 2006/06/03 20:31:01 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class CallbackTest extends TestCase 
{
    private static final String DATABASE_URI = "enerj://root:root@-/CallbackTestDB?DefaultMetaObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";

    private static boolean sEnhanced = false;
    
    //----------------------------------------------------------------------
    public CallbackTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(CallbackTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(CallbackTest.class);
    }
    
    //----------------------------------------------------------------------
    private void checkCallbacks(Testable aTestable, boolean hasPostLoad, boolean hasPreStore, boolean hasPostStore)
        throws Exception
    {
        Persistable persistable = (Persistable)aTestable;
        PersistableHelper.setNonTransactional(persistable);

        // These should all be false right now.
        assertTrue("voPostLoad should not have been called", !hasPostLoad || !aTestable.wasPostLoadCalled());
        assertTrue("voPreStore should not have been called", !hasPreStore || !aTestable.wasPreStoreCalled());
        assertTrue("voPostStore should not have been called", !hasPostStore || !aTestable.wasPostStoreCalled());

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(1000);
        DataOutputStream dataOutput = new DataOutputStream(byteOutputStream);
        persistable.enerj_WriteObject( new ObjectSerializer.WriteContext(dataOutput) );
        byte[] buf  = byteOutputStream.toByteArray();
        
        assertTrue("voPostLoad should not have been called", !hasPostLoad || !aTestable.wasPostLoadCalled());
        assertTrue("voPreStore should have been called", !hasPreStore || aTestable.wasPreStoreCalled());
        assertTrue("voPostStore should have been called", !hasPostStore || aTestable.wasPostStoreCalled());

        aTestable.reset();
        // These should all be false right now.
        assertTrue("voPostLoad should not have been called", !hasPostLoad || !aTestable.wasPostLoadCalled());
        assertTrue("voPreStore should not have been called", !hasPreStore || !aTestable.wasPreStoreCalled());
        assertTrue("voPostStore should not have been called", !hasPostStore || !aTestable.wasPostStoreCalled());
        
        DataInputStream dataInput = new DataInputStream( new ByteArrayInputStream(buf) );
        persistable.enerj_ReadObject( new ObjectSerializer.ReadContext(dataInput) );
        
        // These should all be false right now.
        assertTrue("voPostLoad should have been called", !hasPostLoad || aTestable.wasPostLoadCalled());
        assertTrue("voPreStore should not have been called", !hasPreStore || !aTestable.wasPreStoreCalled());
        assertTrue("voPostStore should not have been called", !hasPostStore || !aTestable.wasPostStoreCalled());
    }
    
    //----------------------------------------------------------------------
    /**
     * Basic tests.
     */
    public void testBasicStuff() throws Exception
    {
        checkCallbacks( new TLPWithPrivateCallback(), true, true, true);
        checkCallbacks( new TLPWithPublicCallback(), true, true, true);
        checkCallbacks( new NTLPWithPrivateCallback(), true, true, true);
        checkCallbacks( new NTLPWithPublicCallback(), true, true, true);
        checkCallbacks( new TLPWithoutCallback(), false, false, false);
        checkCallbacks( new NTLPWithoutCallback(), false, false, false);
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Interface for testing callbacks.
     */
    private static interface Testable
    {
        boolean wasPostLoadCalled();
        boolean wasPreStoreCalled();
        boolean wasPostStoreCalled();
        void reset();
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Test class for Callback on a Top-Level Persistable with a private voPreCallback
     * callback.
     */
    @Persist
    private static class TLPWithPrivateCallback implements Testable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        transient private boolean mPostLoadCalled = false;
        transient private boolean mPreStoreCalled = false;
        transient private boolean mPostStoreCalled = false;
        
        //----------------------------------------------------------------------
        TLPWithPrivateCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        
        //----------------------------------------------------------------------
        private void voPostLoad()
        {
            mPostLoadCalled = true;
        }

        //----------------------------------------------------------------------
        private void voPreStore()
        {
            mPreStoreCalled = true;
        }

        //----------------------------------------------------------------------
        private void voPostStore()
        {
            mPostStoreCalled = true;
        }

        //----------------------------------------------------------------------
        public void reset()
        {
            mPostLoadCalled = false;
            mPreStoreCalled = false;
            mPostStoreCalled = false;
        }

        //----------------------------------------------------------------------
        public boolean wasPostLoadCalled()
        {
            return mPostLoadCalled;
        }

        //----------------------------------------------------------------------
        public boolean wasPreStoreCalled()
        {
            return mPreStoreCalled;
        }

        //----------------------------------------------------------------------
        public boolean wasPostStoreCalled()
        {
            return mPostStoreCalled;
        }
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Test class for Callback on a Top-Level Persistable with a public voPreCallback
     * callback.
     */
    @Persist
    private static class TLPWithPublicCallback implements Testable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        transient private boolean mPostLoadCalled = false;
        transient private boolean mPreStoreCalled = false;
        transient private boolean mPostStoreCalled = false;
        
        //----------------------------------------------------------------------
        TLPWithPublicCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        
        //----------------------------------------------------------------------
        public void voPostLoad()
        {
            mPostLoadCalled = true;
        }

        //----------------------------------------------------------------------
        public void voPreStore()
        {
            mPreStoreCalled = true;
        }

        //----------------------------------------------------------------------
        public void voPostStore()
        {
            mPostStoreCalled = true;
        }

        //----------------------------------------------------------------------
        public void reset()
        {
            mPostLoadCalled = false;
            mPreStoreCalled = false;
            mPostStoreCalled = false;
        }

        //----------------------------------------------------------------------
        public boolean wasPostLoadCalled()
        {
            return mPostLoadCalled;
        }

        //----------------------------------------------------------------------
        public boolean wasPreStoreCalled()
        {
            return mPreStoreCalled;
        }

        //----------------------------------------------------------------------
        public boolean wasPostStoreCalled()
        {
            return mPostStoreCalled;
        }
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Test class for Callback on a Non-Top-Level Persistable with a private voPreCallback
     * callback.
     */
    @Persist
    private static final class NTLPWithPrivateCallback  extends TLPWithPrivateCallback implements Testable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        transient private boolean mPostLoadCalled = false;
        transient private boolean mPreStoreCalled = false;
        transient private boolean mPostStoreCalled = false;
        
        //----------------------------------------------------------------------
        NTLPWithPrivateCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        
        //----------------------------------------------------------------------
        private void voPostLoad()
        {
            mPostLoadCalled = true;
        }

        //----------------------------------------------------------------------
        private void voPreStore()
        {
            mPreStoreCalled = true;
        }

        //----------------------------------------------------------------------
        private void voPostStore()
        {
            mPostStoreCalled = true;
        }

        //----------------------------------------------------------------------
        public void reset()
        {
            mPostLoadCalled = false;
            mPreStoreCalled = false;
            mPostStoreCalled = false;
        }

        //----------------------------------------------------------------------
        public boolean wasPostLoadCalled()
        {
            return super.wasPostLoadCalled() && mPostLoadCalled;
        }

        //----------------------------------------------------------------------
        public boolean wasPreStoreCalled()
        {
            return super.wasPreStoreCalled() && mPreStoreCalled;
        }

        //----------------------------------------------------------------------
        public boolean wasPostStoreCalled()
        {
            return super.wasPostStoreCalled() && mPostStoreCalled;
        }
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Test class for Callback on a Non-Top-Level Persistable with a public voPreCallback
     * callback. Note that the voPreCallback callback technically overrides the super
     * class version, but since invokespecial is used to invoke the callback, it
     * should be invoked on both objects.
     */
    @Persist
    private static final class NTLPWithPublicCallback extends TLPWithPublicCallback implements Testable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        transient private boolean mPostLoadCalled = false;
        transient private boolean mPreStoreCalled = false;
        transient private boolean mPostStoreCalled = false;
        
        //----------------------------------------------------------------------
        NTLPWithPublicCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        
        //----------------------------------------------------------------------
        public void voPostLoad()
        {
            mPostLoadCalled = true;
        }

        //----------------------------------------------------------------------
        public void voPreStore()
        {
            mPreStoreCalled = true;
        }

        //----------------------------------------------------------------------
        public void voPostStore()
        {
            mPostStoreCalled = true;
        }

        //----------------------------------------------------------------------
        public void reset()
        {
            mPostLoadCalled = false;
            mPreStoreCalled = false;
            mPostStoreCalled = false;
        }

        //----------------------------------------------------------------------
        public boolean wasPostLoadCalled()
        {
            return super.wasPostLoadCalled() && mPostLoadCalled;
        }

        //----------------------------------------------------------------------
        public boolean wasPreStoreCalled()
        {
            return super.wasPreStoreCalled() && mPreStoreCalled;
        }

        //----------------------------------------------------------------------
        public boolean wasPostStoreCalled()
        {
            return super.wasPostStoreCalled() && mPostStoreCalled;
        }
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Test class for Callback on a Top-Level Persistable without a voPreCallback
     * callback.
     */
    @Persist
    private static class TLPWithoutCallback implements Testable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        
        //----------------------------------------------------------------------
        TLPWithoutCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        
        //----------------------------------------------------------------------
        public void reset()
        {
        }

        //----------------------------------------------------------------------
        public boolean wasPostLoadCalled()
        {
            return false;
        }

        //----------------------------------------------------------------------
        public boolean wasPreStoreCalled()
        {
            return false;
        }

        //----------------------------------------------------------------------
        public boolean wasPostStoreCalled()
        {
            return false;
        }
    }
    
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Test class for Callback on a Top-Level Persistable without a voPreCallback
     * callback.
     */
    @Persist
    private static class NTLPWithoutCallback extends TLPWithoutCallback implements Testable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        
        //----------------------------------------------------------------------
        NTLPWithoutCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        
        //----------------------------------------------------------------------
        public void reset()
        {
        }

        //----------------------------------------------------------------------
        public boolean wasPostLoadCalled()
        {
            return false;
        }

        //----------------------------------------------------------------------
        public boolean wasPreStoreCalled()
        {
            return false;
        }

        //----------------------------------------------------------------------
        public boolean wasPostStoreCalled()
        {
            return false;
        }
    }
    
}

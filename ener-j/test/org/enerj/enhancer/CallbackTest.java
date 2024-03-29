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
    public CallbackTest(String aTestName) 
    {
        super(aTestName);
    }

    public static Test suite() 
    {
        return new TestSuite(CallbackTest.class);
    }
    

    private void checkCallbacks(Testable aTestable, boolean hasPostLoad, boolean hasPreStore, boolean hasPostStore)
        throws Exception
    {
        Persistable persistable = (Persistable)aTestable;
        PersistableHelper.setNonTransactional(persistable);

        // These should all be false right now.
        assertTrue("enerjPostLoad should not have been called", !hasPostLoad || !aTestable.wasPostLoadCalled());
        assertTrue("enerjPreStore should not have been called", !hasPreStore || !aTestable.wasPreStoreCalled());
        assertTrue("enerjPostStore should not have been called", !hasPostStore || !aTestable.wasPostStoreCalled());

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(1000);
        DataOutputStream dataOutput = new DataOutputStream(byteOutputStream);
        persistable.enerj_WriteObject( new ObjectSerializer(dataOutput) );
        byte[] buf  = byteOutputStream.toByteArray();
        
        assertTrue("enerjPostLoad should not have been called", !hasPostLoad || !aTestable.wasPostLoadCalled());
        assertTrue("enerjPreStore should have been called", !hasPreStore || aTestable.wasPreStoreCalled());
        assertTrue("enerjPostStore should have been called", !hasPostStore || aTestable.wasPostStoreCalled());

        aTestable.reset();
        // These should all be false right now.
        assertTrue("enerjPostLoad should not have been called", !hasPostLoad || !aTestable.wasPostLoadCalled());
        assertTrue("enerjPreStore should not have been called", !hasPreStore || !aTestable.wasPreStoreCalled());
        assertTrue("enerjPostStore should not have been called", !hasPostStore || !aTestable.wasPostStoreCalled());
        
        DataInputStream dataInput = new DataInputStream( new ByteArrayInputStream(buf) );
        persistable.enerj_ReadObject( new ObjectSerializer(dataInput) );
        
        // These should all be false right now.
        assertTrue("enerjPostLoad should have been called", !hasPostLoad || aTestable.wasPostLoadCalled());
        assertTrue("enerjPreStore should not have been called", !hasPreStore || !aTestable.wasPreStoreCalled());
        assertTrue("enerjPostStore should not have been called", !hasPostStore || !aTestable.wasPostStoreCalled());
    }
    

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
        

        TLPWithPrivateCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        private void enerjPostLoad()
        {
            mPostLoadCalled = true;
        }


        private void enerjPreStore()
        {
            mPreStoreCalled = true;
        }


        private void enerjPostStore()
        {
            mPostStoreCalled = true;
        }


        public void reset()
        {
            mPostLoadCalled = false;
            mPreStoreCalled = false;
            mPostStoreCalled = false;
        }


        public boolean wasPostLoadCalled()
        {
            return mPostLoadCalled;
        }


        public boolean wasPreStoreCalled()
        {
            return mPreStoreCalled;
        }


        public boolean wasPostStoreCalled()
        {
            return mPostStoreCalled;
        }
    }
    


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
        

        TLPWithPublicCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        public void enerjPostLoad()
        {
            mPostLoadCalled = true;
        }


        public void enerjPreStore()
        {
            mPreStoreCalled = true;
        }


        public void enerjPostStore()
        {
            mPostStoreCalled = true;
        }


        public void reset()
        {
            mPostLoadCalled = false;
            mPreStoreCalled = false;
            mPostStoreCalled = false;
        }


        public boolean wasPostLoadCalled()
        {
            return mPostLoadCalled;
        }


        public boolean wasPreStoreCalled()
        {
            return mPreStoreCalled;
        }


        public boolean wasPostStoreCalled()
        {
            return mPostStoreCalled;
        }
    }
    


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
        

        NTLPWithPrivateCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        private void enerjPostLoad()
        {
            mPostLoadCalled = true;
        }


        private void enerjPreStore()
        {
            mPreStoreCalled = true;
        }


        private void enerjPostStore()
        {
            mPostStoreCalled = true;
        }


        public void reset()
        {
            mPostLoadCalled = false;
            mPreStoreCalled = false;
            mPostStoreCalled = false;
        }


        public boolean wasPostLoadCalled()
        {
            return super.wasPostLoadCalled() && mPostLoadCalled;
        }


        public boolean wasPreStoreCalled()
        {
            return super.wasPreStoreCalled() && mPreStoreCalled;
        }


        public boolean wasPostStoreCalled()
        {
            return super.wasPostStoreCalled() && mPostStoreCalled;
        }
    }
    


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
        

        NTLPWithPublicCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        public void enerjPostLoad()
        {
            mPostLoadCalled = true;
        }


        public void enerjPreStore()
        {
            mPreStoreCalled = true;
        }


        public void enerjPostStore()
        {
            mPostStoreCalled = true;
        }


        public void reset()
        {
            mPostLoadCalled = false;
            mPreStoreCalled = false;
            mPostStoreCalled = false;
        }


        public boolean wasPostLoadCalled()
        {
            return super.wasPostLoadCalled() && mPostLoadCalled;
        }


        public boolean wasPreStoreCalled()
        {
            return super.wasPreStoreCalled() && mPreStoreCalled;
        }


        public boolean wasPostStoreCalled()
        {
            return super.wasPostStoreCalled() && mPostStoreCalled;
        }
    }
    


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
        

        TLPWithoutCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        public void reset()
        {
        }


        public boolean wasPostLoadCalled()
        {
            return false;
        }


        public boolean wasPreStoreCalled()
        {
            return false;
        }


        public boolean wasPostStoreCalled()
        {
            return false;
        }
    }
    


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
        

        NTLPWithoutCallback()
        {
            mInt = 5;
            mObj = new Date();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        public void reset()
        {
        }


        public boolean wasPostLoadCalled()
        {
            return false;
        }


        public boolean wasPreStoreCalled()
        {
            return false;
        }


        public boolean wasPostStoreCalled()
        {
            return false;
        }
    }
    
}

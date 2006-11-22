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
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/HollowTest.java,v 1.4 2006/06/09 02:39:16 dsyrstad Exp $

package org.enerj.enhancer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.enerj.annotations.Persist;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;

/**
 * Tests enerj_Hollow callback functionality.
 *
 * @version $Id: HollowTest.java,v 1.4 2006/06/09 02:39:16 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class HollowTest extends TestCase 
{
    private static final String DATABASE_URI = "enerj://root:root@-/HollowTestDB?DefaultObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";


    public HollowTest(String aTestName) 
    {
        super(aTestName);
    }
    

    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(HollowTest.class);
    }
    

    public static Test suite() 
    {
        return new TestSuite(HollowTest.class);
    }
    

    private void checkHollow(Hollowable aHollowable, boolean hasPreHollow)
    {
        Persistable persistable = (Persistable)aHollowable;
        PersistableHelper.setNonTransactional(persistable);

        assertTrue("Fields should be set", aHollowable.areFieldsSet());
        assertTrue("enerjPreHollow should not have been called", !hasPreHollow || !aHollowable.wasPreHollowCalled());

        persistable.enerj_Hollow();
        // Set loaded flag so that checkLoaded doesn't try to load this object
        persistable.enerj_SetLoaded(true);
        assertTrue("Fields should not be set", !aHollowable.areFieldsSet());
        assertTrue("enerjPreHollow should have been called", !hasPreHollow || aHollowable.wasPreHollowCalled());
    }
    

    /**
     * Basic tests.
     */
    public void testBasicStuff() throws Exception
    {
        checkHollow( new TLPWithPrivateCallback(), true);
        checkHollow( new TLPWithPublicCallback(), true);
        checkHollow( new NTLPWithPrivateCallback(), true);
        checkHollow( new NTLPWithPublicCallback(), true);
        checkHollow( new TLPWithoutCallback(), false);
        checkHollow( new NTLPWithoutCallback(), false);
    }
    


    /**
     * Interface for testing voHollow callback.
     */
    private static interface Hollowable
    {
        boolean areFieldsSet();
        boolean wasPreHollowCalled();
    }
    


    /**
     * Test class for Hollow on a Top-Level Persistable with a private enerjPreHollow
     * callback.
     */
    @Persist
    private static class TLPWithPrivateCallback implements Hollowable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        transient private boolean mHollowCalled = false;
        

        TLPWithPrivateCallback()
        {
            mInt = 5;
            mObj = new Object();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        public boolean areFieldsSet()
        {
            return mObj != null && mString != null && mIntArray != null;
        }


        private void enerjPreHollow()
        {
            mHollowCalled = true;
        }


        public boolean wasPreHollowCalled()
        {
            return mHollowCalled;
        }
    }
    


    /**
     * Test class for Hollow on a Top-Level Persistable with a public enerjPreHollow
     * callback.
     */
    @Persist
    private static class TLPWithPublicCallback implements Hollowable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        transient private boolean mHollowCalled = false;
        

        TLPWithPublicCallback()
        {
            mInt = 5;
            mObj = new Object();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        public boolean areFieldsSet()
        {
            return mObj != null && mString != null && mIntArray != null;
        }


        public void enerjPreHollow()
        {
            mHollowCalled = true;
        }


        public boolean wasPreHollowCalled()
        {
            return mHollowCalled;
        }
    }
    


    /**
     * Test class for Hollow on a Non-Top-Level Persistable with a private enerjPreHollow
     * callback.
     */
    @Persist
    private static final class NTLPWithPrivateCallback  extends TLPWithPrivateCallback implements Hollowable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        transient private boolean mHollowCalled = false;
        

        NTLPWithPrivateCallback()
        {
            mInt = 5;
            mObj = new Object();
            mString = "foo";
            mIntArray = new int[10];
        }
        

       public boolean areFieldsSet()
        {
            return mObj != null && mString != null && mIntArray != null;
        }


        private void enerjPreHollow()
        {
            mHollowCalled = true;
        }


        public boolean wasPreHollowCalled()
        {
            return super.wasPreHollowCalled() && mHollowCalled;
        }
    }
    


    /**
     * Test class for Hollow on a Non-Top-Level Persistable with a public enerjPreHollow
     * callback. Note that the enerjPreHollow callback technically overrides the super
     * class version, but since invokespecial is used to invoke the callback, it
     * should be invoked on both objects.
     */
    @Persist
    private static final class NTLPWithPublicCallback extends TLPWithPublicCallback implements Hollowable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        transient private boolean mHollowCalled = false;
        

        NTLPWithPublicCallback()
        {
            mInt = 5;
            mObj = new Object();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        public boolean areFieldsSet()
        {
            return mObj != null && mString != null && mIntArray != null;
        }


        public void enerjPreHollow()
        {
            mHollowCalled = true;
        }


        public boolean wasPreHollowCalled()
        {
            return super.wasPreHollowCalled() && mHollowCalled;
        }
    }
    


    /**
     * Test class for Hollow on a Top-Level Persistable without a enerjPreHollow
     * callback.
     */
    @Persist
    private static class TLPWithoutCallback implements Hollowable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        transient private boolean mHollowCalled = false;
        

        TLPWithoutCallback()
        {
            mInt = 5;
            mObj = new Object();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        public boolean areFieldsSet()
        {
            return mObj != null && mString != null && mIntArray != null;
        }


        public boolean wasPreHollowCalled()
        {
            return false; // not used
        }
    }
    


    /**
     * Test class for Hollow on a Top-Level Persistable without a enerjPreHollow
     * callback.
     */
    @Persist
    private static class NTLPWithoutCallback extends TLPWithoutCallback implements Hollowable
    {
        private int mInt;
        private Object mObj;
        private String mString;
        private int[] mIntArray;
        transient private boolean mHollowCalled = false;
        

        NTLPWithoutCallback()
        {
            mInt = 5;
            mObj = new Object();
            mString = "foo";
            mIntArray = new int[10];
        }
        

        public boolean areFieldsSet()
        {
            return mObj != null && mString != null && mIntArray != null;
        }


        public boolean wasPreHollowCalled()
        {
            return false; // not used 
        }
    }
    
}

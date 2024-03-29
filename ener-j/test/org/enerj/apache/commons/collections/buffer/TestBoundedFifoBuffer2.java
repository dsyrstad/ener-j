/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.enerj.apache.commons.collections.buffer;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Test;

import org.enerj.apache.commons.collections.BoundedCollection;
import org.enerj.apache.commons.collections.BufferOverflowException;
import org.enerj.apache.commons.collections.BulkTest;

/**
 * Runs tests against a full BoundedFifoBuffer, since many of the algorithms
 * differ depending on whether the fifo is full or not.
 * 
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Unknown
 */
public class TestBoundedFifoBuffer2 extends TestBoundedFifoBuffer {

    public TestBoundedFifoBuffer2(String n) {
        super(n);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestBoundedFifoBuffer2.class);
    }

    /**
     *  Returns a BoundedFifoBuffer that's filled to capacity.
     *  Any attempt to add to the returned buffer will result in a 
     *  BufferOverflowException.
     *
     *  @return a full BoundedFifoBuffer
     */
    public Collection makeFullCollection() {
        return new BoundedFifoBuffer(Arrays.asList(getFullElements()));
    }


    /**
     *  Overridden to skip the add tests.  All of them would fail with a 
     *  BufferOverflowException.
     *
     *  @return false
     */
    public boolean isAddSupported() {
        return false;
    }


    /**
     *  Overridden because the add operations raise BufferOverflowException
     *  instead of UnsupportedOperationException.
     */
    public void testUnsupportedAdd() {
    }


    /**
     *  Tests to make sure the add operations raise BufferOverflowException.
     */
    public void testBufferOverflow() {
        resetFull();
        try {
            collection.add(getOtherElements()[0]);
            fail("add should raise BufferOverflow.");
        } catch (BufferOverflowException e) {
            // expected
        }
        verify();

        try {
            collection.addAll(Arrays.asList(getOtherElements()));
            fail("addAll should raise BufferOverflow.");
        } catch (BufferOverflowException e) {
            // expected
        }
        verify();
    }

    /**
     * Tests is full
     */
    public void testIsFull() {
        resetFull();
        assertEquals(true, ((BoundedCollection) collection).isFull());
        ((BoundedFifoBuffer) collection).remove();
        assertEquals(false, ((BoundedCollection) collection).isFull());
        ((BoundedFifoBuffer) collection).add("jj");
        assertEquals(true, ((BoundedCollection) collection).isFull());
    }

    /**
     * Tests max size
     */
    public void testMaxSize() {
        resetFull();
        assertEquals(getFullElements().length, ((BoundedCollection) collection).maxSize());
        ((BoundedFifoBuffer) collection).remove();
        assertEquals(getFullElements().length, ((BoundedCollection) collection).maxSize());
        ((BoundedFifoBuffer) collection).add("jj");
        assertEquals(getFullElements().length, ((BoundedCollection) collection).maxSize());
    }

}


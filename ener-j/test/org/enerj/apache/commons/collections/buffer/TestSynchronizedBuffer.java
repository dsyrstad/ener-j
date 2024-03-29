/*
 *  Copyright 2004 The Apache Software Foundation
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
import junit.framework.TestSuite;

import org.enerj.apache.commons.collections.ArrayStack;
import org.enerj.apache.commons.collections.Buffer;
import org.enerj.apache.commons.collections.collection.AbstractTestCollection;

/**
 * Extension of {@link AbstractTestCollection} for exercising the 
 * {@link SynchronizedBuffer} implementation.
 *
 * @since Commons Collections 3.1
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Phil Steitz
 * @author Stephen Colebourne
 */
public class TestSynchronizedBuffer extends AbstractTestCollection {
    
    public TestSynchronizedBuffer(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestSynchronizedBuffer.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestSynchronizedBuffer.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    //-----------------------------------------------------------------------    
    public Collection makeCollection() {
        return SynchronizedBuffer.decorate(new UnboundedFifoBuffer());
    }
    
    public Collection makeFullCollection() {
        Buffer buffer = new UnboundedFifoBuffer();
        buffer.addAll(Arrays.asList(getFullElements()));
        return SynchronizedBuffer.decorate(buffer);
    }
    
    public Collection makeConfirmedCollection() {
        ArrayStack list = new ArrayStack();
        return list;
    }

    public Collection makeConfirmedFullCollection() {
        ArrayStack list = new ArrayStack();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }

    public boolean isNullSupported() {
        return false;
    }
    
    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/SynchronizedBuffer.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk((java.io.Serializable) collection, "D:/dev/collections/data/test/SynchronizedBuffer.fullCollection.version3.1.obj");
//    }

}

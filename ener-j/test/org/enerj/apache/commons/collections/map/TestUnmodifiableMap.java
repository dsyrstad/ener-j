/*
 *  Copyright 2003-2004 The Apache Software Foundation
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
package org.enerj.apache.commons.collections.map;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.apache.commons.collections.Unmodifiable;

/**
 * Extension of {@link AbstractTestMap} for exercising the 
 * {@link UnmodifiableMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Phil Steitz
 */
public class TestUnmodifiableMap extends AbstractTestIterableMap{
    
    public TestUnmodifiableMap(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestUnmodifiableMap.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestUnmodifiableMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
    //-------------------------------------------------------------------
    
    public Map makeEmptyMap() {
        return UnmodifiableMap.decorate(new HashMap());
    }
    
    public boolean isPutChangeSupported() {
        return false;
    }
    
    public boolean isPutAddSupported() {
        return false;
    }
    
    public boolean isRemoveSupported() {
        return false;
    }
    
    public Map makeFullMap() {
        Map m = new HashMap();
        addSampleMappings(m);
        return UnmodifiableMap.decorate(m);
    }
    
    //-----------------------------------------------------------------------
    public void testUnmodifiable() {
        assertTrue(makeEmptyMap() instanceof Unmodifiable);
        assertTrue(makeFullMap() instanceof Unmodifiable);
    }
    
    public void testDecorateFactory() {
        Map map = makeFullMap();
        assertSame(map, UnmodifiableMap.decorate(map));
        
        try {
            UnmodifiableMap.decorate(null);
            fail();
        } catch (IllegalArgumentException ex) {}
    }

    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        resetEmpty();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/UnmodifiableMap.emptyCollection.version3.1.obj");
//        resetFull();
//        writeExternalFormToDisk(
//            (java.io.Serializable) map,
//            "D:/dev/collections/data/test/UnmodifiableMap.fullCollection.version3.1.obj");
//    }
}
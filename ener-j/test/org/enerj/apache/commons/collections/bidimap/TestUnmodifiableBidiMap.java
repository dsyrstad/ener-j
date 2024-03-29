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
package org.enerj.apache.commons.collections.bidimap;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.enerj.apache.commons.collections.BidiMap;
import org.enerj.apache.commons.collections.BulkTest;

/**
 * JUnit tests.
 * 
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Stephen Colebourne
 */
public class TestUnmodifiableBidiMap extends AbstractTestBidiMap {

    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestUnmodifiableBidiMap.class);
    }

    public TestUnmodifiableBidiMap(String testName) {
        super(testName);
    }

    public BidiMap makeEmptyBidiMap() {
        return UnmodifiableBidiMap.decorate(new DualHashBidiMap());
    }
    public BidiMap makeFullBidiMap() {
        BidiMap bidi = new DualHashBidiMap();
        for (int i = 0; i < entries.length; i++) {
            bidi.put(entries[i][0], entries[i][1]);
        }
        return UnmodifiableBidiMap.decorate(bidi);
    }
    public Map makeFullMap() {
        BidiMap bidi = new DualHashBidiMap();
        addSampleMappings(bidi);
        return UnmodifiableBidiMap.decorate(bidi);
    }
    
    public Map makeConfirmedMap() {
        return new HashMap();
    }

    /**
     * Override to prevent infinite recursion of tests.
     */
    public String[] ignoredTests() {
        return new String[] {"TestUnmodifiableBidiMap.bulkTestInverseMap.bulkTestInverseMap"};
    }

    public boolean isPutAddSupported() {
        return false;
    }
    public boolean isPutChangeSupported() {
        return false;
    }
    public boolean isRemoveSupported() {
        return false;
    }
    
}

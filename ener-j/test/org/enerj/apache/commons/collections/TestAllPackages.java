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
package org.enerj.apache.commons.collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Entry point for all Collections project tests.
 * 
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Stephen Colebourne
 */
public class TestAllPackages extends TestCase {
    public TestAllPackages(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(org.enerj.apache.commons.collections.TestAll.suite());
        suite.addTest(org.enerj.apache.commons.collections.bag.TestAll.suite());
        suite.addTest(org.enerj.apache.commons.collections.bidimap.TestAll.suite());
        suite.addTest(org.enerj.apache.commons.collections.buffer.TestAll.suite());
        suite.addTest(org.enerj.apache.commons.collections.collection.TestAll.suite());
        suite.addTest(org.enerj.apache.commons.collections.comparators.TestAll.suite());
        suite.addTest(org.enerj.apache.commons.collections.iterators.TestAll.suite());
        suite.addTest(org.enerj.apache.commons.collections.keyvalue.TestAll.suite());
        suite.addTest(org.enerj.apache.commons.collections.list.TestAll.suite());
        suite.addTest(org.enerj.apache.commons.collections.map.TestAll.suite());
        suite.addTest(org.enerj.apache.commons.collections.set.TestAll.suite());
        return suite;
    }
        
    public static void main(String args[]) {
        String[] testCaseName = { TestAllPackages.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
    
}

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
package org.enerj.apache.commons.collections.bag;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.apache.commons.collections.Bag;
import org.enerj.apache.commons.collections.collection.TestTransformedCollection;

/**
 * Extension of {@link TestSortedBag} for exercising the {@link TransformedSortedBag}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Stephen Colebourne
 */
public class TestTransformedSortedBag extends AbstractTestSortedBag {
    
    public TestTransformedSortedBag(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTransformedSortedBag.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestTransformedSortedBag.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public Bag makeBag() {
        return TransformedSortedBag.decorate(new TreeBag(), TestTransformedCollection.NOOP_TRANSFORMER);
    }

    public void testTransformedBag() {
        Bag bag = TransformedSortedBag.decorate(new TreeBag(), TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, bag.size());
        Object[] els = new Object[] {"1", "3", "5", "7", "2", "4", "6"};
        for (int i = 0; i < els.length; i++) {
            bag.add(els[i]);
            assertEquals(i + 1, bag.size());
            assertEquals(true, bag.contains(new Integer((String) els[i])));
        }
        
        assertEquals(true, bag.remove(new Integer((String) els[0])));
        
    }

    public String getCompatibilityVersion() {
        return "3.1";
    }

//    public void testCreate() throws Exception {
//        Bag bag = makeBag();
//        writeExternalFormToDisk((java.io.Serializable) bag, "D:/dev/collections/data/test/TransformedSortedBag.emptyCollection.version3.1.obj");
//        bag = makeBag();
//        bag.add("A");
//        bag.add("A");
//        bag.add("B");
//        bag.add("B");
//        bag.add("C");
//        writeExternalFormToDisk((java.io.Serializable) bag, "D:/dev/collections/data/test/TransformedSortedBag.fullCollection.version3.1.obj");
//    }

}

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
package org.enerj.apache.commons.collections.set;

import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.apache.commons.collections.map.LinkedMap;

/**
 * JUnit test.
 *
 * @since Commons Collections 3.1
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Stephen Colebourne
 */
public class TestMapBackedSet2 extends AbstractTestSet {

    public TestMapBackedSet2(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestMapBackedSet2.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestMapBackedSet2.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public Set makeEmptySet() {
        return MapBackedSet.decorate(new LinkedMap());
    }

    protected Set setupSet() {
        Set set = makeEmptySet();

        for (int i = 0; i < 10; i++) {
            set.add(Integer.toString(i));
        }
        return set;
    }

    public void testOrdering() {
        Set set = setupSet();
        Iterator it = set.iterator();

        for (int i = 0; i < 10; i++) {
            assertEquals("Sequence is wrong", Integer.toString(i), it.next());
        }

        for (int i = 0; i < 10; i += 2) {
            assertTrue("Must be able to remove int", set.remove(Integer.toString(i)));
        }

        it = set.iterator();
        for (int i = 1; i < 10; i += 2) {
            assertEquals("Sequence is wrong after remove ", Integer.toString(i), it.next());
        }

        for (int i = 0; i < 10; i++) {
            set.add(Integer.toString(i));
        }

        assertEquals("Size of set is wrong!", 10, set.size());

        it = set.iterator();
        for (int i = 1; i < 10; i += 2) {
            assertEquals("Sequence is wrong", Integer.toString(i), it.next());
        }
        for (int i = 0; i < 10; i += 2) {
            assertEquals("Sequence is wrong", Integer.toString(i), it.next());
        }
    }

    public void testCanonicalEmptyCollectionExists() {
    }

    public void testCanonicalFullCollectionExists() {
    }

}

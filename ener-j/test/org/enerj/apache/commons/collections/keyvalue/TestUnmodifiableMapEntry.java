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
package org.enerj.apache.commons.collections.keyvalue;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.apache.commons.collections.KeyValue;
import org.enerj.apache.commons.collections.Unmodifiable;

/**
 * Test the UnmodifiableMapEntry class.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Neil O'Toole
 */
public class TestUnmodifiableMapEntry extends AbstractTestMapEntry {

    public TestUnmodifiableMapEntry(String testName) {
        super(testName);

    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestUnmodifiableMapEntry.class);
    }

    public static Test suite() {
        return new TestSuite(TestUnmodifiableMapEntry.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Make an instance of Map.Entry with the default (null) key and value.
     * Subclasses should override this method to return a Map.Entry
     * of the type being tested.
     */
    public Map.Entry makeMapEntry() {
        return new UnmodifiableMapEntry(null, null);
    }

    /**
     * Make an instance of Map.Entry with the specified key and value.
     * Subclasses should override this method to return a Map.Entry
     * of the type being tested.
     */
    public Map.Entry makeMapEntry(Object key, Object value) {
        return new UnmodifiableMapEntry(key, value);
    }

    //-----------------------------------------------------------------------
    /**
     * Subclasses should override this method.
     *
     */
    public void testConstructors() {
        // 1. test key-value constructor
        Map.Entry entry = new UnmodifiableMapEntry(key, value);
        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // 2. test pair constructor
        KeyValue pair = new DefaultKeyValue(key, value);
        entry = new UnmodifiableMapEntry(pair);
        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // 3. test copy constructor
        Map.Entry entry2 = new UnmodifiableMapEntry(entry);
        assertSame(key, entry2.getKey());
        assertSame(value, entry2.getValue());

        assertTrue(entry instanceof Unmodifiable);
    }

    public void testAccessorsAndMutators() {
        Map.Entry entry = makeMapEntry(key, value);

        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // check that null doesn't do anything funny
        entry = makeMapEntry(null, null);
        assertSame(null, entry.getKey());
        assertSame(null, entry.getValue());
    }

    public void testSelfReferenceHandling() {
        // block
    }

    public void testUnmodifiable() {
        Map.Entry entry = makeMapEntry();
        try {
            entry.setValue(null);
            fail();

        } catch (UnsupportedOperationException ex) {}
    }

}

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

import java.util.TreeMap;

import org.enerj.apache.commons.collections.map.AbstractTestMap;

/**
 * Tests TreeMap.
 * 
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Jason van Zyl
 */
public abstract class TestTreeMap extends AbstractTestMap {
    
    public TestTreeMap(String testName) {
        super(testName);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestTreeMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public boolean isAllowNullKey() {
        return false;
    }

    protected TreeMap map = null;

    public void setUp() {
        map = (TreeMap) makeEmptyMap();
    }

    public void testNewMap() {
        assertTrue("New map is empty", map.isEmpty());
        assertEquals("New map has size zero", map.size(), 0);
    }

    public void testSearch() {
        map.put("first", "First Item");
        map.put("second", "Second Item");
        assertEquals("Top item is 'Second Item'", map.get("first"), "First Item");
        assertEquals("Next Item is 'First Item'", map.get("second"), "Second Item");
    }
}

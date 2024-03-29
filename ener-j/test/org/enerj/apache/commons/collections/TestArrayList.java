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

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.apache.commons.collections.list.AbstractTestList;

/**
 * Abstract test class for ArrayList.
 * 
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Jason van Zyl
 */
public abstract class TestArrayList extends AbstractTestList {
    
    protected ArrayList list = null;
    
    public TestArrayList(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestArrayList.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestArrayList.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public void setUp() {
        list = (ArrayList) makeEmptyList();
    }

    //-----------------------------------------------------------------------
    public void testNewArrayList() {
        assertTrue("New list is empty", list.isEmpty());
        assertEquals("New list has size zero", list.size(), 0);

        try {
            list.get(1);
            fail("get(int i) should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            ; // Expected result
        }
    }

    public void testSearch() {
        list.add("First Item");
        list.add("Last Item");
        assertEquals("First item is 'First Item'", list.get(0), "First Item");
        assertEquals("Last Item is 'Last Item'", list.get(1), "Last Item");
    }

}

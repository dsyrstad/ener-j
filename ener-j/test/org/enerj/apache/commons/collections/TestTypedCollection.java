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
import java.util.Collection;
import java.util.List;

/**
 * Tests TypedCollection.
 * 
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Stephen Colebourne
 */
public abstract class TestTypedCollection extends BulkTest {

    public TestTypedCollection(String name) {
        super(name);
    }


    protected abstract Collection typedCollection();

    protected Class getType() {
        return String.class;
    }


    public void testIllegalAdd() {
        Collection c = typedCollection();
        Integer i = new Integer(3);
        try {
            c.add(i);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains(i));   
    }


    public void testIllegalAddAll() {
        Collection c = typedCollection();
        List elements = new ArrayList();
        elements.add("one");
        elements.add("two");
        elements.add(new Integer(3));
        elements.add("four");
        try {
            c.addAll(elements);
            fail("Integer should fail string predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains("one"));   
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains("two"));   
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains(new Integer(3)));   
        assertTrue("Collection shouldn't contain illegal element", 
         !c.contains("four"));   
    }

}

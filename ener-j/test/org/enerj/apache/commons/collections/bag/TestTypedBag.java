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

import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.apache.commons.collections.Bag;

/**
 * Extension of {@link TestBag} for exercising the {@link TypedBag}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Phil Steitz
 */
public class TestTypedBag extends AbstractTestBag {
    
    public TestTypedBag(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTypedBag.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestTypedBag.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    //--------------------------------------------------------------------------
    
    protected Class stringClass = this.getName().getClass();
    private Object obj = new Object();
    protected Class objectClass = obj.getClass();
    
    protected Bag decorateBag(HashBag bag, Class claz) {
        return TypedBag.decorate(bag, claz);
    }

    public Bag makeBag() {
        return decorateBag(new HashBag(), objectClass);
    }
    
    protected Bag makeTestBag() {
        return decorateBag(new HashBag(), stringClass);
    }
    
    //--------------------------------------------------------------------------

    public void testlegalAddRemove() {
        Bag bag = makeTestBag();
        assertEquals(0, bag.size());
        Object[] els = new Object[] {"1", "3", "5", "7", "2", "4", "1"};
        for (int i = 0; i < els.length; i++) {
            bag.add(els[i]);
            assertEquals(i + 1, bag.size());
            assertEquals(true, bag.contains(els[i]));
        }
        Set set = ((PredicatedBag) bag).uniqueSet();
        assertTrue("Unique set contains the first element",set.contains(els[0]));
        assertEquals(true, bag.remove(els[0])); 
        set = ((PredicatedBag) bag).uniqueSet();
        assertTrue("Unique set now does not contain the first element",
            !set.contains(els[0])); 
    }
 
    public void testIllegalAdd() {
        Bag bag = makeTestBag();
        Integer i = new Integer(3);
        try {
            bag.add(i);
            fail("Integer should fail type check.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't contain illegal element", 
         !bag.contains(i));   
    }

    public void testIllegalDecorate() {
        HashBag elements = new HashBag();
        elements.add("one");
        elements.add("two");
        elements.add(new Integer(3));
        elements.add("four");
        try {
            Bag bag = decorateBag(elements, stringClass);
            fail("Bag contains an element that should fail the type test.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            Bag bag = decorateBag(new HashBag(), null);
            fail("Expectiing IllegalArgumentException for null predicate.");
        } catch (IllegalArgumentException e) {
            // expected
        }              
    }

    protected boolean skipSerializedCanonicalTests() {
        return true;
    }

}

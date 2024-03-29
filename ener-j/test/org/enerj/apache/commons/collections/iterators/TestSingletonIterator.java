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
package org.enerj.apache.commons.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.apache.commons.collections.ResettableIterator;

/**
 * Tests the SingletonIterator to ensure that the next() method will actually
 * perform the iteration rather than the hasNext() method.
 *
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author James Strachan
 */
public class TestSingletonIterator extends AbstractTestIterator {

    private static final Object testValue = "foo";
    
    public static Test suite() {
        return new TestSuite(TestSingletonIterator.class);
    }
    
    public TestSingletonIterator(String testName) {
        super(testName);
    }
    
    /**
     * Returns a SingletonIterator from which 
     * the element has already been removed.
     */
    public Iterator makeEmptyIterator() {
        SingletonIterator iter = (SingletonIterator)makeFullIterator();
        iter.next();
        iter.remove();        
        iter.reset();
        return iter;
    }

    public Iterator makeFullIterator() {
        return new SingletonIterator( testValue );
    }

    public boolean supportsRemove() {
        return true;
    }

    public boolean supportsEmptyIterator() {
        return true;
    }

    public void testIterator() {
        Iterator iter = (Iterator) makeObject();
        assertTrue("Iterator has a first item", iter.hasNext());

        Object iterValue = iter.next();
        assertEquals("Iteration value is correct", testValue, iterValue);

        assertTrue("Iterator should now be empty", !iter.hasNext());

        try {
            iter.next();
        } catch (Exception e) {
            assertTrue(
                "NoSuchElementException must be thrown",
                e.getClass().equals((new NoSuchElementException()).getClass()));
        }
    }
    
    public void testSingletonIteratorRemove() {
        ResettableIterator iter = new SingletonIterator("xyzzy");
        assertTrue(iter.hasNext());
        assertEquals("xyzzy",iter.next());
        iter.remove();
        iter.reset();
        assertTrue(! iter.hasNext());
    }
    
    public void testReset() {
        ResettableIterator it = (ResettableIterator) makeObject();
        
        assertEquals(true, it.hasNext());
        assertEquals(testValue, it.next());
        assertEquals(false, it.hasNext());

        it.reset();
        
        assertEquals(true, it.hasNext());
        assertEquals(testValue, it.next());
        assertEquals(false, it.hasNext());
        
        it.reset();
        it.reset();
        
        assertEquals(true, it.hasNext());
    }
    
}

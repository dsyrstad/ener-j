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
package org.enerj.apache.commons.collections.set;

import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Test;

import org.enerj.apache.commons.collections.BulkTest;


/**
 * Extension of {@link AbstractTestSortedSet} for exercising the 
 * {@link TypedSortedSet} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Phil Steitz
 */
public class TestTypedSortedSet extends AbstractTestSortedSet{
    
    public TestTypedSortedSet(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return BulkTest.makeSuite(TestTypedSortedSet.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestTypedSortedSet.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
 //-------------------------------------------------------------------      
    protected Class integerType = new Integer(0).getClass();
    
    public Set makeEmptySet() {
        return TypedSortedSet.decorate(new TreeSet(), integerType);
    }
    
    public Set makeFullSet() {
        TreeSet set = new TreeSet();
        set.addAll(Arrays.asList(getFullElements()));
        return TypedSortedSet.decorate(set, integerType);
    }
   
    
//--------------------------------------------------------------------            
    protected Long getNextAsLong() {
        SortedSet set = (SortedSet) makeFullSet();
        int nextValue = ((Integer)set.last()).intValue() + 1;
        return new Long(nextValue);
    }
    
    protected Integer getNextAsInt() {
        SortedSet set = (SortedSet) makeFullSet();
        int nextValue = ((Integer)set.last()).intValue() + 1;
        return new Integer(nextValue);
    }
           
    public void testIllegalAdd() {
        Set set = makeFullSet();
        try {
            set.add(getNextAsLong());
            fail("Should fail type test.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't convert long to int", 
         !set.contains(getNextAsInt()));   
    }

    public void testIllegalAddAll() {
        Set set = makeFullSet();
        Set elements = new TreeSet();
        elements.add(getNextAsLong());
        try {
            set.addAll(elements);
            fail("Should fail type test.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue("Collection shouldn't convert long to int", 
         !set.contains(getNextAsInt()));  
    }       

    public boolean skipSerializedCanonicalTests() {
        return true;  // Typed and Predicated get confused
    }

}

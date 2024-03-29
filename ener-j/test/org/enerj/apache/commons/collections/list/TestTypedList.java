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
package org.enerj.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Extension of {@link TestList} for exercising the {@link TypedList}
 * implementation.
 *
 * @since Commons Collections 3.1
 * @version $Revision: 155406 $ $Date: 2005-02-26 12:55:26 +0000 (Sat, 26 Feb 2005) $
 * 
 * @author Stephen Colebourne
 */
public class TestTypedList extends AbstractTestList {
    
    public TestTypedList(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTypedList.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestTypedList.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public Collection makeConfirmedCollection() {
        return new ArrayList();
    }

    public List makeEmptyList() {
        return TypedList.decorate(new ArrayList(), Object.class);
    }

    public boolean isNullSupported() {
        return false;
    }

    public boolean skipSerializedCanonicalTests() {
        return true;  // TypedList and PredicatedList get confused
    }

}

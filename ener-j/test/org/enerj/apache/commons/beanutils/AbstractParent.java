/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.enerj.apache.commons.beanutils;

public abstract class AbstractParent {
    
    private Child child;
    
    public Child getChild()
    {
        return child;
    }

    /**
     * Method which matches signature but which has wrong parameters 
     */
    public String testAddChild(String badParameter) {
        return null;
    }

    /**
     * Method which matches signature but which has wrong parameters 
     */
    public String testAddChild2(String ignore, String badParameter) {
        return null;
    }
    
    public String testAddChild(Child child) {
        this.child = child;
        return child.getName();
    }
    

    public String testAddChild2(String ignore, Child child) {
        this.child = child;
        return child.getName();
    }

}

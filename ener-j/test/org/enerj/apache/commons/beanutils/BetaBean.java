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

public class BetaBean extends AbstractChild {
    
    private String secret = "utah";
    
    public String getSecret() {
        return secret;
    }
    
    public void setNoGetterProperty(String secret) {
        this.secret = secret;
    }
    
    public void setNoGetterMappedProperty(String secret, String key) {
        this.secret = "MAP:" + secret;
    }
    
    public BetaBean(String name) {
        setName(name);
    }
}

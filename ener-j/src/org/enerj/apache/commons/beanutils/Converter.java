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


/**
 * <p>General purpose data type converter that can be registered and used
 * within the BeanUtils package to manage the conversion of objects from
 * one type to another.
 *
 * @author Craig McClanahan
 * @author Paulo Gaspar
 * @version $Revision: 1.7 $ $Date: 2004/02/28 13:18:33 $
 * @since 1.3
 */

public interface Converter {


    /**
     * Convert the specified input object into an output object of the
     * specified type.
     *
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     *
     * @exception ConversionException if conversion cannot be performed
     *  successfully
     */
    public Object convert(Class type, Object value);


}

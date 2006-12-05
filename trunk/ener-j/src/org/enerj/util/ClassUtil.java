/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
//Ener-J
//Copyright 2001-2004 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/ClassUtil.java,v 1.9 2006/05/31 01:58:22 dsyrstad Exp $

// Portions of this code were derived from OGNL:

//	Copyright (c) 1998-2004, Drew Davidson and Luke Blanshard
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions are
//  met:
//
//  Redistributions of source code must retain the above copyright notice,
//  this list of conditions and the following disclaimer.
//  Redistributions in binary form must reproduce the above copyright
//  notice, this list of conditions and the following disclaimer in the
//  documentation and/or other materials provided with the distribution.
//  Neither the name of the Drew Davidson nor the names of its contributors
//  may be used to endorse or promote products derived from this software
//  without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
//  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
//  FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
//  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
//  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
//  OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
//  AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
//  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
//  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
//  DAMAGE.


package org.enerj.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Static Class utilities. 
 * 
 * @version $Id: ClassUtil.java,v 1.9 2006/05/31 01:58:22 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ClassUtil
{


    // Don't allow construction
    private ClassUtil()
    {
    }


    /**
     * Attempts to find a public method on the given class, searching its superclasses if necessary.
     * 
     * @param aClass the class.
     * @param aMethodName the method name to search for.
     * @param someParamTypes an array of parameter types for the method. Should be an empty array if there
     *  are no parameters.
     * @param anAltName1 an alternate method name (e.g., "getX"). May be null.
     * @param anAltName2 another alternate method name (e.g., "isX"). May be null.
     * 
     * @return the Method, or null if no method can be found.
     */
    public static Method findMethod(Class aClass, String aMethodName, Class[] someParamTypes, String anAltName1,
                    String anAltName2)
    {
        Method method;
        
        if ((method = findMostSpecificMethod(aClass, aMethodName, someParamTypes)) != null) {
            return method;
        }

        if (anAltName1 != null && (method = findMostSpecificMethod(aClass, anAltName1, someParamTypes)) != null) {
            return method;
        }

        if (anAltName2 != null && (method = findMostSpecificMethod(aClass, anAltName2, someParamTypes)) != null) {
            return method;
        }

        return null;
    }


    /**
     * Attempts to find a public field on the given class, searching its superclasses if necessary.
     * 
     * @param aClass the class.
     * @param aFieldName the field name to search for.
     * 
     * @return the Field, or null if no field can be found.
     */
    public static Field findField(Class aClass, String aFieldName)
    {
        try {
            return aClass.getField(aFieldName);
        }
        catch (NoSuchFieldException e) {
            // Ignore - return null.
        }

        return null;
    }

    /**
     * Get all of the declared fields for all classes in the class hierarchy. This is like
     * {@link Class#getDeclaredFields()}, but does it for the entire class heirarchy. 
     * 
     * @param aClass the class.
     * 
     * @return the array of Fields. 
     */
    public static List<Field> getAllDeclaredFields(Class aClass)
    {
        Set<Class> superTypes = new HashSet<Class>();
        superTypes.add(aClass);
        getAllSuperTypes(aClass, superTypes);
        List<Field> fieldList = new ArrayList<Field>(50);
        for (Class targetClass : superTypes) {
            for (Field field : targetClass.getDeclaredFields()) {
                field.setAccessible(true);
                fieldList.add(field);
            }
        }
        
        return fieldList;
    }
    
    /**
     * Maps the given class to one of the primitve class types if aClass is one of the wrapper types: 
     * Boolean, Integer, Double, Byte, Long, Float, Short, or Character.
     *
     * @param aClass the class to be mapped.
     * 
     * @return the primitive Class, or aClass if it was not one of the wrapper types.
     */
    public static final Class mapToPrimitiveType(Class aClass)
    {
        if (aClass == null) {
            return null;
        }
        
        if (aClass == Boolean.class) {
            return Boolean.TYPE;
        }
        
        if (Number.class.isAssignableFrom(aClass)) {
            if (aClass == Integer.class) {
                return Integer.TYPE;
            }
            
            if (aClass == Double.class) {
                return Double.TYPE;
            }
            
            if (aClass == Byte.class) {
                return Byte.TYPE;
            }
            
            if (aClass == Long.class) {
                return Long.TYPE;
            }
            
            if (aClass == Float.class) {
                return Float.TYPE;
            }
            
            if (aClass == Short.class) {
                return Short.TYPE;
            }
        }
        
        if (aClass == Character.class) {
            return Character.TYPE;
        }

        return aClass;
    }


    /**
     * Maps the given class from one of the primitve class types to one of the wrapper types:  
     * Boolean, Integer, Double, Byte, Long, Float, Short, or Character.
     *
     * @param aClass the class to be mapped.
     * 
     * @return the wrapper Class, or aClass if it was not a primitive type.
     */
    public static final Class mapFromPrimitiveType(Class aClass)
    {
        if (aClass == null) {
            return null;
        }
        
        if (aClass == Boolean.TYPE) {
            return Boolean.class;
        }
        
        if (aClass == Integer.TYPE) {
            return Integer.class;
        }
        
        if (aClass == Double.TYPE) {
            return Double.class;
        }
        
        if (aClass == Byte.TYPE) {
            return Byte.class;
        }
        
        if (aClass == Long.TYPE) {
            return Long.class;
        }
        
        if (aClass == Float.TYPE) {
            return Float.class;
        }
        
        if (aClass == Short.TYPE) {
            return Short.class;
        }

        if (aClass == Character.TYPE) {
            return Character.class;
        }

        return aClass;
    }


    /**
     * Tells whether the given type is compatible with the given target type 
     * -- that is, whether the given object can be passed as an argument
     * to a method or constructor whose parameter type is the given class.
     * If object is null this will return true because null is compatible
     * with any type.
     */
    public static final boolean isTypeCompatible(Class aType, Class aTargetType)
    {
        if (aType != null) {
            if (aTargetType.isPrimitive()) {
                if (mapToPrimitiveType(aType) != aTargetType) {
                    return false;
                }
            }
            else if (!aTargetType.isAssignableFrom(aType)) {
                return false;
            }
        }

        return true;
    }


    /**
     * Tells whether the given array of types are compatible with the given array of
     * target types -- that is, whether the given array of objects can be passed as arguments
     * to a method or constructor whose parameter types are the given array of classes.
     */
    public static final boolean areArgsCompatible(Class[] someArgTypes, Class[] targetArgTypes)
    {
        if (someArgTypes.length != targetArgTypes.length) {
            return false;
        }

        for (int index = 0; index < someArgTypes.length; ++index) {
            if (!isTypeCompatible(someArgTypes[index], targetArgTypes[index])) {
                return false;
            }
        }
        
        return true;
    }


    /**
     * Tells whether the first array of classes is more specific than the second.
     * Assumes that the two arrays are of the same length.
     */
    public static final boolean isMoreSpecific(Class[] classes1, Class[] classes2)
    {
        for (int index = 0, count = classes1.length; index < count; ++index) {
            Class c1 = classes1[index], c2 = classes2[index];
            if (c1 == c2) {
                continue;
            }
            else if (c1.isPrimitive()) {
                return true;
            }
            else if (c1.isAssignableFrom(c2)) {
                return false;
            }
            else if (c2.isAssignableFrom(c1)) {
                return true;
            }
        }

        // They are the same!  So the first is not more specific than the second.
        return false;
    }


    /**
     * Gets the most specific method to be called for the given class, method name and arguments types.
     * It attempts to find the method with the most "specific" argument type match (in JLS terms). 
     * If successful this method will return the Method that can be called, otherwise null.
     */
    public static Method findMostSpecificMethod(Class aClass, String aMethodName, Class[] someArgTypes)
    {
        Method result = null;
        Class[] resultParameterTypes = null;
        Method[] methods = aClass.getMethods();

        for (Method method : methods) {
            if (method.getName().equals(aMethodName)) {
                Class[] methodParamTypes = method.getParameterTypes();
    
                if (areArgsCompatible(someArgTypes, methodParamTypes)
                    && ((result == null) || isMoreSpecific(methodParamTypes, resultParameterTypes))) {
                    result = method;
                    resultParameterTypes = methodParamTypes;
                }
            }
        }

        return result;
    }


    /**
     * Gets the most specific constructor to be called for the given class and arguments types.
     * It attempts to find the constructor with the most "specific" argument type match (in JLS terms). 
     * If successful this method will return the Constructor that can be called, otherwise null.
     */
    public static Constructor findMostSpecificConstructor(Class aClass, Class[] someArgTypes)
    {
        Constructor result = null;
        Class[] resultParameterTypes = null;
        Constructor[] xtors = aClass.getConstructors();

        for (Constructor xtor : xtors) {
            Class[] xtorParamTypes = xtor.getParameterTypes();

            if (areArgsCompatible(someArgTypes, xtorParamTypes)
                && ((result == null) || isMoreSpecific(xtorParamTypes, resultParameterTypes))) {
                result = xtor;
                resultParameterTypes = xtorParamTypes;
            }
        }

        return result;
    }

    

    /**
     * Gets the base class name of a Class.
     * 
     * @param aClass the class.
     *
     * @return the base class name (the name without the package name).
     */
    public static String getBaseClassName(Class aClass) 
    {
        String baseClassName = aClass.getName();
        int idx = baseClassName.lastIndexOf('.');
        if (idx >= 0) {
            baseClassName = baseClassName.substring(idx + 1);
        }

        return baseClassName;
    }


    /**
     * Get the property descriptors for an object. Does not return the "class" property.
     *
     * @param object the object.
     * 
     * @return an array of property descriptors. An empty array is return if introspection fails.
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Object object)
    {
        PropertyDescriptor[] propertyDescriptors;
        Class beanClass = object.getClass();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, null);
            propertyDescriptors = beanInfo.getPropertyDescriptors();
        }
        catch (java.beans.IntrospectionException e) {
            return new PropertyDescriptor[0];
        }

        PropertyDescriptor[] returnDescriptors = new PropertyDescriptor[ propertyDescriptors.length - 1 ];
        int ri = 0;
        for (int i = 0; i < propertyDescriptors.length; i++) {
            if ( !propertyDescriptors[i].getName().equals("class")) {
                returnDescriptors[ri] = propertyDescriptors[i];
                ++ri;
            }
        }
        
        return returnDescriptors;
    }


    /**
     * Gets the bytecode bytes for the named class. The class is found via the classpath, but it
     * is <em>not</em> loaded as a Java Class.
     *
     * @param aClassName the class name to be read.
     * 
     * @return the bytecode for the class.
     * 
     * @throws ClassNotFoundException if there is an error finding or reading the class. 
     */
    public static byte[] getBytecode(String aClassName) throws ClassNotFoundException
    {
        InputStream inStream = getClassAsStream(aClassName);
        
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8192);
            byte[] buf = new byte[8192];
            int n;
            while ( (n = inStream.read(buf)) != -1) {
                byteStream.write(buf, 0, n);
            }
            
            byteStream.close();
            return byteStream.toByteArray();
        }
        catch (IOException e) {
            throw new ClassNotFoundException("Error reading class " + aClassName, e);
        }
        finally {
            try {
                inStream.close();
            }
            catch (IOException e) { 
                // Ignore
            }
        }
        
    }


    /**
     * Gets the bytecode InputStream for the named class. The class is found via the classpath, but it
     * is <em>not</em> loaded as a Java Class.
     *
     * @param aClassName the class name to be read.
     * 
     * @return the bytecode InputStream for the class. This stream must be closed after you are done using it.
     * 
     * @throws ClassNotFoundException if there is an error finding the class. 
     */
    public static InputStream getClassAsStream(String aClassName) throws ClassNotFoundException
    {
        String slashified = '/' + aClassName.replace('.', '/') + ".class";
        InputStream inStream = ClassUtil.class.getResourceAsStream(slashified);
        if (inStream == null) {
            throw new ClassNotFoundException("Cannot find class " + aClassName + " in the classpath");
        }
        
        return inStream;
    }
    

    /**
     * Gets all super-type names (super classes and super interfaces) of aClass and
     * return them in an array. 
     *
     * @param aClass the class to be examined.
     * 
     * @return an array of super-type names. This array will be zero-length if 
     *  no super-types exist.
     */
    public static String[] getAllSuperTypeNames(Class aClass)
    {
        Set<Class> types = new HashSet<Class>();
        getAllSuperTypes(aClass, types);
        String[] typeNames = new String[ types.size() ];
        Iterator<Class> iter = types.iterator();
        for (int i = 0; i < typeNames.length; i++) {
            typeNames[i] = iter.next().getName();
        }
        
        return typeNames;
    }
    

    /**
     * Gets all super-types (super classes and super interfaces) of aClass and
     * return them in an array. 
     *
     * @param aClass the class to be examined.
     * 
     * @return an array of super-types. This array will be zero-length if 
     *  no super-types exist.
     */
    public static Class[] getAllSuperTypes(Class aClass)
    {
        Set<Class> types = new HashSet<Class>();
        getAllSuperTypes(aClass, types);
        return types.toArray(new Class[ types.size() ]);
    }

    

    /**
     * Gets all super-types (super classes and super interfaces) of aClass and
     * adds them to the allSuperTypes set. 
     *
     * @param aClass the class to be examined.
     * @param allSuperTypes the set that will receive the super-types.
     */
    public static void getAllSuperTypes(Class aClass, Set<Class> allSuperTypes)
    {
        Class superType = aClass.getSuperclass();
        if (superType != null) {
            allSuperTypes.add(superType);
            getAllSuperTypes(superType, allSuperTypes); 
        }

        for (Class intf : aClass.getInterfaces()) {
            allSuperTypes.add(intf);
            getAllSuperTypes(intf, allSuperTypes); 
        }
    }
}

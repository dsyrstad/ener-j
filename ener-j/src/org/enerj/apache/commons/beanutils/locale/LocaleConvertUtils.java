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
 
package org.enerj.apache.commons.beanutils.locale;

import org.enerj.apache.commons.collections.FastHashMap;

import java.util.Locale;

/**
 * <p>Utility methods for converting locale-sensitive String scalar values to objects of the
 * specified Class, String arrays to arrays of the specified Class and
 * object to locale-sensitive String scalar value.</p>
 *
 * <p>The implementations for these method are provided by {@link LocaleConvertUtilsBean}.
 * These static utility method use the default instance. More sophisticated can be provided
 * by using a <code>LocaleConvertUtilsBean</code> instance.</p>
 *
 * @author Yauheny Mikulski
 */
public class LocaleConvertUtils {

    // ----------------------------------------------------- Instance Variables

    /**
     * <p>Gets the <code>Locale</code> which will be used when 
     * no <code>Locale</code> is passed to a method.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#getDefaultLocale()
     */
    public static Locale getDefaultLocale() {

        return LocaleConvertUtilsBean.getInstance().getDefaultLocale();
    }

    /**
     * <p>Sets the <code>Locale</code> which will be used when 
     * no <code>Locale</code> is passed to a method.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#setDefaultLocale(Locale)
     */
    public static void setDefaultLocale(Locale locale) {

        LocaleConvertUtilsBean.getInstance().setDefaultLocale(locale);
    }

    /**
     * <p>Gets applyLocalized.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#getApplyLocalized()
     */
    public static boolean getApplyLocalized() {
        return LocaleConvertUtilsBean.getInstance().getApplyLocalized();
    }

    /**
     * <p>Sets applyLocalized.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#setApplyLocalized(boolean)
     */
    public static void setApplyLocalized(boolean newApplyLocalized) {
        LocaleConvertUtilsBean.getInstance().setApplyLocalized(newApplyLocalized);
    }

    // --------------------------------------------------------- Methods

    /**
     * <p>Convert the specified locale-sensitive value into a String.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(Object)
     */
    public static String convert(Object value) {
        return LocaleConvertUtilsBean.getInstance().convert(value);
    }

    /**
     * <p>Convert the specified locale-sensitive value into a String
     * using the convertion pattern.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(Object, String)
     */
    public static String convert(Object value, String pattern) {
        return LocaleConvertUtilsBean.getInstance().convert(value, pattern);
    }

    /**
     * <p>Convert the specified locale-sensitive value into a String
     * using the paticular convertion pattern.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(Object, Locale, String)
     */
    public static String convert(Object value, Locale locale, String pattern) {

        return LocaleConvertUtilsBean.getInstance().convert(value, locale, pattern);
    }

    /**
     * <p>Convert the specified value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(String, Class)
     */
    public static Object convert(String value, Class clazz) {

        return LocaleConvertUtilsBean.getInstance().convert(value, clazz);
    }

    /**
     * <p>Convert the specified value to an object of the specified class (if
     * possible) using the convertion pattern. Otherwise, return a String
     * representation of the value.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(String, Class, String)
     */
    public static Object convert(String value, Class clazz, String pattern) {

        return LocaleConvertUtilsBean.getInstance().convert(value, clazz, pattern);
    }

    /**
     * <p>Convert the specified value to an object of the specified class (if
     * possible) using the convertion pattern. Otherwise, return a String
     * representation of the value.</p>
     *
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(String, Class, Locale, String)
     */
    public static Object convert(String value, Class clazz, Locale locale, String pattern) {

        return LocaleConvertUtilsBean.getInstance().convert(value, clazz, locale, pattern);
    }

    /**
     * <p>Convert an array of specified values to an array of objects of the
     * specified class (if possible) using the convertion pattern.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(String[], Class, String)
     */
    public static Object convert(String values[], Class clazz, String pattern) {

        return LocaleConvertUtilsBean.getInstance().convert(values, clazz, pattern);
    }

   /**
    * <p>Convert an array of specified values to an array of objects of the
    * specified class (if possible).</p>
    * 
    * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
    *
    * @see LocaleConvertUtilsBean#convert(String[], Class)
    */
   public static Object convert(String values[], Class clazz) {

       return LocaleConvertUtilsBean.getInstance().convert(values, clazz);
   }

    /**
     * <p>Convert an array of specified values to an array of objects of the
     * specified class (if possible) using the convertion pattern.</p>
     *
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#convert(String[], Class, Locale, String)
     */
    public static Object convert(String values[], Class clazz, Locale locale, String pattern) {

        return LocaleConvertUtilsBean.getInstance().convert(values, clazz, locale, pattern);
    }

    /**
     * <p>Register a custom {@link LocaleConverter} for the specified destination
     * <code>Class</code>, replacing any previously registered converter.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#register(LocaleConverter, Class, Locale)
     */
    public static void register(LocaleConverter converter, Class clazz, Locale locale) {

        LocaleConvertUtilsBean.getInstance().register(converter, clazz, locale);
    }

    /**
     * <p>Remove any registered {@link LocaleConverter}.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#deregister()
     */
    public static void deregister() {

       LocaleConvertUtilsBean.getInstance().deregister();
    }


    /**
     * <p>Remove any registered {@link LocaleConverter} for the specified locale.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#deregister(Locale)
     */
    public static void deregister(Locale locale) {

        LocaleConvertUtilsBean.getInstance().deregister(locale);
    }


    /**
     * <p>Remove any registered {@link LocaleConverter} for the specified locale and Class.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#deregister(Class, Locale)
     */
    public static void deregister(Class clazz, Locale locale) {

        LocaleConvertUtilsBean.getInstance().deregister(clazz, locale);
    }

    /**
     * <p>Look up and return any registered {@link LocaleConverter} for the specified
     * destination class and locale; if there is no registered Converter, return
     * <code>null</code>.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#lookup(Class, Locale)
     */
    public static LocaleConverter lookup(Class clazz, Locale locale) {

        return LocaleConvertUtilsBean.getInstance().lookup(clazz, locale);
    }

    /**
     * <p>Look up and return any registered FastHashMap instance for the specified locale.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#lookup(Locale)
     */
    protected static FastHashMap lookup(Locale locale) {
        return LocaleConvertUtilsBean.getInstance().lookup(locale);
    }

    /**
     * <p>Create all {@link LocaleConverter} types for specified locale.</p>
     * 
     * <p>For more details see <code>LocaleConvertUtilsBean</code></p>
     *
     * @see LocaleConvertUtilsBean#create(Locale)
     */
    protected static FastHashMap create(Locale locale) {

        return LocaleConvertUtilsBean.getInstance().create(locale);
    }
}

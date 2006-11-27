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
//$Header: /cvsroot/ener-j/ener-j/test/org/enerj/util/StringifierTest.java,v 1.4 2006/01/12 23:36:20 dsyrstad Exp $

package org.enerj.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests Stringifier.
 * 
 * @version $Id: StringifierTest.java,v 1.4 2006/01/12 23:36:20 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class StringifierTest extends TestCase 
{

    public StringifierTest(String aTestName) 
    {
        super(aTestName);
    }

    public static Test suite() 
    {
        return new TestSuite(StringifierTest.class);
    }

    public void testToString()
    {
        TestClass s = new TestClass();
        System.out.println(StringUtil.toString(s, true, true).replaceAll("@[0-9]*", "") );
        System.out.println(StringUtil.toString(s, false, true).replaceAll("@[0-9]*", "") );
        System.out.println(StringUtil.toString(s, true, false).replaceAll("@[0-9]*", "") );
        System.out.println(StringUtil.toString(s, false, false) );

        String result = StringUtil.toString(s, true, true);
        assertTrue("has hashcodes", result.indexOf('@') >= 0);
        result = result.replaceAll("[@;][a-f0-9]+", "");
        assertEquals("true, true", result, "org.enerj.util.StringifierTest$TestClass{intField=5, boolField=true, strField=A String, integerField=5, list=[2, A String in list, 3], map={Key1=Value1, Key2=Value2}, strArray=[Ljava.lang.String;, nullObj=null}");

        result = StringUtil.toString(s, false, true);
        assertTrue("has hashcodes", result.indexOf('@') >= 0);
        result = result.replaceAll("[@;][a-f0-9]+", "");
        assertEquals("false, true", result, "org.enerj.util.StringifierTest$TestClass{intField=5, boolField=true, strField=A String, integerField=5, list=[2, A String in list, 3], map={Key1=Value1, Key2=Value2}, strArray=[Ljava.lang.String;, nullObj=null}");

        result = StringUtil.toString(s, true, false);
        assertTrue("has hashcodes", result.indexOf('@') >= 0);
        result = result.replaceAll("[@;][a-f0-9]+", "");
        assertEquals("true, false", result, "org.enerj.util.StringifierTest$TestClass{intField=5, boolField=true, strField=\"A String\", integerField=5, list=java.util.Arrays$ArrayList[2, \"A String in list\", 3], map=java.util.HashMap{[key=\"Key1\", value=\"Value1\"], [key=\"Key2\", value=\"Value2\"]}, strArray=[\"ArrayStr1\", \"ArrayStr2\", \"ArrayStr3\"], nullObj=null}");

        result = StringUtil.toString(s, false, false);
        assertTrue("has no hashcodes", result.indexOf('@') < 0);
        assertEquals("false, false", result, "org.enerj.util.StringifierTest$TestClass{intField=5, boolField=true, strField=\"A String\", integerField=5, list=java.util.Arrays$ArrayList[2, \"A String in list\", 3], map=java.util.HashMap{[key=\"Key1\", value=\"Value1\"], [key=\"Key2\", value=\"Value2\"]}, strArray=[\"ArrayStr1\", \"ArrayStr2\", \"ArrayStr3\"], nullObj=null}");
        
        s = new TestSubClass();

        System.out.println(StringUtil.toString(s, false, false) );
        assertEquals("sub-class false, false", StringUtil.toString(s, false, false), "org.enerj.util.StringifierTest$TestSubClass{subClassField=\"Subclass field\", .intField=5, .boolField=true, .strField=\"A String\", .integerField=5, .list=java.util.Arrays$ArrayList[2, \"A String in list\", 3], .map=java.util.HashMap{[key=\"Key1\", value=\"Value1\"], [key=\"Key2\", value=\"Value2\"]}, .strArray=[\"ArrayStr1\", \"ArrayStr2\", \"ArrayStr3\"], .nullObj=null}");
    }



    private static class TestClass
    {
        private int intField = 5;
        private boolean boolField = true;
        private String strField = "A String";
        private Integer integerField = new Integer(5);
        private List list;
        private Map map;
        private String[] strArray = { "ArrayStr1", "ArrayStr2", "ArrayStr3" };
        private Object nullObj = null;
        
        private TestClass() {
            list = Arrays.asList(new Object[] { new Integer(2), "A String in list", new Long(3) } );
            map = new HashMap();
            map.put("Key1", "Value1");
            map.put("Key2", "Value2");
        }
    }



    private static class TestSubClass extends TestClass 
    {
        private String subClassField = "Subclass field"; 
        
        private TestSubClass() {
        }
    }
}

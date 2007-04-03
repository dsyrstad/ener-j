/*******************************************************************************
 * Copyright 2000, 2007 Visual Systems Corporation.
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
//$Header: $

package org.enerj.core;

import org.enerj.annotations.Index;
import org.enerj.annotations.Indexes;

import junit.framework.TestCase;

/**
 * Tests GenericKey. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class GenericKeyTest extends TestCase
{
    private Indexes mIndexesAnn = Employee.class.getAnnotation(Indexes.class);
    private IndexSchema mIndexSchemaEmplNum = new IndexSchema(mIndexesAnn.value()[0], null);
    private IndexSchema mIndexSchemaComposite = new IndexSchema(mIndexesAnn.value()[1], null);
    private IndexSchema mIndexSchemaComposite2 = new IndexSchema(mIndexesAnn.value()[2], null);
    private Employee mEmployee1 = new Employee(5, "Dan", "Syrstad", new Address("Mpls", "MN"));
    private Employee mEmployee1a = new Employee(5, "Dan", "Syrstad", new Address("Mpls", "MN"));
    private Employee mEmployee2 = new Employee(6, "Zed", "Smith", new Address("Zumbra", "MN"));
    
    /**
     * Construct a GenericKeyTest. 
     *
     * @param name
     */
    public GenericKeyTest(String name)
    {
        super(name);
    }
    
    /**
     * Test method for {@link org.enerj.core.GenericKey#hashCode()}.
     */
    public void testHashCode()
    {
        GenericKey key1 = new GenericKey(mIndexSchemaEmplNum, mEmployee1);
        GenericKey key1a = new GenericKey(mIndexSchemaEmplNum, mEmployee1a);
        GenericKey key2 = new GenericKey(mIndexSchemaEmplNum, mEmployee2);
        assertEquals( key1.hashCode(), key1a.hashCode() );
        assertTrue( key2.hashCode() != key1.hashCode() );
    }

    /**
     * Test method for {@link org.enerj.core.GenericKey#compare(org.enerj.core.GenericKey, org.enerj.core.GenericKey)}.
     */
    public void testCompare()
    {
        GenericKey key1 = new GenericKey(mIndexSchemaEmplNum, mEmployee1);
        GenericKey key2 = new GenericKey(mIndexSchemaEmplNum, mEmployee2);
        assertTrue( key1.compare(key1, key2) < 0);
        assertTrue( key2.compare(key2, key1) > 0);
        assertTrue( key1.compare(key1, key1) == 0);

        GenericKey key3 = new GenericKey(mIndexSchemaComposite, mEmployee1);
        GenericKey key4 = new GenericKey(mIndexSchemaComposite, mEmployee2);
        assertTrue( key3.compare(key3, key4) < 0);
        assertTrue( key4.compare(key4, key3) > 0);
        assertTrue( key3.compare(key3, key3) == 0);

        GenericKey key5 = new GenericKey(mIndexSchemaComposite2, mEmployee1);
        GenericKey key6 = new GenericKey(mIndexSchemaComposite2, mEmployee2);
        assertTrue( key5.compare(key5, key6) > 0);
        assertTrue( key6.compare(key6, key5) < 0);
        assertTrue( key5.compare(key5, key5) == 0);
    }

    /**
     * Test method for {@link org.enerj.core.GenericKey#compareTo(org.enerj.core.GenericKey)}.
     */
    public void testCompareTo()
    {
        GenericKey key1 = new GenericKey(mIndexSchemaEmplNum, mEmployee1);
        GenericKey key2 = new GenericKey(mIndexSchemaEmplNum, mEmployee2);
        assertTrue( key1.compareTo(key2) < 0);
        assertTrue( key2.compareTo(key1) > 0);
        assertTrue( key1.compareTo(key1) == 0);

        GenericKey key3 = new GenericKey(mIndexSchemaComposite, mEmployee1);
        GenericKey key4 = new GenericKey(mIndexSchemaComposite, mEmployee2);
        assertTrue( key3.compareTo(key4) < 0);
        assertTrue( key4.compareTo(key3) > 0);
        assertTrue( key3.compareTo(key3) == 0);

        GenericKey key5 = new GenericKey(mIndexSchemaComposite2, mEmployee1);
        GenericKey key6 = new GenericKey(mIndexSchemaComposite2, mEmployee2);
        assertTrue( key5.compareTo(key6) > 0);
        assertTrue( key6.compareTo(key5) < 0);
        assertTrue( key5.compareTo(key5) == 0);
    }

    /**
     * Test method for {@link org.enerj.core.GenericKey#equals(java.lang.Object)}.
     */
    public void testEqualsObject()
    {
        GenericKey key1 = new GenericKey(mIndexSchemaEmplNum, mEmployee1);
        GenericKey key1a = new GenericKey(mIndexSchemaEmplNum, mEmployee1a);
        GenericKey key2 = new GenericKey(mIndexSchemaEmplNum, mEmployee2);
        assertTrue( key1.equals(key1a) );
        assertFalse( key2.equals(key1) );
    }

    

    @Indexes( {
        @Index(type=Index.Type.BTree, name="emplNum", properties={ "employeeNumber" } ),
        @Index(type=Index.Type.BTree, name="composite", properties={ "firstName", "lastName", "address.city" } ),
        @Index(type=Index.Type.BTree, name="composite2", properties={ "lastName", "firstName", "address.city" } ),
    } )
    public static final class Employee 
    {
        private int employeeNumber;
        private String firstName;
        private String lastName;
        private Address address;
        
        public Employee(int someEmployeeNumber, String someFirstName, String someLastName, Address someAddress)
        {
            employeeNumber = someEmployeeNumber;
            firstName = someFirstName;
            lastName = someLastName;
            address = someAddress;
        }

        public Address getAddress()
        {
            return address;
        }
        
        public int getEmployeeNumber()
        {
            return employeeNumber;
        }
        
        public String getFirstName()
        {
            return firstName;
        }
        
        public String getLastName()
        {
            return lastName;
        } 
    }

    public static final class Address 
    {
        private String city;
        private String state;
        
        public Address(String someCity, String someState)
        {
            city = someCity;
            state = someState;
        }

        public String getCity()
        {
            return city;
        }
        
        public String getState()
        {
            return state;
        }
    }
}

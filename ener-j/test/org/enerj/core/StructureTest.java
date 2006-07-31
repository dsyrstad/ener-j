// Ener-J
// Copyright 2001-2005 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/StructureTest.java,v 1.1 2005/11/15 23:14:49 dsyrstad Exp $

package org.enerj.core;

import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests Structure functionality.
 *
 * @version $Id: StructureTest.java,v 1.1 2005/11/15 23:14:49 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class StructureTest extends TestCase
{

    //----------------------------------------------------------------------
    public StructureTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(StructureTest.class);
    }
    
    //----------------------------------------------------------------------
    /**
     * Tests hashCode and equals.
     */
    public void testHashCodeAndEquals() throws Exception
    {
        String[] names = new String[] { "a", "b", "c" };

        // Two equivalent objects should hash the same and be equal.
        Structure struct1 = new Structure(names, new Object[] { 202827, 3, 999 } );
        Structure struct2 = new Structure(names, new Object[] { 202827, 3, 999 } );
        
        assertEquals(struct1.hashCode(), struct2.hashCode());
        assertEquals(struct1, struct2);
        assertEquals(struct2, struct1);
        
        // Not equal
        struct2 = new Structure(names, new Object[] { 202827, 3, 998 } );
        assertFalse( struct1.equals(struct2) );
        
        // null is not equal
        assertFalse( struct1.equals(null) );

        // Some other type is not equal
        assertFalse( struct1.equals("not a Structure") );
        
        // Test null member values equal
        struct1 = new Structure(names, new Object[] { 202827, null, 999 } );
        struct2 = new Structure(names, new Object[] { 202827, null, 999 } );
        assertEquals(struct1, struct2);

        // Test null in one member, but not in the other. Should not be equal.
        struct2 = new Structure(names, new Object[] { 202827, 3, null } );
        assertFalse( struct1.equals(struct2) );
        // Vice versa
        assertFalse( struct2.equals(struct1) );
    }

    //----------------------------------------------------------------------
    /**
     * Tests compareTo.
     */
    public void testCompareTo() throws Exception
    {
        String[] names = new String[] { "a", "b", "c" };

        // Two equivalent objects should compare == 0.
        Structure struct1 = new Structure(names, new Object[] { 202827, 3, 999 } );
        Structure struct2 = new Structure(names, new Object[] { 202827, 3, 999 } );
        
        assertTrue(struct1.compareTo(struct2) == 0);
        // And vice versa
        assertTrue(struct2.compareTo(struct1) == 0);
        
        // Less than
        struct2 = new Structure(names, new Object[] { 202827, 3, 1000 } );
        assertTrue( struct1.compareTo(struct2) < 0 );
        // Turn it around and it should be greater than.
        assertTrue( struct2.compareTo(struct1) > 0 );
        
        // null should throw ClassCastException
        try {
            struct1.compareTo(null);
            fail("Expected ClassCastException");
        }
        catch (ClassCastException e) {
            // Expected
        }

        // Test null member values equal, lesser and greater
        struct1 = new Structure(names, new Object[] { 202827, null, 999 } );
        struct2 = new Structure(names, new Object[] { 202827, null, 999 } );
        assertTrue(struct1.compareTo(struct2) == 0);
        // And vice versa
        assertTrue(struct2.compareTo(struct1) == 0);

        // Less than (null is less than non-null).
        struct2 = new Structure(names, new Object[] { 202827, 3, null } );
        assertTrue(struct1.compareTo(struct2) < 0);
        // And vice versa should be greater
        assertTrue(struct2.compareTo(struct1) > 0);
        
        // Test members that don't implement Comparable. Should try equals() first, but if not equal, error.
        Object obj = new Object();
        struct1 = new Structure(names, new Object[] { 202827, obj, 999 } );
        struct2 = new Structure(names, new Object[] { 202827, obj, 999 } );
        // These should be equal because Object implements equals() and they are the same obj.
        assertTrue(struct1.compareTo(struct2) == 0);
        
        Object obj2 = new Object();
        struct2 = new Structure(names, new Object[] { 202827, obj2, 999 } );
        // This should be an error because they're not the same object. equals() fails in this case and so
        // we cannot compare > or <.
        try {
            assertTrue(struct1.compareTo(struct2) == 0);
            fail("Expected IllegalArgumentException");
        } 
        catch (IllegalArgumentException e) {
            // Expected
        }
        
        // Test different number of members. If are all equal up to the smaller structures members, than
        // the structure with more members is greater.
        struct1 = new Structure(names, new Object[] { 202827, 33, 999, 44 } );
        struct2 = new Structure(names, new Object[] { 202827, 33, 999 } );
        assertTrue(struct1.compareTo(struct2) > 0);
        // And vice versa is lesser
        assertTrue(struct2.compareTo(struct1) < 0);
    }

    //----------------------------------------------------------------------
    /**
     * Tests clone.
     */
    public void testClone() throws Exception
    {
        String[] names = new String[] { "a", "b", "c" };

        // Two equivalent objects should compare == 0.
        Structure struct1 = new Structure(names, new Object[] { 202827, 3.1, new BigDecimal("45.99") } );
        Structure struct2 = (Structure)struct1.clone();
        
        String[] names1 = struct1.getMemberNames();
        String[] names2 = struct2.getMemberNames();
        Object[] values1 = struct1.getMemberValues();
        Object[] values2 = struct2.getMemberValues();
        // The lengths should be the same
        assertEquals(names1.length, names2.length);
        assertEquals(values1.length, values2.length);
        
        // struct2 should not have the exact same arrays.
        assertNotSame(names1, names2);
        assertNotSame(values1, values2);
        
        // They should be equal.
        assertEquals(struct1, struct2);
        
        // The names and values should be equal.
        for (int i = 0; i < names1.length; i++) {
            assertEquals(names1[i], names2[i]);
            assertEquals(values1[i], values2[i]);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tests getMemberNames/Values.
     */
    public void testGetMembers() throws Exception
    {
        String[] names = new String[] { "a", "b", "c" };

        // Two equivalent objects should compare == 0.
        Object[] values = new Object[] { 202827, 3.1, new BigDecimal("45.99") };
        Structure struct1 = new Structure(names, values);
        
        String[] names1 = struct1.getMemberNames();
        Object[] values1 = struct1.getMemberValues();
        assertEquals(names.length, names1.length);
        assertEquals(values.length, values1.length);
        
        // The names and values should be equal.
        for (int i = 0; i < names1.length; i++) {
            assertEquals(names[i], names1[i]);
            assertEquals(values[i], values1[i]);
        }
    }
}
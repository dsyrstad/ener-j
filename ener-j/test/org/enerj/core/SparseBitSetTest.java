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
// Ener-J
// Copyright 2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/core/SparseBitSetTest.java,v 1.2 2005/11/02 03:15:08 dsyrstad Exp $

package org.enerj.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Collections;
import java.util.Random;
import java.util.Arrays;
import java.util.List;

/**
 * Tests SparseBitSet.
 *
 * @version $Id: SparseBitSetTest.java,v 1.2 2005/11/02 03:15:08 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class SparseBitSetTest extends TestCase
{
    private static final int TEST_NODE_SIZE = 10;


    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(SparseBitSetTest.class);
    }
    

    public static Test suite() 
    {
        return new TestSuite(SparseBitSetTest.class);
    }
    

    /**
     * Tests set(idx, true/false) randomly across the whole set.
     * Also tests the iterator against the same set.
     */
    public void testSetWithFlagRandom() throws Exception
    {
        int nodeSize = TEST_NODE_SIZE;
        int bitsPerNode = nodeSize * 64;
        
        // Create some random bits to completely fill the set.
        int numBitsToCreate = nodeSize * nodeSize * bitsPerNode;
        int numBitsSet = 0;
        boolean[] bits = new boolean[numBitsToCreate];
        SparseBitSet bitset = new SparseBitSet(nodeSize);
        Random random = new Random(15);
        for (int i = 0; i < bits.length; i++) {
            bits[i] = random.nextInt(3) == 0;
            bitset.set(i, bits[i]);
            if (bits[i]) {
                ++numBitsSet;
            }
        }
        
        System.out.println("Setting " + numBitsSet + " out of " + numBitsToCreate);
        
        for (int i = 0; i < bits.length; i++) {
            assertEquals(bits[i], bitset.get(i));
        }

        // Test iterator
        SparseBitSet.Iterator iter = bitset.getIterator();
        int numBitsIterated = 0;
        while (iter.hasNext()) {
            long idx = iter.next();
            // This one should be true.
            assertTrue("Bits[" + idx + "] should have been true", bits[(int)idx]);
            ++numBitsIterated;
        }

        assertEquals(numBitsIterated, numBitsSet);

        // Test and Time getNumBitsSet()
        long startTime = System.currentTimeMillis();
        assertEquals(numBitsIterated, bitset.getNumBitsSet() );
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("getNumBitsSet on small non-sparse set of 1 took " + duration + " ms");
    }


    /**
     * Tests set(idx, true/false) randomly across the whole set in random access order.
     */
    public void testRandomSetWithFlagRandom() throws Exception
    {
        int nodeSize = TEST_NODE_SIZE;
        int bitsPerNode = nodeSize * 64;

        // Create some random bits to completely fill the set.
        int numBitsToCreate = nodeSize * nodeSize * bitsPerNode;
        int numBitsSet = 0;
        boolean[] bits = new boolean[numBitsToCreate];
        Integer[] bitIndex = new Integer[bits.length];
        SparseBitSet bitset = new SparseBitSet(nodeSize);
        Random random = new Random(15);
        for (int i = 0; i < bits.length; i++) {
            bits[i] = random.nextInt(3) == 0;
            bitIndex[i] = new Integer(i);
        }

        // Set in a random order
        List list = Arrays.asList(bitIndex);
        Collections.shuffle(list, random);
        list.toArray(bitIndex);

        for (int i = 0; i < bits.length; i++) {
            int idx = bitIndex[i].intValue();
            bitset.set(idx, bits[idx]);
        }

        for (int i = 0; i < bits.length; i++) {
            assertEquals(bits[i], bitset.get(i));
        }
    }


    /**
     * Tests set(idx) on all bits and the randomly clear(idx) across the whole set.
     */
    public void testSetClearRandom() throws Exception
    {
        int nodeSize = TEST_NODE_SIZE;
        int bitsPerNode = nodeSize * 64;

        // Create some random bits to completely fill the set.
        int numBitsToCreate = nodeSize * nodeSize * bitsPerNode;
        boolean[] bits = new boolean[numBitsToCreate];
        SparseBitSet bitset = new SparseBitSet(nodeSize);

        // Set all bits on.
        for (int i = 0; i < bits.length; i++) {
            bits[i] = true;
            bitset.set(i);
        }

        // Randomly clear bits.
        Random random = new Random(15);
        for (int i = 0; i < bits.length; i++) {
            bits[i] = random.nextInt(5) == 0;
            if (!bits[i]) {
                bitset.clear(i);
            }
        }

        long numBitsSet = 0;
        for (int i = 0; i < bits.length; i++) {
            assertEquals(bits[i], bitset.get(i));
            if (bits[i]) {
                ++numBitsSet;
            }
        }

        assertEquals(numBitsSet, bitset.getNumBitsSet() );
    }


    /**
     * Tests large sparse set with only one bit set and tries to use an
     * index that's only valid as a long. Also validates that an exception is
     * thrown when trying to access past the end of the set.
     * Also tests the ability to quickly iterate over this set.
     */
    public void testLargeSparseSet() throws Exception
    {
        long bitIdx = 6171947600L;

        // Set bit on.
        SparseBitSet bitset = new SparseBitSet(1024);
        bitset.set(bitIdx);

        // Bit should be set.
        assertTrue( bitset.get(bitIdx) );
        // These should not be set..
        assertFalse( bitset.get(bitIdx + 1) );
        assertFalse( bitset.get(bitIdx - 1) );

        // This should throw - one past end of set.
        try {
            bitset.get(68719476736L);
            fail("Expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            // Expected
        }

        // This should not throw - last index. And it should not be set.
        assertFalse( bitset.get(68719476735L) );

        // Test iterator
        long startTime = System.currentTimeMillis();
        SparseBitSet.Iterator iter = bitset.getIterator();
        int numBitsIterated = 0;
        while (iter.hasNext()) {
            long idx = iter.next();
            // This one should be true.
            assertTrue(idx == bitIdx);
            ++numBitsIterated;
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Iterate over bitset of " + bitset.getMaxSize() + " with one entry set took " + duration + " ms");
        assertEquals(1, numBitsIterated);

        // Time getNumBitsSet()
        startTime = System.currentTimeMillis();
        assertEquals(1, bitset.getNumBitsSet() );
        duration = System.currentTimeMillis() - startTime;
        System.out.println("getNumBitsSet on large sparse set of 1 took " + duration + " ms");

    }


    /**
     * Tests isClear(), which indirectly tests the fact that cleared nodes are removed.
     * Tests getMaxSize().
     */
    public void testSetAndIsClear() throws Exception
    {
        int nodeSize = TEST_NODE_SIZE;
        int bitsPerNode = nodeSize * 64;
        SparseBitSet bitset = new SparseBitSet(nodeSize);

        // Set every bit in the set.
        int numBitsToCreate = nodeSize * nodeSize * bitsPerNode;
        assertEquals(numBitsToCreate, bitset.getMaxSize() );

        for (int i = 0; i < numBitsToCreate; i++) {
            bitset.set(i);
        }

        // All should be set.
        for (int i = 0; i < numBitsToCreate; i++) {
            assertTrue( bitset.get(i) );
        }

        // All are set, this should be false.
        assertFalse( bitset.isClear() );

        // Clear the first half of them.
        int endIdx = numBitsToCreate / 2;
        for (int i = 0; i < endIdx; i++) {
            bitset.clear(i);
        }

        // Some bits are still set, this should be false.
        assertFalse( bitset.isClear() );

        // Test that first half are clear and second half are set.
        for (int i = 0; i < numBitsToCreate; i++) {
            assertEquals(i >= endIdx, bitset.get(i) );
        }

        // Test that iterator see the first set bit at endIdx.
        SparseBitSet.Iterator iter = bitset.getIterator();
        assertTrue( iter.hasNext() );
        assertEquals(endIdx, iter.next() );

        // Clear the rest of them.
        for (int i = endIdx; i < numBitsToCreate; i++) {
            bitset.clear(i);
        }

        // All bits should now be clear and empty nodes removed.
        assertTrue( bitset.isClear() );

        // Iterator should have nothing
        iter = bitset.getIterator();
        assertFalse( iter.hasNext() );
    }
}

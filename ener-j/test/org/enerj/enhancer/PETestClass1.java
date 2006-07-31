
package org.enerj.enhancer;

import org.enerj.annotations.PersistenceAware;

/**
 * Test class for TopLevelPersistableEnhancementTest.
 * This is a PersistentAware class which subclasses a Persistable.
 */
@PersistenceAware
class PETestClass1 extends TLPTestClass1
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    PETestClass1(int aValue)
    {
        super(aValue);
    }
}

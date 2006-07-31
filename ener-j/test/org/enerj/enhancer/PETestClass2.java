
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for TopLevelPersistableEnhancementTest.
 * This is a Regular (non Persistable, non PersistentAware) class which subclasses a Persistable.
 */
@Persist(false)
class PETestClass2 extends TLPTestClass1
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    PETestClass2(int aValue)
    {
        super(aValue);
    }
}

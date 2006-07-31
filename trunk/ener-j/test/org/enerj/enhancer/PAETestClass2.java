
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for PersistentAwareEnhancementTest.
 * This class is Persistable.
 */
@Persist
class PAETestClass2
{
    int mExposedField;
    
    //----------------------------------------------------------------------
    PAETestClass2(int aValue)
    {
        mExposedField = aValue;
    }
    
}



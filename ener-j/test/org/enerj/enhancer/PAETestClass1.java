
package org.enerj.enhancer;

import org.enerj.annotations.PersistenceAware;
import org.enerj.core.*;

/**
 * Test class for PersistentAwareEnhancementTest.
 * This class is only PersistentAware.
 */
@PersistenceAware
class PAETestClass1
{
    private int mValue;
    PAETestClass2 mPersistable = null;
    
    //----------------------------------------------------------------------
    PAETestClass1()
    {
        mValue = 5;
        // Here's the reference to the Persistable object.
        mPersistable = new PAETestClass2(mValue);
        PersistableHelper.setNonTransactional(mPersistable);
    }
    
    //----------------------------------------------------------------------
    void modifyPersistable()
    {
        // This should be enhanced
        mPersistable.mExposedField = 22;
        
        // The persitable should now be modified
        
        // This should get enhanced too.
        String test = "The value is " + mPersistable.mExposedField;
    }
    
}

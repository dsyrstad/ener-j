
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for TopLevelPersistableEnhancementTest.
 * Extends a Special FCO, but ArrayList is not persisted.
 */
@Persist
class TLPTestClass4 extends java.util.ArrayList
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    TLPTestClass4(int aValue)
    {
        super();
        mValue = aValue;
    }
    
    
    //----------------------------------------------------------------------
    public void someMethod()
    {
        mValue = 22;
        sSomeValue = 23;
    }

    //----------------------------------------------------------------------
    public boolean equals(Object anObject)
    {
        if (!(anObject instanceof TLPTestClass4)) {
            return false;
        }
        
        TLPTestClass4 obj = (TLPTestClass4)anObject;
        // Don't test super.equals() because it is not persisted.
        return this.mValue == obj.mValue;
    }
}

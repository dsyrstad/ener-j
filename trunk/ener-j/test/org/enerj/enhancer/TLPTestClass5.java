
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for TopLevelPersistableEnhancementTest.
 * Derived from a PersistentAware class.
 */
@Persist
class TLPTestClass5 extends TLPTestClassPA
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    TLPTestClass5(int aValue)
    {
        super(true);
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
        if (!(anObject instanceof TLPTestClass5)) {
            return false;
        }
        
        TLPTestClass5 obj = (TLPTestClass5)anObject;
        return this.mValue == obj.mValue;
    }
}

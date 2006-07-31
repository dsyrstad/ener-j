
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for NonTLPersistableEnhancementTest.
 */
@Persist
class NTLPTestClassParent
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    NTLPTestClassParent(int aValue)
    {
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
        if (!(anObject instanceof NTLPTestClassParent)) {
            return false;
        }
        
        NTLPTestClassParent obj = (NTLPTestClassParent)anObject;
        return this.mValue == obj.mValue;
    }
}

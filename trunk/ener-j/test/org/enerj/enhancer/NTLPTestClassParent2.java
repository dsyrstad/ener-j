
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for NonTLPersistableEnhancementTest.
 * Second-level Persistable.
 */
@Persist
class NTLPTestClassParent2 extends NTLPTestClassParent
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    NTLPTestClassParent2(int aValue)
    {
        super(23);
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
        if (!(anObject instanceof NTLPTestClassParent2)) {
            return false;
        }
        
        NTLPTestClassParent2 obj = (NTLPTestClassParent2)anObject;
        return super.equals(obj) && (this.mValue == obj.mValue);
    }
}

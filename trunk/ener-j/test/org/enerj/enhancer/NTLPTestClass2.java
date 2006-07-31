
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for NonTLPersistableEnhancementTest.
 * Third-level Persistable.
 */
@Persist
class NTLPTestClass2 extends NTLPTestClassParent2
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    NTLPTestClass2(int aValue)
    {
        super(12);
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
        if (!(anObject instanceof NTLPTestClass2)) {
            return false;
        }
        
        NTLPTestClass2 obj = (NTLPTestClass2)anObject;
        return super.equals(obj) && (this.mValue == obj.mValue);
    }
}

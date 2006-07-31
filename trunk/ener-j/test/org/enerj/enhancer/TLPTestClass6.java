
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for TopLevelPersistableEnhancementTest.
 * Note super-class doesn't have an exposed no-arg constructor and
 * neither does this class. This should be a runtime error.
 */
@Persist
class TLPTestClass6 extends TLPTestClassNP
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    TLPTestClass6(int aValue)
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
        if (!(anObject instanceof TLPTestClass6)) {
            return false;
        }
        
        TLPTestClass6 obj = (TLPTestClass6)anObject;
        return this.mValue == obj.mValue;
    }
}

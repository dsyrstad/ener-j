
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for TopLevelPersistableEnhancementTest.
 */
@Persist
class TLPTestClass1 implements Cloneable
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    TLPTestClass1(int aValue)
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
        if (!(anObject instanceof TLPTestClass1)) {
            return false;
        }
        
        TLPTestClass1 obj = (TLPTestClass1)anObject;
        return this.mValue == obj.mValue;
    }

    //----------------------------------------------------------------------
    public Object clone() throws CloneNotSupportedException
    {
        TLPTestClass1 clone = (TLPTestClass1)super.clone();
        
        System.out.println("Cloned " + this.getClass() + ", mValue=" + mValue + " cloned mValue=" + clone.mValue);
        return clone;
    }
}

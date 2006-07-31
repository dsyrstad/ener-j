
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for NonTLPersistableEnhancementTest.
 */
@Persist
class NTLPTestClass1 extends NTLPTestClassParent implements Cloneable
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    NTLPTestClass1(int aValue)
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
        if (!(anObject instanceof NTLPTestClass1)) {
            return false;
        }
        
        NTLPTestClass1 obj = (NTLPTestClass1)anObject;
        return super.equals(obj) && (this.mValue == obj.mValue);
    }
    
    //----------------------------------------------------------------------
    public Object clone() throws CloneNotSupportedException
    {
        NTLPTestClass1 clone = (NTLPTestClass1)super.clone();
        System.out.println("Cloned " + this.getClass() + ", mValue=" + mValue + " cloned mValue=" + clone.mValue);
        return clone;
    }
}

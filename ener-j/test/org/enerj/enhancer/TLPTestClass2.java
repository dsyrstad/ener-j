
package org.enerj.enhancer;

import org.enerj.annotations.Persist;

/**
 * Test class for TopLevelPersistableEnhancementTest.
 * Extends a SCO (but SCO is not persisted).
 * Note that java.util.Date has a no-arg constructor.
 */
@Persist
class TLPTestClass2 extends java.util.Date
{
    private static int sSomeValue = 5;  // not persistent
    private int mValue;
    
    //----------------------------------------------------------------------
    TLPTestClass2(boolean useToday)
    {
        // Generate some significant byte code before invoking superclass constructor.
        super(useToday ? 0L : System.currentTimeMillis());
        mValue = 1;
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
        if (!(anObject instanceof TLPTestClass2)) {
            return false;
        }
        
        // Don't test super.equals because Date is not persisted.
        TLPTestClass2 obj = (TLPTestClass2)anObject;
        return this.mValue == obj.mValue;
    }
}

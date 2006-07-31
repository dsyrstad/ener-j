
package org.enerj.enhancer;

import org.enerj.annotations.Persist;
import org.enerj.core.*;

/**
 * Test class for TopLevelPersistableEnhancementTest.
 * This is NOT Persistable NOR PersistentAware and doesn't have an exposed no-arg constructor.
 */
@Persist(false)
class TLPTestClassNP
{
    //----------------------------------------------------------------------
    private TLPTestClassNP()
    {
    }
    
    //----------------------------------------------------------------------
    TLPTestClassNP(boolean aFlag)
    {
    }
    
}

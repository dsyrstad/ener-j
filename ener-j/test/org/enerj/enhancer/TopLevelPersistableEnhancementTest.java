// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/enhancer/TopLevelPersistableEnhancementTest.java,v 1.5 2006/06/09 02:39:15 dsyrstad Exp $

package org.enerj.enhancer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;

/**
 * Tests enhancement of Top-level Persistable classes.
 *
 * @version $Id: TopLevelPersistableEnhancementTest.java,v 1.5 2006/06/09 02:39:15 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class TopLevelPersistableEnhancementTest extends AbstractPersistableEnhancementTest 
{
    private static final String DATABASE_URI = "enerj://root:root@-/TLPETestDB?DefaultMetaObjectServer.ObjectServerClass=org.enerj.server.MemoryObjectServer";
    
    //----------------------------------------------------------------------
    public TopLevelPersistableEnhancementTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args) 
    {
        junit.swingui.TestRunner.run(TopLevelPersistableEnhancementTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(TopLevelPersistableEnhancementTest.class);
    }

    //----------------------------------------------------------------------
    /**
     * Make sure proper basic enhancement was done for fields and methods.
     *
     * @param aTestClass the class to be checked.
     *
     * @throws Exception when something unexpected occurs.
     */
    private void checkFieldsMethodsAndCommon(Class aTestClass, boolean checkNew, int aPersistentSize) throws Exception
    {
        String[] expectedFields = new String[] { "enerj_sClassId", "enerj_mVersion", "enerj_mModified",
            "enerj_mNew", "enerj_mLoaded", "enerj_mAllowNonTransactionalReads", "enerj_mAllowNonTransactionalWrites", 
            "enerj_mOID", "enerj_mDatabase", "enerj_mLockLevel", "mValue", "sSomeValue",
        };

        String classSuffix = aTestClass.getName().replace('.', '_');
        String[] expectedMethods = { "enerj_GetVersion", "enerj_IsModified", "enerj_IsNew", 
            "enerj_IsLoaded", "enerj_AllowsNonTransactionalRead", 
            "enerj_AllowsNonTransactionalWrite", "enerj_GetPrivateOID",
            "enerj_GetDatabase", "enerj_SetVersion", "enerj_SetPrivateOID",
            "enerj_SetDatabase", "enerj_SetModified", "enerj_SetNew", "enerj_SetLoaded",
            "enerj_SetAllowNonTransactionalRead", "enerj_SetAllowNonTransactionalWrite", 
            "enerj_Get_" + classSuffix + "_mValue", "enerj_Set_" + classSuffix + "_mValue",
            "enerj_Get_" + classSuffix + "_sSomeValue", "enerj_Set_" + classSuffix + "_sSomeValue",
            "enerj_GetClassId", "enerj_GetClassIdStatic", "enerj_ReadObject", "enerj_WriteObject", "enerj_Hollow", "enerj_GetLockLevel", "enerj_SetLockLevel",
            "someMethod", "equals", "clone"
        };
        
        checkCommonEnhancement(aTestClass, expectedFields, expectedMethods, checkNew, aPersistentSize);
    }

    //----------------------------------------------------------------------
    /**
     * Make sure proper basic enhancement was done.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testEnhancement() throws Exception
    {
        checkFieldsMethodsAndCommon(TLPTestClass1.class, true, 4);
        checkFieldsMethodsAndCommon(TLPTestClass2.class, true, 4);
        checkFieldsMethodsAndCommon(TLPTestClass3.class, true, 4);
        checkFieldsMethodsAndCommon(TLPTestClass4.class, true, 4);
        checkFieldsMethodsAndCommon(TLPTestClass5.class, true, 4);
        checkFieldsMethodsAndCommon(TLPTestClass6.class, false, 0);
        
        String[] fields = new String[] { "mValue", "sSomeValue" };
        String[] methods =  new String[0];
        checkFieldsAndMethods(PETestClass1.class, (String[])fields.clone(), (String[])methods.clone());
        checkFieldsAndMethods(PETestClass2.class, (String[])fields.clone(), (String[])methods.clone());
    }
    
    //----------------------------------------------------------------------
    /**
     * Test that the "new" flag is set on instantiation.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testNew() throws Exception
    {
        // Test on class that calls super() with args. This is a more complex case.
        TLPTestClass2 obj = new TLPTestClass2(true);
        // The above shouldn't have thrown an exception.
        
        Persistable persistable = (Persistable)obj;
        PersistableHelper.setNonTransactional(persistable);
        // Tests enhancement of constructor to call PersistableHelper to set NEW
        assertTrue("Expected it to be new", persistable.enerj_IsNew() );
        // Tests that constructor was NOT enhanced for putfield.
        assertTrue("Expected it to be not modified", !persistable.enerj_IsModified() );

        // Construct like the database will. Neither the New nor modified flags should be 
        // set because the superclass no-arg constructor is used.
        Constructor constructor = 
            TLPTestClass2.class.getConstructor(new Class[] { org.enerj.core.EnerJDatabase.class });
        obj = (TLPTestClass2)constructor.newInstance(new Object[] { null } );
        // The above shouldn't have thrown an exception.
        assertNotNull("Construction should not return null", obj);

        persistable = (Persistable)obj;
        PersistableHelper.setNonTransactional(persistable);
        // Tests that constructor was NOT enhanced for new nor putfield.
        assertTrue("Expected it to be not new and not modified", !persistable.enerj_IsNew() && !persistable.enerj_IsModified());
    }
    
    //----------------------------------------------------------------------
    /**
     * Test instantiation via TLPTestClass3(EnerJDatabase) constructor. The default constructor
     * of TLPTestClass3 should be used because its superclass does not have an
     * exposed no-arg constructor. Even though the enhanced TLPTestClass3 no-arg constructor is being used,
     * the NEW flag SHOULD NOT be set.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testNewNoArg() throws Exception
    {
        // Construct like the database will.
        Constructor constructor = 
            TLPTestClass3.class.getConstructor(new Class[] { org.enerj.core.EnerJDatabase.class });
        TLPTestClass3 obj = (TLPTestClass3)constructor.newInstance(new Object[] { null } );
        // The above shouldn't have thrown an exception.
        assertNotNull("Construction should not return null", obj);

        Persistable persistable = (Persistable)obj;
        PersistableHelper.setNonTransactional(persistable);
        // Tests enhancement of constructor to call PersistableHelper to set NEW
        assertTrue("Expected it to be new (this is undone by EnerJDatabase.getObjectByOID)", persistable.enerj_IsNew() );
        // Tests that constructor was NOT enhanced for putfield.
        assertTrue("Expected it to be not modified", !persistable.enerj_IsModified() );
    }

    //----------------------------------------------------------------------
    /**
     * Test missing no-arg constructor on superclass and the persistable class.
     * Should be a runtime error.
     *
     * @throws Exception when something unexpected occurs.
     */
    public void testNoNoArg() throws Exception
    {
        try {
            // Construct like the database will.
            Constructor constructor = 
                TLPTestClass6.class.getConstructor(new Class[] { org.enerj.core.EnerJDatabase.class });
            TLPTestClass6 obj = (TLPTestClass6)constructor.newInstance(new Object[] { null } );
            fail("Expected InvocationTargetException");
        }
        catch (InvocationTargetException e) {
            // Expected. This should be an IllegalAccessError because the no-arg
            // constructor is private on the superclass TLPTestClassNP.
            assertTrue("Expected IllegalAccessError", e.getTargetException() instanceof IllegalAccessError);
        }
    }

}

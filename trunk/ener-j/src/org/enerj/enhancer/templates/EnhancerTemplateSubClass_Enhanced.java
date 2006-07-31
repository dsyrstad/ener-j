// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/templates/EnhancerTemplateSubClass_Enhanced.java,v 1.3 2005/08/12 02:56:53 dsyrstad Exp $

package org.enerj.enhancer.templates;

import java.io.*;

import org.enerj.core.*;
import org.odmg.*;

/**
 * Class file enhancer template for Ener-J. This is an example of a 
 * sub-class of EnhancerTemplate.
 * This class provides a bytecode prototype for developement of the enhancer.
 * This is the class after enhancement. Ignore the _Enhanced extension, it wouldn't normally exist.
 * This is the class stored in the database and loaded on the client via the DatabaseClassLoader.
 *
 * @version $Id: EnhancerTemplateSubClass_Enhanced.java,v 1.3 2005/08/12 02:56:53 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class EnhancerTemplateSubClass_Enhanced extends EnhancerTemplate_Enhanced implements Persistable, Cloneable
{
    private byte mByte;
    private Byte mByteObj;
    private boolean mBoolean;
    private Boolean mBooleanObj;
    private char mChar;
    private Character mCharObj;
    private short mShort;
    private Short mShortObj;
    private int mInt;
    private Integer mIntObj;
    private long mLong;
    private Long mLongObj;
    private float mFloat;
    private Float mFloatObj;
    private double mDouble;
    private Double mDoubleObj;
    private String mString;
    private Object mObject;

    private int[] mIntArray;
    private int[][] m2dArray;
    private Object[] mObjArray;
    
    transient private static long enerj_sClassId;
    
    static {
        enerj_sClassId = 55835L;
    }

    //----------------------------------------------------------------------
    // Generated constructor. EnerJDatabase is simply used for a unique signature.
    public EnhancerTemplateSubClass_Enhanced(EnerJDatabase aDatabase)
    {
        super(aDatabase);
    }

    //----------------------------------------------------------------------
    // User-defined constructor.
    public EnhancerTemplateSubClass_Enhanced(long aParam)
    {
        super(aParam > 0 ? 5 : 4);
        enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mLong(this, aParam);
    }

    //----------------------------------------------------------------------
    // User-defined methods ...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    public void doIt(int aParam)
    {
        enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mInt(this, aParam);
    }


    //----------------------------------------------------------------------
    // Enhanced version of user-defined clone.
    public Object clone()
    {
        EnhancerTemplateSubClass_Enhanced clone = (EnhancerTemplateSubClass_Enhanced)super.clone();
        enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mDouble(clone, 0.);
        enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mIntArray(clone, 
            (int[])enerj_Get_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mIntArray(this).clone() );
 
        return clone;
    }

    //----------------------------------------------------------------------
    /**
     * User callback.
     */
    private void voPreHollow()
    {
    }

    //----------------------------------------------------------------------
    // ...End of User-defined methods.
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // Generated methods for any persistable...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // So we can get a class id when we only have a class and not an object.
    public static long enerj_GetClassIdStatic()
    {
        return enerj_sClassId;
    }
    
    //----------------------------------------------------------------------
    // So we can get a class id via the Persistable interface.
    public long enerj_GetClassId()
    {
        return enerj_sClassId;
    }
    
    //----------------------------------------------------------------------
    private // Modifier matches original field modifier
        static long
            enerj_Get_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mLong(EnhancerTemplateSubClass_Enhanced  anInstance)
    {
        // Must be in a transaction... Unless we allow stale reads
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, false);
        }
        return anInstance.mLong;
    }

    //----------------------------------------------------------------------
    private // Modifier matches original field modifier
        static int[]
            enerj_Get_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mIntArray(EnhancerTemplateSubClass_Enhanced  anInstance)
    {
        // Must be in a transaction... Unless we allow stale reads
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, false);
        }
        return anInstance.mIntArray;
    }

    //----------------------------------------------------------------------
    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mLong(EnhancerTemplateSubClass_Enhanced  anInstance,
                long  aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mLong) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mLong = aValue;
        }
    }

    //----------------------------------------------------------------------
    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mDouble(EnhancerTemplateSubClass_Enhanced  anInstance,
                double  aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mDouble) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mDouble = aValue;
        }
    }

    //----------------------------------------------------------------------
    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mInt(EnhancerTemplateSubClass_Enhanced  anInstance,
                int  aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mInt) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mInt = aValue;
        }
    }

    //----------------------------------------------------------------------
    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mFloat(EnhancerTemplateSubClass_Enhanced  anInstance,
                float  aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mFloat) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mFloat = aValue;
        }
    }

    //----------------------------------------------------------------------
    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mShort(EnhancerTemplateSubClass_Enhanced  anInstance,
                short  aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mShort) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mShort = aValue;
        }
    }

    //----------------------------------------------------------------------
    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mIntArray(EnhancerTemplateSubClass_Enhanced  anInstance,
                int[] aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }
        
        if (aValue != anInstance.mIntArray) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mIntArray = aValue;
        }
    }

    //----------------------------------------------------------------------
    private // Modifier matches original field modifier
        static void enerj_Set_org_enerj_enhancer_templates_EnhancerTemplateSubClass_mString(EnhancerTemplateSubClass_Enhanced  anInstance,
                String aValue)
    {
        if (!anInstance.enerj_mLoaded && !anInstance.enerj_mNew) {
            PersistableHelper.checkLoaded(anInstance, true);
        }

        if ((aValue == null && anInstance.mString != null) || (aValue != null && !aValue.equals(anInstance.mString))) {
            if (!anInstance.enerj_mModified) {
                PersistableHelper.addModified(anInstance);
            }
            anInstance.mString = aValue;
        }
    }

    //----------------------------------------------------------------------
    public void enerj_ReadObject(ObjectSerializer.ReadContext aContext) throws IOException
    {
        DataInput stream = aContext.mStream;

        super.enerj_ReadObject(aContext);

        mByte = stream.readByte();
        mByteObj = (java.lang.Byte)ObjectSerializer.readObject(aContext, this);
        mBoolean = stream.readBoolean();
        mBooleanObj = (java.lang.Boolean)ObjectSerializer.readObject(aContext, this);
        mChar = stream.readChar();
        mCharObj = (java.lang.Character)ObjectSerializer.readObject(aContext, this);
        mShort = stream.readShort();
        mShortObj = (java.lang.Short)ObjectSerializer.readObject(aContext, this);
        mInt = stream.readInt();
        mIntObj = (java.lang.Integer)ObjectSerializer.readObject(aContext, this);
        mLong = stream.readLong();
        mLongObj = (java.lang.Long)ObjectSerializer.readObject(aContext, this);
        mFloat = stream.readFloat();
        mFloatObj = (java.lang.Float)ObjectSerializer.readObject(aContext, this);
        mDouble = stream.readDouble();
        mDoubleObj = (java.lang.Double)ObjectSerializer.readObject(aContext, this);
        mString = (String)ObjectSerializer.readObject(aContext, this);
        mObject = (Object)ObjectSerializer.readObject(aContext, this);
        mIntArray = (int[])ObjectSerializer.readObject(aContext, this);
        m2dArray = (int[][])ObjectSerializer.readObject(aContext, this);
        mObjArray = (Object[])ObjectSerializer.readObject(aContext, this);
   }

    //----------------------------------------------------------------------
    public void enerj_WriteObject(ObjectSerializer.WriteContext aContext) throws IOException
    {
        DataOutput stream = aContext.mStream;

        super.enerj_WriteObject(aContext);

        stream.writeByte(mByte);
        ObjectSerializer.writeObject(aContext, mByteObj, this);
        stream.writeBoolean(mBoolean);
        ObjectSerializer.writeObject(aContext, mBooleanObj, this);
        stream.writeChar(mChar);
        ObjectSerializer.writeObject(aContext, mCharObj, this);
        stream.writeShort(mShort);
        ObjectSerializer.writeObject(aContext, mShortObj, this);
        stream.writeInt(mInt);
        ObjectSerializer.writeObject(aContext, mIntObj, this);
        stream.writeLong(mLong);
        ObjectSerializer.writeObject(aContext, mLongObj, this);
        stream.writeFloat(mFloat);
        ObjectSerializer.writeObject(aContext, mFloatObj, this);
        stream.writeDouble(mDouble);
        ObjectSerializer.writeObject(aContext, mDoubleObj, this);
        ObjectSerializer.writeObject(aContext, mString, this);
        ObjectSerializer.writeObject(aContext, mObject, this);
        ObjectSerializer.writeObject(aContext, mIntArray, this);
        ObjectSerializer.writeObject(aContext, m2dArray, this);
        ObjectSerializer.writeObject(aContext, mObjArray, this);
    }

    //----------------------------------------------------------------------
    /** Clear the object's persistent fields. Only persistent fields which
     * refer to Objects are cleared (i.e., primitive field values are not touched).
     */
    public void enerj_Hollow() 
    {
        voPreHollow();
        
        super.enerj_Hollow();
        mByteObj = null;
        mBooleanObj = null;
        mCharObj = null;
        mShortObj = null;
        mIntObj = null;
        mLongObj = null;
        mFloatObj = null;
        mDoubleObj = null;
        mString = null;
        mObject = null;
        mIntArray = null;
        m2dArray = null;
        mObjArray = null;
        
        PersistableHelper.completeHollow(this);
    }
    
    
    //----------------------------------------------------------------------
    // ... End of Generated methods for any persistable.
    //----------------------------------------------------------------------
    
}

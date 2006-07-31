/**
 * 
 */
package org.enerj.core;

import org.enerj.annotations.Persist;
import org.enerj.util.StringUtil;

/**
 * Structure returned by OQL queries. <p>
 * 
 * @version $Id: Structure.java,v 1.5 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Persist
public class Structure implements Comparable, Cloneable
{
    private String[] mMemberNames;
    private Object[] mMemberValues;

    
    //--------------------------------------------------------------------------------
    /**
     * Construct a Structure. 
     *
     * @param someMemberNames the names of the members. Must be the same size as someMemberValues.
     * @param someMemberValues the values of the members.
     */
    public Structure(String[] someMemberNames, Object[] someMemberValues)
    {
        assert someMemberNames.length == someMemberValues.length;
        mMemberNames = someMemberNames;
        mMemberValues = someMemberValues;
    }


    //--------------------------------------------------------------------------------
    /**
     * Gets the member names.
     *
     * @return a String[] containing the member names with a one-to-one correspondence with getMemberValues().
     */
    public String[] getMemberNames()
    {
        return mMemberNames;
    }


    //--------------------------------------------------------------------------------
    /**
     * Gets the member values.
     *
     * @return a Object[] contains the member values with a one-to-one correspondence with getMemberNames().
     */
    public Object[] getMemberValues()
    {
        return mMemberValues;
    }

    //--------------------------------------------------------------------------------
    /** 
     * Note: This method expects that all members implement Comparable. If not, and the members
     * are not equal (using equals()), IllegalArgumentException
     * will be thrown. A null for any member value compares less than a non-null value.
     * <p/>
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(Object anObject)
    {
        if (anObject == null) {
            throw new ClassCastException("Subject of compareTo is null");
        }
        
        // This will throw ClassCastException, as defined by the compareTo() contract, if it's not a Structure.
        Structure struct = (Structure)anObject;
        for (int i = 0; i < mMemberValues.length; i++) {
            if (i >= struct.mMemberValues.length) {
                // anObject has less members, but so far all of them compared equal. So this object compares
                // greater because it has more members.
                return 1;
            }
            
            Object leftValue = mMemberValues[i];
            Object rightValue = struct.mMemberValues[i];

            // Handles nulls and the same object.
            if (leftValue == rightValue) {
                continue;
            }
            
            if (leftValue == null && rightValue != null) {
                return -1;
            }
            
            if (leftValue != null && rightValue == null) {
                return 1;
            }
            
            if (leftValue instanceof Comparable) {
                int delta = ((Comparable)leftValue).compareTo(rightValue);
                if (delta != 0) {
                    return delta;
                }
            }
            else {
                // One last chance. We'll see if they are equal.
                if ( !leftValue.equals(rightValue)) {
                    throw new IllegalArgumentException(leftValue.getClass() + " does not implement Comparable");
                }
            }
        }
        
        // So far everything compares equal, but if anObject has more members, this object compares less.
        if (struct.mMemberValues.length > mMemberValues.length) {
            return -1;
        }

        return 0;
    }
    
    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see java.lang.Object#clone()
     */
    protected Object clone() throws CloneNotSupportedException
    {
        Structure struct = (Structure)super.clone();
        struct.mMemberNames = mMemberNames.clone();
        struct.mMemberValues = mMemberValues.clone();
        
        return struct;
    }

    //--------------------------------------------------------------------------------
    /** 
     * Only the values are compared for equality, not the names.<p/>
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object anObject)
    {
        if (anObject == null || !(anObject instanceof Structure)) {
            return false;
        }
        
        Structure struct = (Structure)anObject;
        if (struct.mMemberValues.length != mMemberValues.length) {
            return false;
        }
        
        for (int i = 0; i < mMemberValues.length; i++) {
            Object leftValue = mMemberValues[i];
            Object rightValue = struct.mMemberValues[i];
            if ((leftValue == null && rightValue != null) || (leftValue != null && rightValue == null)) { 
                return false;
            }

            // null == null, no equals() check needed.
            if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
                return false;
            }
        }

        return true;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int hash = 0;
        for (Object value : mMemberValues) {
            hash = ((hash << 1) + 37) ^ value.hashCode(); 
        }
        
        return hash;
    }

    //--------------------------------------------------------------------------------
    /** 
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder builder = new StringBuilder("Structure[");
        for (int i = 0; i < mMemberValues.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            
            builder.append(mMemberNames[i]);
            builder.append("=");
            builder.append( StringUtil.toString(mMemberValues[i], false, true) );
        }
        
        builder.append(']');
        return builder.toString();
    }
    
}

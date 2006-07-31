//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/StructureFunctor.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import org.enerj.core.Structure;
import org.enerj.jga.fn.UnaryFunctor;
import org.enerj.util.StringUtil;

/**
 * Wraps the collection passed as an argument to fn() in a FilteredCollection.  
 * <p>
 * 
 * @version $Id: StructureFunctor.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class StructureFunctor extends UnaryFunctor
{
    private static final long serialVersionUID = 741932631033216128L;

    private String[] mMemberNames;
    private UnaryFunctor[] mValueFunctors;
   
    //--------------------------------------------------------------------------------
    /**
     * Construct a StructureFunctor.
     * 
     * @param someMemberNames an array of member names. Must be the same size as someValueFunctors.
     * @param someValueFunctors an array of UnaryFunctors that represent the value of the member.
     */
    public StructureFunctor(String[] someMemberNames, UnaryFunctor[] someValueFunctors) 
    {
        mMemberNames = someMemberNames;
        mValueFunctors = someValueFunctors;
    }
    
    //--------------------------------------------------------------------------------
    public Object fn(Object arg)
    {
        Object[] values = new Object[ mValueFunctors.length ];
        for (int i = 0; i < values.length; i++) {
            values[i] = mValueFunctors[i].fn(null);
        }
        
        return new Structure(mMemberNames, values);
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(StructureFunctor)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof StructureFunctor.Visitor)
            ((StructureFunctor.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "StructureFunctor[names=" + StringUtil.toString(mMemberNames, false, false) + ", valueFunctors=" +
                StringUtil.toString(mValueFunctors, false, true) + ']';
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>StructureFunctor</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(StructureFunctor host);
    }
}

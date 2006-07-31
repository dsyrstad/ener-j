//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/ConvertToCollection.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.odmg.DArray;
import org.odmg.DSet;
import org.enerj.core.RegularDArray;
import org.enerj.core.RegularDSet;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Converts a the results of an ApplyUnary functor (or the like) to the specified Collection type.  
 * <p>
 * 
 * @version $Id: ConvertToCollection.java,v 1.4 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ConvertToCollection extends UnaryFunctor
{
    private static final long serialVersionUID = -4320835180809092201L;

    private Class mCollectionType;
    private UnaryFunctor mApplyFunctor;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a ConvertToCollection functor.
     * 
     * @param aCollectionType the type of the desired collection. Must be one of the
     *  interface types: Collection, List, or Set.
     * @param anApplyFunctor a UnaryFunctor that will create an array of values 
     *  for the collection.
     */
    public ConvertToCollection(Class aCollectionType, UnaryFunctor anApplyFunctor) 
    {
        super();
        assert aCollectionType == List.class || aCollectionType == Set.class || aCollectionType == Collection.class;
        mCollectionType = aCollectionType;
        if (mCollectionType == Collection.class) {
            mCollectionType = List.class; // We're really going to create a List for Collection.
        }
        
        mApplyFunctor = anApplyFunctor;
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object arg)
    {
        Object[] values = (Object[])mApplyFunctor.fn(arg);
        List valueList = Arrays.asList(values);
        if (mCollectionType == List.class) {
            DArray dArray = new RegularDArray(values.length); 
            dArray.addAll(valueList);
            return dArray;
        }
        
        // if (mCollectionType == Set.class)
        DSet dSet = new RegularDSet(values.length);
        dSet.addAll(valueList);
        return dSet;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(ConvertToCollection)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v) {
        if (v instanceof ConvertToCollection.Visitor)
            ((ConvertToCollection.Visitor)v).visit(this);
    }
    
    //--------------------------------------------------------------------------------
    public String toString() {
        return "ConvertToCollection( " + mApplyFunctor + ')';
    }
    
    // AcyclicVisitor
    
    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>ConvertToCollection</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor {
        public void visit(ConvertToCollection host);
    }
}

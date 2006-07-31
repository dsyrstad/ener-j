//Ener-J
//Copyright 2001-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/fn/FlattenCollection.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $

package org.enerj.query.oql.fn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.enerj.jga.fn.UnaryFunctor;

/**
 * Flattens the given Collection of Collections parameter to a collection of the sub-collection 
 * elements.  
 * <p>
 * 
 * @version $Id: FlattenCollection.java,v 1.2 2006/02/19 01:20:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class FlattenCollection extends UnaryFunctor
{
    private static final long serialVersionUID = -16298753889352785L;
    private static final FlattenCollection LIST_INSTANCE = new FlattenCollection(false);
    private static final FlattenCollection SET_INSTANCE = new FlattenCollection(true);

    private boolean mIsResultASet;

    //--------------------------------------------------------------------------------
    /**
     * Construct a FlattenCollection functor.
     *
     * @param isSet true if the result should be a Set, otherwise it will be a List.
     */
    private FlattenCollection(boolean isSet)
    {
        mIsResultASet = isSet;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets an instance of this functor for the given type.
     *
     * @param aType the resulting collection type. Must be List, Set, or Collection.
     * 
     * @return the instance of the functor.
     */
    public static FlattenCollection getInstance(Class aType)
    {
        if (Set.class.isAssignableFrom(aType)) {
            return SET_INSTANCE;
        }

        return LIST_INSTANCE;
    }

    //--------------------------------------------------------------------------------
    public Object fn(Object arg)
    {
        // If null or already a Set, return the argument.
        if (arg == null) {
            return arg;
        }

        if (!(arg instanceof Collection)) {
            throw new IllegalArgumentException("Argument is not a Collection");
        }

        Collection source = (Collection)arg;
        Collection result;
        if (mIsResultASet) {
            result = new HashSet(source.size());
        }
        else {
            result = new ArrayList(source.size());
        }

        for (Object coll : source) {
            if (coll instanceof Collection) {
                for (Object obj : (Collection)coll) {
                    result.add(obj);
                }
            }
            else {
                // If not a collection, just add this object.
                result.add(coll);
            }
        }

        return result;
    }

    //--------------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * Calls the Visitor's <code>visit(FlattenCollection)</code> method, if it implements
     * the nested Visitor interface.
     */
    public void accept(org.enerj.jga.fn.Visitor v)
    {
        if (v instanceof FlattenCollection.Visitor)
            ((FlattenCollection.Visitor)v).visit(this);
    }

    //--------------------------------------------------------------------------------
    public String toString()
    {
        return "FlattenCollection(" + (mIsResultASet ? "Set" : "List") + ')';
    }


    // AcyclicVisitor

    //--------------------------------------------------------------------------------
    /**
     * Interface for classes that may interpret a <b>FlattenCollection</b> functor.
     */
    public interface Visitor extends org.enerj.jga.fn.Visitor
    {
        public void visit(FlattenCollection host);
    }
}

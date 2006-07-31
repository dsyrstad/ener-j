//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/query/oql/EvaluatorContext.java,v 1.10 2006/02/24 03:00:40 dsyrstad Exp $

package org.enerj.query.oql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.odmg.QueryException;
import org.enerj.core.Extent;
import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJTransaction;
import org.enerj.jga.fn.UnaryFunctor;

/**
 * Context used by the OQL query evaluator. <p>
 * 
 * @version $Id: EvaluatorContext.java,v 1.10 2006/02/24 03:00:40 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class EvaluatorContext
{
    private static ThreadLocal<EvaluatorContext> sContexts = new ThreadLocal();
    
    // A map whose key is an alias name, and value is a fully-qualified class name.
    private Map<String, String> mImportAlias = new HashMap<String, String>();
    private List<String> mImports = new ArrayList<String>();
    /** The database used for the query. */
    private EnerJDatabase mDatabase;
    /** The transaction for the query. */
    private EnerJTransaction mTransaction;
    /** A collection of Extents that were opened during the query. */
    private Collection<Extent> mExtents = new LinkedList();
    
    /** Scoped Variables. Each element is a Map keyed by the name of the variable and has a VariableDef as a value.
     * the functor will evaluate to the variable's value during execution. The first element is the most
     * local scope and proceeds to the most global scope.
     */
    private LinkedList<Map<String, VariableDef>> scopedVariables = new LinkedList<Map<String, VariableDef>>();

    //--------------------------------------------------------------------------------
    /**
     * Construct a EvaluatorContext. 
     *
     */
    public EvaluatorContext() 
    {
        mDatabase = EnerJDatabase.getCurrentDatabase();
        mTransaction = EnerJTransaction.getCurrentTransaction();
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets the evaluator context for this thread. 
     *
     * @param aContext the context.
     */
    public static void setContext(EvaluatorContext aContext)
    {
        sContexts.set(aContext);
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the evaluator context for this thread. 
     *
     * @param aContext the context.
     */
    public static EvaluatorContext getContext()
    {
        return sContexts.get();
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Disposes resources that were opened during evaluation.
     */
    public void dispose() 
    {
        for (Extent extent : mExtents) {
            extent.closeAll();
        }
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Tracks an extent for later disposal.
     * 
     * @param anExtent the Extent to be tracked.
     */
    public void trackExtent(Extent anExtent) 
    {
        mExtents.add(anExtent);
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Adds an import to the context.
     *
     * @param aName may be a fully-qualified class name or package name.
     * @param anAlias an optional alias that can be used to refer to the class. May be
     *  null if the alias is not specified.
     */
    public void addImport(String aName, String anAlias)
    {
        mImports.add(aName);
        if (anAlias != null) {
            mImportAlias.put(anAlias, aName);
        }
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets the list of imports.
     *
     * @return a List of String representing the imports names. Some may be 
     * fully-qualified class names, others may be package names.
     */
    public List<String> getImports()
    {
        return mImports;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Resolves a class alias to a fully-qualified class name. 
     *
     * @param anAlias the class alias name.
     * 
     * @return the fully-qualified class name, or null if the alias is not defined.
     */
    public String resolveClassAlias(String anAlias)
    {
        return mImportAlias.get(anAlias);
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Finds a class given a class name. The class name may be unqualified or fully-qualified.
     * If it is fully-qualified, the class is loaded and returned.
     * If it is unqualified, the following steps are taken:<br>
     * <ol>
     * <li>If the class name matches the last part of an import statement
     *     and the class can be loaded using the fully-qualified import name, the import name is used.</li>
     * <li>If the class name matches an import alias
     *     and the class can be loaded using the alias' fully-qualified import name, the import name is used.</li>
     * <li>If the class name can be loaded from the java.lang package, it is used.</li>
     * <li>If the class name can be loaded from the java.math package, it is used.</li>
     * <li>If the class name can be loaded from the java.util package, it is used.</li>
     * <li>If the class can be loaded from the default package (no package name), it is used.</li>
     * </ol> 
     *
     * @param aClassName the class name.
     * 
     * @return the Class for aClassName, or null if the class could not be loaded. 
     */
    public Class resolveClass(String aClassName)
    {
        // Imports
        List<String> imports = EvaluatorContext.getContext().getImports();
        Class type = null;
        String cmpClassName = '.' + aClassName;
        for (String imp : imports) {
            if (imp.endsWith(cmpClassName)) {
                try { 
                    return Class.forName(imp);
                }
                catch (ClassNotFoundException e) { // Ignore  
                }
            } 
            else if (imp.endsWith(".*")) {
            	String className = imp.substring(0, imp.length() - 1) + aClassName;
                try { 
                    return Class.forName(className);
                }
                catch (ClassNotFoundException e) { // Ignore  
                }
            }
        }
        
        // Aliases
        String aliasClassName = resolveClassAlias(aClassName);
        if (aliasClassName != null) {
            try { 
                return Class.forName(aliasClassName);
            }
            catch (ClassNotFoundException e) { // Ignore 
            }
        }
        
        // java.lang, java.util, java.math, org.odmg, and default package.
        String[] pkgs = { "java.lang.", "java.util.", "java.math.", "org.odmg.", "" };
        for (String pkg : pkgs) {
            try { 
                return Class.forName(pkg + aClassName);
            }
            catch (ClassNotFoundException e) { // Ignore 
            }
        }
        
        return null;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the Database.
     *
     * @return a EnerJDatabase.
     */
    public EnerJDatabase getDatabase()
    {
        return mDatabase;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets the Database.
     *
     * @param aDatabase a EnerJDatabase.
     */
    public void setDatabase(EnerJDatabase aDatabase)
    {
        mDatabase = aDatabase;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the Transaction.
     *
     * @return a EnerJTransaction.
     */
    public EnerJTransaction getTransaction()
    {
        return mTransaction;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets the Transaction.
     *
     * @param aTransaction a EnerJTransaction.
     */
    public void setTransaction(EnerJTransaction aTransaction)
    {
        mTransaction = aTransaction;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Pushs a new variable scope onto the stack. 
     */
    public void pushVariableScope() 
    {
        scopedVariables.addFirst( new HashMap<String, VariableDef>(16) );
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Pops a variable scope from the stack. 
     * 
     * @throws QueryException if no scope exists.
     */
    public void popVariableScope() throws QueryException
    {
        checkScopeExists();
        scopedVariables.removeFirst();
    }

    //--------------------------------------------------------------------------------
    /**
     * @throws QueryException if no local variable scope exists. 
     */
    private void checkScopeExists() throws QueryException
    {
        if (scopedVariables.isEmpty()) {
            throw new QueryException("No variable scope exists yet.");
        }
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Adds a variable to the most local scope. 
     * 
     * @param aVariableName the name of the variable.
     * @param aFunctor a UnaryFunctor that will return the value of the variable.
     * @param aType the type that aFunctor will return.
     * 
     * @throws QueryException if any other variable by the same name exists within the most local scope.
     *  Also throws if no current scope exists.
     */
    public void addVariable(String aVariableName, UnaryFunctor aFunctor, Class aType) throws QueryException
    {
        checkScopeExists();
        Map<String, VariableDef> vars = scopedVariables.getFirst();
        if (vars.containsKey(aVariableName)) {
            throw new QueryException("Variable " + aVariableName + " already exists in current scope");
        }
        
        vars.put(aVariableName, new VariableDef(aFunctor, aType) );
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets a variable that is in scope. 
     * 
     * @param aVariableName the name of the variable.
     * 
     * @return the VariableDef of the variable, or null if the variable does not exist.
     */
    public VariableDef getVariable(String aVariableName) throws QueryException
    {
        for (Map<String, VariableDef> vars : scopedVariables) {
            VariableDef varDef = vars.get(aVariableName);
            if (varDef != null) {
                return varDef;
            }
        }
        
        return null;
    }
    
    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    public static final class VariableDef
    {
        private UnaryFunctor mFunctor;
        private Class mType;
        
        public VariableDef(UnaryFunctor aFunctor, Class aType)
        {
            mFunctor = aFunctor;
            mType = aType;
        }
        
        public UnaryFunctor getValueFunctor()
        {
            return mFunctor;
        }
        
        public Class getType()
        {
            return mType;
        }
    }
}

package org.enerj.oo7;

import java.util.ArrayList;

public class ComplexAssembly extends Assembly
{
    private Module module;
    private ArrayList subAssemblies;

    public ComplexAssembly()
    {
    }

    public ComplexAssembly(int id, int buildDate, Module module)
    {
        super(id, buildDate);
        this.module = module;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the module.
     *
     * @return a Module.
     */
    public Module getModule()
    {
        return module;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets ComplexAssembly.java.
     *
     * @param someModule a Module.
     */
    public void setModule(Module someModule)
    {
        module = someModule;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the subAssemblies.
     *
     * @return a ArrayList.
     */
    public ArrayList getSubAssemblies()
    {
        return subAssemblies;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets ComplexAssembly.java.
     *
     * @param someSubAssemblies a ArrayList.
     */
    public void setSubAssemblies(ArrayList someSubAssemblies)
    {
        subAssemblies = someSubAssemblies;
    }
}

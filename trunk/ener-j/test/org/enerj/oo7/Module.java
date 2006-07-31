package org.enerj.oo7;

import java.util.ArrayList;

public class Module extends DesignObj
{
    private ArrayList assemblies;
    private Assembly designRoot;
    private Manual manual;

    //--------------------------------------------------------------------------------
    /**
     * Construct a Module. 
     *
     */
    public Module()
    {
        super();
    }

    //--------------------------------------------------------------------------------
    /**
     * Construct a Module. 
     *
     * @param someId
     * @param someBuildDate
     */
    public Module(int someId, int someBuildDate)
    {
        super(someId, someBuildDate);
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the assemblies.
     *
     * @return a ArrayList.
     */
    public ArrayList getAssemblies()
    {
        return assemblies;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Module.java.
     *
     * @param someAssemblies a ArrayList.
     */
    public void setAssemblies(ArrayList someAssemblies)
    {
        assemblies = someAssemblies;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the designRoot.
     *
     * @return a Assembly.
     */
    public Assembly getDesignRoot()
    {
        return designRoot;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Module.java.
     *
     * @param someDesignRoot a Assembly.
     */
    public void setDesignRoot(Assembly someDesignRoot)
    {
        designRoot = someDesignRoot;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the manual.
     *
     * @return a Manual.
     */
    public Manual getManual()
    {
        return manual;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Module.java.
     *
     * @param someManual a Manual.
     */
    public void setManual(Manual someManual)
    {
        manual = someManual;
    }
}

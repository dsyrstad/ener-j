package org.enerj.oo7;

import java.util.ArrayList;

public class BaseAssembly extends Assembly
{
    private ArrayList componentsShared;
    private ArrayList componentsPrivate;

    public BaseAssembly()
    {
    }

    public BaseAssembly(int id, int buildDate)
    {
        super(id, buildDate);
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the componentsPrivate.
     *
     * @return a ArrayList.
     */
    public ArrayList getComponentsPrivate()
    {
        return componentsPrivate;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets BaseAssembly.java.
     *
     * @param someComponentsPrivate a ArrayList.
     */
    public void setComponentsPrivate(ArrayList someComponentsPrivate)
    {
        componentsPrivate = someComponentsPrivate;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the componentsShared.
     *
     * @return a ArrayList.
     */
    public ArrayList getComponentsShared()
    {
        return componentsShared;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets BaseAssembly.java.
     *
     * @param someComponentsShared a ArrayList.
     */
    public void setComponentsShared(ArrayList someComponentsShared)
    {
        componentsShared = someComponentsShared;
    }
}

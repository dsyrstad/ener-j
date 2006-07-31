package org.enerj.oo7;

public class Assembly extends DesignObj
{
    private ComplexAssembly superAssembly;

    public Assembly()
    {
    }

    public Assembly(int id, int buildDate)
    {
        super(id, buildDate);
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the superAssembly.
     *
     * @return a ComplexAssembly.
     */
    public ComplexAssembly getSuperAssembly()
    {
        return superAssembly;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Assembly.java.
     *
     * @param someSuperAssembly a ComplexAssembly.
     */
    public void setSuperAssembly(ComplexAssembly someSuperAssembly)
    {
        superAssembly = someSuperAssembly;
    }
}

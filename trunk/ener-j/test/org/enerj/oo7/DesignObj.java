package org.enerj.oo7;


public class DesignObj
{
    private int id;
    private int buildDate;
    private String type;

    public DesignObj()
    {
    }

    public DesignObj(int id, int buildDate)
    {
        this.id = id;
        this.buildDate = buildDate;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the buildDate.
     *
     * @return a int.
     */
    public int getBuildDate()
    {
        return buildDate;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets DesignObj.java.
     *
     * @param someBuildDate a int.
     */
    public void setBuildDate(int someBuildDate)
    {
        buildDate = someBuildDate;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the id.
     *
     * @return a int.
     */
    public int getId()
    {
        return id;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets DesignObj.java.
     *
     * @param someId a int.
     */
    public void setId(int someId)
    {
        id = someId;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the type.
     *
     * @return a String.
     */
    public String getType()
    {
        return type;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets DesignObj.java.
     *
     * @param someType a String.
     */
    public void setType(String someType)
    {
        type = someType;
    }
}

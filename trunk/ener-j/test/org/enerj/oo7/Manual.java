package org.enerj.oo7;


public class Manual
{
    private int id;
    private int textLen;
    private String title;
    private String text;
    private Module module;

    public Manual()
    {
    }

    public Manual(int id, int textLen, String title, String text, Module module)
    {
        this.id = id;
        this.textLen = textLen;
        this.title = title;
        this.text = text;
        this.module = module;
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
     * Sets Manual.java.
     *
     * @param someId a int.
     */
    public void setId(int someId)
    {
        id = someId;
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
     * Sets Manual.java.
     *
     * @param someModule a Module.
     */
    public void setModule(Module someModule)
    {
        module = someModule;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the text.
     *
     * @return a String.
     */
    public String getText()
    {
        return text;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Manual.java.
     *
     * @param someText a String.
     */
    public void setText(String someText)
    {
        text = someText;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the textLen.
     *
     * @return a int.
     */
    public int getTextLen()
    {
        return textLen;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Manual.java.
     *
     * @param someTextLen a int.
     */
    public void setTextLen(int someTextLen)
    {
        textLen = someTextLen;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the title.
     *
     * @return a String.
     */
    public String getTitle()
    {
        return title;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Manual.java.
     *
     * @param someTitle a String.
     */
    public void setTitle(String someTitle)
    {
        title = someTitle;
    }
}

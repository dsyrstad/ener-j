package org.enerj.oo7;

public class Document
{
    private int id;
    private String title;
    private String text;
    private CompositePart part;

    public Document()
    {
    }

    public Document(int id, String title, String text, CompositePart part)
    {
        this.id = id;
        this.title = title;
        this.text = text;
        this.part = part;
        part.setDocument(this);
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
     * Sets Document.java.
     *
     * @param someId a int.
     */
    public void setId(int someId)
    {
        id = someId;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the part.
     *
     * @return a CompositePart.
     */
    public CompositePart getPart()
    {
        return part;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets Document.java.
     *
     * @param somePart a CompositePart.
     */
    public void setPart(CompositePart somePart)
    {
        part = somePart;
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
     * Sets Document.java.
     *
     * @param someText a String.
     */
    public void setText(String someText)
    {
        text = someText;
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
     * Sets Document.java.
     *
     * @param someTitle a String.
     */
    public void setTitle(String someTitle)
    {
        title = someTitle;
    }
}

package org.enerj.oo7;

import java.util.ArrayList;

public class CompositePart extends DesignObj
{
    private Document document;
    private ArrayList parts;
    private AtomicPart rootPart;
    private ArrayList usedInShared;
    private ArrayList usedInPrivate;

    public CompositePart()
    {
    }

    public CompositePart(int id, int buildDate)
    {
        super(id, buildDate);
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the document.
     *
     * @return a Document.
     */
    public Document getDocument()
    {
        return document;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets CompositePart.java.
     *
     * @param someDocument a Document.
     */
    public void setDocument(Document someDocument)
    {
        document = someDocument;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the parts.
     *
     * @return a ArrayList.
     */
    public ArrayList getParts()
    {
        return parts;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets CompositePart.java.
     *
     * @param someParts a ArrayList.
     */
    public void setParts(ArrayList someParts)
    {
        parts = someParts;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the rootPart.
     *
     * @return a AtomicPart.
     */
    public AtomicPart getRootPart()
    {
        return rootPart;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets CompositePart.java.
     *
     * @param someRootPart a AtomicPart.
     */
    public void setRootPart(AtomicPart someRootPart)
    {
        rootPart = someRootPart;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the usedInPrivate.
     *
     * @return a ArrayList.
     */
    public ArrayList getUsedInPrivate()
    {
        return usedInPrivate;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets CompositePart.java.
     *
     * @param someUsedInPrivate a ArrayList.
     */
    public void setUsedInPrivate(ArrayList someUsedInPrivate)
    {
        usedInPrivate = someUsedInPrivate;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the usedInShared.
     *
     * @return a ArrayList.
     */
    public ArrayList getUsedInShared()
    {
        return usedInShared;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets CompositePart.java.
     *
     * @param someUsedInShared a ArrayList.
     */
    public void setUsedInShared(ArrayList someUsedInShared)
    {
        usedInShared = someUsedInShared;
    }
}

package org.enerj.oo7;

import java.util.ArrayList;

public class AtomicPart extends DesignObj
{
    private CompositePart partOf;
    private int x;
    private int y;
    private int docId;
    private ArrayList from;
    private ArrayList to;

    public AtomicPart()
    {
    }

    public AtomicPart(int id, int buildDate, int x, int y, int docId)
    {
        super(id, buildDate);
        this.x = x;
        this.y = y;
        this.docId = docId;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the docId.
     *
     * @return a int.
     */
    public int getDocId()
    {
        return docId;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets AtomicPart.java.
     *
     * @param someDocId a int.
     */
    public void setDocId(int someDocId)
    {
        docId = someDocId;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the from.
     *
     * @return a ArrayList.
     */
    public ArrayList getFrom()
    {
        return from;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets AtomicPart.java.
     *
     * @param someFrom a ArrayList.
     */
    public void setFrom(ArrayList someFrom)
    {
        from = someFrom;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the partOf.
     *
     * @return a CompositePart.
     */
    public CompositePart getPartOf()
    {
        return partOf;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets AtomicPart.java.
     *
     * @param somePartOf a CompositePart.
     */
    public void setPartOf(CompositePart somePartOf)
    {
        partOf = somePartOf;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the to.
     *
     * @return a ArrayList.
     */
    public ArrayList getTo()
    {
        return to;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets to.
     *
     * @param someTo a ArrayList.
     */
    public void setTo(ArrayList someTo)
    {
        to = someTo;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the x.
     *
     * @return a int.
     */
    public int getX()
    {
        return x;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets X.
     *
     * @param someX a int.
     */
    public void setX(int someX)
    {
        x = someX;
    }

    //--------------------------------------------------------------------------------
    /**
     * Gets the y.
     *
     * @return a int.
     */
    public int getY()
    {
        return y;
    }

    //--------------------------------------------------------------------------------
    /**
     * Sets AtomicPart.java.
     *
     * @param someY a int.
     */
    public void setY(int someY)
    {
        y = someY;
    }
}

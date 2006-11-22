/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
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


    /**
     * Gets the id.
     *
     * @return a int.
     */
    public int getId()
    {
        return id;
    }


    /**
     * Sets Document.java.
     *
     * @param someId a int.
     */
    public void setId(int someId)
    {
        id = someId;
    }


    /**
     * Gets the part.
     *
     * @return a CompositePart.
     */
    public CompositePart getPart()
    {
        return part;
    }


    /**
     * Sets Document.java.
     *
     * @param somePart a CompositePart.
     */
    public void setPart(CompositePart somePart)
    {
        part = somePart;
    }


    /**
     * Gets the text.
     *
     * @return a String.
     */
    public String getText()
    {
        return text;
    }


    /**
     * Sets Document.java.
     *
     * @param someText a String.
     */
    public void setText(String someText)
    {
        text = someText;
    }


    /**
     * Gets the title.
     *
     * @return a String.
     */
    public String getTitle()
    {
        return title;
    }


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

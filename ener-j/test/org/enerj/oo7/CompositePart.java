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

import java.util.ArrayList;
import java.util.List;

public class CompositePart extends DesignObj
{
    private Document document;
    private List<AtomicPart> parts;
    private AtomicPart rootPart;
    private List usedInShared;
    private List usedInPrivate;

    public CompositePart()
    {
    }

    public CompositePart(int id, int buildDate)
    {
        super(id, buildDate);
    }


    /**
     * Gets the document.
     *
     * @return a Document.
     */
    public Document getDocument()
    {
        return document;
    }


    /**
     * Sets CompositePart.java.
     *
     * @param someDocument a Document.
     */
    public void setDocument(Document someDocument)
    {
        document = someDocument;
    }


    /**
     * Gets the parts.
     *
     * @return a List.
     */
    public List<AtomicPart> getParts()
    {
        return parts;
    }


    /**
     * Sets CompositePart.java.
     *
     * @param someParts a ArrayList.
     */
    public void setParts(List<AtomicPart> someParts)
    {
        parts = someParts;
    }


    /**
     * Gets the rootPart.
     *
     * @return a AtomicPart.
     */
    public AtomicPart getRootPart()
    {
        return rootPart;
    }


    /**
     * Sets CompositePart.java.
     *
     * @param someRootPart a AtomicPart.
     */
    public void setRootPart(AtomicPart someRootPart)
    {
        rootPart = someRootPart;
    }


    /**
     * Gets the usedInPrivate.
     *
     * @return a List.
     */
    public List getUsedInPrivate()
    {
        return usedInPrivate;
    }


    /**
     * Sets CompositePart.java.
     *
     * @param someUsedInPrivate a List.
     */
    public void setUsedInPrivate(List someUsedInPrivate)
    {
        usedInPrivate = someUsedInPrivate;
    }


    /**
     * Gets the usedInShared.
     *
     * @return a ArrayList.
     */
    public List getUsedInShared()
    {
        return usedInShared;
    }


    /**
     * Sets CompositePart.java.
     *
     * @param someUsedInShared a List.
     */
    public void setUsedInShared(List someUsedInShared)
    {
        usedInShared = someUsedInShared;
    }
}

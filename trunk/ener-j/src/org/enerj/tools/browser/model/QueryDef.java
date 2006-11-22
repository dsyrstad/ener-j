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
//Ener-J
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/tools/enerjbrowser/model/QueryDef.java,v 1.1 2006/02/03 19:41:01 dsyrstad Exp $

package org.enerj.tools.browser.model;

/**
 * Query definition and result used by the model. <p>
 * 
 * @version $Id: QueryDef.java,v 1.1 2006/02/03 19:41:01 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class QueryDef
{
    private String mQuery;
    private Object mResult;
    

    QueryDef(String aQuery, Object aResult)
    {
        mQuery = aQuery;
        mResult = aResult;
    }


    public String getQuery()
    {
        return mQuery;
    }


    public Object getResult()
    {
        return mResult;
    }
    

    public String toString() 
    {
        return mQuery;
    }
}

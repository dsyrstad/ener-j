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
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/util/FileUtil.java,v 1.4 2006/06/09 02:39:28 dsyrstad Exp $

package org.enerj.util;

import java.io.File;
import java.util.StringTokenizer;

/**
 * File-based utilities. <p>
 * 
 * @version $Id: FileUtil.java,v 1.4 2006/06/09 02:39:28 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class FileUtil
{

    // Don't allow construction
    private FileUtil() 
    {
    }
    

    /**
     * Finds a file on the given path.
     *
     * @param aFileName the base file name.
     * @param aDirList a list of directory names, separated by File.pathSeparator.
     * 
     * @return the corresponding File, which is guaranteed to be a normal readable file, 
     *  or null if the aFileName could not be found.
     */
    public static File findFileOnPath(String aFileName, String aDirList)
    {
        StringTokenizer tokenizer = new StringTokenizer(aDirList, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            String dir = tokenizer.nextToken();
            File file = new File(dir, aFileName);
            if (file.exists() && file.isFile() && file.canRead()) {
                return file;
            }
        }
        
        return null;
    }
    

    /**
     * Gets the parent directory of aDirectoryName. Return aDirectoryName
     * if it does not have a File.separatorChar.
     *
     * @param aDirectoryName the directory name.
     *
     * @return the parent directory name.
     */
    public static String getParentDirectory(String aDirectoryName)
    {
        int idx = aDirectoryName.lastIndexOf(File.separatorChar);
        if (idx < 0) {
            return aDirectoryName; 
        }
        
        return aDirectoryName.substring(0, idx);
    }
   
}

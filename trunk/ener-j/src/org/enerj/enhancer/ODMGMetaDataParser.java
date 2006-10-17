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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/ODMGMetaDataParser.java,v 1.3 2006/05/03 21:14:26 dsyrstad Exp $

package org.enerj.enhancer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.LinkedList;
import java.util.List;


/**
 * Parses an ODMG+ Java meta data file. <p>
 * 
 * @version $Id: ODMGMetaDataParser.java,v 1.3 2006/05/03 21:14:26 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
class ODMGMetaDataParser
{
    // Context used while parsing
    private String mFileName;
    private StreamTokenizer mTokenizer;
    private FileReader fileReader;
    private MetaData mMetaData;

    //--------------------------------------------------------------------------------
    /**
     * Construct a ODMGMetaDataParser. 
     *
     * @param aPropFileName the property file to be parsed.
     * @param aMetaData the MetaData to receive the results of the parsing.
     *
     * @throws MetaDataException if an error occurs
     */
    public ODMGMetaDataParser(String aPropFileName, MetaData aMetaData) throws MetaDataException
    {
        mFileName = aPropFileName;
        mMetaData = aMetaData;
        
        try {
            fileReader = new FileReader(aPropFileName);
        }
        catch (FileNotFoundException e) {
            throw new MetaDataException("Error reading meta data", e);
        }
       
        boolean success = false;
        try {
            mTokenizer = new StreamTokenizer( new BufferedReader(fileReader) );
            mTokenizer.resetSyntax();
            // TODO we should verify java identifier syntax here.
            // TODO I think unicode letters and numbers are allowed.
            mTokenizer.wordChars('a', 'z');
            mTokenizer.wordChars('A', 'Z');
            mTokenizer.wordChars('0', '9');
            mTokenizer.wordChars('_', '_');
            mTokenizer.wordChars('.', '.');
            mTokenizer.wordChars('*', '*');
            mTokenizer.wordChars('$', '$'); // For inner classes
            mTokenizer.wordChars(128 + 32, 255);
            mTokenizer.whitespaceChars(0, ' ');
            mTokenizer.commentChar('#');
            mTokenizer.commentChar(';');
            success = true;
        }
        finally {
            if (!success) {
                try {
                    fileReader.close();
                }
                catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    //----------------------------------------------------------------------
    /**
     * Parse the property file into our model.
     *
     * @throws MetaDataException if an error occurs
     */
    void parse() throws MetaDataException
    {
        try {
            parseTopLevelSections();
        }
        catch (IOException e) {
            throw new MetaDataException("Error reading meta data: " + mFileName, e);
        }
        finally {
            try {
                fileReader.close();
            }
            catch (IOException e) {
                // Ignore
            }
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Generate a error message via an exception.
     */
    private MetaDataException generateError(String aMsg) 
    {
        return new MetaDataException("Error: " + mFileName + " at line " + mTokenizer.lineno() + ": " + aMsg);
    }
    
    //----------------------------------------------------------------------
    /**
     * Expects to parse a series of "class" sections.
     */
    private void parseTopLevelSections() throws MetaDataException, IOException
    {
        int tokenType;
        
        while ((tokenType = mTokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
            if (tokenType == StreamTokenizer.TT_WORD && mTokenizer.sval.equals("class")) {
                parseClass();
            }
            else {
                throw generateError("Expected 'class', got " + mTokenizer.sval);
            }
        }
    }

    //----------------------------------------------------------------------
    /**
     * Having parsed a "class" token, parse the rest of the section.
     */
    private void parseClass() throws MetaDataException, IOException
    {
        if (mTokenizer.nextToken() != StreamTokenizer.TT_WORD) {
            throw generateError("Expected class name, package name, or '*'");
        }

        String className = mTokenizer.sval;
        
        List<Pair> pairList = parseKeyValuePairs();
        // Default.
        int persistentType = ClassDef.TYPE_CAPABLE;
        for (Pair pair : pairList) {
            // The only key ODMG 3.0 defines is "persistent"
            if (pair.mKey.equals("persistent")) {
                if (pair.mValue.equals("capable")) {
                    persistentType = ClassDef.TYPE_CAPABLE;
                }
                else if (pair.mValue.equals("not")) {
                    persistentType = ClassDef.TYPE_NOT_CAPABLE;
                }
                else if (pair.mValue.equals("aware")) {
                    persistentType = ClassDef.TYPE_AWARE;
                }
                else if (pair.mValue.equals("serialized")) {
                    throw generateError("'serialized' is not supported by Ener-J.");
                }
                else {
                    throw generateError("Unrecognized persistent value '" + pair.mValue + "'.");
                }
            }
            else {
                throw generateError("Unrecognized class key '" + pair.mKey + "'.");
            }
        }
        
        FieldDef[] fieldDefs = parseFieldDefs();
        ClassDef classDef = new ClassDef(className, persistentType, fieldDefs);
        
        // Check if class name is '*' or a package name. '*' means we have to 
        // recursively descend the hierarchies of the sourcepath and add all classes 
        // we find with this default definition. A package name, implicitly denoted by 
        // finding a matching directory name somewhere in the sourcepath, means we
        // have to add all classes in the directory using this ClassDef.
        // If the class name really is just a class name, then it can override a 
        // previously added defintion.
        // Beyond ODMG: We also allow packagename.* to recursive scan all packages below the specified one.
        String packageName = className;
        boolean recursePackage = false;
        if (packageName.endsWith(".*")) {
            packageName = packageName.substring(0, packageName.length() - 2);
            recursePackage = true;
        }

        if (className.equals("*")) {
            // Global default meta data
            mMetaData.setDefaultClassDef(classDef);
        }
        else if (recursePackage || Package.getPackage(packageName) != null) {
            // Package-default meta data
            if (recursePackage) {
                mMetaData.addRecursivePackageDefaultClassDef(packageName, classDef);
            }
            else {
                mMetaData.addPackageDefaultClassDef(packageName, classDef);
            }
        }
        else {
            // Class-specific meta data
            mMetaData.addClassDef(className, classDef);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Parse zero or more "field" sections.
     */
    private FieldDef[] parseFieldDefs() throws MetaDataException, IOException
    {
        List<FieldDef> fieldDefs = new LinkedList<FieldDef>();
        int tokenType;
        while ((tokenType = mTokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
            if (tokenType == StreamTokenizer.TT_WORD && mTokenizer.sval.equals("class")) {
                mTokenizer.pushBack();
                break;
            }

            if (tokenType == StreamTokenizer.TT_WORD && mTokenizer.sval.equals("field")) {
                tokenType = mTokenizer.nextToken();
                if (tokenType != StreamTokenizer.TT_WORD) {
                    throw generateError("Excpected field name to follow 'field'");
                }
                
                String fieldName = mTokenizer.sval;
                int persistentOverride = FieldDef.PERSISTENT_USE_FIELDDEF;
                String refersTo = null;
                String inverse = null;
                for(Pair pair : parseKeyValuePairs()) {
                    if (pair.mKey.equals("transient")) {
                        if (pair.mValue.equals("true")) {
                            persistentOverride = FieldDef.PERSISTENT_NO;
                        }
                        else if (pair.mValue.equals("false")) {
                            persistentOverride = FieldDef.PERSISTENT_YES;
                        }
                        else {
                            throw generateError("Expected 'true' or 'false' for 'transient='");
                        }
                    }
                    else if (pair.mKey.equals("refersTo")) {
                        refersTo = pair.mValue;
                    }
                    else if (pair.mKey.equals("inverse")) {
                        inverse = pair.mValue;
                    }
                    else {
                        throw generateError("Expected 'transient', 'refersTo', or 'inverse'. Got '" + pair.mKey + "'.");
                    }
                } // End while...pairs
                
                fieldDefs.add( new FieldDef(fieldName, persistentOverride, refersTo, inverse) );
            }
            else {
                throw generateError("Expected 'field', got " + mTokenizer.sval);
            }
        } // End while...token
        
        FieldDef[] fieldDefArray = new FieldDef[ fieldDefs.size() ];
        fieldDefs.toArray(fieldDefArray);
        
        return fieldDefArray;
    }
    
    //----------------------------------------------------------------------
    /**
     * Parse a set of key/value pairs until we see "class" or "field".
     *
     * @return a List of Pair, which may be empty.
     *
     * @throws Exception if an error occurs.
     */
    private List<Pair> parseKeyValuePairs() throws MetaDataException, IOException
    {
        List<Pair> pairs = new LinkedList<Pair>();

        while (mTokenizer.nextToken() == StreamTokenizer.TT_WORD) {
            if (mTokenizer.sval.equals("class") || mTokenizer.sval.equals("field")) {
                mTokenizer.pushBack();
                break;
            }
            
            String key = mTokenizer.sval;
            if (mTokenizer.nextToken() != '=') {
                throw generateError("Expected '='");
            }
            
            if (mTokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                throw generateError("Expected a value");
            }
            
            String value = mTokenizer.sval;
            pairs.add( new Pair(key, value) );
        }
        
        return pairs;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * A key/value pair.
     */
    private static final class Pair
    {
        String mKey;
        String mValue;

        //----------------------------------------------------------------------
        Pair(String aKey, String aValue) 
        {
            mKey = aKey;
            mValue = aValue;
        }
    }
}

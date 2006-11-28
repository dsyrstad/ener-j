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
//Copyright 2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/InstrumentationAgent.java,v 1.14 2006/06/03 20:30:53 dsyrstad Exp $

package org.enerj.enhancer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * Contains the Instrumentation premain method to handle on-the-fly enhancement. 
 * Meta data is read from annotations on the classes and packages.  
 * The Java system property "enerj.metadata" may be set to a global default meta data file name.
 * If this is set, any meta data in this file overrides the annotations.
 * 
 * @version $Id: InstrumentationAgent.java,v 1.14 2006/06/03 20:30:53 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class InstrumentationAgent implements ClassFileTransformer
{
    private static final Logger logger = Logger.getLogger(InstrumentationAgent.class.getName());
    
    private static InstrumentationAgent sInstance = null;
    
    private MetaData mMetaData;
    

    private InstrumentationAgent()
    {
        sInstance = this;
    }


    /**
     * Agent premain for java.lang.instrument. Handles registration of on-the-fly enhancement. 
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(String someAgentArgs, Instrumentation anInst)
    {
        anInst.addTransformer( new InstrumentationAgent() );
    }

    

    /**
     * Returns the runtime instance of InstrumentationAgent.
     *
     * @return the instance, or null if runtime instrumentation is not in effect. 
     */
    public static InstrumentationAgent getInstance()
    {
        return sInstance;
    }
    

    /** 
     * {@inheritDoc}
     * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader, java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])
     * 
     * This is synchronized because mMetaData is shared.
     */
    public byte[] transform(ClassLoader aLoader, String aClassName, Class<?> aClassBeingRedefined, ProtectionDomain aProtectionDomain, byte[] aClassfileBuffer) throws IllegalClassFormatException
    {
        // We can be active and the Finalizer can reenter ourself, which causes deadlock. Avoid this.
        // Also we don't enhance java system classes.
        if (Thread.currentThread().getName().equals("Finalizer") || aClassName.startsWith("java/") || aClassName.startsWith("sun/")) {
            return null;
        }
        
        try {
            synchronized (this) {
                // Lazily init metadata.
                if (mMetaData == null) {
                    List<String> metaFiles = new ArrayList<String>(1);
                    String metaFile = System.getProperty("enerj.metadata");
                    if (metaFile != null) {
                        metaFiles.add(metaFile);
                    }
                    
                    mMetaData = new MetaData(metaFiles);
                }
                
                String dottedClassName = aClassName.replace('/', '.');
                if (!mMetaData.isClassEnhanceable(dottedClassName, aClassfileBuffer)) {
                    return null;
                }
                
                //logger.finest("Enhancing " + dottedClassName);
                //long start = System.currentTimeMillis();
                ClassReader classReader = new ClassReader(aClassfileBuffer);
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                ClassEnhancer classEnhancer;
    
                classEnhancer = new ClassEnhancer(classWriter, dottedClassName, aClassfileBuffer, mMetaData);
                classReader.accept(classEnhancer, null, 0);
    
                byte[] enhancedBytes = classWriter.toByteArray();
                
                //if (logger.isLoggable(Level.FINE)) logger.fine("Time to enhance " + dottedClassName + ": " + (System.currentTimeMillis() - start));
                return enhancedBytes;
            }
        }
        catch (AlreadyEnhancedException e) {
            return null;
        }
        catch (SkipEnhancementException e) {
            return null;
        }
        catch (Throwable e) {
            logger.log(Level.SEVERE, "Error enhancing " + aClassName, e);
            return null;
        }
        finally {
        }
    }
}

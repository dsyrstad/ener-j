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
// Ener-J
// Copyright 2001-2003 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/test/org/enerj/server/LockSchedulerTest.java,v 1.4 2006/01/12 04:39:45 dsyrstad Exp $

package org.enerj.server;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Tests LockScheduler. <p>
 *
 * @version $Id: LockSchedulerTest.java,v 1.4 2006/01/12 04:39:45 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public class LockSchedulerTest extends AbstractLockServerTest
{
    
    //----------------------------------------------------------------------
    public LockSchedulerTest(String aTestName) 
    {
        super(aTestName);
    }
    
    //----------------------------------------------------------------------
    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(LockSchedulerTest.class);
    }
    
    //----------------------------------------------------------------------
    public static Test suite() 
    {
        return new TestSuite(LockSchedulerTest.class);
    }
    
    //----------------------------------------------------------------------
    protected Properties getWaitsForProperties()
    {
        Properties props = new Properties( System.getProperties() );
        props.setProperty("LockScheduler.initialNumObjs", "1000");
        props.setProperty("LockScheduler.deadlockAlgorithm", "Waits-For");
        return props;
    }
        
    //----------------------------------------------------------------------
    protected Properties getTimestampProperties()
    {
        Properties props = new Properties( System.getProperties() );
        props.setProperty("LockScheduler.initialNumObjs", "1000");
        props.setProperty("LockScheduler.deadlockAlgorithm", "Timestamp");
        return props;
    }
    
    //----------------------------------------------------------------------
    protected String getLockServerClassName()
    {
        return LockScheduler.class.getName();
    }
}

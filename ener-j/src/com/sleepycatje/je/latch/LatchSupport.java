/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: LatchSupport.java,v 1.9.2.1 2007/02/01 14:49:46 cwl Exp $
 */

package com.sleepycatje.je.latch;

import com.sleepycatje.je.dbi.EnvironmentImpl;

/**
 * Various constructs to support Latches.  Switch hitting for 1.4 vs Java 5
 * JVM latch implementation (i.e. our's vs the JVM's), and assert-based
 * latch counting code.
 */
public class LatchSupport {

    private static String DISABLE_JAVA5_LATCHES = "je.disable.java5.latches";

    private static Class JAVA5_LATCH_CLASS = null;

    private static Class JAVA5_SHARED_LATCH_CLASS = null;

    static {
	try {
	    if (System.getProperty(DISABLE_JAVA5_LATCHES) == null) {
		Class.forName("java.util.concurrent.locks.ReentrantLock");
		JAVA5_LATCH_CLASS = Class.forName
		    ("com.sleepycatje.je.latch.Java5LatchImpl");
	    }
	} catch (ClassNotFoundException CNFE) {
	}
    }

    static {
	try {
	    if (System.getProperty(DISABLE_JAVA5_LATCHES) == null) {
		Class.forName
		    ("java.util.concurrent.locks.ReentrantReadWriteLock");
		JAVA5_SHARED_LATCH_CLASS = Class.forName
		    ("com.sleepycatje.je.latch.Java5SharedLatchImpl");
	    }
	} catch (ClassNotFoundException CNFE) {
	}
    }

    public static Class getJava5LatchClass() {
	return JAVA5_LATCH_CLASS;
    }

    public static Latch makeLatch(String name, EnvironmentImpl env) {
	if (JAVA5_LATCH_CLASS == null) {
	    return new LatchImpl(name, env);
	} else {
	    try {
		Latch ret = (Latch) JAVA5_LATCH_CLASS.newInstance();
		ret.setName(name);
		return ret;
	    } catch (InstantiationException IE) {
	    } catch (IllegalAccessException IAE) {
	    }

	    /* Something bad happened.  Revert back to our 1.4 latches. */
	    JAVA5_LATCH_CLASS = null;
	    return new LatchImpl(name, env);
	}
    }

    public static Latch makeLatch(EnvironmentImpl env) {
	if (JAVA5_LATCH_CLASS == null) {
	    return new LatchImpl(env);
	} else {
	    try {
		return (Latch) JAVA5_LATCH_CLASS.newInstance();
	    } catch (InstantiationException IE) {
	    } catch (IllegalAccessException IAE) {
	    }

	    /* Something bad happened.  Revert back to our 1.4 latches. */
	    JAVA5_LATCH_CLASS = null;
	    return new LatchImpl(env);
	}
    }

    public static SharedLatch makeSharedLatch(String name,
					      EnvironmentImpl env) {
	if (JAVA5_SHARED_LATCH_CLASS == null) {
	    return new SharedLatchImpl(name, env);
	} else {
	    try {
		SharedLatch ret = (SharedLatch)
		    JAVA5_SHARED_LATCH_CLASS.newInstance();
		ret.setName(name);
		return ret;
	    } catch (InstantiationException IE) {
	    } catch (IllegalAccessException IAE) {
	    }

	    /* Something bad happened.  Revert back to our 1.4 latches. */
	    JAVA5_SHARED_LATCH_CLASS = null;
	    return new SharedLatchImpl(name, env);
	}
    }

    /* Used for debugging */
    static LatchTable latchTable = new LatchTable("LatchImpl");

    /**
     * Only call under the assert system. This records and counts held latches.
     */
    static public int countLatchesHeld() {

        return latchTable.countLatchesHeld();
    }

    static public void dumpLatchesHeld() {

        System.out.println(latchesHeldToString());
    }

    static public String latchesHeldToString() {

        return latchTable.latchesHeldToString();
    }

    static public void clearNotes() {

        latchTable.clearNotes();
    }
}

/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002,2007 Oracle.  All rights reserved.
 *
 * $Id: DbStat.java,v 1.21.2.1 2007/02/01 14:49:54 cwl Exp $
 */

package com.sleepycatje.je.util;

import java.io.File;
import java.io.PrintStream;
import java.util.logging.Level;

import com.sleepycatje.je.Database;
import com.sleepycatje.je.DatabaseConfig;
import com.sleepycatje.je.DatabaseException;
import com.sleepycatje.je.DatabaseStats;
import com.sleepycatje.je.DbInternal;
import com.sleepycatje.je.Environment;
import com.sleepycatje.je.JEVersion;
import com.sleepycatje.je.StatsConfig;
import com.sleepycatje.je.utilint.CmdUtil;
import com.sleepycatje.je.utilint.Tracer;

public class DbStat extends DbVerify {
    private String usageString =
	"usage: " + CmdUtil.getJavaCommand(DbStat.class) + "\n" +
	"               [-V] -s database -h dbEnvHome [-v progressInterval]\n";

    private int progressInterval = 0;

    static public void main(String argv[])
	throws DatabaseException {

	DbStat stat = new DbStat();
	stat.parseArgs(argv);

	int ret = 0;
	try {
	    if (!stat.stats(System.err)) {
		ret = 1;
	    }
	} catch (Throwable T) {
	    ret = 1;
	    T.printStackTrace(System.err);
	}

	try {
	    stat.env.close();
	} catch (Throwable ignored) {

	    /* 
	     * Klockwork - ok
	     * Don't say anything about exceptions here.
	     */
	}
	System.exit(ret);
    }

    protected DbStat() {
    }

    public DbStat(Environment env, String dbName) {
	super(env, dbName, false);
    }

    protected void printUsage(String msg) {
	System.err.println(msg);
	System.err.println(usageString);
	System.exit(-1);
    }

    protected void parseArgs(String argv[]) {

	int argc = 0;
	int nArgs = argv.length;
	while (argc < nArgs) {
	    String thisArg = argv[argc++];
	    if (thisArg.equals("-V")) {
		System.out.println(JEVersion.CURRENT_VERSION);
		System.exit(0);
	    } else if (thisArg.equals("-h")) {
		if (argc < nArgs) {
		    envHome = new File(argv[argc++]);
		} else {
		    printUsage("-h requires an argument");
		}
	    } else if (thisArg.equals("-s")) {
		if (argc < nArgs) {
		    dbName = argv[argc++];
		} else {
		    printUsage("-s requires an argument");
		}
	    } else if (thisArg.equals("-v")) {
		if (argc < nArgs) {
		    progressInterval = Integer.parseInt(argv[argc++]);
		    if (progressInterval <= 0) {
			printUsage("-v requires a positive argument");
		    }
		} else {
		    printUsage("-v requires an argument");
		}
	    }
	}

	if (envHome == null) {
	    printUsage("-h is a required argument");
	}

	if (dbName == null) {
	    printUsage("-s is a required argument");
	}
    }

    public boolean stats(PrintStream out)
	throws DatabaseException {

	try {
	    openEnv();

	    Tracer.trace(Level.INFO, DbInternal.envGetEnvironmentImpl(env),
			 "DbStat.stats of " + dbName + " starting");

	    DatabaseConfig dbConfig = new DatabaseConfig();
	    dbConfig.setReadOnly(true);
	    dbConfig.setAllowCreate(false);
	    DbInternal.setUseExistingConfig(dbConfig, true);
	    Database db = env.openDatabase(null, dbName, dbConfig);

	    StatsConfig statsConfig = new StatsConfig();
	    if (progressInterval > 0) {
		statsConfig.setShowProgressInterval(progressInterval);
		statsConfig.setShowProgressStream(out);
	    }

	    DatabaseStats stats = db.getStats(statsConfig);
	    out.println(stats);

	    db.close();
	    Tracer.trace(Level.INFO, DbInternal.envGetEnvironmentImpl(env),
			 "DbStat.stats of " + dbName + " ending");
	} catch (DatabaseException DE) {
	    return false;
	}
	return true;
    }
}

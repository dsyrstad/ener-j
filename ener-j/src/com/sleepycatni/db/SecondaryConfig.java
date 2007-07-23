/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002-2006
 *	Oracle Corporation.  All rights reserved.
 *
 * $Id: SecondaryConfig.java,v 12.4 2006/08/24 14:46:09 bostic Exp $
 */

package com.sleepycatni.db;

import com.sleepycatni.db.internal.Db;
import com.sleepycatni.db.internal.DbConstants;
import com.sleepycatni.db.internal.DbEnv;
import com.sleepycatni.db.internal.DbTxn;

public class SecondaryConfig extends DatabaseConfig implements Cloneable {
    /*
     * For internal use, to allow null as a valid value for
     * the config parameter.
     */
    public static final SecondaryConfig DEFAULT = new SecondaryConfig();

    /* package */
    static SecondaryConfig checkNull(SecondaryConfig config) {
        return (config == null) ? DEFAULT : config;
    }

    private boolean allowPopulate;
    private boolean immutableSecondaryKey;
    private SecondaryKeyCreator keyCreator;

    public SecondaryConfig() {
    }

    public void setKeyCreator(final SecondaryKeyCreator keyCreator) {
        this.keyCreator = keyCreator;
    }

    public SecondaryKeyCreator getKeyCreator() {
        return keyCreator;
    }

    public void setAllowPopulate(final boolean allowPopulate) {
        this.allowPopulate = allowPopulate;
    }

    public boolean getAllowPopulate() {
        return allowPopulate;
    }

    public void setImmutableSecondaryKey(final boolean immutableSecondaryKey) {
        this.immutableSecondaryKey = immutableSecondaryKey;
    }

    public boolean getImmutableSecondaryKey() {
        return immutableSecondaryKey;
    }

    /* package */
    Db openSecondaryDatabase(final DbEnv dbenv,
                             final DbTxn txn,
                             final String fileName,
                             final String databaseName,
                             final Db primary)
        throws DatabaseException, java.io.FileNotFoundException {

        int associateFlags = 0;
        associateFlags |= allowPopulate ? DbConstants.DB_CREATE : 0;
        if (getTransactional() && txn == null)
            associateFlags |= DbConstants.DB_AUTO_COMMIT;
        if (immutableSecondaryKey)
            associateFlags |= DbConstants.DB_IMMUTABLE_KEY;

        final Db db = super.openDatabase(dbenv, txn, fileName, databaseName);
        boolean succeeded = false;
        try {
            primary.associate(txn, db, keyCreator, associateFlags);
            succeeded = true;
            return db;
        } finally {
            if (!succeeded)
                try {
                    db.close(0);
                } catch (Throwable t) {
                    // Ignore it -- there is already an exception in flight.
                }
        }
    }

    /* package */
    SecondaryConfig(final Db db)
        throws DatabaseException {

        super(db);

        // XXX: There is no way to find out whether allowPopulate was set.
        allowPopulate = false;
        keyCreator = db.get_seckey_create();
    }
}


/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 1997-2006
 *	Oracle Corporation.  All rights reserved.
 *
 * $Id: MessageHandler.java,v 12.3 2006/08/24 14:46:08 bostic Exp $
 */
package com.sleepycatni.db;

public interface MessageHandler {
    void message(Environment dbenv, String message);
}

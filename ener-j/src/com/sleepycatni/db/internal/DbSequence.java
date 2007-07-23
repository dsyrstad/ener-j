/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.29
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.sleepycatni.db.internal;

import com.sleepycatni.db.*;
import java.util.Comparator;

public class DbSequence {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected DbSequence(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(DbSequence obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  /* package */ void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      throw new UnsupportedOperationException("C++ destructor does not have public access");
    }
    swigCPtr = 0;
  }

	public Sequence wrapper;

	public synchronized void close(int flags) throws DatabaseException {
		try {
			close0(flags);
		} finally {
			swigCPtr = 0;
		}
	}

	public synchronized void remove(DbTxn txn, int flags)
	    throws DatabaseException {
		try {
			remove0(txn, flags);
		} finally {
			swigCPtr = 0;
		}
	}

  public DbSequence(Db db, int flags) throws com.sleepycatni.db.DatabaseException {
    this(db_javaJNI.new_DbSequence(Db.getCPtr(db), flags), true);
  }

  /* package */ void close0(int flags) { db_javaJNI.DbSequence_close0(swigCPtr, flags); }

  public long get(DbTxn txnid, int delta, int flags) throws com.sleepycatni.db.DatabaseException { return db_javaJNI.DbSequence_get(swigCPtr, DbTxn.getCPtr(txnid), delta, flags); }

  public int get_cachesize() throws com.sleepycatni.db.DatabaseException { return db_javaJNI.DbSequence_get_cachesize(swigCPtr); }

  public Db get_db() throws com.sleepycatni.db.DatabaseException {
    long cPtr = db_javaJNI.DbSequence_get_db(swigCPtr);
    return (cPtr == 0) ? null : new Db(cPtr, false);
  }

  public int get_flags() throws com.sleepycatni.db.DatabaseException { return db_javaJNI.DbSequence_get_flags(swigCPtr); }

  public void get_key(com.sleepycatni.db.DatabaseEntry key) throws com.sleepycatni.db.DatabaseException { db_javaJNI.DbSequence_get_key(swigCPtr, key); }

  public long get_range_min() throws com.sleepycatni.db.DatabaseException { return db_javaJNI.DbSequence_get_range_min(swigCPtr); }

  public long get_range_max() throws com.sleepycatni.db.DatabaseException { return db_javaJNI.DbSequence_get_range_max(swigCPtr); }

  public void initial_value(long val) throws com.sleepycatni.db.DatabaseException { db_javaJNI.DbSequence_initial_value(swigCPtr, val); }

  public void open(DbTxn txnid, com.sleepycatni.db.DatabaseEntry key, int flags) throws com.sleepycatni.db.DatabaseException { db_javaJNI.DbSequence_open(swigCPtr, DbTxn.getCPtr(txnid), key, flags); }

  /* package */ void remove0(DbTxn txnid, int flags) { db_javaJNI.DbSequence_remove0(swigCPtr, DbTxn.getCPtr(txnid), flags); }

  public void set_cachesize(int size) throws com.sleepycatni.db.DatabaseException { db_javaJNI.DbSequence_set_cachesize(swigCPtr, size); }

  public void set_flags(int flags) throws com.sleepycatni.db.DatabaseException { db_javaJNI.DbSequence_set_flags(swigCPtr, flags); }

  public void set_range(long min, long max) throws com.sleepycatni.db.DatabaseException { db_javaJNI.DbSequence_set_range(swigCPtr, min, max); }

  public com.sleepycatni.db.SequenceStats stat(int flags) throws com.sleepycatni.db.DatabaseException { return db_javaJNI.DbSequence_stat(swigCPtr, flags); }

}

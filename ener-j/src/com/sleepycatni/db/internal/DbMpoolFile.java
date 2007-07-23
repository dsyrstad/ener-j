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

public class DbMpoolFile {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected DbMpoolFile(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(DbMpoolFile obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  /* package */ void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      throw new UnsupportedOperationException("C++ destructor does not have public access");
    }
    swigCPtr = 0;
  }

  public int get_priority() throws com.sleepycatni.db.DatabaseException {
    return db_javaJNI.DbMpoolFile_get_priority(swigCPtr);
  }

  public void set_priority(int priority) throws com.sleepycatni.db.DatabaseException { db_javaJNI.DbMpoolFile_set_priority(swigCPtr, priority); }

  public int get_flags() throws com.sleepycatni.db.DatabaseException { return db_javaJNI.DbMpoolFile_get_flags(swigCPtr); }

  public void set_flags(int flags, boolean onoff) throws com.sleepycatni.db.DatabaseException { db_javaJNI.DbMpoolFile_set_flags(swigCPtr, flags, onoff); }

  public long get_maxsize() throws com.sleepycatni.db.DatabaseException {
    return db_javaJNI.DbMpoolFile_get_maxsize(swigCPtr);
  }

  public void set_maxsize(long bytes) throws com.sleepycatni.db.DatabaseException { db_javaJNI.DbMpoolFile_set_maxsize(swigCPtr, bytes); }

}

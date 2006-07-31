// Ener-J
// Copyright 2001-2004 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/server/MetaObjectServer.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $

package org.enerj.server;


/**
 * Ener-J Meta Object Server interface. This is the main interface used by the client
 * API. Whereas the ObjectServer provides basic object persistence and transaction management, a MetaObjectServer
 * provides higher-level database services, such as extents, indexes, access control, etc.
 * These services require access to objects in the database. ObjectServer only manipulates
 * objects as persistent blobs of bytes.<p>
 *
 * Note that this extends ObjectServer. This allow MetaObjectServer to intercept
 * object stores and update extents and indexes. A MetaObjectServer may directly implement
 * all of the ObjectServer interface, or it may delegate to another server implementing
 * ObjectServer. DefaultMetaObjectServer provides the standard Ener-J implementation and
 * can delegate to any other ObjectServer for persistence.<p>
 *
 * Beyond this interface, the following additional requirements
 * are placed on an MetaObjectServer implementation:<p>
 *
 * 1. A static factory method "connect" must be defined. The definition
 *    for the method is:<p>
 *
 * public static MetaObjectServerSession connect(Properties someProperties) throws ODMGException;<p>
 *
 * The 'someProperties' parameter contains the connection properties. Standard property keys are:<br>
 * <ul>
 * <li>vo.dbname - the database name. Required.</li>
 * <li>vo.accessmode - the database access mode. This is the integer value of the org.odmg.Database access modes. Required.</li>
 * <li>vo.username - the username for the database. Optional.</li>
 * <li>vo.password - the password for username. Optional.</li>
 * <li>vo.hostname - the host to connect to. Required for a remote connection. Does not exist for a in-client local connection.</li>
 * <li>vo.port - the port on the host to connect to. Optional. </li>
 * <li>vo.dbpath - a list of database directory names, separated by File.pathSeparator. Required on non-proxy servers.</li> 
 * </ul>
 * 
 * These are also static defined by this interface.
 * <p>
 *
 * Throws: ODMGException in the event of an error. These errors include, but are not limited to:
 *  DatabaseNotFoundException if the database doesn't exist;  DatabaseIsReadOnlyException if the
 *  database is read-only (e.g., on a read-only filesystem), but OPEN_READ_ONLY was not specified
 *  (note that this is really an ODMGRuntimeException). <p>
 *
 * Returns: a MetaObjectServerSession which references the same MetaObjectServer object
 * for a given URI that refers to the same MetaObjectServer. <p>
 *
 * 2. The MetaObjectServer must be thread-safe. It is not recommended that the "synchronized"
 *    keyword be used on methods since this will have the effect of single-threading
 *    the MetaObjectServer. Finer-grained/shorter-term locking should be used instead.<p>
 *
 * @version $Id: MetaObjectServer.java,v 1.3 2005/08/12 02:56:50 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
public interface MetaObjectServer extends ObjectServer 
{
    // Property keys.
    /** The database name. Required. */
    public static final String EnerJ_DBNAME_PROP = "vo.dbname";
    /** The database access mode. This is the integer value of the org.odmg.Database access modes. Required. */
    public static final String EnerJ_ACCESS_MODE_PROP = "vo.accessmode";
    /** The username for the database. Optional. */
    public static final String EnerJ_USERNAME_PROP = "vo.username";
    /** The password for username. Optional. */
    public static final String EnerJ_PASSWORD_PROP = "vo.password"; 
    /** The host to connect to. Required for a remote connection. Must not exist for a in-client local connection. */
    public static final String EnerJ_HOSTNAME_PROP = "vo.hostname";
    /** The port on the host to connect to. Optional. */
    public static final String EnerJ_PORT_PROP = "vo.port"; 
    /** A list of database directory names, separated by File.pathSeparator. */
    public static final String EnerJ_DBPATH_PROP = "vo.dbpath";
    /** The directory where the database configuration was found. */
    public static final String EnerJ_DBDIR_PROP = "vo.dbdir";
}


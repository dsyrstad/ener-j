#!/bin/sh
#
#Ener-J
#Copyright 2001-2005 Visual Systems Corporation
#$Header: /cvsroot/ener-j/ener-j/bin/enerjcreatestddb.sh,v 1.3 2005/08/12 02:56:46 dsyrstad Exp $
#
# Creates a standard database. Does everything including creating a base configuration file.

base=`dirname $0`/..

[ $# -lt 2 ] && {
	echo "Usage: $0 database-dir-name database-name [-options for enerjcreatedb.sh...]" >&2
	exit 1
}

basedbdir=$1
dbname=$2
shift 2

dbdir=$basedbdir/$dbname
[ -d "$dbdir" ] && {
	echo "$dbdir already exists" >&2
	exit 1
}

mkdir -p $dbdir || exit 1

cat > $dbdir/$dbname.properties <<!! 
DefaultMetaObjectServer.ObjectServerClass=org.enerj.server.PagedObjectServer
PagedObjectServer.PageServerClass=org.enerj.server.CachedPageServer
PagedObjectServer.LockServerClass=org.enerj.server.LockScheduler
PagedObjectServer.RedoLogServerClass=org.enerj.server.ArchivingRedoLogServer
PagedObjectServer.MaxUpdateCacheSize=8192000
PagedObjectServer.UpdateCacheInitialHashSize=80000
ArchivingRedoLogServer.logName=\${enerj.dbdir}/$dbname.log
ArchivingRedoLogServer.shouldArchive=true
ArchivingRedoLogServer.requestedLogSize=0
CachedPageServer.delegatePageServerClass=org.enerj.server.FilePageServer
CachedPageServer.numberOfCachedPages=1000
FilePageServer.volume=\${enerj.dbdir}/$dbname.enerj
FilePageServer.pageSize=8192
LockScheduler.initialNumObjs=20000
LockScheduler.deadlockAlgorithm=Waits-For
!!

export EnerJDBPATH=$basedbdir
$base/bin/enerjcreatedb.sh $1 $2 $3 $dbname

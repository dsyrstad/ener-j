#!/bin/sh
#
#Ener-J
#Copyright 2001-2005 Visual Systems Corporation
#$Header: /cvsroot/ener-j/ener-j/bin/enerjcreatedb.sh,v 1.5 2006/04/30 01:14:07 dsyrstad Exp $
#
# Creates a database.

base=`dirname $0`/..

os=`uname -o`
p=":"
[ "$os" = "Cygwin" ] && {
    p=";"
	base=`echo $base | sed -e 's#/cygdrive/c#c:#'`
}

echo "$EnerJDBPATH"
java -javaagent:lib/enerjenh.jar -Denerj.dbpath=$EnerJDBPATH -cp $base/classes$p$base/lib/jga-0.7-retro.jar$p$base$p$base/lib/asm-2.2.2.jar org.enerj.util.CreateDatabase $1 $2 $3 $4 $5 $6 $7


#!/bin/sh
#
#Ener-J
#Copyright 2001-2005 Visual Systems Corporation
#$Header: /cvsroot/ener-j/ener-j/bin/enerjoql.sh,v 1.7 2006/04/30 01:14:07 dsyrstad Exp $
#
# EnerJQL command-line wrapper.

base=`dirname $0`/..

os=`uname -o`
p=":"
[ "$os" = "Cygwin" ] && {
	p=";"
	base=`echo $base | sed -e 's#/cygdrive/c#c:#'`
}

java -javaagent:lib/enerjenh.jar -Xverify:none -XX:+UseParallelGC -XX:PermSize=20M -XX:MaxNewSize=32M -XX:NewSize=32M -Xms256m -Xmx512m -cp $base/classes$p$base/lib/asm-2.2.2.jar -Denerj.dbpath=$EnerJDBPATH org.enerj.util.EnerJOQLCmdLine "$1" "$2" "$3" "$4"


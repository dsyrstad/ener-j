#!/bin/sh

[ -z "$1" ] && {
	echo "Usage: $0 release-num "
	exit 1
}
release=$1
	 
# Repackage BDB Java JNI interface.
cd src || exit 1
[ ! -d com/sleepycatje ] && {
	echo "Run repkg-bdb-je.sh first" >&2
	exit 1
}

rm -rf com/sleepycat
unzip ../third-party/bdb-ni-$release-src.zip || exit 1
chmod -R u+w com/sleepycat
rm -rf com/sleepycat/persist com/sleepycat/collections

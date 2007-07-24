#!/bin/sh

# Repackage BDB Java JNI interface.
cd src || exit 1
[ ! -d com/sleepycatje ] && {
	echo "Run repkg-bdb-je.sh first" >&2
	exit 1
}

unzip ../third-party/bdb-ni-4.6.18-src.zip || exit 1
chmod -R u+w com/sleepycat
rm -rf com/sleepycat/persist com/sleepycat/collections

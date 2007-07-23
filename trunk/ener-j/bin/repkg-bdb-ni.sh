#!/bin/sh

# Repackage BDB Java JNI interface.

cd src || exit 1
unzip ../third-party/bdb-ni-db-4.5.20-src.zip || exit 1
chmod -R u+w com/sleepycat
mv com/sleepycat com/sleepycatni
rm -rf com/sleepycatni/asm com/sleepycatni/persist com/sleepycatni/je/jca com/sleepycatni/collections

find com/sleepycatni -type f -print | while read f 
do 
	sed -e 's/com\.sleepycat\./com.sleepycatni./g' < $f > mod && mv mod $f
done

 
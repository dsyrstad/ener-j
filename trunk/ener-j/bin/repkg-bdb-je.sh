#!/bin/sh

# Repackage BDB JE.

cd src || exit 1
unzip ../third-party/bdb-je-3.2.23-src.zip || exit 1
chmod -R u+w com/sleepycat
mv com/sleepycat com/sleepycatje
rm -rf com/sleepycatje/asm com/sleepycatje/persist com/sleepycatje/je/jca com/sleepycatje/collections

find com/sleepycatje -type f -print | while read f 
do 
	sed -e 's/com\.sleepycat\./com.sleepycatje./g' < $f > mod && mv mod $f
done

 
java -javaagent:lib/enerjenh.jar -server -Xverify:none -XX:+UseParallelGC -XX:PermSize=20M -XX:MaxNewSize=32M -XX:NewSize=32M -Xms256m -Xmx512m -Dvo.dbpath=databases -cp testClasses:classes:lib/asm-2.2.2.jar org.enerj.oo7.OO7Loader oo7 $1 $2
#!/bin/sh

cat <<EOF > /tmp/scolist
java.lang.Byte
java.lang.Boolean
java.lang.Character
java.lang.Short
java.lang.Integer
java.lang.Long
java.lang.Float
java.lang.Double
java.lang.String
java.math.BigDecimal
java.math.BigInteger
java.util.Locale
java.util.Date
java.sql.Date
java.sql.Time
java.sql.Timestamp
java.util.ArrayList
java.util.LinkedList
java.util.TreeSet
java.util.Vector
java.util.Stack
java.util.HashSet
java.util.LinkedHashSet
java.util.HashMap
java.util.Hashtable
java.util.LinkedHashMap
java.util.Properties
java.util.TreeMap
java.util.IdentityHashMap
EOF

awk '{
    className=$0
    name=className
    gsub("\\.", "_", name)
    name = "J"  substr(name, 2) 
    serializerName = name "_Serializer"
    typeID = "s" name "_TypeId"

print "    //----------------------------------------------------------------------"
print "    /** Internal serializer for a " className " SCO."
print "     */"
print "    private static final class " serializerName " implements Serializer"
print "    {"
print "        //----------------------------------------------------------------------"
print "        " serializerName "()"
print "        {"
print "        }"
print ""
print "        //----------------------------------------------------------------------"
print "        byte getTypeId()"
print "        {"
print "            return " typeID ";"
print "        }"
print ""
print "        //----------------------------------------------------------------------"
print "        Class getRepresentingClass()"
print "        {"
print "            return " className ".class;"
print "        }"
print "        "
print "        //----------------------------------------------------------------------"
print "        void write(DataOutput aBuffer, Object anObject)"
print "        {"
print "            " className " aValue = (" className ")anObject;"
print "        }"
print ""
print "        //----------------------------------------------------------------------"
print "        Object read(DataInput aBuffer)"
print "        {"
print "            return null; // @todo"
print "        }"
print "    }"
print ""
}' /tmp/scolist

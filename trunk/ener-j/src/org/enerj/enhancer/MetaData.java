// Ener-J Enhancer
// Copyright 2001, 2002 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/org/enerj/enhancer/MetaData.java,v 1.20 2006/05/27 21:32:28 dsyrstad Exp $

package org.enerj.enhancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enerj.annotations.Persist;
import org.enerj.annotations.PersistenceAware;
import org.enerj.annotations.SchemaAnnotation;
import org.enerj.util.ClassUtil;
import org.enerj.util.asm.AnnotationNode;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Enhancer MetaData representation.
 *
 * @version $Id: MetaData.java,v 1.20 2006/05/27 21:32:28 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad</a>
 */
class MetaData
{
    private static final String sSchemaAnnotationDescr = Type.getDescriptor(SchemaAnnotation.class);
    private static final String sPersistDescr = Type.getDescriptor(Persist.class);
    private static final String sPersistenceAwareDescr = Type.getDescriptor(PersistenceAware.class);
    
    /** Primitive types followed by the corresponding DataInput/Output suffix to use
     * for reading and writing. These are used to load sPrimitiveTypesMap.
     */
    private static final String[] sPrimitiveTypes = {
        "B", "Byte",
        "Z", "Boolean",
        "C", "Char",
        "S", "Short",
        "I", "Int",
        "J", "Long",
        "F", "Float",
        "D", "Double",
    };

    /** Map keyed by primitive type JVM names with a value of the DataInput/Output read/write suffix */
    private static HashMap<String, String> sPrimitiveTypesMap = null;
    
    /** General defaults. The default is not persistable. */
    private ClassDef mDefaultClassDef = new ClassDef("*", ClassDef.TYPE_NOT_CAPABLE, new FieldDef[0]);
    
    /** Map of ClassDefs by class name explicitly specified in the metadata. Key is class name, value is ClassDef. */
    private HashMap<String, ClassDef> mClassDefMap = new HashMap<String, ClassDef>(1024);
    
    /** Map of ClassDefs by package name. These are package defaults. Key is pacakge name, value is ClassDef. */
    private HashMap<String, ClassDef> mPackageDefaultsMap = new HashMap<String, ClassDef>(64);
    
    /** Map of ClassDefs by package name. These are recursive package defaults. Key is pacakge name, value is ClassDef. */
    private HashMap<String, ClassDef> mRecursivePackageDefaultsMap = new HashMap<String, ClassDef>(64);
    
    /** Cache of classes that have been read to get annotations, etc. Key is class name, value is ClassReflector containing the class information. */
    private HashMap<String, ClassReflector> mClassReflectorCache = new HashMap<String, ClassReflector>(1024);
    
    
    /** Cache of direct supertypes of a class. Key is a fully qualified dotted class name, Value is a Set of class names. */
    private Map<String, Set<String>> mDirectSuperTypes = new HashMap<String, Set<String>>(1024);
    
    /** Cache of all supertypes of a class. Key is a fully qualified dotted class name, Value is a Set of class names. */
    private Map<String, Set<String>> mAllSuperTypes = new HashMap<String, Set<String>>(1024);


    //----------------------------------------------------------------------
    /**
     * Construct a new MetaData model using the specified property files and
     * source path.
     *
     * @param aPropFileList the Ener-J property files names - a List of Strings.
     *
     * @throws MetaDataException if there is an error parsing a property file.
     */
    MetaData(List<String> aPropFileList) throws MetaDataException
    {
        // Initialize TypeInfo maps.
        if (sPrimitiveTypesMap == null) {
            sPrimitiveTypesMap = new HashMap<String, String>(sPrimitiveTypes.length / 2, 1.0F);
            for (int i = 0; i < sPrimitiveTypes.length; i += 2) {
                sPrimitiveTypesMap.put(sPrimitiveTypes[i], sPrimitiveTypes[i + 1]);
            }
        }
        
        for (String propFile : aPropFileList) {
            new ODMGMetaDataParser(propFile, this).parse();
        }
    }

    //----------------------------------------------------------------------
    /**
     * Determines if the specified class name is enhanceable (defined in the
     * metadata to be persistable or persistence aware).
     *
     * @param aClassName the class name.
     * @param someClassBytecodes the class' bytecodes, if known. Otherwise null.
     *
     * @return true if it is enhanceable, else false 
     */
    boolean isClassEnhanceable(String aClassName, byte[] someClassBytecodes) throws MetaDataException
    {
        ClassDef classDef = getClassDef(aClassName, someClassBytecodes);
        int type = classDef.getPersistentType();
        
        return (type == ClassDef.TYPE_AWARE || type == ClassDef.TYPE_CAPABLE);
    }

    //----------------------------------------------------------------------
    /**
     * Determines the specified class' enhancment type (defined in the
     * metadata to be persistable, persistence aware, or transient).
     *
     * @param aClassName the class name.
     * @param someClassBytecodes the class' bytecodes, if known. Otherwise null.
     *
     * @return non-zero if it is enhanceable, else zero. If it is enhancable, 1 is
     * returned if class is persistence capable, -1 if persistence aware.
     */
    int getClassEnhancementType(String aClassName, byte[] someClassBytecodes) throws MetaDataException
    {
        ClassDef classDef = getClassDef(aClassName, someClassBytecodes);
        int type = classDef.getPersistentType();
        
        return (type == ClassDef.TYPE_AWARE ? -1 : (type == ClassDef.TYPE_CAPABLE ? 1 : 0));
    }
    
    //----------------------------------------------------------------------
    /**
     * Determines if the specified class name is only PersistentAware (defined in the
     * metadata to be persistence=aware).
     *
     * @param aClassName the class name.
     *
     * @return true if it is just PersistentAware, else false.
     */
    boolean isClassOnlyPersistentAware(String aClassName) throws MetaDataException
    {
        ClassDef classDef = getClassDef(aClassName);
        int type = classDef.getPersistentType();
        
        return (type == ClassDef.TYPE_AWARE);
    }
    
    //----------------------------------------------------------------------
    /**
     * Converts a Java VM signature to a class name.
     *
     * @param aSignature the signature.
     *
     * @return the full class name with dot notation. If aSignature is
     *  not recognized, it is just returned.
     */
    static String convertSignatureToClassName(String aSignature)
    {
        String className = aSignature;

        // Fix "L{class};" forms. Must also change the '/'s to '.'s.
        if (className.charAt(0) == 'L' && className.charAt( className.length() - 1) == ';') {
            className = className.substring(1, className.length() - 2);
        }
        
        className = className.replace('/', '.');
        
        return className;
    }
    
    //----------------------------------------------------------------------
    /**
     * Determines if the specified class name is a FCO (i.e., it 
     * implements Persistable). 
     * 
     * @param aClassName the class name. May be a dot notated class name (e.g., 
     * "java.lang.Object" or a Java VM signature.
     *
     * @return true if it is persistable, else false.
     */
    boolean isClassAFCO(String aClassName) throws MetaDataException
    {
        aClassName = convertSignatureToClassName(aClassName);
        return getClassDef(aClassName).getPersistentType() == ClassDef.TYPE_CAPABLE;
    }
    
    //----------------------------------------------------------------------
    /**
     * Determines if the specified signature (e.g., "Z") denotes a primitive type.
     *
     * @param aSignature the Java VM signature to check.
     * 
     * @return true if it represents a primitive type.
     */
    static boolean isPrimitive(String aSignature)
    {
        return sPrimitiveTypesMap.containsKey(aSignature);
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the corresponding DataInput/Output read/write method suffix for
     * aSignature.
     *
     * @param aSignature the Java VM signature to check. Must be a primitive type.
     * 
     * @return the suffix (e.g., "Byte"), or null if aSignature is not a primitive type.
     */
    static String getPrimitiveDataInOutSuffix(String aSignature)
    {
        return (String)sPrimitiveTypesMap.get(aSignature);
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the overrides, if any, for a field as defined by the metadata.
     *
     * @param aClassName the name of the class that the field is contained in.
     * @param aFieldName the name of the field.
     *
     * @return a FieldDef declaring the overrides, or null if no overrides were
     *  specified.
     */
    FieldDef getFieldOverrides(String aClassName, String aFieldName) throws MetaDataException
    {
        ClassDef classDef = getClassDef(aClassName);
        // TODO Refactor to ClassDef
        FieldDef[] fieldDefs = classDef.getFieldDefs();
        for (int i = 0; fieldDefs != null && i < fieldDefs.length; i++) {
            if (fieldDefs[i].getName().equals(aFieldName)) {
                return fieldDefs[i];
            }
        }
        
        return null;
    }
    
    //----------------------------------------------------------------------
    /**
     * Ensure that the field overrides for a class are actually defined on the class.
     *
     * @param aClassName the name of the class.
     * @param someFields a map from field name to Field of fields defined on the class.
     *
     * @throws  MetaDataException if a field override does not exist on the class or some other error occurs.
     */
    void validateFieldOverrides(String aClassName, Map<String, Field> someFields) throws MetaDataException
    {
        ClassDef classDef = getClassDef(aClassName);
        FieldDef[] fieldDefs = classDef.getFieldDefs();
        for (int overrideIdx = 0; fieldDefs != null && overrideIdx < fieldDefs.length; overrideIdx++) {
            String fieldOverrideName = fieldDefs[overrideIdx].getName();
            
            Field field = someFields.get(fieldOverrideName);
            if (field == null) {
                throw new EnhancerException("Metadata field override " + fieldOverrideName + " is not an actual field on class " + aClassName, null);
            }
            else {
                // If override say transient=false, make sure the field is not declared static nor final because
                // these types are always non-persistent.
                int modifiers = field.getAccessModifiers();
                if (fieldDefs[overrideIdx].getPersistentOverride() == FieldDef.PERSISTENT_YES && isStaticOrFinal(modifiers)) {
                    throw new EnhancerException("Metadata field override " + fieldOverrideName + 
                        " on class " + aClassName + " incorrectly attempts to make a static or final field persistent.", null);
                }
            }
        }
    }
    
    //----------------------------------------------------------------------
    /**
     * Determines if a field is persistent. If the specified class is found 
     * in the source path, the metadata and/or Field definition is used
     * to determine persistability. If the class is not in the source path, the
     * database schema is used to determine persistability. 
     *<p>
     * EnerJ will attempt to persist all fields of a class that are not marked static, final,
     * transient, or are marked "transient=false" in the metadata. At runtime, 
     * if EnerJ tries to persist a field
     * and the field contains an object that is not an SCO nor a FCO, EnerJ throws 
     * org.odmg.ClassNotPersistenceCapableException.
     *
     * @param aClassName the fully qualified dotted class name that the field is contained in.
     * @param aFieldName the name of the field.
     * @param someModifiers The field's access modifiers as defined by ASM Opcodes.ACC_*.
     *
     * @return true if the field is persistent, else false.
     */
    boolean isFieldPersistent(String aClassName, String aFieldName, int someModifiers) throws MetaDataException
    {
        // If the _containing_ class of the field is not persistable, neither is the field.
        if (!isClassAFCO(aClassName)) {
            return false;
        }

        // Static or final fields are never persistent
        if (isStaticOrFinal(someModifiers)) {
            return false;
        }

        FieldDef fieldDef = getFieldOverrides(aClassName, aFieldName);

        if (fieldDef != null) {
            // If "transient=" was specified, use it to determine the override.
            int persistentOverride = fieldDef.getPersistentOverride();
            if (persistentOverride != FieldDef.PERSISTENT_USE_FIELDDEF) {
                return persistentOverride == FieldDef.PERSISTENT_YES;
            }
        }
        
        // Transient fields are not persistent, by default (if not overridden in metadata).
        return (someModifiers & Opcodes.ACC_TRANSIENT) != Opcodes.ACC_TRANSIENT;
    }
    
    //----------------------------------------------------------------------
    /**
     * Determine if the access modifiers are static or final.
     * 
     * @param someModifiers the ASM Opcodes.ACC_* modifiers.
     * 
     * @return true if the modifiers represent static or final.
     */
    // TODO Refactor to ASMUtil
    private static boolean isStaticOrFinal(int someModifiers) 
    {
        return (someModifiers & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC || 
               (someModifiers & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL;        
    }

    //----------------------------------------------------------------------
    /**
     * Gets the ClassDef for a class.
     *
     * @param aClassName the class name.
     *
     * @return a ClassDef. Due to default ClassDefs, the class name may not 
     *  be accurate. If you need it to be accurate, clone the ClassDef and
     *  set the class name.
     *  
     *  @throws MetaDataException if an error occurs.
     */
    private ClassDef getClassDef(String aClassName) throws MetaDataException
    {
        return getClassDef(aClassName, null);
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the ClassDef for a class.
     *
     * @param aClassName the class name.
     * @param someClassBytecodes the class' bytecodes, if known. Otherwise null.
     *
     * @return a ClassDef.
     *  
     * @throws MetaDataException if an error occurs.
     */
    private ClassDef getClassDef(String aClassName, byte[] someClassBytecodes) throws MetaDataException
    {
        // If we have an explict class def, return that. This can only come from global meta data
        // or from a previously cached getClassDef() resolution.
        ClassDef classDef = mClassDefMap.get(aClassName);
        if (classDef != null) {
            return classDef;
        }
        
        classDef = getUncachedClassDef(aClassName, someClassBytecodes);
        ClassDef cachableClassDef = classDef;
        String classDefName = classDef.getName();
        if (classDefName == null || !classDefName.equals(aClassName)) {
            cachableClassDef = (ClassDef)classDef.clone();
            cachableClassDef.setName(aClassName);
        }
        
        // Cache it for next time.
        mClassDefMap.put(aClassName, cachableClassDef);
        return cachableClassDef;
    }
    
    //----------------------------------------------------------------------
    /**
     * Gets the uncached ClassDef for a class.
     *
     * @param aClassName the class name.
     * @param someClassBytecodes the class' bytecodes, if known. Otherwise null.
     *
     * @return a ClassDef.
     *  
     * @throws MetaDataException if an error occurs.
     */
    private ClassDef getUncachedClassDef(String aClassName, byte[] someClassBytecodes) throws MetaDataException
    {
        // Look at class meta data or schema annotations. Schema annotations are taken over
        // meta data annotations because schema annotations are added as a result of static enhancement.
        ClassDef classDef = getClassDefFromClassAnnotation(aClassName, someClassBytecodes);
        if (classDef != null) {
            return classDef;
        }
        
        // Check package annotations all of the way up the hierarchy.
        // If we find one, cache a ClassDef for the package-info class and one for the class.
        String packageName = getPackageName(aClassName);
        for (; packageName.length() > 0; packageName = getPackageName(packageName)) {
            try {
                ClassReflector pkgInfo = getClassReflector(packageName + ".package-info", null);
                for (AnnotationNode anno : pkgInfo.getClassAnnotations()) {
                    if (anno.desc.equals(sPersistDescr)) {
                        Boolean persist = (Boolean)anno.getValue("value");
                        classDef = new ClassDef(aClassName, (persist == null || persist) ? ClassDef.TYPE_CAPABLE : ClassDef.TYPE_NOT_CAPABLE, null);
                        return classDef;
                    }
                }
            }
            catch (MetaDataException e) {
                // Ignore. Just try the next one.
            }
        }
        
        
        // Check global meta data.
        
        // Progressively work back thru package defs, and finally 
        // return the default def if no others are found.
        packageName = getPackageName(aClassName);
        classDef = mPackageDefaultsMap.get(packageName);
        if (classDef != null) {
            return classDef;
        }
        
        // Test recursive package defaults. If our packageName, or any part of it, matches one in 
        // the map, use the corresponding ClassDef. We search from most qualified package name to least
        // so that "com.xyz.abc.*" overrides "com.xyz.*".
        for (; packageName.length() > 0; packageName = getPackageName(packageName)) {
            classDef = mRecursivePackageDefaultsMap.get(packageName);
            if (classDef != null) {
                return classDef;
            }
        }

        // Punt. Return the default definition.
        return mDefaultClassDef;
    }

    //--------------------------------------------------------------------------------
    /**
     * Get the containing package name of the given class or package name.
     *
     * @param aName a dotted class or package name.
     * 
     * @return the containing package name, or an empty string for the default package. 
     */
    private String getPackageName(String aName)
    {
        String packageName = ""; // Default package.
        int lastDot = aName.lastIndexOf('.');
        if (lastDot >= 0) {
            packageName = aName.substring(0, lastDot);
        }
        
        return packageName;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Attempts to get a ClassDef for the named class via annotations on the class.
     *
     * @param aClassName the class name.
     * @param someClassBytecodes the class' bytecodes, if known. Otherwise null.
     * 
     * @return the ClassDef, or null if no applicable annotations exist.
     * 
     * @throws MetaDataException if an error occurs.
     */
    private ClassDef getClassDefFromClassAnnotation(String aClassName, byte[] someClassBytecodes) throws MetaDataException
    {
        ClassReflector classInfo = getClassReflector(aClassName, someClassBytecodes);
        List<AnnotationNode> classAnnotations = classInfo.getClassAnnotations();
        
        for (AnnotationNode anno : classAnnotations) {
            // Do we have a SchemaAnnotation from a static enhancement?
            if (anno.desc.equals(sSchemaAnnotationDescr)) {
                // Yep, class was statically enhanced. Build a class def from the annotation.
                String[] persistentFields = (String[])anno.getArray("persistentFieldNames", String.class);
                String[] transientFields = (String[])anno.getArray("transientFieldNames", String.class);
                FieldDef[] fieldDefs = new FieldDef[ persistentFields.length + transientFields.length ];
                int i = 0;
                for (String fieldName : persistentFields) {
                    fieldDefs[i++] = new FieldDef(fieldName, FieldDef.PERSISTENT_YES, null, null);
                }

                for (String fieldName : transientFields) {
                    fieldDefs[i++] = new FieldDef(fieldName, FieldDef.PERSISTENT_NO, null, null);
                }

                return new ClassDef(aClassName, ClassDef.TYPE_CAPABLE, fieldDefs);
            }
            else if (anno.desc.equals(sPersistDescr)) {
                // Persist anno. 
                Boolean persistent = (Boolean)anno.getValue("value");
                FieldDef[] fieldDefs = null;

                if (persistent == null || persistent) {
                    // Need to get field annos, if any. These are overrides.
                    List<FieldNode> fields = classInfo.getFields();
                    List<FieldDef> fieldDefList = new ArrayList<FieldDef>();
                    for (FieldNode field : fields) {
                        for (AnnotationNode fieldAnno : field.getAnnotations()) {
                            if (fieldAnno.desc.equals(sPersistDescr)) {
                                Boolean fieldPersistent = (Boolean)anno.getValue("value");
                                fieldDefList.add( new FieldDef(field.getName(),
                                                (fieldPersistent == null || fieldPersistent) ? FieldDef.PERSISTENT_YES : FieldDef.PERSISTENT_NO,
                                                "", "") );
                            }
                        }
                    }
                    
                    fieldDefs = new FieldDef[ fieldDefList.size() ];
                    fieldDefList.toArray(fieldDefs);
                }
                
                return new ClassDef(aClassName, (persistent == null || persistent) ? ClassDef.TYPE_CAPABLE : ClassDef.TYPE_NOT_CAPABLE, 
                                fieldDefs);
            }
            else if (anno.desc.equals(sPersistenceAwareDescr)) {
                // PersistenceAware anno.
                Boolean persistenceAware = (Boolean)anno.getValue("value");
                return new ClassDef(aClassName, (persistenceAware == null || persistenceAware) ? ClassDef.TYPE_AWARE : ClassDef.TYPE_NOT_CAPABLE, null); 
            }
        }
        
        return null;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Gets information about a class by reading its bytecodes. After a class
     * has been read once, the information is cached.  
     *
     * @param aClassName the class.
     * @param someClassBytecodes the class' bytecodes, if known. Otherwise null.
     * 
     * @return a ClassReflector which contains information about the class.
     * 
     * @throws MetaDataException if an error occurs.
     */
    private ClassReflector getClassReflector(String aClassName, byte[] someClassBytecodes) throws MetaDataException
    {
        // Is class already cached?
        ClassReflector classInfo = mClassReflectorCache.get(aClassName);
        if (classInfo != null) {
            return classInfo;
        }
        
        // Load class information using ASM. If so, cache it in mClassReflectorMap.
        try {
            if (someClassBytecodes == null) {
                someClassBytecodes = ClassUtil.getBytecode(aClassName);
            }

            ClassReader classReader = new ClassReader(someClassBytecodes);
            classInfo = new ClassReflector();
            classReader.accept(classInfo, false);
            mClassReflectorCache.put(aClassName, classInfo);
            return classInfo;
        }
        catch (ClassNotFoundException e) {
            throw new MetaDataException(e);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Recursively resolves all superclasses and superinterfaces of the specified class.
     * Intentionally attempts to avoid using the ClassLoader to reflect on these values. Classes
     * on the source path will be examined with ASM, others will be reflected with the ClassLoader. 
     * 
     * @param aClassName the fully qualified dotted class name to be resolved.
     * 
     * @return a Set<String> contains all super-types. Every class will contain at least one: java.lang.Object.
     *
     * @throws MetaDataException if an error occurs.
     */
    Set<String> resolveSuperTypes(String aClassName) throws MetaDataException
    {
        // Check to see if we already resolved this class.
        Set<String> cachedAllSuperTypes = mAllSuperTypes.get(aClassName);
        if (cachedAllSuperTypes != null) {
            return cachedAllSuperTypes;
        }
        
        Set<String> directSuperTypes = new HashSet<String>(16);
        cachedAllSuperTypes = new HashSet<String>(16);

        String directSuperClass;
        String[] directInterfaces;

        // TODO pass bytecodes into this.
        ClassReflector classReflector = getClassReflector(aClassName, null);
        directSuperClass = classReflector.getSuperClass();
        directInterfaces = classReflector.getSuperInterfaces();

        // Add direct supertype information. directSuperClass will only be null for java.lang.Object.
        if (directSuperClass != null) {
            directSuperTypes.add(directSuperClass);
            cachedAllSuperTypes.add(directSuperClass);
            cachedAllSuperTypes.addAll( resolveSuperTypes(directSuperClass) );
        }
        
        for (String intf : directInterfaces) {
            directSuperTypes.add(intf);
            cachedAllSuperTypes.add(intf);
            cachedAllSuperTypes.addAll( resolveSuperTypes(intf) );
        }
        
        // Cache the results.
        mDirectSuperTypes.put(aClassName, directSuperTypes);
        mAllSuperTypes.put(aClassName, cachedAllSuperTypes);
        
        return cachedAllSuperTypes;
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Sets the default ClassDef.
     *
     * @param aClassDef the ClassDef to become the default.
     */
    void setDefaultClassDef(ClassDef aClassDef)
    {
        mDefaultClassDef = aClassDef;
    }
    
    
    //--------------------------------------------------------------------------------
    /**
     * Adds a default ClassDef for a package. Only classes below this package are 
     * treated by aClassDef.
     *
     * @param aPackageName the package name.
     * @param aClassDef the ClassDef to become the default for the package.
     */
    void addPackageDefaultClassDef(String aPackageName, ClassDef aClassDef)
    {
        mPackageDefaultsMap.put(aPackageName, aClassDef);
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Adds a default ClassDef for a recursive package. All classes in this package and
     * all sub-packages below it are treated by aClassDef. 
     *
     * @param aPackageName the package name.
     * @param aClassDef the ClassDef to become the default for the package.
     */
    void addRecursivePackageDefaultClassDef(String aPackageName, ClassDef aClassDef)
    {
        mRecursivePackageDefaultsMap.put(aPackageName, aClassDef);
    }
   
    //--------------------------------------------------------------------------------
    /**
     * Adds a ClassDef.
     *
     * @param aClassName the class name.
     * @param aClassDef the ClassDef.
     */
    void addClassDef(String aClassName, ClassDef aClassDef)
    {
        mClassDefMap.put(aClassName, aClassDef);
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * A simple ASM ClassVisitor that gathers information about a class. 
     */
    private static final class ClassReflector implements ClassVisitor
    {
        private String mSuperClass = null;
        private String[] mSuperInterfaces = null;
        private List<AnnotationNode> mClassAnnotations = new ArrayList<AnnotationNode>();
        private List<FieldNode> mFields = new ArrayList<FieldNode>();
        

        public void visit(int someVersion, int access, String someName, String someSignature, String someSuperName, String[] someInterfaces)
        {
            if (someSuperName != null) {
                mSuperClass = someSuperName.replace('/', '.');
            }
            
            if (someInterfaces != null) {
                mSuperInterfaces = new String[ someInterfaces.length ];
                for (int i = 0; i < someInterfaces.length; i++) {
                    mSuperInterfaces[i] = someInterfaces[i].replace('/', '.');
                }
            }
        }

        public AnnotationVisitor visitAnnotation(String aDesc, boolean isVisible)
        {
            AnnotationNode v = new AnnotationNode(aDesc);
            mClassAnnotations.add(v);
            return v;
        }

        public void visitAttribute(Attribute attr)
        {
        }

        public void visitEnd()
        {
        }

        public FieldVisitor visitField(int access, String aName, String aDesc, String aSignature, Object aValue)
        {
            FieldNode fieldNode = new FieldNode(aName);
            mFields.add(fieldNode);
            return fieldNode;
        }

        public void visitInnerClass(String someName, String someOuterName, String someInnerName, int access)
        {
        }

        public MethodVisitor visitMethod(int access, String someName, String someDesc, String someSignature, String[] someExceptions)
        {
            return null;
        }

        public void visitOuterClass(String someOwner, String someName, String someDesc)
        {
        }

        public void visitSource(String someSource, String someDebug)
        {
        }

        String getSuperClass()
        {
            return mSuperClass;
        }

        String[] getSuperInterfaces()
        {
            return mSuperInterfaces;
        }

        List<AnnotationNode> getClassAnnotations()
        {
            return mClassAnnotations;
        }

        List<FieldNode> getFields()
        {
            return mFields;
        }
    }
    
    
    private static final class FieldNode implements FieldVisitor
    {
        private String mName;
        private List<AnnotationNode> mAnnotations = new ArrayList<AnnotationNode>();

        FieldNode(String aName) 
        { 
            mName = aName;
        }

        public AnnotationVisitor visitAnnotation(String aDesc, boolean anVisible)
        {
            AnnotationNode anno = new AnnotationNode(aDesc);
            mAnnotations.add(anno);
            return anno;
        }

        public void visitAttribute(Attribute attr)
        {
        }

        public void visitEnd()
        {
        }

        List<AnnotationNode> getAnnotations()
        {
            return mAnnotations;
        }

        String getName()
        {
            return mName;
        }
    }
}

//Ener-J
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/annotations/Persist.java,v 1.2 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to indicate whether a field, class, or all classes in a package 
 * are persistable. Package-level annotations must be placed in package-info.java
 * in the corresponding package. This rarely needs to be used on the
 * field level because all fields that are not static, final, or transient are
 * persistent by default.<p>
 * 
 * @version $Id: Persist.java,v 1.2 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Target({TYPE, PACKAGE, FIELD}) @Retention(RUNTIME)
public @interface Persist {
    /** True if the class or classes in a package are persistable, else false. */
    boolean value() default true;
}

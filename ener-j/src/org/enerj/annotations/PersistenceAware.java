//Ener-J
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/annotations/PersistenceAware.java,v 1.2 2006/05/05 13:47:14 dsyrstad Exp $

package org.enerj.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to indicate whether a class or all classes in a package 
 * are persistence aware (i.e., they use fields of a persistent class
 * directly, but are not persistable themselves). <p>
 * 
 * @version $Id: PersistenceAware.java,v 1.2 2006/05/05 13:47:14 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Target({TYPE, PACKAGE}) @Retention(RUNTIME)
public @interface PersistenceAware {
    /** True if the class or classes in a package are persistence aware, else false. */
    boolean value() default true;
}

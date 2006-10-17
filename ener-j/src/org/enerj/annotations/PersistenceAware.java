/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
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

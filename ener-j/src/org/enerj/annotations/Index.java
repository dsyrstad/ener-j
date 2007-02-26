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
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/annotations/SchemaAnnotation.java,v 1.1 2006/05/02 22:05:24 dsyrstad Exp $

package org.enerj.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Comparator;

/**
 * Annotation for a persistent index. May be specified at the type level for multiple fields, or
 * on the field/getter property level for specific properties. If specified on a field or property (accessor),
 * the property names are not required. The default is to allow nulls and duplicate keys.
 * <p>
 * 
 * @version $Id:  $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
@Target({TYPE, FIELD, METHOD}) @Retention(RUNTIME)
@Index
public @interface Index {
    enum Type { BTree, Hash };
    Type type() default Type.BTree;
    /** Index name. If not specified, it is generated from the properties. */
    String name() default ""; // Empty string means generate name.
    /** The property names that are indexed. Empty for FIELD and METHOD level annotations. */
    String[] properties() default { };
    /** True if ascending order (default), false if descending. Only applies to ordered indexes. */
    boolean ascending() default true;
    /** True if null keys are allowed. */ 
    boolean allowNullKeys() default true;
    /** True if duplicate keys are allowed. */
    boolean allowDuplicateKeys() default true;
    /** The Key Comparator class name, if any. */
    String comparator() default ""; // This default indicates no comparator.
}

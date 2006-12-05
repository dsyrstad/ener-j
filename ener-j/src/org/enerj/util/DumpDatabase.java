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
//$Header: $

package org.enerj.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.enerj.core.EnerJDatabase;
import org.enerj.core.Extent;
import org.enerj.core.Persistable;
import org.enerj.core.PersistableHelper;
import org.odmg.Database;

/**
 * Dumps a database created by PagedObjectServer. <p>
 * 
 * @version $Id: $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class DumpDatabase
{

    /**
     * 
     *
     * @param args the database URI.
     */
    public static void main(String[] args) throws Exception
    {
        EnerJDatabase db = new EnerJDatabase();
        db.open(args[0], Database.OPEN_READ_ONLY);
        try {
            db.setAllowNontransactionalReads(true);
            
            Extent extent = db.getExtent(Object.class, true);
    
            for (Object obj : extent) {
                System.out.println(dumpObject((Persistable)obj));
                //db.evictAll();
            }
        }
        finally {
            db.close();
        }

        /*Properties props = new Properties(System.getProperties());
        props.setProperty("PagedObjectServer.PageServerClass", FilePageServer.class.getName());
        props.setProperty("FilePageServer.volume", args[0]);
        PagedStore store = new PagedStore(props, null, null, true);
        
        OIDList oidList = store.getOIDList();
        long maxOID = oidList.getListSize();
        
        for (long oid = 1; oid < maxOID; ++oid) {
            long offset = oidList.getObjectOffsetForOID(oid);
            if (offset == 0) {
                continue;
            }
            
            long cid = oidList.getCIDforOID(oid);
            System.out.println("OID " + oid + " cid " + Long.toHexString(cid) + " offset " + offset);
        }
        
        store.disconnect();
        */
    }

    private static String dumpObject(Persistable obj) throws Exception
    {
        PersistableHelper.checkLoaded(obj, false);
        
        if (!obj.enerj_IsLoaded()) {
            throw new Exception("Object " + obj.getClass() + " is still not loaded. New=" + obj.enerj_IsNew());
        }
        
        long cid = obj.enerj_GetClassId();
        Class objClass = obj.getClass(); 
        String objClassName = objClass.getName();
        
        StringBuilder buf = new StringBuilder();
        buf.append("OID=");
        buf.append(obj.enerj_GetPrivateOID());
        buf.append(" CID=");
        buf.append(Long.toHexString(cid));
        buf.append(" Class=" + objClassName);
        buf.append(" [");
        
        // Note: Could use a modified version of StringUtil.toString().
        String sep = "";
        for (Field field : ClassUtil.getAllDeclaredFields(obj.getClass())) {
            String fieldName = field.getName();
            if (fieldName.startsWith("enerj_")) {
                continue;
            }
            
            buf.append(sep);

            Class declClass = field.getDeclaringClass();
            if (!declClass.getName().equals(objClassName)) {
                buf.append(declClass.getSimpleName());
                buf.append('.');
            }
            
            buf.append(fieldName);
            buf.append('=');
            Object value = field.get(obj);
            if (value instanceof Persistable) {
                buf.append("{oid=");
                buf.append(((Persistable)value).enerj_GetPrivateOID());
                buf.append('}');
            }
            else if (value == null) {
                buf.append("null");
            }
            else {
                buf.append(value.getClass().getName());
                buf.append('@');
                buf.append(System.identityHashCode(value));
            }
            
            sep = " ";
        }
        
        buf.append(']');
        
        return buf.toString();
    }
}

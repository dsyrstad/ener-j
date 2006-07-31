// ============================================================================
// $Id: FunctorTest.java,v 1.3 2005/08/12 02:56:52 dsyrstad Exp $
// Copyright (c) 2002  David A. Hall
// ============================================================================
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
// ============================================================================

package org.enerj.jga.fn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import junit.framework.TestCase;

/**
 * Common values and methods for tests of functors
 * <p>
 * Copyright &copy; 2002  David A. Hall
 *
 * @author <a href="mailto:davidahall@users.sourceforge.net">David A. Hall</a>
 */

public class FunctorTest<T extends Serializable> extends TestCase {
    public FunctorTest (String name){ super(name); }

    public void testBogus() {}

    public T makeSerial(T obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

            byte[] ba = baos.toByteArray();
//             System.out.println(byteArrayToString(ba));
        
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            ObjectInputStream ois = new ObjectInputStream(bais);

//             T fn = (T) ois.readObject();
//             System.out.println(fn);
//             return fn;
            
            return (T) ois.readObject();
        }
        catch (IOException x) {
            throw new RuntimeException(x);
        }
        catch (ClassNotFoundException x) {
            throw new RuntimeException(x);
       }
    }

    static public String byteArrayToString(byte[] arr) {
        StringBuffer buf = new StringBuffer(arr.length * 2);
        for (int i  = 0; i < arr.length; ++i) {
            if (arr[i] < 0x21 || arr[i] > 0x7e) {
                buf.append("\\");
                appendByte(buf,arr[i]);
            }
            else
                buf.append(new Character((char) arr[i]));
        }

        return buf.toString();
    }

    static private StringBuffer appendByte(StringBuffer buf, byte b) {
        buf.append(nibbleToString((b & 0xF0) >> 4));
        buf.append(nibbleToString((b & 0x0F)));
        return buf;
    }

//     static public StringBuffer appendShort(StringBuffer buf, short s) {
//         appendByte(buf, (s & 0xFF00) >> 8);
//         appendByte(buf, (s & 0x00FF);
//         return buf;
//     }

//     static public StringBuffer appendInt(StringBuffer buf, int i) {
//         appendByte(buf, (s & 0xFF000000) >> 24);
//         appendByte(buf, (s & 0x00FF0000) >> 16);
//         appendByte(buf, (s & 0x0000FF00) >> 8);
//         appendByte(buf, (s & 0x000000FF));
//         return buf;
//     }
    
    static private char nibbleToString(int b) {
        assert b >= 0 && b <= 15;
        return "0123456789ABCDEF".charAt(b);
    }
}

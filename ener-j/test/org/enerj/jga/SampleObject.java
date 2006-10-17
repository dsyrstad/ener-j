// ============================================================================
// $Id: SampleObject.java,v 1.3 2005/08/12 02:56:48 dsyrstad Exp $
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

package org.enerj.jga;

import java.math.BigDecimal;
import java.util.Date;

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

public class SampleObject {
    public SampleObject (String name, Integer count) {
        this(name, count.intValue());
    }
    
    public SampleObject (String name, int count){
        this(name, count, new BigDecimal(0.0D), new Date());
    }
    
    public SampleObject (String name, int count, BigDecimal price, Date date){
        _name  = name;
        _count = new Integer(count);
        _price = price;
        _date  = date;
    }

    public String _name;
    public Integer _count;
    public BigDecimal _price;
    public Date _date;

    private Object _detail;
    
    public String getName() { return _name; }
    public void setName(String v) { _name = v; }
    
    public Integer getCount() { return _count; }
    public void setCount(Integer v) { _count = v; }

    public BigDecimal getPrice() { return _price; }
    public SampleObject setPrice(BigDecimal v) { _price = v; return this; }
    
    public Date getDate() { return _date; }
    public Date setDate(Date v) { Date r = _date; _date = v; return r; }

    private Object getDetail() { return _detail; }
    private void setDetail(Object obj) { _detail = obj; }

    public String toString() {
        return super.toString() +"[name="+_name+",count="+_count
            +",price="+_price+",date="+_date+"]";
    }
}



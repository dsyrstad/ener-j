package org.enerj.fa.core;

public class Atom
{

    // Name is not required - it may be null.
    private String name;
    // String, long, decimal, bigint, boolean, double, byte[], Tuple (ref), expression, null (==Empty tuple).
    // Date types.
    private Object value;

    public Atom(String name)
    {
        this(name, null);
    }

    public Atom(String name, Object value)
    {
        this.name = name;
        setValue(value);
    }

    public String getName()
    {
        return name;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public Tuple getValueAsTuple()
    {
        return (Tuple)value;
    }

    public Long getValueAsLong()
    {
        // TODO Conversions
        return (Long)value;
    }
}

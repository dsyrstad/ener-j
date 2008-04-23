public class Atom
{
    public static final Atom NULL = new Atom();

    // String, long, decimal, bigint, boolean, double, byte[], Tuple, expression, null (==Empty tuple).
    private Object value;

    public Atom()
    {
        value = null;
    }

    public Atom(Object value)
    {
        set(value);
    }

    public Object get()
    {
        return value;
    }

    public void set(Object value)
    {
        this.value = value;
    }
}

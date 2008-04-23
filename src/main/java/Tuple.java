import java.util.ArrayList;
import java.util.List;

public class Tuple
{
    private List<Atom> atoms;

    public Tuple(Object... atomValues)
    {
        assert atomValues != null;

        atoms = new ArrayList<Atom>(atomValues.length);
        for (Object atomValue : atomValues) {
            atoms.add(new Atom(atomValue));
        }
    }

    public Atom get(int index)
    {
        return atoms.get(index);
    }

    public Atom get(String atomName)
    {
        return null;
    }

    public void set(int index, Atom atom)
    {

    }

    public void set(String atomName, Atom atom)
    {

    }

    public long getId()
    {
        return 0;
    }

    public Tuple getDomain()
    {
        return null;
    }
}

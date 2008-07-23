 package org.enerj.fa.core;

import java.util.ArrayList;
import java.util.List;

// TODO atom names should be written to domain with name/id.
// tuples would be written id/atom value, id/atom value, ...
// Domains should have a name. So maybe have a constructor that takes a domain name....
public class Tuple
{
    public static final String ATOM_NAME_ID = "id";
    public static final String ATOM_NAME_DOMAIN = "domain";

    // TODO This should probably be a linked hash map mapping atom name to atom
    private List<Atom> atoms;

    /**
     * Create a tuple with the given domain and atom values.
     */
    public Tuple(Tuple domain, Atom... atoms)
    {
        assert atoms != null;

        this.atoms = new ArrayList<Atom>(atoms.length + 2);
        this.atoms.add(new Atom(ATOM_NAME_ID));
        this.atoms.add(new Atom(ATOM_NAME_DOMAIN, domain));
        for (Atom atom : atoms) {
            this.atoms.add(atom);
        }
    }

    public Atom getAtom(int index)
    {
        return atoms.get(index);
    }

    public int getNumAtoms()
    {
        return atoms.size();
    }

    public Atom getAtom(String atomName)
    {
        int index = indexOfAtom(atomName);
        if (index >= 0) {
            return getAtom(index);
        }

        return new Atom(atomName);
    }

    /**
     * Gets the index of an atom by name.
     *
     * @param atomName
     * @return the index of the atom with name atomName, or -1 if it was not found.
     */
    public int indexOfAtom(String atomName)
    {
        for (int i = 0; i < atoms.size(); i++) {
            Atom atom = atoms.get(i);
            if (atom != null && atom.getName() != null && atom.getName().equals(atomName)) {
                return i;
            }
        }

        return -1;
    }

    public void setAtom(int index, Atom atom)
    {
        // TODO if atom.name is in list at a different index position, delete it.
        // TODO removeAtom(int|name)
        atoms.set(index, atom);
    }

    /**
     * Sets the atom. If atom has a name, atom replaces any existing atom with the same name.
     *
     * @param atom
     */
    public void setAtom(Atom atom)
    {
        int index = indexOfAtom(atom.getName());
        if (index >= 0) {
            atoms.set(index, atom);
        }
        else {
            atoms.add(atom);
        }
    }

    public Long getId()
    {
        return getAtom(ATOM_NAME_ID).getValueAsLong();
    }

    public Tuple getDomain()
    {
        return getAtom(ATOM_NAME_DOMAIN).getValueAsTuple();
    }
}

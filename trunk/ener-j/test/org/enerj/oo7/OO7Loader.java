package org.enerj.oo7;

import java.util.ArrayList;
import java.util.Random;

import org.odmg.DBag;
import org.odmg.DMap;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.ODMGException;
import org.odmg.Transaction;
import org.enerj.core.RegularDBag;
import org.enerj.core.RegularDMap;
import org.enerj.core.EnerJDatabase;
import org.enerj.core.EnerJImplementation;
import org.enerj.core.EnerJTransaction;

public class OO7Loader
{
    private static final int TINY = 0;
    private static final int SMALL = 1;
    private static final int MEDIUM = 2;
    private static final int LARGE = 3;

    private static String sDBURL;

    private Implementation mImplementation;
    private Random mRandom;
    private EnerJDatabase mDB;
    private DMap mAtomicPartByIDMap;
    private int mType;
    private int mSize;
    
    private char[] randomStringWork = new char[1000000 / 2]; 

    private String getName()
    {
        switch (mType) {
        case TINY:
            return "tiny-" + mSize;
        case SMALL:
            return "small-" + mSize;
        case MEDIUM:
            return "medium-" + mSize;
        case LARGE:
            return "large-" + mSize;
        default:
            return "unknown-" + mSize;
        }
    }

    // Create a random string
    private String randomString(int length)
    {
        for (int idx = 0; idx < length; idx++) {
            randomStringWork[idx] = (char)('A' + mRandom.nextInt(26));
        }
        
        return new String(randomStringWork, 0, length);
    }

    private AtomicPart[] buildAtomicParts()
    {
        AtomicPart[] result = new AtomicPart[(mType < MEDIUM) ? 20 : 200];
        // Build Parts
        for (int idx = 0; idx < result.length; idx++) {
            result[idx] = new AtomicPart(idx + 1000, mRandom.nextInt(10000), mRandom.nextInt(), mRandom.nextInt(),
                            mRandom.nextInt());

            mAtomicPartByIDMap.put(result[idx].getId(), result[idx]);
        }
        
        // Construct ring
        for (int idx = 0; idx < result.length - 1; idx++) {
            new Connection(mRandom.nextInt(), randomString(3), result[idx], result[idx + 1]);
        }
        
        new Connection(mRandom.nextInt(), randomString(3), result[result.length - 1], result[0]);
        // Add random connections
        for (int idx = 0; idx < result.length; idx++) {
            for (int count = 0; count < mSize; count++) {
                new Connection(mRandom.nextInt(), randomString(3), result[idx],
                                result[mRandom.nextInt(result.length)]);
            }
        }
        
        EnerJTransaction.getCurrentTransaction().flush();
        return result;
    }

    private CompositePart[] buildCompositeParts() throws ODMGException
    {
        CompositePart[] result = new CompositePart[(mType == TINY) ? 50 : 500];
        int documentSize = 10000; // 1 char = 2 bytes
        int atomicPerComp = (mType < MEDIUM) ? 20 : 200;
        if (mType == TINY)
            documentSize = 10;
        else if (mType == SMALL)
            documentSize = 1000;
        // Build composites;
        for (int idx = 0; idx < result.length; idx++) {
            AtomicPart[] atomicParts = buildAtomicParts();
            CompositePart current = new CompositePart(mRandom.nextInt(), mRandom.nextInt());
            result[idx] = current;
            // Build document
            new Document(mRandom.nextInt(), randomString(5), randomString(documentSize), current);
            
            // Add some parts
            ArrayList bag = new ArrayList(atomicPerComp);
            current.setParts(bag);
            current.setRootPart(atomicParts[0]);
            bag.add(current.getRootPart());
            for (int subIdx = 1; subIdx < atomicPerComp; subIdx++) {
                bag.add(atomicParts[subIdx]);
            }
        }

        EnerJTransaction.getCurrentTransaction().flush();
        return result;
    }

    private Assembly buildAssemblies(int level, Module mod, CompositePart[] compositeParts)
    {
        if (level == 1) {
            BaseAssembly result = new BaseAssembly(mRandom.nextInt(), mRandom.nextInt());
            ArrayList bag = new ArrayList(3);
            result.setComponentsPrivate(bag);
            for (int idx = 0; idx < 3; idx++) {
                bag.add(compositeParts[mRandom.nextInt(compositeParts.length)]);
            }
            
            return result;
        }
        else {
            ComplexAssembly result = new ComplexAssembly(mRandom.nextInt(), mRandom.nextInt(), mod);
            ArrayList bag = new ArrayList(3);
            result.setSubAssemblies(bag);
            for (int idx = 0; idx < 3; idx++) {
                Assembly asm = buildAssemblies(level - 1, mod, compositeParts);
                asm.setSuperAssembly(result);
                bag.add(asm);
            }
            
            return result;
        }
    }

    private Module buildModule() throws ODMGException
    {
        Module result = new Module();

        CompositePart[] compositeParts = buildCompositeParts();
        result.setDesignRoot((ComplexAssembly)buildAssemblies(7, result, compositeParts));

        int manualSize; // In characters (2 byte per char)
        if (mType == TINY)
            manualSize = 512;
        else if (mType == SMALL)
            manualSize = 100000 / 2;    // 100KB (2 bytes/char)
        else
            manualSize = 1000000 / 2;   // 1MB (2 bytes/char)

        result.setManual(new Manual(mRandom.nextInt(), manualSize, randomString(10), randomString(manualSize), result));
        EnerJTransaction.getCurrentTransaction().flush();
        return result;
    }

    private void buildDB(int aType, int aSize)
    {
        try {
            mType = aType;
            mSize = aSize;
            String name = getName();

            System.out.println("Creating " + name + "...");
            long start = System.currentTimeMillis();

            // Create a new DB
            mDB = new EnerJDatabase();
            mDB.open(sDBURL, Database.OPEN_READ_WRITE);

            // Create a transaction
            Transaction txn = mImplementation.newTransaction();
            txn.begin();

            // Create data
            int size = ((mType != LARGE) ? 1 : 10);
            DBag modules = new RegularDBag(size);
            mAtomicPartByIDMap = new RegularDMap(1024*1024);
            mDB.bind(mAtomicPartByIDMap, "AtomicPartsByID");

            for (int idx = 0; idx < size; idx++) {
                modules.add( buildModule() );
            }
            
            mDB.bind(modules, "Modules");

            // Commit
            txn.commit();

            // Close DB
            mDB.close();
            mDB = null;

            // Calculate time
            long end = System.currentTimeMillis();
            System.out.println("Duration: " + (end - start) + "ms");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run(int type, int size) throws Exception
    {
        mImplementation = EnerJImplementation.getInstance();
        mRandom = new Random();

        buildDB(type, size);
    }

    public static final void main(String[] args) throws Exception
    {
        if (args.length == 3) {
            sDBURL = args[0];
            
            int type = -1, size = -1;
            if (args[1].equals("t"))
                type = TINY;
            else if (args[1].equals("s"))
                type = SMALL;
            else if (args[1].equals("m"))
                type = MEDIUM;
            else if (args[1].equals("l"))
                type = LARGE;
            if (args[2].equals("3"))
                size = 3;
            else if (args[2].equals("6"))
                size = 6;
            else if (args[2].equals("9"))
                size = 9;
            if ((type >= 0) && (size >= 0)) {
                (new OO7Loader()).run(type, size);
                return;
            }
        }

        System.out.println("Usage: OO7Loader dburl type:t|s|m|l size:3|6|9");
    }
}
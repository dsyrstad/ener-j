package misc;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class TestFileLockBug
{
    public static void main(String[] args) throws Exception
    {
        TestFileLockBug o = new TestFileLockBug();
        try {
            o.run();
        }
        finally {
            new File("test.file").delete();
        }
    }

    void run() throws Exception
    {
        final RandomAccessFile ras = new RandomAccessFile("test.file", "rw");
        getLock(ras); // Get the initial lock

        Thread t1 = new Thread("Thread 1") {
            public void run()
            {
                for (int i = 0; i < 1000000; i++) {
                    try {
                        ras.seek(0);
                        ras.read(new byte[1024]);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        
        Thread t2 = new Thread("Thread 2")  {
            public void run()
            {
                for (int i = 0; i < 1000000; i++) {
                    try {
                        ras.seek(0);
                        ras.write(new byte[1024]);
                        ras.getChannel().force(false);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        
        t1.start();
        t2.start();
    }

    void getLock(RandomAccessFile ras) throws Exception
    {
        FileChannel chan = ras.getChannel();
        if (chan.tryLock() == null) {
            throw new Exception("Lock failed on thread " + Thread.currentThread());
        }
    }
}

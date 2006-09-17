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
        RandomAccessFile ras = new RandomAccessFile("test.file", "rw");
        getLock(ras); // Get the initial lock

        ras.seek(0);
        ras.write(new byte[1024]);

        Thread t1 = new Thread(new LockThread(ras), "Thread 1");
        Thread t2 = new Thread(new LockThread(ras), "Thread 2");
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


    private static final class LockThread implements Runnable
    {
        private RandomAccessFile ras;

        LockThread(RandomAccessFile ras)
        {
            this.ras = ras;
        }

        public void run()
        {
            for (int i = 0; i < 1; i++) {
                try {
                    ras.seek(0);
                    ras.read(new byte[1024]);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

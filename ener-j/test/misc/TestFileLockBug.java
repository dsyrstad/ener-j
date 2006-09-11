package misc;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class TestFileLockBug 
{

	public static void main(String[] args) throws Exception
	{
		try {
			for (int i = 0; i < 5; i++) {
				System.out.println("Iteration " + i);
				RandomAccessFile ras = new RandomAccessFile("test.file", "rw");
				FileChannel chan = ras.getChannel();
				if (chan.tryLock() == null) {
					throw new Exception("Lock failed");
				}

				ByteBuffer hdr = ByteBuffer.allocate(73);
				hdr.putLong(1);
                hdr.putInt(2);
                hdr.putLong(3);
                hdr.putLong(4);
                hdr.putLong(5);
                hdr.putLong(6);
                hdr.putInt(7);
                hdr.putLong(8);
                hdr.put((byte)1);
                hdr.putLong(1);
                hdr.putLong(1);                
                hdr.flip();
                
                chan.write(hdr, 0);
				
				chan.force(true);

				switch (i) {
				case 0:
					break;
				case 1:
					ras.seek(1024);
					ras.writeLong(5);
					break;
				default:
					ras.seek(1024);
					ras.readLong();
					break;
				}
				
				ras.close();
			}
		}
		finally {
			new File("test.file").delete();
		}
	}
}

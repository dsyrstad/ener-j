package misc;

import java.nio.ByteBuffer;

public class ByteBufferBench
{
    private static byte[] copyBytes = new byte[100];
    private static int cnt = 5000000;

    public static void main(String[] args)
    {
        // Warm up
        testByteBuffer();
        testByteArray();
        
        testByteBuffer();
        testByteArray();
        
        testByteBuffer();
        testByteArray();
        
        testByteBuffer();
        testByteArray();
        
    }
    
    private static void testByteBuffer()
    {
        long start = System.currentTimeMillis();
        ByteBuffer byteBuf =  ByteBuffer.allocate(8192);
        for (int i = 0; i < cnt; i++) {
            byteBuf.position(0);
            byteBuf.putLong(0, 1L);
            byteBuf.putLong(8, 2L);
            byteBuf.put(copyBytes);
        }
        
        long end  = System.currentTimeMillis();
        System.out.println("ByteBuffer  = " + (end-start) + "ms");
    }
    
    private static void testByteArray()
    {
        long start = System.currentTimeMillis();
        byte[] byteBuf = new byte[8192];
        for (int i = 0; i < cnt; i++) {
            writeLong(byteBuf, 0, 1L);
            writeLong(byteBuf, 8, 1L);
            System.arraycopy(copyBytes, 0, byteBuf, 16, copyBytes.length);
        }
        
        long end  = System.currentTimeMillis();
        System.out.println("Byte array = " + (end-start) + "ms");
    }

    private static void writeLong(byte[] buf, int idx, long v)
    {
        buf[idx] = (byte)(v >>> 56);
        buf[idx + 1] = (byte)(v >>> 48);
        buf[idx + 2] = (byte)(v >>> 40);
        buf[idx + 3] = (byte)(v >>> 32);
        buf[idx + 4] = (byte)(v >>> 24);
        buf[idx + 5] = (byte)(v >>> 16);
        buf[idx + 6] = (byte)(v >>>  8);
        buf[idx + 7] = (byte)(v >>>  0);
        
    }
}

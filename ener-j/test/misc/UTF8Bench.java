package misc;

import java.io.IOException;

import org.enerj.util.ByteArrayUtil;

public class UTF8Bench
{
    private static String test = "A test string with \u0add unicode characters in it \u0bff cool.";
    private static byte[] testBytes = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'h', 'j', 'k', 'l', 'm' };
    private static int cnt = 500000;

    public static void main(String[] args) throws Exception
    {
        // Warm up
        testStringUTF8();
        testStaticUTF8();
        
        testStringUTF8();
        testStaticUTF8();

        testStringUTF8Bytes();
        testStaticUTF8Bytes();
        
        testStringUTF8Bytes();
        testStaticUTF8Bytes();
    }
    
    private static void testStringUTF8() throws Exception
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            byte[] bytes = test.getBytes("UTF8"); // To 
            String str = new String(bytes, "UTF8"); // And back
        }
        
        long end  = System.currentTimeMillis();
        System.out.println("testStringUTF8  = " + (end-start) + "ms");
    }
    
    private static void testStaticUTF8() throws IOException
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            byte[] bytes = new byte[test.length() * 3];
            int len = ByteArrayUtil.putModifiedUTF8(bytes, 0, test); // To
            String str = ByteArrayUtil.getModifiedUTF8(bytes, 0, len); // And back.
        }
        
        long end  = System.currentTimeMillis();
        System.out.println("testStaticUTF8 = " + (end-start) + "ms\n");
    }

    private static void testStringUTF8Bytes() throws Exception
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            String str = new String(testBytes, "UTF8"); 
        }
        
        long end  = System.currentTimeMillis();
        System.out.println("testStringUTF8  = " + (end-start) + "ms");
    }
    
    private static void testStaticUTF8Bytes() throws IOException
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < cnt; i++) {
            String str = ByteArrayUtil.getModifiedUTF8(testBytes, 0, testBytes.length); 
        }
        
        long end  = System.currentTimeMillis();
        System.out.println("testStaticUTF8 = " + (end-start) + "ms\n");
    }

}

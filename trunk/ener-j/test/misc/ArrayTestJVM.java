package misc;


public class ArrayTestJVM 
{
    public static void main(String[] args) throws Exception
    {
        int[] intArray = new int[10];
        short[] shortArray = new short[10];
        Integer[] intObjArray = new Integer[10];
        
        System.out.println("intArray class=" + intArray.getClass());
        System.out.println("shortArray class=" + shortArray.getClass());
        System.out.println("intObjArray class=" + intObjArray.getClass() + " component =" + intObjArray.getClass().getComponentType());
        System.out.println("intArray class equals = " + intArray.getClass().equals( Class.forName("[I") ));
        System.out.println("intArray component type=" + intArray.getClass().getComponentType());
        
        int[][] int2DArray = new int[10][];
        System.out.println("int2DArray class=" + int2DArray.getClass() + " len=" + int2DArray.length);
        
        for (int i = 0; i < int2DArray.length; i++) {
            int2DArray[i] = new int[5];
        }

        System.out.println("int2DArray[3] class=" + int2DArray[3].getClass() + " len=" + int2DArray[3].length);

        int[][] int2DArray2 = new int[10][6];
        System.out.println("int2DArray2 class=" + int2DArray2.getClass() + " len=" + int2DArray2.length);
        System.out.println("int2DArray2[3] class=" + int2DArray2[3].getClass() + " len=" + int2DArray2[3].length);
        
        int[][][][] int4DArray = new int[2][3][4][5];
        System.out.println("int4DArray class=" + int4DArray.getClass() + " len=" + int4DArray.length);
        System.out.println("int4DArray[1] class=" + int4DArray[1].getClass() + " len=" + int4DArray[1].length);
        System.out.println("int4DArray component type=" + int4DArray.getClass().getComponentType());
        
        Object int4DArrayObj = java.lang.reflect.Array.newInstance(Class.forName("[[[I"), 10);
        System.out.println("int4DArrayObj class=" + int4DArrayObj.getClass() + " len=" + ((Object[])int4DArrayObj).length);
        
        int[] a1 = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        int[] a2 = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        int[] a3 = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 7 };
        
        System.out.println("a1==a2?" + a1.equals(a2));
        System.out.println("a1==a3?" + a1.equals(a3));
        System.out.println("a2==a1?" + a2.equals(a1));
        System.out.println("a2==a3?" + a2.equals(a3));
        System.out.println("a1==a1?" + a1.equals(a1));
    }
}

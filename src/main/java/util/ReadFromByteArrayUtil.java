package util;

import entry.FileContent;

public class ReadFromByteArrayUtil {
    private static int INT_OFFSET = 4;

    private static int LONG_OFFSET = 8;

    public static int getIntFromBytes(FileContent content){
        int result = getIntFromBytes(content.getArray(), content.getIndex());
        content.increase(INT_OFFSET);
        return result;
    }

    public static int getIntFromBytes(byte[] array,int index){
        return (array[index] & 0xff) << 24 | (array[index + 1] & 0xff) << 16 | (array[index + 2] & 0xff) << 8 | array[index + 3] & 0xff;
    }

    public static String getStringFromBytes(FileContent content, int length){
        String result = getStringFromBytes(content.getArray(), content.getIndex(), length);
        content.increase(length);
        return result;
    }
    public static String getStringFromBytes(byte[] array,int offset, int length){
        byte[] copy = new byte[length];
        System.arraycopy(array, offset, copy, 0, length);
        return new String(copy);
    }

    public static long getLongFromBytes(FileContent content){
        long result = getLongFromBytes(content.getArray(), content.getIndex());
        content.increase(LONG_OFFSET);
        return result;
    }

    public static long getLongFromBytes(byte[] array, int index){
        long value = 0;
        for (int count = 0; count < 8; count++){
            int shift = (7 - count) << 3;
            value |=((long)0xff<< shift) & ((long)array[index+count] << shift);
        }
        return value;
    }
}

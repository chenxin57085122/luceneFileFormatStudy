package entry;

import java.io.DataInputStream;
import java.io.IOException;
import util.ReadFromByteArrayUtil;

public class FileContent {
    private byte[] array;
    private int index = 0;

    private static int INT_OFFSET = 4;
    private static int LONG_OFFSET = 8;

    public FileContent(byte[] array, int index) {
        this.array = array;
        this.index = index;
    }
    public FileContent(DataInputStream inputStream) throws IOException {
        this.array = new byte[1024];
        inputStream.read(this.array);
    }

    public byte[] getArray() {
        return array;
    }

    public void setArray(byte[] array) {
        this.array = array;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void increase(){
        increase(1);
    }
    public void increase(int length){
        checkLength(length);
        index += length;
    }

    public byte[] clone(int length){
        byte[] result = new byte[length];
        System.arraycopy(array, index, result, 0, length);
        increase(length);
        return result;
    }
    public int getIntFromBytes(){
        int result = ReadFromByteArrayUtil.getIntFromBytes(array, index);
        increase(INT_OFFSET);
        return result;
    }

    public long getLongFromBytes(){
        long result = ReadFromByteArrayUtil.getLongFromBytes(array, index);
        increase(LONG_OFFSET);
        return result;
    }

    public String getStringFromBytes(){
        int length = getVIntFromBytes();
        return getStringFromBytes(length);
    }
    //TODO: VInt maybe 1~4 byte, only read one byte can cause 
    public int getVIntFromBytes(){
        return getByteFromBytes();
    }

    public int getByteFromBytes(){
        return array[index++];
    }

    public String getStringFromBytes(int length){
        String result = ReadFromByteArrayUtil.getStringFromBytes(array, index, length);
        increase(length);
        return result;
    }

    public void checkLength(int length){
        if (length < 0){
            throw new IllegalArgumentException("argument length must > 0");
        }
    }

    public void StringMapInfoRead(){
        int mapCount = getVIntFromBytes();
        int keySize = 0;
        int valueSize = 0;
        String key = "";
        String value = "";
        for (int i = 0; i < mapCount; i ++){
            keySize = getVIntFromBytes();
            key = getStringFromBytes(keySize);
            valueSize = getVIntFromBytes();
            value = getStringFromBytes(valueSize);
            System.out.println(key + "=" + value);
        }
    }
}

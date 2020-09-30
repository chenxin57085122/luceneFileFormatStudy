package resolve;

import settings.Setting;
import util.ReadFromByteArrayUtil;
import entry.FileContent;
import java.io.*;

public class FieldInfoResolve {
    private static DataInputStream inputStream;
    public static void main(String[] args) throws IOException {
        String file = Setting.PATH + File.separator + "_0.fnm";
        resolve(file);

    }
    public static void resolve(String file) throws IOException {
        inputStream = new DataInputStream(new FileInputStream(file));
        FileContent content = new FileContent(inputStream);
        readIndexHeader(content);
        readFieldInfo(content);
        System.out.println("Footer codec_magic: 0x" + Integer.toHexString(content.getIntFromBytes()));
    }
    public static void readIndexHeader(FileContent content){
        System.out.println("---------indexHeader read start...---------");
        System.out.println("start codec_magic: 0x" + Integer.toHexString(ReadFromByteArrayUtil.getIntFromBytes(content)));
        System.out.println(content.getStringFromBytes());
        System.out.println("version: " + content.getIntFromBytes());
        byte[] segmentID = content.clone(16);
        int suffixLength = content.getVIntFromBytes();
        if (suffixLength > 0){
            content.getStringFromBytes(suffixLength);
        }
        System.out.println(content.getIndex());
        System.out.println("---------indexHeader read end...---------");
    }

    public static void readFieldInfo(FileContent content){
        System.out.println("-----------------------------------------");
        System.out.println("---------FieldInfo read start...---------");
        int fieldsCount = content.getVIntFromBytes();
        System.out.println("fieldsCount: " + fieldsCount);
        for (int n = 0; n < fieldsCount; n ++){
            String fieldName = content.getStringFromBytes();
            System.out.println("fieldName: "+fieldName);
            System.out.println("FieldNumber: "+content.getVIntFromBytes());
            System.out.println("fieldBits: "+content.getByteFromBytes());
            System.out.println("IndexOptions: "+content.getByteFromBytes());
            System.out.println("docValuesByte: " + content.getByteFromBytes());
            System.out.println("DocValuesGen: " + content.getLongFromBytes());
            content.StringMapInfoRead();
            int dimensionCount = content.getVIntFromBytes();
            System.out.println("dimensionCount: " + dimensionCount);
            if (dimensionCount != 0){
                System.out.println("pointNumber: " + content.getVIntFromBytes());
            }
        }
        System.out.println("---------FieldInfo read end...---------");
    }
}

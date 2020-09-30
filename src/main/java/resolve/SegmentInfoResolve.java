package resolve;

import entry.FileContent;
import settings.Setting;
import util.ReadFromByteArrayUtil;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static settings.Setting.CODEC_MAGIC;
import static settings.Setting.FOOTER_MAGIC;

/**
 * Lucene 6.2 Segment info format.
 * Files:
 *
 * .si: Header, SegVersion, SegSize, IsCompoundFile, Diagnostics, Files, Attributes, IndexSort, Footer
 * Data types:
 *      Header --> IndexHeader
 *      SegSize --> Int32
 *      SegVersion --> String
 *      Files --> Set<String>
 *      Diagnostics,Attributes --> Map<String,String>
 *      IsCompoundFile --> Int8
 *      IndexSort --> Int32 count, followed by count SortField
 *      SortField --> String field name, followed by Int32 sort type ID, followed by Int8 indicatating reversed sort, followed by a type-specific encoding of the optional missing value
 *      Footer --> CodecFooter
 * Field Descriptions:
 *      SegVersion is the code version that created the segment.
 *      SegSize is the number of documents contained in the segment index.
 *      IsCompoundFile records whether the segment is written as a compound file or not. If this is -1, the segment is not a compound file. If it is 1, the segment is a compound file.
 *      The Diagnostics Map is privately written by IndexWriter, as a debugging aid, for each segment it creates. It includes metadata like the current Lucene version, OS, Java version, why the segment was created (merge, flush, addIndexes), etc.
 *      Files is a list of files referred to by this segment.
 *
 */
public class SegmentInfoResolve {
    public static String fileName = "_0.si";
    public static DataInputStream inputStream = null;

    public static void main(String[] args) throws IOException {
        System.out.println("codec Header : 0x" + Integer.toHexString(CODEC_MAGIC));
        System.out.println("codec footer : 0x" + Integer.toHexString(FOOTER_MAGIC));
        byteRead1();
    }

    public static void byteRead1() throws IOException{
        System.out.println("...start si file");
        inputStream = new DataInputStream(new FileInputStream(Setting.PATH+ File.separator+fileName));
        FileContent content = new FileContent(inputStream);

        System.out.println("0x" +Integer.toHexString(ReadFromByteArrayUtil.getIntFromBytes(content)));
        int size = content.getArray()[content.getIndex()];
        content.increase();
        System.out.println(size);
        System.out.println(ReadFromByteArrayUtil.getStringFromBytes(content, size));
        System.out.println("version: " + ReadFromByteArrayUtil.getIntFromBytes(content));
        byte[] segInfoId = new byte[16];
        System.arraycopy(content.getArray(), content.getIndex(), segInfoId, 0, 16);
        content.setIndex(content.getIndex()+16);
        System.out.println("suffix length: "+content.getArray()[content.getIndex()]);
        content.setIndex(content.getIndex()+1);
        int major = ReadFromByteArrayUtil.getIntFromBytes(content);
        int minor = ReadFromByteArrayUtil.getIntFromBytes(content);
        int bugfix = ReadFromByteArrayUtil.getIntFromBytes(content);
        System.out.println("luceneVersion: " + major+"."+minor+"."+bugfix);
        System.out.println("maxDoc: " + ReadFromByteArrayUtil.getIntFromBytes(content));
        System.out.println("UseCompoundFile: " + content.getArray()[content.getIndex()]);
        content.setIndex(content.getIndex()+1);
        content.setIndex(StringMapInfoRead(content.getArray(), content.getIndex()));
        content.setIndex(fileSetNameRead(content.getArray(), content.getIndex()));
        content.setIndex(StringMapInfoRead(content.getArray(), content.getIndex()));
        size = content.getArray()[content.getIndex()];
        System.out.println(size);
        content.increase();
        System.out.println("0x"+Integer.toHexString(ReadFromByteArrayUtil.getIntFromBytes(content)));
        System.out.println(ReadFromByteArrayUtil.getIntFromBytes(content));
        System.out.println("0x" + Long.toHexString(ReadFromByteArrayUtil.getLongFromBytes(content)));
        System.out.println(content.getIndex());
    }

    public static void byteRead() throws IOException{
        int index = 0;
        System.out.println("...start si file");
        inputStream = new DataInputStream(new FileInputStream(Setting.PATH+ File.separator+fileName));
        byte []array = new byte [512];
        inputStream.read(array);
        System.out.println("0x" +Integer.toHexString(ReadFromByteArrayUtil.getIntFromBytes(array,index)));
        index += 4;
        int size = array[index++];
        System.out.println(size);
        System.out.println(ReadFromByteArrayUtil.getStringFromBytes(array,index,size));
        index += size;
        System.out.println("version: " + ReadFromByteArrayUtil.getIntFromBytes(array,index));
        index +=4;
        byte[] segInfoId = new byte[16];
        System.arraycopy(array, index, segInfoId, 0, 16);
        index += 16;
        System.out.println("suffix length: "+array[index++]);
        int major = ReadFromByteArrayUtil.getIntFromBytes(array, index);
        index += 4;
        int minor = ReadFromByteArrayUtil.getIntFromBytes(array, index);
        index += 4;
        int bugfix = ReadFromByteArrayUtil.getIntFromBytes(array, index);
        index += 4;
        System.out.println("luceneVersion: " + major+"."+minor+"."+bugfix);
        System.out.println("maxDoc: " + ReadFromByteArrayUtil.getIntFromBytes(array,index));
        index +=4;
        System.out.println("UseCompoundFile: " + array[index++]);
        index = StringMapInfoRead(array, index);
        index = fileSetNameRead(array, index);
        index = StringMapInfoRead(array, index);
        size = array[index++];
        System.out.println(size);
        index += size;
        System.out.println("0x"+Integer.toHexString(ReadFromByteArrayUtil.getIntFromBytes(array,index)));
        index += 4;
        System.out.println(ReadFromByteArrayUtil.getIntFromBytes(array,index));
        index += 4;
        System.out.println("0x" + Long.toHexString(ReadFromByteArrayUtil.getLongFromBytes(array, index)));
        index += 8;
        System.out.println(index);
    }

    public static int StringMapInfoRead(byte[] array,int index){

        int mapCount = array[index++];
        int keySize = 0;
        int valueSize = 0;
        for (int i = 0; i < mapCount; i ++){
            keySize = array[index++];
            System.out.println(ReadFromByteArrayUtil.getStringFromBytes(array, index, keySize));
            index += keySize;
            valueSize = array[index++];
            System.out.println(ReadFromByteArrayUtil.getStringFromBytes(array, index, valueSize));
            index += valueSize;
        }
        return index;
    }

    public static int fileSetNameRead(byte[] array, int index){
        int fileCount = array[index++];
        int size = 0;
        for (int i = 0; i < fileCount; i ++){
            size = array[index++];
            System.out.println(ReadFromByteArrayUtil.getStringFromBytes(array, index, size));
            index += size;
        }

        return index;
    }

}

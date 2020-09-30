package resolve;

import settings.Setting;
import util.ReadFromByteArrayUtil;
import java.io.*;
import static settings.Setting.CODEC_MAGIC;
import static settings.Setting.FOOTER_MAGIC;

/**
 * Files:
 *
 *      segments_N: Header, LuceneVersion, Version, NameCounter, SegCount, MinSegmentLuceneVersion, <SegName, HasSegID, SegID, SegCodec, DelGen, DeletionCount, FieldInfosGen, DocValuesGen, UpdatesFiles>SegCount, CommitUserData, Footer
 * Data types:
 *      Header --> IndexHeader
 *      LuceneVersion --> Which Lucene code Version was used for this commit, written as three vInt: major, minor, bugfix
 *      MinSegmentLuceneVersion --> Lucene code Version of the oldest segment, written as three vInt: major, minor, bugfix; this is only written only if there's at least one segment
 *      NameCounter, SegCount, DeletionCount --> Int32
 *      Generation, Version, DelGen, Checksum, FieldInfosGen, DocValuesGen --> Int64
 *      HasSegID --> Int8
 *      SegID --> Int8ID_LENGTH
 *      SegName, SegCodec --> String
 *      CommitUserData --> Map<String,String>
 *      UpdatesFiles --> Map<Int32, Set<String>>
 *      Footer --> CodecFooter
 * Field Descriptions:
 *      Version counts how often the index has been changed by adding or deleting documents.
 *      NameCounter is used to generate names for new segment files.
 *      SegName is the name of the segment, and is used as the file name prefix for all of the files that compose the segment's index.
 *      DelGen is the generation count of the deletes file. If this is -1, there are no deletes. Anything above zero means there are deletes stored by LiveDocsFormat.
 *      DeletionCount records the number of deleted documents in this segment.
 *      SegCodec is the name of the Codec that encoded this segment.
 *      HasSegID is nonzero if the segment has an identifier. Otherwise, when it is 0 the identifier is null and no SegID is written. Null only happens for Lucene 4.x segments referenced in commits.
 *      SegID is the identifier of the Codec that encoded this segment.
 *      CommitUserData stores an optional user-supplied opaque Map<String,String> that was passed to IndexWriter.setLiveCommitData(Iterable).
 *      FieldInfosGen is the generation count of the fieldInfos file. If this is -1, there are no updates to the fieldInfos in that segment. Anything above zero means there are updates to fieldInfos stored by FieldInfosFormat .
 *      DocValuesGen is the generation count of the updatable DocValues. If this is -1, there are no updates to DocValues in that segment. Anything above zero means there are updates to DocValues stored by DocValuesFormat.
 *      UpdatesFiles stores the set of files that were updated in that segment per field.
 */
public class Segment_NResolve {
    public static String fileName = Setting.SEGMENTS + "_" + 1;
    public static DataInputStream inputStream = null;

    public static void main(String[] args) throws IOException {
        System.out.println("codec Header : 0x" + Integer.toHexString(CODEC_MAGIC));
        System.out.println("codec footer : 0x" + Integer.toHexString(FOOTER_MAGIC));
        byteRead();
    }

    public static void byteRead() throws IOException{
        int index = 0;
        System.out.println("...start N file");
        inputStream = new DataInputStream(new FileInputStream(Setting.PATH+ File.separator+fileName));
        byte []array = new byte [256];
        inputStream.read(array);
        System.out.println("0x" +Integer.toHexString(ReadFromByteArrayUtil.getIntFromBytes(array,index)));
        index += 4;
        int size = array[index++];
        System.out.println(ReadFromByteArrayUtil.getStringFromBytes(array,index,size));
        index += size;
        System.out.println("version: " + ReadFromByteArrayUtil.getIntFromBytes(array,index));
        index +=4;
        byte[] segInfoId = new byte[16];
        System.arraycopy(array, index, segInfoId, 0, 16);
        index += 16;
        System.out.println("suffix length: "+array[index++]);
        System.out.println("suffix content: "+array[index++]);
        int major = array[index++];
        int minor = array[index++];
        int bugfix = array[index++];
        System.out.println("luceneVersion: " + major+"."+minor+"."+bugfix);
        System.out.println("Version: " + ReadFromByteArrayUtil.getLongFromBytes(array,index));
        index += 8;
        System.out.println("NameCounter: "+ReadFromByteArrayUtil.getIntFromBytes(array,index));
        index += 4;
        System.out.println("SegCount: "+ReadFromByteArrayUtil.getIntFromBytes(array,index));
        index += 4;
        major = array[index++];
        minor = array[index++];
        bugfix = array[index++];
        System.out.println("MinSegmentLuceneVersion: " + major + "." + minor + "." + bugfix);
        size = array[index++];
        System.out.println("SegNamePrefix: "+ReadFromByteArrayUtil.getStringFromBytes(array, index, size));
        index += size;

        //loop by SegCount
        System.out.println("Segment info...");
        System.out.println("HasSegID: "+Integer.toHexString(array[index++]));
        segInfoId = new byte[16];
        System.arraycopy(array, index, segInfoId, 0, 16);
        index += 16;
        size = array[index++];
        System.out.println("SegCodec: "+ReadFromByteArrayUtil.getStringFromBytes(array, index, size));
        index += size;
        System.out.println("DelGen: " + ReadFromByteArrayUtil.getLongFromBytes(array, index));
        index += 8;
        System.out.println("DeletionCount: " + ReadFromByteArrayUtil.getIntFromBytes(array, index));
        index += 4;
        System.out.println("FieldInfosGen: " + ReadFromByteArrayUtil.getLongFromBytes(array, index));
        index += 8;
        System.out.println("DocValuesGen: " + ReadFromByteArrayUtil.getLongFromBytes(array, index));
        index += 8;
        System.out.println("UpdatesFiles(Map<Int32,String>): ");
        size = array[index++];
        System.out.println(size);
        // TODO the 5 byte is mean ?
        index += 5;
        System.out.println("Footer: 0x"+Integer.toHexString(ReadFromByteArrayUtil.getIntFromBytes(array, index)));
        index += 4;
        System.out.println("algorithmID: " + ReadFromByteArrayUtil.getIntFromBytes(array, index));
        index += 4;
        System.out.println("0x" + Long.toHexString(ReadFromByteArrayUtil.getLongFromBytes(array, index)));
        index += 8;
        System.out.println(index);
    }


}

package filedb;

import java.io.IOException;
import java.io.RandomAccessFile;

public class IO {

    public static FileBlock readBlockAt(RandomAccessFile reader, long offSet)
            throws IOException {
        reader.seek(offSet);
        long prevBlock = reader.readLong();
        long headerPoint = reader.readLong();
        long headerSize = reader.readLong();
        long dataPoint = reader.readLong();
        long dataSize = reader.readLong();
        FileBlock block = new FileBlock(prevBlock, headerPoint,
                headerSize, dataPoint, dataSize);
        return block;
    }

    public static String readHeader(RandomAccessFile file, FileBlock block) throws Exception {
        return new String(readHeaderBytes(file, block));
    }

    public static byte[] readHeaderBytes(RandomAccessFile file, FileBlock block) throws Exception {
        long hp = block.getHeaderPoint();
        long hs = block.getHeaderSize();
        byte[] data = new byte[(int) hs];
        file.seek(hp);
        file.readFully(data);
        return data;
    }

    public static String readBody(RandomAccessFile file, FileBlock block) throws Exception {
        return new String(readBodyBytes(file, block));
    }

    public static byte[] readBodyBytes(RandomAccessFile file, FileBlock block) throws Exception {
        long dp = block.getDataPoint();
        long ds = block.getDataSize();
        byte[] data = new byte[(int) ds];
        file.seek(dp);
        file.readFully(data);
        return data;
    }
}

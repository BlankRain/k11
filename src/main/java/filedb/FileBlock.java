package filedb;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileBlock {

    private long prevBlock;
    private long headerPoint;
    private long headerSize;
    private long dataPoint;
    private long dataSize;
    public static long FILE_BLOCK_DEFINE_SIZE = 8 + 8 + 8 + 8 + 8;

    public FileBlock() {
    }

    public FileBlock(
            long prevBlock,
            long headerPoint,
            long headerSize,
            long dataPoint,
            long dataSize) {
        this.prevBlock = prevBlock;
        this.headerPoint = headerPoint;
        this.headerSize = headerSize;
        this.dataPoint = dataPoint;
        this.dataSize = dataSize;
    }

    public long getPrevBlock() {
        return prevBlock;
    }

    public void setPrevBlock(long xprevBlock) {
        this.prevBlock = xprevBlock;
    }

    public long getHeaderPoint() {
        return headerPoint;
    }

    public void setHeaderPoint(long xheaderPoint) {
        this.headerPoint = xheaderPoint;
    }

    public long getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(long xheaderSize) {
        this.headerSize = xheaderSize;
    }

    public long getDataPoint() {
        return dataPoint;
    }

    public void setDataPoint(long xdataPoint) {
        this.dataPoint = xdataPoint;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long xdataSize) {
        this.dataSize = xdataSize;
    }

    public void writeTo(RandomAccessFile writer, long offSet) {
        try {
            writer.seek(offSet);
            writer.writeLong(this.prevBlock);
            writer.writeLong(this.headerPoint);
            writer.writeLong(this.headerSize);
            writer.writeLong(this.dataPoint);
            writer.writeLong(this.dataSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long nextBlockPoint() {
        return dataPoint + dataSize;
    }

    public void appendTo(RandomAccessFile writer) throws Exception {
        writer.writeLong(this.prevBlock);
        writer.writeLong(this.headerPoint);
        writer.writeLong(this.headerSize);
        writer.writeLong(this.dataPoint);
        writer.writeLong(this.dataSize);
    }
}

package filedb;

import java.io.RandomAccessFile;

public interface IterateCall {
    void call(RandomAccessFile file, FileBlock currentBlock) throws Exception;
}

package filedb;

import java.io.RandomAccessFile;
import java.util.Iterator;

public class FileList extends FileStack implements DataContainer {
    /**
     * 读指针. 方便顺序遍历访问.
     * for(int i=0;i<fs.size();i++){
     * 与链表效果一样.
     * }
     */
    private int currentIndex;
    private long currentIndexPos;

    public FileList(RandomAccessFile file, String fileName) {
        super(file, fileName);
    }

    @Override
    public void ready() throws Exception {
        super.ready();
        currentIndex = getDepth();
        currentIndexPos = super.currentBlockPos;
    }

    public int size() {
        return super.getDepth();
    }

    public boolean isEmpty() {
        return super.getDepth() == 0;
    }

    public byte[][] get(int i) throws Exception {
        if (getDepth() == 0) {
            return new byte[][] {null, null};
        }
        if (i > getDepth() - 1) {
            throw new ArrayIndexOutOfBoundsException();
        }
        long tamp = guard.writeLock();
        try {
            long start = file.getFilePointer();
            FileBlock block = IO.readBlockAt(file, currentIndexPos);
            if (currentIndex > i) {
                for (; currentIndex > i; currentIndex--) {
                    currentIndexPos = block.getPrevBlock();
                    block = IO.readBlockAt(file, currentIndexPos);
                }
            } else if (currentIndex < i) {
                for (; currentIndex < i; currentIndex++) {
                    currentIndexPos = block.nextBlockPoint();
                    block = IO.readBlockAt(file, currentIndexPos);
                }
            }
            byte[] head = IO.readHeaderBytes(file, block);
            byte[] body = IO.readBodyBytes(file, block);
            // reset to start.
            file.seek(start);
            return new byte[][] {head, body};
        } finally {
            guard.unlockWrite(tamp);
        }
    }

    public int add(String key, String value) {
        return add(key.getBytes(), value.getBytes());
    }

    public int add(byte[] header, byte[] body) {
        return super.push(header, body);
    }

    public byte[][] head() throws Exception {
        return readHeadAndBody(0);
    }

    public byte[][] tail() throws Exception {
        return readHeadAndBody(super.currentBlockPos);
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public Object next() {
                try {
                    byte[][] obj = get(index);
                    index++;
                    return obj;
                } catch (Exception e) {
                    index--;
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            try {
                byte[][] ele = get(i);
                sb.append(new String(ele[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

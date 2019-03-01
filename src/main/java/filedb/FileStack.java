package filedb;

import java.io.*;
import java.time.LocalDateTime;
import java.util.function.Function;

public class FileStack implements Closeable, AutoCloseable {

    protected int depth;

    protected FileBlock currentFileBlock;

    protected long currentBlockPos;

    protected FeakLock guard = new FeakLock();

    protected RandomAccessFile file;

    private String fileName;

    protected LocalDateTime lastAccess;

    public FileStack(RandomAccessFile file, String fileName) {
        this.file = file;
        this.fileName = fileName;
    }

    public int push(byte[] header, byte[] data) {
        long stamp = guard.writeLock();
        try {
            lastAccess = LocalDateTime.now();
            file = getFile();
            file.seek(currentFileBlock.nextBlockPoint());
            long currentOffset = file.getFilePointer();
            long bodyOffset = currentOffset
                    + FileBlock.FILE_BLOCK_DEFINE_SIZE;

            FileBlock block = new FileBlock(
                    currentBlockPos,
                    bodyOffset,
                    header.length,
                    bodyOffset + header.length,
                    data.length
            );
            try {

                block.appendTo(file);
                file.write(header);
                file.write(data);
                depth++;
                currentBlockPos = currentOffset;
                currentFileBlock = block;
                return depth;
            } catch (IOException e) {
                file.seek(currentOffset);
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            guard.unlockWrite(stamp);
        }
        return -1;
    }

    public byte[][] pop() {
        if (depth == 0) {
            return new byte[][] {null, null};
        }
        long stamp = guard.writeLock();
        try {
            lastAccess = LocalDateTime.now();
            file = getFile();
            byte[] data = new byte[(int) currentFileBlock.getDataSize()];
            byte[] head = new byte[(int) currentFileBlock.getHeaderSize()];
            //read header
            file.seek(currentFileBlock.getHeaderPoint());
            file.readFully(head);
            // read data
            file.seek(currentFileBlock.getDataPoint());
            file.readFully(data);
            FileBlock newBlock = null;
            if (currentBlockPos != 0) {
                newBlock = IO.readBlockAt(file, currentFileBlock.getPrevBlock());
            }
            //remove tail
            file.setLength(currentBlockPos);

            depth--;
            currentBlockPos = currentFileBlock.getPrevBlock();
            currentFileBlock = newBlock;

            return new byte[][] {head, data};

        } catch (Exception e) {
            return new byte[][] {null, null};
        } finally {
            guard.unlockWrite(stamp);
        }
    }

    public byte[][] peak() {
        if (depth == 0) {
            return new byte[][] {null, null};
        }
        long stamp = guard.writeLock();
        try {
            lastAccess = LocalDateTime.now();
            file = getFile();
            byte[] data = new byte[(int) currentFileBlock.getDataSize()];
            byte[] head = new byte[(int) currentFileBlock.getHeaderSize()];
            //read header
            file.seek(currentFileBlock.getHeaderPoint());
            file.readFully(head);
            // read data
            file.seek(currentFileBlock.getDataPoint());
            file.readFully(data);
            return new byte[][] {head, data};
        } catch (Exception e) {
            return new byte[][] {null, null};
        } finally {
            guard.unlockWrite(stamp);
        }
    }

    public byte[] peakHeader() {
        if (depth == 0) {
            return null;
        }
        long stamp = guard.writeLock();
        try {
            lastAccess = LocalDateTime.now();
            file = getFile();
            byte[] head = new byte[(int) currentFileBlock.getHeaderSize()];
            //read header
            file.seek(currentFileBlock.getHeaderPoint());
            file.readFully(head);
            return head;
        } catch (Exception e) {
            return null;
        } finally {
            guard.unlockWrite(stamp);
        }
    }

    public int getDepth() {
        return depth;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }


    private RandomAccessFile getFile() throws Exception {
        if (file == null) {
            file = new RandomAccessFile(new File(fileName), "rw");
        }
        return file;
    }

    @Override
    public void close() throws IOException {
        long stamp = guard.writeLock();
        try {
            if (file != null) {
                file.close();
                file = null;
            }
        } finally {
            guard.unlockWrite(stamp);
        }
    }

    public void iterateForward(IterateCall call) throws Exception {
        long stamp = guard.writeLock();
        try {
            lastAccess = LocalDateTime.now();
            file = getFile();
            long fileSize = file.length();
            FileBlock currentBlock = new FileBlock();
            long currentBlockOffset = 0;
            int dep = 0;
            while (currentBlock.nextBlockPoint() < fileSize) {
                long newPos = currentBlock.nextBlockPoint();
                if (newPos > fileSize) {
                    System.err.println("Bad reference to next block at" + currentBlockOffset + "!trunc!");
                    file.setLength(currentBlockOffset);
                    break;
                }
                try {
                    FileBlock block = IO.readBlockAt(file, newPos);

                    if (block.getPrevBlock() != currentBlockOffset) {
                        System.err.println("Bad back reference" + block.getPrevBlock() + "!=" + currentBlockOffset + "!upd!");
                        block.setPrevBlock(currentBlockOffset);
                        block.writeTo(file, newPos);
                    }

                    currentBlockOffset = newPos;
                    currentBlock = block;
                    // body
                    if (call != null) {
                        call.call(file, currentBlock);
                    }
                    dep++;
                } catch (IOException e) {
                    if (e instanceof EOFException) {
                        System.err.println("Broken meta info at" + newPos + "!trunc!");
                        file.setLength(currentBlockOffset);
                        break;
                    } else {
                        System.err.println("Can't read block!");
                        throw e;
                    }
                }

            }

            depth = dep;
            this.currentFileBlock = currentBlock;
            this.currentBlockPos = currentBlockOffset;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            guard.unlockWrite(stamp);
        }
    }

    public void iterateBackward(IterateCall call) throws Exception {
        long stamp = guard.writeLock();
        try {
            if (depth == 0) {
                return;
            }
            file = getFile();
            long len = file.length();

            FileBlock currentBlock = currentFileBlock;

            long currentBlockOffset = currentBlockPos;
            long dep = depth;
            while (true) {
                if (call != null) {
                    call.call(file, currentBlock);
                }
                // ...
                if (currentBlock.getPrevBlock() > currentBlockOffset) {
                    System.err.println(String.format("Danger back-ref link: prev block %v has greater index then current %v", currentBlock.getPrevBlock(), currentBlockOffset));
                }
                dep--;
                if (currentBlock.getPrevBlock() == currentBlockOffset) {
                    break;
                }
                currentBlockOffset = currentBlock.getPrevBlock();
                try {
                    currentBlock = IO.readBlockAt(file, currentBlock.getPrevBlock());
                } catch (Exception e) {
                    throw e;
                }

            }
            if (dep != 0) {
                System.err.println("Broker back path detected at" + dep + "depth index");
            }

            // seek end.
            file.seek(len);
        } finally {
            guard.unlockWrite(stamp);
        }

    }

    protected byte[][] readHeadAndBody(long offSet) throws Exception {
        FileBlock block = IO.readBlockAt(file, offSet);
        byte[] head = IO.readHeaderBytes(file, block);
        byte[] body = IO.readBodyBytes(file, block);
        return new byte[][] {head, body};
    }

    public void ready() throws Exception {
        iterateForward(null);
    }

    public static Function<String, IterateCall> Println = (String fmt) -> new IterateCall() {
        @Override
        public void call(RandomAccessFile file, FileBlock currentBlock) throws Exception {
            String head = IO.readHeader(file, currentBlock);
            String body = IO.readBody(file, currentBlock);
            System.out.println(String.format(fmt, head, body));
        }
    };
}

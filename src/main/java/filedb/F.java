package filedb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class F {
    public static FileStack openStack(String fileName) throws Exception {
        try {
            RandomAccessFile file = new RandomAccessFile(
                    new File(fileName), "rw");
            return newStack(file, fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static FileList openLinkedList(String fileName) throws Exception {
        try {
            RandomAccessFile file = new RandomAccessFile(
                    new File(fileName), "rw");
            return newList(file, fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static FileStack newStack(RandomAccessFile file, String fileName) throws Exception {
        FileStack fs = new FileStack(file, fileName);
        fs.ready();
        return fs;
    }

    public static FileStack newStack() throws Exception {
        File f = File.createTempFile("fileBased", "linkedList");
        f.deleteOnExit();
        String fileName = f.getName();
        RandomAccessFile file = new RandomAccessFile(f, "rw");
        return newStack(file, fileName);
    }

    /**
     * @return use temp files.
     * @throws Exception e
     */
    public static FileList newList() throws Exception {
        File f = File.createTempFile("fileBased", "linkedList");
        f.deleteOnExit();
        String fileName = f.getName();
        RandomAccessFile file = new RandomAccessFile(f, "rw");
        return newList(file, fileName);
    }

    public static FileList newList(RandomAccessFile file, String fileName) throws Exception {
        FileList fl = new FileList(file, fileName);
        fl.ready();
        return fl;
    }


    public static void main(String[] args) throws Exception {
        FileList fs = openLinkedList("helloworld.fs");
        fs.iterateForward(FileStack.Println.apply("%s => %s"));
//        fs.iterateBackward(FileStack.Println.apply("%s : %s"));

        System.out.println(fs.size());
        for (int i = 0; i < fs.size(); i++) {
            byte[][] element = fs.get(i);
            System.out.println(new String(element[0]) + "<>" + new String(element[1]));
        }
        byte[][] element = fs.get(fs.size() - 1);
        System.out.println(new String(element[0]) + "<1>" + new String(element[1]));
        element = fs.tail();
        System.out.println(new String(element[0]) + "<tail>" + new String(element[1]));
        element = fs.head();
        System.out.println(new String(element[0]) + "<head>" + new String(element[1]));
    }
}

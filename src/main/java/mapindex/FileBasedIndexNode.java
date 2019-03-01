package mapindex;

import filedb.F;
import filedb.FileList;

public class FileBasedIndexNode<T, R extends ByteAbleObject> implements IndexNode<T, R> {

    T source;

    FileList target;

    R tmp;

    @Override
    public T source() {
        return source;
    }

    @Override
    public void source(T object) {
        source = object;
    }

    @Override
    public Object target() {
        if (target != null) {
            return target;
        }
        return tmp;
    }

    public static final byte[] zero = new byte[0];

    @Override
    public void target(R obj) throws Exception {
        if (target == null && tmp == null) {
            tmp = obj;
            return;
        }
        if (target != null) {
            target.add(zero, obj.bytes());
        }
        if (target == null) {
            target = F.newList();
            target.add(zero, tmp.bytes());
            target.add(zero, obj.bytes());
            tmp = null;
        }
    }

    @Override
    public String toString() {
        return "FileBasedIndexNode{"
                +
                "source=" + source
                +
                ", target=" + target
                +
                ", tmp=" + tmp
                +
                '}';
    }
}

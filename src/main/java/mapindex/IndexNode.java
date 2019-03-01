package mapindex;

import filedb.DataContainer;

import java.util.LinkedList;

public interface IndexNode<T, R> {

    T source();

    void source(T object);

    Object target();

    void target(R obj) throws Exception;

    default public String print() {
        StringBuilder sb = new StringBuilder("\n");
        if (target() instanceof DataContainer) {
            for (Object e : (DataContainer) target()) {
                sb.append(source() + "<>" + e + "\n");
            }
        } else {
            sb.append(source() + "<>" + target() + "\n");
        }
        return sb.toString();
    }
}
